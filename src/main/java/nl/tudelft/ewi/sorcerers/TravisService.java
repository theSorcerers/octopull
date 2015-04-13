package nl.tudelft.ewi.sorcerers;

import static javax.ws.rs.client.Entity.json;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.inject.Inject;
import javax.inject.Named;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

public class TravisService {
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class AccessTokenHolder {
		public String access_token;
	}

	private String githubToken;
	private Client client;

	@Inject
	public TravisService(@Named("env:GITHUB_TOKEN") String githubToken) {
		this.githubToken = githubToken;
		this.client = createClient();
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

	public InputStream getLogFromJobId(String id) throws IOException {
		AccessTokenHolder accessTokenHolder = authenticate();

		Invocation logInvocation = this.client
				.target("https://api.travis-ci.com/jobs/{jobId}/log.txt")
				.resolveTemplate("jobId", id)
				.queryParam("access_token", accessTokenHolder.access_token)
				.request(MediaType.TEXT_PLAIN)
				.header("User-Agent", "Octopull/1.0.0").buildGet();

		Response logResponse = logInvocation.invoke();
		if (logResponse.getStatus() == 200) {
			return logResponse.readEntity(InputStream.class);
		} else {
			// TODO fix this
			throw new RuntimeException(String.format(
					"Failed to retrieve log from Travis CI, got status %d",
					logResponse.getStatus()));
		}
	}

	private AccessTokenHolder authenticate() throws IOException {
		String authRequest = String.format("{\"github_token\":\"%s\"}",
				this.githubToken);
		Invocation authInvocation = this.client
				.target("https://api.travis-ci.com/auth/github")
				.request("application/vnd.travis-ci.2+json")
				.header("User-Agent", "Octopull/1.0.0")
				.buildPost(json(authRequest));

		Response authResponse = authInvocation.invoke();
		if (authResponse.getStatus() != 200) {
			// TODO fix this
			throw new RuntimeException("Could not authenticate to Travis CI.");
		}
		// TODO temporary
		ObjectMapper mapper = new ObjectMapper();
		AnnotationIntrospector introspector = new JacksonAnnotationIntrospector();
		// make deserializer use JAXB annotations (only)
		mapper.getDeserializationConfig().withAppendedAnnotationIntrospector(
				introspector);
		// make serializer use JAXB annotations (only)
		mapper.getSerializationConfig().withAppendedAnnotationIntrospector(
				introspector);

		AccessTokenHolder accessTokenHolder;
		try {
			accessTokenHolder = mapper.readValue(
					(InputStream) authResponse.readEntity(InputStream.class),
					AccessTokenHolder.class);
		} catch (JsonParseException e) {
			// TODO fix this
			throw new RuntimeException("Could not parse Travis CI response.", e);
		} catch (JsonMappingException e) {
			// TODO fix this
			throw new RuntimeException("Could not parse Travis CI response.", e);
		}
		return accessTokenHolder;
	}
}
