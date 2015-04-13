package nl.tudelft.ewi.sorcerers.servlet;

import java.io.IOException;
import java.net.URI;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

@Priority(Priorities.AUTHENTICATION - 1)
@PreMatching
public class BaseURIFilter implements ContainerRequestFilter {
	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		String scheme = requestContext.getHeaderString("X-Forwarded-Proto");
		String path = requestContext.getHeaderString("X-Forwarded-Path");

		UriInfo uriInfo = requestContext.getUriInfo();
		UriBuilder baseBuilder = uriInfo.getBaseUriBuilder();
		URI requestUri = uriInfo.getBaseUri().relativize(uriInfo.getRequestUri());
		if (scheme != null) {
			baseBuilder.scheme(scheme);
		}
		if (path != null) {
			if (!path.endsWith("/"))
				path += "/";
			baseBuilder.path(path);
		}
		URI baseURI = baseBuilder.build();
		requestContext.setRequestUri(baseURI, baseURI.resolve(requestUri));
	}
}