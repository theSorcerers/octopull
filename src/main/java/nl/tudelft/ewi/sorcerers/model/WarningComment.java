package nl.tudelft.ewi.sorcerers.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(WarningCommentId.class)
public class WarningComment {
	@Id private String repo;
	@Id private String commit;
	@Id private Integer warningId;
	@Id private Long commentId;
	@Basic private Date date;
	
	@SuppressWarnings("unused")
	private WarningComment() {}
	
	public WarningComment(String repo, String commit, int warningId, Date date, long commentId) {
		this.repo = repo;
		this.commit = commit;
		this.warningId = warningId;
		this.date = date;
		this.commentId = commentId;
	}
}
