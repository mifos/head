package org.mifos.application.configuration.business.service;

import java.util.List;

import org.mifos.application.configuration.persistence.ConfigurationPersistence;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.security.util.UserContext;

public class ConfigurationBusinessService extends BusinessService {

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}

	public List<MifosLookUpEntity> getLookupEntities() {
		return new ConfigurationPersistence().getLookupEntities();
	}

	public List<LookUpValueEntity> getLookupValues() {
		return new ConfigurationPersistence().getLookupValues();
	}
}
