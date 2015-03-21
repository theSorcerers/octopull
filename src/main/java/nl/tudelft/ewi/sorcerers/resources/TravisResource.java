package nl.tudelft.ewi.sorcerers.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/travis")
public class TravisResource {
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("/webhook")
	public Response webhook(@FormParam("payload") String payload) {
		System.out.println(payload);
		return Response.ok().build();
	}
}
