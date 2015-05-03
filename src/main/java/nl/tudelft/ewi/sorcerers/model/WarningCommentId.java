package nl.tudelft.ewi.sorcerers.model;

import java.io.Serializable;

import javax.persistence.Id;

public class WarningCommentId implements Serializable {
	private static final long serialVersionUID = -91365949595298563L;
	
	@Id
	private String repo;
	@Id
	private String commit;
	@Id
	private Integer warningId;
	@Id
	private Long commentId;
	
	@SuppressWarnings("unused")
	private WarningCommentId() {}
	
	public WarningCommentId(String repo, String commit, int warningId, long commentId) {
		this.repo = repo;
		this.commit = commit;
		this.warningId = warningId;
		this.commentId = commentId;
	}

	public String getRepo() {
		return this.repo;
	}

	public String getCommit() {
		return this.commit;
	}

	public Integer getWarningId() {
		return this.warningId;
	}
	
	public Long getCommentId() {
		return this.commentId;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof WarningCommentId) {
			WarningCommentId otherId = (WarningCommentId) other;
			return this.repo.equals(otherId.getRepo())
					&& this.commit.equals(otherId.getCommit())
					&& this.warningId.equals(otherId.getWarningId())
					&& this.commentId.equals(otherId.getCommentId());
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    int repoHash = this.repo.hashCode();
		result = prime * result + (int) (repoHash ^ (repoHash >>> 32));
		int commitHash = this.commit.hashCode();
	    result = prime * result + (int) (commitHash ^ (commitHash >>> 32));
	    int warningIdHash = this.warningId.hashCode();
	    result = prime * result + (int) (warningIdHash ^ (warningIdHash >>> 32));
	    int commentIdHash = this.commentId.hashCode();
	    result = prime * result + (int) (commentIdHash ^ (commentIdHash >>> 32));
	    return result;
	}
}
