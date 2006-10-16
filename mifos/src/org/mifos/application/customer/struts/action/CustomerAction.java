package org.mifos.application.customer.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.struts.action.AccountAppAction;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class CustomerAction extends AccountAppAction {

	private  MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CUSTOMERLOGGER);
	
	public CustomerAction() throws Exception {
		super();
	}
	
	@TransactionDemarcate(validateAndResetToken=true)
	@CloseSession
	public ActionForward forwardWaiveChargeDue(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		String type = request.getParameter("type");
		return mapping.findForward("waive"+type+"Charges_Success");
	}
	
	@TransactionDemarcate(validateAndResetToken=true)
	@CloseSession
	public ActionForward forwardWaiveChargeOverDue(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		String type = request.getParameter("type");
		return mapping.findForward("waive"+type+"Charges_Success");
	}
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward getAllActivity(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		logger.debug("In CustomerAction::getAllActivity()");
		String type = request.getParameter("type");
		String globalCustNum=request.getParameter("globalCustNum");
		SessionUtils.setAttribute(CustomerConstants.CLIENTRECENTACCACTIVITYLIST,getCustomerBusinessService().getAllActivityView(globalCustNum),request);
		return mapping.findForward("view"+type+"Activity");
	}
}
