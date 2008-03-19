package org.mifos.framework.business.service;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.mifos.framework.exceptions.ConfigServiceInitializationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.CollectionUtils;
import org.springframework.core.io.Resource;


public abstract class ConfigService {
	private Properties config;
	private static final String CSV_DELIMITER = "\\s*,\\s*";

	public ConfigService(Resource configResource) {
		super();
		initConfig(configResource);
	}

	protected void initConfig(Resource configResource) {
		if (!configResource.exists())
			throw new ConfigServiceInitializationException(
					"Failed to initialize config cervice for resource: "
							+ configResource.getFilename());
		config = new Properties();
		try {
			config.load(configResource.getInputStream());
		}
		catch (IOException e) {
			throw new ConfigServiceInitializationException(
					"Failed to initialize Config service ", e);
		}
	}

	protected String getProperty(String propertyKey) throws ServiceException {
		String propertyValue = config.getProperty(propertyKey);
		if (propertyValue == null)
			throw new ServiceException("Failed to retrieve " + propertyKey
					+ " from config resource");
		return propertyValue;
	}

	protected List<String> getPropertyValues(String key)
			throws ServiceException {
		String[] values = getProperty(key).split(CSV_DELIMITER);
		return CollectionUtils.asList(values);
	}
}
