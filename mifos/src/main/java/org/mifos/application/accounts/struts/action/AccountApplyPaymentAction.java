/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.application.accounts.struts.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.struts.actionforms.AccountApplyPaymentActionForm;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class AccountApplyPaymentAction extends BaseAction {
	AccountBusinessService accountBusinessService = null;

	LoanBusinessService loanBusinessService = null;

    private AccountPersistence accountPersistence = new AccountPersistence();

    public AccountApplyPaymentAction() {
	}

	@Override
	protected BusinessService getService() throws ServiceException {
		return getAccountBusinessService();
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("applyPaymentAction");
		security.allow("load", SecurityConstants.VIEW);
		security.allow("preview", SecurityConstants.VIEW);
		security.allow("previous", SecurityConstants.VIEW);
		security.allow("applyPayment", SecurityConstants.VIEW);
		return security;
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		AccountApplyPaymentActionForm actionForm = (AccountApplyPaymentActionForm) form;
		clearActionForm(actionForm);
		actionForm.setTransactionDate(DateUtils.makeDateAsSentFromBrowser());
		AccountBO account = getAccountBusinessService().getAccount(
				Integer.valueOf(actionForm.getAccountId()));
		account.setUserContext(uc);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, account, request);
		AcceptedPaymentTypePersistence persistence = new AcceptedPaymentTypePersistence();
		String input = request.getParameter(Constants.INPUT);
		if(input != null && input.trim() != Constants.EMPTY_STRING)
		{
			if(input.equals(Constants.LOAN))
			{
				SessionUtils.setCollectionAttribute(MasterConstants.PAYMENT_TYPE,
				persistence.getAcceptedPaymentTypesForATransaction(
						uc.getLocaleId(),
						TrxnTypes.loan_repayment.getValue()), request);
			}
			else
			{
				SessionUtils.setCollectionAttribute(MasterConstants.PAYMENT_TYPE,
						persistence.getAcceptedPaymentTypesForATransaction(
								uc.getLocaleId(),
								TrxnTypes.fee.getValue()), request);
			}
		}		
		actionForm.setAmount(account.getTotalPaymentDue());
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.previous_success.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	@CloseSession
	public ActionForward applyPayment(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
            throws ApplicationException, CustomerException, ServiceException {
		AccountBO savedAccount = (AccountBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		AccountApplyPaymentActionForm actionForm = (AccountApplyPaymentActionForm) form;
		AccountBO account = getAccountBusinessService().getAccount(
				Integer.valueOf(actionForm.getAccountId()));
		checkVersionMismatch(savedAccount.getVersionNo(),account.getVersionNo());
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());

		if (! account.isPaymentPermitted(uc)) {
				throw new CustomerException(
					SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }

        Date trxnDate = DateUtils.getDateAsSentFromBrowser(actionForm.getTransactionDate());

		if (!account.isTrxnDateValid(trxnDate))
			throw new AccountException("errors.invalidTxndate");

		account.setVersionNo(savedAccount.getVersionNo());

		Money amount;
		if (account.getType() == AccountTypes.LOAN_ACCOUNT) {
			amount = actionForm.getAmount();
		}
		else {
			amount = account.getTotalPaymentDue();
		}

        Date receiptDate = DateUtils.getDateAsSentFromBrowser(actionForm.getReceiptDate());
        PaymentData paymentData = account.createPaymentData(uc,
                amount, trxnDate, actionForm.getReceiptId(), receiptDate,
                Short.valueOf(actionForm.getPaymentTypeId()));
        account.applyPaymentWithPersist(paymentData);
		return mapping
				.findForward(getForward(((AccountApplyPaymentActionForm) form)
						.getInput()));
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping
				.findForward(getForward(((AccountApplyPaymentActionForm) form)
						.getInput()));
	}

	private void clearActionForm(AccountApplyPaymentActionForm actionForm) {
		actionForm.setReceiptDate(null);
		actionForm.setReceiptId(null);
		actionForm.setPaymentTypeId(null);
	}

	private String getForward(String input) {
		if (input.equals(Constants.LOAN))
			return ActionForwards.loan_detail_page.toString();
		else
			return "applyPayment_success";
	}

	private AccountBusinessService getAccountBusinessService() {
		if (accountBusinessService == null)
			accountBusinessService = new AccountBusinessService();
		return accountBusinessService;
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");
		String forward = null;
		if (method != null) {
			forward = method + "_failure";
		}
		return mapping.findForward(forward);
	}

    public AccountPersistence getAccountPersistence() {
        return accountPersistence;
    }
}
