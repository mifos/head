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

package org.mifos.clientportfolio.newloan.domain;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.util.helpers.InstallmentPrincipalAndInterest;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.InstallmentDate;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.config.AccountingRules;
import org.mifos.customers.business.CustomerBO;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.BusinessRuleException;

public class IndividualLoanScheduleFactory implements LoanScheduleFactory {

    @Override
    public IndividualLoanSchedule create(List<DateTime> loanScheduleDates, LoanOfferingBO loanProduct, Money loanAmount) {

        GraceType graceType = loanProduct.getGraceType();
        Integer gracePeriodDuration = Integer.valueOf(0);
        if (loanProduct.getGracePeriodDuration() != null) {
            gracePeriodDuration = loanProduct.getGracePeriodDuration().intValue();
        }
        MeetingBO loanMeeting = loanProduct.getLoanOfferingMeetingValue();
        Double interestRate = Double.valueOf("10.0");
        Integer interestDays = Integer.valueOf(AccountingRules.getNumberOfInterestDays().intValue());
        InterestType interestType = loanProduct.getInterestType();

        CustomerBO customer = null;
        Integer numberOfInstallments = loanScheduleDates.size();

        List<LoanScheduleEntity> scheduledLoanRepayments = new ArrayList<LoanScheduleEntity>();

        Integer installmentNumber = 1;
        List<InstallmentDate> dueInstallmentDates = new ArrayList<InstallmentDate>();
        for (DateTime scheduledDate : loanScheduleDates) {
            dueInstallmentDates.add(new InstallmentDate(installmentNumber.shortValue(), scheduledDate.toLocalDate()
                    .toDateMidnight().toDate()));
            installmentNumber++;
        }

        if (loanProduct.isPrinDueLastInst()) {
            // Principal due on last installment has been cut, so throw an exception if we reach this code.
            throw new BusinessRuleException(AccountConstants.NOT_SUPPORTED_EMI_GENERATION);
        }

        // loan interest calculation for various interest calculation algorithms
        LoanDecliningInterestAnnualPeriodCalculator decliningInterestAnnualPeriodCalculator = new LoanDecliningInterestAnnualPeriodCalculatorFactory().create(loanMeeting.getRecurrenceType());
        Double decliningInterestAnnualPeriod = decliningInterestAnnualPeriodCalculator.calculate(loanMeeting.getRecurAfter().intValue(), interestDays);
        Double interestFractionalRatePerInstallment = interestRate / decliningInterestAnnualPeriod / 100;

        LoanDurationInAccountingYearsCalculator loanDurationInAccountingYearsCalculator = new LoanDurationInAccountingYearsCalculatorFactory().create(loanMeeting.getRecurrenceType());

        Double durationInYears = loanDurationInAccountingYearsCalculator.calculate(loanMeeting.getRecurAfter().intValue(), numberOfInstallments, interestDays);

        LoanInterestCalculationDetails loanInterestCalculationDetails = new LoanInterestCalculationDetails(loanAmount, interestRate, graceType, gracePeriodDuration,
                numberOfInstallments, durationInYears, interestFractionalRatePerInstallment);

        LoanInterestCalculatorFactory loanInterestCalculatorFactory = new LoanInterestCalculatorFactoryImpl();
        LoanInterestCalculator loanInterestCalculator = loanInterestCalculatorFactory.create(interestType);
        Money loanInterest = loanInterestCalculator.calculate(loanInterestCalculationDetails);
        // end of loan Interest creation

        EqualInstallmentGeneratorFactory equalInstallmentGeneratorFactory = new EqualInstallmentGeneratorFactoryImpl();
        PrincipalWithInterestGenerator equalInstallmentGenerator = equalInstallmentGeneratorFactory.create(interestType, loanInterest);

        List<InstallmentPrincipalAndInterest> EMIInstallments = equalInstallmentGenerator.generateEqualInstallments(loanInterestCalculationDetails);

        // create loanSchedules
        int installmentIndex = 0;
        for (InstallmentDate installmentDate : dueInstallmentDates) {
            InstallmentPrincipalAndInterest em = EMIInstallments.get(installmentIndex);
            LoanScheduleEntity loanScheduleEntity = new LoanScheduleEntity(null, customer, installmentDate
                    .getInstallmentId(), new java.sql.Date(installmentDate.getInstallmentDueDate().getTime()),
                    PaymentStatus.UNPAID, em.getPrincipal(), em.getInterest());
            scheduledLoanRepayments.add(loanScheduleEntity);
            installmentIndex++;
        }

        List<LoanScheduleEntity> allExistingLoanSchedules = new ArrayList<LoanScheduleEntity>();
        
        LoanScheduleRounderHelper loanScheduleRounderHelper = new DefaultLoanScheduleRounderHelper();
        LoanScheduleRounder loanScheduleInstallmentRounder = new DefaultLoanScheduleRounder(loanScheduleRounderHelper);

        List<LoanScheduleEntity> roundedLoanSchedules = loanScheduleInstallmentRounder.round(graceType, gracePeriodDuration.shortValue(), loanAmount,
        		interestType, scheduledLoanRepayments, allExistingLoanSchedules);
        
        List<LoanScheduleRepaymentItem> loanScheduleItems = new ArrayList<LoanScheduleRepaymentItem>();
        for (LoanScheduleEntity loanScheduleEntity : roundedLoanSchedules) {

            LoanScheduleRepaymentItemImpl loanScheduleItem = new LoanScheduleRepaymentItemImpl(loanScheduleEntity
                    .getInstallmentId().intValue(), new LocalDate(loanScheduleEntity.getActionDate()),
                    loanScheduleEntity.getPrincipal(), loanScheduleEntity.getInterest());

            loanScheduleItems.add(loanScheduleItem);
        }

        return new IndividualLoanScheduleImpl(loanScheduleItems);
    }
}