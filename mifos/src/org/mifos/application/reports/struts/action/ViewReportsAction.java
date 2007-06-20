package org.mifos.application.reports.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.reports.business.service.ReportsBusinessService;
import org.mifos.application.reports.persistence.ReportsPersistence;
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

public class ViewReportsAction extends BaseAction {

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);
	private ReportsBusinessService reportsBusinessService;

	public ViewReportsAction() {
		reportsBusinessService = new ReportsBusinessService();
	}

	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("viewReportsAction");
		security.allow("getPage",
				SecurityConstants.VIEW_REPORTS);
		
		return security;
	}

	public ActionForward getPage(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("In ReportsAction:getViewReportsPage Method: ");
		request.getSession().setAttribute(ReportsConstants.LISTOFREPORTS,
				new ReportsPersistence().getAllReportCategories());
		return mapping.findForward(ActionForwards.load_success.toString());
	}


	@Override
	protected BusinessService getService() throws ServiceException {
		return reportsBusinessService;
	}
	
}
