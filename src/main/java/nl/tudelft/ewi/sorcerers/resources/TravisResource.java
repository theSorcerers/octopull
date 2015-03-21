package nl.tudelft.ewi.sorcerers.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/travis")
public class TravisResource {
	@Context ObjectMapper mapper;
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("/webhook")
	public Response webhook(@FormParam("payload") String payload) {
		System.out.println(payload);
		System.out.println(mapper.convertValue(payload, TravisPayload.class));
		return Response.ok().build();
	}
	
	private static class TravisPayload {
		public String commit;
	}
}
