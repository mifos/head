package org.mifos.application.reports.business.service;

import java.util.List;

import org.mifos.application.reports.business.ReportsBO;
import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.reports.persistence.service.ReportsPersistenceService;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class ReportsBusinessService extends BusinessService{
	
	private ReportsPersistenceService reportsPersistenceService;
	
	
	public BusinessObject getBusinessObject(UserContext userContext) {
		return new ReportsBO(userContext);
	}
	
	private ReportsPersistenceService getPersistenceService()throws ServiceException{
		if(reportsPersistenceService==null){
			reportsPersistenceService=(ReportsPersistenceService) ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.Reports);
		}
		return reportsPersistenceService;
	}
	
	public List<ReportsCategoryBO> getAllReportCategories() throws ServiceException{
			return getPersistenceService().getAllReportCategories();
	}

}
