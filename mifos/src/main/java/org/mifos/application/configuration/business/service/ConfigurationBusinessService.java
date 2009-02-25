package org.mifos.application.configuration.business.service;

import java.util.List;

import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.configuration.business.ConfigurationKeyValueInteger;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;


public class ConfigurationBusinessService implements BusinessService {

	private final ConfigurationPersistence configurationPersistence;

	ConfigurationBusinessService(ConfigurationPersistence configurationPersistence) {
		this.configurationPersistence = configurationPersistence;
	}

	public ConfigurationBusinessService() {
		this(new ConfigurationPersistence());
	}

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}

	public List<ConfigurationKeyValueInteger> getConfiguration() throws ServiceException{
		try {
			return configurationPersistence.getAllConfigurationKeyValueIntegers();
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	
	public boolean isGlimEnabled() throws ServiceException {
		try {
			return configurationPersistence.isGlimEnabled();
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	
	public boolean isRepaymentIndepOfMeetingEnabled() throws ServiceException {
		try {
			return new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	
}

