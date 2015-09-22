package nl.tudelft.ewi.sorcerers;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.List;

import nl.tudelft.ewi.sorcerers.model.Warning;

import org.junit.Test;

public class FindBugsLogParserTest {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String xml = "<BugCollection timestamp=\"1442828452000\" analysisTimestamp=\"1442828523755\" sequence=\"0\" release=\"\" version=\"3.0.1\">" + LINE_SEPARATOR +
			"<Project><SrcDir>/src/main/java</SrcDir></Project>" +
			"<BugInstance rank=\"18\" category=\"PERFORMANCE\" instanceHash=\"7eab4f285f3064d59b2a9b7988903d96\" instanceOccurrenceNum=\"0\" priority=\"2\" abbrev=\"UrF\" type=\"URF_UNREAD_FIELD\" instanceOccurrenceMax=\"0\">" + LINE_SEPARATOR +
			    "<ShortMessage>Unread field</ShortMessage>" +
			    "<LongMessage>Long Message</LongMessage>" +
			    "<SourceLine endBytecode=\"16\" startBytecode=\"16\" start=\"49\" classname=\"nl.tudelft.ewi.sorcerers.ForbiddenExceptionMapper$Message\" primary=\"true\" sourcepath=\"nl/tudelft/ewi/sorcerers/ForbiddenExceptionMapper.java\" sourcefile=\"ForbiddenExceptionMapper.java\" end=\"49\">" +
			        "<Message>At ForbiddenExceptionMapper.java:[line 49]</Message>" +
			    "</SourceLine>" +
		    "</BugInstance>" +
		"</BugCollection>";
	
	@Test
	public void should_return_warnings() {
		FindBugsLogParser p = new FindBugsLogParser();
		
		List<Warning> parse = p.parse(new InputStreamReader(new ByteArrayInputStream(xml.getBytes())));
		
		assertEquals(new Warning(null, null, "/src/main/java/nl/tudelft/ewi/sorcerers/ForbiddenExceptionMapper.java", 49, "findbugs", "Long Message"), parse.get(0));
	}
}
