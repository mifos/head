/**

 * AccountApplyPaymentAction.java    version: xxx

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */
package org.mifos.application.accounts.struts.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.CustomerAccountBO;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.struts.actionforms.AccountApplyPaymentActionForm;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.CustomerAccountPaymentData;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class AccountApplyPaymentAction extends BaseAction {
	AccountBusinessService accountBusinessService = null;

	LoanBusinessService loanBusinessService = null;

	private MasterDataService masterDataService;

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

	@TransactionDemarcate(joinToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		AccountApplyPaymentActionForm actionForm = (AccountApplyPaymentActionForm) form;
		clearActionForm(actionForm);
		actionForm.setTransactionDate(DateHelper.getCurrentDate(uc
				.getPereferedLocale()));
		AccountBO account = getAccountBusinessService().getAccount(
				Integer.valueOf(actionForm.getAccountId()));
		account.setUserContext(uc);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, account, request);
		SessionUtils.setAttribute(MasterConstants.PAYMENT_TYPE,
				getMasterDataService().getSupportedPaymentModes(
						uc.getLocaleId(), TrxnTypes.loan_repayment.getValue()),
				request);
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
			throws Exception {
		AccountBO savedAccount = (AccountBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		AccountApplyPaymentActionForm actionForm = (AccountApplyPaymentActionForm) form;
		AccountBO account = getAccountBusinessService().getAccount(
				Integer.valueOf(actionForm.getAccountId()));
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		CustomerLevel customerLevel = null;
		if(account.getType().equals(AccountTypes.CUSTOMERACCOUNT))
			customerLevel = account.getCustomer().getLevel();
		if (account.getPersonnel() != null)
			checkPermissionForMakingPayment(account.getType(), customerLevel, uc,
					account.getOffice().getOfficeId(), account.getPersonnel()
							.getPersonnelId());
		else
			checkPermissionForMakingPayment(account.getType(), customerLevel, uc,
					account.getOffice().getOfficeId(), uc.getId());
		Date trxnDate = getDateFromString(actionForm.getTransactionDate(), uc
				.getPereferedLocale());
		Date receiptDate = getDateFromString(actionForm.getReceiptDate(), uc
				.getPereferedLocale());

		if (!account.isTrxnDateValid(trxnDate))
			throw new AccountException("errors.invalidTxndate");

		account.setVersionNo(savedAccount.getVersionNo());
		Money amount = new Money();
		if (account.getAccountType().getAccountTypeId().equals(
				AccountTypes.LOANACCOUNT.getValue()))
			amount = actionForm.getAmount();
		else
			amount = account.getTotalPaymentDue();
		account.applyPayment(createPaymentData(amount, trxnDate, actionForm
				.getReceiptId(), receiptDate, Short.valueOf(actionForm
				.getPaymentTypeId()), uc.getId(), account));
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

	private PaymentData createPaymentData(Money amount, Date trxnDate,
			String receiptId, Date receiptDate, Short paymentTypeId,
			Short userId, AccountBO account) throws Exception {
		PaymentData paymentData = new PaymentData(amount,
				new PersonnelPersistence().getPersonnel(userId), paymentTypeId,
				trxnDate);
		paymentData.setRecieptDate(receiptDate);
		paymentData.setRecieptNum(receiptId);
		for (AccountActionDateEntity installment : account
				.getTotalInstallmentsDue()) {
			if (account instanceof CustomerAccountBO) {
				paymentData
						.addAccountPaymentData(new CustomerAccountPaymentData(
								installment));
			}
		}
		return paymentData;
	}

	private void clearActionForm(AccountApplyPaymentActionForm actionForm) {
		actionForm.setReceiptDate(null);
		actionForm.setReceiptId(null);
		actionForm.setPaymentTypeId(null);
	}

	private String getForward(String input) {
		if (input.equals("loan"))
			return ActionForwards.loan_detail_page.toString();
		else
			return "applyPayment_success";
	}

	private AccountBusinessService getAccountBusinessService() {
		if (accountBusinessService == null)
			accountBusinessService = (AccountBusinessService) ServiceFactory
					.getInstance().getBusinessService(
							BusinessServiceName.Accounts);
		return accountBusinessService;
	}

	private MasterDataService getMasterDataService() {
		if (masterDataService == null)
			masterDataService = (MasterDataService) ServiceFactory
					.getInstance().getBusinessService(
							BusinessServiceName.MasterDataService);
		return masterDataService;
	}

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
	
	private void checkPermissionForMakingPayment(AccountTypes accountTypes,CustomerLevel customerLevel,
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
		return ActivityMapper.getInstance().isPaymentPermittedForAccounts(
				accountTypes, customerLevel, userContext, recordOfficeId,
				recordLoanOfficerId);
	}
}
