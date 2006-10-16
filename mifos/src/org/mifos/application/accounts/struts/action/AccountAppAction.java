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
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class AccountAppAction extends BaseAction {

	@Override
	protected BusinessService getService() {
		return getAccountBizService();
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	@CloseSession
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward removeFees(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Integer accountId = getIntegerValue(request.getParameter("accountId"));
		Short feeId = getShortValue(request.getParameter("feeId"));
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USERCONTEXT, request.getSession());
		AccountBO accountBO = getAccountBizService().getAccount(accountId);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO,request);
		if (accountBO.getPersonnel() != null)
			getAccountBizService().checkPermissionForRemoveFees(accountBO.getType(),accountBO.getCustomer().getLevel(), uc,
					accountBO.getOffice().getOfficeId(), accountBO.getPersonnel()
							.getPersonnelId());
		else
			getAccountBizService().checkPermissionForRemoveFees(accountBO.getType(),accountBO.getCustomer().getLevel(), uc,
					accountBO.getOffice().getOfficeId(), uc.getId());
		accountBO.removeFees(feeId, uc.getId());
		String fromPage = request.getParameter(CenterConstants.FROM_PAGE);
		StringBuilder forward = new StringBuilder();
		forward = forward.append(AccountConstants.REMOVE + "_" + fromPage + "_"
				+ AccountConstants.CHARGES);
		if (fromPage != null) {
			return mapping.findForward(forward.toString());
		} else {
			return mapping.findForward(AccountConstants.REMOVE_SUCCESS);
		}
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward getTrxnHistory(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String globalAccountNum = request.getParameter("globalAccountNum");
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USERCONTEXT, request.getSession());
		AccountBO accountBO = getAccountBizService()
				.findBySystemId(globalAccountNum);
		SessionUtils.setAttribute(SavingsConstants.TRXN_HISTORY_LIST,
				getAccountBizService().getTrxnHistory(accountBO, uc), request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO,request);
		return mapping.findForward("getTransactionHistory_success");
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward waiveChargeDue(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USERCONTEXT, request.getSession());
		Integer accountId = getIntegerValue(request.getParameter("accountId"));
		AccountBO account = getAccountBizService().getAccount(accountId);
		account.setUserContext(uc);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, account,request);
		WaiveEnum waiveEnum = getWaiveType(request.getParameter(AccountConstants.WAIVE_TYPE));
		if (account.getPersonnel() != null)
			getAccountBizService().checkPermissionForWaiveDue(waiveEnum, account.getType(), account.getCustomer().getLevel(), uc,
					account.getOffice().getOfficeId(), account.getPersonnel()
							.getPersonnelId());
		else
			getAccountBizService().checkPermissionForWaiveDue(waiveEnum, account.getType(), account.getCustomer().getLevel(), uc,
					account.getOffice().getOfficeId(), uc.getId());
		account.waiveAmountDue(waiveEnum);
		return mapping.findForward("waiveChargesDue_Success");
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward waiveChargeOverDue(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USERCONTEXT, request.getSession());
		Integer accountId = getIntegerValue(request.getParameter("accountId"));
		AccountBO account = getAccountBizService().getAccount(accountId);
		account.setUserContext(uc);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, account,request);
		WaiveEnum waiveEnum = getWaiveType(request.getParameter(AccountConstants.WAIVE_TYPE));
		if (account.getPersonnel() != null)
			getAccountBizService().checkPermissionForWaiveDue(waiveEnum, account.getType(), account.getCustomer().getLevel(), uc,
					account.getOffice().getOfficeId(), account.getPersonnel()
							.getPersonnelId());
		else
			getAccountBizService().checkPermissionForWaiveDue(waiveEnum, account.getType(), account.getCustomer().getLevel(), uc,
					account.getOffice().getOfficeId(), uc.getId());
		account.waiveAmountOverDue(waiveEnum);
		return mapping.findForward("waiveChargesOverDue_Success");
	}

	private WaiveEnum getWaiveType(String waiveType) {
		if (waiveType != null) {
			if (waiveType.equalsIgnoreCase(WaiveEnum.PENALTY.toString())) {
				return WaiveEnum.PENALTY;
			}
			if (waiveType.equalsIgnoreCase(WaiveEnum.FEES.toString())) {
				return WaiveEnum.FEES;
			}
		}
		return WaiveEnum.ALL;
	}
	
	protected CustomerBO getCustomer(Integer customerId) throws Exception {
		return getCustomerBusinessService().getCustomer(customerId);
	}

	protected CustomerBusinessService getCustomerBusinessService() {
		return (CustomerBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Customer);
	}
	
	private AccountBusinessService getAccountBizService() {
		return (AccountBusinessService) ServiceFactory
		.getInstance().getBusinessService(BusinessServiceName.Accounts);
	}
}
