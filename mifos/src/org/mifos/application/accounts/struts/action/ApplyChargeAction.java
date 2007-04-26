package org.mifos.application.accounts.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.struts.actionforms.ApplyChargeActionForm;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class ApplyChargeAction extends BaseAction {

	@Override
	protected BusinessService getService() throws ServiceException {
		return getAccountBusinessService();
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		doCleanUp(request, form);
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		Integer accountId = Integer.valueOf(request.getParameter("accountId"));
		SessionUtils.setCollectionAttribute(AccountConstants.APPLICABLE_CHARGE_LIST,
				getAccountBusinessService().getAppllicableFees(accountId,
						userContext), request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,
				getAccountBusinessService().getAccount(accountId), request);
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	@CloseSession
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		ApplyChargeActionForm applyChargeActionForm = (ApplyChargeActionForm) form;
		Short chargeType = Short.valueOf(applyChargeActionForm.getChargeType());
		Double chargeAmount = new Double(request.getParameter("charge"));
		AccountBO accountBO = getAccountBusinessService().getAccount(
				Integer.valueOf(applyChargeActionForm.getAccountId()));
		accountBO.setUserContext(userContext);

		CustomerLevel customerLevel = null;
		if(accountBO.getType().equals(AccountTypes.CUSTOMER_ACCOUNT))
			customerLevel = accountBO.getCustomer().getLevel();
		if (accountBO.getPersonnel() != null)
			checkPermissionForApplyCharges(accountBO.getType(), customerLevel, userContext,
					accountBO.getOffice().getOfficeId(), accountBO.getPersonnel()
							.getPersonnelId());
		else
			checkPermissionForApplyCharges(accountBO.getType(), customerLevel, userContext,
					accountBO.getOffice().getOfficeId(), userContext.getId());

		accountBO.applyCharge(chargeType, chargeAmount);
		accountBO.update();
		return mapping.findForward(getDetailAccountPage(accountBO));
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception{
		request.removeAttribute(
				AccountConstants.APPLICABLE_CHARGE_LIST);
		AccountBO accountBO = (AccountBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		return mapping.findForward(getDetailAccountPage(accountBO));
	}

	private AccountBusinessService getAccountBusinessService()
			throws ServiceException {
		return new AccountBusinessService();
	}

	private void doCleanUp(HttpServletRequest request, ActionForm form) {
		ApplyChargeActionForm applyChargeActionForm = (ApplyChargeActionForm) form;
		applyChargeActionForm.setAccountId(null);
		applyChargeActionForm.setChargeType(null);
		applyChargeActionForm.setChargeAmount(null);
		request.removeAttribute(AccountConstants.APPLICABLE_CHARGE_LIST);
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request
				.getAttribute(SavingsConstants.METHODCALLED);
		String forward = null;
		if (method != null) {
			if (method.equals(Methods.update.toString()))
				forward = ActionForwards.update_failure.toString();
		}
		return mapping.findForward(forward);
	}

	private String getDetailAccountPage(AccountBO account) {
		if (account.getAccountType().getAccountTypeId().equals(
				AccountTypes.LOAN_ACCOUNT.getValue())) {
			return "loanDetails_success";
		} else {
			if (account.getCustomer().getCustomerLevel().getId().equals(
					CustomerLevel.CLIENT.getValue()))
				return "clientDetails_success";
			else if (account.getCustomer().getCustomerLevel().getId().equals(
					CustomerLevel.GROUP.getValue()))
				return "groupDetails_success";
			else
				return "centerDetails_success";
		}
	}
	
	private void checkPermissionForApplyCharges(AccountTypes accountTypes,CustomerLevel customerLevel,
			UserContext userContext, Short recordOfficeId,
			Short recordLoanOfficerId) throws ApplicationException {
		if (!isPermissionAllowed(accountTypes, customerLevel, userContext,
				recordOfficeId, recordLoanOfficerId))
			throw new CustomerException(
					SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
	}
	
	private boolean isPermissionAllowed(AccountTypes accountTypes,CustomerLevel customerLevel,
			UserContext userContext, Short recordOfficeId,
			Short recordLoanOfficerId) {
		return ActivityMapper.getInstance().isApplyChargesPermittedForAccounts(
				accountTypes, customerLevel, userContext, recordOfficeId,
				recordLoanOfficerId);
	}
}
