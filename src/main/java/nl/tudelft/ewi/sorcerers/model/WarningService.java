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

	public Warning addWarningIfNew(String repo, String commit, String path, int line,
			String message) {
		Warning warning = this.warningRepository.find(repo, commit, path, line, message);
		if (warning == null)
			return this.warningRepository.add(new Warning(repo, commit, path, line, message));
		else
			return warning;
	}

}
