package nl.tudelft.ewi.sorcerers;

import javax.inject.Inject;
import javax.inject.Named;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerResolver implements InjectionResolver<Inject> {
	@Inject
	@Named(InjectionResolver.SYSTEM_RESOLVER_NAME)
	private InjectionResolver<Inject> systemResolver;

	@Override
	public Object resolve(Injectee injectee, ServiceHandle<?> root) {
		if (Logger.class.equals(injectee.getRequiredType())) {
			return LoggerFactory.getLogger(injectee.getInjecteeClass());
		} else {
			return systemResolver.resolve(injectee, root);
		}
	}

	@Override
	public boolean isConstructorParameterIndicator() {
		return false;
	}

	@Override
	public boolean isMethodParameterIndicator() {
		return false;
	}
}