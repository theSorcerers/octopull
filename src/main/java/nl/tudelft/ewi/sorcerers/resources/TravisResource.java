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

import javax.annotation.security.RolesAllowed;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

@Path("/travis")
public class TravisResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(TravisResource.class);
	
	private static final Pattern RESULT_PATTERN = Pattern.compile("^== ([_A-Z]+)_RESULT ==$");
	private static final Pattern COMMIT_PATTERN = Pattern.compile("^OCTOPULL_SHA=([0-9a-z]+)$");
	private TravisService travisService;
	private WarningService warningService;
	private IterableProvider<LogParser> logParsers;
	
	@Inject
	public TravisResource(TravisService travisService, WarningService warningService, IterableProvider<LogParser> logParsers) {
		this.travisService = travisService;
		this.warningService = warningService;
		this.logParsers = logParsers;
	}
	
	@RolesAllowed("user")
	@GET
	@Path("/webhook")
	public Response test() {
		return webhook("{\"commit\": \"123\", \"build_url\": \"https://travis-ci.org/svenfuchs/minimal/builds/1\", \"repository\": { \"owner_name\": \"rmhartog\", \"name\": \"octopull\" }, \"matrix\": [{ \"id\": 16181256 }] }");
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
			
			String host = "travis-ci.org";
			Pattern buildPattern = Pattern.compile("^http(s?)://([~/]+)/(.*)$");
			if (travisPayload.build_url != null) {
				Matcher matcher = buildPattern.matcher(travisPayload.build_url);
				if (matcher.matches()) {
					host = matcher.group(1);
				}
			}
			
			for (TravisJobPayload job : travisPayload.matrix) {
				InputStream log = null;
				try {
					log = this.travisService.getLogFromJobId(host, job.id);
					if (log != null) {
						parseLog(travisPayload, log);
					}
				} catch (Exception e) {
					LOGGER.error(String.format("Unable to retrieve log from Travis for job %s of repo %s/%s",
							job.id, travisPayload.repository.owner_name, travisPayload.repository.name), e);
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

	public void parseLog(TravisPayload travisPayload, InputStream log) {
		String repo = String.format("%s/%s", travisPayload.repository.owner_name, travisPayload.repository.name);
		String commit = travisPayload.commit;

		BufferedInputStream logStream = new BufferedInputStream(log);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(logStream, StandardCharsets.UTF_8));

			Pattern commitPattern = COMMIT_PATTERN;
			Pattern resultPattern = RESULT_PATTERN;
			String line;
			while ((line = reader.readLine()) != null){
				Matcher commitMatcher = commitPattern.matcher(line);
				if (commitMatcher.matches()) {
					commit = commitMatcher.group(1);
				} else {
					Matcher resultMatcher = resultPattern.matcher(line);
					if (resultMatcher.matches()) {
						String tool = resultMatcher.group(1);
						LOGGER.debug("Found section for tool '"+ tool + "'");
						Reader r = new ReadUntilReader(reader, ("== END_" + tool + "_RESULT ==").toCharArray());
						LogParser parser = logParsers.named(tool.toLowerCase()).get();
						if (parser != null) {
							for (Warning warning : parser.parse(r)) {
								String path = warning.getPath().replaceAll("^/home/travis/build/" + repo + "/", "");
								this.warningService.addWarningIfNew(repo, commit, path, warning.getLine(), warning.getTool(), warning.getMessage());
							}
						}
						r.close();
					}
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
	static class TravisRepository {
		public String name;
		public String owner_name;
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class TravisJobPayload {
		public String id;
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class TravisPayload {
		public String commit;
		public String build_url;
		public TravisRepository repository;
		public List<TravisJobPayload> matrix;
	}
}
