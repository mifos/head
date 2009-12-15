/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.application.fees.struts.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.financial.business.service.FinancialBusinessService;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.CategoryTypeEntity;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeFormulaEntity;
import org.mifos.application.fees.business.FeeFrequencyTypeEntity;
import org.mifos.application.fees.business.FeePaymentEntity;
import org.mifos.application.fees.business.FeeStatusEntity;
import org.mifos.application.fees.business.RateFeeBO;
import org.mifos.application.fees.business.service.FeeBusinessService;
import org.mifos.application.fees.struts.actionforms.FeeActionForm;
import org.mifos.application.fees.util.helpers.FeeChangeType;
import org.mifos.application.fees.util.helpers.FeeConstants;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.SecurityConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class FeeAction extends BaseAction {

    public FeeAction() throws Exception {
    }

    @Override
    protected BusinessService getService() throws ServiceException {
        return getFeeBusinessService();
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
        doCleanUp(request);
        loadCreateMasterData(request);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        request.setAttribute("currencyCode", getSelectedCurrencyFromList((FeeActionForm)form).getCurrencyCode());
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
        FeeBO fee = createFee(actionForm, request);
        
        fee.save();
        actionForm.setFeeId(fee.getFeeId().toString());
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
        FeeActionForm actionForm = (FeeActionForm) form;
        FeeBO fee = getFeeBusinessService().getFee(actionForm.getFeeIdValue());
        setLocaleForMasterEntities(fee, getUserContext(request).getLocaleId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, fee, request);
        return mapping.findForward(ActionForwards.get_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward manage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        FeeBO fee = (FeeBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        FeeActionForm feeActionForm = (FeeActionForm) form;
        feeActionForm.setFeeStatus(fee.getFeeStatus().getId().toString());
        if (fee.getFeeType().equals(RateAmountFlag.AMOUNT)) {
            feeActionForm.setAmount(((AmountFeeBO) fee).getFeeAmount().toString());
            feeActionForm.setRate(null);
            feeActionForm.setFeeFormula(null);
        } else {
            feeActionForm.setRate(((RateFeeBO) fee).getRate().toString());
            feeActionForm.setFeeFormula(((RateFeeBO) fee).getFeeFormula().getId().toString());
            feeActionForm.setAmount(null);
        }
        loadUpdateMasterData(request);
        return mapping.findForward(ActionForwards.manage_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editPreview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.editPreview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editPrevious(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.editprevious_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        FeeActionForm feeActionForm = (FeeActionForm) form;
        FeeBO fee = (FeeBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        fee.setUserContext(getUserContext(request));
        FeeChangeType feeChangeType;
        if (fee.getFeeType().equals(RateAmountFlag.AMOUNT)) {
            AmountFeeBO amountFee = ((AmountFeeBO) fee);
            feeChangeType = amountFee.calculateNewFeeChangeType(feeActionForm.getAmountValue(), new FeeStatusEntity(
                    feeActionForm.getFeeStatusValue()));
            amountFee.setFeeAmount(feeActionForm.getAmountValue());
        } else {
            RateFeeBO rateFee = ((RateFeeBO) fee);
            feeChangeType = rateFee.calculateNewFeeChangeType(feeActionForm.getRateValue(), new FeeStatusEntity(
                    feeActionForm.getFeeStatusValue()));
            rateFee.setRate(feeActionForm.getRateValue());
        }

        fee.updateStatus(feeActionForm.getFeeStatusValue());
        fee.updateFeeChangeType(feeChangeType);
        fee.update();
        return mapping.findForward(ActionForwards.update_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward viewAll(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Short localeId = getUserContext(request).getLocaleId();

        List<FeeBO> customerFees = getFeeBusinessService().retrieveCustomerFees();
        List<FeeBO> productFees = getFeeBusinessService().retrieveProductFees();

        for (FeeBO fee : customerFees)
            setLocaleForMasterEntities(fee, localeId);
        for (FeeBO fee : productFees)
            setLocaleForMasterEntities(fee, localeId);

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

    private LinkedList<MifosCurrency> getCurrencies() {
        //FIXME TODO stubbed code: required to retrieve real list of currencies from configuration
        // First currency to be added in the list should be default currency
        // Depends on mingle story 2176
        LinkedList<MifosCurrency> currencies = new LinkedList<MifosCurrency>();
        currencies.add(Money.getDefaultCurrency());
        return currencies;
    }
    
    private MifosCurrency getSelectedCurrencyFromList(FeeActionForm form) {
        LinkedList<MifosCurrency> currencies = getCurrencies();
        Iterator<MifosCurrency> i = currencies.iterator();
        while(i.hasNext()) {
            MifosCurrency a = i.next();
            if(a.getCurrencyId().equals(form.getCurrencyId())) {
                return a;
            }
        }
        return null;
    }

    private FeeBO createFee(FeeActionForm actionForm, HttpServletRequest request) throws ApplicationException {

        CategoryTypeEntity feeCategory = (CategoryTypeEntity) findMasterEntity(request, FeeConstants.CATEGORYLIST,
                actionForm.getCategoryTypeValue().getValue());
        FeeFrequencyTypeEntity feeFrequencyType = (FeeFrequencyTypeEntity) findMasterEntity(request,
                FeeConstants.FEE_FREQUENCY_TYPE_LIST, actionForm.getFeeFrequencyTypeValue().getValue());
        GLCodeEntity glCode = findGLCodeEntity(request, FeeConstants.GLCODE_LIST, actionForm.getGlCodeValue());

        FeeBO fee = null;
        if (feeFrequencyType.isOneTime()) {
            fee = createOneTimeFee(actionForm, request, feeCategory, feeFrequencyType, glCode);
        } else {
           fee = createPeriodicFee(actionForm, request, feeCategory, feeFrequencyType, glCode);
        }
        fee.setCurrency(getSelectedCurrencyFromList(actionForm));
        return fee;

    }

    private FeeBO createOneTimeFee(FeeActionForm actionForm, HttpServletRequest request,
            CategoryTypeEntity feeCategory, FeeFrequencyTypeEntity feeFrequencyType, GLCodeEntity glCode)
            throws ApplicationException {
        UserContext userContext = getUserContext(request);
        FeePaymentEntity feePayment = (FeePaymentEntity) findMasterEntity(request, FeeConstants.TIMEOFCHARGES,
                actionForm.getFeePaymentTypeValue().getValue());
        if (actionForm.isRateFee()) {
            FeeFormulaEntity feeFormula = (FeeFormulaEntity) findMasterEntity(request, FeeConstants.FORMULALIST,
                    actionForm.getFeeFormulaValue().getValue());
            return new RateFeeBO(userContext, actionForm.getFeeName(), feeCategory, feeFrequencyType, glCode,
                    actionForm.getRateValue(), feeFormula, actionForm.isCustomerDefaultFee(), feePayment);
        }
        return new AmountFeeBO(userContext, actionForm.getFeeName(), feeCategory, feeFrequencyType, glCode,
                actionForm.getAmountValue(), actionForm.isCustomerDefaultFee(), feePayment);
    }

    private FeeBO createPeriodicFee(FeeActionForm actionForm, HttpServletRequest request,
            CategoryTypeEntity feeCategory, FeeFrequencyTypeEntity feeFrequencyType, GLCodeEntity glCode)
            throws ApplicationException {
        UserContext userContext = getUserContext(request);
        MeetingBO feeFrequency = actionForm.getFeeRecurrenceTypeValue().equals(RecurrenceType.MONTHLY) ? new MeetingBO(
                actionForm.getFeeRecurrenceTypeValue(), actionForm.getMonthRecurAfterValue(), new DateTimeService()
                        .getCurrentJavaDateTime(), MeetingType.PERIODIC_FEE) : new MeetingBO(actionForm
                .getFeeRecurrenceTypeValue(), actionForm.getWeekRecurAfterValue(), new DateTimeService()
                .getCurrentJavaDateTime(), MeetingType.PERIODIC_FEE);
        if (actionForm.isRateFee()) {
            FeeFormulaEntity feeFormula = (FeeFormulaEntity) findMasterEntity(request, FeeConstants.FORMULALIST,
                    actionForm.getFeeFormulaValue().getValue());
            return new RateFeeBO(userContext, actionForm.getFeeName(), feeCategory, feeFrequencyType, glCode,
                    actionForm.getRateValue(), feeFormula, actionForm.isCustomerDefaultFee(), feeFrequency);
        }
        return new AmountFeeBO(userContext, actionForm.getFeeName(), feeCategory, feeFrequencyType, glCode,
                actionForm.getAmountValue(), actionForm.isCustomerDefaultFee(), feeFrequency);
    }

    private List<GLCodeEntity> getGLCodes() throws SystemException, ApplicationException {

        return new FinancialBusinessService()
                .getGLCodes(FinancialActionConstants.FEEPOSTING, FinancialConstants.CREDIT);
    }

    private List<MasterDataEntity> getTimeOfChargeForCustomer(List<MasterDataEntity> timeOfCharges) {
        List<MasterDataEntity> customerTimeOfCharges = new ArrayList<MasterDataEntity>();
        for (MasterDataEntity entity : timeOfCharges)
            if (entity.getId().equals(FeePayment.UPFRONT.getValue()))
                customerTimeOfCharges.add(entity);
        return customerTimeOfCharges;
    }

    private void doCleanUp(HttpServletRequest request) {
        request.getSession().setAttribute("feeactionform", null);
    }

    private void loadCreateMasterData(HttpServletRequest request) throws ApplicationException, SystemException {
        Short localeId = getUserContext(request).getLocaleId();
        SessionUtils.setCollectionAttribute(FeeConstants.CATEGORYLIST, getMasterEntities(CategoryTypeEntity.class,
                localeId), request);

        List<MasterDataEntity> timeOfCharges = getMasterEntities(FeePaymentEntity.class, localeId);
        SessionUtils.setCollectionAttribute(FeeConstants.TIMEOFCHARGES, timeOfCharges, request);
        SessionUtils.setCollectionAttribute(FeeConstants.CUSTOMERTIMEOFCHARGES,
                getTimeOfChargeForCustomer(timeOfCharges), request);
        SessionUtils.setCollectionAttribute(FeeConstants.FORMULALIST, getMasterEntities(FeeFormulaEntity.class,
                localeId), request);
        SessionUtils.setCollectionAttribute(FeeConstants.FEE_FREQUENCY_TYPE_LIST, getMasterEntities(
                FeeFrequencyTypeEntity.class, localeId), request);
        SessionUtils.setCollectionAttribute(FeeConstants.GLCODE_LIST, getGLCodes(), request);
        request.setAttribute("currencies", getCurrencies());
    }

    private void loadUpdateMasterData(HttpServletRequest request) throws ApplicationException, SystemException {

        SessionUtils.setCollectionAttribute(FeeConstants.STATUSLIST, getMasterEntities(FeeStatusEntity.class,
                getUserContext(request).getLocaleId()), request);
    }

    private void setLocaleForMasterEntities(FeeBO fee, Short localeId) {
        fee.getCategoryType().setLocaleId(localeId);
        fee.getFeeFrequency().getFeeFrequencyType().setLocaleId(localeId);
        fee.getFeeStatus().setLocaleId(localeId);
        if (fee.isOneTime())
            fee.getFeeFrequency().getFeePayment().setLocaleId(localeId);
        if (fee.getFeeType().equals(RateAmountFlag.RATE))
            ((RateFeeBO) fee).getFeeFormula().setLocaleId(localeId);
    }

    private MasterDataEntity findMasterEntity(HttpServletRequest request, String collectionName, Short value)
            throws PageExpiredException {
        List<MasterDataEntity> entities = (List<MasterDataEntity>) SessionUtils.getAttribute(collectionName, request);
        for (MasterDataEntity entity : entities)
            if (entity.getId().equals(value))
                return entity;
        return null;
    }

    private GLCodeEntity findGLCodeEntity(HttpServletRequest request, String collectionName, Short value)
            throws PageExpiredException {
        List<GLCodeEntity> glCodeList = (List<GLCodeEntity>) SessionUtils.getAttribute(collectionName, request);
        for (GLCodeEntity glCode : glCodeList)
            if (glCode.getGlcodeId().equals(value))
                return glCode;
        return null;
    }

    private FeeBusinessService getFeeBusinessService() throws ServiceException {
        return (FeeBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.FeesService);
    }
}
