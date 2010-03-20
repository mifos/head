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

package org.mifos.accounts.loan.struts.action;

import static org.mifos.framework.util.helpers.DateUtils.getUserLocaleDate;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.acceptedpaymenttype.business.service.AcceptedPaymentTypeService;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.struts.action.validate.ProductMixValidator;
import org.mifos.accounts.loan.struts.actionforms.LoanDisbursementActionForm;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.config.AccountingRules;
import org.mifos.config.AccountingRulesConstants;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class LoanDisbursementAction extends BaseAction {

    private LoanBusinessService loanBusinessService = null;
    private final ProductMixValidator productMixValidator;

    LoanDisbursementAction(final LoanBusinessService service, final ProductMixValidator validator) {
        this.loanBusinessService = service;
        this.productMixValidator = validator;
    }

    public LoanDisbursementAction() {
        this(new LoanBusinessService(), new ProductMixValidator());
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("loanDisbursementAction");
        security.allow("load", SecurityConstants.LOAN_CAN_DISBURSE_LOAN);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.VIEW);
        return security;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward load(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        setIsRepaymentScheduleEnabled(request);

        setIsBackdatedTransactionAllowed(request);
        LoanDisbursementActionForm loanDisbursementActionForm = (LoanDisbursementActionForm) form;
        loanDisbursementActionForm.clear();
        loanDisbursementActionForm.setAmountCannotBeZero(false);

        productMixValidator.checkIfProductsOfferingCanCoexist(getLoan(loanDisbursementActionForm));

        Date currentDate = new DateTimeService().getCurrentJavaDateTime();
        LoanBO loan = getLoan(loanDisbursementActionForm);
        setProposedDisbursementDate(request, currentDate, loan);
        loanDisbursementActionForm.setTransactionDate(getUserLocaleDate(getUserContext(request).getPreferredLocale(),
                getProposedDisbursementDateFromSession(request)));

        UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
        loan.setUserContext(uc);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);

        SessionUtils.setCollectionAttribute(MasterConstants.PAYMENT_TYPE, getAcceptedPaymentTypes(uc.getLocaleId()),
                request);
        loanDisbursementActionForm.setAmount(loan.getAmountTobePaidAtdisburtail().toString());
        loanDisbursementActionForm.setLoanAmount(loan.getLoanAmount().toString());
        return mapping.findForward(Constants.LOAD_SUCCESS);
    }

    private LoanBO getLoan(final LoanDisbursementActionForm loanDisbursementActionForm) throws ServiceException {
        return ((LoanBusinessService) getService()).getAccount(Integer
                .valueOf(loanDisbursementActionForm.getAccountId()));
    }

    private static List<PaymentTypeEntity> getAcceptedPaymentTypes(final Short localeId) throws Exception {
        return AcceptedPaymentTypeService.getAcceptedPaymentTypes(localeId);
    }

    private String getProposedDisbursementDateFromSession(final HttpServletRequest request) throws PageExpiredException {
        return SessionUtils.getAttribute(LoanConstants.PROPOSED_DISBURSAL_DATE, request).toString();
    }

    private void setProposedDisbursementDate(final HttpServletRequest request, final Date currentDate, final LoanBO loan)
            throws PageExpiredException {
        if (AccountingRules.isBackDatedTxnAllowed()) {
            SessionUtils.setAttribute(LoanConstants.PROPOSED_DISBURSAL_DATE, loan.getDisbursementDate(), request);
        } else {
            SessionUtils.setAttribute(LoanConstants.PROPOSED_DISBURSAL_DATE, DateUtils.toDatabaseFormat(currentDate), request);
        }
    }

    private void setIsBackdatedTransactionAllowed(final HttpServletRequest request) throws PageExpiredException {
        SessionUtils.setAttribute(AccountingRulesConstants.BACKDATED_TRANSACTIONS_ALLOWED, AccountingRules
                .isBackDatedTxnAllowed(), request);
    }

    private void setIsRepaymentScheduleEnabled(final HttpServletRequest request) throws PageExpiredException,
            PersistenceException {
        SessionUtils.setAttribute(LoanConstants.REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED,
                new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled() ? 1 : 0, request);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {

        return mapping.findForward(Constants.PREVIEW_SUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        return mapping.findForward(Constants.PREVIOUS_SUCCESS);
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward update(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {

        LoanBO savedloan = (LoanBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        LoanDisbursementActionForm actionForm = (LoanDisbursementActionForm) form;
        LoanBO loan = getLoan(actionForm);
        checkVersionMismatch(savedloan.getVersionNo(), loan.getVersionNo());
        loan.setVersionNo(savedloan.getVersionNo());
        UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
        Date trxnDate = getDateFromString(actionForm.getTransactionDate(), uc.getPreferredLocale());
        trxnDate = DateUtils.getDateWithoutTimeStamp(trxnDate.getTime());
        Date receiptDate = getDateFromString(actionForm.getReceiptDate(), uc.getPreferredLocale());
        PersonnelBO personnel = new PersonnelPersistence().getPersonnel(uc.getId());

        if (!loan.isTrxnDateValid(trxnDate)) {

            if (AccountingRules.isBackDatedTxnAllowed()) {
                throw new AccountException("errors.invalidTxndate");
            } else {
                throw new AccountException("errors.invalidTxndate");
            }
        }

        if (loan.getCustomer().hasActiveLoanAccountsForProduct(loan.getLoanOffering())) {
            throw new AccountException("errors.cannotDisburseLoan.because.otherLoansAreActive");
        }

        String modeOfPayment = actionForm.getPaymentModeOfPayment();
        Short modeOfPaymentId = StringUtils.isEmpty(modeOfPayment) ? PaymentTypes.CASH.getValue() : Short
                .valueOf(modeOfPayment);

        loan.disburseLoan(actionForm.getReceiptId(), trxnDate, Short.valueOf(actionForm.getPaymentTypeId()), personnel,
                receiptDate, modeOfPaymentId);
        return mapping.findForward(Constants.UPDATE_SUCCESS);
    }

    private LoanBusinessService getLoanBusinessService() throws ServiceException {
        if (loanBusinessService == null) {
            loanBusinessService = new LoanBusinessService();
        }
        return loanBusinessService;
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

    @Override
    protected BusinessService getService() throws ServiceException {
        return getLoanBusinessService();
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(final String method) {
        return true;
    }

    @Override
    protected boolean isNewBizRequired(final HttpServletRequest request) throws ServiceException {
        return false;
    }

}
