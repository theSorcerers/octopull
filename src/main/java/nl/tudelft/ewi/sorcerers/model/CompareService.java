package nl.tudelft.ewi.sorcerers.model;

import java.io.IOException;

public interface CompareService {

	public abstract String getMergeBase(String repo, String base, String head)
			throws IOException;

}