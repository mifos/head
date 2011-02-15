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

package org.mifos.customers.personnel.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.customers.personnel.struts.actionforms.PersonnelNoteActionForm;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.struts.action.SearchAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class PersonnelNoteAction extends SearchAction {

    @TransactionDemarcate(joinToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        clearActionForm(form);
        PersonnelNoteActionForm actionForm = (PersonnelNoteActionForm) form;

        Short personnelId = getShortValue(actionForm.getPersonnelId());
        PersonnelBO personnelBO = this.personnelDao.findPersonnelById(personnelId);

        actionForm.setPersonnelName(personnelBO.getDisplayName());
        actionForm.setOfficeName(personnelBO.getOffice().getOfficeName());
        actionForm.setGlobalPersonnelNum(personnelBO.getGlobalPersonnelNum());
        actionForm.setCommentDate(DateUtils.getCurrentDate(getUserContext(request).getPreferredLocale()));

        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, personnelBO, request);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancel_success.toString());
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        PersonnelNoteActionForm actionForm = (PersonnelNoteActionForm) form;
        Short personnelId = getShortValue(actionForm.getPersonnelId());
        this.centerServiceFacade.addNoteToPersonnel(personnelId, actionForm.getComment());

        return mapping.findForward(ActionForwards.create_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
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

    private void clearActionForm(ActionForm form) {
        ((PersonnelNoteActionForm) form).setComment("");
        ((PersonnelNoteActionForm) form).setCommentDate("");
    }

    @Override
    protected QueryResult getSearchResult(ActionForm form) throws ApplicationException {
        return new PersonnelBusinessService().getAllPersonnelNotes(
                getShortValue(((PersonnelNoteActionForm) form).getPersonnelId()));
    }
}