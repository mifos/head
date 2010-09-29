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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.LocalDate;
import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.business.AccountActionEntity;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.service.SavingsBusinessService;
import org.mifos.accounts.savings.struts.actionforms.SavingsDepositWithdrawalActionForm;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.config.AccountingRules;
import org.mifos.customers.util.helpers.ChildrenStateType;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.dto.domain.SavingsDepositDto;
import org.mifos.dto.domain.SavingsWithdrawalDto;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SavingsDepositWithdrawalAction extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(SavingsDepositWithdrawalAction.class);

    private SavingsBusinessService savingsService;
    private AccountBusinessService accountsService;

    @Override
    protected BusinessService getService() throws ServiceException {
        return getSavingsService();
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("savingsDepositWithdrawalAction");
        security.allow("load", SecurityConstants.SAVINGS_CAN_MAKE_DEPOSIT_WITHDRAWAL);
        security.allow("reLoad", SecurityConstants.SAVINGS_CAN_MAKE_DEPOSIT_WITHDRAWAL);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("makePayment", SecurityConstants.VIEW);
        return security;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward load(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        logger.debug("In SavingsDepositWithdrawalAction::load(), accountId: " + savings.getAccountId());
        UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
        SavingsDepositWithdrawalActionForm actionForm = (SavingsDepositWithdrawalActionForm) form;
        clearActionForm(actionForm);

        List<AccountActionEntity> trxnTypes = new ArrayList<AccountActionEntity>();
        trxnTypes.add(getAccountsService().getAccountAction(AccountActionTypes.SAVINGS_DEPOSIT.getValue(),
                uc.getLocaleId()));
        trxnTypes.add(getAccountsService().getAccountAction(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(),
                uc.getLocaleId()));
        SessionUtils.setCollectionAttribute(AccountConstants.TRXN_TYPES, trxnTypes, request);

        if (savings.getCustomer().getCustomerLevel().getId().shortValue() == CustomerLevel.CENTER.getValue()
                || savings.getCustomer().getCustomerLevel().getId().shortValue() == CustomerLevel.GROUP.getValue()
                && savings.getRecommendedAmntUnit().getId().equals(RecommendedAmountUnit.PER_INDIVIDUAL.getValue())) {
            SessionUtils.setCollectionAttribute(SavingsConstants.CLIENT_LIST, savings.getCustomer().getChildren(
                    CustomerLevel.CLIENT, ChildrenStateType.ACTIVE_AND_ONHOLD), request);
        } else {
            SessionUtils.setAttribute(SavingsConstants.CLIENT_LIST, null, request);
        }
        AcceptedPaymentTypePersistence persistence = new AcceptedPaymentTypePersistence();
        SessionUtils.setCollectionAttribute(MasterConstants.PAYMENT_TYPE, persistence
                .getAcceptedPaymentTypesForATransaction(uc.getLocaleId(), TrxnTypes.savings_deposit.getValue()),
                request);

        SessionUtils.setAttribute(SavingsConstants.IS_BACKDATED_TRXN_ALLOWED, new Boolean(AccountingRules
                .isBackDatedTxnAllowed()), request);

        actionForm.setTrxnDate(DateUtils.getCurrentDate(uc.getPreferredLocale()));
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward reLoad(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        SavingsBO savingsInSession = (SavingsBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        SavingsBO savings = getSavingsService().findById(savingsInSession.getAccountId());
        savings.setVersionNo(savingsInSession.getVersionNo());
        savingsInSession = null;
        logger.debug("In SavingsDepositWithdrawalAction::reload(), accountId: " + savings.getAccountId());
        SavingsDepositWithdrawalActionForm actionForm = (SavingsDepositWithdrawalActionForm) form;
        if (actionForm.getTrxnTypeId() != null && actionForm.getTrxnTypeId() != Constants.EMPTY_STRING) {
            Short trxnTypeId = Short.valueOf(actionForm.getTrxnTypeId());
            // added for defect 1587 [start]
            AcceptedPaymentTypePersistence persistence = new AcceptedPaymentTypePersistence();
            UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
            if (trxnTypeId.equals(AccountActionTypes.SAVINGS_DEPOSIT.getValue())) {
                if (actionForm.getCustomerId() != null && actionForm.getCustomerId() != Constants.EMPTY_STRING) {
                    actionForm.setAmount(savings.getTotalPaymentDue(Integer.valueOf(actionForm.getCustomerId()))
                            .toString());
                }
                SessionUtils.setCollectionAttribute(MasterConstants.PAYMENT_TYPE,
                        persistence.getAcceptedPaymentTypesForATransaction(uc.getLocaleId(), TrxnTypes.savings_deposit
                                .getValue()), request);
            } else {
                actionForm.setAmount(new Money(savings.getCurrency(), "0").toString());
                SessionUtils.setCollectionAttribute(MasterConstants.PAYMENT_TYPE, persistence
                        .getAcceptedPaymentTypesForATransaction(uc.getLocaleId(), TrxnTypes.savings_withdrawal
                                .getValue()), request);
            }
        }
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(final ActionMapping mapping, @SuppressWarnings("unused") final ActionForm form, @SuppressWarnings("unused") final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(final ActionMapping mapping, @SuppressWarnings("unused") final ActionForm form, @SuppressWarnings("unused") final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(final ActionMapping mapping, @SuppressWarnings("unused") final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        doCleanUp(request);
        return mapping.findForward(ActionForwards.account_details_page.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward makePayment(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        SavingsBO savedAccount = (SavingsBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        SavingsBO savings = getSavingsService().findById(savedAccount.getAccountId());
        checkVersionMismatch(savedAccount.getVersionNo(), savings.getVersionNo());
        savings.setVersionNo(savedAccount.getVersionNo());
        logger.debug("In SavingsDepositWithdrawalAction::makePayment(), accountId: " + savings.getAccountId());
        SavingsDepositWithdrawalActionForm actionForm = (SavingsDepositWithdrawalActionForm) form;
        UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
        Date trxnDate = getDateFromString(actionForm.getTrxnDate(), uc.getPreferredLocale());

        if (!savings.isTrxnDateValid(trxnDate)) {
            throw new AccountException(AccountConstants.ERROR_INVALID_TRXN);
        }

        Long savingsId = Long.valueOf(savings.getAccountId());
        Long customerId = Long.valueOf(savings.getCustomer().getCustomerId());
        Locale preferredLocale = uc.getPreferredLocale();
        LocalDate dateOfDepositOrWithdrawalTransaction = new LocalDate(trxnDate);
        Double amount = Double.valueOf(actionForm.getAmount());
        Integer modeOfPayment = Integer.valueOf(actionForm.getPaymentTypeId());
        String receiptId = actionForm.getReceiptId();
        LocalDate dateOfReceipt = null;
        if (StringUtils.isNotBlank(actionForm.getReceiptDate())) {
             dateOfReceipt = new LocalDate(getDateFromString(actionForm.getReceiptDate(), preferredLocale));
        }

        try {
            Short trxnTypeId = Short.valueOf(actionForm.getTrxnTypeId());
            if (trxnTypeId.equals(AccountActionTypes.SAVINGS_DEPOSIT.getValue())) {

                SavingsDepositDto savingsDeposit = new SavingsDepositDto(savingsId, customerId,
                        dateOfDepositOrWithdrawalTransaction, amount, modeOfPayment, receiptId, dateOfReceipt,
                        preferredLocale);
                this.savingsServiceFacade.deposit(savingsDeposit);

            } else if (trxnTypeId.equals(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue())) {

                SavingsWithdrawalDto savingsWithdrawal = new SavingsWithdrawalDto(savingsId, customerId,
                        dateOfDepositOrWithdrawalTransaction, amount, modeOfPayment, receiptId, dateOfReceipt,
                        preferredLocale);
                this.savingsServiceFacade.withdraw(savingsWithdrawal);
            }
        } catch (BusinessRuleException e) {
            throw new AccountException(e.getMessageKey(), e);
        }

        return mapping.findForward(ActionForwards.account_details_page.toString());
    }

    private void clearActionForm(final SavingsDepositWithdrawalActionForm actionForm) {
        actionForm.setReceiptDate(null);
        actionForm.setReceiptId(null);
        actionForm.setPaymentTypeId(null);
        actionForm.setTrxnTypeId(null);
        actionForm.setCustomerId(null);
        actionForm.setAmount(Constants.EMPTY_STRING);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(final ActionMapping mapping, @SuppressWarnings("unused") final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        String forward = null;
        if (method != null) {
            forward = method + "_failure";
        }
        return mapping.findForward(forward);
    }

    private void doCleanUp(final HttpServletRequest request) throws Exception {
        SessionUtils.removeAttribute(AccountConstants.TRXN_TYPES, request);
        SessionUtils.removeAttribute(SavingsConstants.CLIENT_LIST, request);
        SessionUtils.removeAttribute(MasterConstants.PAYMENT_TYPE, request);
    }

    private SavingsBusinessService getSavingsService() {
        if (savingsService == null) {
            savingsService = new SavingsBusinessService();
        }
        return savingsService;
    }

    private AccountBusinessService getAccountsService() {
        if (accountsService == null) {
            accountsService = new AccountBusinessService();
        }
        return accountsService;
    }
}
