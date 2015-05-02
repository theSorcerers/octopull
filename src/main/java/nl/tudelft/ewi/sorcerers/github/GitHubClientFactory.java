package nl.tudelft.ewi.sorcerers.github;

import java.security.Principal;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import nl.tudelft.ewi.sorcerers.servlet.GitHubOAuthFilter.GitHubPrincipal;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.glassfish.hk2.api.Factory;

public class GitHubClientFactory implements Factory<GitHubClient> {
	private SecurityContext securityContext;
	
	@Inject
	public GitHubClientFactory(@Context SecurityContext securityContext) {
		this.securityContext = securityContext;
	}

	@Override
	public GitHubClient provide() {
		GitHubClient client = new PatchedGitHubClient();
		Principal user = securityContext.getUserPrincipal();
		if (user != null && user instanceof GitHubPrincipal) {
			GitHubPrincipal githubUser = (GitHubPrincipal) user;
			client.setOAuth2Token(githubUser.getToken());
		}
		return client;
	}

	@Override
	public void dispose(GitHubClient instance) {
		// Nothing to do.
	}

}
