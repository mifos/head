/**
 * 
 */
package org.mifos.application.office.persistence.service;

import java.util.List;

import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.service.PersistenceService;
import org.mifos.framework.security.util.OfficeCacheView;

public class OfficePersistenceService extends PersistenceService {

	private OfficePersistence serviceImpl = new OfficePersistence();

	public OfficeBO getOffice(Short officeId) throws PersistenceException {
		
			return serviceImpl.getOffice(officeId);
	
	}
	public List<OfficeView> getActiveBranches(Short officeId) throws PersistenceException {
		return serviceImpl.getActiveOffices(officeId);
	}
	public OfficeBO getHeadOffice() throws PersistenceException {
		return serviceImpl.getHeadOffice();
	}
	public List<OfficeCacheView> getAllOffices()throws PersistenceException{
		return serviceImpl.getAllOffices();
	}
	public Short getMaxOfficeId() throws PersistenceException{
		return serviceImpl.getMaxOfficeId();
	}
	public Integer getChildCount(Short officeId) throws PersistenceException{
		return serviceImpl.getChildCount(officeId);
	}
}
