package nl.tudelft.ewi.sorcerers.model;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;

import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.PullRequestService;

public class CommentService {
	private WarningService warningService;
	private PullRequestService pullRequestService;
	private WarningCommentRepository warningCommentRepository;

	@Inject
	public CommentService(WarningService warningService,
			PullRequestService pullRequestService,
			WarningCommentRepository warningCommentRepository) {
		this.warningService = warningService;
		this.pullRequestService = pullRequestService;
		this.warningCommentRepository = warningCommentRepository;
	}

	public CommitComment createCommentFromWarning(String repo, String commit,
			Integer pullRequest, Integer warningId, Integer position)
			throws IOException {
		Warning warning = this.warningService.get(repo, commit, warningId);
		if (warning == null) {
			throw new IllegalArgumentException("No such warning exists.");
		} else {
			StringBuilder bodyBuilder = new StringBuilder();
			bodyBuilder.append("*Auto-generated comment*\n\n");
			bodyBuilder.append("    ");
			bodyBuilder.append(warning.getMessage().replaceAll("\n", "\n    "));
			bodyBuilder.append("\n");

			CommitComment comment = new CommitComment();
			comment.setBody(bodyBuilder.toString());
			comment.setCommitId(commit);
			comment.setPath(warning.getPath());
			comment.setPosition(position);
			comment = this.pullRequestService.createComment(
					RepositoryId.createFromId(repo), pullRequest, comment);

			this.warningCommentRepository.add(new WarningComment(warning.getRepo(), warning.getCommit(), warning.getId(), new Date(), comment.getId()));
			
			return comment;
		}
	}
}
