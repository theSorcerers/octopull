package nl.tudelft.ewi.sorcerers.github;

import static java.util.regex.Pattern.MULTILINE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.eclipse.egit.github.core.CommitFile;
import org.eclipse.egit.github.core.RepositoryCommitCompare;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.CommitService;

public class LineMapService {
	private static final String HUNK_HEADER_REGEX = "^@@\\s+\\-([0-9]+(?:,[0-9]+)?)\\s+\\+([0-9]+(?:,[0-9]+)?)\\s+@@$";
	private static final String HUNK_BODY_REGEX = "((?:^[-+ ].*$[\r\n]+)*)";
	private static final String LINE_SEPARATOR_REGEX = "(?:[\r\n]+)";
	private CommitService service;

	@Inject
	public LineMapService(CommitService service) {
		this.service = service;
	}
	
	public void createLineMap(String repo, String base, String head) {
		System.out.println(String.format("createLineMap %s %s %s", repo, base, head));
		try {
			RepositoryCommitCompare compare = this.service.compare(RepositoryId.createFromId(repo), base, head);
			for (CommitFile file : compare.getFiles()) {
				parseHunks(file.getPatch());
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block	
			e.printStackTrace();
		}
	}

	private List<Hunk> parseHunks(String patch) {
		List<Hunk> list = new ArrayList<Hunk>();
		
		// Fix missing newlines after hunk header
		String patch_fixed = Pattern.compile("^@@(.*)@@(?!$)", MULTILINE).matcher(patch).replaceAll("@@$1@@\n");
		
		Matcher hunkMatcher = Pattern.compile(HUNK_HEADER_REGEX + LINE_SEPARATOR_REGEX + HUNK_BODY_REGEX + "(^\\\\(?:.*)$[\r\n]+)?", MULTILINE).matcher(patch_fixed);
		while (hunkMatcher.find()) {
			String baseLines = hunkMatcher.group(1);
			int baseLineStart, baseLineCount, headLineStart, headLineCount;
			String headLines = hunkMatcher.group(2);
			String lines = hunkMatcher.group(3);
			
			Pattern linesPattern = Pattern.compile("^([0-9]+)(,[0-9]+)?$");
			Matcher baseLinesMatcher = linesPattern.matcher(baseLines);
			baseLinesMatcher.matches();
			baseLineStart = Integer.valueOf(baseLinesMatcher.group(1));
			baseLineCount = 1;
			if (baseLinesMatcher.groupCount() == 2) {
				baseLineCount = Integer.valueOf(baseLinesMatcher.group(2));
			}

			Matcher headLinesMatcher = linesPattern.matcher(baseLines);
			headLinesMatcher.matches();
			headLineStart = Integer.valueOf(headLinesMatcher.group(1));
			headLineCount = 1;
			if (headLinesMatcher.groupCount() == 2) {
				headLineCount = Integer.valueOf(headLinesMatcher.group(2));
			}
			
			System.out.println(hunkMatcher.groupCount());
			for (int i = 1; i <= hunkMatcher.groupCount(); i++) {
				System.out.println(hunkMatcher.group(i));
			}
			Hunk hunk = new Hunk();
			list.add(hunk);
		}
		
		return list;
	}
	
	public static class Hunk {
		
	}
}
