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

package org.mifos.clientportfolio.newloan.domain;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.loan.util.helpers.InstallmentPrincipalAndInterest;
import org.mifos.framework.util.helpers.Money;

public class FlatVariableInstallmentPrincipalWithInterestGenerator implements PrincipalWithInterestGenerator {

    private InterestCalculationForumula formula = new DecliningBalanceWithInterestCalculatedDailyFormula();
    
    @Override
    public List<InstallmentPrincipalAndInterest> generateEqualInstallments(LoanInterestCalculationDetails loanInterestCalculationDetails) {
        
        List<InstallmentPrincipalAndInterest> principalAndInterestDetails = new ArrayList<InstallmentPrincipalAndInterest>();
        
        Double interestRate = loanInterestCalculationDetails.getInterestRate();
        Money loanAmount = loanInterestCalculationDetails.getLoanAmount();
        LocalDate interestPeriodStartDate = loanInterestCalculationDetails.getDisbursementDate();
        
        Money totalPrincipal = new Money(loanAmount.getCurrency(), Double.valueOf("0"));
        
        if (loanInterestCalculationDetails.getTotalInstallmentAmounts().isEmpty()) {

            // if empty just divide loan amount by number of installments to get principal due per installment
            Money installmentPrincipalDue = loanAmount.divide(loanInterestCalculationDetails.getNumberOfInstallments());
            
            int index=0;
            for (DateTime installmentDueDate : loanInterestCalculationDetails.getLoanScheduleDates()) {
                Money interestForInstallment = formula.calculate(loanAmount, interestRate, interestPeriodStartDate, new LocalDate(installmentDueDate));
                interestPeriodStartDate = new LocalDate(installmentDueDate);
                totalPrincipal = totalPrincipal.add(installmentPrincipalDue);
                
                if (index == loanInterestCalculationDetails.getLoanScheduleDates().size()-1) {
                    // last installment
                    Money realTotalPrincipal = new Money(totalPrincipal.getCurrency(), totalPrincipal.toString());
                    if (realTotalPrincipal.isLessThan(loanInterestCalculationDetails.getLoanAmount())) {
                        Money difference = loanInterestCalculationDetails.getLoanAmount().subtract(realTotalPrincipal);
                        installmentPrincipalDue = installmentPrincipalDue.add(difference);
                    } else if (realTotalPrincipal.isGreaterThan(loanInterestCalculationDetails.getLoanAmount())) {
                        Money difference = realTotalPrincipal.subtract(loanInterestCalculationDetails.getLoanAmount());
                        installmentPrincipalDue = installmentPrincipalDue.subtract(difference);
                    }
                }
                
                principalAndInterestDetails.add(new InstallmentPrincipalAndInterest(installmentPrincipalDue, interestForInstallment));
                index++;
            }
        } else {
            int index=0;
            List<Money> totalInstallmentAmounts = loanInterestCalculationDetails.getTotalInstallmentAmounts();
            for (DateTime installmentDueDate : loanInterestCalculationDetails.getLoanScheduleDates()) {
                
                Money totalInstallmentPayment = totalInstallmentAmounts.get(index);
                Money interestForInstallment = formula.calculate(loanAmount, interestRate, interestPeriodStartDate, new LocalDate(installmentDueDate));
                interestPeriodStartDate = new LocalDate(installmentDueDate);
                Money installmentPrincipalDue = totalInstallmentPayment.subtract(interestForInstallment);
                totalPrincipal = totalPrincipal.add(installmentPrincipalDue);
                
                if (index == loanInterestCalculationDetails.getLoanScheduleDates().size()-1) {
                    // last installment
                    Money realTotalPrincipal = new Money(totalPrincipal.getCurrency(), totalPrincipal.toString());
                    if (realTotalPrincipal.isLessThan(loanInterestCalculationDetails.getLoanAmount())) {
                        Money difference = loanInterestCalculationDetails.getLoanAmount().subtract(realTotalPrincipal);
                        installmentPrincipalDue = installmentPrincipalDue.add(difference);
                    } else if (realTotalPrincipal.isGreaterThan(loanInterestCalculationDetails.getLoanAmount())) {
                        Money difference = realTotalPrincipal.subtract(loanInterestCalculationDetails.getLoanAmount());
                        installmentPrincipalDue = installmentPrincipalDue.subtract(difference);
                    }
                }
                
                principalAndInterestDetails.add(new InstallmentPrincipalAndInterest(installmentPrincipalDue, interestForInstallment));
                
                index++;
            }
            
        }
        return principalAndInterestDetails;
    }
}