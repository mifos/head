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

package org.mifos.accounts.fees.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.mifos.accounts.fees.business.FeeStatusEntity;
import org.mifos.accounts.fees.struts.actionforms.FeeActionForm;
import org.mifos.accounts.fees.util.helpers.FeeConstants;
import org.mifos.accounts.fees.util.helpers.FeeStatus;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.AccountingRules;
import org.mifos.dto.domain.FeeCreateDto;
import org.mifos.dto.domain.FeeDto;
import org.mifos.dto.domain.FeeUpdateRequest;
import org.mifos.dto.screen.FeeDetailsForLoadDto;
import org.mifos.dto.screen.FeeDetailsForPreviewDto;
import org.mifos.dto.screen.FeeParameters;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class FeeAction extends BaseAction {

    public FeeAction() throws Exception {
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        form.reset(mapping, request);
        ((FeeActionForm) form).clear();

        FeeDetailsForLoadDto feeDetailsForLoad = this.feeServiceFacade.retrieveDetailsForFeeLoad();

        request.getSession().setAttribute("isMultiCurrencyEnabled", feeDetailsForLoad.isMultiCurrencyEnabled());
        request.getSession().setAttribute(FeeParameters.class.getSimpleName(), feeDetailsForLoad.getFeeParameters());

        request.getSession().setAttribute("currencies", AccountingRules.getCurrencies());
        
        request.getSession().setAttribute("GlNamesMode", AccountingRules.getGlNamesMode());
        
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
    public ActionForward create(ActionMapping mapping, ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        FeeActionForm actionForm = (FeeActionForm) form;

        Short feeCategory = null;
        if (actionForm.getCategoryTypeValue() != null) {
            feeCategory = actionForm.getCategoryTypeValue().getValue();
        }

        Short feeFrequency = null;
        if (actionForm.getFeeFrequencyTypeValue() != null) {
            feeFrequency = actionForm.getFeeFrequencyTypeValue().getValue();
        }

        Short feePayment = null;
        if (actionForm.getFeePaymentTypeValue() != null) {
            feePayment = actionForm.getFeePaymentTypeValue().getValue();
        }

        Short feeFormula = null;
        if (actionForm.getFeeFormulaValue() != null) {
            feeFormula = actionForm.getFeeFormulaValue().getValue();
        }

        Short feeRecurrence = null;
        if (actionForm.getFeeRecurrenceTypeValue() != null) {
            feeRecurrence = actionForm.getFeeRecurrenceTypeValue().getValue();
        }
        FeeCreateDto feeCreateRequest = new FeeCreateDto(feeCategory, feeFrequency, actionForm.getGlCodeValue(), feePayment,
                feeFormula, actionForm.getFeeName(), actionForm.isRateFee(), actionForm
                        .isCustomerDefaultFee(), actionForm.getRateValue(), actionForm.getCurrencyId(), actionForm
                        .getAmount(), feeRecurrence, actionForm.getMonthRecurAfterValue(), actionForm.getWeekRecurAfterValue());

        String feeId = this.feeServiceFacade.createFee(feeCreateRequest);

        actionForm.setFeeId(feeId);
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

        Short feeId = ((FeeActionForm) form).getFeeIdValue();
        FeeDto feeDto = this.feeDao.findDtoById(feeId);
        request.setAttribute("feeModel", feeDto);
        request.setAttribute("GlNamesMode", AccountingRules.getGlNamesMode());
        return mapping.findForward(ActionForwards.get_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward manage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        FeeActionForm feeActionForm = (FeeActionForm) form;

        Short feeId = Short.valueOf(feeActionForm.getFeeId());

        FeeDto fee = this.feeDao.findDtoById(feeId);
        List<FeeStatusEntity> feeStatuses = this.feeDao.retrieveFeeStatuses();

        SessionUtils.setCollectionAttribute(FeeConstants.STATUSLIST, feeStatuses, request);
        feeActionForm.updateWithFee(fee);
        request.getSession().setAttribute("feeModel", fee);
        return mapping.findForward(ActionForwards.manage_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editPreview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
    	FeeActionForm feeActionForm = (FeeActionForm) form;
    	
        Short feeId = ((FeeActionForm) form).getFeeIdValue();
        FeeDto feeDto = this.feeDao.findDtoById(feeId);
        
		Short appliedFeeId = this.feeDao.findFeeAppliedToLoan(feeId);
		boolean isInProducts = appliedFeeId == null ? true : false;
		List<Short> feesAppliedLoanAccountList = this.feeDao.getAllAtachedFeesToLoanAcounts();
		boolean isFeeAppliedToLoan = feesAppliedLoanAccountList.contains(feeId);
        ActionMessages messages = new ActionMessages();
        if (feeActionForm.getFeeStatus().equals("2")) {
        	feeActionForm.setToRemove(true);
        }
        if (feeActionForm.isToRemove()) {
	    	if (!isInProducts && !isFeeAppliedToLoan) {
	    		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("Fees.feeRemoved"));
	    	}
	    	else if (isFeeAppliedToLoan) {
	    		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("Fees.feeRemovedFromPrd"));
	    	}
	    	else {
	    		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("Fees.feeNotUsedRemove"));
	    	}
        }
		saveErrors(request, messages);

        request.getSession().setAttribute("feeModel", feeDto);
        return mapping.findForward(ActionForwards.editPreview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editPrevious(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        Short feeId = ((FeeActionForm) form).getFeeIdValue();
        FeeDto feeDto = this.feeDao.findDtoById(feeId);

        request.setAttribute("feeModel", feeDto);
        return mapping.findForward(ActionForwards.editprevious_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(ActionMapping mapping, ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        FeeActionForm feeActionForm = (FeeActionForm) form;
        
        FeeStatus feeStatus = feeActionForm.getFeeStatusValue();
        String forward = "";
        Short feeStatusValue = null;
        
        if (feeStatus != null) {
            feeStatusValue = feeStatus.getValue();
        }
        FeeUpdateRequest feeUpdateRequest = new FeeUpdateRequest(Short.valueOf(feeActionForm.getFeeId()), feeActionForm.getCurrencyId(),
                feeActionForm.getAmount(), feeStatusValue, feeActionForm.getRateValue());
        
        if (feeActionForm.isToRemove() && feeUpdateRequest.getFeeStatusValue() == 2) {
        	this.feeServiceFacade.updateFee(feeUpdateRequest);
        	this.feeServiceFacade.removeFee(feeUpdateRequest);
        	forward = ActionForwards.remove_success.toString();
            List<FeeDto> customerFees = this.feeDao.retrieveAllCustomerFees();
            List<FeeDto> productFees = this.feeDao.retrieveAllProductFees();
            
            SessionUtils.setCollectionAttribute(FeeConstants.CUSTOMER_FEES, customerFees, request);
            SessionUtils.setCollectionAttribute(FeeConstants.PRODUCT_FEES, productFees, request);
        }
        else if (feeActionForm.isToRemove() && feeUpdateRequest.getFeeStatusValue() == 1) {
        	ActionMessages errors = new ActionMessages();
        	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("Fees.feeCantBeRemove"));
        	saveErrors(request, errors);
        	forward = ActionForwards.update_failure.toString();
        }
        else {
        	this.feeServiceFacade.updateFee(feeUpdateRequest);
        	forward = ActionForwards.update_success.toString();
        }
        
        return mapping.findForward(forward);
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward viewAll(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        List<FeeDto> customerFees = this.feeDao.retrieveAllCustomerFees();
        List<FeeDto> productFees = this.feeDao.retrieveAllProductFees();

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