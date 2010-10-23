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

package org.mifos.accounts.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.accounts.servicefacade.AccountServiceFacade;
import org.mifos.accounts.servicefacade.AccountTypeCustomerLevelDto;
import org.mifos.accounts.servicefacade.WebTierAccountServiceFacade;
import org.mifos.accounts.struts.actionforms.ApplyChargeActionForm;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.ApplicableCharge;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class ApplyChargeAction extends BaseAction {

    @Override
    protected BusinessService getService() throws ServiceException {
        return null;
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("applyChargeAction");
        security.allow("load", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.VIEW);
        return security;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        doCleanUp(request, form);
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request
                .getSession());
        Integer accountId = Integer.valueOf(request.getParameter("accountId"));

        AccountServiceFacade accountServiceFacade = new WebTierAccountServiceFacade();
        List<ApplicableCharge> applicableCharges = accountServiceFacade.getApplicableFees(accountId, userContext);
        SessionUtils.setCollectionAttribute(AccountConstants.APPLICABLE_CHARGE_LIST, applicableCharges, request);

        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request
                .getSession());
        ApplyChargeActionForm applyChargeActionForm = (ApplyChargeActionForm) form;

        Short feeId = Short.valueOf(applyChargeActionForm.getFeeId());
        Double chargeAmount = getDoubleValue(request.getParameter("charge"));

        Integer accountId = Integer.valueOf(applyChargeActionForm.getAccountId());
        AccountServiceFacade accountServiceFacade = new WebTierAccountServiceFacade();
        accountServiceFacade.applyCharge(accountId, userContext, feeId, chargeAmount);

        return mapping
                .findForward(getDetailAccountPage(accountServiceFacade.getAccountTypeCustomerLevelDto(accountId)));
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        request.removeAttribute(AccountConstants.APPLICABLE_CHARGE_LIST);

        ApplyChargeActionForm applyChargeActionForm = (ApplyChargeActionForm) form;
        Integer accountId = Integer.valueOf(applyChargeActionForm.getAccountId());

        AccountServiceFacade accountServiceFacade = new WebTierAccountServiceFacade();
        return mapping
                .findForward(getDetailAccountPage(accountServiceFacade.getAccountTypeCustomerLevelDto(accountId)));
    }

    private void doCleanUp(HttpServletRequest request, ActionForm form) {
        ApplyChargeActionForm applyChargeActionForm = (ApplyChargeActionForm) form;
        applyChargeActionForm.setAccountId(null);
        applyChargeActionForm.setChargeType(null);
        applyChargeActionForm.setChargeAmount(null);
        request.removeAttribute(AccountConstants.APPLICABLE_CHARGE_LIST);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
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
