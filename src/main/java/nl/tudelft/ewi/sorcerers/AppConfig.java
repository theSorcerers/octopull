package nl.tudelft.ewi.sorcerers;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import nl.tudelft.ewi.sorcerers.github.LineMapService;
import nl.tudelft.ewi.sorcerers.infrastructure.JPAWarningRepository;
import nl.tudelft.ewi.sorcerers.model.WarningRepository;
import nl.tudelft.ewi.sorcerers.model.WarningService;
import nl.tudelft.ewi.sorcerers.servlet.BaseURIFilter;
import nl.tudelft.ewi.sorcerers.servlet.CORSResponseFilter;
import nl.tudelft.ewi.sorcerers.servlet.GitHubOAuthFilter;
import nl.tudelft.ewi.sorcerers.usecases.GetWarningsForCommit;

import org.eclipse.egit.github.core.service.CommitService;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class AppConfig extends ResourceConfig {
	@Inject
	public AppConfig(ServiceLocator serviceLocator) {
		packages("nl.tudelft.ewi.sorcerers.resources");

		System.out.println("Registering injectables...");
		
		register(BaseURIFilter.class);
		register(JacksonJaxbJsonProvider.class);
		register(CORSResponseFilter.class);
		register(GitHubOAuthFilter.class);
		register(RolesAllowedDynamicFeature.class);
		register(ForbiddenExceptionMapper.class);
		
		final String githubToken = System.getenv("GITHUB_TOKEN");
		if (githubToken == null) {
			throw new IllegalArgumentException("GITHUB_TOKEN is missing.");
		}
		
		final String githubClientId = System.getenv("GITHUB_CLIENT_ID");
		if (githubClientId == null) {
			throw new IllegalArgumentException("GITHUB_CLIENT_ID is missing.");
		}
		
		final String githubClientSecret = System.getenv("GITHUB_CLIENT_SECRET");
		if (githubClientSecret == null) {
			throw new IllegalArgumentException("GITHUB_CLIENT_SECRET is missing.");
		}
		
		register(new AbstractBinder() {
			@Override
			protected void configure() {	
				bind(githubToken).named("env:GITHUB_TOKEN").to(String.class);
				bind(githubClientId).named("env:GITHUB_CLIENT_ID").to(String.class);
				bind(githubClientSecret).named("env:GITHUB_CLIENT_SECRET").to(String.class);
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
		
		register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(CheckstyleLogParser.class).named("checkstyle").to(LogParser.class);
				bind(PMDLogParser.class).named("pmd").to(LogParser.class);
			}
		});
	}
}
