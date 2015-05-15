package nl.tudelft.ewi.sorcerers.servlet;

import static javax.ws.rs.client.Entity.form;
import static javax.ws.rs.core.Response.Status.OK;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Priority;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.inject.Named;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

@Priority(Priorities.AUTHENTICATION)
@PreMatching
@Provider
public class GitHubOAuthFilter implements ContainerRequestFilter {
	private String clientId;
	private String clientSecret;

	@Inject
	public GitHubOAuthFilter(@Named("env:GITHUB_CLIENT_ID") String clientId,
			@Named("env:GITHUB_CLIENT_SECRET") String clientSecret) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	private String getGithubToken(ContainerRequestContext requestContext) {
		Cookie githubTokenCookie = requestContext.getCookies().get("github_token");
		if (githubTokenCookie == null) {
			return null;
		} else {
			return githubTokenCookie.getValue();
		}
	}
	
	private String signReturnAddress(String returnAddress) {
        String digest = null;
        try {
            SecretKeySpec key = new SecretKeySpec(this.clientSecret.getBytes("UTF-8"), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(key);

            byte[] bytes = mac.doFinal(returnAddress.getBytes("UTF-8"));

            StringBuffer hash = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
        	throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
        	throw new RuntimeException(e);
        }
        return String.format("%s:%s", digest, returnAddress);
	}

	private void redirectToLogin(ContainerRequestContext requestContext,
			String returnAddress) {
		UriBuilder uri = UriBuilder
				.fromUri("https://github.com/login/oauth/authorize");
		UriBuilder redirectUri = requestContext.getUriInfo()
				.getBaseUriBuilder().path("oauth/authorize/");
		uri.queryParam("client_id", this.clientId);
		uri.queryParam("redirect_uri", redirectUri);
		uri.queryParam("scope", "repo");
		uri.queryParam("state", signReturnAddress(returnAddress));
		
		requestContext.abortWith(Response.temporaryRedirect(uri.build())
				.build());
	}
	
	private void redirectWithToken(ContainerRequestContext requestContext,
			String returnAddress, String token) {
		String baseUri = requestContext.getUriInfo().getBaseUriBuilder()
				.scheme(null).host(null).port(-1).toString();
		
		System.out.println(baseUri);
		NewCookie tokenCookie = new NewCookie("github_token",
				token, baseUri, null, Cookie.DEFAULT_VERSION, null,
				NewCookie.DEFAULT_MAX_AGE, null, false, true);
		requestContext.abortWith(Response
				.seeOther(URI.create(returnAddress)).cookie(tokenCookie)
				.build());
	}
	
	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		UriInfo uriInfo = requestContext.getUriInfo();
		MultivaluedMap<String, String> queryParameters = uriInfo
				.getQueryParameters();
		String path = uriInfo.getPath();
		
		if ("oauth/login/".equals(path)) {
			String returnAddress = queryParameters.getFirst("return_to");
			if (returnAddress == null) {
				returnAddress = requestContext.getHeaderString("Referer");
			}
			if (returnAddress == null) {
				returnAddress = "https://github.com/";
			}
			redirectToLogin(requestContext, returnAddress);
		} else if ("oauth/authorize/".equals(path)) {
			String state = queryParameters.getFirst("state");
			if (state == null) {
				throw new WebApplicationException("Attempted XSS forgery.", Status.FORBIDDEN);
			}
			String[] stateParts = state.split(":", 2);
			String returnAddress = stateParts[1];
			if (stateParts.length != 2 || !state.equals(signReturnAddress(returnAddress))) {
				throw new WebApplicationException("Attempted XSS forgery.", Status.FORBIDDEN);
			}
			
			String code = queryParameters.getFirst("code");
			if (code == null) {
				// TODO report proper error
				throw new WebApplicationException("Error while logging in to GitHub.", Status.FORBIDDEN);
			} else {
				String token = exchangeForToken(this.clientId, this.clientSecret, code);
				if (token == null) {
					throw new WebApplicationException("Error while logging in to GitHub.", Status.FORBIDDEN);
				} else {
					redirectWithToken(requestContext, returnAddress, token);
				}
			}
		} else if (getGithubToken(requestContext) != null) {
			String token = getGithubToken(requestContext);
			System.out.format("Got github token %s", token);
			AuthorizationPayload verifyToken = verifyToken(token);

			if (verifyToken != null) {
				String username = null;
				if (verifyToken.user != null) {
					username = verifyToken.user.login;
				}
				List<String> scopes = Arrays.asList(new String[0]);
				if (verifyToken.scopes != null) {
					scopes = new ArrayList<String>(verifyToken.scopes);
				}
				boolean isSecure = requestContext.getSecurityContext().isSecure();
				requestContext.setSecurityContext(new GitHubSecurityContext(username, token, scopes, isSecure));
			}
		} else {
//			Response forbiddenResponse = Response.status(Status.FORBIDDEN)
//					.build();
//			requestContext.abortWith(forbiddenResponse);
		}
	}

