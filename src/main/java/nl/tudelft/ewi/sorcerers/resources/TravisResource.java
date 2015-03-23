package nl.tudelft.ewi.sorcerers.resources;

import static java.util.regex.Pattern.MULTILINE;

import java.io.IOException;
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
import javax.xml.bind.annotation.XmlRootElement;

import nl.tudelft.ewi.sorcerers.TravisService;
import nl.tudelft.ewi.sorcerers.model.WarningService;

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

	@Inject
	public TravisResource(TravisService travisService, WarningService warningService) {
		this.travisService = travisService;
		this.warningService = warningService;
	}
	
	@GET
	@Path("/webhook")
	public Response test() {
		return webhook("{\"commit\": \"123\" }");
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
				String log = null;
				try {
					log = this.travisService.getLogFromJobId(job.id);
				} catch (Exception e) {
					System.out.println("failed to get log");
					e.printStackTrace();
				}
				if (log != null) {
					Pattern pattern = Pattern.compile("^\\[(WARNING|ERROR|INFO)\\] (.*)\\[([0-9]+)(?::([0-9]+))?\\] \\((.*)\\) ([a-zA-Z]+):\\s+(.*)$", MULTILINE);
					Matcher matcher = pattern.matcher(log);
					while (matcher.find()) {
						if (matcher.groupCount() == 7) {
							String repo = String.format("%s/%s", travisPayload.repository.owner_name, travisPayload.repository.name);
							System.out.println(String.format("%s %s %s %s", repo, travisPayload.commit, matcher.group(2), matcher.group(3)));
							this.warningService.addWarningIfNew(repo, travisPayload.commit, matcher.group(2), Integer.valueOf(matcher.group(3)), matcher.group(7));
						}
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
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class TravisRepository {
		public String name;
		public String owner_name;
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class TravisJobPayload {
		public String id;
	}
	
	@XmlRootElement
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class TravisPayload {
		public String commit;
		public TravisRepository repository;
		public List<TravisJobPayload> matrix;
	}
}
