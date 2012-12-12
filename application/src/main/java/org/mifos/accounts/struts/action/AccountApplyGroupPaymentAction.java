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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.acceptedpaymenttype.persistence.LegacyAcceptedPaymentTypeDao;
import org.mifos.accounts.api.AccountService;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.servicefacade.AccountPaymentDto;
import org.mifos.accounts.servicefacade.AccountServiceFacade;
import org.mifos.accounts.servicefacade.AccountTypeDto;
import org.mifos.accounts.struts.actionforms.AccountApplyPaymentActionForm;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.servicefacade.GroupLoanAccountServiceFacade;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.dto.domain.AccountPaymentParametersDto;
import org.mifos.dto.domain.AccountReferenceDto;
import org.mifos.dto.domain.GroupIndividualLoanDto;
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
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class AccountApplyGroupPaymentAction extends BaseAction {
    
    private final static String ZERO_PAYMENT = "0.0";
    private final static String LOAN_TYPE = "loanType";
    private final static String PARENT = "parent";
    private final static String MEMBER = "member";
    private AccountService accountService = null;
    private GroupLoanAccountServiceFacade groupLoanService;
    private LoanAccountServiceFacade loanService;
    
    public AccountApplyGroupPaymentAction() throws Exception {
        accountService = ApplicationContextProvider.getBean(AccountService.class);
        groupLoanService = ApplicationContextProvider.getBean(GroupLoanAccountServiceFacade.class);
        loanService = ApplicationContextProvider.getBean(LoanAccountServiceFacade.class);
    }

    @Deprecated
    // For unit testing
    public AccountApplyGroupPaymentAction(AccountServiceFacade accountServiceFacade,
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
        
        LoanBO loan = loanDao.findById(accountReferenceDto.getAccountId());
        if (loan.isGroupLoanAccountParent()) {
            SessionUtils.setAttribute(LOAN_TYPE, PARENT, request);
        }
        else if (loan.isGroupLoanAccountMember()) {
            SessionUtils.setAttribute(LOAN_TYPE, MEMBER, request);
        }

        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward divide(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        AccountApplyPaymentActionForm actionForm = (AccountApplyPaymentActionForm) form;
        actionForm.getIndividualValues().clear();
        
        List<GroupIndividualLoanDto> memberAccounts = groupLoanService.getMemberLoansAndDefaultPayments(Integer.valueOf(actionForm.getAccountId()), new BigDecimal(actionForm.getAmount()));
                    
        for(int i = 0 ; i < memberAccounts.size() ; i++) {
            actionForm.getIndividualValues().put(memberAccounts.get(i).getAccountId(), String.valueOf(memberAccounts.get(i).getDefaultAmount().doubleValue()));
        }
        
        List<LoanBO> memberInfos = getMemberAccountsInformation(actionForm.getAccountId());
        SessionUtils.setCollectionAttribute("memberInfos", memberInfos, request);
        
        return mapping.findForward("divide");
    }

    private List<LoanBO> getMemberAccountsInformation(String accountId) {
        List<LoanBO> membersInfo = new ArrayList<LoanBO>();
        for (LoanBO memberAcc : loanDao.findById(Integer.valueOf(accountId)).getMemberAccounts()) {
            membersInfo.add(memberAcc);
        }
        return membersInfo;
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
        AccountApplyPaymentActionForm accountApplyPaymentActionForm = (AccountApplyPaymentActionForm) form;
        
        Double oldAmmount = Double.valueOf(accountApplyPaymentActionForm.getAmount());
        Double newAmounts = 0.0;
        if (!accountApplyPaymentActionForm.getIndividualValues().isEmpty()) {
            for(String amount : accountApplyPaymentActionForm.getIndividualValues().values()) {
                newAmounts += Double.valueOf(amount);
            }
            
            if (!oldAmmount.equals(newAmounts)) {
                accountApplyPaymentActionForm.setAmount(newAmounts.toString());
            }
        }
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
        String paymentType = request.getParameter(Constants.INPUT);
        Integer accountId;
        if (actionForm.getAccountId().isEmpty() || actionForm.getAccountId() == null) {
            accountId = loanDao.findByGlobalAccountNum(actionForm.getGlobalAccountNum()).getAccountId();
        }
        else {
            accountId = Integer.valueOf(actionForm.getAccountId());
        }
        UserReferenceDto userReferenceDto = new UserReferenceDto(userContext.getId());
        
        AccountPaymentDto accountPaymentDto = accountServiceFacade.getAccountPaymentInformation(accountId, paymentType,
                userContext.getLocaleId(), userReferenceDto, actionForm.getTrxnDate());

        validateAccountPayment(accountPaymentDto, accountId, request);
        validateAmount(accountPaymentDto, actionForm.getAmount());

        PaymentTypeDto paymentTypeDto;
        String amount = actionForm.getAmount();
        if (accountPaymentDto.getAccountType().equals(AccountTypeDto.LOAN_ACCOUNT) ||
                accountPaymentDto.getAccountType().equals(AccountTypeDto.GROUP_LOAN_ACCOUNT)) {
            paymentTypeDto = getLoanPaymentTypeDtoForId(Short.valueOf(actionForm.getPaymentTypeId()));
        } else {
            paymentTypeDto = getFeePaymentTypeDtoForId(Short.valueOf(actionForm.getPaymentTypeId()));
        }
        AccountPaymentParametersDto accountPaymentParametersDto;
        if (isGroupParentAccount(accountId)) {
            accountPaymentParametersDto = new AccountPaymentParametersDto(userReferenceDto,
                    new AccountReferenceDto(accountId), new BigDecimal(amount), actionForm.getTrxnDateAsLocalDate(),
                    paymentTypeDto, AccountConstants.NO_COMMENT, actionForm.getReceiptDateAsLocalDate(),
                    actionForm.getReceiptId(), accountPaymentDto.getCustomerDto(), actionForm.getIndividualValues());
        }
        else if (isGroupMemberAccount(accountId)) {
            accountPaymentParametersDto = preparePaymentParametersDto(accountId, userReferenceDto, amount,
                    actionForm, paymentTypeDto, userContext, paymentType);
        }
        else {
            accountPaymentParametersDto = new AccountPaymentParametersDto(userReferenceDto,
                    new AccountReferenceDto(accountId), new BigDecimal(amount), actionForm.getTrxnDateAsLocalDate(),
                    paymentTypeDto, AccountConstants.NO_COMMENT, actionForm.getReceiptDateAsLocalDate(),
                    actionForm.getReceiptId(), accountPaymentDto.getCustomerDto());
        }

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
    
    private boolean isGroupParentAccount(Integer accountId) {
        return this.loanDao.findById(accountId).isGroupLoanAccountParent();
    }
    
    private boolean isGroupMemberAccount(Integer accountId) {
        return this.loanDao.findById(accountId).isGroupLoanAccountMember();
    }
    
    private AccountPaymentParametersDto preparePaymentParametersDto(Integer accountId, UserReferenceDto userReferenceDto, String amount,
            AccountApplyPaymentActionForm actionForm, PaymentTypeDto paymentTypeDto, UserContext userContext, String paymentType) throws InvalidDateException {

        LoanBO acctualMemberAccount = loanDao.findById(accountId);
        LoanBO parrentAccount = acctualMemberAccount.getParentAccount();
        Integer parentAccountId = parrentAccount.getAccountId();
        
        AccountPaymentDto accountPaymentDto = accountServiceFacade.getAccountPaymentInformation(parentAccountId, paymentType,
                userContext.getLocaleId(), userReferenceDto, actionForm.getTrxnDate());
        
        HashMap<Integer, String> individualValues = new HashMap<Integer, String>();
        for (LoanBO member : loanDao.findIndividualLoans(parentAccountId)) {
            if (member.getAccountId().equals(acctualMemberAccount.getAccountId())){
                individualValues.put(member.getAccountId(), amount);
            }
            else {
                individualValues.put(member.getAccountId(), ZERO_PAYMENT);
            }
        }
        
        return new AccountPaymentParametersDto(userReferenceDto,
                new AccountReferenceDto(parentAccountId), new BigDecimal(amount), actionForm.getTrxnDateAsLocalDate(),
                paymentTypeDto, AccountConstants.NO_COMMENT, actionForm.getReceiptDateAsLocalDate(),
                actionForm.getReceiptId(), accountPaymentDto.getCustomerDto(), individualValues);
    }

    private void validateAmount(AccountPaymentDto accountPaymentDto, String amount) throws ApplicationException {
        if (new BigDecimal(amount).compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApplicationException("errors.invalid_amount_according_to_due");
        }
        if (!accountPaymentDto.getAccountType().equals(AccountTypeDto.LOAN_ACCOUNT) &&
                !accountPaymentDto.getAccountType().equals(AccountTypeDto.GROUP_LOAN_ACCOUNT)) {
            if (new BigDecimal(amount).compareTo(new BigDecimal(accountPaymentDto.getTotalPaymentDue())) > 0) {
                throw new ApplicationException("errors.invalid_amount_according_to_due");
            }
        }
    }

    private void validateAccountPayment(AccountPaymentDto accountPaymentDto, Integer accountId, HttpServletRequest request) throws Exception {
        checkPermission(accountId);
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