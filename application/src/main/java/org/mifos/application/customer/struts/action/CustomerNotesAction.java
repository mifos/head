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

package org.mifos.application.customer.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerNoteEntity;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.struts.actionforms.CustomerNotesActionForm;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.SecurityConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.SearchAction;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class CustomerNotesAction extends SearchAction {

    private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CUSTOMERLOGGER);

    @Override
    protected BusinessService getService() {
        return getCustomerBusinessService();
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("customerNotesAction");
        security.allow("load", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.VIEW);
        security.allow("search", SecurityConstants.VIEW);
        return security;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("In CustomerNotesAction::load()");
        clearActionForm(form);
        UserContext userContext = getUserContext(request);
        CustomerBO customerBO = getCustomerBusinessService().getCustomer(
                Integer.valueOf(((CustomerNotesActionForm) form).getCustomerId()));
        customerBO.setUserContext(userContext);
        setFormAttributes(userContext, form, customerBO);
        PersonnelBO personnelBO = new PersonnelPersistence().getPersonnel(userContext.getId());
        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, customerBO, request);
        SessionUtils.setAttribute(CustomerConstants.PERSONNEL_NAME, personnelBO.getDisplayName(), request);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("In CustomerNotesAction::preview()");
        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("In CustomerNotesAction::previous()");
        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("In CustomerNotesAction::cancel()");
        return mapping.findForward(getDetailCustomerPage(form));
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("In CustomerNotesAction::create()");
        ActionForward forward = null;
        CustomerNotesActionForm notesActionForm = (CustomerNotesActionForm) form;
        CustomerBO customerBO = getCustomerBusinessService().getCustomer(
                Integer.valueOf(((CustomerNotesActionForm) form).getCustomerId()));
        UserContext uc = getUserContext(request);
        if (customerBO.getPersonnel() != null)
            checkPermissionForAddingNotes(AccountTypes.CUSTOMER_ACCOUNT, customerBO.getLevel(), uc, customerBO
                    .getOffice().getOfficeId(), customerBO.getPersonnel().getPersonnelId());
        else
            checkPermissionForAddingNotes(AccountTypes.CUSTOMER_ACCOUNT, customerBO.getLevel(), uc, customerBO
                    .getOffice().getOfficeId(), uc.getId());
        PersonnelBO personnelBO = new PersonnelPersistence().getPersonnel(uc.getId());
        CustomerNoteEntity customerNote = new CustomerNoteEntity(notesActionForm.getComment(), new DateTimeService()
                .getCurrentJavaSqlDate(), personnelBO, customerBO);
        customerBO.addCustomerNotes(customerNote);
        customerBO.setUserContext(uc);
        customerBO.update();
        forward = mapping.findForward(getDetailCustomerPage(notesActionForm));
        customerBO = null;
        return forward;
    }

    private String getDetailCustomerPage(ActionForm form) {
        CustomerNotesActionForm notesActionForm = (CustomerNotesActionForm) form;
        String input = notesActionForm.getInput();
        String forward = null;
        if (input.equals("center"))
            forward = ActionForwards.center_detail_page.toString();
        if (input.equals("group"))
            forward = ActionForwards.group_detail_page.toString();
        if (input.equals("client"))
            forward = ActionForwards.client_detail_page.toString();
        return forward;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute(SavingsConstants.METHODCALLED);
        String forward = null;
        if (method != null) {
            if (method.equals(Methods.preview.toString()))
                forward = ActionForwards.preview_failure.toString();
            else if (method.equals(Methods.create.toString()))
                forward = ActionForwards.create_failure.toString();
        }
        return mapping.findForward(forward);
    }

    private void clearActionForm(ActionForm form) {
        ((CustomerNotesActionForm) form).setComment("");
        ((CustomerNotesActionForm) form).setCommentDate("");
    }

    @Override
    protected QueryResult getSearchResult(ActionForm form) throws ApplicationException {
        return getCustomerBusinessService().getAllCustomerNotes(
                Integer.valueOf(((CustomerNotesActionForm) form).getCustomerId()));
    }

    private CustomerBusinessService getCustomerBusinessService() {
        return (CustomerBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Customer);
    }

    private void setFormAttributes(UserContext userContext, ActionForm form, CustomerBO customerBO)
            throws ApplicationException, InvalidDateException {
        CustomerNotesActionForm notesActionForm = (CustomerNotesActionForm) form;
        notesActionForm.setLevelId(customerBO.getCustomerLevel().getId().toString());
        notesActionForm.setGlobalCustNum(customerBO.getGlobalCustNum());
        notesActionForm.setCustomerName(customerBO.getDisplayName());
        notesActionForm.setCommentDate(DateUtils.getCurrentDate(userContext.getPreferredLocale()));
        if (customerBO instanceof CenterBO)
            notesActionForm.setInput("center");
        else if (customerBO instanceof GroupBO)
            notesActionForm.setInput("group");
        else if (customerBO instanceof ClientBO)
            notesActionForm.setInput("client");
    }
}
