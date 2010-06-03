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
import org.mifos.accounts.fees.business.FeeStatusEntity;
import org.mifos.accounts.fees.business.service.FeeBusinessService;
import org.mifos.accounts.fees.servicefacade.FeeCreateRequest;
import org.mifos.accounts.fees.servicefacade.FeeDto;
import org.mifos.accounts.fees.servicefacade.FeeServiceFacade;
import org.mifos.accounts.fees.servicefacade.FeeUpdateRequest;
import org.mifos.accounts.fees.servicefacade.WebTierFeeServiceFacade;
import org.mifos.accounts.fees.struts.actionforms.FeeActionForm;
import org.mifos.accounts.fees.util.helpers.FeeConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.AccountingRules;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class FeeAction extends BaseAction {

    private static final FeeServiceFacade feeServiceFacade = new WebTierFeeServiceFacade();
    private static final FeeBusinessService feeBusinessService = new FeeBusinessService();

    public FeeAction() throws Exception {
    }

    @Override
    protected BusinessService getService() throws ServiceException {
        return feeBusinessService;
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
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
            HttpServletResponse response) throws Exception {
        form.reset(mapping, request);
        request.getSession().setAttribute("isMultiCurrencyEnabled", AccountingRules.isMultiCurrencyEnabled());
        request.getSession().setAttribute("currencies", AccountingRules.getCurrencies());
        FeeParameters feeParameters = feeServiceFacade.parameters(getUserContext(request).getLocaleId());
        request.getSession().setAttribute(FeeParameters.class.getSimpleName(), feeParameters);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        SessionUtils.setAttribute("isMultiCurrencyEnabled", AccountingRules.isMultiCurrencyEnabled(), request);
        if (AccountingRules.isMultiCurrencyEnabled()) {
            Short currencyId = ((FeeActionForm) form).getCurrencyId();
            String currencyCode = getCurrency(currencyId).getCurrencyCode();
            request.getSession().setAttribute("currencyCode", currencyCode);
        }
        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        FeeActionForm actionForm = (FeeActionForm) form;
        UserContext userCtx = getUserContext(request);
        FeeCreateRequest feeCreateRequest = new FeeCreateRequest(actionForm.getCategoryTypeValue(), actionForm
                .getFeeFrequencyTypeValue(), actionForm.getGlCodeValue(), actionForm.getFeePaymentTypeValue(),
                actionForm.getFeeFormulaValue(), actionForm.getFeeName(), actionForm.isRateFee(), actionForm
                        .isCustomerDefaultFee(), actionForm.getRateValue(), actionForm.getCurrencyId(), actionForm
                        .getAmount(), actionForm.getFeeRecurrenceTypeValue(), actionForm.getMonthRecurAfterValue(),
                actionForm.getWeekRecurAfterValue());

        FeeDto feeDto = feeServiceFacade.createFee(feeCreateRequest, userCtx);
        actionForm.setFeeId(feeDto.getId().toString());
        return mapping.findForward(ActionForwards.create_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
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
            HttpServletResponse response) throws Exception {
        FeeDto feeDto = feeServiceFacade.getFeeDetails(((FeeActionForm) form).getFeeIdValue());
        request.setAttribute("model", feeDto);
        return mapping.findForward(ActionForwards.get_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward manage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        FeeActionForm feeActionForm = (FeeActionForm) form;
        FeeDto fee = feeServiceFacade.getFeeDetails(Short.valueOf(feeActionForm.getFeeId()));
        feeActionForm.updateWithFee(fee);
        loadUpdateMasterData(request);
        request.setAttribute("model", fee);
        return mapping.findForward(ActionForwards.manage_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editPreview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        FeeDto feeDto = feeServiceFacade.getFeeDetails(((FeeActionForm) form).getFeeIdValue());
        request.setAttribute("model", feeDto);
        return mapping.findForward(ActionForwards.editPreview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editPrevious(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        FeeDto feeDto = feeServiceFacade.getFeeDetails(((FeeActionForm) form).getFeeIdValue());
        request.setAttribute("model", feeDto);
        return mapping.findForward(ActionForwards.editprevious_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        FeeActionForm feeActionForm = (FeeActionForm) form;
        feeServiceFacade.updateFee(new FeeUpdateRequest(Short.valueOf(feeActionForm.getFeeId()), feeActionForm.getCurrencyId(),
                feeActionForm.getAmount(), feeActionForm.getFeeStatusValue(), feeActionForm.getRateValue()),
                getUserContext(request));
        return mapping.findForward(ActionForwards.update_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward viewAll(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<FeeDto> customerFees = feeServiceFacade.getCustomerFees();
        List<FeeDto> productFees = feeServiceFacade.getProductFees();

        SessionUtils.setCollectionAttribute(FeeConstants.CUSTOMER_FEES, customerFees, request);
        SessionUtils.setCollectionAttribute(FeeConstants.PRODUCT_FEES, productFees, request);
        return mapping.findForward(ActionForwards.viewAll_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancelCreate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancelCreate_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancelEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancelEdit_success.toString());
    }

    private void loadUpdateMasterData(HttpServletRequest request) throws ApplicationException, SystemException {
        SessionUtils.setCollectionAttribute(FeeConstants.STATUSLIST, getMasterEntities(FeeStatusEntity.class,
                getUserContext(request).getLocaleId()), request);
    }
}
