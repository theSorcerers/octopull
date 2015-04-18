package nl.tudelft.ewi.sorcerers.github;

import javax.inject.Inject;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.glassfish.hk2.api.Factory;

public class PullRequestServiceFactory implements Factory<PullRequestService> {
	private GitHubClient client;

	@Inject
	public PullRequestServiceFactory(GitHubClient client) {
		this.client = client;
	}

	@Override
	public PullRequestService provide() {
		return new PullRequestService(this.client);
	}

	@Override
	public void dispose(PullRequestService instance) {
		// Nothing to do.
	}
}
