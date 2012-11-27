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

package org.mifos.clientportfolio.loan.service;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.dto.domain.CreateAccountFeeDto;

@SuppressWarnings("PMD")
public class CreateLoanSchedule implements RecurringSchedule {

    private final Integer customerId;
    private final Integer productId;
    private final BigDecimal loanAmount;
    private final Double interestRate;
    private final LocalDate disbursementDate;
    private final int numberOfInstallments;
    private final int graceDuration;
    private final boolean repaymentIndependentOfCustomerMeetingSchedule;
    private final RecurringSchedule recurringSchedule;
    private final List<CreateAccountFeeDto> accountFeeEntities;

    public CreateLoanSchedule(Integer customerId, Integer productId, BigDecimal loanAmount, Double interestRate,
            LocalDate disbursementDate, int numberOfInstallments, int graceDuration, 
            boolean repaymentIndependentOfCustomerMeetingSchedule, RecurringSchedule recurringSchedule, List<CreateAccountFeeDto> accountFeeEntities) {
        this.customerId = customerId;
        this.productId = productId;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.disbursementDate = disbursementDate;
        this.numberOfInstallments = numberOfInstallments;
        this.graceDuration = graceDuration;
        this.repaymentIndependentOfCustomerMeetingSchedule = repaymentIndependentOfCustomerMeetingSchedule;
        this.recurringSchedule = recurringSchedule;
        this.accountFeeEntities = accountFeeEntities;
    }

    public int getGraceDuration() {
        return graceDuration;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public Integer getProductId() {
        return productId;
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

    public int getNumberOfInstallments() {
        return numberOfInstallments;
    }
    
    public boolean isRepaymentIndependentOfCustomerMeetingSchedule() {
        return repaymentIndependentOfCustomerMeetingSchedule;
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
    
    public List<CreateAccountFeeDto> getAccountFeeEntities() {
        return accountFeeEntities;
    }

    @Override
    public boolean isDaily() {
        return this.recurringSchedule.isDaily();
    }
}