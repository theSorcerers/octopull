package nl.tudelft.ewi.sorcerers.model;

import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.PullRequestService;

public class CommentService {
	private WarningService warningService;
	private PullRequestService pullRequestService;

	@Inject
	public CommentService(WarningService warningService, PullRequestService pullRequestService) {
		this.warningService = warningService;
		this.pullRequestService = pullRequestService;
	}
	
	public CommitComment createCommentFromWarning(String repo, String commit,
			Integer pullRequest, Integer warningId, Integer position) throws IOException {
		Warning warning = this.warningService.get(repo, commit, warningId);
		if (warning == null) {
			throw new IllegalArgumentException("No such warning exists.");
		} else {
			CommitComment comment = new CommitComment();
			comment.setBody("this is a test comment!");
			comment.setCommitId(commit);
			comment.setPath(warning.getPath());
			comment.setPosition(position);
			System.out.println(String.format("%s, %s, %d, %s, %d", commit, warning.getPath(), position, repo, pullRequest));
			comment = this.pullRequestService.createComment(RepositoryId.createFromId(repo), pullRequest, comment);
			return comment;
		}
	}
}
