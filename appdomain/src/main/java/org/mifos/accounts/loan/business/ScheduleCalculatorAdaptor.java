/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */
package org.mifos.accounts.loan.business;

import static org.mifos.accounts.loan.util.helpers.LoanConstants.RECALCULATE_INTEREST;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.math.BigDecimal;

import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.loan.schedule.calculation.ScheduleCalculator;
import org.mifos.accounts.loan.schedule.domain.Schedule;
import org.mifos.config.AccountingRules;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.util.helpers.Money;
import org.springframework.beans.factory.annotation.Autowired;

public class ScheduleCalculatorAdaptor {

    private ScheduleCalculator scheduleCalculator;
    private ScheduleMapper scheduleMapper;
    private ConfigurationPersistence configurationPersistence;

    @Autowired
    public ScheduleCalculatorAdaptor(ScheduleCalculator scheduleCalculator, ScheduleMapper scheduleMapper, ConfigurationPersistence configurationPersistence) {
        this.scheduleCalculator = scheduleCalculator;
        this.scheduleMapper = scheduleMapper;
        this.configurationPersistence = configurationPersistence;
    }

    public void applyPayment(LoanBO loanBO, Money amount, Date paymentDate, PersonnelBO personnel, AccountPaymentEntity accountPaymentEntity) {
        Schedule schedule = scheduleMapper.mapToSchedule(loanBO.getLoanScheduleEntities(), loanBO.getDisbursementDate(),
                getDailyInterest(loanBO.getInterestRate()), loanBO.getLoanAmount().getAmount());
        scheduleCalculator.applyPayment(schedule, amount.getAmount(), paymentDate);
        scheduleMapper.populatePaymentDetails(schedule, loanBO, paymentDate, personnel, accountPaymentEntity);
    }

    public void computeExtraInterest(LoanBO loan, Date asOfDate) {
    	int recalculateInterest = configurationPersistence.getConfigurationValueInteger(RECALCULATE_INTEREST);
       	if(recalculateInterest==1 && loan.isDecliningBalanceEqualPrincipleCalculation()){
       		Schedule schedule = scheduleMapper.mapToSchedule(new ArrayList<LoanScheduleEntity>(loan.getLoanScheduleEntities()),
            loan.getDisbursementDate(), getDailyInterest(loan.getInterestRate()), loan.getLoanAmount().getAmount());
            scheduleCalculator.computeExtraInterest(schedule, asOfDate);
            populateExtraInterestInLoanScheduleEntities(schedule, loan.getLoanScheduleEntityMap());
       	} else if (loan.isDecliningBalanceInterestRecalculation()) {
            Schedule schedule = scheduleMapper.mapToSchedule(new ArrayList<LoanScheduleEntity>(loan.getLoanScheduleEntities()),
                    loan.getDisbursementDate(), getDailyInterest(loan.getInterestRate()), loan.getLoanAmount().getAmount());
            scheduleCalculator.computeExtraInterest(schedule, asOfDate);
            populateExtraInterestInLoanScheduleEntities(schedule, loan.getLoanScheduleEntityMap());
        }
    }
    
    public BigDecimal getExtraInterest(LoanBO loan, Date transactionDate) {
        Schedule schedule = scheduleMapper.mapToSchedule(new ArrayList<LoanScheduleEntity>(loan.getLoanScheduleEntities()),
                loan.getDisbursementDate(), getDailyInterest(loan.getInterestRate()), loan.getLoanAmount().getAmount());
        return scheduleCalculator.getExtraInterest(schedule, transactionDate);
    }

    private double getDailyInterest(Double annualInterest) {
        return annualInterest / (AccountingRules.getNumberOfInterestDays() * 100d);
    }

    void populateExtraInterestInLoanScheduleEntities(Schedule schedule, Map<Integer, LoanScheduleEntity> loanScheduleEntities) {
        scheduleMapper.populateExtraInterestInLoanScheduleEntities(schedule, loanScheduleEntities);
    }

    public RepaymentResultsHolder computeRepaymentAmount(LoanBO loanBO, Date asOfDate) {
        Schedule schedule = scheduleMapper.mapToSchedule(loanBO.getLoanScheduleEntities(), loanBO.getDisbursementDate(),
                        getDailyInterest(loanBO.getInterestRate()), loanBO.getLoanAmount().getAmount());
        return scheduleCalculator.computeRepaymentAmount(schedule, asOfDate);
    }
}
