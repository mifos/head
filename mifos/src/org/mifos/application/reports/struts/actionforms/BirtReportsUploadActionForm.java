package org.mifos.application.reports.struts.actionforms;

import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.reports.business.ReportsCategoryBO;

public class BirtReportsUploadActionForm extends ValidatorActionForm {
	private String reportCategoryId;
	private String reportTitle;
	
	public BirtReportsUploadActionForm() {
		super();	
	}
	public void clear() {
		this.reportCategoryId = "";
	}
	public String getReportCategoryId() {
		return reportCategoryId;
	}
	public void setReportCategoryId(String reportCategoryId) {
		this.reportCategoryId = reportCategoryId;
	}
	public void populate(ReportsCategoryBO reportsCategoryBO) throws OfficeException {
		this.reportCategoryId = reportsCategoryBO.getReportCategoryId().toString();
	}
	public String getReportTitle() {
		return reportTitle;
	}
	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}
}
