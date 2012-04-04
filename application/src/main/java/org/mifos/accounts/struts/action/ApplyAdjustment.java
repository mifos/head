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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AdjustablePaymentDto;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.struts.actionforms.ApplyAdjustmentActionForm;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.servicefacade.ListItem;
import org.mifos.core.MifosRuntimeException;
import org.mifos.dto.domain.AdjustedPaymentDto;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
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

    @TransactionDemarcate(joinToken=true)
    public ActionForward editAdjustment(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ApplyAdjustmentActionForm appAdjustActionForm = (ApplyAdjustmentActionForm) form;
        boolean isRevert = Boolean.parseBoolean(request.getParameter(Constants.ADJUSTMENT_IS_REVERT));
        appAdjustActionForm.setAdjustcheckbox(isRevert);
        
        request.setAttribute("method", "loadAdjustment");
        return mapping.findForward("loadadjustment_success");
    }
    
    @TransactionDemarcate(joinToken = true)
    public ActionForward loadAdjustment(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ApplyAdjustmentActionForm appAdjustActionForm = (ApplyAdjustmentActionForm) form;
        AccountBO accnt = getBizService().findBySystemId(appAdjustActionForm.getGlobalAccountNum());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accnt, request);
        request.setAttribute("method", "loadAdjustment");
        
        UserContext userContext = getUserContext(request);
        SessionUtils.setCollectionAttribute(MasterConstants.PAYMENT_TYPE, 
                this.accountServiceFacade.constructPaymentTypeListForLoanRepayment(userContext.getLocaleId()), request);
        
        AccountPaymentEntity payment = null;
        if (request.getParameter(Constants.ADJ_TYPE_KEY) != null && request.getParameter(Constants.ADJ_TYPE_KEY).equals(Constants.ADJ_SPECIFIC)) {
            Integer paymentId = appAdjustActionForm.getPaymentId();
            payment = accnt.findPaymentById(paymentId);
                        
            AccountPaymentEntity previous = null;
            AccountPaymentEntity next = null;
            boolean getPrevious = false;
            for (AccountPaymentEntity p : accnt.getAccountPayments()) {
                if (!p.getAmount().equals(Money.zero())) {
                    if (getPrevious) {
                        previous = p;
                        break;
                    }
                    else if (p.getPaymentId().equals(payment.getPaymentId())) {
                        getPrevious = true;
                    } else { 
                        next = p;
                    }
                }
            }
            
            Date previousPaymentDate = (previous == null) ? null : previous.getPaymentDate();
            Date nextPaymentDate = (next == null) ? null : next.getPaymentDate();
            
            appAdjustActionForm.setPreviousPaymentDate(previousPaymentDate);
            appAdjustActionForm.setNextPaymentDate(nextPaymentDate);
            
            SessionUtils.setAttribute(Constants.ADJUSTED_AMOUNT, payment.getAmount().getAmount(), request);
        } else {
            payment = accnt.getLastPmntToBeAdjusted();
            appAdjustActionForm.setPaymentId(null);
            SessionUtils.setAttribute(Constants.ADJUSTED_AMOUNT, accnt.getLastPmntAmntToBeAdjusted(), request);
        }
        populateForm(appAdjustActionForm, payment);
        return mapping.findForward("loadadjustment_success");
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward listPossibleAdjustments(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ApplyAdjustmentActionForm appAdjustActionForm = (ApplyAdjustmentActionForm) form;
        AccountBO accnt = getBizService().findBySystemId(appAdjustActionForm.getGlobalAccountNum());
        
        boolean decliningRecalculation = false;
        if (accnt instanceof LoanBO) {
            LoanBO loan = (LoanBO)accnt;
            if (loan.getInterestType().getId().equals(InterestType.DECLINING_PB.getValue())) {
                decliningRecalculation = true;
            }
        }
        
        List<AccountPaymentEntity> payments = accnt.getAccountPayments();
        ArrayList<AdjustablePaymentDto> adjustablePayments = new ArrayList<AdjustablePaymentDto>();
        
        int i = 1;
        for (AccountPaymentEntity payment : payments) {
            //ommit disbursal payment
            if (!payment.getAmount().equals(Money.zero()) && i != payments.size()) {
                AdjustablePaymentDto adjustablePaymentDto = new AdjustablePaymentDto(payment.getPaymentId(), payment.getAmount(), 
                        payment.getPaymentType().getName(), payment.getPaymentDate(), payment.getReceiptDate(), payment.getReceiptNumber());
                
                adjustablePayments.add(adjustablePaymentDto);
                if (decliningRecalculation) {
                    break; //only last payment
                }
            }           
            i++;
        }
        
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accnt, request);
        SessionUtils.setAttribute(Constants.POSSIBLE_ADJUSTMENTS, adjustablePayments, request);
        request.setAttribute("method", "loadAdjustment");   
        
        return mapping.findForward("loadadjustments_success");
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
        
        AccountPaymentEntity payment = null;
        if (request.getParameter(Constants.ADJ_TYPE_KEY) != null && request.getParameter(Constants.ADJ_TYPE_KEY).equals(Constants.ADJ_SPECIFIC)) {
            Integer paymentId = appAdjustActionForm.getPaymentId();
            payment = accnt.findPaymentById(paymentId);           
            SessionUtils.setAttribute(Constants.ADJUSTED_AMOUNT, payment.getAmount().getAmount(), request);
        } else {
            payment = accnt.getLastPmntToBeAdjusted();
            appAdjustActionForm.setPaymentId(null);
            SessionUtils.setAttribute(Constants.ADJUSTED_AMOUNT, accnt.getLastPmntAmntToBeAdjusted(), request);
        }
        
        appAdjustActionForm.setAdjustData(false);
        populateForm(appAdjustActionForm, payment);
        return mapping.findForward("loadadjustment_success");
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewAdjustment(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ApplyAdjustmentActionForm appAdjustActionForm = (ApplyAdjustmentActionForm) form;
        appAdjustActionForm.setAdjustData(!appAdjustActionForm.getAdjustcheckbox());
        
        if (appAdjustActionForm.isAdjustData()) {
            @SuppressWarnings("unchecked")
            List<ListItem<Short>> paymentTypes = (List<ListItem<Short>>)SessionUtils.getAttribute(MasterConstants.PAYMENT_TYPE, request);
            
            Short elementType = Short.valueOf(appAdjustActionForm.getPaymentType());          
            for (ListItem<Short> item : paymentTypes) {
                if (item.getId().equals(elementType)) {
                    SessionUtils.setAttribute(Constants.ADJUSTMENT_PAYMENT_TYPE, item.getDisplayValue(), request);
                }
            }
        }
        
        request.setAttribute("method", "previewAdjustment");
        return mapping.findForward("previewadj_success");
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward applyAdjustment(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        request.setAttribute("method", "applyAdjustment");
        ApplyAdjustmentActionForm appAdjustActionForm = (ApplyAdjustmentActionForm) form;
        Integer paymentId = appAdjustActionForm.getPaymentId();
        
        checkVersionMismatchForAccountBO(request, appAdjustActionForm);
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
        try {
            if (paymentId == null) {
                accountServiceFacade.applyAdjustment(appAdjustActionForm.getGlobalAccountNum(),
                        appAdjustActionForm.getAdjustmentNote(), userContext.getId());
            } else {
                AdjustedPaymentDto adjustedPaymentDto = appAdjustActionForm.getPaymentData();
                accountServiceFacade.applyHistoricalAdjustment(appAdjustActionForm.getGlobalAccountNum(), paymentId,
                        appAdjustActionForm.getAdjustmentNote(), userContext.getId(), adjustedPaymentDto);
            }
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
        appAdjustActionForm.setPaymentId(null);
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
    
    private void populateForm(ApplyAdjustmentActionForm form, AccountPaymentEntity payment) {
        if (payment != null) {
            form.setPaymentType(String.valueOf(payment.getPaymentType().getId()));
            form.setAmount(payment.getAmount().toString());
            form.setTrxnDate(payment.getPaymentDate());
        }
    }
}