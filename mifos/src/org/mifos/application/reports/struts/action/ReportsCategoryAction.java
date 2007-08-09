package org.mifos.application.reports.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.reports.business.service.ReportsBusinessService;
import org.mifos.application.reports.struts.actionforms.ReportsCategoryActionForm;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;

public class ReportsCategoryAction extends BaseAction {
	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.REPORTSLOGGER);
	private ReportsBusinessService reportsBusinessService;

	public ReportsCategoryAction() {
		reportsBusinessService = (ReportsBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.ReportsService);
	}

	@Override
	protected BusinessService getService() throws ServiceException {
		return reportsBusinessService;
	}

	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("reportsCategoryAction");
		security.allow("loadDefineNewCategoryPage",
				SecurityConstants.DEFINE_REPORT_CATEGORY);
		security.allow("preview", SecurityConstants.DEFINE_REPORT_CATEGORY);
		return security;
	}

	public ActionForward loadDefineNewCategoryPage(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger
				.debug("In ReportsCategoryAction:loadDefineNewCategoryPage Method: ");
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In ReportsCategoryAction:preview Method: ");
		ReportsCategoryActionForm defineCategoryForm = (ReportsCategoryActionForm) form;
		defineCategoryForm.clear();
		String categoryName = defineCategoryForm.getCategoryName();
		request.setAttribute("categoryName", categoryName);
		return mapping.findForward(ActionForwards.preview_success.toString());

	}
}
