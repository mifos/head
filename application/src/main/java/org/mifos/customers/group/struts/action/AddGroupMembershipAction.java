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

package org.mifos.customers.group.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.business.service.CustomerBusinessService;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.service.ClientBusinessService;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.business.service.GroupBusinessService;
import org.mifos.customers.group.struts.actionforms.AddGroupMembershipForm;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;

public class AddGroupMembershipAction extends BaseAction {

    private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER);

    @Override
    protected BusinessService getService() throws ServiceException {
        return getGroupBusinessService();
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("addGroupMembershipAction");
        security.allow("loadSearch", SecurityConstants.CAN_ADD_CLIENTS_TO_GROUPS);
        security.allow("previewParentAddClient", SecurityConstants.CAN_ADD_CLIENTS_TO_GROUPS);
        security.allow("updateParent", SecurityConstants.CAN_ADD_CLIENTS_TO_GROUPS);
        // security.allow("cancel",
        // SecurityConstants.CAN_ADD_CLIENTS_TO_GROUPS);

        return security;
    }

    private GroupBusinessService getGroupBusinessService() {
        return (GroupBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Group);
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        AddGroupMembershipForm actionForm = (AddGroupMembershipForm) form;
        actionForm.setSearchString(null);
        logger.debug("In AddGroupMembershipAction ::loadSearch  method ");
        return mapping.findForward(ActionForwards.loadSearch_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewParentAddClient(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.confirmAddClientToGroup_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward updateParent(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        AddGroupMembershipForm actionForm = (AddGroupMembershipForm) form;
        GroupBO addToGroup = (GroupBO) getCustomerBusinessService().getCustomer(actionForm.getParentGroupIdValue());
        if (!addToGroup.isActive()) {
            throw new CustomerException(CustomerConstants.CLIENT_CANT_BE_ADDED_TO_INACTIVE_GROUP);
        }
        addToGroup.setUserContext(getUserContext(request));
        ClientBO clientInSession = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        ClientBO client = getClientBusinessService().getClient(clientInSession.getCustomerId());
        checkVersionMismatch(clientInSession.getVersionNo(), client.getVersionNo());

        client.validateBeforeAddingClientToGroup();

        client.setVersionNo(clientInSession.getVersionNo());
        client.setUserContext(getUserContext(request));
        setInitialObjectForAuditLogging(client);

        client.addClientToGroup(addToGroup);

        clientInSession = null;
        addToGroup = null;
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);

        return mapping.findForward(ActionForwards.view_client_details_page.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancel_success.toString());
    }

    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse httpservletresponse) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        if (method.equalsIgnoreCase(Methods.loadSearch.toString())) {
            return mapping.findForward(ActionForwards.loadSearch_success.toString());
        }
        if (method.equalsIgnoreCase(Methods.updateParent.toString())) {
            return mapping.findForward(ActionForwards.confirmAddClientToGroup_success.toString());
        }
        return null;
    }

    private CustomerBusinessService getCustomerBusinessService() throws ServiceException {
        return (CustomerBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Customer);
    }

    private ClientBusinessService getClientBusinessService() throws ServiceException {
        return (ClientBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Client);
    }

}
