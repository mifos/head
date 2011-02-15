package org.mifos.clientportfolio.newloan.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.loan.util.helpers.EMIInstallment;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyUtils;

public class DecliningBalanceEqualPrincipalEqualInstallmentGenerator implements EqualInstallmentGenerator {

    @Override
    public List<EMIInstallment> generateEqualInstallments(LoanInterestCalculationDetails loanInterestCalculationDetails) {

        Money loanAmount = loanInterestCalculationDetails.getLoanAmount();
        Integer numberOfInstallments = loanInterestCalculationDetails.getNumberOfInstallments();
        Double interestFractionalRatePerInstallment = loanInterestCalculationDetails.getInterestFractionalRatePerInstallment();

        GraceType graceType = loanInterestCalculationDetails.getGraceType();
        Integer gracePeriodDuration = loanInterestCalculationDetails.getGracePeriodDuration();

        return allDecliningEPIInstallments_v2(loanAmount, numberOfInstallments, interestFractionalRatePerInstallment, graceType, gracePeriodDuration);
    }

    private List<EMIInstallment> allDecliningEPIInstallments_v2(Money loanAmount, Integer numberOfInstallments, Double interestFractionalRatePerInstallment, GraceType graceType, Integer gracePeriodDuration) {

        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();

        if (graceType == GraceType.NONE || graceType == GraceType.GRACEONALLREPAYMENTS) {
            emiInstallments = generateDecliningEPIInstallmentsNoGrace_v2(numberOfInstallments, loanAmount, interestFractionalRatePerInstallment);
        } else {
            emiInstallments = generateDecliningInstallmentsInterestOnly_v2(loanAmount, gracePeriodDuration, interestFractionalRatePerInstallment);
            emiInstallments.addAll(generateDecliningEPIInstallmentsNoGrace_v2(numberOfInstallments - gracePeriodDuration, loanAmount, interestFractionalRatePerInstallment));
        }
        return emiInstallments;
    }

    /**
     * Generate interest-only payments for the duration of the grace period. Interest paid is on the outstanding
     * balance, which during the grace period is the entire principal amount.
     */
    private List<EMIInstallment> generateDecliningInstallmentsInterestOnly_v2(Money loanAmount, Integer gracePeriodDuration, Double interestFractionalRatePerInstallment) {

        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        Money zero = MoneyUtils.zero(loanAmount.getCurrency());
        for (int i = 0; i < gracePeriodDuration; i++) {
            EMIInstallment installment = new EMIInstallment(loanAmount.getCurrency());
            installment.setInterest(loanAmount.multiply(interestFractionalRatePerInstallment));
            installment.setPrincipal(zero);
            emiInstallments.add(installment);
        }

        return emiInstallments;
    }


    private List<EMIInstallment> generateDecliningEPIInstallmentsNoGrace_v2(final int numInstallments, Money loanAmount, Double interestFractionalRatePerInstallment) {

        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        Money principalBalance = loanAmount;
        Money principalPerPeriod = principalBalance.divide(new BigDecimal(numInstallments));
        double interestRate = interestFractionalRatePerInstallment;

        for (int i = 0; i < numInstallments; i++) {
            EMIInstallment installment = new EMIInstallment(loanAmount.getCurrency());
            Money interestThisPeriod = principalBalance.multiply(interestRate);
            installment.setInterest(interestThisPeriod);
            installment.setPrincipal(principalPerPeriod);
            principalBalance = principalBalance.subtract(principalPerPeriod);
            emiInstallments.add(installment);
        }

        return emiInstallments;
    }
}