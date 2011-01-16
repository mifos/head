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

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.struts.action.AccountAppAction;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.struts.actionforms.CustomerAccountActionForm;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.dto.screen.CustomerRecentActivityDto;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class CustomerAccountAction extends AccountAppAction {

    public CustomerAccountAction() throws Exception {
        super();
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String globalCustNum = ((CustomerAccountActionForm) form).getGlobalCustNum();
        if (StringUtils.isBlank(globalCustNum)) {
            // NOTE: see CustomerAction.getAllActivity for explanation of this craziness
            globalCustNum = (String) SessionUtils.getAttribute("customerGlobalNum", request.getSession());
        }

        CustomerBO customerBO = this.customerDao.findCustomerBySystemId(globalCustNum);
        CustomerAccountBO customerAccount = customerBO.getCustomerAccount();
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, customerAccount, request);

        List<CustomerRecentActivityDto> recentActivities = this.centerServiceFacade.retrieveRecentActivities(customerBO.getCustomerId(), 3);
        SessionUtils.setCollectionAttribute(CustomerConstants.RECENT_ACTIVITIES, recentActivities, request);

        ActionForwards forward = getForward(customerBO);
        return mapping.findForward(forward.toString());
    }

    private ActionForwards getForward(CustomerBO customerBO) {
        if (customerBO.getCustomerLevel().getId().equals(CustomerLevel.CLIENT.getValue())) {
            return ActionForwards.client_detail_page;
        }
        if (customerBO.getCustomerLevel().getId().equals(CustomerLevel.GROUP.getValue())) {
            return ActionForwards.group_detail_page;
        }
        if (customerBO.getCustomerLevel().getId().equals(CustomerLevel.CENTER.getValue())) {
            return ActionForwards.center_detail_page;
        }
        return null;
    }
}