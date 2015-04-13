package nl.tudelft.ewi.sorcerers;

import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.glassfish.hk2.api.Factory;

public class HKEntityManagerFactoryFactory implements Factory<EntityManagerFactory> {
	private String postgresUrl;

	@Inject
	public HKEntityManagerFactoryFactory(@Named("env:POSTGRES_URL") String postgresUrl) {
		this.postgresUrl = postgresUrl;
	}
	
	@Override
	public EntityManagerFactory provide() {
		Properties persistanceProperties = new Properties();
		persistanceProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		// WARNING: temporary
		persistanceProperties.put("javax.persistence.jdbc.url", String.format("jdbc:postgresql://%s", postgresUrl));
		persistanceProperties.put("hibernate.hbm2ddl.auto", "update");
		persistanceProperties.put("hibernate.show_sql", "true");
		persistanceProperties.put("hibernate.c3p0.acquireRetryAttempts", 0);
		persistanceProperties.put("hibernate.c3p0.acquireRetryDelay",
				3000);
		persistanceProperties.put(
				"hibernate.c3p0.breakAfterAcquireFailure", false);
		persistanceProperties.put("hibernate.c3p0.maxConnectionAge",
				6000);
		persistanceProperties.put("hibernate.c3p0.maxIdleTime", 6000);
		persistanceProperties.put(
				"hibernate.c3p0.maxIdleTimeExcessConnections", 1800);
		persistanceProperties.put("hibernate.c3p0.idleConnectionTestPeriod", 3600);
		
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("octopull-jpa", persistanceProperties);
		
		return entityManagerFactory;
	}

	@Override
	public void dispose(EntityManagerFactory instance) {
		if (instance.isOpen()) {
			instance.close();
		}
	}
}
