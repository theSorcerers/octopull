package nl.tudelft.ewi.sorcerers.usecases;

import java.util.List;

import javax.inject.Inject;

import com.google.inject.persist.Transactional;

import nl.tudelft.ewi.sorcerers.model.Warning;
import nl.tudelft.ewi.sorcerers.model.WarningService;

public class GetWarningsForCommit {
	private WarningService warningService;

	@Inject
	public GetWarningsForCommit(WarningService warningService) {
		this.warningService = warningService;
	}
	
	@Transactional
	public List<Warning> execute(String repo, String commit) {
		return this.warningService.getWarningsForCommit(repo, commit);
	}
}