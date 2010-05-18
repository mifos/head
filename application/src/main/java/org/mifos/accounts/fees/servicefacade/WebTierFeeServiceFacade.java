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

package org.mifos.accounts.fees.servicefacade;

import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.CategoryTypeEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeFormulaEntity;
import org.mifos.accounts.fees.business.FeeFrequencyEntity;
import org.mifos.accounts.fees.business.FeeFrequencyTypeEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.fees.business.FeeStatusEntity;
import org.mifos.accounts.fees.business.RateFeeBO;
import org.mifos.accounts.fees.business.service.FeeBusinessService;
import org.mifos.accounts.fees.entities.AmountFeeEntity;
import org.mifos.accounts.fees.entities.FeeEntity;
import org.mifos.accounts.fees.entities.RateFeeEntity;
import org.mifos.accounts.fees.exceptions.FeeException;
import org.mifos.accounts.fees.struts.action.FeeParameters;
import org.mifos.accounts.fees.util.helpers.FeeChangeType;
import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.fees.util.helpers.RateAmountFlag;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.business.service.FinancialBusinessService;
import org.mifos.accounts.financial.servicefacade.GLCodeDto;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.config.AccountingRules;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;

public class WebTierFeeServiceFacade implements FeeServiceFacade {

    private FeeBusinessService feeBusinessService = new FeeBusinessService();
    private MasterDataService masterDataService = new MasterDataService();
    private FinancialBusinessService financeService = new FinancialBusinessService();
    
    @Autowired
    private FeeService feeService;

    public WebTierFeeServiceFacade() {
        super();
    }

    /**
     * FIXME: only used in test... remove when wired via Spring
     */
    public WebTierFeeServiceFacade(FeeBusinessService feeBusinessService) {
        super();
        this.feeBusinessService = feeBusinessService;
    }
    public List<FeeDto> getProductFees() throws ServiceException {
        List<FeeBO> feeBOs = feeBusinessService.retrieveProductFees();
        try {
            return mapFeeDtos(feeBOs);
        } catch (PropertyNotFoundException e) {
            throw new ServiceException(e);
        }
    }

    public List<FeeDto> getCustomerFees() throws ServiceException {
        List<FeeBO> feeBOs = feeBusinessService.retrieveCustomerFees();
        try {
            return mapFeeDtos(feeBOs);
        } catch (PropertyNotFoundException e) {
            throw new ServiceException(e);
        }
    }

    private List<FeeDto> mapFeeDtos(List<FeeBO> feeBOs) throws PropertyNotFoundException {
        List<FeeDto> fees = new ArrayList<FeeDto>();
        for (FeeBO feeBO : feeBOs) {
            fees.add(mapFeeDto(feeBO));
        }
        return fees;
    }

    private FeeDto mapFeeDto(FeeBO feeBO) throws PropertyNotFoundException {
        FeeDto feeDto = new FeeDto();
        feeDto.setId(Short.toString(feeBO.getFeeId()));
        feeDto.setName(feeBO.getFeeName());
        feeDto.setCategoryType(feeBO.getCategoryType().getName());
        feeDto.setFeeStatus(mapFeeStatusDto(feeBO.getFeeStatus()));
        feeDto.setOneTime(feeBO.isOneTime());
        feeDto.setFeeFrequency(mapFeeFrequencyDto(feeBO.getFeeFrequency()));
        feeDto.setActive(feeBO.isActive());
        feeDto.setGlCode(feeBO.getGlCode().getGlcode());
        feeDto.setCustomerDefaultFee(feeBO.isCustomerDefaultFee());
        feeDto.setRateBasedFee(feeBO instanceof RateFeeBO);

        feeDto.setChangeType(feeBO.getFeeChangeType().getValue());
        feeDto.setFeeFrequencyType(feeBO.getFeeFrequency().getFeeFrequencyType().getName());
        feeDto.setGlCodeDto(mapGLCodeDto(feeBO.getGlCode()));

        feeDto.setOneTime(feeBO.getFeeFrequency().isOneTime());
        feeDto.setPeriodic(feeBO.getFeeFrequency().isPeriodic());
        feeDto.setTimeOfDisbursement(feeBO.getFeeFrequency().isTimeOfDisbursement());

        if (feeBO instanceof AmountFeeBO) {
            feeDto.setAmount(((AmountFeeBO) feeBO).getFeeAmount());
        } else {
            RateFeeBO rateFeeBo = (RateFeeBO) feeBO;
            feeDto.setRate(rateFeeBo.getRate());
            feeDto.setFeeFormula(mapFeeFormulaDto(rateFeeBo.getFeeFormula()));
        }
        return feeDto;
    }

