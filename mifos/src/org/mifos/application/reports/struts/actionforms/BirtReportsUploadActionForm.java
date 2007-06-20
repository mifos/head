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

	public BirtReportsUploadActionForm() {
		super();
	}

	public void clear() {
		this.reportCategoryId = "";
		this.reportTitle = null;
		this.file = null;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String method = request.getParameter("method");
		request.setAttribute(Constants.CURRENTFLOWKEY, request
				.getParameter(Constants.CURRENTFLOWKEY));
		request.getSession().setAttribute(Constants.CURRENTFLOWKEY,
				request.getParameter(Constants.CURRENTFLOWKEY));


		if (method.equals(Methods.preview.toString())) {
			if (StringUtils.isNullOrEmpty(reportTitle)){
				errors.add(ReportsConstants.ERROR_TITLE, new ActionMessage(
						ReportsConstants.ERROR_TITLE));
			}
			if (this.getReportCategoryId().equals("-1")) {
				errors.add(ReportsConstants.ERROR_CATEGORYID, new ActionMessage(
						ReportsConstants.ERROR_CATEGORYID));
			}
			if (file == null || !file.getFileName().endsWith(".rptdesign")) {
				errors.add(ReportsConstants.ERROR_FILEISNULL, new ActionMessage(
						ReportsConstants.ERROR_FILEISNULL));
			}
		}
		
		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", method);
		}
		
		return errors;
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

}
