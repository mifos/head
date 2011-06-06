package org.mifos.clientportfolio.newloan.domain;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.loan.util.helpers.InstallmentPrincipalAndInterest;
import org.mifos.framework.util.helpers.Money;

public class VariableInstallmentPrincipalWithInterestGenerator implements PrincipalWithInterestGenerator {

    private InterestCalculationForumula formula = new DecliningBalanceWithInterestCalculatedDailyFormula();
    
    @Override
    public List<InstallmentPrincipalAndInterest> generateEqualInstallments(LoanInterestCalculationDetails loanInterestCalculationDetails) {
        
        List<InstallmentPrincipalAndInterest> principalAndInterestDetails = new ArrayList<InstallmentPrincipalAndInterest>();
        
        Double interestRate = loanInterestCalculationDetails.getInterestRate();
        Money principalOutstanding = loanInterestCalculationDetails.getLoanAmount();
        LocalDate interestPeriodStartDate = loanInterestCalculationDetails.getDisbursementDate();
        
        Money totalPrincipal = new Money(principalOutstanding.getCurrency(), Double.valueOf("0"));
        
        if (loanInterestCalculationDetails.getTotalInstallmentAmounts().isEmpty()) {

            // if empty just divide loan amount by number of installments to get principal due per installment
            Money installmentPrincipalDue = principalOutstanding.divide(loanInterestCalculationDetails.getNumberOfInstallments());
            
            int index=0;
            for (DateTime installmentDueDate : loanInterestCalculationDetails.getLoanScheduleDates()) {
                Money interestForInstallment = formula.calculate(principalOutstanding, interestRate, interestPeriodStartDate, new LocalDate(installmentDueDate));
                principalOutstanding = principalOutstanding.subtract(installmentPrincipalDue);
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
                
                Money interestForInstallment = formula.calculate(principalOutstanding, interestRate, interestPeriodStartDate, new LocalDate(installmentDueDate));
                principalOutstanding = principalOutstanding.subtract(totalInstallmentPayment);
                
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