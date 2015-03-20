package nl.tudelft.ewi.sorcerers.servlet;

import java.io.IOException;
import java.util.Arrays;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class CORSResponseFilter implements ContainerResponseFilter {
	@Override
	public void filter(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) throws IOException {
		String origin = requestContext.getHeaderString("Origin");
		if (origin != null) {
			responseContext.getHeaders().put("Access-Control-Allow-Origin", Arrays.asList((Object) origin));
			responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET");
			responseContext.getHeaders().add("Access-Control-Allow-Methods", "HEAD");
			responseContext.getHeaders().add("Access-Control-Allow-Methods", "OPTIONS");
			responseContext.getHeaders().add("Access-Control-Allow-Methods", "POST");
			responseContext.getHeaders().add("Access-Control-Allow-Methods", "PUT");
			responseContext.getHeaders().add("Access-Control-Allow-Methods", "PATCH");
			responseContext.getHeaders().add("Access-Control-Allow-Methods", "DELETE");
			responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
		}
	}
}
