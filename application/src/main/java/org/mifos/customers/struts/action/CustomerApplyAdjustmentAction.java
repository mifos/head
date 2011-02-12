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
import org.mifos.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.struts.actionforms.CustomerApplyAdjustmentActionForm;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.service.BusinessRuleException;

public class CustomerApplyAdjustmentAction extends BaseAction {

    public CustomerApplyAdjustmentAction() {
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadAdjustment(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        CustomerApplyAdjustmentActionForm applyAdjustmentActionForm = (CustomerApplyAdjustmentActionForm) form;
        resetActionFormFields(applyAdjustmentActionForm);
        String globalCustNum = applyAdjustmentActionForm.getGlobalCustNum();
        CustomerBO customerBO = this.customerDao.findCustomerBySystemId(globalCustNum);
        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, customerBO, request);

        request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_LOAD_ADJUSTMENT);
        if (null == customerBO.getCustomerAccount().findMostRecentNonzeroPaymentByPaymentDate()) {
            request.setAttribute("isDisabled", "true");
            throw new ApplicationException(AccountExceptionConstants.ZEROAMNTADJUSTMENT);
        }
        return mapping.findForward(CustomerConstants.METHOD_LOAD_ADJUSTMENT_SUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewAdjustment(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_PREVIEW_ADJUSTMENT);
        CustomerApplyAdjustmentActionForm applyAdjustmentActionForm = (CustomerApplyAdjustmentActionForm) form;
        String globalCustNum = applyAdjustmentActionForm.getGlobalCustNum();
        CustomerBO customerBO = this.customerDao.findCustomerBySystemId(globalCustNum);
        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, customerBO, request);

        if (null == customerBO.getCustomerAccount().findMostRecentNonzeroPaymentByPaymentDate()) {
            request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_LOAD_ADJUSTMENT);
            request.setAttribute("isDisabled", "true");
            throw new ApplicationException(AccountExceptionConstants.ZEROAMNTADJUSTMENT);
        }
        return mapping.findForward(CustomerConstants.METHOD_PREVIEW_ADJUSTMENT_SUCCESS);
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward applyAdjustment(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String forward = null;
        request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_APPLY_ADJUSTMENT);
        CustomerApplyAdjustmentActionForm applyAdjustmentActionForm = (CustomerApplyAdjustmentActionForm) form;
        String globalCustNum = applyAdjustmentActionForm.getGlobalCustNum();
        CustomerBO customerBO = this.customerDao.findCustomerBySystemId(globalCustNum);

        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, customerBO, request);
        if (null == customerBO.getCustomerAccount().findMostRecentNonzeroPaymentByPaymentDate()) {
            request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_PREVIEW_ADJUSTMENT);
            throw new ApplicationException(AccountExceptionConstants.ZEROAMNTADJUSTMENT);
        }

        try {
            this.centerServiceFacade.revertLastChargesPayment(globalCustNum, applyAdjustmentActionForm.getAdjustmentNote());
        } catch (BusinessRuleException e) {
            request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_PREVIEW_ADJUSTMENT);
            throw e;
        }

        String inputPage = applyAdjustmentActionForm.getInput();
        resetActionFormFields(applyAdjustmentActionForm);
        if (inputPage != null) {
            if (inputPage.equals(CustomerConstants.VIEW_GROUP_CHARGES)) {
                forward = CustomerConstants.APPLY_ADJUSTMENT_GROUP_SUCCESS;
            } else if (inputPage.equals(CustomerConstants.VIEW_CENTER_CHARGES)) {
                forward = CustomerConstants.APPLY_ADJUSTMENT_CENTER_SUCCESS;
            } else if (inputPage.equals(CustomerConstants.VIEW_CLIENT_CHARGES)) {
                forward = CustomerConstants.APPLY_ADJUSTMENT_CLIENT_SUCCESS;
            }
        }
        return mapping.findForward(forward);
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancelAdjustment(ActionMapping mapping, ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String forward = null;
        CustomerApplyAdjustmentActionForm applyAdjustmentActionForm = (CustomerApplyAdjustmentActionForm) form;
        String inputPage = applyAdjustmentActionForm.getInput();
        resetActionFormFields(applyAdjustmentActionForm);
        if (inputPage != null) {
            if (inputPage.equals(CustomerConstants.VIEW_GROUP_CHARGES)) {
                forward = CustomerConstants.CANCELADJ_GROUP_SUCCESS;
            } else if (inputPage.equals(CustomerConstants.VIEW_CENTER_CHARGES)) {
                forward = CustomerConstants.CANCELADJ_CENTER_SUCCESS;
            } else if (inputPage.equals(CustomerConstants.VIEW_CLIENT_CHARGES)) {
                forward = CustomerConstants.CANCELADJ_CLIENT_SUCCESS;
            }
        }
        return mapping.findForward(forward);
    }

    private void resetActionFormFields(CustomerApplyAdjustmentActionForm applyAdjustmentActionForm) {
        applyAdjustmentActionForm.setAdjustmentNote(null);
    }
}