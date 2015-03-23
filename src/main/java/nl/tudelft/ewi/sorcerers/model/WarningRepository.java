package nl.tudelft.ewi.sorcerers.model;

import java.util.List;

public interface WarningRepository {
	Warning add(Warning warning);
	Warning find(String repo, String commit, String path, int line, String message);
	
	List<Warning> getWarningsForCommit(String repo, String commit);
}
