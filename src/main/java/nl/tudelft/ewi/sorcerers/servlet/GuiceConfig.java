//package nl.tudelft.ewi.sorcerers.servlet;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//
//import nl.tudelft.ewi.sorcerers.core.MyAppModule;
//import nl.tudelft.ewi.sorcerers.github.GitHubModule;
//import nl.tudelft.ewi.sorcerers.infrastructure.JPAWarningRepository;
//import nl.tudelft.ewi.sorcerers.model.WarningRepository;
//import nl.tudelft.ewi.sorcerers.resources.FooResource;
//
//import com.google.inject.AbstractModule;
//import com.google.inject.Guice;
//import com.google.inject.Injector;
//import com.google.inject.persist.PersistFilter;
//import com.google.inject.persist.jpa.JpaPersistModule;
//import com.google.inject.servlet.GuiceServletContextListener;
//
//public class GuiceConfig extends GuiceServletContextListener {
//
//	@Override
//	protected Injector getInjector() {
//		return Guice.createInjector(new AbstractModule() {
//			@Override
//			protected void configure() {
//				install(new MyAppModule());
//				install(new GitHubModule());
//				
//				bind(FooResource.class);
//				
//				String postgresHost = System.getenv("POSTGRES_HOST");
//				Integer postgresPort = Integer.valueOf(System.getenv("POSTGRES_PORT"));
//				String postgresDb = System.getenv("POSTGRES_DB");
//				String postgresUser = System.getenv("POSTGRES_USER");
////				String postgresPass = System.getenv("POSTGRES_PASS");
//				
//				Properties persistanceProperties = new Properties();
//				persistanceProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
//				// WARNING: temporary
//				persistanceProperties.put("javax.persistence.jdbc.url", String.format("jdbc:postgresql://%s:%d/%s", postgresHost, postgresPort, postgresDb));
//				persistanceProperties.put("javax.persistence.jdbc.user", postgresUser);
//				persistanceProperties.put("hibernate.hbm2ddl.auto", "update");
//				persistanceProperties.put("hibernate.show_sql", "true");
//				persistanceProperties.put("hibernate.c3p0.acquireRetryAttempts", 0);
//				persistanceProperties.put("hibernate.c3p0.acquireRetryDelay",
//						3000);
//				persistanceProperties.put(
//						"hibernate.c3p0.breakAfterAcquireFailure", false);
//				persistanceProperties.put("hibernate.c3p0.maxConnectionAge",
//						6000);
//				persistanceProperties.put("hibernate.c3p0.maxIdleTime", 6000);
//				persistanceProperties.put(
//						"hibernate.c3p0.maxIdleTimeExcessConnections", 1800);
//				persistanceProperties.put("hibernate.c3p0.idleConnectionTestPeriod", 3600);
//
//				install(new JpaPersistModule("octopull-jpa").properties(persistanceProperties));
//				bind(WarningRepository.class).to(JPAWarningRepository.class);
//				
//				// Set init params for Jersey
//				Map<String, String> params = new HashMap<String, String>();
//				params.put("com.sun.jersey.config.property.packages", "nl.tudelft.ewi.sorcerers.resources");
//				params.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
//
////				filter("/*").through(PersistFilter.class);
//				// Route all requests through GuiceContainer
//				serve("/*").with(GuiceContainer.class, params);
//			}
//		});
//	}
//
//}
