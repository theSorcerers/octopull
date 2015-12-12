package nl.tudelft.ewi.sorcerers.model;

import java.io.IOException;

public interface ReviewService {

	public abstract ReviewComment createCommitComment(String repo,
			String commit, Integer pullRequest, Integer position,
			Warning warning, String body) throws IOException;

}