package nl.tudelft.ewi.sorcerers.github;

import javax.inject.Inject;
import javax.inject.Provider;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.glassfish.hk2.api.Factory;

public class CommitServiceFactory implements Factory<CommitService> {
	private Provider<GitHubClient> clientProvider;

	@Inject
	public CommitServiceFactory(Provider<GitHubClient> clientProvider) {
		this.clientProvider = clientProvider;
	}

	@Override
	public CommitService provide() {
		return new CommitService(this.clientProvider.get());
	}

	@Override
	public void dispose(CommitService instance) {
		// Nothing to do.
	}
}
