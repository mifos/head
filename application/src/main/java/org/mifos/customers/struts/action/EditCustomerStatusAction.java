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
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.servicefacade.CustomerServiceFacade;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.service.CustomerBusinessService;
import org.mifos.customers.checklist.business.CustomerCheckListBO;
import org.mifos.customers.struts.actionforms.EditCustomerStatusActionForm;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.CustomerStatusFlag;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class EditCustomerStatusAction extends BaseAction {

    private final CustomerServiceFacade customerServiceFacade = DependencyInjectedServiceLocator.locateCustomerServiceFacade();

    private CustomerBusinessService customerService;

    private static final MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CUSTOMERNOTELOGGER);

    public EditCustomerStatusAction() {
        customerService = new CustomerBusinessService();
    }

    @Override
    protected BusinessService getService() {
        return customerService;
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("editCustomerStatusAction");
        security.allow("loadStatus", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.VIEW);
        security.allow("previewStatus", SecurityConstants.VIEW);
        security.allow("previousStatus", SecurityConstants.VIEW);
        security.allow("updateStatus", SecurityConstants.VIEW);
        security.allow("cancelStatus", SecurityConstants.VIEW);
        return security;
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(@SuppressWarnings("unused") String method) {
        return true;

    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadStatus(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In EditCustomerStatusAction:load()");
        doCleanUp(form, request);
        CustomerBO customerBO = customerService.getCustomer(((EditCustomerStatusActionForm) form).getCustomerIdValue());
        loadInitialData(form, customerBO, getUserContext(request));
        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, customerBO, request);

        SessionUtils.setCollectionAttribute(SavingsConstants.STATUS_LIST, customerService.getStatusList(customerBO
                .getCustomerStatus(), customerBO.getLevel(), getUserContext(request).getLocaleId()), request);
        return mapping.findForward(ActionForwards.loadStatus_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewStatus(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In EditCustomerStatusAction:preview()");
        CustomerBO customerBO = (CustomerBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        setCustomerStatusDetails(form, customerBO, request, getUserContext(request));
        return mapping.findForward(ActionForwards.previewStatus_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previousStatus(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In EditCustomerStatusAction:previous()");
        return mapping.findForward(ActionForwards.previousStatus_success.toString());
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward updateStatus(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        logger.debug("In EditCustomerStatusAction:update()");
        EditCustomerStatusActionForm editStatusActionForm = (EditCustomerStatusActionForm) form;
        CustomerBO customerBOInSession = (CustomerBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        UserContext userContext = getUserContext(request);

        this.customerServiceFacade.updateCustomerStatus(customerBOInSession.getCustomerId(), customerBOInSession.getVersionNo(), editStatusActionForm.getFlagId(), editStatusActionForm.getNewStatusId(), editStatusActionForm.getNotes(), userContext);

        return mapping.findForward(getDetailAccountPage(form));
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancelStatus(ActionMapping mapping, ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In EditCustomerStatusAction:cancel()");
        return mapping.findForward(getDetailAccountPage(form));
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) {
        logger.debug("In EditCustomerStatusAction:validate()");
        String method = (String) request.getAttribute(SavingsConstants.METHODCALLED);
        String forward = null;
        if (method != null) {
            if (method.equals(Methods.preview.toString())) {
                forward = ActionForwards.preview_failure.toString();
            } else if (method.equals(Methods.load.toString())) {
                forward = getDetailAccountPage(form);
            } else if (method.equals(Methods.update.toString())) {
                forward = ActionForwards.update_failure.toString();
            } else if (method.equals(Methods.previewStatus.toString())) {
                forward = ActionForwards.previewStatus_failure.toString();
            } else if (method.equals(Methods.loadStatus.toString())) {
                forward = getDetailAccountPage(form);
            } else if (method.equals(Methods.updateStatus.toString())) {
                forward = ActionForwards.updateStatus_failure.toString();
            }
        }
        return mapping.findForward(forward);
    }

    private String getStatusName(CustomerBO customerBO, Short localeId, String statusId, Short statusIdValue) {
        if (StringUtils.isNotBlank(statusId)) {
            return customerService
                    .getStatusName(localeId, CustomerStatus.fromInt(statusIdValue), customerBO.getLevel());
        }
        return null;
    }

    private String getFlagName(CustomerBO customerBO, Short localeId, String statusId, Short statusIdValue,
            Short flagIdValue) {
        if (StringUtils.isNotBlank(statusId) && isNewStatusCancelledOrClosed(statusIdValue)) {
            return customerService.getFlagName(localeId, CustomerStatusFlag.getStatusFlag(flagIdValue), customerBO
                    .getLevel());
        }
        return null;
    }

    private void setCustomerStatusDetails(ActionForm form, CustomerBO customerBO, HttpServletRequest request,
            UserContext userContext) throws Exception {
        EditCustomerStatusActionForm statusActionForm = (EditCustomerStatusActionForm) form;
        statusActionForm.setCommentDate(DateUtils.getCurrentDate(userContext.getPreferredLocale()));
        String newStatusName = null;
        String flagName = null;
        List<CustomerCheckListBO> checklist = customerService.getStatusChecklist(
                statusActionForm.getNewStatusIdValue(), statusActionForm.getLevelIdValue());
        SessionUtils.setCollectionAttribute(SavingsConstants.STATUS_CHECK_LIST, checklist, request);
        newStatusName = getStatusName(customerBO, userContext.getLocaleId(), statusActionForm.getNewStatusId(),
                statusActionForm.getNewStatusIdValue());
        flagName = getFlagName(customerBO, userContext.getLocaleId(), statusActionForm.getNewStatusId(),
                statusActionForm.getNewStatusIdValue(), statusActionForm.getFlagIdValue());
        SessionUtils.setAttribute(SavingsConstants.NEW_STATUS_NAME, newStatusName, request);
        SessionUtils.setAttribute(SavingsConstants.FLAG_NAME, flagName, request);

    }

    private boolean isNewStatusCancelledOrClosed(Short newStatusId) {
        return newStatusId.equals(CustomerStatus.CLIENT_CANCELLED.getValue())
                || newStatusId.equals(CustomerStatus.CLIENT_CLOSED.getValue())
                || newStatusId.equals(CustomerStatus.GROUP_CANCELLED.getValue())
                || newStatusId.equals(CustomerStatus.GROUP_CLOSED.getValue());
    }

    private void setFormAttributes(ActionForm form, CustomerBO customerBO) {
        EditCustomerStatusActionForm editCustomerStatusActionForm = (EditCustomerStatusActionForm) form;
        editCustomerStatusActionForm.setLevelId(customerBO.getCustomerLevel().getId().toString());
        editCustomerStatusActionForm.setCurrentStatusId(customerBO.getCustomerStatus().getId().toString());
        editCustomerStatusActionForm.setGlobalAccountNum(customerBO.getGlobalCustNum());
        editCustomerStatusActionForm.setCustomerName(customerBO.getDisplayName());
        if (customerBO.getCustomerLevel().isCenter()) {
            editCustomerStatusActionForm.setInput("center");
        } else if (customerBO.getCustomerLevel().isGroup()) {
            editCustomerStatusActionForm.setInput("group");
        } else if (customerBO.getCustomerLevel().isClient()) {
            editCustomerStatusActionForm.setInput("client");
        }

    }

    private void doCleanUp(ActionForm form, @SuppressWarnings("unused") HttpServletRequest request) {
        EditCustomerStatusActionForm editCustomerStatusActionForm = (EditCustomerStatusActionForm) form;
        editCustomerStatusActionForm.setSelectedItems(null);
        editCustomerStatusActionForm.setNotes(null);
        editCustomerStatusActionForm.setNewStatusId(null);
        editCustomerStatusActionForm.setFlagId(null);
    }

    private String getDetailAccountPage(ActionForm form) {
        EditCustomerStatusActionForm editStatusActionForm = (EditCustomerStatusActionForm) form;
        String input = editStatusActionForm.getInput();
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

    private void loadInitialData(ActionForm form, CustomerBO customerBO, UserContext userContext) throws Exception {
        customerBO.setUserContext(userContext);
        customerService.initializeStateMachine(userContext.getLocaleId(), customerBO.getOffice().getOfficeId(),
                AccountTypes.CUSTOMER_ACCOUNT, customerBO.getLevel());
        setFormAttributes(form, customerBO);
        customerBO.getCustomerStatus().setLocaleId(userContext.getLocaleId());
    }
}
