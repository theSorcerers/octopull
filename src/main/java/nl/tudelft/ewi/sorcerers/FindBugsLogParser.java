package nl.tudelft.ewi.sorcerers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import nl.tudelft.ewi.sorcerers.model.Warning;

import org.jvnet.hk2.annotations.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@Service
public class FindBugsLogParser implements LogParser {
	private static class SourceLine {
		@JacksonXmlProperty(localName = "primary", isAttribute = true)
		boolean primary;
		@JacksonXmlProperty(localName = "start", isAttribute = true)
		int start;
		@JacksonXmlProperty(localName = "sourcepath", isAttribute = true)
		String sourcePath;
	}
	
	private static class BugInstance {
		@JacksonXmlProperty(localName = "LongMessage")
		String message;
		@JacksonXmlProperty(localName = "SourceLine")
		@JacksonXmlElementWrapper(useWrapping = false)
		public List<SourceLine> lines;
	}

	private static class Project {
		@JacksonXmlProperty(localName = "SrcDir")
		String sourceDir;
	}
	
	@JacksonXmlRootElement(localName="BugCollection")
	private static class BugCollection {
		@JacksonXmlProperty(localName = "Project")
		public Project project;
		@JacksonXmlProperty(localName = "BugInstance")
		@JacksonXmlElementWrapper(useWrapping = false)
		public List<BugInstance> bugs;
	}

	@Override
	public List<Warning> parse(Reader reader) {
		List<Warning> warnings = new ArrayList<Warning>();
		
		// TODO temporary
		ObjectMapper mapper = new XmlMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		AnnotationIntrospector introspector = new JacksonXmlAnnotationIntrospector();
		// make deserializer use JAXB annotations (only)
		mapper.getDeserializationConfig().withAppendedAnnotationIntrospector(introspector);
		
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(reader);
			BugCollection report = mapper.readValue(bufferedReader, BugCollection.class);
			if (report.bugs != null) {
				String srcDir = "";
				if (report.project != null && report.project.sourceDir != null) {
					srcDir = report.project.sourceDir;
				}
				for (BugInstance bug : report.bugs) {
					if (bug.lines != null && bug.lines.size() > 0) {					
						SourceLine line = null;
						for (SourceLine l : bug.lines) {
							if (l.primary) {
								line = l;
							}
						}
						if (line == null) {
							bug.lines.get(0);
						}
						
						warnings.add(new Warning(null, null, srcDir + "/" + line.sourcePath, line.start, "findbugs", bug.message));
					}
				}
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return warnings;
	}
}