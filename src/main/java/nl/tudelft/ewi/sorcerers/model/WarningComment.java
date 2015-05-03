package nl.tudelft.ewi.sorcerers.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class WarningComment {
	@Id private String repo;
	@Id private String commit;
	@Id private Integer warningId;
	@Id private Long commentId;
	
	@SuppressWarnings("unused")
	private WarningComment() {}
	
	public WarningComment(String repo, String commit, int warningId, long commentId) {
		this.repo = repo;
		this.commit = commit;
		this.warningId = warningId;
		this.commentId = commentId;
	}
}
