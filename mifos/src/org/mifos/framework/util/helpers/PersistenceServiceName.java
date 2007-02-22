package org.mifos.framework.util.helpers;

import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.persistence.service.PersistenceService;

/**
 * This class is deprecated, because {@link PersistenceService} is.
 * In addition, there's no reason to construct an instance via
 * {@link ServiceFactory} rather than just calling a constructor.
 */
public enum PersistenceServiceName {
	MasterDataService("org.mifos.application.master.persistence.service.MasterPersistenceService"), 
	BulkEntryPersistanceService("org.mifos.application.bulkentry.persistance.service.BulkEntryPersistanceService"),
	Configuration("org.mifos.framework.components.configuration.persistence.service.ConfigurationPersistenceService");

	String name;

	PersistenceServiceName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
