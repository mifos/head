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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.accounts.struts.actionforms.ApplyChargeActionForm;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.dto.domain.ApplicableCharge;
import org.mifos.dto.screen.AccountTypeCustomerLevelDto;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class ApplyChargeAction extends BaseAction {

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
        
        if (this.loanDao.findById(accountId) != null) {
            applicableCharges.addAll(this.loanAccountServiceFacade.getApplicablePenalties(accountId));
        }

        SessionUtils.setCollectionAttribute(AccountConstants.APPLICABLE_CHARGE_LIST, applicableCharges, request);

        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        ApplyChargeActionForm applyChargeActionForm = (ApplyChargeActionForm) form;

        Short feeId = Short.valueOf(applyChargeActionForm.getFeeId());
        Double chargeAmount = getDoubleValue(request.getParameter("charge"));

        Integer accountId = Integer.valueOf(applyChargeActionForm.getAccountId());
        this.accountServiceFacade.applyCharge(accountId, feeId, chargeAmount, applyChargeActionForm.isPenaltyType());

        AccountTypeCustomerLevelDto accountTypeCustomerLevel = accountServiceFacade.getAccountTypeCustomerLevelDto(accountId);

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