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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.business.AccountActionDateEntity;
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
import org.mifos.accounts.util.helpers.AccountPaymentData;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.accounts.util.helpers.SavingsPaymentData;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.config.AccountingRules;
import org.mifos.customers.business.service.CustomerBusinessService;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.util.helpers.ChildrenStateType;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class SavingsDepositWithdrawalAction extends BaseAction {

    private static final MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);

    private SavingsBusinessService savingsService;
    private AccountBusinessService accountsService;

    @Override
    protected BusinessService getService() throws ServiceException {
        return getSavingsService();
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(final String method) {
        return true;
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
            final HttpServletResponse response) throws Exception {
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
                || savings.getCustomer().getCustomerLevel().getId().shortValue() == CustomerLevel.GROUP.getValue() && savings
                        .getRecommendedAmntUnit().getId().equals(RecommendedAmountUnit.PER_INDIVIDUAL.getValue())) {
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
            final HttpServletResponse response) throws Exception {
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
                actionForm.setAmount(new Money(savings.getCurrency(),"0").toString());
                SessionUtils.setCollectionAttribute(MasterConstants.PAYMENT_TYPE, persistence
                        .getAcceptedPaymentTypesForATransaction(uc.getLocaleId(), TrxnTypes.savings_withdrawal
                                .getValue()), request);
            }
        }
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        doCleanUp(request);
        return mapping.findForward(ActionForwards.account_details_page.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward makePayment(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
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

        Short trxnTypeId = Short.valueOf(actionForm.getTrxnTypeId());
        if (trxnTypeId.equals(AccountActionTypes.SAVINGS_DEPOSIT.getValue())) {
            savings.applyPaymentWithPersist(createPaymentDataForDeposit(actionForm, uc, savings));
        } else if (trxnTypeId.equals(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue())) {
            boolean persist = true;
            savings.withdraw(createPaymentData(actionForm, uc, savings.getCurrency()), persist);
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

    private PaymentData createPaymentData(final SavingsDepositWithdrawalActionForm actionForm, final UserContext uc,
            final MifosCurrency currency)
            throws Exception {
        Date trxnDate = getDateFromString(actionForm.getTrxnDate(), uc.getPreferredLocale());
        Date receiptDate = getDateFromString(actionForm.getReceiptDate(), uc.getPreferredLocale());
        PaymentData paymentData = PaymentData.createPaymentData(
                new Money(currency, actionForm.getAmount()), new PersonnelPersistence()
                .getPersonnel(uc.getId()), Short.valueOf(actionForm.getPaymentTypeId()), trxnDate);
        paymentData.setReceiptDate(receiptDate);
        paymentData.setReceiptNum(actionForm.getReceiptId());
        CustomerBusinessService customerService = (CustomerBusinessService) ServiceFactory.getInstance()
                .getBusinessService(BusinessServiceName.Customer);
        paymentData.setCustomer(customerService.getCustomer(Integer.valueOf(actionForm.getCustomerId())));
        return paymentData;
    }

    private PaymentData createPaymentDataForDeposit(final SavingsDepositWithdrawalActionForm actionForm, final UserContext uc,
            final SavingsBO savings) throws Exception {
        PaymentData paymentData = createPaymentData(actionForm, uc, savings.getCurrency());
        for (AccountActionDateEntity installment : savings.getTotalInstallmentsDue(Integer.valueOf(actionForm
                .getCustomerId()))) {
            AccountPaymentData accountPaymentData = new SavingsPaymentData(installment);
            paymentData.addAccountPaymentData(accountPaymentData);
        }
        return paymentData;

    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
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

    private SavingsBusinessService getSavingsService() throws ServiceException {
        if (savingsService == null) {
            savingsService = new SavingsBusinessService();
        }
        return savingsService;
    }

    private AccountBusinessService getAccountsService() throws ServiceException {
        if (accountsService == null) {
            accountsService = new AccountBusinessService();
        }
        return accountsService;
    }
}
