package nl.tudelft.ewi.sorcerers;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.glassfish.jersey.servlet.ServletContainer;

public class Main {

	public static void main(String[] args) throws Exception {
		System.out.println("Starting server on port 8080");

		// Create the server
		Server server = new Server(8080);

		// Create a servlet context and add the jersey servlet
		ServletContextHandler sch = new ServletContextHandler(server, "/");

		FilterHolder servletContainer = new FilterHolder(ServletContainer.class);
		servletContainer.setInitParameter("javax.ws.rs.Application", AppConfig.class.getCanonicalName());
		sch.addFilter(servletContainer, "/*", null);

		// Must add DefaultServlet for embedded Jetty.
		// Failing to do this will cause 404 errors.
		sch.addServlet(DefaultServlet.class, "/");

		// Start the server
		server.start();
		
		// Wait for server to shutdown
		server.join();
	}

}
