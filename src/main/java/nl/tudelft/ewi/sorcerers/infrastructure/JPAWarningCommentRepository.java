package nl.tudelft.ewi.sorcerers.infrastructure;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import nl.tudelft.ewi.sorcerers.model.WarningComment;
import nl.tudelft.ewi.sorcerers.model.WarningCommentRepository;

public class JPAWarningCommentRepository implements WarningCommentRepository {
	private EntityManager entityManager;

	@Inject
	public JPAWarningCommentRepository(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public WarningComment add(WarningComment warningComment) {
		entityManager.persist(warningComment);
		return warningComment;
	}
}
