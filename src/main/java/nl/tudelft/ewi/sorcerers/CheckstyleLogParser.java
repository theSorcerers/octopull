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

@Service
public class CheckstyleLogParser implements LogParser {
	private static class CheckStyleError {
		@JacksonXmlProperty(isAttribute = true)
		public int line;
		@JacksonXmlProperty(isAttribute = true)
		public String severity;
		@JacksonXmlProperty(localName = "message", isAttribute = true)
		public String message;
		@JacksonXmlProperty(isAttribute = true)
		public String source;
	}
	
	private static class CheckStyleFile {
		@JacksonXmlProperty(localName = "name", isAttribute = true)
		public String name;
		@JacksonXmlProperty(localName = "error")
		@JacksonXmlElementWrapper(useWrapping = false)
		public List<CheckStyleError> errors;
	}

	@JacksonXmlRootElement(localName="checkstyle")
	private static class CheckStyleReport {
		@JacksonXmlProperty(localName = "version", isAttribute = true)
		public String version;
		@JacksonXmlProperty(localName = "file")
		@JacksonXmlElementWrapper(useWrapping = false)
		public List<CheckStyleFile> files;
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
			CheckStyleReport report = mapper.readValue(bufferedReader, CheckStyleReport.class);
			for (CheckStyleFile file : report.files) {
				String path = file.name;
				if (file.errors != null) {
					for (CheckStyleError error : file.errors) {
						warnings.add(new Warning(null, null, path, error.line, error.message));
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