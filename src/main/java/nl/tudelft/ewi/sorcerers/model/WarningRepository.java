package nl.tudelft.ewi.sorcerers.model;

import java.util.List;

public interface WarningRepository {
	List<Warning> getWarningsForCommit(String repo, String commit);
}
