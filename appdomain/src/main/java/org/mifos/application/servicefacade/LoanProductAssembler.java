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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.business.service.GeneralLedgerDao;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.fund.persistence.FundDao;
import org.mifos.accounts.penalties.business.PenaltyBO;
import org.mifos.accounts.penalties.persistence.PenaltyDao;
import org.mifos.accounts.productdefinition.LoanAmountCalculation;
import org.mifos.accounts.productdefinition.LoanInstallmentCalculation;
import org.mifos.accounts.productdefinition.LoanProductCaluclationTypeAssembler;
import org.mifos.accounts.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.PrdApplicableMasterEntity;
import org.mifos.accounts.productdefinition.business.PrdStatusEntity;
import org.mifos.accounts.productdefinition.business.ProductCategoryBO;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.productdefinition.persistence.PrdOfferingPersistence;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.config.AccountingRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.dto.domain.LoanProductRequest;
import org.mifos.dto.domain.ProductDetailsDto;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.MifosUser;

public class LoanProductAssembler {

    private final LoanProductDao loanProductDao;
    private final GeneralLedgerDao generalLedgerDao;
    private final FundDao fundDao;
    private final PenaltyDao penaltyDao;
    private LoanProductCaluclationTypeAssembler loanProductCaluclationTypeAssembler = new LoanProductCaluclationTypeAssembler();

    public LoanProductAssembler(LoanProductDao loanProductDao, GeneralLedgerDao generalLedgerDao, FundDao fundDao,
            PenaltyDao penaltyDao) {
        this.loanProductDao = loanProductDao;
        this.generalLedgerDao = generalLedgerDao;
        this.fundDao = fundDao;
        this.penaltyDao = penaltyDao;
    }

    public LoanOfferingBO fromDto(MifosUser user, LoanProductRequest loanProductRequest) {

        try {

            Integer userId = user.getUserId();
            ProductDetailsDto productDetails = loanProductRequest.getProductDetails();

            String name = productDetails.getName();
            String shortName = productDetails.getShortName();
            String description = productDetails.getDescription();
            Integer category = productDetails.getCategory();
            boolean loanCycleCounter = loanProductRequest.isIncludeInLoanCycleCounter();
            boolean waiverInterest = loanProductRequest.isWaiverInterest();

            PrdStatusEntity activeStatus = new PrdOfferingPersistence().getPrdStatus(PrdStatus.LOAN_ACTIVE);
            PrdStatusEntity inActiveStatus = new PrdOfferingPersistence().getPrdStatus(PrdStatus.LOAN_INACTIVE);

            PrdStatusEntity selectedStatus = activeStatus;
            if (productDetails.getStatus() != null
                    && inActiveStatus.getOfferingStatusId().equals(productDetails.getStatus().shortValue())) {
                selectedStatus = inActiveStatus;
            }

            MifosCurrency currency = Money.getDefaultCurrency();
            if (AccountingRules.isMultiCurrencyEnabled()) {
                currency = AccountingRules.getCurrencyByCurrencyId(loanProductRequest.getCurrencyId().shortValue());
            }

            ProductCategoryBO productCategory = this.loanProductDao.findActiveProductCategoryById(category);
            DateTime startDate = productDetails.getStartDate();
            DateTime endDate = productDetails.getEndDate();
            ApplicableTo applicableTo = ApplicableTo.fromInt(productDetails.getApplicableFor());
            PrdApplicableMasterEntity applicableToEntity = this.loanProductDao.findApplicableProductType(applicableTo);

            LoanAmountCalculation loanAmountCalculation = this.loanProductCaluclationTypeAssembler
                    .assembleLoanAmountCalculationFromDto(loanProductRequest.getLoanAmountDetails());

            InterestType interestType = InterestType.fromInt(loanProductRequest.getInterestRateType());
            Double minRate = loanProductRequest.getInterestRateRange().getMin().doubleValue();
            Double maxRate = loanProductRequest.getInterestRateRange().getMax().doubleValue();
            Double defaultRate = loanProductRequest.getInterestRateRange().getTheDefault().doubleValue();

            InterestTypesEntity interestTypeEntity = this.loanProductDao.findInterestType(interestType);

            RecurrenceType recurrence = RecurrenceType.fromInt(loanProductRequest.getRepaymentDetails()
                    .getFrequencyType().shortValue());
            Integer recurEvery = loanProductRequest.getRepaymentDetails().getRecurs();

            LoanInstallmentCalculation loanInstallmentCalculation = this.loanProductCaluclationTypeAssembler
                    .assembleLoanInstallmentCalculationFromDto(loanProductRequest.getRepaymentDetails()
                            .getInstallmentCalculationDetails());

            GraceType gracePeriodType = GraceType
                    .fromInt(loanProductRequest.getRepaymentDetails().getGracePeriodType());
            GracePeriodTypeEntity gracePeriodTypeEntity = this.loanProductDao.findGracePeriodType(gracePeriodType);
            Integer gracePeriodDuration = loanProductRequest.getRepaymentDetails().getGracePeriodDuration();

            List<FeeBO> applicableFees = new ArrayList<FeeBO>();
            List<Integer> applicableFeeIds = loanProductRequest.getApplicableFees();
            for (Integer feeId : applicableFeeIds) {
                FeeBO fee = ApplicationContextProvider.getBean(FeeDao.class).findById(feeId.shortValue());
                applicableFees.add(fee);
            }

            List<FundBO> applicableFunds = new ArrayList<FundBO>();
            List<Integer> applicableFundIds = loanProductRequest.getAccountDetails().getApplicableFunds();
            for (Integer fundId : applicableFundIds) {
                FundBO fund = this.fundDao.findById(fundId.shortValue());
                applicableFunds.add(fund);
            }
            
            List<PenaltyBO> applicablePenalties = new ArrayList<PenaltyBO>();
            List<Integer> applicablePenaltyIds = loanProductRequest.getApplicablePenalties();
            for (Integer penaltyId : applicablePenaltyIds) {
                PenaltyBO penalty = this.penaltyDao.findPenaltyById(penaltyId);
                applicablePenalties.add(penalty);
            }

            GLCodeEntity interestGlCode = this.generalLedgerDao.findGlCodeById(loanProductRequest.getAccountDetails()
                    .getInterestGlCodeId().shortValue());
            GLCodeEntity principalGlCode = this.generalLedgerDao.findGlCodeById(loanProductRequest.getAccountDetails()
                    .getPrincipalClCodeId().shortValue());

            String globalProductId = generateProductGlobalNum(user);

            LoanOfferingBO loanProduct = LoanOfferingBO.createNew(userId, globalProductId, name, shortName,
                    description, productCategory, startDate, endDate, applicableToEntity, currency, interestTypeEntity,
                    minRate, maxRate, defaultRate, recurrence, recurEvery, interestGlCode, principalGlCode,
                    activeStatus, inActiveStatus, gracePeriodTypeEntity, gracePeriodDuration, waiverInterest,
                    loanCycleCounter, loanAmountCalculation, loanInstallmentCalculation, applicableFees,
                    applicableFunds, applicablePenalties);
            loanProduct.updateStatus(selectedStatus);
            return loanProduct;
        } catch (PersistenceException e) {
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
