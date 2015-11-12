package nl.tudelft.ewi.sorcerers.github;

public class GitHubCommitComment {
	private long id;
	private String htmlUrl;

	public GitHubCommitComment(long id, String htmlUrl) {
		this.id = id;
		this.htmlUrl = htmlUrl;
	}

	public long getId() {
		return id;
	}

	public String getHtmlUrl() {
		return htmlUrl;
	}
}
