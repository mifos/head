package org.mifos.application.reports.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.reports.util.helpers.ReportsConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.StringUtils;

public class BirtReportsUploadActionForm extends ValidatorActionForm {
	private String reportCategoryId;
	private String reportTitle;
	protected FormFile file;
	private String reportId;
	private String isActive;
	public BirtReportsUploadActionForm() {
		super();
	}

	public void clear() {
		this.reportCategoryId = null;
		this.reportTitle = null;
		this.file = null;
		this.reportId = null;
		this.isActive = null;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		request.setAttribute(Constants.CURRENTFLOWKEY, request
				.getParameter(Constants.CURRENTFLOWKEY));
		request.getSession().setAttribute(Constants.CURRENTFLOWKEY,
				request.getParameter(Constants.CURRENTFLOWKEY));
		String method = request.getParameter("method");

		validateMethod(errors, Methods.preview.toString(), method);
		validateMethod(errors, Methods.editpreview.toString(), method);
		
		
		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", method);
		}
		
		return errors;
	}

	private void validateMethod(ActionErrors errors, String verifyMethod, String methodFromRequest) {
		if (methodFromRequest.equals(verifyMethod)) {
			if (StringUtils.isNullOrEmpty(reportTitle)){
				errors.add(ReportsConstants.ERROR_TITLE, new ActionMessage(
						ReportsConstants.ERROR_TITLE));
			}
			if (StringUtils.isNullOrEmpty(reportCategoryId) || this.getReportCategoryId().equals("-1")) {
				errors.add(ReportsConstants.ERROR_CATEGORYID, new ActionMessage(
						ReportsConstants.ERROR_CATEGORYID));
			}
			if (StringUtils.isNullOrEmpty(isActive) || this.getIsActive().equals("-1")){
				errors.add(ReportsConstants.ERROR_STATUS, new ActionMessage(
						ReportsConstants.ERROR_STATUS));
			}
			if (file !=null && !StringUtils.isEmpty(file.getFileName()) && !file.getFileName().endsWith(".rptdesign")) {
				errors.add(ReportsConstants.ERROR_FILE, new ActionMessage(
						ReportsConstants.ERROR_FILE));
			}
		}
	}

	public String getReportCategoryId() {
		return reportCategoryId;
	}

	public void setReportCategoryId(String reportCategoryId) {
		this.reportCategoryId = reportCategoryId;
	}

	public void populate(ReportsCategoryBO reportsCategoryBO)
			throws OfficeException {
		this.reportCategoryId = reportsCategoryBO.getReportCategoryId()
				.toString();
	}

	public String getReportTitle() {
		return reportTitle;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public FormFile getFile() {
		return file;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	
}
