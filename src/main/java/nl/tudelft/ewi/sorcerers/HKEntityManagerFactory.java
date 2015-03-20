package nl.tudelft.ewi.sorcerers;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.glassfish.hk2.api.Factory;

public class HKEntityManagerFactory implements Factory<EntityManager> {
	private EntityManagerFactory factory;

	@Inject
	public HKEntityManagerFactory(EntityManagerFactory factory) {
		this.factory = factory;
	}
	
	@Override
	public EntityManager provide() {
		return this.factory.createEntityManager();
	}

	@Override
	public void dispose(EntityManager entityManager) {
		if (entityManager.isOpen()) {
			entityManager.close();
		}
	}
}
