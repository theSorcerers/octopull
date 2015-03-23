package nl.tudelft.ewi.sorcerers.model;

import java.util.List;

public interface WarningRepository {
	Warning add(Warning warning);
	List<Warning> getWarningsForCommit(String repo, String commit);
}
