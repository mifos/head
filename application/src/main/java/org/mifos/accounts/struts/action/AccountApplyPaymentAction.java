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

package org.mifos.accounts.struts.action;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.LocalDate;
import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.api.AccountPaymentParametersDto;
import org.mifos.accounts.api.AccountReferenceDto;
import org.mifos.accounts.api.PaymentTypeDto;
import org.mifos.accounts.api.StandardAccountService;
import org.mifos.accounts.api.UserReferenceDto;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.persistance.LoanPersistence;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.servicefacade.AccountPaymentDto;
import org.mifos.accounts.servicefacade.AccountServiceFacade;
import org.mifos.accounts.servicefacade.AccountTypeDto;
import org.mifos.accounts.servicefacade.WebTierAccountServiceFacade;
import org.mifos.accounts.struts.actionforms.AccountApplyPaymentActionForm;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class AccountApplyPaymentAction extends BaseAction {
    private AccountServiceFacade accountServiceFacade = new WebTierAccountServiceFacade();
    private StandardAccountService standardAccountService = null;
    private AccountBusinessService accountBusinessService = null;
    private AccountPersistence accountPersistence = new AccountPersistence();
    private List<PaymentTypeDto> loanPaymentTypeDtos;
    private List<PaymentTypeDto> feePaymentTypeDtos;

    public AccountApplyPaymentAction() throws Exception {
        standardAccountService = new StandardAccountService(accountPersistence, new LoanPersistence(), new AcceptedPaymentTypePersistence());
        loanPaymentTypeDtos = standardAccountService.getLoanPaymentTypes();
        feePaymentTypeDtos = standardAccountService.getFeePaymentTypes();
    }

    public StandardAccountService getStandardAccountService() {
        return standardAccountService;
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
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
        AccountApplyPaymentActionForm actionForm = (AccountApplyPaymentActionForm) form;
        clearActionForm(actionForm);
        actionForm.setTransactionDate(DateUtils.makeDateAsSentFromBrowser());

        AccountPaymentDto accountPaymentDto = accountServiceFacade.getAccountPaymentInformation(
                new AccountReferenceDto(Integer.valueOf(actionForm.getAccountId())),
                request.getParameter(Constants.INPUT), userContext.getLocaleId(),
                new UserReferenceDto(userContext.getId()));

        SessionUtils.setAttribute(Constants.ACCOUNT_VERSION, accountPaymentDto.getVersion(), request);
        SessionUtils.setAttribute(Constants.ACCOUNT_TYPE, accountPaymentDto.getAccountType().name(), request);
        SessionUtils.setAttribute(Constants.ACCOUNT_ID, Integer.valueOf(actionForm.getAccountId()), request);

        SessionUtils.setCollectionAttribute(MasterConstants.PAYMENT_TYPE,
                accountPaymentDto.getPaymentTypeList(), request);

        actionForm.setAmount(accountPaymentDto.getTotalPaymentDue().toString());
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward applyPayment(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws NumberFormatException, Exception {
        Integer savedAccountVersion = (Integer)SessionUtils.getAttribute(Constants.ACCOUNT_VERSION, request);
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());

        AccountApplyPaymentActionForm actionForm = (AccountApplyPaymentActionForm) form;
        AccountReferenceDto accountReferenceDto = new AccountReferenceDto(Integer.valueOf(actionForm.getAccountId()));
        AccountPaymentDto accountPaymentDto = accountServiceFacade.getAccountPaymentInformation(
                accountReferenceDto,
                request.getParameter(Constants.INPUT), userContext.getLocaleId(),
                new UserReferenceDto(userContext.getId()));

        checkVersionMismatch(savedAccountVersion, accountPaymentDto.getVersion());

        if (!accountServiceFacade.isPaymentPermitted(accountReferenceDto, userContext)) {
            throw new CustomerException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }

        try {
            Date trxnDate = DateUtils.getDateAsSentFromBrowser(actionForm.getTransactionDate());
            Date receiptDate = DateUtils.getDateAsSentFromBrowser(actionForm.getReceiptDate());
            PaymentTypeDto paymentTypeDto;

            String amount = "0";
            if (accountPaymentDto.getAccountType().equals(AccountTypeDto.LOAN_ACCOUNT)) {
                amount = actionForm.getAmount();
                paymentTypeDto = getLoanPaymentTypeDtoForId(Short.valueOf(actionForm.getPaymentTypeId()));
            } else {
                amount = accountPaymentDto.getTotalPaymentDue().toString();
                paymentTypeDto = getFeePaymentTypeDtoForId(Short.valueOf(actionForm.getPaymentTypeId()));
            }

            AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(
                    new UserReferenceDto(userContext.getId()),
                    new AccountReferenceDto(Integer.valueOf(actionForm.getAccountId())),
                    new BigDecimal(amount),
                    new LocalDate(trxnDate.getTime()),
                    paymentTypeDto,
                    "",
                    (receiptDate == null) ? null : new LocalDate(receiptDate.getTime()),
                    actionForm.getReceiptId());

            getStandardAccountService().makePayment(accountPaymentParametersDto);

            return mapping.findForward(getForward(((AccountApplyPaymentActionForm) form).getInput()));
        } catch (InvalidDateException ide) {
            throw new AccountException(ide);
        }
    }

    private PaymentTypeDto getLoanPaymentTypeDtoForId(short id) {
        for (PaymentTypeDto paymentTypeDto : loanPaymentTypeDtos) {
            if (paymentTypeDto.getValue() == id) {
                return paymentTypeDto;
            }
        }
        throw new MifosRuntimeException("Expected loan PaymentTypeDto not found for id: " + id);
    }

    private PaymentTypeDto getFeePaymentTypeDtoForId(short id) {
        for (PaymentTypeDto paymentTypeDto : feePaymentTypeDtos) {
            if (paymentTypeDto.getValue() == id) {
                return paymentTypeDto;
            }
        }
        throw new MifosRuntimeException("Expected fee PaymentTypeDto not found for id: " + id);

    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(getForward(((AccountApplyPaymentActionForm) form).getInput()));
    }

    private void clearActionForm(AccountApplyPaymentActionForm actionForm) throws InvalidDateException {
        actionForm.setReceiptDate(null);
        actionForm.setReceiptId(null);
        actionForm.setPaymentTypeId(null);
    }

    private String getForward(String input) {
        if (input.equals(Constants.LOAN)) {
            return ActionForwards.loan_detail_page.toString();
        }

        return "applyPayment_success";
    }

    private AccountBusinessService getAccountBusinessService() {
        if (accountBusinessService == null) {
            accountBusinessService = new AccountBusinessService();
        }
        return accountBusinessService;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
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
