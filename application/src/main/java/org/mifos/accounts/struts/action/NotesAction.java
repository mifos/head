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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.accounts.struts.actionforms.NotesActionForm;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.dto.domain.CreateAccountNote;
import org.mifos.dto.domain.NoteSearchDto;
import org.mifos.dto.domain.SavingsAccountDetailDto;
import org.mifos.dto.screen.LoanAccountDetailDto;
import org.mifos.dto.screen.NotesSearchResultsDto;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.struts.action.SearchAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.UserContext;

public class NotesAction extends SearchAction {

    @TransactionDemarcate(joinToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        clearActionForm(form);
        NotesActionForm notesActionForm = (NotesActionForm) form;
        Integer accountId = Integer.valueOf(notesActionForm.getAccountId());
        AccountBO account = new AccountBusinessService().getAccount(accountId);

        if (account.isLoanAccount() || account.isGroupLoanAccount()) {
            LoanAccountDetailDto loanAccountDto = this.loanAccountServiceFacade.retrieveLoanAccountNotes(accountId.longValue());
            if (account.isLoanAccount()) {
                notesActionForm.setAccountTypeId(AccountTypes.LOAN_ACCOUNT.getValue().toString());
            } else {
                notesActionForm.setAccountTypeId(AccountTypes.GROUP_LOAN_ACCOUNT.getValue().toString());
            }
            notesActionForm.setGlobalAccountNum(loanAccountDto.getGlobalAccountNum());
            notesActionForm.setPrdOfferingName(loanAccountDto.getProductDetails().getPrdOfferingName());
        } else if (account.isSavingsAccount()) {
            SavingsAccountDetailDto savingsAccountDto = this.savingsServiceFacade.retrieveSavingsAccountNotes(accountId.longValue());
            notesActionForm.setPrdOfferingName(savingsAccountDto.getProductDetails().getProductDetails().getName());
            notesActionForm.setAccountTypeId(AccountTypes.SAVINGS_ACCOUNT.getValue().toString());
            notesActionForm.setGlobalAccountNum(savingsAccountDto.getGlobalAccountNum());
        }
        
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        NotesActionForm notesActionForm = (NotesActionForm) form;
        UserContext uc = getUserContext(request);

        LocalDate commentDate = new LocalDate();
        String comment = notesActionForm.getComment();
        Integer createdById = uc.getId().intValue();
        Integer accountId = Integer.valueOf(notesActionForm.getAccountId());
        CreateAccountNote accountNote = new CreateAccountNote(commentDate, comment, createdById, accountId);
        SessionUtils.setAttribute("model.accountNote", accountNote, request);
        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(chooseForward(Short.valueOf(((NotesActionForm) form).getAccountTypeId())));
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        NotesActionForm notesActionForm = (NotesActionForm) form;
        UserContext uc = getUserContext(request);

        CreateAccountNote accountNote = (CreateAccountNote) SessionUtils.getAttribute("model.accountNote", request);

        AccountBO accountBO = new AccountBusinessService().getAccount(accountNote.getAccountId());
        if (accountBO.getPersonnel() != null) {
            checkPermissionForAddingNotes(accountBO.getType(), null, uc, accountBO.getOffice().getOfficeId(), accountBO.getPersonnel().getPersonnelId());
        } else {
            checkPermissionForAddingNotes(accountBO.getType(), null, uc, accountBO.getOffice().getOfficeId(), uc.getId());
        }

        if (accountBO.isSavingsAccount()) {
            this.savingsServiceFacade.addNote(accountNote);
        } else {
            this.loanAccountServiceFacade.addNote(accountNote);
        }

        return mapping.findForward(chooseForward(Short.valueOf(notesActionForm.getAccountTypeId())));
    }

    private String chooseForward(Short accountTypeId) {
        String forward = null;
        if (accountTypeId.equals(AccountTypes.LOAN_ACCOUNT.getValue())) {
            forward = ActionForwards.loan_detail_page.toString();
        } else if (accountTypeId.equals(AccountTypes.SAVINGS_ACCOUNT.getValue())) {
            forward = ActionForwards.savings_details_page.toString();
        } else if (accountTypeId.equals(AccountTypes.GROUP_LOAN_ACCOUNT.getValue())) {
            forward = ActionForwards.group_loan_detail_page.toString();
        }
        
        return forward;
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
        ((NotesActionForm) form).setComment("");
    }

    @Override
    protected QueryResult getSearchResult(ActionForm form) throws Exception {

        Integer accountId = Integer.valueOf(((NotesActionForm) form).getAccountId());
        AccountBO account = new AccountBusinessService().getAccount(accountId);
        if (account.isSavingsAccount()) {
            NoteSearchDto noteSearchDto = new NoteSearchDto(accountId, Integer.valueOf(1), Integer.valueOf(10));
            NotesSearchResultsDto noteResults = this.savingsServiceFacade.retrievePagedNotesDto(noteSearchDto);
        }

        return legacyAccountDao.getAllAccountNotes(accountId);
    }
}