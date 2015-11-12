package nl.tudelft.ewi.sorcerers.model;

public class ReviewComment {
	private long id;
	private String htmlUrl;

	public ReviewComment(long id, String htmlUrl) {
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
