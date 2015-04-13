package nl.tudelft.ewi.sorcerers.resources;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.tudelft.ewi.sorcerers.LogParser;
import nl.tudelft.ewi.sorcerers.TravisService;
import nl.tudelft.ewi.sorcerers.model.Warning;
import nl.tudelft.ewi.sorcerers.model.WarningService;
import nl.tudelft.ewi.sorcerers.util.ReadUntilReader;

import org.glassfish.hk2.api.IterableProvider;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

@Path("/travis")
public class TravisResource {
	private TravisService travisService;
	private WarningService warningService;
	private IterableProvider<LogParser> logParsers;
	
	@Inject
	public TravisResource(TravisService travisService, WarningService warningService, IterableProvider<LogParser> logParsers) {
		this.travisService = travisService;
		this.warningService = warningService;
		this.logParsers = logParsers;
	}
	
	@GET
	@Path("/webhook")
	public Response test() {
		return webhook("{\"commit\": \"123\", \"repository\": { \"owner_name\": \"rmhartog\", \"name\": \"octopull\" }, \"matrix\": [{ \"id\": 16181256 }] }");
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("/webhook")
	@Transactional
	public Response webhook(@FormParam("payload") String payload) {
		// TODO temporary
		ObjectMapper mapper = new ObjectMapper();
		AnnotationIntrospector introspector = new JacksonAnnotationIntrospector();
		// make deserializer use JAXB annotations (only)
		mapper.getDeserializationConfig().withAppendedAnnotationIntrospector(introspector);
		// make serializer use JAXB annotations (only)
		mapper.getSerializationConfig().withAppendedAnnotationIntrospector(introspector);
		try {
			TravisPayload travisPayload = mapper.readValue(payload, TravisPayload.class);
			System.out.println(travisPayload.commit);
			System.out.println("Looping the matrix");
			for (TravisJobPayload job : travisPayload.matrix) {
				System.out.println(job.id);
				InputStream log = null;
				try {
					log = this.travisService.getLogFromJobId(job.id);
					if (log != null) {
						parseLog(travisPayload, log);
					}
				} catch (Exception e) {
					System.out.println("failed to get log");
					e.printStackTrace();
				} finally {
					if (log != null) {
						log.close();
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
		}
		return Response.ok().build();
	}

	private void parseLog(TravisPayload travisPayload, InputStream log) {
		String repo = String.format("%s/%s", travisPayload.repository.owner_name, travisPayload.repository.name);
		String commit = travisPayload.commit;
		
		BufferedInputStream logStream = new BufferedInputStream(log);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(logStream, StandardCharsets.UTF_8));

			Pattern resultPattern = Pattern.compile("^== ([_A-Z]+)_RESULT ==$");
			String line;
			while ((line = reader.readLine()) != null){
				Matcher matcher = resultPattern.matcher(line);
				if (matcher.matches()) {
					String tool = matcher.group(1);
					Reader r = new ReadUntilReader(reader, ("== END_" + tool + "_RESULT ==").toCharArray());
					LogParser parser = logParsers.named(tool.toLowerCase()).get();
					if (parser != null) {
						System.out.println("parser: " + tool);
						for (Warning warning : parser.parse(r)) {
							String path = warning.getPath().replaceAll("^/home/travis/build/" + repo + "/", "");
							this.warningService.addWarningIfNew(repo, commit, path, warning.getLine(), warning.getMessage());
						}
					}
					r.close();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class TravisRepository {
		public String name;
		public String owner_name;
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class TravisJobPayload {
		public String id;
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class TravisPayload {
		public String commit;
		public TravisRepository repository;
		public List<TravisJobPayload> matrix;
	}
}
