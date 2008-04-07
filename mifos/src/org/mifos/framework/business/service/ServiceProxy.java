package org.mifos.framework.business.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.mifos.framework.components.logger.ServiceLogger;

public class ServiceProxy implements InvocationHandler {
	private Object service;
	private final ServiceLogger logger;

	ServiceProxy(Object service, ServiceLogger logger) {
		this.service = service;
		this.logger = logger;
	}

	public Object invoke(Object proxy, Method method, Object[] args) {
		Object result = null;
		try {
			logger.startOfServiceCall(method, args);
			result = method.invoke(service, args);
			logger.endOfServiceCall(method, args);
		}
		catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return result;
	}
}
