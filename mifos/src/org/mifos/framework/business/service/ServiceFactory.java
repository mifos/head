package org.mifos.framework.business.service;

import java.util.HashMap;
import java.util.Map;

import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.ServiceUnavailableException;
import org.mifos.framework.persistence.service.PersistenceService;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class ServiceFactory {
	
	private ServiceFactory(){}
	private static ServiceFactory instance = new ServiceFactory();
	private Map<BusinessServiceName, BusinessService> businessServicesMap = new HashMap<BusinessServiceName, BusinessService>();
	private Map<PersistenceServiceName, PersistenceService> persistenceServicesMap = new HashMap<PersistenceServiceName, PersistenceService>();
	
	public static ServiceFactory getInstance(){
		return instance;
	}
	
	public BusinessService getBusinessService(BusinessServiceName key)throws ServiceUnavailableException{
		if (!businessServicesMap.containsKey(key)){
			try{
				businessServicesMap.put(key,(BusinessService)Class.forName(key.getName()).newInstance());
			}catch (ClassNotFoundException cnfe) {
				throw new ServiceUnavailableException(ExceptionConstants.SERVICEEXCEPTION,cnfe);
			}
			catch (InstantiationException ie) {
				throw new ServiceUnavailableException(ExceptionConstants.SERVICEEXCEPTION,ie);
			}
			catch (IllegalAccessException iae) {
				throw new ServiceUnavailableException(ExceptionConstants.SERVICEEXCEPTION,iae);
			}
		}
		return businessServicesMap.get(key);
	}
	
	//TODO too be removed
	public PersistenceService getPersistenceService(PersistenceServiceName key)throws ServiceUnavailableException{
		if (!persistenceServicesMap.containsKey(key)){
			try{
				persistenceServicesMap.put(key,(PersistenceService)Class.forName(key.getName()).newInstance());
			}catch (ClassNotFoundException cnfe) {
				throw new ServiceUnavailableException(ExceptionConstants.SERVICEEXCEPTION,cnfe);
			}
			catch (InstantiationException ie) {
				throw new ServiceUnavailableException(ExceptionConstants.SERVICEEXCEPTION,ie);
			}
			catch (IllegalAccessException iae) {
				throw new ServiceUnavailableException(ExceptionConstants.SERVICEEXCEPTION,iae);
			}
		}
		return persistenceServicesMap.get(key);
	}

}
