/**
 * 
 */
package org.mifos.application.accounts.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;

public class AccountAppAction extends BaseAction {

	AccountBusinessService accountBusinessService=null;
	CustomerBusinessService customerService;

	public AccountAppAction() throws ServiceException{
		accountBusinessService =(AccountBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Accounts);
		customerService=(CustomerBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Customer);
	}	
	
	@Override
	protected BusinessService getService() {
		return accountBusinessService;
	}
	
	protected CustomerBO getCustomer(Integer customerId)throws ServiceException{
		return customerService.getCustomer(customerId);
	}
	
	protected BusinessService getCustomerBusinessService(){
		return customerService;
	}
	
	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		if(method.equals("removeFees")||method.equals("getTrxnHistory")
				|| method.equals("waiveChargeDue") || method.equals("waiveChargeOverDue")
				|| method.equals("forwardWaiveChargeDue") || method.equals("forwardWaiveChargeOverDue")
				|| method.equals("getAllActivity") || method.equals("getAllClosedAccounts")) 
			return true;
		else
			return false;
	}

	public ActionForward removeFees(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws SystemException, ApplicationException{
		Integer accountId=Integer.valueOf((String)request.getParameter("accountId"));		
		Short feeId=Short.valueOf((String)request.getParameter("feeId"));		
		UserContext uc = (UserContext)SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY,request.getSession());		
		AccountBO accountBO=accountBusinessService.getAccount(accountId);
		accountBO.removeFees(feeId,uc.getId());
		String fromPage = request.getParameter(CenterConstants.FROM_PAGE);
		StringBuilder forward = new StringBuilder();
		forward = forward.append(AccountConstants.REMOVE+"_"+fromPage +"_"+AccountConstants.CHARGES);		
		if( fromPage != null ){
			return mapping.findForward(forward.toString());
		}
		else{
			return mapping.findForward(AccountConstants.REMOVE_SUCCESS);
		}
	}
	
	public ActionForward getTrxnHistory(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception{
		String globalAccountNum=request.getParameter("globalAccountNum");
		UserContext uc = (UserContext)SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY,request.getSession());
		AccountBO accountBO = accountBusinessService.findBySystemId(globalAccountNum);
		SessionUtils.setAttribute(SavingsConstants.TRXN_HISTORY_LIST,accountBusinessService.getTrxnHistory(accountBO,uc),request.getSession());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,customerService.getBySystemId(accountBO.getCustomer().getGlobalCustNum(),accountBO.getCustomer().getCustomerLevel().getId()),request.getSession());
		return mapping.findForward("getTransactionHistory_success");
	}
	
	public ActionForward waiveChargeDue(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {		
		UserContext uc = (UserContext)SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY,request.getSession());
		Integer accountId=Integer.valueOf((String)request.getParameter("accountId"));
		AccountBusinessService accountBusinessService =(AccountBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Accounts);
		AccountBO account = accountBusinessService.getAccount(accountId);
		account.setUserContext(uc);		
		account.waiveAmountDue(getWaiveType(request.getParameter(AccountConstants.WAIVE_TYPE)));		
		return mapping.findForward("waiveChargesDue_Success");
	}
	
	public ActionForward waiveChargeOverDue(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		UserContext uc = (UserContext)SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY,request.getSession());
		Integer accountId=Integer.valueOf((String)request.getParameter("accountId"));
		AccountBusinessService accountBusinessService =(AccountBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Accounts);
		AccountBO account = accountBusinessService.getAccount(accountId);
		account.setUserContext(uc);
		account.waiveAmountOverDue(getWaiveType(request.getParameter(AccountConstants.WAIVE_TYPE)));
		return mapping.findForward("waiveChargesOverDue_Success");
	}
	
	private WaiveEnum getWaiveType(String waiveType){		
		if(waiveType != null){			
			if(waiveType.equalsIgnoreCase(WaiveEnum.PENALTY.toString())){
				return WaiveEnum.PENALTY;
			}
			if(waiveType.equalsIgnoreCase(WaiveEnum.FEES.toString())){
				return WaiveEnum.FEES;
			}			
		}
		return WaiveEnum.ALL;
	}
}