    private FeeFormulaDto mapFeeFormulaDto(FeeFormulaEntity feeFormula) {
        FeeFormulaDto feeFormulaDto = new FeeFormulaDto();
        feeFormulaDto.setId(feeFormula.getId());
        feeFormulaDto.setName(feeFormula.getName());
        return feeFormulaDto;
    }

    private FeeFrequencyDto mapFeeFrequencyDto(FeeFrequencyEntity feeFrequencyEntity) {
        FeeFrequencyDto feeFrequencyDto = new FeeFrequencyDto();
        FeeFrequencyTypeEntity feeFrequencyType = feeFrequencyEntity.getFeeFrequencyType();
        feeFrequencyDto.setType(feeFrequencyType.getName());
        if (feeFrequencyType.isOneTime()) {
            feeFrequencyDto.setPayment(feeFrequencyEntity.getFeePayment().getName());
        } else {
            MeetingBO feeMeetingFrequency = feeFrequencyEntity.getFeeMeetingFrequency();
            feeFrequencyDto.setMonthly(feeMeetingFrequency.isMonthly());
            feeFrequencyDto.setWeekly(feeMeetingFrequency.isWeekly());
            feeFrequencyDto.setRecurAfterPeriod(feeMeetingFrequency.getRecurAfter().toString());
        }
        return feeFrequencyDto;
    }


    private GLCodeDto mapGLCodeDto(GLCodeEntity glCode) {
        GLCodeDto glCodeDto = new GLCodeDto();
        glCodeDto.setGlcode(glCode.getGlcode());
        glCodeDto.setGlcodeId(glCode.getGlcodeId());
        return glCodeDto;
    }

    private FeeStatusDto mapFeeStatusDto(FeeStatusEntity feeStatusEntity) {
        FeeStatusDto feeStatus = new FeeStatusDto();
        feeStatus.setId(Short.toString(feeStatusEntity.getId()));
        feeStatus.setName(feeStatusEntity.getName());
        return feeStatus;
    }

    @Override
    public FeeParameters getFeeParameters(Short localeId) throws ServiceException {
        List<GLCodeEntity> glCodes = retrieveGlCodes();
        List<CategoryTypeEntity> categories = masterDataService.retrieveMasterEntities(CategoryTypeEntity.class,
                localeId);
        List<FeeFormulaEntity> formulas = masterDataService.retrieveMasterEntities(FeeFormulaEntity.class, localeId);
        List<FeeFrequencyTypeEntity> frequencies = masterDataService.retrieveMasterEntities(
                FeeFrequencyTypeEntity.class, localeId);
        List<FeePaymentEntity> timesOfCharging = masterDataService.retrieveMasterEntities(FeePaymentEntity.class,
                localeId);
        List<MasterDataEntity> timesOfChargeingCustomer = new ArrayList<MasterDataEntity>();
        for (MasterDataEntity timeOfCharging : timesOfCharging) {
            if (timeOfCharging.getId().equals(FeePayment.UPFRONT.getValue())) {
                timesOfChargeingCustomer.add(timeOfCharging);
            }
        }
        return new FeeParameters(categories, timesOfCharging, timesOfChargeingCustomer, formulas, frequencies, glCodes);
    }

    private List<GLCodeEntity> retrieveGlCodes() throws ServiceException {
        // FIXME: Should General ledger code retrieval logic be here? As in should one service call another service
        List<GLCodeEntity> glCodes;
        try {
            glCodes = financeService.getGLCodes(FinancialActionConstants.FEEPOSTING, FinancialConstants.CREDIT);
        } catch (SystemException e) {
            throw new ServiceException(e);
        } catch (ApplicationException e) {
            throw new ServiceException(e);
        }
        return glCodes;
    }



