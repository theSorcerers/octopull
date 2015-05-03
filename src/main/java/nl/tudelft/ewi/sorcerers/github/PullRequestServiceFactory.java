package nl.tudelft.ewi.sorcerers.github;

import javax.inject.Inject;
import javax.inject.Provider;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.glassfish.hk2.api.Factory;

public class PullRequestServiceFactory implements Factory<PullRequestService> {
	private Provider<GitHubClient> clientProvider;

	@Inject
	public PullRequestServiceFactory(Provider<GitHubClient> clientProvider) {
		this.clientProvider = clientProvider;
	}

	@Override
	public PullRequestService provide() {
		return new PullRequestService(this.clientProvider.get());
	}

	@Override
	public void dispose(PullRequestService instance) {
		// Nothing to do.
	}
}
