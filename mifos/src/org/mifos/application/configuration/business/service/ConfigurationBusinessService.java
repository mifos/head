package org.mifos.application.configuration.business.service;

import java.util.List;

import org.mifos.application.configuration.persistence.ApplicationConfigurationPersistence;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.persistence.FieldConfigurationPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;

public class ConfigurationBusinessService extends BusinessService {

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}

	public List<MifosLookUpEntity> getLookupEntities() {
		return new ApplicationConfigurationPersistence().getLookupEntities();
	}

	public List<LookUpValueEntity> getLookupValues() {
		return new ApplicationConfigurationPersistence().getLookupValues();
	}
	
	public List<FieldConfigurationEntity> getAllConfigurationFieldList() 
	throws ServiceException {
		try {
			return new FieldConfigurationPersistence().getAllConfigurationFieldList();
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
}
