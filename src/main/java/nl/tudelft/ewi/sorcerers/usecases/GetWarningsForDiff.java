package nl.tudelft.ewi.sorcerers.usecases;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import nl.tudelft.ewi.sorcerers.model.Diff;
import nl.tudelft.ewi.sorcerers.model.Warning;
import nl.tudelft.ewi.sorcerers.model.WarningService;

public class GetWarningsForDiff {
	private WarningService warningService;

	@Inject
	public GetWarningsForDiff(WarningService warningService) {
		this.warningService = warningService;
	}
	
	public Diff execute(String repo, String base, String head) throws IOException {
		return this.warningService.getWarningsForDiff(repo, base, head);
	}
}
