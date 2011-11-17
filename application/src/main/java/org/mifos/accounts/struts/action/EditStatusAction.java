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

import static org.mifos.accounts.loan.util.helpers.LoanConstants.METHODCALLED;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.business.AccountStateFlagEntity;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.accounts.struts.actionforms.EditStatusActionForm;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.questionnaire.struts.DefaultQuestionnaireServiceFacadeLocator;
import org.mifos.application.questionnaire.struts.QuestionnaireFlowAdapter;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.checklist.business.AccountCheckListBO;
import org.mifos.dto.domain.AccountStatusDto;
import org.mifos.dto.domain.AccountUpdateStatus;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.UserContext;

public class EditStatusAction extends BaseAction {

    private QuestionnaireFlowAdapter loanQuestionnaire;

    @TransactionDemarcate(joinToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        EditStatusActionForm editStatusActionForm1 = (EditStatusActionForm) form;
        editStatusActionForm1.setSelectedItems(null);
        editStatusActionForm1.setNotes(null);
        editStatusActionForm1.setNewStatusId(null);
        editStatusActionForm1.setFlagId(null);
        editStatusActionForm1.setQuestionGroups(null);
        request.getSession().removeAttribute(Constants.BUSINESS_KEY);
        UserContext userContext = getUserContext(request);
        EditStatusActionForm actionForm = (EditStatusActionForm) form;
        Integer accountId = Integer.valueOf(actionForm.getAccountId());
        AccountBO accountBO = new AccountBusinessService().getAccount(accountId);

        if (accountBO.isLoanAccount()) {
            // NOTE - not using dto values at present but available when ui is refactored away from jsp
            AccountStatusDto accountStatuses = this.loanAccountServiceFacade.retrieveAccountStatuses(accountId.longValue());

            LoanBO loanAccount = this.loanDao.findById(accountId);

            EditStatusActionForm editStatusActionForm = (EditStatusActionForm) form;
            editStatusActionForm.setAccountTypeId(AccountTypes.LOAN_ACCOUNT.getValue().toString());
            editStatusActionForm.setCurrentStatusId(loanAccount.getAccountState().getId().toString());
            editStatusActionForm.setGlobalAccountNum(loanAccount.getGlobalAccountNum());
            editStatusActionForm.setAccountName(loanAccount.getLoanOffering().getPrdOfferingName());
            editStatusActionForm.setInput("loan");
        } if (accountBO.isSavingsAccount()) {

            // NOTE - not using dto values at present but available when ui is refactored away from jsp
            AccountStatusDto accountStatuses = this.savingsServiceFacade.retrieveAccountStatuses(accountId.longValue());

            SavingsBO savingsAccount = this.savingsDao.findById(accountId.longValue());

            EditStatusActionForm editStatusActionForm = (EditStatusActionForm) form;
            editStatusActionForm.setAccountTypeId(AccountTypes.SAVINGS_ACCOUNT.getValue().toString());
            editStatusActionForm.setCurrentStatusId(savingsAccount.getAccountState().getId().toString());
            editStatusActionForm.setGlobalAccountNum(savingsAccount.getGlobalAccountNum());
            editStatusActionForm.setAccountName(savingsAccount.getSavingsOffering().getPrdOfferingName());
            editStatusActionForm.setInput("savings");
        }
        List<AccountStateEntity> accountStatuses = new AccountBusinessService().getStatusList(accountBO.getAccountState(), accountBO.getType(), userContext.getLocaleId());

        for (AccountStateEntity customerStatusEntity : accountStatuses) {
            for (AccountStateFlagEntity flag : customerStatusEntity.getFlagSet()) {
                String statusMessageText = ApplicationContextProvider.getBean(MessageLookup.class).lookup(flag.getLookUpValue().getPropertiesKey());
                flag.setStatusFlagMessageText(statusMessageText);
            }
        }

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO, request);
        SessionUtils.setCollectionAttribute(SavingsConstants.STATUS_LIST, accountStatuses, request);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        AccountBO accountBO = (AccountBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        EditStatusActionForm editStatusActionForm = (EditStatusActionForm) form;

        // FIXME - ElsieF - KEITHW - is checklist functionality being removed from application?
        List<AccountCheckListBO> checklist = new AccountBusinessService().getStatusChecklist(
                getShortValue(editStatusActionForm.getNewStatusId()),
                getShortValue(editStatusActionForm.getAccountTypeId()));
        SessionUtils.setCollectionAttribute(SavingsConstants.STATUS_CHECK_LIST, checklist, request);

        String newStatusId = editStatusActionForm.getNewStatusId();

        initializeLoanQuestionnaire(accountBO.getGlobalAccountNum(), newStatusId);
        if (loanApproved(newStatusId) || loanClosed(newStatusId)) {
            return loanQuestionnaire.fetchAppliedQuestions(mapping, editStatusActionForm, request, ActionForwards.preview_success);
        }

        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    private void initializeLoanQuestionnaire(String globalAccountNum, String newStatusId) {
        if (loanClosed(newStatusId)) {
            loanQuestionnaire = new QuestionnaireFlowAdapter("Close", "Loan", ActionForwards.preview_success,
                    "loanAccountAction.do?method=get&globalAccountNum=" + globalAccountNum, new DefaultQuestionnaireServiceFacadeLocator());
        }
        else {
            loanQuestionnaire = new QuestionnaireFlowAdapter("Approve", "Loan", ActionForwards.preview_success,
                    "loanAccountAction.do?method=get&globalAccountNum=" + globalAccountNum, new DefaultQuestionnaireServiceFacadeLocator());
        }
    }

    private boolean loanApproved(String newStatusId) {
        return AccountState.LOAN_APPROVED.getValue().toString().equals(newStatusId);
    }

    private boolean loanClosed(String newStatusId) {
        return AccountState.LOAN_CLOSED_RESCHEDULED.getValue().toString().equals(newStatusId) ||
                AccountState.LOAN_CLOSED_WRITTEN_OFF.getValue().toString().equals(newStatusId);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(getDetailAccountPage(form));
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        EditStatusActionForm editStatusActionForm = (EditStatusActionForm) form;

        Integer accountId = Integer.valueOf(editStatusActionForm.getAccountId());
        AccountBO accountBO = new AccountBusinessService().getAccount(accountId);

        Short flagId = null;
        Short newStatusId = null;
        String updateComment = editStatusActionForm.getNotes();
        if (StringUtils.isNotBlank(editStatusActionForm.getFlagId())) {
            flagId = getShortValue(editStatusActionForm.getFlagId());
        }
        if (StringUtils.isNotBlank(editStatusActionForm.getNewStatusId())) {
            newStatusId = getShortValue(editStatusActionForm.getNewStatusId());
        }

        checkPermission(accountBO, getUserContext(request), newStatusId, flagId);

        if (accountBO.isLoanAccount()) {
            initializeLoanQuestionnaire(accountBO.getGlobalAccountNum(), newStatusId != null ? newStatusId.toString() : null);
            loanQuestionnaire.saveResponses(request, editStatusActionForm, accountId);

            AccountUpdateStatus updateStatus = new AccountUpdateStatus(accountId.longValue(), newStatusId, flagId, updateComment);
            this.loanAccountServiceFacade.updateLoanAccountStatus(updateStatus);

            return mapping.findForward(ActionForwards.loan_detail_page.toString());
        } if (accountBO.isSavingsAccount()) {
            AccountUpdateStatus updateStatus = new AccountUpdateStatus(accountId.longValue(), newStatusId, flagId, updateComment);
            this.savingsServiceFacade.updateSavingsAccountStatus(updateStatus);
            return mapping.findForward(ActionForwards.savings_details_page.toString());
        }

        // nothing but loan of savings account should be detected. customer account status change goes through separate action.
        return null;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute(SavingsConstants.METHODCALLED);
        String forward = null;
        if (method != null) {
            if (method.equals(Methods.preview.toString())) {
                forward = ActionForwards.preview_failure.toString();
            } else if (method.equals(Methods.load.toString())) {
                forward = getDetailAccountPage(form);
            } else if (method.equals(Methods.update.toString())) {
                forward = ActionForwards.update_failure.toString();
            }
        }
        return mapping.findForward(forward);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward captureQuestionResponses(
            final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        request.setAttribute(METHODCALLED, "captureQuestionResponses");
        ActionErrors errors = loanQuestionnaire.validateResponses(request, (EditStatusActionForm) form);
        if (errors != null && !errors.isEmpty()) {
            addErrors(request, errors);
            return mapping.findForward(ActionForwards.captureQuestionResponses.toString());
        }
        return loanQuestionnaire.rejoinFlow(mapping);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editQuestionResponses(
            final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        request.setAttribute(METHODCALLED, "editQuestionResponses");
        return loanQuestionnaire.editResponses(mapping, request, (EditStatusActionForm) form);
    }

    private String getDetailAccountPage(ActionForm form) {
        EditStatusActionForm editStatusActionForm = (EditStatusActionForm) form;
        String input = editStatusActionForm.getInput();
        String forward = null;
        if (input.equals("loan")) {
            forward = ActionForwards.loan_detail_page.toString();
        } else if (input.equals("savings")) {
            forward = ActionForwards.savings_details_page.toString();
        }
        return forward;
    }

    private void checkPermission(AccountBO accountBO, UserContext userContext, Short newStatusId, Short flagId)
            throws Exception {
        if (null != accountBO.getPersonnel()) {
            new AccountBusinessService().checkPermissionForStatusChange(newStatusId, userContext, flagId,
                    accountBO.getOffice().getOfficeId(), accountBO.getPersonnel().getPersonnelId());
        } else {
            new AccountBusinessService().checkPermissionForStatusChange(newStatusId, userContext, flagId,
                    accountBO.getOffice().getOfficeId(), userContext.getId());
        }
    }
}
