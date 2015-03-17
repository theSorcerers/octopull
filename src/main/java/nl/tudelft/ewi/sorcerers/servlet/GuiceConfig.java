package nl.tudelft.ewi.sorcerers.servlet;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import nl.tudelft.ewi.sorcerers.core.MyAppModule;
import nl.tudelft.ewi.sorcerers.github.GitHubModule;
import nl.tudelft.ewi.sorcerers.infrastructure.JPAWarningRepository;
import nl.tudelft.ewi.sorcerers.model.WarningRepository;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class GuiceConfig extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new JerseyServletModule() {
			@Override
			protected void configureServlets() {
				install(new MyAppModule());
				install(new GitHubModule());
				
				String postgresHost = System.getenv("POSTGRES_HOST");
				Integer postgresPort = Integer.valueOf(System.getenv("POSTGRES_PORT"));
				String postgresDb = System.getenv("POSTGRES_DB");
				String postgresUser = System.getenv("POSTGRES_USER");
//				String postgresPass = System.getenv("POSTGRES_PASS");
				
				Properties persistanceProperties = new Properties();
				persistanceProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
				// WARNING: temporary
				persistanceProperties.put("javax.persistence.jdbc.url", String.format("jdbc:postgresql://%s:%d/%s", postgresHost, postgresPort, postgresDb));
				persistanceProperties.put("javax.persistence.jdbc.user", postgresUser);
				persistanceProperties.put("hibernate.hbm2ddl.auto", "update");
				persistanceProperties.put("hibernate.show_sql", "true");
								
				install(new JpaPersistModule("octopull-jpa").properties(persistanceProperties));
				bind(WarningRepository.class).to(JPAWarningRepository.class);
				
				// Set init params for Jersey
				Map<String, String> params = new HashMap<String, String>();
				params.put("com.sun.jersey.config.property.packages", "nl.tudelft.ewi.sorcerers.resources");
				params.put("com.sun.jersey.api.json.POJOMappingFeature", "true");

				// Route all requests through GuiceContainer
				filter("/*").through(PersistFilter.class);
				serve("/*").with(GuiceContainer.class, params);
			}
		});
	}

}