    private MifosCurrency getCurrency(Short currencyId) {
        MifosCurrency currency;
        if (currencyId == null) {
            // Currency is passed from Form only for Loan (Amount) Fees in multi-currency settings
            currency = Money.getDefaultCurrency();
        } else {
            currency = AccountingRules.getCurrencyByCurrencyId(currencyId);
        }
        return currency;
    }

    @Override
    public FeeDto getFeeDetails(Short feeId) throws ServiceException {
        try {
            return mapFeeDto(feeBusinessService.getFee(feeId));
        } catch (PropertyNotFoundException e) {
            throw new ServiceException(e);
        }
    }


    @Override
    public FeeDto createFeeSpike(FeeCreateRequest feeCreateRequest, UserContext userContext) throws ServiceException {
        try {
            Money feeMoney = new Money(getCurrency(feeCreateRequest.getCurrencyId()), feeCreateRequest.getAmount());
            GLCodeEntity glCodeEntity = masterDataService.findGLCodeEntity(feeCreateRequest.getGlCode());
            FeeEntity fee;
            if (feeCreateRequest.getFeeFrequencyType().equals(FeeFrequencyType.ONETIME)) {
                fee = feeService.createOneTimeFee(userContext,
                        feeCreateRequest.getFeeName(), feeCreateRequest.isCustomerDefaultFee(),
                        feeCreateRequest.isRateFee(), feeCreateRequest.getRate(), feeMoney,
                        feeCreateRequest.getCategoryType(), feeCreateRequest.getFeeFormula(),
                        glCodeEntity, getHeadOffice(), feeCreateRequest.getFeePaymentType());
            } else {
                Short recurAfter =
                    feeCreateRequest.getFeeRecurrenceType().equals(RecurrenceType.MONTHLY) ?
                            feeCreateRequest.getMonthRecurAfter() : feeCreateRequest.getWeekRecurAfter();
                fee = feeService.createPeriodicFee(userContext,
                        feeCreateRequest.getFeeName(), feeCreateRequest.isCustomerDefaultFee(),
                        feeCreateRequest.isRateFee(), feeCreateRequest.getRate(), feeMoney,
                        feeCreateRequest.getCategoryType(), feeCreateRequest.getFeeFormula(),
                        glCodeEntity, getHeadOffice(), feeCreateRequest.getFeeRecurrenceType(), recurAfter);
            }
            return mapFeeDto(fee);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        } catch (FeeException e) {
            throw new ServiceException(e);
        } catch (PropertyNotFoundException e) {
            throw new ServiceException(e);
        }
    }

    private FeeDto mapFeeDto(FeeEntity fee) throws PropertyNotFoundException {
        FeeDto feeDto = new FeeDto();
        feeDto.setId(Short.toString(fee.getFeeId()));
        feeDto.setName(fee.getFeeName());
        feeDto.setCategoryType(fee.getCategoryType().getName());
        feeDto.setFeeStatus(mapFeeStatusDto(fee.getFeeStatus()));
        feeDto.setOneTime(fee.getFeeFrequency().isOneTime());
        feeDto.setFeeFrequency(mapFeeFrequencyDto(fee.getFeeFrequency()));
        feeDto.setActive(fee.getFeeStatus().isActive());
        feeDto.setGlCode(fee.getGlCode().getGlcode());
        feeDto.setCustomerDefaultFee(fee.isCustomerDefaultFee());
        feeDto.setRateBasedFee(fee instanceof RateFeeEntity);

        feeDto.setChangeType(fee.getFeeChangeType().getValue());
        feeDto.setFeeFrequencyType(fee.getFeeFrequency().getFeeFrequencyType().getName());
        feeDto.setGlCodeDto(mapGLCodeDto(fee.getGlCode()));

        feeDto.setOneTime(fee.getFeeFrequency().isOneTime());
        feeDto.setPeriodic(fee.getFeeFrequency().isPeriodic());
        feeDto.setTimeOfDisbursement(fee.getFeeFrequency().isTimeOfDisbursement());

        if (fee instanceof AmountFeeEntity) {
            feeDto.setAmount(((AmountFeeEntity) fee).getFeeAmount());
        } else {
            RateFeeEntity rateFee = (RateFeeEntity) fee;
            feeDto.setRate(rateFee.getRate());
            feeDto.setFeeFormula(mapFeeFormulaDto(rateFee.getFeeFormula()));
        }
        return feeDto;
    }

