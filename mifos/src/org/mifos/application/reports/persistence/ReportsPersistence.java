package org.mifos.application.reports.persistence;

import java.util.List;

import org.hibernate.Query;
import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.reports.util.helpers.ReportsConstants;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;

public class ReportsPersistence extends Persistence {	
	
	public ReportsPersistence(){
		
	}	
		
	public List<ReportsCategoryBO> getAllReportCategories()
	{					
		Query query = HibernateUtil.getSessionTL().getNamedQuery(ReportsConstants.GETALLREPORTS);
		return query.list();
	}

}
