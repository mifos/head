package org.mifos.application.reports.persistence.service;

import java.util.List;

import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.reports.persistence.ReportsPersistence;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.persistence.service.PersistenceService;

public class ReportsPersistenceService extends PersistenceService {
	
	private ReportsPersistence reportsPersistence ;
	
	private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);
	
	public ReportsPersistenceService(){
		reportsPersistence = new ReportsPersistence();
	}	
	
	public List<ReportsCategoryBO> getAllReportCategories()
	{
		logger.debug("In getAllReports of ReportPersistenceService ");
		return reportsPersistence.getAllReportCategories();
	}

}
