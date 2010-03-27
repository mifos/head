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

package org.mifos.customers.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFlagMapping;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.customers.business.service.CustomerBusinessService;
import org.mifos.customers.struts.actionforms.CustActionForm;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.struts.action.SearchAction;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class CustAction extends SearchAction {
    private static final MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CUSTOMERLOGGER);

    @Override
    protected BusinessService getService() {
        return new DummyBusinessService();
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(@SuppressWarnings("unused") String method) {
        return true;
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("custAction");
        security.allow("getClosedAccounts", SecurityConstants.VIEW);
        security.allow("getBackToDetailsPage", SecurityConstants.VIEW);
        return security;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getClosedAccounts(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        logger.debug("In CustAction::getClosedAccounts()");
        UserContext userContext = getUserContext(request);

        // FIXME - #000022 - refactor away from customer business service
        Integer customerId = getIntegerValue(((CustActionForm) form).getCustomerId());
        CustomerBusinessService customerService = new CustomerBusinessService();
        List<AccountBO> loanAccountsList = customerService.getAllClosedAccount(customerId, AccountTypes.LOAN_ACCOUNT
                .getValue());
        List<AccountBO> savingsAccountList = customerService.getAllClosedAccount(customerId,
                AccountTypes.SAVINGS_ACCOUNT.getValue());
        for (AccountBO savingsBO : savingsAccountList) {
            setLocaleIdForToRetrieveMasterDataName(savingsBO, userContext);
        }
        for (AccountBO loanBO : loanAccountsList) {
            setLocaleIdForToRetrieveMasterDataName(loanBO, userContext);
        }
        SessionUtils.setCollectionAttribute(AccountConstants.CLOSEDLOANACCOUNTSLIST, loanAccountsList, request);
        SessionUtils.setCollectionAttribute(AccountConstants.CLOSEDSAVINGSACCOUNTSLIST, savingsAccountList, request);

        return mapping.findForward(ActionForwards.getAllClosedAccounts.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward getBackToDetailsPage(ActionMapping mapping, ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(getCustomerDetailPage(((CustActionForm) form).getInput()));
    }

    private String getCustomerDetailPage(String input) {
        String forward = null;
        if (input.equals("center")) {
            forward = ActionForwards.center_detail_page.toString();
        } else if (input.equals("group")) {
            forward = ActionForwards.group_detail_page.toString();
        } else if (input.equals("client")) {
            forward = ActionForwards.client_detail_page.toString();
        }
        return forward;
    }

    private void setLocaleIdForToRetrieveMasterDataName(AccountBO accountBO, UserContext userContext) {
        accountBO.getAccountState().setLocaleId(userContext.getLocaleId());
        for (AccountFlagMapping accountFlagMapping : accountBO.getAccountFlags()) {
            accountFlagMapping.getFlag().setLocaleId(userContext.getLocaleId());
        }
    }

    private class DummyBusinessService implements BusinessService {

        @Override
        public BusinessObject getBusinessObject(@SuppressWarnings("unused") final UserContext userContext) {
            return null;
        }
    }
}