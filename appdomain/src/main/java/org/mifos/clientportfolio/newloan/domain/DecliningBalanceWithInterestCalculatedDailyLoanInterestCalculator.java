package org.mifos.clientportfolio.newloan.domain;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.framework.util.helpers.Money;

/**
 * Implementation of {@link LoanInterestCalculator} for a variable installment loan product.
 * 
 * Interest is calculated based on a daily frequency (even if product is weekly or monthly).
 * 
 * see http://mifosforge.jira.com/wiki/display/MIFOS/Variable+Loan+Installments+and+Cash+Flow+Comparison+Functional+Spec
 */
public class DecliningBalanceWithInterestCalculatedDailyLoanInterestCalculator implements LoanInterestCalculator {

    private InterestCalculationForumula formula = new DecliningBalanceWithInterestCalculatedDailyFormula();
    
    @Override
    public Money calculate(LoanInterestCalculationDetails loanInterestCalculationDetails) {

        Double interestRate = loanInterestCalculationDetails.getInterestRate();
        Money principalOutstanding = loanInterestCalculationDetails.getLoanAmount();
        Money totalInterestOwed = new Money(principalOutstanding.getCurrency(), BigDecimal.ZERO);
        
        if (loanInterestCalculationDetails.getTotalInstallmentAmounts().isEmpty()) {

            // if empty just divide loan amount by number of installments to get principal due per installment
            Money installmentPrincipalDue = principalOutstanding.divide(loanInterestCalculationDetails.getNumberOfInstallments());
            
            LocalDate interestPeriodStartDate = loanInterestCalculationDetails.getDisbursementDate();
            for (DateTime installmentDueDate : loanInterestCalculationDetails.getLoanScheduleDates()) {
                Money interestForInstallment = formula.calculate(principalOutstanding, interestRate, interestPeriodStartDate, new LocalDate(installmentDueDate));
                totalInterestOwed = totalInterestOwed.add(interestForInstallment);
                principalOutstanding = principalOutstanding.subtract(installmentPrincipalDue);
                interestPeriodStartDate = new LocalDate(installmentDueDate);
            }
        } else {
            int index=0;
            List<Money> totalInstallmentAmounts = loanInterestCalculationDetails.getTotalInstallmentAmounts();
            LocalDate interestPeriodStartDate = loanInterestCalculationDetails.getDisbursementDate();
            for (DateTime installmentDueDate : loanInterestCalculationDetails.getLoanScheduleDates()) {
                
                Money totalInstallmentPayment = totalInstallmentAmounts.get(index);
                
                Money interestForInstallment = formula.calculate(principalOutstanding, interestRate, interestPeriodStartDate, new LocalDate(installmentDueDate));
                totalInterestOwed = totalInterestOwed.add(interestForInstallment);
                principalOutstanding = principalOutstanding.subtract(totalInstallmentPayment);
                
                interestPeriodStartDate = new LocalDate(installmentDueDate);
                
                index++;
            }
            
        }
        return totalInterestOwed;
    }
}