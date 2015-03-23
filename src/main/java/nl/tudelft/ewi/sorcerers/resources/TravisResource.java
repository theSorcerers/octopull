package nl.tudelft.ewi.sorcerers.resources;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

import nl.tudelft.ewi.sorcerers.TravisService;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

@Path("/travis")
public class TravisResource {
	private TravisService travisService;

	@Inject
	public TravisResource(TravisService travisService) {
		this.travisService = travisService;
	}
	
	@GET
	@Path("/webhook")
	public Response test() {
		return webhook("{\"commit\": \"123\" }");
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("/webhook")
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
				try {
					System.out.println(this.travisService.getLogFromJobId(job.id));
				} catch (Exception e) {
					System.out.println("failed to get log");
					e.printStackTrace();
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
	
	@XmlRootElement
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class TravisJobPayload {
		public String id;
	}
	
	@XmlRootElement
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class TravisPayload {
		public String commit;
		public List<TravisJobPayload> matrix;
	}
}
