/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import org.mifos.accounts.loan.schedule.domain.Installment;
import org.mifos.accounts.loan.schedule.domain.Schedule;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.AccountingRules;
import org.mifos.framework.util.helpers.Money;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ScheduleCalculatorAdaptor {

    public void computeExtraInterest(LoanBO loan, Date asOfDate) {
        if (loan.isDecliningPrincipalBalance()) {
            Schedule schedule = mapToSchedule(new ArrayList<LoanScheduleEntity>(loan.getLoanScheduleEntities()),
                    loan.getDisbursementDate(), getDailyInterest(loan.getInterestRate()), loan.getLoanAmount().getAmount());
            schedule.computeExtraInterest(asOfDate);
            populateExtraInterestInLoanScheduleEntities(schedule, loan.getLoanScheduleEntityMap());
        }
    }

    private double getDailyInterest(Double annualInterest) {
        return annualInterest / (AccountingRules.getNumberOfInterestDays() * 100d);
    }

    Schedule mapToSchedule(List<LoanScheduleEntity> loanScheduleEntities, Date disbursementDate, Double dailyInterestRate, BigDecimal loanAmount) {
        return new Schedule(disbursementDate, dailyInterestRate, loanAmount, mapToInstallments(loanScheduleEntities));
    }

    private List<Installment> mapToInstallments(List<LoanScheduleEntity> loanScheduleEntities) {
        List<Installment> installments = new ArrayList<Installment>();
        for (LoanScheduleEntity loanScheduleEntity : loanScheduleEntities) {
            installments.add(mapToInstallment(loanScheduleEntity));
        }
        return installments;
    }

    private Installment mapToInstallment(LoanScheduleEntity loanScheduleEntity) {
        return new Installment(loanScheduleEntity.getInstallmentId().intValue(),
                loanScheduleEntity.getActionDate(), loanScheduleEntity.getPrincipal().getAmount(),
                loanScheduleEntity.getInterest().getAmount(), loanScheduleEntity.getTotalFees().getAmount());
    }

    void populateExtraInterestInLoanScheduleEntities(Schedule schedule, Map<Integer, LoanScheduleEntity> loanScheduleEntities) {
        for (Installment installment : schedule.getInstallments().values()) {
            LoanScheduleEntity loanScheduleEntity = loanScheduleEntities.get(installment.getId());
            MifosCurrency mifosCurrency = loanScheduleEntity.getPrincipal().getCurrency();
            loanScheduleEntity.setExtraInterest(new Money(mifosCurrency, installment.getExtraInterest()));
        }
    }
}
