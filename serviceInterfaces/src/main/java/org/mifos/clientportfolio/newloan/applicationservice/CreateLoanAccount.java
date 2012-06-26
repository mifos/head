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

package org.mifos.clientportfolio.newloan.applicationservice;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.clientportfolio.loan.service.RecurringSchedule;
import org.mifos.dto.domain.CreateAccountFeeDto;
import org.mifos.dto.domain.CreateAccountPenaltyDto;

@SuppressWarnings("PMD")
public class CreateLoanAccount implements RecurringSchedule {

    private final Integer customerId;
    private final Integer productId;
    private final Integer accountState;
    private final BigDecimal loanAmount;
    private final Double interestRate;
    private final LocalDate disbursementDate;
    private final Short disbursalPaymentTypeId;
    private final int numberOfInstallments;
    private final int graceDuration;
    private final Integer sourceOfFundId;
    private final Integer loanPurposeId;
    private final Integer collateralTypeId;
    private final String collateralNotes;
    private final String externalId;
    private final boolean repaymentScheduleIndependentOfCustomerMeeting;
    private final RecurringSchedule recurringSchedule;
    private final List<CreateAccountFeeDto> accountFees;
    private final List<CreateAccountPenaltyDto> accountPenalties;
    private final int minAllowedNumberOfInstallments;
    private final int maxAllowedNumberOfInstallments;
    private final BigDecimal minAllowedLoanAmount;
    private final BigDecimal maxAllowedLoanAmount;
    private String predefinedAccountNumber;
    private Short flagId;

    public CreateLoanAccount(Integer customerId, Integer productId, Integer accountState, BigDecimal loanAmount,
            BigDecimal minAllowedLoanAmount, BigDecimal maxAllowedLoanAmount, Double interestRate,
            LocalDate disbursementDate, Short disbursalPaymentTypeId, int numberOfInstallments, int minAllowedNumberOfInstallments,
            int maxAllowedNumberOfInstallments, int graceDuration, Integer sourceOfFundId, Integer loanPurposeId,
            Integer collateralTypeId, String collateralNotes, String externalId,
            boolean repaymentScheduleIndependentOfCustomerMeeting, RecurringSchedule recurringSchedule,
            List<CreateAccountFeeDto> accountFees, List<CreateAccountPenaltyDto> accountPenalties) {
        this.customerId = customerId;
        this.productId = productId;
        this.accountState = accountState;
        this.loanAmount = loanAmount;
        this.minAllowedLoanAmount = minAllowedLoanAmount;
        this.maxAllowedLoanAmount = maxAllowedLoanAmount;
        this.interestRate = interestRate;
        this.disbursementDate = disbursementDate;
        this.disbursalPaymentTypeId = disbursalPaymentTypeId;
        this.numberOfInstallments = numberOfInstallments;
        this.minAllowedNumberOfInstallments = minAllowedNumberOfInstallments;
        this.maxAllowedNumberOfInstallments = maxAllowedNumberOfInstallments;
        this.graceDuration = graceDuration;
        this.sourceOfFundId = sourceOfFundId;
        this.loanPurposeId = loanPurposeId;
        this.collateralTypeId = collateralTypeId;
        this.collateralNotes = collateralNotes;
        this.externalId = externalId;
        this.repaymentScheduleIndependentOfCustomerMeeting = repaymentScheduleIndependentOfCustomerMeeting;
        this.recurringSchedule = recurringSchedule;
        this.accountFees = accountFees;
        this.accountPenalties = accountPenalties;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public Integer getProductId() {
        return productId;
    }
    
    public Integer getAccountState() {
        return accountState;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public LocalDate getDisbursementDate() {
        return disbursementDate;
    }

    public Short getDisbursalPaymentTypeId() {
        return disbursalPaymentTypeId;
    }

    public int getNumberOfInstallments() {
        return numberOfInstallments;
    }

    public int getGraceDuration() {
        return graceDuration;
    }

    public Integer getSourceOfFundId() {
        return sourceOfFundId;
    }

    public Integer getLoanPurposeId() {
        return loanPurposeId;
    }

    public Integer getCollateralTypeId() {
        return collateralTypeId;
    }

    public String getCollateralNotes() {
        return collateralNotes;
    }
    
    public String getExternalId() {
        return externalId;
    }
    
    public boolean isRepaymentScheduleIndependentOfCustomerMeeting() {
        return repaymentScheduleIndependentOfCustomerMeeting;
    }
    
    @Override
    public boolean isWeekly() {
        return this.recurringSchedule.isWeekly();
    }

    @Override
    public boolean isMonthly() {
        return this.recurringSchedule.isMonthly();
    }

    @Override
    public boolean isMonthlyOnDayOfMonth() {
        return this.recurringSchedule.isMonthlyOnDayOfMonth();
    }

    @Override
    public boolean isMonthlyOnWeekAndDayOfMonth() {
        return this.recurringSchedule.isMonthlyOnWeekAndDayOfMonth();
    }

    @Override
    public Integer getEvery() {
        return this.recurringSchedule.getEvery();
    }

    @Override
    public Integer getDay() {
        return this.recurringSchedule.getDay();
    }

    @Override
    public Integer getWeek() {
        return this.recurringSchedule.getWeek();
    }
    
    public List<CreateAccountFeeDto> getAccountFees() {
        return accountFees;
    }
    
    public List<CreateAccountPenaltyDto> getAccountPenalties() {
        return accountPenalties;
    }

    public Integer getMinAllowedNumberOfInstallments() {
        return minAllowedNumberOfInstallments;
    }

    public Integer getMaxAllowedNumberOfInstallments() {
        return maxAllowedNumberOfInstallments;
    }

    public BigDecimal getMinAllowedLoanAmount() {
        return minAllowedLoanAmount;
    }

    public BigDecimal getMaxAllowedLoanAmount() {
        return maxAllowedLoanAmount;
    }
    /**
     * Sets predefined account number used for importing loan accounts
     * @param accountNumber
     */
    public void setPredefinedAccountNumber(String accountNumber) {
        this.predefinedAccountNumber=accountNumber;
    }
    /**
     *
     *@return predefined account number used for importing loan accounts
     */
    public String getPredefinedAccountNumber(){
        return predefinedAccountNumber;
    }
    /**
     *
     *@return id of status flag used in import
     */
    public Short getFlagId() {
        return flagId;
    }
    /**
     * Sets status flag id used in import
     * @param flag
     */
    public void setFlagId(Short flag) {
        this.flagId=flag;
    }
}