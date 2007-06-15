package org.mifos.application.reports.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.reports.business.service.ReportsBusinessService;
import org.mifos.application.reports.persistence.ReportsPersistence;
import org.mifos.application.reports.struts.actionforms.BirtReportsUploadActionForm;
import org.mifos.application.reports.util.helpers.ReportsConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;

public class BirtReportsUploadAction extends BaseAction {
	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);
	private ReportsBusinessService reportsBusinessService;

	public BirtReportsUploadAction() {
		reportsBusinessService = new ReportsBusinessService();
	}

	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("birtReportsUploadAction");
		security.allow("getBirtReportsUploadPage", SecurityConstants.UPLOAD_REPORT_TEMPLATE);
		security.allow("preview", SecurityConstants.UPLOAD_REPORT_TEMPLATE);
		return security;
	}

	public ActionForward getBirtReportsUploadPage(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("In ReportsAction:getBirtReportPage Method: ");
		request.getSession().setAttribute(ReportsConstants.LISTOFREPORTS,new ReportsPersistence().getAllReportCategories());
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@Override
	protected BusinessService getService() throws ServiceException {
		return reportsBusinessService;
	}
	
	public ActionForward preview(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BirtReportsUploadActionForm uploadForm = (BirtReportsUploadActionForm) form;
		Short reportCategoryId = Short.parseShort(uploadForm.getReportCategoryId());
		List<ReportsCategoryBO> categories = new ReportsPersistence().getAllReportCategories();
		ReportsCategoryBO category = null;
		for (ReportsCategoryBO reportsCategory : categories) {
			if (reportsCategory.getReportCategoryId().equals(reportCategoryId)) {
				category = reportsCategory;
				break;
			}
		}
		request.setAttribute("category", category);
		return  mapping.findForward(ActionForwards.preview_success.toString());
	}
}
