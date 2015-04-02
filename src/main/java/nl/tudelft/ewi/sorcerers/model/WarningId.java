package nl.tudelft.ewi.sorcerers.model;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class WarningId implements Serializable {
	private static final long serialVersionUID = -1379555617623700835L;

	@Id
	private String repo;
	@Id
	private String commit;
	@Id
	@GeneratedValue
	private Integer warningId;

	public String getRepo() {
		return this.repo;
	}

	public String getCommit() {
		return this.commit;
	}

	public Integer getWarningId() {
		return this.warningId;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof WarningId) {
			WarningId otherId = (WarningId) other;
			return this.repo.equals(otherId.getRepo())
					&& this.commit.equals(otherId.getCommit())
					&& this.warningId.equals(otherId.getWarningId());
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
	    return result;
	}
}
