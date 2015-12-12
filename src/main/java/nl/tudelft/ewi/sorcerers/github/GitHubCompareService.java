package nl.tudelft.ewi.sorcerers.github;

import java.io.IOException;

import javax.inject.Inject;

import nl.tudelft.ewi.sorcerers.model.CompareService;

import org.eclipse.egit.github.core.RepositoryCommitCompare;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.CommitService;

public class GitHubCompareService implements CompareService {
	private CommitService commitService;
	
	@Inject
	public GitHubCompareService(CommitService commitService) {
		this.commitService = commitService;
	}
	
	@Override
	public String getMergeBase(String repo, String base, String head)
			throws IOException {
		RepositoryCommitCompare compare = this.commitService.compare(RepositoryId.createFromId(repo), base, head);
		return compare.getBaseCommit().getSha();
	}
}
