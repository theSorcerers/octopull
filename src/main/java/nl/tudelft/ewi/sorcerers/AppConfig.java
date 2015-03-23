package nl.tudelft.ewi.sorcerers;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import nl.tudelft.ewi.sorcerers.github.LineMapService;
import nl.tudelft.ewi.sorcerers.infrastructure.JPAWarningRepository;
import nl.tudelft.ewi.sorcerers.model.WarningRepository;
import nl.tudelft.ewi.sorcerers.model.WarningService;
import nl.tudelft.ewi.sorcerers.servlet.CORSResponseFilter;
import nl.tudelft.ewi.sorcerers.usecases.GetWarningsForCommit;

import org.eclipse.egit.github.core.service.CommitService;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class AppConfig extends ResourceConfig {
	@Inject
	public AppConfig(ServiceLocator serviceLocator) {
		packages("nl.tudelft.ewi.sorcerers.resources");

		System.out.println("Registering injectables...");
		
		register(JacksonJaxbJsonProvider.class);
		register(CORSResponseFilter.class);
		
		final String githubToken = System.getenv("GITHUB_TOKEN");
		if (githubToken == null) {
			throw new IllegalArgumentException("GITHUB_TOKEN is missing.");
		}
		
		register(new AbstractBinder() {
			@Override
			protected void configure() {	
				bind(githubToken).named("env:GITHUB_TOKEN").to(String.class);
				bindAsContract(TravisService.class);
			}
		});
				
		
		final String postgresUrl = System.getenv("POSTGRES_URL");
		if (postgresUrl == null) {
			throw new IllegalArgumentException("POSTGRES_URL is missing.");
		}
		
		register(new AbstractBinder() {
			@Override
			protected void configure() {	
				bind(postgresUrl).named("env:POSTGRES_URL").to(String.class);
				
				bindFactory(HKEntityManagerFactoryFactory.class).to(EntityManagerFactory.class).in(Singleton.class);
				bindFactory(HKEntityManagerFactory.class).to(EntityManager.class).in(RequestScoped.class);
				bind(TransactionInterceptionService.class).to(InterceptionService.class).in(Singleton.class);
				bind(JPAWarningRepository.class).to(WarningRepository.class).in(RequestScoped.class);
			}
		});

		register(new AbstractBinder() {
			@Override
			protected void configure() {
				bindAsContract(GetWarningsForCommit.class);
				bindAsContract(WarningService.class);
			}
		});
		
		register(new AbstractBinder() {
			@Override
			protected void configure() {
				bindAsContract(CommitService.class);
				bindAsContract(LineMapService.class);
			}
		});
	}
}