    private FeeFrequencyDto mapFeeFrequencyDto(org.mifos.accounts.fees.entities.FeeFrequencyEntity feeFrequency) {
        FeeFrequencyDto feeFrequencyDto = new FeeFrequencyDto();
        FeeFrequencyTypeEntity feeFrequencyType = feeFrequency.getFeeFrequencyType();
        feeFrequencyDto.setType(feeFrequencyType.getName());
        if (feeFrequencyType.isOneTime()) {
            feeFrequencyDto.setPayment(feeFrequency.getFeePayment().getName());
        } else {
            MeetingBO feeMeetingFrequency = feeFrequency.getFeeMeetingFrequency();
            feeFrequencyDto.setMonthly(feeMeetingFrequency.isMonthly());
            feeFrequencyDto.setWeekly(feeMeetingFrequency.isWeekly());
            feeFrequencyDto.setRecurAfterPeriod(feeMeetingFrequency.getRecurAfter().toString());
        }
        return feeFrequencyDto;
    }

    @Override
    public FeeDto createFee(FeeCreateRequest feeCreateRequest, UserContext userContext) throws ServiceException {
        try {
            GLCodeEntity glCodeEntity = masterDataService.findGLCodeEntity(feeCreateRequest.getGlCode());
            FeeFrequencyTypeEntity feeFrequencyType =
                masterDataService.retrieveMasterEntity(
                        FeeFrequencyTypeEntity.class, feeCreateRequest.getFeeFrequencyType().getValue(), userContext.getLocaleId());
            CategoryTypeEntity feeCategoryType = masterDataService.retrieveMasterEntity(
                    CategoryTypeEntity.class, feeCreateRequest.getCategoryType().getValue(), userContext.getLocaleId());

            //FIXME: replace with (feeCreateRequest.getFeeFrequencyType() == FeeFrequencyType.ONETIME) ??
            FeeBO feeBO = feeFrequencyType.isOneTime() ?
                    createOneTimeFee(feeCreateRequest, feeFrequencyType, feeCategoryType, glCodeEntity, userContext) :
                        createPeriodicFee(feeCreateRequest, feeFrequencyType, feeCategoryType, glCodeEntity, userContext);
            return mapFeeDto(feeBO);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        } catch (FeeException e) {
            throw new ServiceException(e);
        } catch (MeetingException e) {
            throw new ServiceException(e);
        } catch (PropertyNotFoundException e) {
            throw new ServiceException(e);
        }
    }

    private FeeBO createPeriodicFee(FeeCreateRequest feeCreateRequest, FeeFrequencyTypeEntity feeFrequencyType,
            CategoryTypeEntity feeCategoryType, GLCodeEntity glCodeEntity, UserContext userContext)
            throws FeeException, PersistenceException, MeetingException {


        MeetingBO feeMeeting = feeCreateRequest.getFeeRecurrenceType().equals(RecurrenceType.MONTHLY) ? new MeetingBO(
                feeCreateRequest.getFeeRecurrenceType(), feeCreateRequest.getMonthRecurAfter(), new DateTimeService()
                        .getCurrentJavaDateTime(), MeetingType.PERIODIC_FEE) : new MeetingBO(feeCreateRequest
                .getFeeRecurrenceType(), feeCreateRequest.getWeekRecurAfter(), new DateTimeService()
                .getCurrentJavaDateTime(), MeetingType.PERIODIC_FEE);
        FeeBO feeBO = null;
        if (feeCreateRequest.isRateFee()) {
            FeeFormulaEntity feeFormulaEntity = masterDataService.retrieveMasterEntity(FeeFormulaEntity.class,
                    feeCreateRequest.getFeeFormula().getValue(), userContext.getLocaleId());
            feeBO = new RateFeeBO(userContext, feeCreateRequest.getFeeName(), feeCategoryType, feeFrequencyType,
                    glCodeEntity, feeCreateRequest.getRate(), feeFormulaEntity,
                    feeCreateRequest.isCustomerDefaultFee(), feeMeeting, getHeadOffice());
        } else {
            Money feeMoney = new Money(getCurrency(feeCreateRequest.getCurrencyId()), feeCreateRequest.getAmount());
            feeBO = new AmountFeeBO(userContext, feeCreateRequest.getFeeName(), feeCategoryType, feeFrequencyType,
                    glCodeEntity, feeMoney, feeCreateRequest.isCustomerDefaultFee(), feeMeeting, getHeadOffice());
        }
        feeBO.save();
        return feeBO;

    }

