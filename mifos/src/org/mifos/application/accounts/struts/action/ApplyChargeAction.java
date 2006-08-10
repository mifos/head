package org.mifos.application.accounts.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.struts.actionforms.ApplyChargeActionForm;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;

public class ApplyChargeAction extends BaseAction {

	@Override
	protected BusinessService getService() throws ServiceException {
		return getAccountBusinessService();
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		doCleanUp(request.getSession(), form);
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		Integer accountId = Integer.valueOf(request.getParameter("accountId"));
		SessionUtils.setAttribute(AccountConstants.APPLICABLE_CHARGE_LIST,
				getAccountBusinessService().getAppllicableFees(accountId,
						userContext), request.getSession());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,
				getAccountBusinessService().getAccount(accountId), request
						.getSession());
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws SystemException, ApplicationException{
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		ApplyChargeActionForm applyChargeActionForm = (ApplyChargeActionForm) form;
		Short chargeType = Short.valueOf(applyChargeActionForm.getChargeType());
		Money chargeAmount = new Money(request.getParameter("charge"));
		AccountBO accountBO = (AccountBO) getAccountBusinessService().getAccount(Integer.valueOf(applyChargeActionForm.getAccountId()));
		accountBO.setUserContext(userContext);
		accountBO.applyCharge(chargeType, chargeAmount);
		accountBO.update();
		HibernateUtil.commitTransaction();
		return mapping.findForward(getDetailAccountPage(accountBO));
	}

	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		request.getSession().removeAttribute(AccountConstants.APPLICABLE_CHARGE_LIST);
		AccountBO accountBO = (AccountBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		return mapping.findForward(getDetailAccountPage(accountBO));
	}

	private AccountBusinessService getAccountBusinessService()
			throws ServiceException {
		return (AccountBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Accounts);
	}

	private void doCleanUp(HttpSession session, ActionForm form) {
		ApplyChargeActionForm applyChargeActionForm = (ApplyChargeActionForm) form;
		applyChargeActionForm.setAccountId(null);
		applyChargeActionForm.setChargeType(null);
		applyChargeActionForm.setChargeAmount(null);
		session.removeAttribute(AccountConstants.APPLICABLE_CHARGE_LIST);
	}

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
				AccountTypes.LOANACCOUNT.getValue())) {
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
}
