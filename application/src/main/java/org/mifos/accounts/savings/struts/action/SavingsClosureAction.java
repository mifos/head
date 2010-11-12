/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.accounts.savings.struts.action;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.LocalDate;
import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.persistence.SavingsPersistence;
import org.mifos.accounts.savings.struts.actionforms.SavingsClosureActionForm;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.dto.domain.SavingsAccountClosureDto;
import org.mifos.dto.domain.SavingsWithdrawalDto;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class SavingsClosureAction extends BaseAction {

    public SavingsClosureAction() {
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("savingsClosureAction");
        security.allow("load", SecurityConstants.SAVINGS_CLOSE_SAVINGS_ACCOUNT);
        security.allow("preview", SecurityConstants.SAVINGS_CLOSE_SAVINGS_ACCOUNT);
        security.allow("previous", SecurityConstants.SAVINGS_CLOSE_SAVINGS_ACCOUNT);
        security.allow("close", SecurityConstants.SAVINGS_CLOSE_SAVINGS_ACCOUNT);
        return security;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        SavingsClosureActionForm actionForm = (SavingsClosureActionForm) form;
        actionForm.clearForm();

        UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
        SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

        Long savingsId = Long.valueOf(savings.getAccountId());
        savings = this.savingsDao.findById(savingsId);
        savings.setUserContext(uc);

        // NOTE: initialise so when error occurs when apply adjustment, savings object is correctly initialised for
        // use within Tag library in jsp.
        new SavingsPersistence().initialize(savings);

        LocalDate accountCloseDate = new LocalDate();
        SavingsAccountClosureDto closureDetails = this.savingsServiceFacade.retrieveClosingDetails(savingsId, accountCloseDate, uc.getLocaleId());

        AcceptedPaymentTypePersistence persistence = new AcceptedPaymentTypePersistence();
        List<PaymentTypeEntity> acceptedPaymentTypes = persistence.getAcceptedPaymentTypesForATransaction(uc.getLocaleId(), TrxnTypes.savings_withdrawal.getValue());

        Money interestAmountDue = new Money(savings.getCurrency(), closureDetails.getInterestAmountAtClosure());
        Money endOfAccountBalance = new Money(savings.getCurrency(), closureDetails.getBalance());
        Date transactionDate = closureDetails.getClosureDate().toDateMidnight().toDate();

        Money withdrawalAmountAtClosure = endOfAccountBalance.add(interestAmountDue);
        AccountPaymentEntity payment = new AccountPaymentEntity(savings, withdrawalAmountAtClosure, null, null, null, transactionDate);

        actionForm.setAmount(withdrawalAmountAtClosure.toString());
        actionForm.setTrxnDate(DateUtils.getCurrentDate(uc.getPreferredLocale()));

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        SessionUtils.setCollectionAttribute(MasterConstants.PAYMENT_TYPE, acceptedPaymentTypes, request);
        SessionUtils.setAttribute(SavingsConstants.ACCOUNT_PAYMENT, payment, request);

        return mapping.findForward("load_success");
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        SavingsClosureActionForm actionForm = (SavingsClosureActionForm) form;
        AccountPaymentEntity payment = (AccountPaymentEntity) SessionUtils.getAttribute(SavingsConstants.ACCOUNT_PAYMENT, request);
        AccountPaymentEntity accountPaymentEntity = null;
        Date transactionDate = new DateTimeService().getCurrentJavaDateTime();
        if (actionForm.getReceiptDate() != null && actionForm.getReceiptDate() != "") {
            accountPaymentEntity = new AccountPaymentEntity(payment.getAccount(), payment.getAmount(), actionForm
                    .getReceiptId(), new java.util.Date(DateUtils.getDateAsSentFromBrowser(actionForm.getReceiptDate())
                    .getTime()), new PaymentTypeEntity(Short.valueOf(actionForm.getPaymentTypeId())), transactionDate);
        } else {
            if (actionForm.getPaymentTypeId() != null && !actionForm.getPaymentTypeId().equals("")) {
                if (!(actionForm.getPaymentTypeId().equals(""))) {
                    accountPaymentEntity = new AccountPaymentEntity(payment.getAccount(), payment.getAmount(),
                            actionForm.getReceiptId(), null, new PaymentTypeEntity(Short.valueOf(actionForm
                                    .getPaymentTypeId())), transactionDate);
                } else {
                    accountPaymentEntity = new AccountPaymentEntity(payment.getAccount(), payment.getAmount(),
                            actionForm.getReceiptId(), null, new PaymentTypeEntity(), transactionDate);
                }
            } else {
                accountPaymentEntity = new AccountPaymentEntity(payment.getAccount(), payment.getAmount(), actionForm
                        .getReceiptId(), null, new PaymentTypeEntity(), transactionDate);
            }
        }
        SessionUtils.setAttribute(SavingsConstants.ACCOUNT_PAYMENT, accountPaymentEntity, request);
        return mapping.findForward("preview_success");
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward("previous_success");
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        SavingsClosureActionForm actionForm = (SavingsClosureActionForm) form;

        AccountPaymentEntity payment = (AccountPaymentEntity) SessionUtils.getAttribute(SavingsConstants.ACCOUNT_PAYMENT, request);
        SavingsBO savingsInSession = (SavingsBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

        Long savingsId = Long.valueOf(savingsInSession.getAccountId());
        SavingsBO savings = this.savingsDao.findById(savingsId);

        checkVersionMismatch(savingsInSession.getVersionNo(), savings.getVersionNo());
        savings.setUserContext(getUserContext(request));

        UserContext userContext = getUserContext(request);

        Long customerId = null;
        if (actionForm.getCustomerId() != null) {
            customerId = Long.valueOf(actionForm.getCustomerId());
        }
        LocalDate dateOfWithdrawal = new LocalDate(payment.getPaymentDate());
        Double amount = payment.getAmount().getAmountDoubleValue();
        Integer modeOfPayment = payment.getPaymentType().getId().intValue();
        String receiptId = payment.getReceiptNumber();
        LocalDate dateOfReceipt = null;
        if (payment.getReceiptDate() != null) {
            dateOfReceipt = new LocalDate(payment.getReceiptDate());
        }
        Locale preferredLocale = userContext.getPreferredLocale();
        SavingsWithdrawalDto closeAccount = new SavingsWithdrawalDto(savingsId, customerId, dateOfWithdrawal, amount, modeOfPayment, receiptId, dateOfReceipt, preferredLocale);

        this.savingsServiceFacade.closeSavingsAccount(savingsId, actionForm.getNotes(), closeAccount);

        SessionUtils.removeAttribute(SavingsConstants.ACCOUNT_PAYMENT, request);

        return mapping.findForward("close_success");
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward("close_success");
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        String forward = null;
        if (method != null && method.equals("preview")) {
            forward = "preview_faliure";
        }
        return mapping.findForward(forward);
    }
}
