package nl.tudelft.ewi.sorcerers.github;

import java.io.IOException;

import javax.inject.Inject;

import nl.tudelft.ewi.sorcerers.github.PatchedGitHubClient.PatchedCommitComment;
import nl.tudelft.ewi.sorcerers.model.ReviewComment;
import nl.tudelft.ewi.sorcerers.model.ReviewService;
import nl.tudelft.ewi.sorcerers.model.Warning;

import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.PullRequestService;

public class GitHubReviewService implements ReviewService {
	private PullRequestService pullRequestService;
	
	@Inject
	public GitHubReviewService(PullRequestService pullRequestService) {
		this.pullRequestService = pullRequestService;
	}
	
	@Override
	public ReviewComment createCommitComment(String repo, String commit, Integer pullRequest, Integer position, Warning warning, String body)
			throws IOException {
		CommitComment comment = new CommitComment();
		comment.setBody(body);
		comment.setCommitId(commit);
		comment.setPath(warning.getPath());
		comment.setPosition(position);
		PatchedCommitComment presult = (PatchedCommitComment) this.pullRequestService.createComment(
				RepositoryId.createFromId(repo), pullRequest, comment);
		ReviewComment result = new ReviewComment(presult.getId(), presult.getHtmlUrl());
		return result;
	}

}
