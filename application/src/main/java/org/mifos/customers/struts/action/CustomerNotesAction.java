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

package org.mifos.customers.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.LocalDate;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.struts.actionforms.CustomerNotesActionForm;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.dto.screen.CustomerNoteFormDto;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.struts.action.SearchAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerNotesAction extends SearchAction {

    private static final Logger logger = LoggerFactory.getLogger(CustomerNotesAction.class);

    @TransactionDemarcate(joinToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                             @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In CustomerNotesAction::load()");

        CustomerNotesActionForm notesActionForm = (CustomerNotesActionForm) form;
        notesActionForm.setComment("");
        notesActionForm.setCommentDate("");

        UserContext userContext = getUserContext(request);
        Integer customerId = Integer.valueOf(((CustomerNotesActionForm) form).getCustomerId());
        CustomerBO customerBO = this.customerDao.findCustomerById(customerId);
        customerBO.setUserContext(userContext);

        CustomerNoteFormDto noteDto = this.centerServiceFacade.retrieveCustomerNote(customerBO.getGlobalCustNum());

        notesActionForm.setLevelId(noteDto.getCustomerLevel().toString());
        notesActionForm.setGlobalCustNum(noteDto.getGlobalNum());
        notesActionForm.setCustomerName(noteDto.getDisplayName());
        notesActionForm.setCommentDate(DateUtils.getCurrentDate(userContext.getPreferredLocale()));

        if (customerBO.isCenter()) {
            notesActionForm.setInput("center");
        } else if (customerBO.isGroup()) {
            notesActionForm.setInput("group");
        } else if (customerBO.isClient()) {
            notesActionForm.setInput("client");
        }

        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, customerBO, request);
        SessionUtils.setAttribute(CustomerConstants.PERSONNEL_NAME, noteDto.getCommentUser(), request);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In CustomerNotesAction::preview()");
        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In CustomerNotesAction::previous()");
        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, @SuppressWarnings("unused") HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In CustomerNotesAction::cancel()");
        return mapping.findForward(getDetailCustomerPage(form));
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In CustomerNotesAction::create()");
        ActionForward forward = null;
        CustomerNotesActionForm notesActionForm = (CustomerNotesActionForm) form;
        Integer customerId = Integer.valueOf(((CustomerNotesActionForm) form).getCustomerId());
        CustomerBO customerBO = this.customerDao.findCustomerById(customerId);
        UserContext uc = getUserContext(request);

        if (customerBO.getPersonnel() != null) {
            checkPermissionForAddingNotes(AccountTypes.CUSTOMER_ACCOUNT, customerBO.getLevel(), uc, customerBO
                    .getOffice().getOfficeId(), customerBO.getPersonnel().getPersonnelId());
        } else {
            checkPermissionForAddingNotes(AccountTypes.CUSTOMER_ACCOUNT, customerBO.getLevel(), uc, customerBO
                    .getOffice().getOfficeId(), uc.getId());
        }

        CustomerNoteFormDto customerNoteForm = new CustomerNoteFormDto(customerBO.getGlobalCustNum(), customerBO.getDisplayName(),
                customerBO.getCustomerLevel().getId().intValue(), new LocalDate(), "", notesActionForm.getComment());

        this.centerServiceFacade.createCustomerNote(customerNoteForm);

        forward = mapping.findForward(getDetailCustomerPage(notesActionForm));
        return forward;
    }

    private String getDetailCustomerPage(ActionForm form) {
        CustomerNotesActionForm notesActionForm = (CustomerNotesActionForm) form;
        String input = notesActionForm.getInput();
        String forward = null;
        if (input.equals("center")) {
            forward = ActionForwards.center_detail_page.toString();
        }
        if (input.equals("group")) {
            forward = ActionForwards.group_detail_page.toString();
        }
        if (input.equals("client")) {
            forward = ActionForwards.client_detail_page.toString();
        }
        return forward;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute(SavingsConstants.METHODCALLED);
        String forward = null;
        if (method != null) {
            if (method.equals(Methods.preview.toString())) {
                forward = ActionForwards.preview_failure.toString();
            } else if (method.equals(Methods.create.toString())) {
                forward = ActionForwards.create_failure.toString();
            }
        }
        return mapping.findForward(forward);
    }

    @Override
    protected QueryResult getSearchResult(ActionForm form) throws ApplicationException {
        return new CustomerPersistence().getAllCustomerNotes(Integer.valueOf(((CustomerNotesActionForm) form).getCustomerId()));
    }
}