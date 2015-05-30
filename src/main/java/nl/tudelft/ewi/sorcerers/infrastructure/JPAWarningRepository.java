package nl.tudelft.ewi.sorcerers.infrastructure;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import nl.tudelft.ewi.sorcerers.model.Warning;
import nl.tudelft.ewi.sorcerers.model.WarningId;
import nl.tudelft.ewi.sorcerers.model.WarningRepository;

public class JPAWarningRepository implements WarningRepository {
	private EntityManager entityManager;

	@Inject
	public JPAWarningRepository(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public Warning add(Warning warning) {
		entityManager.persist(warning);
		return warning;
	}
	
	@Override
	public Warning get(String repo, String commit, Integer warningId) {
		return this.entityManager.find(Warning.class, new WarningId(repo, commit, warningId));
	}
	
	@Override
	public Warning find(String repo, String commit, String path, int line,
			String tool, String message) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Warning> cq = cb.createQuery(Warning.class);
		
		Root<Warning> warning = cq.from(Warning.class);
		cq.where(cb.and(
					cb.equal(warning.get("repo"), repo),
					cb.equal(warning.get("commit"), commit),
					cb.equal(warning.get("path"), path),
					cb.equal(warning.get("line"), line),
					cb.equal(warning.get("tool"), tool),
					cb.equal(warning.get("message"), message)
				)
		);
		
		TypedQuery<Warning> query = entityManager.createQuery(cq);
		try {
			return query.getSingleResult();
		} catch(NoResultException e) {
			return null;
		}
	}
	
	@Override
	public List<Warning> getWarningsForCommit(String repo, String commit) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Warning> cq = cb.createQuery(Warning.class);

		Root<Warning> warning = cq.from(Warning.class);
		cq.where(cb.and(cb.equal(warning.get("repo"), repo),
				cb.equal(warning.get("commit"), commit)));
		
		TypedQuery<Warning> query = entityManager.createQuery(cq);
		return query.getResultList();
	}
}
