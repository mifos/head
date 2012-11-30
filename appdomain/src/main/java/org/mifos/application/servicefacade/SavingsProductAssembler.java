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

package org.mifos.application.servicefacade;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.business.service.GeneralLedgerDao;
import org.mifos.accounts.productdefinition.business.InterestCalcTypeEntity;
import org.mifos.accounts.productdefinition.business.PrdApplicableMasterEntity;
import org.mifos.accounts.productdefinition.business.PrdStatusEntity;
import org.mifos.accounts.productdefinition.business.ProductCategoryBO;
import org.mifos.accounts.productdefinition.business.RecommendedAmntUnitEntity;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsTypeEntity;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.productdefinition.persistence.PrdOfferingPersistence;
import org.mifos.accounts.productdefinition.persistence.SavingsProductDao;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.core.MifosRuntimeException;
import org.mifos.dto.domain.ProductDetailsDto;
import org.mifos.dto.domain.SavingsProductDto;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.MifosUser;

public class SavingsProductAssembler {

    private final LoanProductDao loanProductDao;
    private final GeneralLedgerDao generalLedgerDao;
    private final SavingsProductDao savingsProductDao;

    public SavingsProductAssembler(LoanProductDao loanProductDao, SavingsProductDao savingsProductDao, GeneralLedgerDao generalLedgerDao) {
        this.loanProductDao = loanProductDao;
        this.savingsProductDao = savingsProductDao;
        this.generalLedgerDao = generalLedgerDao;
    }

    public SavingsOfferingBO fromDto(MifosUser user, SavingsProductDto savingsProductRequest) {

        try {
            // FIXME - keithw - this is general assembler common to both savings and loans i.e. all products. so
            // ProductDao and ProductAssembler
            ProductDetailsDto productDetails = savingsProductRequest.getProductDetails();
            String name = productDetails.getName();
            String shortName = productDetails.getShortName();
            String description = productDetails.getDescription();
            Integer category = productDetails.getCategory();

            ProductCategoryBO productCategory = this.loanProductDao.findActiveProductCategoryById(category);
            DateTime startDate = productDetails.getStartDate();
            DateTime endDate = productDetails.getEndDate();
            ApplicableTo applicableTo = ApplicableTo.fromInt(productDetails.getApplicableFor());
            PrdApplicableMasterEntity applicableToEntity = this.loanProductDao.findApplicableProductType(applicableTo);

            PrdStatusEntity activeStatus = new PrdOfferingPersistence().getPrdStatus(PrdStatus.SAVINGS_ACTIVE);
            PrdStatusEntity inActiveStatus = new PrdOfferingPersistence().getPrdStatus(PrdStatus.SAVINGS_INACTIVE);

            PrdStatusEntity selectedStatus = activeStatus;
            if (productDetails.getStatus() != null && inActiveStatus.getOfferingStatusId().equals(productDetails.getStatus().shortValue())) {
                selectedStatus = inActiveStatus;
            }

            String globalNum = generateProductGlobalNum(user);

            // savings specific
            SavingsType savingsType = SavingsType.fromInt(savingsProductRequest.getDepositType());
            SavingsTypeEntity savingsTypeEntity = this.loanProductDao.retrieveSavingsType(savingsType);

            RecommendedAmntUnitEntity recommendedAmntUnitEntity = null;
            if (savingsProductRequest.getGroupMandatorySavingsType() != null) {
                RecommendedAmountUnit recommendedAmountType = RecommendedAmountUnit.fromInt(savingsProductRequest.getGroupMandatorySavingsType());
                recommendedAmntUnitEntity = this.loanProductDao.retrieveRecommendedAmountType(recommendedAmountType);
            }

            Money amountForDeposit = new Money(Money.getDefaultCurrency(), BigDecimal.valueOf(savingsProductRequest.getAmountForDeposit()));
            Money maxWithdrawal = new Money(Money.getDefaultCurrency(), BigDecimal.valueOf(savingsProductRequest.getMaxWithdrawal()));

            // interest specific
            BigDecimal interestRate = savingsProductRequest.getInterestRate();

            InterestCalcType interestCalcType = InterestCalcType.fromInt(savingsProductRequest.getInterestCalculationType());
            InterestCalcTypeEntity interestCalcTypeEntity = this.savingsProductDao.retrieveInterestCalcType(interestCalcType);

            RecurrenceType recurrence = RecurrenceType.fromInt(savingsProductRequest.getInterestCalculationFrequencyPeriod().shortValue());
            Integer every = savingsProductRequest.getInterestCalculationFrequency();
            MeetingBO interestCalculationMeeting = new MeetingBO(recurrence, every.shortValue(), new Date(), MeetingType.SAVINGS_INTEREST_CALCULATION_TIME_PERIOD);

            Integer interestPostingEveryMonthFreq = savingsProductRequest.getInterestPostingFrequency();
            RecurrenceType interestPostingRecurrenceType = null;
            
            if (savingsProductRequest.isDailyPosting()) {
                interestPostingRecurrenceType = RecurrenceType.DAILY;
            } else {
                interestPostingRecurrenceType = RecurrenceType.MONTHLY;
            }
            
            MeetingBO interestPostingMeeting = new MeetingBO(interestPostingRecurrenceType, interestPostingEveryMonthFreq.shortValue(),
                    new Date(), MeetingType.SAVINGS_INTEREST_POSTING);

            Money minAmountForCalculation = new Money(Money.getDefaultCurrency(), savingsProductRequest.getMinBalanceForInterestCalculation());

            GLCodeEntity depositGlEntity = this.generalLedgerDao.findGlCodeById(savingsProductRequest.getDepositGlCode().shortValue());
            GLCodeEntity interestGlEntity = this.generalLedgerDao.findGlCodeById(savingsProductRequest.getInterestGlCode().shortValue());

            MifosCurrency currency = Money.getDefaultCurrency();
            return SavingsOfferingBO.createNew(user.getUserId(), globalNum, name, shortName, description,
                        productCategory, startDate, endDate, applicableToEntity, currency, selectedStatus,
                        savingsTypeEntity, recommendedAmntUnitEntity, amountForDeposit, maxWithdrawal,
                        interestRate, interestCalcTypeEntity, interestCalculationMeeting, interestPostingMeeting, minAmountForCalculation, depositGlEntity, interestGlEntity);

        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        } catch (MeetingException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private String generateProductGlobalNum(MifosUser user) {
        StringBuilder globalPrdOfferingNum = new StringBuilder();
        globalPrdOfferingNum.append(user.getBranchId());
        globalPrdOfferingNum.append("-");

        try {
            Short maxPrdID = new PrdOfferingPersistence().getMaxPrdOffering();
            globalPrdOfferingNum.append(StringUtils.leftPad(String.valueOf(maxPrdID != null ? maxPrdID + 1
                    : ProductDefinitionConstants.DEFAULTMAX + 1), 3, '0'));
            return globalPrdOfferingNum.toString();
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }
}
