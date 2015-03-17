package nl.tudelft.ewi.sorcerers.model;

import java.util.List;

import javax.inject.Inject;

public class WarningService {
	private WarningRepository warningRepository;

	@Inject
	public WarningService(WarningRepository warningRepository) {
		this.warningRepository = warningRepository;
	}
	
	public List<Warning> getWarningsForCommit(String repo, String commit) {
		return this.warningRepository.getWarningsForCommit(repo, commit);
	}
	
}
