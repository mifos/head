package org.mifos.clientportfolio.newloan.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.framework.util.helpers.Money;

public class VariableInstallmentLoanScheduleRounder implements LoanScheduleRounder {

    @Override
    public List<LoanScheduleEntity> round(GraceType graceType, Short gracePeriodDuration, Money loanAmount,
            InterestType interestType, List<LoanScheduleEntity> unroundedLoanSchedules,
            List<LoanScheduleEntity> allExistingLoanSchedules) {
        
        // for variable installments - want to round all installments total amount values except for the last installment.
        Money totalDue = new Money(loanAmount.getCurrency(), Double.valueOf("0"));
        for (LoanScheduleEntity loanScheduleEntity : unroundedLoanSchedules) {
            totalDue = totalDue.add(loanScheduleEntity.getPrincipalDue()).add(loanScheduleEntity.getInterestDue()).add(loanScheduleEntity.getTotalFeesDue());
        }

        BigDecimal installmentAmount = totalDue.getAmount().divide(BigDecimal.valueOf(Integer.valueOf(unroundedLoanSchedules.size())), RoundingMode.HALF_UP);
        
        long roundedValue = Math.round(installmentAmount.doubleValue());
        
        Money totalInstallmentMonetaryAmount = new Money(loanAmount.getCurrency(), BigDecimal.valueOf(roundedValue));
        Money totalPrincipalToDate = new Money(loanAmount.getCurrency(), BigDecimal.ZERO);
        int index = 0;
        for (LoanScheduleEntity loanScheduleEntity : unroundedLoanSchedules) {
            if (index == unroundedLoanSchedules.size()-1) {
                // last installment
                loanScheduleEntity.setPrincipal(loanAmount.subtract(totalPrincipalToDate));
            } else {
                Money principal = totalInstallmentMonetaryAmount.subtract(loanScheduleEntity.getInterest());
                loanScheduleEntity.setPrincipal(principal);
                totalPrincipalToDate = totalPrincipalToDate.add(principal);
            }
            
            index++;
        }
        
        return unroundedLoanSchedules;
    }

}
