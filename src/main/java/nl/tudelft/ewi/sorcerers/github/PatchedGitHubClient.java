package nl.tudelft.ewi.sorcerers.github;

import java.io.IOException;
import java.lang.reflect.Type;

import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.client.GitHubClient;

public class PatchedGitHubClient extends GitHubClient {
	@Override
	public <V> V post(String uri, Object params, Type type) throws IOException {
		if (type == CommitComment.class) {
			type = PatchedCommitComment.class;
		}
		return super.post(uri, params, type);
	}

	public static class PatchedCommitComment extends CommitComment {
		private static final long serialVersionUID = -730519500692172099L;
		
		private String htmlUrl;
		
		public CommitComment setHtmlUrl(String htmlUrl) {
			this.htmlUrl = htmlUrl;
			return this;
		}
		
		public String getHtmlUrl() {
			return this.htmlUrl;
		}
	}
}
