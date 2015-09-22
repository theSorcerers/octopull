package nl.tudelft.ewi.sorcerers.model;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "repo", "commit",
		"path", "line", "message" }))
@IdClass(WarningId.class)
@XmlRootElement
public class Warning {
	@Id
	private String repo;
	@Id
	private String commit;
	@Id
	@GeneratedValue
	private Integer warningId;

	@Basic
	private String path;
	@Basic
	private int line;
	@Basic
	private String tool;
	@Basic
	private String message;

	@SuppressWarnings("unused")
	private Warning() {
	}

	public Warning(String repo, String commit, String path, int line,
			String tool, String message) {
		this.repo = repo;
		this.commit = commit;
		this.path = path;
		this.line = line;
		this.tool = tool;
		this.message = message;
	}

	public String getRepo() {
		return repo;
	}

	public String getCommit() {
		return commit;
	}

	public Integer getId() {
		return warningId;
	}

	public String getPath() {
		return path;
	}

	public int getLine() {
		return line;
	}

	public String getTool() {
		return tool;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Warning) {
			Warning that = (Warning) other;
			return (repo == null && that.getRepo() == null || repo.equals(that
					.getRepo()))
					&& (commit == null && that.getCommit() == null || commit
							.equals(that.getCommit()))
					&& (path == null && that.getPath() == null || path
							.equals(that.getPath()))
					&& line == that.getLine()
					&& (tool == null && that.getTool() == null || tool.equals(that.getTool()))
					&& (message == null && that.getMessage() == null || message.equals(that.getMessage()));
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return String.format("Warning (%s, %s, %s:%d, %s, %s)", repo, commit,
				path, line, tool, message);
	}
}
