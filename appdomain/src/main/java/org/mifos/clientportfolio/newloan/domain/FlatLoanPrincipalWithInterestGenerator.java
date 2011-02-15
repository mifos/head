package org.mifos.clientportfolio.newloan.domain;

import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.loan.util.helpers.InstallmentPrincipalAndInterest;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyUtils;

public class FlatLoanPrincipalWithInterestGenerator implements PrincipalWithInterestGenerator {

    private final Money loanInterest;

    public FlatLoanPrincipalWithInterestGenerator(Money loanInterest) {
        this.loanInterest = loanInterest;
    }

    @Override
    public List<InstallmentPrincipalAndInterest> generateEqualInstallments(LoanInterestCalculationDetails loanInterestCalculationDetails) {

        GraceType graceType = loanInterestCalculationDetails.getGraceType();
        Integer gracePeriodDuration = loanInterestCalculationDetails.getGracePeriodDuration();
        Money loanAmount = loanInterestCalculationDetails.getLoanAmount();
        Integer numberOfInstallments = loanInterestCalculationDetails.getNumberOfInstallments();

        return allFlatInstallments_v2(loanInterest, graceType, loanAmount, numberOfInstallments, gracePeriodDuration);
    }

    /**
     * Generate flat-interest installment variants based on the type of grace period.
     * <ul>
     * <li>If grace period is none, or applies to both principal and interest, the loan calculations are the same.
     * <li>If grace period is for principal only, don't add new installments. The first grace installments are
     * interest-only, and principal is paid off with the remaining installments. NOTE: Principal-only grace period
     * should be disable for release 1.1.
     * </ul>
     */
    private List<InstallmentPrincipalAndInterest> allFlatInstallments_v2(final Money loanInterest, GraceType graceType, Money loanAmount, Integer numberOfInstallments, Integer gracePeriodDuration) {

        List<InstallmentPrincipalAndInterest> emiInstallments = new ArrayList<InstallmentPrincipalAndInterest>();

        Money principalPerInstallment = loanAmount.divide(numberOfInstallments);
        Money interestPerInstallment = loanInterest.divide(numberOfInstallments);

        if (graceType == GraceType.NONE || graceType == GraceType.GRACEONALLREPAYMENTS) {
            emiInstallments = generateFlatInstallmentsNoGrace_v2(principalPerInstallment, interestPerInstallment, numberOfInstallments);
        } else {
            // getGraceType() == GraceType.PRINCIPALONLYGRACE which is disabled.
            emiInstallments = generateFlatInstallmentsInterestOnly_v2(loanInterest, numberOfInstallments, gracePeriodDuration);
            emiInstallments.addAll(generateFlatInstallmentsAfterInterestOnlyGraceInstallments_v2(loanInterest, loanAmount, numberOfInstallments, gracePeriodDuration));
        }
        return emiInstallments;
    }

    /**
     * Calculate the installments after grace period, in the case of principal-only grace type for a flat-interest loan.
     * Divide interest evenly among all installments, but divide principle evenly among installments after the grace
     * period.
     */
    private List<InstallmentPrincipalAndInterest> generateFlatInstallmentsAfterInterestOnlyGraceInstallments_v2(final Money loanInterest, Money loanAmount, Integer numberOfInstallments, Integer gracePeriodDuration) {
        List<InstallmentPrincipalAndInterest> emiInstallments = new ArrayList<InstallmentPrincipalAndInterest>();
        Money principalPerInstallment = loanAmount.divide(numberOfInstallments - gracePeriodDuration);
        Money interestPerInstallment = loanInterest.divide(numberOfInstallments);
        for (int i = gracePeriodDuration; i < numberOfInstallments; i++) {
            InstallmentPrincipalAndInterest installment = new InstallmentPrincipalAndInterest(principalPerInstallment, interestPerInstallment);
            emiInstallments.add(installment);
        }
        return emiInstallments;
    }

    /**
     * Generate interest-only payments for the duration of the grace period. Interest is divided evenly among all
     * installments, but only interest is paid during the grace period.
     */
    private List<InstallmentPrincipalAndInterest> generateFlatInstallmentsInterestOnly_v2(final Money loanInterest, Integer numberOfInstallments, Integer gracePeriodDuration) {

        List<InstallmentPrincipalAndInterest> emiInstallments = new ArrayList<InstallmentPrincipalAndInterest>();
        Money zeroPrincipal = MoneyUtils.zero(loanInterest.getCurrency());
        Money interestPerInstallment = loanInterest.divide(numberOfInstallments);

        for (int i = 0; i < gracePeriodDuration; i++) {
            InstallmentPrincipalAndInterest installment = new InstallmentPrincipalAndInterest(zeroPrincipal, interestPerInstallment);
            emiInstallments.add(installment);
        }

        return emiInstallments;
    }

    /**
     * Divide principal and interest evenly among all installments, no grace period
     */
    private List<InstallmentPrincipalAndInterest> generateFlatInstallmentsNoGrace_v2(Money principalPerInstallment, Money interestPerInstallment, Integer numberOfInstallments) {
        List<InstallmentPrincipalAndInterest> emiInstallments = new ArrayList<InstallmentPrincipalAndInterest>();
        for (int i = 0; i < numberOfInstallments; i++) {
            InstallmentPrincipalAndInterest installment = new InstallmentPrincipalAndInterest(principalPerInstallment, interestPerInstallment);
            emiInstallments.add(installment);
        }
        return emiInstallments;
    }
}