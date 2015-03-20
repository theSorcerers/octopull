package nl.tudelft.ewi.sorcerers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.jvnet.hk2.annotations.Service;

@Service
public class TransactionInterceptionService implements InterceptionService {
	private Provider<EntityManager> entityManager;

	@Inject
	public TransactionInterceptionService(Provider<EntityManager> em) {
		System.out.println("CREATED "  + em);
		this.entityManager = em;
	}
	
	@Override
	public Filter getDescriptorFilter() {
		return BuilderHelper.allFilter();
	}

	@Override
	public List<MethodInterceptor> getMethodInterceptors(Method method) {
		if (method.isAnnotationPresent(Transactional.class)) {
			return Arrays.asList((MethodInterceptor) new TransactionInterceptor(this.entityManager.get()));
		}
		return null;
	}

	@Override
	public List<ConstructorInterceptor> getConstructorInterceptors(
			Constructor<?> constructor) {
		if (constructor.isAnnotationPresent(Transactional.class)) {
			return Arrays.asList((ConstructorInterceptor) new TransactionInterceptor(this.entityManager.get()));
		}
		return null;
	}

}
