package nl.tudelft.ewi.sorcerers.model;

import java.util.List;

public interface WarningRepository {
	Warning add(Warning warning);
	Warning get(String repo, String commit, Integer warningId);
	Warning find(String repo, String commit, String path, int line, String tool, String message);
	
	List<Warning> getWarningsForCommit(String repo, String commit);
}
