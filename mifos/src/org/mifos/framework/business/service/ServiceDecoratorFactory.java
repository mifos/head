package org.mifos.framework.business.service;

import java.lang.reflect.Proxy;

import org.mifos.application.reports.business.service.ICollectionSheetReportService;
import org.mifos.application.reports.business.service.IReportsParameterService;
import org.mifos.framework.components.logger.ServiceLogger;

public class ServiceDecoratorFactory {
	private static Object newInstance(Object obj, ServiceLogger logger) {
		return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj
				.getClass().getInterfaces(), new ServiceProxy(obj
				, logger));
	}

	public static ICollectionSheetReportService decorate(
			ICollectionSheetReportService service, ServiceLogger logger) {
		return (ICollectionSheetReportService) newInstance(service, logger);
	}

	public static IReportsParameterService decorate(
			IReportsParameterService service) {
		return (IReportsParameterService) newInstance(service, ServiceLogger.MUTED_LOGGER);
	}
	
	//  this generic implementation is not working with spring container, not sure why, but would love to have 
	//	this in place instead of the above specific method	
	//	public static<T> T getLoggerWrappedService(
	//			T service) {
	//		return (T) newInstance(service);
	//	}
}
