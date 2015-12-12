package nl.tudelft.ewi.sorcerers.model;

import java.io.IOException;
import java.net.URI;
import java.util.Date;

import javax.inject.Inject;

public class CommentService {
	private WarningService warningService;
	private ReviewService reviewService;
	private WarningCommentRepository warningCommentRepository;

	@Inject
	public CommentService(WarningService warningService,
			ReviewService reviewService,
			WarningCommentRepository warningCommentRepository) {
		this.warningService = warningService;
		this.reviewService = reviewService;
		this.warningCommentRepository = warningCommentRepository;
	}

	public URI createCommentFromWarning(String repo, String commit,
			Integer pullRequest, Integer warningId, Integer position)
			throws IOException {
		Warning warning = this.warningService.get(repo, commit, warningId);
		if (warning == null)
			throw new IllegalArgumentException("No such warning exists.");
		
		StringBuilder bodyBuilder = new StringBuilder();
		bodyBuilder.append("*Auto-generated comment*\n\n");
		bodyBuilder.append("    ");
		bodyBuilder.append(warning.getMessage().replaceAll("\n", "\n    "));
		bodyBuilder.append("\n");
		String body = bodyBuilder.toString();

		ReviewComment result = this.reviewService.createCommitComment(repo, commit,
				pullRequest, position, warning, body);
		
		this.warningCommentRepository.add(new WarningComment(warning.getRepo(), warning.getCommit(), warning.getId(), new Date(), result.getId()));
		
		return URI.create(result.getHtmlUrl());
	}
}
