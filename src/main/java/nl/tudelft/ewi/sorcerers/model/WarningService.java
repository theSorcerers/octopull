package nl.tudelft.ewi.sorcerers.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.exception.ConstraintViolationException;

public class WarningService {
	private WarningRepository warningRepository;
	private CompareService compareService;

	@Inject
	public WarningService(WarningRepository warningRepository, CompareService compareService) {
		this.warningRepository = warningRepository;
		this.compareService = compareService;
	}

	public List<Warning> getWarningsForCommit(String repo, String commit) {
		return this.warningRepository.getWarningsForCommit(repo, commit);
	}

	public Warning addWarningIfNew(String repo, String commit, String path, int line,
			String tool, String message) {
		Warning warning = this.warningRepository.find(repo, commit, path, line, tool, message);
		if (warning == null) {
			try {
				return this.warningRepository.add(new Warning(repo, commit, path, line, tool, message));
			} catch(ConstraintViolationException e) {
				return null;
			}
		} else {
			return warning;
		}
	}

	public Warning addWarningIfNew(Warning warning) {
		return this.addWarningIfNew(warning.getRepo(), warning.getCommit(), warning.getPath(), warning.getLine(), warning.getTool(), warning.getMessage());
	}

	public Warning get(String repo, String commit, Integer warningId) {
		return this.warningRepository.get(repo, commit, warningId);
	}

	public Diff getWarningsForDiff(String repo, String base,
			String head) throws IOException {
		String baseCommit = compareService.getMergeBase(repo, base, head);
		
		ArrayList<Warning> warnings = new ArrayList<Warning>();
		warnings.addAll(getWarningsForCommit(repo, baseCommit));
		warnings.addAll(getWarningsForCommit(repo, head));
		return new Diff(baseCommit, head, warnings);
	}
}
