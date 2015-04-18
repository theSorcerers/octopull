package nl.tudelft.ewi.sorcerers.github;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import nl.tudelft.ewi.sorcerers.servlet.GitHubOAuthFilter;
import nl.tudelft.ewi.sorcerers.servlet.GitHubOAuthFilter.GitHubSecurityContext;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.glassfish.hk2.api.Factory;

public class GitHubClientFactory implements Factory<GitHubClient> {
	private GitHubSecurityContext gitHubSecurityContext;

	@Inject
	public GitHubClientFactory(@Context SecurityContext securityContext) {
		if (securityContext instanceof GitHubSecurityContext) {
			this.gitHubSecurityContext = (GitHubSecurityContext) securityContext;
		}
	}
	
	@Override
	public GitHubClient provide() {
		GitHubClient client = new GitHubClient();
		if (this.gitHubSecurityContext != null) {
			client.setOAuth2Token(this.gitHubSecurityContext.getToken());
		}
		return client;
	}

	@Override
	public void dispose(GitHubClient instance) {
		// Nothing to do.
	}

}
