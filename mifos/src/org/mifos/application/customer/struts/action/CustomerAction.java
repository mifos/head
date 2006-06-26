package org.mifos.application.customer.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.struts.action.AccountAppAction;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;

public class CustomerAction extends AccountAppAction {

	private  MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);
	
	public CustomerAction() throws ServiceException {
		super();
	}
	
	public ActionForward forwardWaiveChargeDue(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		String type = request.getParameter("type");
		return mapping.findForward("waive"+type+"Charges_Success");
	}
	
	public ActionForward forwardWaiveChargeOverDue(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		String type = request.getParameter("type");
		return mapping.findForward("waive"+type+"Charges_Success");
	}
	
	public ActionForward getAllActivity(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		logger.debug("In CustomerAction::getAllActivity()");
		String type = request.getParameter("type");
		String globalCustNum=request.getParameter("globalCustNum");
		SessionUtils.setAttribute(CustomerConstants.CLIENTRECENTACCACTIVITYLIST,((CustomerBusinessService)getCustomerBusinessService()).getAllActivityView(globalCustNum),request.getSession());
		return mapping.findForward("view"+type+"Activity");
	}
	

}
