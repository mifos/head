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
import org.mifos.accounts.fees.business.FeeFrequencyTypeEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.fees.business.FeeStatusEntity;
import org.mifos.accounts.fees.business.RateFeeBO;
import org.mifos.accounts.fees.exceptions.FeeException;
import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.fees.struts.action.FeeParameters;
import org.mifos.accounts.fees.util.helpers.FeeChangeType;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.fees.util.helpers.RateAmountFlag;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.business.service.GeneralLedgerService;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.servicefacade.FeeDetailsForLoadDto;
import org.mifos.application.servicefacade.FeeDetailsForManageDto;
import org.mifos.application.servicefacade.FeeDetailsForPreviewDto;
import org.mifos.config.AccountingRules;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;

public class WebTierFeeServiceFacade implements FeeServiceFacade {

    private MasterDataService masterDataService = new MasterDataService();

    private final FeeDao feeDao;
    private final GeneralLedgerService generalLedgerService;

    public WebTierFeeServiceFacade(FeeDao feeDao, GeneralLedgerService generalLedgerService) {
        this.feeDao = feeDao;
        this.generalLedgerService = generalLedgerService;
    }

    public List<FeeDto> getProductFees() {

        return this.feeDao.retrieveAllProductFees();
    }

    public List<FeeDto> getCustomerFees() {

        return this.feeDao.retrieveAllCustomerFees();
    }

    @Override
    public FeeParameters parameters(Short localeId) throws ApplicationException {
        List<GLCodeEntity> glCodes = generalLedgerService.retreiveGlCodesBy(FinancialActionConstants.FEEPOSTING, FinancialConstants.CREDIT);

        List<CategoryTypeEntity> categories = this.feeDao.retrieveFeeCategories();
        List<FeeFormulaEntity> formulas = this.feeDao.retrieveFeeFormulae();
        List<FeeFrequencyTypeEntity> frequencies = this.feeDao.retrieveFeeFrequencies();
        List<FeePaymentEntity> timesOfCharging = this.feeDao.retrieveFeePayments();

        List<MasterDataEntity> timesOfChargeingCustomer = new ArrayList<MasterDataEntity>();
        for (MasterDataEntity timeOfCharging : timesOfCharging) {
            if (timeOfCharging.getId().equals(FeePayment.UPFRONT.getValue())) {
                timesOfChargeingCustomer.add(timeOfCharging);
            }
        }

        return new FeeParameters(categories, timesOfCharging, timesOfChargeingCustomer, formulas, frequencies, glCodes);
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
    public FeeDto getFeeDetails(Short feeId) {

        return this.feeDao.findDtoById(feeId);
    }


    @Override
    public FeeDto createFee(FeeCreateRequest feeCreateRequest, UserContext userContext) throws ServiceException {
        try {
            FeeFrequencyTypeEntity feeFrequencyType = masterDataService.retrieveMasterEntity(FeeFrequencyTypeEntity.class, feeCreateRequest.getFeeFrequencyType().getValue(), userContext.getLocaleId());
            CategoryTypeEntity feeCategoryType = masterDataService.retrieveMasterEntity(CategoryTypeEntity.class, feeCreateRequest.getCategoryType().getValue(), userContext.getLocaleId());
            GLCodeEntity glCodeEntity = masterDataService.findGLCodeEntity(feeCreateRequest.getGlCode());

            FeeBO feeBO = null;

            if (feeFrequencyType.isOneTime()) {
                feeBO = createOneTimeFee(feeCreateRequest, feeFrequencyType, feeCategoryType, glCodeEntity, userContext);
            } else {
                feeBO = createPeriodicFee(feeCreateRequest, feeFrequencyType, feeCategoryType, glCodeEntity, userContext);
            }

            return feeBO.toDto();
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        } catch (FeeException e) {
            throw new ServiceException(e);
        } catch (MeetingException e) {
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
                    feeCreateRequest.isCustomerDefaultFee(), feeMeeting);
        } else {
            Money feeMoney = new Money(getCurrency(feeCreateRequest.getCurrencyId()), feeCreateRequest.getAmount());
            feeBO = new AmountFeeBO(userContext, feeCreateRequest.getFeeName(), feeCategoryType, feeFrequencyType,
                    glCodeEntity, feeMoney, feeCreateRequest.isCustomerDefaultFee(), feeMeeting);
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
                    feeCreateRequest.isCustomerDefaultFee(), feePaymentEntity);
        } else {
            Money feeMoney = new Money(getCurrency(feeCreateRequest.getCurrencyId()), feeCreateRequest.getAmount());
            feeBO = new AmountFeeBO(userContext, feeCreateRequest.getFeeName(), feeCategoryType, feeFrequencyType,
                    glCodeEntity, feeMoney, feeCreateRequest.isCustomerDefaultFee(), feePaymentEntity);
        }
        feeBO.save();
        return feeBO;
    }

    @Override
    public void updateFee(FeeUpdateRequest feeUpdateRequest, UserContext userContext) throws FeeException {
        /*
         * Move the following logic into FeeBusinessService.updateFee(feeId)
         */
        FeeBO feeBo = this.feeDao.findById(feeUpdateRequest.getFeeId());
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

        feeBo.updateStatus(feeUpdateRequest.getFeeStatusValue());
        feeBo.updateFeeChangeType(feeChangeType);
        feeBo.update();
    }

    @Override
    public FeeDetailsForLoadDto retrieveDetailsForFeeLoad(Short localeId) throws ApplicationException {
        FeeParameters feeParameters = parameters(localeId);
        boolean isMultiCurrencyEnabled = AccountingRules.isMultiCurrencyEnabled();
        List<MifosCurrency> currencies = AccountingRules.getCurrencies();
        return new FeeDetailsForLoadDto(feeParameters, isMultiCurrencyEnabled, currencies);
    }

    @Override
    public FeeDetailsForPreviewDto retrieveDetailsforFeePreview(Short currencyId) {

        boolean isMultiCurrencyEnabled = AccountingRules.isMultiCurrencyEnabled();
        String currencyCode = "";

        if (isMultiCurrencyEnabled) {
            currencyCode = getCurrency(currencyId).getCurrencyCode();
        }

        return new FeeDetailsForPreviewDto(isMultiCurrencyEnabled, currencyCode);
    }

    @Override
    public FeeDetailsForManageDto retrieveDetailsForFeeManage(Short feeId, Short localeId) throws ApplicationException {
        FeeDto fee = getFeeDetails(feeId);
        List<FeeStatusEntity> feeStatuses = new MasterPersistence().retrieveMasterEntities(FeeStatusEntity.class, localeId);

        return new FeeDetailsForManageDto(fee, feeStatuses);
    }
}