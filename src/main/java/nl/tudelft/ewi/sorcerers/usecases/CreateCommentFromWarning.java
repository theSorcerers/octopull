package nl.tudelft.ewi.sorcerers.usecases;

import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.egit.github.core.CommitComment;

import nl.tudelft.ewi.sorcerers.model.CommentService;

public class CreateCommentFromWarning {
	private CommentService commentService;

	@Inject
	public CreateCommentFromWarning(CommentService commentService) {
		this.commentService = commentService;
	}
	
	public CommitComment execute(String repo, String commit, Integer pullRequest, Integer warningId, Integer position) throws IOException {
		return this.commentService.createCommentFromWarning(repo, commit, pullRequest, warningId, position);
	}
}
