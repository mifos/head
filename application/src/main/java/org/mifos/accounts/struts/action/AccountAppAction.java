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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.WaiveEnum;
import org.mifos.application.util.helpers.TransactionHistoryDtoComperator;
import org.mifos.config.AccountingRules;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.center.util.helpers.CenterConstants;
import org.mifos.dto.screen.TransactionHistoryDto;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.UserContext;

public class AccountAppAction extends BaseAction {
    private static final String LOAN_DISBURSMENT = "Loan Disbursement";
    
    private AccountBusinessService accountBusinessService;

    public AccountAppAction() {
        this.accountBusinessService = new AccountBusinessService();
    }

    public AccountAppAction(AccountBusinessService accountBusinessService) {
        this.accountBusinessService = accountBusinessService;
    }

    @Override
    protected BusinessService getService() {
        return getAccountBusinessService();
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getTrxnHistory(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String globalAccountNum = request.getParameter("globalAccountNum");
        AccountBO accountBO = getAccountBusinessService().findBySystemId(globalAccountNum);
        List<TransactionHistoryDto>  transactionHistoryDto = this.centerServiceFacade.retrieveAccountTransactionHistory(globalAccountNum);
        
        if  (accountBO.isGroupLoanAccount() && null == ((LoanBO)accountBO).getParentAccount()) {
            SessionUtils.setAttribute(Constants.TYPE_OF_GROUP_LOAN, "parentAcc", request);
        }
        else if (accountBO.isGroupLoanAccount() && null != ((LoanBO)accountBO).getParentAccount()) {
            SessionUtils.setAttribute(Constants.TYPE_OF_GROUP_LOAN, "memberAcc", request);
        }
        
        SessionUtils.setCollectionAttribute(SavingsConstants.TRXN_HISTORY_LIST, transactionHistoryDto, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO, request);
        request.setAttribute("GlNamesMode", AccountingRules.getGlNamesMode());
        return mapping.findForward("getTransactionHistory_success");
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward removeFees(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        Integer accountId = getIntegerValue(request.getParameter("accountId"));
        Short feeId = getShortValue(request.getParameter("feeId"));

        this.centerServiceFacade.removeAccountFee(accountId, feeId);

        AccountBO accountBO = getAccountBusinessService().getAccount(accountId);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO, request);

        String fromPage = request.getParameter(CenterConstants.FROM_PAGE);
        StringBuilder forward = new StringBuilder();
        forward = forward.append(AccountConstants.REMOVE + "_" + fromPage + "_" + AccountConstants.CHARGES);
        if (fromPage != null) {
            return mapping.findForward(forward.toString());
        }
        return mapping.findForward(AccountConstants.REMOVE_SUCCESS);
    }
    
    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward removePenalties(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        Integer accountId = getIntegerValue(request.getParameter("accountId"));
        Short penaltyId = getShortValue(request.getParameter("penaltyId"));
        
        AccountBO accountBO = getAccountBusinessService().getAccount(accountId);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO, request);
        
        if (accountBO instanceof LoanBO) {
            this.loanAccountServiceFacade.removeLoanPenalty(accountId, penaltyId);
        }
        
        String fromPage = request.getParameter(CenterConstants.FROM_PAGE);
        StringBuilder forward = new StringBuilder();
        forward = forward.append(AccountConstants.REMOVE + "_" + fromPage + "_" + AccountConstants.CHARGES);
        if (fromPage != null) {
            return mapping.findForward(forward.toString());
        }
        return mapping.findForward(AccountConstants.REMOVE_SUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward waiveChargeDue(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession());
        Integer accountId = getIntegerValue(request.getParameter("accountId"));

        WaiveEnum waiveEnum = getWaiveType(request.getParameter(AccountConstants.WAIVE_TYPE));
        this.centerServiceFacade.waiveChargesDue(accountId, waiveEnum.ordinal());

        AccountBO account = getAccountBusinessService().getAccount(accountId);
        account.updateDetails(uc);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, account, request);

        return mapping.findForward("waiveChargesDue_Success");
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward waiveChargeOverDue(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession());
        Integer accountId = getIntegerValue(request.getParameter("accountId"));
        WaiveEnum waiveEnum = getWaiveType(request.getParameter(AccountConstants.WAIVE_TYPE));

        this.centerServiceFacade.waiveChargesOverDue(accountId, waiveEnum.ordinal());

        AccountBO account = getAccountBusinessService().getAccount(accountId);
        account.updateDetails(uc);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, account, request);

        return mapping.findForward("waiveChargesOverDue_Success");
    }

    private WaiveEnum getWaiveType(String waiveType) {
        if (waiveType != null) {
            if (waiveType.equalsIgnoreCase(WaiveEnum.PENALTY.toString())) {
                return WaiveEnum.PENALTY;
            }
            if (waiveType.equalsIgnoreCase(WaiveEnum.FEES.toString())) {
                return WaiveEnum.FEES;
            }
        }
        return WaiveEnum.ALL;
    }

    protected CustomerBO getCustomer(Integer customerId) {
        return this.customerDao.findCustomerById(customerId);
    }

    protected AccountBusinessService getAccountBusinessService() {
        return accountBusinessService;
    }
}
