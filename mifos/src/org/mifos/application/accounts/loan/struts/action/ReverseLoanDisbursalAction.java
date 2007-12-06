/**

 * ReverseLoanDisbursalAction.java    version: xxx



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

package org.mifos.application.accounts.loan.struts.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.loan.business.LoanActivityView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.loan.struts.actionforms.ReverseLoanDisbursalActionForm;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class ReverseLoanDisbursalAction extends BaseAction {

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	@Override
	protected BusinessService getService() {
		return new LoanBusinessService();
	}
	
	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("reverseloandisbaction");
		security.allow("search", SecurityConstants.CAN_REVERSE_LOAN_DISBURSAL);
		security.allow("load", SecurityConstants.VIEW);
		security.allow("preview", SecurityConstants.VIEW);
		security.allow("update", SecurityConstants.VIEW);
		security.allow("cancel", SecurityConstants.VIEW);
		security.allow("validate", SecurityConstants.VIEW);
		return security;
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward search(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Inside search method");
		request.getSession().setAttribute(
				LoanConstants.REVERSE_LOAN_DIBURSAL_ACTION_FORM, null);
		request.getSession().setAttribute(Constants.BUSINESS_KEY, null);
		logger.debug("Outside search method");
		return mapping.findForward(ActionForwards.search_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Inside load method");
		ReverseLoanDisbursalActionForm actionForm = (ReverseLoanDisbursalActionForm) form;
		LoanBO loan = getLoanAccount(actionForm.getSearchString(),
				getUserContext(request));
		if (loan == null) {
			throw new ApplicationException(LoanConstants.NOSEARCHRESULTS);
		}
		if (!(loan.getAccountState().getId().equals(
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING.getValue()) || loan
				.getAccountState().getId().equals(
						AccountState.LOAN_ACTIVE_IN_BAD_STANDING.getValue()))) {
			throw new ApplicationException(LoanConstants.NOSEARCHRESULTS);
		}
		List<LoanActivityView> payments = getApplicablePayments(loan);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
		SessionUtils.setCollectionAttribute(LoanConstants.PAYMENTS_LIST,
				payments, request);
		SessionUtils.setAttribute(LoanConstants.PAYMENTS_SIZE, payments.size(),
				request);
		logger.debug("Outside load method");
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("preview method called");
		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Inside validate method");
		ActionForwards actionForward = ActionForwards.search_success;
		String method = (String) request.getAttribute("methodCalled");
		if (method != null) {
			if (method.equals(Methods.search.toString())
					|| method.equals(Methods.load.toString())) {
				actionForward = ActionForwards.search_success;
			} else if (method.equals(Methods.preview.toString())) {
				actionForward = ActionForwards.load_success;
			} else if (method.equals(Methods.update.toString())) {
				actionForward = ActionForwards.preview_success;
			}
		}
		logger.debug("outside validate method");
		return mapping.findForward(actionForward.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Inside update method");
		ReverseLoanDisbursalActionForm actionForm = (ReverseLoanDisbursalActionForm) form;
		LoanBO loan = (LoanBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		loan = ((LoanBusinessService) getService()).getAccount(loan
				.getAccountId());
		PersonnelBO personnel = ((PersonnelBusinessService) ServiceFactory
				.getInstance()
				.getBusinessService(BusinessServiceName.Personnel))
				.getPersonnel(getUserContext(request).getId());
		loan.setUserContext(getUserContext(request));
		loan.reverseLoanDisbursal(personnel, actionForm.getNote());
		logger.debug("Outside update method");
		return mapping.findForward(ActionForwards.update_success.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("cancel method called");
		return mapping.findForward(ActionForwards.cancel_success.toString());
	}

	private LoanBO getLoanAccount(String globalAccountNum,
			UserContext userContext) throws Exception {
		LoanBO loan = ((LoanBusinessService) getService())
				.findBySystemId(globalAccountNum);
		if (loan != null && isAccountUnderUserScope(loan, userContext)) {
			return loan;
		}
		return null;
	}

	private boolean isAccountUnderUserScope(LoanBO loan, UserContext userContext)
			throws Exception {
		if (userContext.getLevelId().equals(
				PersonnelLevel.LOAN_OFFICER.getValue())) {
			return (loan.getPersonnel().getPersonnelId().equals(userContext
					.getId()));
		} else {
			if (userContext.getOfficeLevelId().equals(
					OfficeLevel.BRANCHOFFICE.getValue())) {
				return (loan.getOffice().getOfficeId().equals(userContext
						.getBranchId()));
			} else {
				OfficeBO userOffice = ((OfficeBusinessService) ServiceFactory
						.getInstance().getBusinessService(
								BusinessServiceName.Office))
						.getOffice(userContext.getBranchId());
				return (userOffice.isParent(loan.getOffice()));
			}
		}
	}

	private List<LoanActivityView> getApplicablePayments(LoanBO loan) {
		List<LoanActivityView> payments = new ArrayList<LoanActivityView>();
		Set<AccountPaymentEntity> accountPayments = loan.getAccountPayments();
		int i = accountPayments.size() - 1;
		if (accountPayments != null && accountPayments.size() > 0) {
			for (AccountPaymentEntity accountPayment : accountPayments) {
				if (accountPayment.getAmount().getAmountDoubleValue() > 0.0) {
					Money amount = new Money();
					if (i == 0) {
						for (AccountTrxnEntity accountTrxn : accountPayment
								.getAccountTrxns()) {
							if (accountTrxn.getAccountActionEntity().getId()
									.shortValue() == AccountActionTypes.LOAN_REPAYMENT.getValue()
									|| accountTrxn.getAccountActionEntity()
											.getId().shortValue() == AccountActionTypes.FEE_REPAYMENT.getValue()) {
								amount = amount.add(accountTrxn.getAmount());
							}
						}
					} else {
						amount = accountPayment.getAmount();
					}
					if (amount.getAmountDoubleValue() > 0.0) {
						LoanActivityView loanActivityView = new LoanActivityView();
						loanActivityView.setActionDate(accountPayment
								.getPaymentDate());
						loanActivityView.setTotal(amount);
						payments.add(0, loanActivityView);
					}
				}
				i--;
			}
		}
		return payments;
	}
}
