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
 
package org.mifos.application.accounts.savings.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.application.accounts.business.AccountNotesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.service.SavingsBusinessService;
import org.mifos.application.accounts.savings.struts.actionforms.SavingsClosureActionForm;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.savings.util.helpers.SavingsHelper;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.ChildrenStateType;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class SavingsClosureAction extends BaseAction {
	private SavingsBusinessService savingsService;

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	public SavingsClosureAction() throws ServiceException {
		savingsService = new SavingsBusinessService();
	}

	@Override
	protected BusinessService getService() {
		return savingsService;
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}
	
	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("savingsClosureAction");
		security.allow("load", SecurityConstants.SAVINGS_CLOSE_SAVINGS_ACCOUNT);
		security.allow("preview",
				SecurityConstants.SAVINGS_CLOSE_SAVINGS_ACCOUNT);
		security.allow("previous",
				SecurityConstants.SAVINGS_CLOSE_SAVINGS_ACCOUNT);
		security
				.allow("close", SecurityConstants.SAVINGS_CLOSE_SAVINGS_ACCOUNT);
		return security;
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		clearActionForm(form);

		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		logger.debug("In SavingsClosureAction::load(), accountId: "
				+ savings.getAccountId());
		// retrieve the savings object

		savings = savingsService.findById(savings.getAccountId());
		savingsService.initialize(savings.getCustomer());
		savingsService.initialize(savings.getCustomer().getPersonnel());
		savingsService.initialize(savings.getAccountNotes());
		savingsService.initialize(savings.getAccountStatusChangeHistory());
		savingsService.initialize(savings.getSavingsActivityDetails());
		initialize(savings.getSavingsOffering().getDepositGLCode());
		initialize(savings.getSavingsOffering().getInterestGLCode());

		SavingsClosureActionForm actionForm = (SavingsClosureActionForm) form;
		Money money = savings.getSavingsBalance();
		actionForm.setAmount(money.getAmount().toString());
		
		savings.setUserContext(uc);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
		AcceptedPaymentTypePersistence persistence = new AcceptedPaymentTypePersistence();
		SessionUtils.setCollectionAttribute(MasterConstants.PAYMENT_TYPE,
				persistence.getAcceptedPaymentTypesForATransaction(
						uc.getLocaleId(),
						TrxnTypes.savings_withdrawal.getValue()), request);
		
		// client list will be loaded only if it is center savings account,
		// or group savings account with deposit schedule of per client

		if (savings.getCustomer().getCustomerLevel().getId().shortValue() == CustomerLevel.CENTER.getValue()
				|| (savings.getCustomer().getCustomerLevel().getId()
						.shortValue() == CustomerLevel.GROUP.getValue() && savings
						.getRecommendedAmntUnit()
						.getId()
						.equals(RecommendedAmountUnit.PER_INDIVIDUAL.getValue())))
			SessionUtils.setCollectionAttribute(SavingsConstants.CLIENT_LIST,
					savings.getCustomer().getChildren(CustomerLevel.CLIENT,
							ChildrenStateType.ACTIVE_AND_ONHOLD), request);
		else SessionUtils.setAttribute(SavingsConstants.CLIENT_LIST, null,
				request);

		Money interestAmount = savings
				.calculateInterestForClosure(new SavingsHelper()
						.getCurrentDate());
		logger.debug("In SavingsClosureAction::load(), Interest calculated:  "
				+ interestAmount.getAmountDoubleValue());
		AccountPaymentEntity payment = new AccountPaymentEntity(savings,
				savings.getSavingsBalance().add(interestAmount), null, null,
				null, new DateTimeService().getCurrentJavaDateTime());
		SessionUtils.setAttribute(SavingsConstants.ACCOUNT_PAYMENT, payment,
				request);
		((SavingsClosureActionForm) form).setTrxnDate(DateUtils.getCurrentDate(uc.getPreferredLocale()));
		return mapping.findForward("load_success");
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsClosureAction::preview()");
		SavingsClosureActionForm actionForm = (SavingsClosureActionForm) form;
		AccountPaymentEntity payment = (AccountPaymentEntity) SessionUtils
				.getAttribute(SavingsConstants.ACCOUNT_PAYMENT, request);
		AccountPaymentEntity accountPaymentEntity = null;
		if (actionForm.getReceiptDate() != null
				&& actionForm.getReceiptDate() != "")
			accountPaymentEntity = new AccountPaymentEntity(payment
					.getAccount(), payment.getAmount(), actionForm
					.getReceiptId(), new java.util.Date(DateUtils.getDateAsSentFromBrowser(actionForm.getReceiptDate())
					.getTime()), new PaymentTypeEntity(Short.valueOf(actionForm
					.getPaymentTypeId())), new DateTimeService().getCurrentJavaDateTime());
		else
		{
			if(actionForm.getPaymentTypeId() != null && !actionForm.getPaymentTypeId().equals(""))
			{
				if(!(actionForm.getPaymentTypeId().equals("")))
					accountPaymentEntity = new AccountPaymentEntity(payment.getAccount(), payment.getAmount(), actionForm.getReceiptId(), null, new PaymentTypeEntity(Short.valueOf(actionForm.getPaymentTypeId())), new DateTimeService().getCurrentJavaDateTime());
				else
					accountPaymentEntity = new AccountPaymentEntity(payment.getAccount(), payment.getAmount(), actionForm.getReceiptId(), null, new PaymentTypeEntity(), new DateTimeService().getCurrentJavaDateTime());
			}
			else
				accountPaymentEntity = new AccountPaymentEntity(payment.getAccount(), payment.getAmount(), actionForm.getReceiptId(), null, new PaymentTypeEntity(), new DateTimeService().getCurrentJavaDateTime());
		}
		SessionUtils.setAttribute(SavingsConstants.ACCOUNT_PAYMENT,
				accountPaymentEntity, request);
		return mapping.findForward("preview_success");
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsClosureAction::previous()");
		return mapping.findForward("previous_success");
	}

	@TransactionDemarcate(validateAndResetToken = true)
	@CloseSession
	public ActionForward close(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		AccountPaymentEntity payment = (AccountPaymentEntity) SessionUtils
				.getAttribute(SavingsConstants.ACCOUNT_PAYMENT, request);
		SavingsBO savingsInSession = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		SavingsBO savings = savingsService.findById(savingsInSession
				.getAccountId());
		checkVersionMismatch(savingsInSession.getVersionNo(), savings
				.getVersionNo());
		savings.setUserContext(getUserContext(request));
		logger.debug("In SavingsClosureAction::close(), accountId: "
				+ savings.getAccountId());
		SavingsClosureActionForm actionForm = (SavingsClosureActionForm) form;
		AccountNotesEntity notes = new AccountNotesEntity(new DateTimeService().getCurrentJavaSqlDate(), actionForm.getNotes(),
				(new PersonnelPersistence()).getPersonnel(getUserContext(
						request).getId()), savings);
		CustomerBO customer = searchForCustomer(request, actionForm
				.getCustomerId());
		if (customer == null)
			customer = savings.getCustomer();
		savings.closeAccount(payment, notes, customer);
		SessionUtils.removeAttribute(SavingsConstants.CLIENT_LIST, request);
		SessionUtils.removeAttribute(SavingsConstants.ACCOUNT_PAYMENT, request);
		StaticHibernateUtil.commitTransaction();
		StaticHibernateUtil.getSessionTL().evict(savings);
		return mapping.findForward("close_success");
	}

	private CustomerBO searchForCustomer(HttpServletRequest request,
			String customerId) throws Exception {
		Object obj = SessionUtils.getAttribute(SavingsConstants.CLIENT_LIST,
				request);
		CustomerBO client = null;
		if (obj != null && customerId != null && customerId != "") {
			List<CustomerBO> customerList = (List<CustomerBO>) obj;
			for (CustomerBO customer : customerList) {
				if (customer.getCustomerId()
						.equals(Integer.valueOf(customerId))) {
					client = customer;
					break;
				}
			}
		}
		return client;
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsClosureAction::cancel()");
		return mapping.findForward("close_success");
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");
		logger.debug("In SavingsClosureAction::validate(), method: " + method);
		String forward = null;
		if (method != null && method.equals("preview"))
			forward = "preview_faliure";
		return mapping.findForward(forward);
	}

	private void clearActionForm(ActionForm form) {
		SavingsClosureActionForm actionForm = (SavingsClosureActionForm) form;
		actionForm.setNotes(null);
		actionForm.setReceiptDate(null);
		actionForm.setReceiptId(null);
		actionForm.setPaymentTypeId(null);
		actionForm.setCustomerId(null);
	}

	private void initialize(GLCodeEntity glCode) {
		savingsService.initialize(glCode);
		savingsService.initialize(glCode.getAssociatedCOA());

		savingsService.initialize(glCode.getAssociatedCOA().getCOAHead());
		savingsService.initialize(glCode.getAssociatedCOA()
				.getAssociatedGlcode());
		savingsService.initialize(glCode.getAssociatedCOA().getSubCategory());

	}
}
