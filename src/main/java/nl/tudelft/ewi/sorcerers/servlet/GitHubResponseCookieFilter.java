package nl.tudelft.ewi.sorcerers.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.Principal;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import nl.tudelft.ewi.sorcerers.servlet.GitHubOAuthFilter.GitHubPrincipal;

@Priority(Priorities.AUTHENTICATION)
@PreMatching
@Provider
public class GitHubResponseCookieFilter implements ContainerResponseFilter {
	@Override
	public void filter(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) throws IOException {
		SecurityContext securityContext = requestContext.getSecurityContext();
		Principal userPrincipal = securityContext.getUserPrincipal();
		if (userPrincipal instanceof GitHubPrincipal) {
			GitHubPrincipal githubPrincipal = (GitHubPrincipal) userPrincipal;
			
			String baseUri = requestContext.getUriInfo().getBaseUriBuilder()
					.scheme(null).host(null).port(-1).toString();
			
			String token = githubPrincipal.getToken();
			String tag = githubPrincipal.getTag();
			String username = githubPrincipal.getName();
			StringBuilder stringBuilder = new StringBuilder();
			if (githubPrincipal.getScopes() != null) {
				for (String scope : githubPrincipal.getScopes()) {
					if (stringBuilder.length() > 0) {
						stringBuilder.append(",");
					}
					stringBuilder.append(scope);
				}
			}
			String scopes = stringBuilder.toString();
			
			String cookie = URLEncoder.encode(String.format("%s;%s;%s;%s", token, tag, username, scopes), "UTF-8");
			
			NewCookie tokenCookie = new NewCookie("github_token",
					cookie, baseUri, null, Cookie.DEFAULT_VERSION, null,
					NewCookie.DEFAULT_MAX_AGE, null, false, true);
			
			responseContext.getHeaders().add("Set-Cookie", tokenCookie);
		}
	}
}