    private FeeBO createOneTimeFee(FeeCreateRequest feeCreateRequest, FeeFrequencyTypeEntity feeFrequencyType,
            CategoryTypeEntity feeCategoryType, GLCodeEntity glCodeEntity, UserContext userContext)
            throws PersistenceException, FeeException {
        FeePaymentEntity feePaymentEntity = masterDataService.retrieveMasterEntity(FeePaymentEntity.class,
                feeCreateRequest.getFeePaymentType().getValue(), userContext.getLocaleId());
        FeeBO feeBO = null;
        if (feeCreateRequest.isRateFee()) {
            FeeFormulaEntity feeFormulaEntity = masterDataService.retrieveMasterEntity(FeeFormulaEntity.class,
                    feeCreateRequest.getFeeFormula().getValue(), userContext.getLocaleId());
            feeBO = new RateFeeBO(userContext, feeCreateRequest.getFeeName(), feeCategoryType, feeFrequencyType,
                    glCodeEntity, feeCreateRequest.getRate(), feeFormulaEntity,
                    feeCreateRequest.isCustomerDefaultFee(), feePaymentEntity, getHeadOffice());
        } else {
            Money feeMoney = new Money(getCurrency(feeCreateRequest.getCurrencyId()), feeCreateRequest.getAmount());
            feeBO = new AmountFeeBO(userContext, feeCreateRequest.getFeeName(), feeCategoryType, feeFrequencyType,
                    glCodeEntity, feeMoney, feeCreateRequest.isCustomerDefaultFee(), feePaymentEntity, getHeadOffice());
        }
        feeBO.save();
        return feeBO;
    }

    private OfficeBO getHeadOffice() throws PersistenceException {
        //FIXME:
        return new OfficePersistence().getHeadOffice();
    }

    @Override
    public void updateFee(FeeUpdateRequest feeUpdateRequest, UserContext userContext) throws ServiceException {
        /*
         * Move the following logic into FeeBusinessService.updateFee(feeId)
         */
        FeeBO feeBo = feeBusinessService.getFee(feeUpdateRequest.getFeeId());
        feeBo.setUserContext(userContext);
        FeeChangeType feeChangeType;
        if (feeBo.getFeeType().equals(RateAmountFlag.AMOUNT)) {
            AmountFeeBO amountFee = ((AmountFeeBO) feeBo);
            feeChangeType = amountFee.calculateNewFeeChangeType(new Money(getCurrency(feeUpdateRequest.getCurrencyId()),
                    feeUpdateRequest.getAmount()), new FeeStatusEntity(feeUpdateRequest.getFeeStatusValue()));
            amountFee.setFeeAmount(new Money(getCurrency(feeUpdateRequest.getCurrencyId()), feeUpdateRequest.getAmount()));
        } else {
            RateFeeBO rateFee = ((RateFeeBO) feeBo);
            feeChangeType = rateFee.calculateNewFeeChangeType(feeUpdateRequest.getRateValue(), new FeeStatusEntity(
                    feeUpdateRequest.getFeeStatusValue()));
            rateFee.setRate(feeUpdateRequest.getRateValue());
        }

        try {
            feeBo.updateStatus(feeUpdateRequest.getFeeStatusValue());
            feeBo.updateFeeChangeType(feeChangeType);
            feeBo.update();
        } catch (FeeException e) {
            throw new ServiceException(e);
        }

    }

}
