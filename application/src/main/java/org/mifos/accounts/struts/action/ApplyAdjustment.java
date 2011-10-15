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
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.struts.actionforms.ApplyAdjustmentActionForm;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.UserContext;

/**
 * This is the action class for applying adjustment. This action is to be merged
 * with AccountAction once an AccountAction for M2 is done.
 */
public class ApplyAdjustment extends BaseAction {

    @Override
    protected BusinessService getService() throws ServiceException {
        return new AccountBusinessService();
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadAdjustment(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ApplyAdjustmentActionForm appAdjustActionForm = (ApplyAdjustmentActionForm) form;
        AccountBO accnt = getBizService().findBySystemId(appAdjustActionForm.getGlobalAccountNum());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accnt, request);
        request.setAttribute("method", "loadAdjustment");
        return mapping.findForward("loadadjustment_success");

    }

    /*
     * This method do the same thing as loadAdjustment, but added to allow
     * handling permission : can adjust payment when account is closed
     * obligation met
     */

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadAdjustmentWhenObligationMet(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ApplyAdjustmentActionForm appAdjustActionForm = (ApplyAdjustmentActionForm) form;
        AccountBO accnt = getBizService().findBySystemId(appAdjustActionForm.getGlobalAccountNum());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accnt, request);
        request.setAttribute("method", "loadAdjustmentWhenObligationMet");
        return mapping.findForward("loadadjustment_success");

    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewAdjustment(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        request.setAttribute("method", "previewAdjustment");
        return mapping.findForward("previewadj_success");
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward applyAdjustment(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        request.setAttribute("method", "applyAdjustment");
        ApplyAdjustmentActionForm appAdjustActionForm = (ApplyAdjustmentActionForm) form;
        checkVersionMismatchForAccountBO(request, appAdjustActionForm);
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
        try {
            accountServiceFacade.applyAdjustment(appAdjustActionForm.getGlobalAccountNum(),
                    appAdjustActionForm.getAdjustmentNote(), userContext.getId());
        } catch (MifosRuntimeException e) {
            request.setAttribute("method", "previewAdjustment");
            throw (Exception) e.getCause();
        }
        resetActionFormFields(appAdjustActionForm);
        return mapping.findForward("applyadj_success");
    }

    private void checkVersionMismatchForAccountBO(HttpServletRequest request, ApplyAdjustmentActionForm appAdjustActionForm) throws ApplicationException {
        AccountBO accountBOInSession = (AccountBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        AccountBO accountBO = getBizService().findBySystemId(appAdjustActionForm.getGlobalAccountNum());
        checkVersionMismatch(accountBOInSession.getVersionNo(), accountBO.getVersionNo());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancelAdjustment(ActionMapping mapping, ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ApplyAdjustmentActionForm appAdjustActionForm = (ApplyAdjustmentActionForm) form;
        resetActionFormFields(appAdjustActionForm);
        return mapping.findForward("canceladj_success");
    }

    /**
     * This method resets action form fields after successfully applying payment
     * or on cancel.
     */
    private void resetActionFormFields(ApplyAdjustmentActionForm appAdjustActionForm) {
        appAdjustActionForm.setAdjustmentNote(null);
    }

    @Override
    protected boolean isNewBizRequired(HttpServletRequest request) throws ServiceException {
        if (request.getAttribute(Constants.BUSINESS_KEY) != null) {
            return false;
        }
        return true;
    }

    private AccountBusinessService getBizService() {
        return new AccountBusinessService();
    }
}