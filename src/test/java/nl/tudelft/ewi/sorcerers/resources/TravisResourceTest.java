package nl.tudelft.ewi.sorcerers.resources;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.tudelft.ewi.sorcerers.CheckstyleLogParser;
import nl.tudelft.ewi.sorcerers.LogParser;
import nl.tudelft.ewi.sorcerers.model.Warning;
import nl.tudelft.ewi.sorcerers.model.WarningService;
import nl.tudelft.ewi.sorcerers.resources.TravisResource.TravisPayload;
import nl.tudelft.ewi.sorcerers.resources.TravisResource.TravisRepository;

import org.glassfish.hk2.api.IterableProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class TravisResourceTest {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	@Mock
	private IterableProvider<LogParser> logProviders;
	@Mock
	private IterableProvider<LogParser> aaaProvider;
	@Mock
	private IterableProvider<LogParser> bbbProvider;
	@Mock
	private LogParser bbbParser;
	@Mock
	private WarningService warningService;
	
	@Test
	public void should_parse_warnings_per_tool() {		
		when(logProviders.named("aaa")).thenReturn(aaaProvider);
		when(logProviders.named("bbb")).thenReturn(bbbProvider);
		when(aaaProvider.get()).thenReturn(new CheckstyleLogParser());
		when(bbbProvider.get()).thenReturn(bbbParser);
		when(bbbParser.parse(any(Reader.class))).thenAnswer(new Answer<List<Warning>>() {
			@Override
			public List<Warning> answer(InvocationOnMock invocation)
					throws Throwable {
				Reader r = invocation.getArgumentAt(0, Reader.class);
				BufferedReader br = new BufferedReader(r);
				
				ArrayList<Warning> warnings = new ArrayList<Warning>();
				String line;
				int i = 1;
				while ((line = br.readLine()) != null) {
					warnings.add(new Warning(null, null, "/a/b/c", i, "bbb", line));
					i++;
				}
				return warnings;
			}
		});
		
		TravisResource tr = new TravisResource(null, warningService, logProviders);
		TravisPayload payload = new TravisPayload();
		payload.repository = new TravisRepository();
		payload.repository.name = "repo";
		payload.repository.owner_name = "owner";
		payload.commit = "commit_sha";
		
		InputStream input = new ByteArrayInputStream((
				"travis stuff" + LINE_SEPARATOR +
				"== AAA_RESULT ==" + LINE_SEPARATOR +
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + LINE_SEPARATOR +
					"<checkstyle version=\"5.8\">" + LINE_SEPARATOR +
						"<file name=\"/home/travis/build/owner/repo/a/b/c\"><error line=\"1\" severity=\"error\" message=\"aaa_message_1\" source=\"com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocPackageCheck\"/></file>" + LINE_SEPARATOR +
					"</checkstyle>" + LINE_SEPARATOR +
				"== END_AAA_RESULT ==" + LINE_SEPARATOR +
				"== BBB_RESULT ==" + LINE_SEPARATOR +
				"bbb_message_1" + LINE_SEPARATOR +
				"bbb_message_2" + LINE_SEPARATOR +
				"== END_BBB_RESULT ==" + LINE_SEPARATOR +
				"travis stuff" + LINE_SEPARATOR +
						"").getBytes());
		
		tr.parseLog(payload, input);
		
		verify(warningService, times(1)).addWarningIfNew("owner/repo", "commit_sha", "a/b/c", 1, "checkstyle", "aaa_message_1");
		verify(warningService, times(1)).addWarningIfNew("owner/repo", "commit_sha", "/a/b/c", 1, "bbb", "bbb_message_1");
		verify(warningService, times(1)).addWarningIfNew("owner/repo", "commit_sha", "/a/b/c", 2, "bbb", "bbb_message_2");
		verifyNoMoreInteractions(warningService);
	}
}
