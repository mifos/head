package org.mifos.application.office.business.service;

import java.util.List;

import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;

public class OfficeBusinessService extends BusinessService {

	private OfficePersistence officePersistence  = new OfficePersistence();
	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}
	
	public List<OfficeView> getActiveParents(OfficeLevel level,Short localeId){
		return officePersistence.getActiveParents(level,localeId);
	}
	public List <OfficeView> getConfiguredLevels(Short localeId){
		return officePersistence.getActiveLevels(localeId);
		
	}
	public OfficeBO getOffice(Short officeId) throws ServiceException{
		try {
			return officePersistence.getOffice(officeId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	public List<OfficeView> getStatusList(Short localeId){
		return officePersistence.getStatusList(localeId);
	}
	public List<OfficeBO> getOfficesTillBranchOffice(){
		return officePersistence.getOfficesTillBranchOffice();
	}
	public List<OfficeBO> getBranchOffices(){
		return officePersistence.getBranchOffices();
	}

	public List<OfficeView> getChildOffices(String searchId) {
		return officePersistence.getChildOffices(searchId);
	}

}
