package nl.tudelft.ewi.sorcerers;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.ConstructorInvocation;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class TransactionInterceptor implements ConstructorInterceptor, MethodInterceptor {
	private EntityManager entityManager;

	@Inject
	public TransactionInterceptor(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		EntityTransaction transaction = this.entityManager.getTransaction();
		try {
			transaction.begin();
			Object ret = invocation.proceed();
			transaction.commit();
			return ret;
		} finally {
			if (transaction.isActive()) {
				transaction.rollback();
			}
		}
	}

	@Override
	public Object construct(ConstructorInvocation invocation) throws Throwable {
		EntityTransaction transaction = this.entityManager.getTransaction();
		try {
			transaction.begin();
			Object ret = invocation.proceed();
			transaction.commit();
			return ret;
		} finally {
			transaction.rollback();
		}
	}

}
