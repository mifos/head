package org.mifos.framework.business.service;

import java.util.HashMap;
import java.util.Map;

import org.mifos.framework.exceptions.ServiceUnavailableException;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.ExceptionConstants;

public class ServiceFactory {

	private ServiceFactory() {
	}

	private static ServiceFactory instance = new ServiceFactory();

	private Map<BusinessServiceName, BusinessService> businessServicesMap = new HashMap<BusinessServiceName, BusinessService>();

	public static ServiceFactory getInstance() {
		return instance;
	}

	/**
	 * This method is deprecated.
	 * 
	 * Instead just call the constructor of the business service directly.
	 * for example, new ConfigurationBusinessService();
	 */
	public BusinessService getBusinessService(BusinessServiceName key)
			throws ServiceUnavailableException {
		if (!businessServicesMap.containsKey(key)) {
			try {
				businessServicesMap.put(key, (BusinessService) Class.forName(
						key.getName()).newInstance());
			} catch (Exception cnfe) {
				throw new ServiceUnavailableException(
						ExceptionConstants.SERVICEEXCEPTION, cnfe);
			}
		}
		return businessServicesMap.get(key);
	}

}
