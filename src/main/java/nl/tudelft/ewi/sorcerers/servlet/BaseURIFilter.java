package nl.tudelft.ewi.sorcerers.servlet;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.UriBuilder;

@PreMatching
public class BaseURIFilter implements ContainerRequestFilter {
	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		String scheme = requestContext.getHeaderString("X-Forwarded-Proto");
		String path = requestContext.getHeaderString("X-Forwarded-Path");

		UriBuilder baseBuilder = requestContext.getUriInfo().getBaseUriBuilder();
		UriBuilder requestBuilder = requestContext.getUriInfo().getRequestUriBuilder();
		if (scheme != null) {
			baseBuilder.scheme(scheme);
			requestBuilder.scheme(scheme);
		}
		if (path != null) {
			baseBuilder.path(path);
		}
		requestContext.setRequestUri(baseBuilder.build(), requestBuilder.build());
	}
}