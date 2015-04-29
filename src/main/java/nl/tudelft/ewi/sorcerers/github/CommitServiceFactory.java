package nl.tudelft.ewi.sorcerers.github;

import javax.inject.Inject;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.glassfish.hk2.api.Factory;

public class CommitServiceFactory implements Factory<CommitService> {
	private GitHubClient client;

	@Inject
	public CommitServiceFactory(GitHubClient client) {
		this.client = client;
	}

	@Override
	public CommitService provide() {
		return new CommitService(this.client);
	}

	@Override
	public void dispose(CommitService instance) {
		// Nothing to do.
	}
}
