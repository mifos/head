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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.accounts.struts.actionforms.ApplyChargeActionForm;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.servicefacade.GroupLoanAccountServiceFacade;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.dto.domain.ApplicableCharge;
import org.mifos.dto.domain.GroupIndividualLoanDto;
import org.mifos.dto.screen.AccountTypeCustomerLevelDto;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class ApplyChargeAction extends BaseAction {

    private GroupLoanAccountServiceFacade groupLoanService;
    
    public ApplyChargeAction() {
        this.groupLoanService = ApplicationContextProvider.getBean(GroupLoanAccountServiceFacade.class);
    }
    
    @TransactionDemarcate(joinToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ApplyChargeActionForm applyChargeActionForm = (ApplyChargeActionForm) form;

        AccountBO account = ApplicationContextProvider.getBean(LegacyAccountDao.class).getAccount(Integer.valueOf(applyChargeActionForm.getAccountId()));
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, account, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, account, request.getSession());
        applyChargeActionForm.clear();
        request.removeAttribute(AccountConstants.APPLICABLE_CHARGE_LIST);

        Integer accountId = Integer.valueOf(request.getParameter("accountId"));
        List<ApplicableCharge> applicableCharges = this.accountServiceFacade.getApplicableFees(accountId);
        LoanBO loan = loanDao.findById(accountId);
        if (loan != null) {
            for(int i = applicableCharges.size() - 1; i >= 0 ; --i) {
                if(applicableCharges.get(i).getFeeId().equals(AccountConstants.MISC_PENALTY)) {
                    applicableCharges.remove(i);
                    break;
                }
            }
            
            applicableCharges.addAll(this.loanAccountServiceFacade.getApplicablePenalties(accountId));
        }

        SessionUtils.setCollectionAttribute(AccountConstants.APPLICABLE_CHARGE_LIST, applicableCharges, request);
        
        if (null != loan && (null == loan.getParentAccount() && loan.isGroupLoanAccount())) {
            SessionUtils.setAttribute(Constants.ACCOUNT_TYPE, "newGlim", request);
        }
        return mapping.findForward(ActionForwards.load_success.toString());
    }
    
    @TransactionDemarcate(joinToken = true)
    public ActionForward divide(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ApplyChargeActionForm chargeForm = (ApplyChargeActionForm) form;
        chargeForm.getIndividualValues().clear();
        
        List<GroupIndividualLoanDto> memberAccounts = groupLoanService.getMemberLoansAndDefaultPayments(Integer.valueOf(chargeForm.getAccountId()), new BigDecimal(chargeForm.getCharge()));
                    
        for(int i = 0 ; i < memberAccounts.size() ; i++) {
            chargeForm.getIndividualValues().put(memberAccounts.get(i).getAccountId(), String.valueOf(memberAccounts.get(i).getDefaultAmount().doubleValue()));
        }
        
        List<LoanBO> memberInfos = getMemberAccountsInformation(chargeForm.getAccountId());
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
    
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        ApplyChargeActionForm applyChargeActionForm = (ApplyChargeActionForm) form;

        Short feeId = Short.valueOf(applyChargeActionForm.getFeeId());
        Double chargeAmount = 0.0;

        AccountBO account = new AccountBusinessService().getAccount(Integer.valueOf(applyChargeActionForm.getAccountId()));
        
        if (null == ((LoanBO)account).getParentAccount() && account.isGroupLoanAccount()) {
            this.accountServiceFacade.applyGroupCharge(applyChargeActionForm.getIndividualValues(), feeId, applyChargeActionForm.isPenaltyType());
        }
        else {
            chargeAmount = getDoubleValue(request.getParameter("charge"));
            this.accountServiceFacade.applyCharge(account.getAccountId(), feeId, chargeAmount, applyChargeActionForm.isPenaltyType());
        }

        AccountTypeCustomerLevelDto accountTypeCustomerLevel = accountServiceFacade.getAccountTypeCustomerLevelDto(account.getAccountId());

        return mapping.findForward(getDetailAccountPage(accountTypeCustomerLevel));
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        request.removeAttribute(AccountConstants.APPLICABLE_CHARGE_LIST);

        ApplyChargeActionForm applyChargeActionForm = (ApplyChargeActionForm) form;
        Integer accountId = Integer.valueOf(applyChargeActionForm.getAccountId());
        AccountTypeCustomerLevelDto accountTypeCustomerLevel = accountServiceFacade.getAccountTypeCustomerLevelDto(accountId);

        return mapping.findForward(getDetailAccountPage(accountTypeCustomerLevel));
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute(SavingsConstants.METHODCALLED);
        String forward = null;
        if (method != null) {
            if (method.equals(Methods.update.toString())) {
                forward = ActionForwards.update_failure.toString();
            }
            else if (method.equals(Methods.create.toString())) {
                forward = "divide";
            }
        }
        return mapping.findForward(forward);
    }

    private String getDetailAccountPage(AccountTypeCustomerLevelDto accountTypeCustomerLevel) {
        if (accountTypeCustomerLevel.getAccountType().equals(AccountTypes.LOAN_ACCOUNT.getValue())) {
            return "loanDetails_success";
        }
        if (accountTypeCustomerLevel.getCustomerLevelId().equals(CustomerLevel.CLIENT.getValue())) {
            return "clientDetails_success";
        } else if (accountTypeCustomerLevel.getCustomerLevelId().equals(CustomerLevel.GROUP.getValue())) {
            return "groupDetails_success";
        } else {
            return "centerDetails_success";
        }
    }
}