package org.mifos.application.office.business.service;

import java.util.List;

import org.hibernate.Session;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.hibernate.helper.HibernateUtil;
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
	public OfficeBO getOffice(Short officeId) {
		return officePersistence.getOffice(officeId);
	}
}
