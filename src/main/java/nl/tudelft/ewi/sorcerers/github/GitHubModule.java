//package nl.tudelft.ewi.sorcerers.github;
//
//import javax.inject.Inject;
//import javax.inject.Provider;
//
//import org.eclipse.egit.github.core.client.GitHubClient;
//import org.eclipse.egit.github.core.service.CommitService;
//
//import com.google.inject.AbstractModule;
//
//public class GitHubModule extends AbstractModule {
//
//	@Override
//	protected void configure() {
//		String githubToken = System.getenv("GITHUB_TOKEN");
//		
//		GitHubClient instance = new GitHubClient();
//		instance.setOAuth2Token(githubToken);
//		bind(GitHubClient.class).toInstance(instance);
//		
//		bind(CommitService.class).toProvider(CommitServiceProvider.class);
//	}
//	
//	private static class CommitServiceProvider implements Provider<CommitService> {
//		private GitHubClient client;
//		private CommitService commitService;
//
//		@Inject protected CommitServiceProvider(GitHubClient client) {
//			this.client = client;
//			this.commitService = null;
//		}
//
//		@Override
//		public CommitService get() {
//			if (this.commitService == null) {
//				this.commitService = new CommitService(this.client);
//			}
//			return this.commitService;
//		}
//	}
//}
