package org.mifos.framework.business.service;

import java.util.HashMap;
import java.util.Map;

import org.mifos.framework.exceptions.ServiceException;
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
	
	public BusinessService getBusinessService(BusinessServiceName key)throws ServiceException{
		if (!businessServicesMap.containsKey(key)){
			try{
				businessServicesMap.put(key,(BusinessService)Class.forName(key.getName()).newInstance());
			}catch (ClassNotFoundException cnfe) {
				throw new ServiceException(ExceptionConstants.SERVICEEXCEPTION,cnfe);
			}
			catch (InstantiationException ie) {
				throw new ServiceException(ExceptionConstants.SERVICEEXCEPTION,ie);
			}
			catch (IllegalAccessException iae) {
				throw new ServiceException(ExceptionConstants.SERVICEEXCEPTION,iae);
			}
		}
		return businessServicesMap.get(key);
	}
	public PersistenceService getPersistenceService(PersistenceServiceName key)throws ServiceException{
		if (!persistenceServicesMap.containsKey(key)){
			try{
				persistenceServicesMap.put(key,(PersistenceService)Class.forName(key.getName()).newInstance());
			}catch (ClassNotFoundException cnfe) {
				throw new ServiceException(ExceptionConstants.SERVICEEXCEPTION,cnfe);
			}
			catch (InstantiationException ie) {
				throw new ServiceException(ExceptionConstants.SERVICEEXCEPTION,ie);
			}
			catch (IllegalAccessException iae) {
				throw new ServiceException(ExceptionConstants.SERVICEEXCEPTION,iae);
			}
		}
		return persistenceServicesMap.get(key);
	}

}
