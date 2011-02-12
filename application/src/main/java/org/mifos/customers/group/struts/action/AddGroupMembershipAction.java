/*
 * Copyright Grameen Foundation USA
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

package org.mifos.customers.group.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.struts.actionforms.AddGroupMembershipForm;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class AddGroupMembershipAction extends BaseAction {

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadSearch(ActionMapping mapping, ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        AddGroupMembershipForm actionForm = (AddGroupMembershipForm) form;
        actionForm.setSearchString(null);
        return mapping.findForward(ActionForwards.loadSearch_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewParentAddClient(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.confirmAddClientToGroup_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward updateParent(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        AddGroupMembershipForm actionForm = (AddGroupMembershipForm) form;

        Integer parentGroupId = actionForm.getParentGroupIdValue();
        ClientBO clientInSession = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

        String globalCustNum = this.clientServiceFacade.transferClientToGroup(parentGroupId, clientInSession.getGlobalCustNum(), clientInSession.getVersionNo());

        ClientBO client = this.customerDao.findClientBySystemId(globalCustNum);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);

        return mapping.findForward(ActionForwards.view_client_details_page.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancel_success.toString());
    }

    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        if (method.equalsIgnoreCase(Methods.loadSearch.toString())) {
            return mapping.findForward(ActionForwards.loadSearch_success.toString());
        }
        if (method.equalsIgnoreCase(Methods.updateParent.toString())) {
            return mapping.findForward(ActionForwards.confirmAddClientToGroup_success.toString());
        }
        return null;
    }
}