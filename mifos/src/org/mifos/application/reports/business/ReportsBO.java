package org.mifos.application.reports.business;

import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.plugin.helper.EntityMasterConstants;

public class ReportsBO extends BusinessObject {
	
	public ReportsBO(){	
		this.reportsCategoryBO = new ReportsCategoryBO();
	}
	
	public ReportsBO(UserContext userContext) {
		super(userContext);		
	}
	
	private Short reportId;
	private String reportName;
	private String reportIdentifier;
	private ReportsCategoryBO reportsCategoryBO;		
	
	
	public Short getReportId() {
		return reportId;
	}
	public void setReportId(Short reportId) {
		this.reportId = reportId;
	}
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}		

	public String getReportIdentifier() {
		return reportIdentifier;
	}

	public void setReportIdentifier(String reportIdentifier) {
		this.reportIdentifier = reportIdentifier;
	}

	public ReportsCategoryBO getReportsCategoryBO() {
		return reportsCategoryBO;
	}

	public void setReportsCategoryBO(ReportsCategoryBO reportsCategoryBO) {
		this.reportsCategoryBO = reportsCategoryBO;
	}


}
