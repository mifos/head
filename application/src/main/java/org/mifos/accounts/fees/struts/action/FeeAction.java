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

package org.mifos.accounts.fees.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.fees.servicefacade.FeeCreateRequest;
import org.mifos.accounts.fees.servicefacade.FeeDto;
import org.mifos.accounts.fees.servicefacade.FeeUpdateRequest;
import org.mifos.accounts.fees.struts.actionforms.FeeActionForm;
import org.mifos.accounts.fees.util.helpers.FeeConstants;
import org.mifos.application.servicefacade.FeeDetailsForLoadDto;
import org.mifos.application.servicefacade.FeeDetailsForManageDto;
import org.mifos.application.servicefacade.FeeDetailsForPreviewDto;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class FeeAction extends BaseAction {

    public FeeAction() throws Exception {
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("feeaction");
        security.allow("search", SecurityConstants.VIEW);
        security.allow("load", SecurityConstants.FEES_CREATE_FEES);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("editPreview", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.FEES_CREATE_FEES);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("manage", SecurityConstants.FEES_EDIT_FEES);
        security.allow("update", SecurityConstants.FEES_EDIT_FEES);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("editPrevious", SecurityConstants.VIEW);
        security.allow("viewAll", SecurityConstants.VIEW);
        security.allow("cancelCreate", SecurityConstants.VIEW);
        security.allow("cancelEdit", SecurityConstants.VIEW);
        return security;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        form.reset(mapping, request);

        FeeDetailsForLoadDto feeDetailsForLoad = this.feeServiceFacade.retrieveDetailsForFeeLoad(getUserContext(request).getLocaleId());

        request.getSession().setAttribute("isMultiCurrencyEnabled", feeDetailsForLoad.isMultiCurrencyEnabled());
        request.getSession().setAttribute("currencies", feeDetailsForLoad.getCurrencies());
        request.getSession().setAttribute(FeeParameters.class.getSimpleName(), feeDetailsForLoad.getFeeParameters());
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        FeeActionForm feeActionForm = (FeeActionForm) form;
        Short currencyId = feeActionForm.getCurrencyId();

        FeeDetailsForPreviewDto feeDetailsForPreview = this.feeServiceFacade.retrieveDetailsforFeePreview(currencyId);

        SessionUtils.setAttribute("isMultiCurrencyEnabled", feeDetailsForPreview.isMultiCurrencyEnabled(), request);
        if (feeDetailsForPreview.isMultiCurrencyEnabled()) {
            request.getSession().setAttribute("currencyCode", feeDetailsForPreview.getCurrencyCode());
        }

        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        FeeActionForm actionForm = (FeeActionForm) form;
        UserContext userContext = getUserContext(request);
        FeeCreateRequest feeCreateRequest = new FeeCreateRequest(actionForm.getCategoryTypeValue(), actionForm
                .getFeeFrequencyTypeValue(), actionForm.getGlCodeValue(), actionForm.getFeePaymentTypeValue(),
                actionForm.getFeeFormulaValue(), actionForm.getFeeName(), actionForm.isRateFee(), actionForm
                        .isCustomerDefaultFee(), actionForm.getRateValue(), actionForm.getCurrencyId(), actionForm
                        .getAmount(), actionForm.getFeeRecurrenceTypeValue(), actionForm.getMonthRecurAfterValue(),
                actionForm.getWeekRecurAfterValue());

        FeeDto feeDto = this.feeServiceFacade.createFee(feeCreateRequest, userContext);

        actionForm.setFeeId(feeDto.getId().toString());
        return mapping.findForward(ActionForwards.create_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String forward = null;
        String method = (String) request.getAttribute("methodCalled");
        if (method != null) {
            if (method.equals(Methods.previous.toString()) || method.equals(Methods.create.toString())) {
                forward = ActionForwards.previous_failure.toString();
            } else if (method.equals(Methods.editPrevious.toString()) || method.equals(Methods.update.toString())) {
                forward = ActionForwards.editprevious_failure.toString();
            } else {
                forward = method + "_failure";
            }
        }
        return mapping.findForward(forward.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        FeeDto feeDto = this.feeServiceFacade.getFeeDetails(((FeeActionForm) form).getFeeIdValue());
        request.setAttribute("model", feeDto);
        return mapping.findForward(ActionForwards.get_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward manage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        FeeActionForm feeActionForm = (FeeActionForm) form;

        Short feeId = Short.valueOf(feeActionForm.getFeeId());
        FeeDetailsForManageDto feeDetailsForManage = feeServiceFacade.retrieveDetailsForFeeManage(feeId);

        SessionUtils.setCollectionAttribute(FeeConstants.STATUSLIST, feeDetailsForManage.getFeeStatuses(), request);
        feeActionForm.updateWithFee(feeDetailsForManage.getFee());
        request.setAttribute("model", feeDetailsForManage.getFee());
        return mapping.findForward(ActionForwards.manage_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editPreview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        FeeDto feeDto = this.feeServiceFacade.getFeeDetails(((FeeActionForm) form).getFeeIdValue());
        request.setAttribute("model", feeDto);
        return mapping.findForward(ActionForwards.editPreview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editPrevious(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        FeeDto feeDto = this.feeServiceFacade.getFeeDetails(((FeeActionForm) form).getFeeIdValue());
        request.setAttribute("model", feeDto);
        return mapping.findForward(ActionForwards.editprevious_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        FeeActionForm feeActionForm = (FeeActionForm) form;

        FeeUpdateRequest feeUpdateRequest = new FeeUpdateRequest(Short.valueOf(feeActionForm.getFeeId()), feeActionForm.getCurrencyId(),
                feeActionForm.getAmount(), feeActionForm.getFeeStatusValue(), feeActionForm.getRateValue());

        this.feeServiceFacade.updateFee(feeUpdateRequest, getUserContext(request));
        return mapping.findForward(ActionForwards.update_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward viewAll(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        List<FeeDto> customerFees = feeServiceFacade.getCustomerFees();
        List<FeeDto> productFees = feeServiceFacade.getProductFees();

        SessionUtils.setCollectionAttribute(FeeConstants.CUSTOMER_FEES, customerFees, request);
        SessionUtils.setCollectionAttribute(FeeConstants.PRODUCT_FEES, productFees, request);
        return mapping.findForward(ActionForwards.viewAll_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancelCreate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancelCreate_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancelEdit(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancelEdit_success.toString());
    }
}