package org.mifos.application.customer.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFlagMapping;
import org.mifos.application.accounts.struts.action.AccountAppAction;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.ClosedAccSearchConstants;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.BusinessServiceName;
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
	
	//TODO:: Remove this method once back button is implemented throughout the application.
	public ActionForward getAllClosedAccounts(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServiceException, PersistenceException {
		logger.debug("In CustomerAction::getAllClosedAccounts()");
		Integer customerId = Integer.valueOf(request.getParameter("customerId"));
		CustomerBusinessService customerService = (CustomerBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Customer);
		List<AccountBO> loanAccountsList=customerService.getAllClosedAccount(customerId,AccountTypes.LOANACCOUNT.getValue());
		List<AccountBO> savingsAccountList = customerService.getAllClosedAccount(customerId,AccountTypes.SAVINGSACCOUNT.getValue());
		for(AccountBO savingsBO:savingsAccountList) {
			setLocaleIdForToRetrieveMasterDataName(savingsBO,request);
		}
		for(AccountBO loanBO:loanAccountsList) {
			setLocaleIdForToRetrieveMasterDataName(loanBO,request);
		}
		SessionUtils.setAttribute(ClosedAccSearchConstants.CLOSEDLOANACCOUNTSLIST,loanAccountsList,request.getSession());
		SessionUtils.setAttribute(ClosedAccSearchConstants.CLOSEDSAVINGSACCOUNTSLIST,savingsAccountList,request.getSession());
		return mapping.findForward(ActionForwards.viewAllClosedAccounts.toString());
	}
	
	private void setLocaleIdForToRetrieveMasterDataName(AccountBO accountBO, HttpServletRequest request) {
		accountBO.getAccountState().setLocaleId(getUserContext(request).getLocaleId());
		for(AccountFlagMapping accountFlagMapping : accountBO.getAccountFlags()) {
			accountFlagMapping.getFlag().setLocaleId(getUserContext(request).getLocaleId());
		}
	}
}