	private AuthorizationPayload verifyToken(String token) {
		Client client = createClient();
		
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(this.clientId, this.clientSecret);
		
		Invocation verifyToken = client
			.target("https://api.github.com/applications/{client_id}/tokens/{access_token}")
			.resolveTemplate("client_id", this.clientId)
			.resolveTemplate("access_token", token)
			.register(feature)
			.request()
			.buildGet();
		
		Response verifyResponse = verifyToken.invoke();
		if (verifyResponse.getStatus() == 200) {
			// TODO temporary
			ObjectMapper mapper = new ObjectMapper();
			AnnotationIntrospector introspector = new JacksonAnnotationIntrospector();
			// make deserializer use JAXB annotations (only)
			mapper.getDeserializationConfig().withAppendedAnnotationIntrospector(
					introspector);
			// make serializer use JAXB annotations (only)
			mapper.getSerializationConfig().withAppendedAnnotationIntrospector(
					introspector);

			AuthorizationPayload authPayload = null;
			try {
				authPayload = mapper.readValue(
						(InputStream) verifyResponse.readEntity(InputStream.class),
						AuthorizationPayload.class);
			} catch (IOException e) {
				// TODO fix this
				throw new RuntimeException("Could not get or parse GitHub Auth response.", e);
			}
			return authPayload;
		} else {
			System.out.format("Received error in verify response %d", verifyResponse.getStatus());
			return null;
		}
	}

	private String exchangeForToken(String clientId, String clientSecret,
			String code) {
		Client client = createClient();
		Form form = new Form().param("client_id", clientId)
				.param("client_secret", clientSecret).param("code", code);
		Invocation exchange = client
				.target("https://github.com/login/oauth/access_token")
				.request().header("User-Agent", "Octopull/1.0.0")
				.buildPost(form(form));

		Response exchangeResponse = exchange.invoke();
		if (exchangeResponse.getStatus() == OK.getStatusCode()) {
			MultivaluedMap<String, String> exchangeBody = exchangeResponse
					.readEntity(new GenericType<MultivaluedMap<String, String>>() {
					});
			// TODO handle errors
			return exchangeBody.getFirst("access_token");
		} else {
			// TODO handle errors
			System.out.format("Error, token exchange returned status %d", exchangeResponse.getStatus());
			return null;
		}
	}

	private Client createClient() {
		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, new TrustManager[] { new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] arg0,
						String arg1) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] arg0,
						String arg1) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}

			} }, new java.security.SecureRandom());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HostnameVerifier hostnameVerifier = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				// TODO Auto-generated method stub
				return true;
			}
		};
		return ClientBuilder.newBuilder().sslContext(sslContext)
				.hostnameVerifier(hostnameVerifier).build();
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class UserPayload {
		public String login;
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class AuthorizationPayload {
		public List<String> scopes;
		public UserPayload user;
	}
	
	public static class GitHubPrincipal implements Principal {
		private String username;
		private String token;
		private ArrayList<String> scopes;

		public GitHubPrincipal(String username, String token,
				ArrayList<String> scopes) {
			this.username = username;
			this.token = token;
			this.scopes = new ArrayList<String>(scopes);
		}

		@Override
		public String getName() {
			return this.username;
		}
		
		public String getToken() {
			return this.token;
		}
		
		public List<String> getScopes() {
			return this.scopes;
		}
	}

	public static class GitHubSecurityContext implements SecurityContext {
		private String username;
		private String token;
		private ArrayList<String> scopes;
		private boolean secure;

		public GitHubSecurityContext(String username, String token, List<String> scopes,
				boolean isSecure) {
			this.username = username;
			this.token = token;
			this.scopes = new ArrayList<String>(scopes);
			this.secure = isSecure;
		}
		
		public String getToken() {
			return this.token;
		}

		@Override
		public Principal getUserPrincipal() {
			if (this.username == null) {
				return null;
			} else {
				final String username = this.username;
				return new GitHubPrincipal(username, token, scopes);
			}
		}

		@Override
		public boolean isUserInRole(String role) {
			return "user".equals(role);
		}
			
		public boolean hasScope(String scope) {
			String[] roleParts = scope.split(":");
			return this.scopes.contains(roleParts[0])
					|| this.scopes.contains(scope);
		}

		@Override
		public boolean isSecure() {
			return this.secure;
		}

		@Override
		public String getAuthenticationScheme() {
			return "github_oauth";
		}

	}
}
