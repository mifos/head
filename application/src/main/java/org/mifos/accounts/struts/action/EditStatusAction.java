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

package org.mifos.accounts.struts.action;

import static org.mifos.accounts.loan.util.helpers.LoanConstants.METHODCALLED;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.accounts.struts.actionforms.EditStatusActionForm;
import org.mifos.application.questionnaire.struts.DefaultQuestionnaireServiceFacadeLocator;
import org.mifos.application.questionnaire.struts.QuestionnaireFlowAdapter;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.checklist.business.AccountCheckListBO;
import org.mifos.dto.domain.SavingsAccountStatusDto;
import org.mifos.dto.domain.SavingsAccountUpdateStatus;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class EditStatusAction extends BaseAction {

    private QuestionnaireFlowAdapter approveLoanQuestionnaire;

    @Override
    protected BusinessService getService() {
        return getAccountBusinessService();
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("editStatusAction");
        security.allow("load", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.VIEW);
        security.allow("captureQuestionResponses", SecurityConstants.VIEW);
        security.allow("editQuestionResponses", SecurityConstants.VIEW);
        return security;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        doCleanUp(request.getSession(), form);
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession());
        EditStatusActionForm actionForm = (EditStatusActionForm) form;
        Integer accountId = Integer.valueOf(actionForm.getAccountId());
        AccountBO accountBO = getAccountBusinessService().getAccount(accountId);

        if (accountBO.isLoanAccount()) {
            getAccountBusinessService().initializeStateMachine(accountBO.getType(), null);
            accountBO.setUserContext(userContext);
            accountBO.getAccountState().setLocaleId(userContext.getLocaleId());

            setFormAttributes(form, accountBO);
        } if (accountBO.isSavingsAccount()) {

            // NOTE - not using dto values at present but available when ui is refactored away from jsp
            SavingsAccountStatusDto accountStatuses = this.savingsServiceFacade.retrieveAccountStatuses(accountId.longValue(), userContext.getLocaleId());

            SavingsBO savingsAccount = this.savingsDao.findById(accountId.longValue());

            setFormAttributes(form, savingsAccount);
        }
        List<AccountStateEntity> accountStatuses = getAccountBusinessService().getStatusList(accountBO.getAccountState(), accountBO.getType(), userContext.getLocaleId());

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO, request);
        SessionUtils.setCollectionAttribute(SavingsConstants.STATUS_LIST, accountStatuses, request);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        AccountBO accountBO = (AccountBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        EditStatusActionForm editStatusActionForm = (EditStatusActionForm) form;

        // FIXME - KEITHW - is checklist functionality being removed from application?
        List<AccountCheckListBO> checklist = getAccountBusinessService().getStatusChecklist(
                getShortValue(editStatusActionForm.getNewStatusId()),
                getShortValue(editStatusActionForm.getAccountTypeId()));
        SessionUtils.setCollectionAttribute(SavingsConstants.STATUS_CHECK_LIST, checklist, request);

        initializeLoanQuestionnaire(accountBO.getGlobalAccountNum());
        if (loanApproved(request)) {
            return approveLoanQuestionnaire.fetchAppliedQuestions(mapping,(EditStatusActionForm) form, request, ActionForwards.preview_success);
        }
        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    private void initializeLoanQuestionnaire(String globalAccountNum) {
        approveLoanQuestionnaire = new QuestionnaireFlowAdapter("Approve", "Loan", ActionForwards.preview_success,
                "loanAccountAction.do?method=get&globalAccountNum=" + globalAccountNum, new DefaultQuestionnaireServiceFacadeLocator());
    }

    private boolean loanApproved(HttpServletRequest request) throws PageExpiredException {
        return StringUtils.equalsIgnoreCase("Application Approved", (String) SessionUtils.getAttribute(SavingsConstants.NEW_STATUS_NAME, request));
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
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession());
        AccountBO accountBOInSession = (AccountBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

        Integer accountId = Integer.valueOf(editStatusActionForm.getAccountId());
        AccountBO accountBO = getAccountBusinessService().getAccount(accountId);

        Short flagId = null;
        Short newStatusId = null;
        String updateComment = editStatusActionForm.getNotes();
        if (StringUtils.isNotBlank(editStatusActionForm.getFlagId())) {
            flagId = getShortValue(editStatusActionForm.getFlagId());
        }
        if (StringUtils.isNotBlank(editStatusActionForm.getNewStatusId())) {
            newStatusId = getShortValue(editStatusActionForm.getNewStatusId());
        }

        if (accountBO.isLoanAccount()) {
            checkVersionMismatch(accountBOInSession.getVersionNo(), accountBO.getVersionNo());
            accountBO.setUserContext(userContext);
            accountBO.getAccountState().setLocaleId(userContext.getLocaleId());
            setInitialObjectForAuditLogging(accountBO);
            checkPermission(accountBO, getUserContext(request), newStatusId, flagId);
            initializeLoanQuestionnaire(accountBO.getGlobalAccountNum());
            approveLoanQuestionnaire.saveResponses(request, editStatusActionForm, accountId);
            accountBO.changeStatus(newStatusId, flagId, updateComment);
            accountBOInSession = null;
            accountBO.update();
            accountBO = null;
            return mapping.findForward(ActionForwards.loan_detail_page.toString());
        } if (accountBO.isSavingsAccount()) {
            SavingsAccountUpdateStatus updateStatus = new SavingsAccountUpdateStatus(accountId.longValue(), newStatusId, flagId, updateComment);
            this.savingsServiceFacade.updateSavingsAccountStatus(updateStatus, userContext.getLocaleId());
            return mapping.findForward(ActionForwards.savings_details_page.toString());
        }

        // nothing but loan of savings account should be detected. customer account status change goes through seperate action.
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
        ActionErrors errors = approveLoanQuestionnaire.validateResponses(request, (EditStatusActionForm) form);
        if (errors != null && !errors.isEmpty()) {
            addErrors(request, errors);
            return mapping.findForward(ActionForwards.captureQuestionResponses.toString());
        }
        return approveLoanQuestionnaire.rejoinFlow(mapping);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editQuestionResponses(
            final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        request.setAttribute(METHODCALLED, "editQuestionResponses");
        return approveLoanQuestionnaire.editResponses(mapping, request, (EditStatusActionForm) form);
    }

    private AccountBusinessService getAccountBusinessService() {
        return new AccountBusinessService();
    }

    private void doCleanUp(HttpSession session, ActionForm form) {
        EditStatusActionForm editStatusActionForm = (EditStatusActionForm) form;
        editStatusActionForm.setSelectedItems(null);
        editStatusActionForm.setNotes(null);
        editStatusActionForm.setNewStatusId(null);
        editStatusActionForm.setFlagId(null);
        editStatusActionForm.setQuestionGroups(null);
        session.removeAttribute(Constants.BUSINESS_KEY);
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

    private void setFormAttributes(ActionForm form, AccountBO accountBO) throws Exception {
        EditStatusActionForm editStatusActionForm = (EditStatusActionForm) form;
        editStatusActionForm.setAccountTypeId(accountBO.getType().getValue().toString());
        editStatusActionForm.setCurrentStatusId(accountBO.getAccountState().getId().toString());
        editStatusActionForm.setGlobalAccountNum(accountBO.getGlobalAccountNum());
        if (accountBO instanceof LoanBO) {
            editStatusActionForm.setAccountName(((LoanBO) accountBO).getLoanOffering().getPrdOfferingName());
            editStatusActionForm.setInput("loan");
        } else if (accountBO instanceof SavingsBO) {
            editStatusActionForm.setAccountName(((SavingsBO) accountBO).getSavingsOffering().getPrdOfferingName());
            editStatusActionForm.setInput("savings");
        }
    }

    private void checkPermission(AccountBO accountBO, UserContext userContext, Short newStatusId, Short flagId)
            throws Exception {
        if (null != accountBO.getPersonnel()) {
            getAccountBusinessService().checkPermissionForStatusChange(newStatusId, userContext, flagId,
                    accountBO.getOffice().getOfficeId(), accountBO.getPersonnel().getPersonnelId());
        } else {
            getAccountBusinessService().checkPermissionForStatusChange(newStatusId, userContext, flagId,
                    accountBO.getOffice().getOfficeId(), userContext.getId());
        }
    }
}
