package nl.tudelft.ewi.sorcerers;

import static org.glassfish.jersey.CommonProperties.METAINF_SERVICES_LOOKUP_DISABLE;

import java.util.concurrent.ArrayBlockingQueue;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.glassfish.jersey.servlet.ServletContainer;

public class Main {

	public static void main(String[] args) throws Exception {
		System.out.println("Starting server on port 8080");

		
		// Create the server
		ThreadPool threadPool = new QueuedThreadPool(10, 4, 60, new ArrayBlockingQueue<Runnable>(6000));
		Server server = new Server(threadPool);

		ServerConnector connector = new ServerConnector(server);
		connector.setPort(8080);
		server.addConnector(connector);

		// Create a servlet context and add the jersey servlet
		ServletContextHandler sch = new ServletContextHandler(server, "/");

		FilterHolder servletContainer = new FilterHolder(ServletContainer.class);
		servletContainer.setInitParameter("javax.ws.rs.Application",
				AppConfig.class.getCanonicalName());
		servletContainer.setInitParameter(METAINF_SERVICES_LOOKUP_DISABLE,
				"true");
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
