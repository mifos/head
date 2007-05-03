package org.mifos.application.accounts.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.struts.actionforms.EditStatusActionForm;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStateFlag;
import org.mifos.application.checklist.business.AccountCheckListBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class EditStatusAction extends BaseAction {

	@Override
	protected BusinessService getService() {
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
		doCleanUp(request.getSession(), form);
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USERCONTEXT, request.getSession());
		AccountBO accountBO = getAccountBusinessService().getAccount(
				getIntegerValue(((EditStatusActionForm) form).getAccountId()));
		getAccountBusinessService().initializeStateMachine(
				userContext.getLocaleId(),
				accountBO.getOffice().getOfficeId(),
				accountBO.getType(), 
				null);
		accountBO.setUserContext(userContext);
		accountBO.getAccountState().setLocaleId(userContext.getLocaleId());
		setFormAttributes(form, accountBO);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO, request);
		SessionUtils.setCollectionAttribute(SavingsConstants.STATUS_LIST,
				getAccountBusinessService()
						.getStatusList(
								accountBO.getAccountState(),
								accountBO.getType(),
								userContext.getLocaleId()), request);
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		AccountBO accountBO = (AccountBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USERCONTEXT, request.getSession());
		getMasterData(form, accountBO, request, userContext);
		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.previous_success.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(getDetailAccountPage(form));
	}

	@TransactionDemarcate(validateAndResetToken = true)
	@CloseSession
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		EditStatusActionForm editStatusActionForm = (EditStatusActionForm) form;
		AccountBO accountBOInSession = (AccountBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USERCONTEXT, request.getSession());
		AccountBO accountBO = getAccountBusinessService().getAccount(getIntegerValue(editStatusActionForm.getAccountId()));
		checkVersionMismatch(accountBOInSession.getVersionNo(),accountBO.getVersionNo());
		accountBO.setUserContext(userContext);
		accountBO.getAccountState().setLocaleId(userContext.getLocaleId());
		setInitialObjectForAuditLogging(accountBO);
		Short flagId = null;
		Short newStatusId = null;
		if (StringUtils.isNullAndEmptySafe(editStatusActionForm.getFlagId()))
			flagId = getShortValue(editStatusActionForm.getFlagId());
		if (StringUtils.isNullAndEmptySafe(editStatusActionForm
				.getNewStatusId()))
			newStatusId = getShortValue(editStatusActionForm.getNewStatusId());
		checkPermission(accountBO, getUserContext(request), newStatusId, flagId);
		accountBO.changeStatus(newStatusId, flagId, editStatusActionForm
				.getNotes());
		accountBOInSession = null;
		accountBO.update();
		accountBO = null;
		return mapping.findForward(getDetailAccountPage(form));
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request
				.getAttribute(SavingsConstants.METHODCALLED);
		String forward = null;
		if (method != null) {
			if (method.equals(Methods.preview.toString()))
				forward = ActionForwards.preview_failure.toString();
			else if (method.equals(Methods.load.toString()))
				forward = getDetailAccountPage(form);
			else if (method.equals(Methods.update.toString()))
				forward = ActionForwards.update_failure.toString();
		}
		return mapping.findForward(forward);
	}

	private AccountBusinessService getAccountBusinessService() {
		return new AccountBusinessService();
	}

	private void doCleanUp(HttpSession session, ActionForm form) {
		EditStatusActionForm editStatusActionForm = (EditStatusActionForm) form;
		editStatusActionForm.setSelectedItems(null);
		editStatusActionForm.setNotes(null);
		editStatusActionForm.setNewStatusId(null);
		editStatusActionForm.setFlagId(null);
		session.removeAttribute(Constants.BUSINESS_KEY);
	}

	private String getDetailAccountPage(ActionForm form) {
		EditStatusActionForm editStatusActionForm = (EditStatusActionForm) form;
		String input = editStatusActionForm.getInput();
		String forward = null;
		if (input.equals("loan"))
			forward = ActionForwards.loan_detail_page.toString();
		else if (input.equals("savings"))
			forward = ActionForwards.savings_details_page.toString();
		return forward;
	}

	private void getMasterData(ActionForm form, AccountBO accountBO,
			HttpServletRequest request, UserContext userContext)
			throws Exception {
		EditStatusActionForm editStatusActionForm = (EditStatusActionForm) form;
		editStatusActionForm.setCommentDate(DateUtils.getCurrentDate(userContext.getPreferredLocale()));
		String newStatusName = null;
		String flagName = null;
		List<AccountCheckListBO> checklist = getAccountBusinessService()
				.getStatusChecklist(
						getShortValue(editStatusActionForm.getNewStatusId()),
						getShortValue(editStatusActionForm.getAccountTypeId()));
		SessionUtils.setCollectionAttribute(SavingsConstants.STATUS_CHECK_LIST,
				checklist, request);
		if (StringUtils.isNullAndEmptySafe(editStatusActionForm
				.getNewStatusId()))
			newStatusName = getAccountBusinessService().getStatusName(
					userContext.getLocaleId(),
					AccountState.fromShort(getShortValue(editStatusActionForm
							.getNewStatusId())),
					accountBO.getType());
		SessionUtils.setAttribute(SavingsConstants.NEW_STATUS_NAME,
				newStatusName, request);
		if (StringUtils.isNullAndEmptySafe(editStatusActionForm
				.getNewStatusId())
				&& isNewStatusIsCancel(getShortValue(editStatusActionForm
						.getNewStatusId())))
			flagName = getAccountBusinessService().getFlagName(
					userContext.getLocaleId(),
					AccountStateFlag
							.getStatusFlag(getShortValue(editStatusActionForm
									.getFlagId())),
					accountBO.getType());
		SessionUtils
				.setAttribute(SavingsConstants.FLAG_NAME, flagName, request);
	}

	private boolean isNewStatusIsCancel(Short newStatusId) {
		return newStatusId.equals(AccountState.SAVINGS_ACC_CANCEL.getValue())
				|| newStatusId.equals(AccountState.LOANACC_CANCEL.getValue());
	}

	private void setFormAttributes(ActionForm form, AccountBO accountBO)
			throws Exception {
		EditStatusActionForm editStatusActionForm = (EditStatusActionForm) form;
		editStatusActionForm.setAccountTypeId(
				accountBO.getType().getValue().toString());
		editStatusActionForm.setCurrentStatusId(accountBO.getAccountState()
				.getId().toString());
		editStatusActionForm.setGlobalAccountNum(accountBO
				.getGlobalAccountNum());
		if (accountBO instanceof LoanBO) {
			editStatusActionForm.setAccountName(((LoanBO) accountBO)
					.getLoanOffering().getPrdOfferingName());
			editStatusActionForm.setInput("loan");
		} else if (accountBO instanceof SavingsBO) {
			editStatusActionForm.setAccountName(((SavingsBO) accountBO)
					.getSavingsOffering().getPrdOfferingName());
			editStatusActionForm.setInput("savings");
		}
	}

	private void checkPermission(AccountBO accountBO, UserContext userContext,
			Short newStatusId, Short flagId) throws Exception {
		if (null != accountBO.getPersonnel())
			getAccountBusinessService().checkPermissionForStatusChange(
					newStatusId, userContext, flagId,
					accountBO.getOffice().getOfficeId(),
					accountBO.getPersonnel().getPersonnelId());
		else
			getAccountBusinessService().checkPermissionForStatusChange(
					newStatusId, userContext, flagId,
					accountBO.getOffice().getOfficeId(), userContext.getId());
	}
}
