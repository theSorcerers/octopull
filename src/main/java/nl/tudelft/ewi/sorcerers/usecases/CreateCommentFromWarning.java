package nl.tudelft.ewi.sorcerers.usecases;

import java.io.IOException;
import java.net.URI;

import javax.inject.Inject;

import nl.tudelft.ewi.sorcerers.model.CommentService;
import nl.tudelft.ewi.sorcerers.resources.CommentResource;

public class CreateCommentFromWarning {
	private CommentService commentService;

	@Inject
	public CreateCommentFromWarning(CommentService commentService) {
		this.commentService = commentService;
	}
	
	public URI execute(CommentResource commentResource, String repo, String commit, Integer pullRequest, Integer warningId, Integer position) throws IOException {
		return this.commentService.createCommentFromWarning(repo, commit, pullRequest, warningId, position);
	}
}
