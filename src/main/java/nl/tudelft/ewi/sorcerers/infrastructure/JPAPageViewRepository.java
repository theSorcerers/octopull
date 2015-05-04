package nl.tudelft.ewi.sorcerers.infrastructure;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import nl.tudelft.ewi.sorcerers.model.PageView;
import nl.tudelft.ewi.sorcerers.model.PageViewRepository;

public class JPAPageViewRepository implements PageViewRepository {
	private EntityManager entityManager;

	@Inject
	public JPAPageViewRepository(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public PageView add(PageView pageView) {
		entityManager.persist(pageView);
		return pageView;
	}
}
