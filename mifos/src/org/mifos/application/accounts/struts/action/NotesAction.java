package org.mifos.application.accounts.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountNotesEntity;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.struts.actionforms.NotesActionForm;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.SearchAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;

public class NotesAction extends SearchAction {

	@Override
	protected BusinessService getService() {
		return (AccountBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Accounts);
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		if (method.equals(Methods.previous.toString())
				|| method.equals(Methods.load.toString())
				|| method.equals(Methods.preview.toString())
				|| method.equals(Methods.cancel.toString())
				|| method.equals(Methods.create.toString())
				|| method.equals(Methods.search.toString())) {
			return true;
		} else
			return false;
	}

	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		clearActionForm(form);
		AccountBO accountBO = ((AccountBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Accounts))
				.getAccount(Integer.valueOf(((NotesActionForm) form)
						.getAccountId()));
		setFormAttributes(form, accountBO);
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		NotesActionForm notesActionForm = (NotesActionForm) form;
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		PersonnelBO personnel = new PersonnelPersistence().getPersonnel(uc
				.getId());
		AccountBO account = ((AccountBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Accounts))
				.getAccount(Integer.valueOf(notesActionForm.getAccountId()));
		AccountNotesEntity accountNotes = new AccountNotesEntity(
				new java.sql.Date(System.currentTimeMillis()), notesActionForm
						.getComment(), personnel, account);
		SessionUtils.setAttribute(AccountConstants.ACCOUNT_NOTES, accountNotes,
				request.getSession());
		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.previous_success.toString());
	}

	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(chooseForward(Short
				.valueOf(((NotesActionForm) form).getAccountTypeId())));
	}

	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionForward forward = null;
		NotesActionForm notesActionForm = (NotesActionForm) form;
		try {
			AccountBO accountBO = ((AccountBusinessService) ServiceFactory
					.getInstance().getBusinessService(
							BusinessServiceName.Accounts)).getAccount(Integer
					.valueOf(notesActionForm.getAccountId()));
			UserContext uc = (UserContext) SessionUtils.getAttribute(
					Constants.USER_CONTEXT_KEY, request.getSession());
			AccountNotesEntity accountNotes = (AccountNotesEntity) SessionUtils
					.getAttribute(AccountConstants.ACCOUNT_NOTES, request
							.getSession());
			accountBO.addAccountNotes(accountNotes);
			accountBO.setUserContext(uc);
			accountBO.update();
			HibernateUtil.commitTransaction();
			forward = mapping.findForward(chooseForward(Short
					.valueOf(notesActionForm.getAccountTypeId())));
		} catch (Exception e) {
			ActionErrors errors = new ActionErrors();
			errors.add(AccountConstants.UNKNOWN_EXCEPTION, new ActionMessage(
					AccountConstants.UNKNOWN_EXCEPTION));
			request.setAttribute(Globals.ERROR_KEY, errors);
			forward = mapping.findForward(ActionForwards.create_failure
					.toString());
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}
		return forward;
	}

	private String chooseForward(Short accountTypeId) {
		String forward = null;
		if (accountTypeId.equals(AccountTypes.LOANACCOUNT.getValue()))
			forward = ActionForwards.loan_detail_page.toString();
		else if (accountTypeId.equals(AccountTypes.SAVINGSACCOUNT.getValue()))
			forward = ActionForwards.savings_details_page.toString();
		return forward;
	}

	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request
				.getAttribute(SavingsConstants.METHODCALLED);
		String forward = null;
		if (method != null) {
			if (method.equals(Methods.preview.toString()))
				forward = ActionForwards.preview_failure.toString();
			else if (method.equals(Methods.create.toString()))
				forward = ActionForwards.create_failure.toString();
		}
		return mapping.findForward(forward);
	}

	private void clearActionForm(ActionForm form) {
		((NotesActionForm) form).setComment("");
		((NotesActionForm) form).setSecurityParamInput("");
	}

	@Override
	protected QueryResult getSearchResult(ActionForm form) throws Exception {
		return ((AccountBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Accounts))
				.getAllAccountNotes(Integer.valueOf(((NotesActionForm) form)
						.getAccountId()));
	}

	private void setFormAttributes(ActionForm form, AccountBO accountBO)
			throws Exception {
		NotesActionForm notesActionForm = (NotesActionForm) form;
		notesActionForm.setAccountTypeId(accountBO.getAccountType()
				.getAccountTypeId().toString());
		notesActionForm.setGlobalAccountNum(accountBO.getGlobalAccountNum());
		if (accountBO instanceof LoanBO) {
			notesActionForm.setPrdOfferingName(((LoanBO) accountBO)
					.getLoanOffering().getPrdOfferingName());
			notesActionForm.setSecurityParamInput("Loan");
		} else if (accountBO instanceof SavingsBO) {
			notesActionForm.setPrdOfferingName(((SavingsBO) accountBO)
					.getSavingsOffering().getPrdOfferingName());
			notesActionForm.setSecurityParamInput("Savings");
		}
	}
}
