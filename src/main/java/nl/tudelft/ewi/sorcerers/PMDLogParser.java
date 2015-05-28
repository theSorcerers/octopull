package nl.tudelft.ewi.sorcerers;

import java.io.BufferedReader;
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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

@Service
public class PMDLogParser implements LogParser {
	private static class PMDViolation {
		@JacksonXmlProperty(isAttribute = true)
		public int beginline;
		@JacksonXmlProperty(isAttribute = true)
		public String severity;
		@JacksonXmlText
		public String message;
		@JacksonXmlProperty(isAttribute = true)
		public String rule;
	}
	
	private static class PMDFile {
		@JacksonXmlProperty(localName = "name", isAttribute = true)
		public String name;
		@JacksonXmlProperty(localName = "violation")
		@JacksonXmlElementWrapper(useWrapping = false)
		public List<PMDViolation> violations;
	}

	@JacksonXmlRootElement(localName="checkstyle")
	private static class PMDReport {
		@JacksonXmlProperty(localName = "version", isAttribute = true)
		public String version;
		@JacksonXmlProperty(localName = "file")
		@JacksonXmlElementWrapper(useWrapping = false)
		public List<PMDFile> files;
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
			PMDReport report = mapper.readValue(bufferedReader, PMDReport.class);
			for (PMDFile file : report.files) {
				String path = file.name;
				for (PMDViolation error : file.violations) {
					warnings.add(new Warning(null, null, path, error.beginline, "pmd", error.message));
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
