/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.acceptedpaymenttype.persistence.LegacyAcceptedPaymentTypeDao;
import org.mifos.accounts.api.AccountService;
import org.mifos.accounts.servicefacade.AccountPaymentDto;
import org.mifos.accounts.servicefacade.AccountServiceFacade;
import org.mifos.accounts.servicefacade.AccountTypeDto;
import org.mifos.accounts.struts.actionforms.AccountApplyPaymentActionForm;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.admin.servicefacade.RolesPermissionServiceFacade;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.dto.domain.AccountPaymentParametersDto;
import org.mifos.dto.domain.AccountReferenceDto;
import org.mifos.dto.domain.PaymentTypeDto;
import org.mifos.dto.domain.UserReferenceDto;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class AccountApplyPaymentAction extends BaseAction {

    private AccountService accountService = null;
    
    public AccountApplyPaymentAction() throws Exception {
        accountService = ApplicationContextProvider.getBean(AccountService.class);
    }

    @Deprecated
    // For unit testing
    public AccountApplyPaymentAction(AccountServiceFacade accountServiceFacade,
            LegacyAcceptedPaymentTypeDao legacyAcceptedPaymentTypeDao) {
        this.accountServiceFacade = accountServiceFacade;
        this.legacyAcceptedPaymentTypeDao = legacyAcceptedPaymentTypeDao;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        UserContext userContext = getUserContext(request);
        AccountApplyPaymentActionForm actionForm = (AccountApplyPaymentActionForm) form;
        actionForm.setReceiptDate(null);
        actionForm.setReceiptId(null);
        actionForm.setPaymentTypeId(null);
        actionForm.setTransactionDate(DateUtils.makeDateAsSentFromBrowser());
        actionForm.setPrintReceipt(false);
        actionForm.setTruePrintReceipt(false);

        final AccountReferenceDto accountReferenceDto = new AccountReferenceDto(Integer.valueOf(actionForm.getAccountId()));
        AccountPaymentDto accountPaymentDto = accountServiceFacade.getAccountPaymentInformation(
                accountReferenceDto.getAccountId(), request.getParameter(Constants.INPUT), userContext.getLocaleId(),
                new UserReferenceDto(userContext.getId()), DateUtils.getCurrentJavaDateTime());

        setValuesInSession(request, actionForm, accountPaymentDto);
        actionForm.setLastPaymentDate(accountPaymentDto.getLastPaymentDate());
        actionForm.setAmount(accountPaymentDto.getTotalPaymentDue());
        actionForm.setTransferPaymentTypeId(this.legacyAcceptedPaymentTypeDao.getSavingsTransferId());
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    void setValuesInSession(HttpServletRequest request, AccountApplyPaymentActionForm actionForm, AccountPaymentDto accountPaymentDto) throws PageExpiredException {
        SessionUtils.setAttribute(Constants.ACCOUNT_VERSION, accountPaymentDto.getVersion(), request);
        SessionUtils.setAttribute(Constants.ACCOUNT_TYPE, accountPaymentDto.getAccountType().name(), request);
        SessionUtils.setAttribute(Constants.ACCOUNT_ID, Integer.valueOf(actionForm.getAccountId()), request);
        SessionUtils.setCollectionAttribute(MasterConstants.PAYMENT_TYPE, accountPaymentDto.getPaymentTypeList(), request);
        SessionUtils.setCollectionAttribute(Constants.ACCOUNTS_FOR_TRANSFER, accountPaymentDto.getSavingsAccountsFroTransfer(), request);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        
        //workaround for checkbox problem
        AccountApplyPaymentActionForm accountApplyPaymentActionForm = (AccountApplyPaymentActionForm) form;
        accountApplyPaymentActionForm.setTruePrintReceipt(accountApplyPaymentActionForm.getPrintReceipt());
        accountApplyPaymentActionForm.setPrintReceipt(false);
        
        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward applyPayment(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
        AccountApplyPaymentActionForm actionForm = (AccountApplyPaymentActionForm) form;
        Integer accountId = Integer.valueOf(actionForm.getAccountId());
        String paymentType = request.getParameter(Constants.INPUT);
        UserReferenceDto userReferenceDto = new UserReferenceDto(userContext.getId());
        AccountPaymentDto accountPaymentDto = accountServiceFacade.getAccountPaymentInformation(accountId, paymentType,
                userContext.getLocaleId(), userReferenceDto, actionForm.getTrxnDate());

        validateAccountPayment(accountPaymentDto, accountId, request);
        validateAmount(accountPaymentDto, actionForm.getAmount());

        PaymentTypeDto paymentTypeDto;
        String amount = actionForm.getAmount();
        if (accountPaymentDto.getAccountType().equals(AccountTypeDto.LOAN_ACCOUNT)) {
            paymentTypeDto = getLoanPaymentTypeDtoForId(Short.valueOf(actionForm.getPaymentTypeId()));
        } else {
            paymentTypeDto = getFeePaymentTypeDtoForId(Short.valueOf(actionForm.getPaymentTypeId()));
        }

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(userReferenceDto,
                new AccountReferenceDto(accountId), new BigDecimal(amount), actionForm.getTrxnDateAsLocalDate(),
                paymentTypeDto, AccountConstants.NO_COMMENT, actionForm.getReceiptDateAsLocalDate(),
                actionForm.getReceiptId(), accountPaymentDto.getCustomerDto());

        if (paymentTypeDto.getValue().equals(this.legacyAcceptedPaymentTypeDao.getSavingsTransferId())) {
            this.accountServiceFacade.makePaymentFromSavingsAcc(accountPaymentParametersDto,
                    actionForm.getAccountForTransfer());
        } else {
            this.accountServiceFacade.makePayment(accountPaymentParametersDto);
        }
        
        request.getSession().setAttribute("globalAccountNum", ((AccountApplyPaymentActionForm) form).getGlobalAccountNum());
        
        ActionForward findForward;
        if(actionForm.getPrintReceipt()){
            findForward = mapping.findForward(getForward("PRINT"));
        }
        else {
            findForward = mapping.findForward(getForward(((AccountApplyPaymentActionForm) form).getInput()));
        }
        return findForward;

    }

    private void validateAmount(AccountPaymentDto accountPaymentDto, String amount) throws ApplicationException {
        if (new BigDecimal(amount).compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApplicationException("errors.invalid_amount_according_to_due");
        }
        if (!accountPaymentDto.getAccountType().equals(AccountTypeDto.LOAN_ACCOUNT)) {
            if (new BigDecimal(amount).compareTo(new BigDecimal(accountPaymentDto.getTotalPaymentDue())) > 0) {
                throw new ApplicationException("errors.invalid_amount_according_to_due");
            }
        }
    }

    private void validateAccountPayment(AccountPaymentDto accountPaymentDto, Integer accountId, HttpServletRequest request) throws Exception {
        checkVersion(request, accountPaymentDto.getVersion());
        checkPermission(accountId);
    }

    private void checkVersion(HttpServletRequest request, int accountVersion) throws Exception {
        Integer savedAccountVersion = (Integer) SessionUtils.getAttribute(Constants.ACCOUNT_VERSION, request);
        checkVersionMismatch(savedAccountVersion, accountVersion);
    }

    private void checkPermission(Integer accountId) throws CustomerException {
        if (!accountServiceFacade.isPaymentPermitted(accountId)) {
            throw new CustomerException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }
    }

    private PaymentTypeDto getLoanPaymentTypeDtoForId(short id) throws Exception {
        for (PaymentTypeDto paymentTypeDto : accountService.getLoanPaymentTypes()) {
            if (paymentTypeDto.getValue() == id) {
                return paymentTypeDto;
            }
        }
        throw new MifosRuntimeException("Expected loan PaymentTypeDto not found for id: " + id);
    }

    private PaymentTypeDto getFeePaymentTypeDtoForId(short id) throws Exception {
        for (PaymentTypeDto paymentTypeDto : accountService.getFeePaymentTypes()) {
            if (paymentTypeDto.getValue() == id) {
                return paymentTypeDto;
            }
        }
        throw new MifosRuntimeException("Expected fee PaymentTypeDto not found for id: " + id);
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(getForward(((AccountApplyPaymentActionForm) form).getInput()));
    }

    private String getForward(String input) {
        if (input.equals(Constants.LOAN)) {
            return ActionForwards.loan_detail_page.toString();
        } else if (input.equals("PRINT")) {
            return ActionForwards.printPaymentReceipt.toString();
        }

        return "applyPayment_success";
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        String forward = null;
        
        //workaround for checkbox problem
        AccountApplyPaymentActionForm accountApplyPaymentActionForm = (AccountApplyPaymentActionForm) form;     
        accountApplyPaymentActionForm.setTruePrintReceipt(accountApplyPaymentActionForm.getPrintReceipt());
        accountApplyPaymentActionForm.setPrintReceipt(false);    
        
        if (method != null) {
            forward = method + "_failure";
        }
        return mapping.findForward(forward);
    }
}