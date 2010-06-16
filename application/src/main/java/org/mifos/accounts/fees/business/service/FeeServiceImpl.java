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

package org.mifos.accounts.fees.business.service;

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
import org.mifos.accounts.fees.servicefacade.FeeCreateRequest;
import org.mifos.accounts.fees.servicefacade.FeeUpdateRequest;
import org.mifos.accounts.fees.util.helpers.FeeChangeType;
import org.mifos.accounts.fees.util.helpers.RateAmountFlag;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.business.service.GeneralLedgerDao;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.config.AccountingRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;

public class FeeServiceImpl implements FeeService {

    private final FeeDao feeDao;
    private final GeneralLedgerDao generalLedgerDao;
    private final HibernateTransactionHelper hibernateTransactionHelper;

    public FeeServiceImpl(FeeDao feeDao, GeneralLedgerDao generalLedgerDao, HibernateTransactionHelper hibernateTransactionHelper) {
        this.feeDao = feeDao;
        this.generalLedgerDao = generalLedgerDao;
        this.hibernateTransactionHelper = hibernateTransactionHelper;
    }

    @Override
    public FeeBO create(FeeCreateRequest feeCreateRequest, UserContext userContext) throws ApplicationException {

        FeeFrequencyTypeEntity feeFrequencyType = this.feeDao.findFeeFrequencyEntityByType(feeCreateRequest.getFeeFrequencyType());
        CategoryTypeEntity feeCategoryType = this.feeDao.findFeeCategoryTypeEntityByType(feeCreateRequest.getCategoryType());
        GLCodeEntity glCodeEntity = this.generalLedgerDao.findGlCodeById(feeCreateRequest.getGlCode());

        FeeBO feeBO = null;

        if (feeFrequencyType.isOneTime()) {
            feeBO = createOneTimeFee(feeCreateRequest, feeFrequencyType, feeCategoryType, glCodeEntity, userContext);
        } else {
            feeBO = createPeriodicFee(feeCreateRequest, feeFrequencyType, feeCategoryType, glCodeEntity, userContext);
        }

        try {
            hibernateTransactionHelper.startTransaction();
            this.feeDao.save(feeBO);
            hibernateTransactionHelper.commitTransaction();
        } catch (Exception e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            hibernateTransactionHelper.closeSession();
        }

        return feeBO;
    }

    private FeeBO createPeriodicFee(FeeCreateRequest feeCreateRequest, FeeFrequencyTypeEntity feeFrequencyType,
            CategoryTypeEntity feeCategoryType, GLCodeEntity glCodeEntity, UserContext userContext)
            throws FeeException, MeetingException {

        MeetingBO feeMeeting = feeCreateRequest.getFeeRecurrenceType().equals(RecurrenceType.MONTHLY) ? new MeetingBO(feeCreateRequest.getFeeRecurrenceType(), feeCreateRequest.getMonthRecurAfter(), new DateTimeService()
                        .getCurrentJavaDateTime(), MeetingType.PERIODIC_FEE) : new MeetingBO(feeCreateRequest.getFeeRecurrenceType(), feeCreateRequest.getWeekRecurAfter(), new DateTimeService().getCurrentJavaDateTime(), MeetingType.PERIODIC_FEE);

        FeeBO feeBO = null;
        if (feeCreateRequest.isRateFee()) {
            FeeFormulaEntity feeFormulaEntity = this.feeDao.findFeeFormulaEntityByType(feeCreateRequest.getFeeFormula());

            feeBO = new RateFeeBO(userContext, feeCreateRequest.getFeeName(), feeCategoryType, feeFrequencyType,
                    glCodeEntity, feeCreateRequest.getRate(), feeFormulaEntity,
                    feeCreateRequest.isCustomerDefaultFee(), feeMeeting);
        } else {
            Money feeMoney = new Money(getCurrency(feeCreateRequest.getCurrencyId()), feeCreateRequest.getAmount());
            feeBO = new AmountFeeBO(userContext, feeCreateRequest.getFeeName(), feeCategoryType, feeFrequencyType,
                    glCodeEntity, feeMoney, feeCreateRequest.isCustomerDefaultFee(), feeMeeting);
        }
        return feeBO;
    }

    private FeeBO createOneTimeFee(FeeCreateRequest feeCreateRequest, FeeFrequencyTypeEntity feeFrequencyType,
            CategoryTypeEntity feeCategoryType, GLCodeEntity glCodeEntity, UserContext userContext) throws FeeException {

        FeePaymentEntity feePaymentEntity = this.feeDao.findFeePaymentEntityByType(feeCreateRequest.getFeePaymentType());

        FeeBO feeBO = null;
        if (feeCreateRequest.isRateFee()) {
            FeeFormulaEntity feeFormulaEntity = this.feeDao.findFeeFormulaEntityByType(feeCreateRequest.getFeeFormula());

            feeBO = new RateFeeBO(userContext, feeCreateRequest.getFeeName(), feeCategoryType, feeFrequencyType,
                    glCodeEntity, feeCreateRequest.getRate(), feeFormulaEntity,
                    feeCreateRequest.isCustomerDefaultFee(), feePaymentEntity);
        } else {
            Money feeMoney = new Money(getCurrency(feeCreateRequest.getCurrencyId()), feeCreateRequest.getAmount());
            feeBO = new AmountFeeBO(userContext, feeCreateRequest.getFeeName(), feeCategoryType, feeFrequencyType,
                    glCodeEntity, feeMoney, feeCreateRequest.isCustomerDefaultFee(), feePaymentEntity);
        }
        return feeBO;
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
    public void update(FeeUpdateRequest feeUpdateRequest, UserContext userContext) throws ApplicationException {

        FeeBO feeBo = this.feeDao.findById(feeUpdateRequest.getFeeId());
        feeBo.updateDetails(userContext);

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
            hibernateTransactionHelper.startTransaction();
            feeBo.updateStatus(feeUpdateRequest.getFeeStatusValue());
            feeBo.updateFeeChangeType(feeChangeType);

            this.feeDao.save(feeBo);
            hibernateTransactionHelper.commitTransaction();
        } catch (ApplicationException e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw new ApplicationException(e.getKey(), e);
        } catch (Exception e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            hibernateTransactionHelper.closeSession();
        }
    }
}