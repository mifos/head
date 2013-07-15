package org.mifos.clientportfolio.newloan.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.RepaymentTotals;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.framework.util.helpers.Money;

public class FirstInstallmentRoudingDifferenceLoanScheduleRounder extends DefaultLoanScheduleRounder {

    public FirstInstallmentRoudingDifferenceLoanScheduleRounder(LoanScheduleRounderHelper loanScheduleInstallmentRounder) {
        super(loanScheduleInstallmentRounder);
    }

    @Override
    public List<LoanScheduleEntity> round(GraceType graceType, Short gracePeriodDuration, Money loanAmount,
            InterestType interestType, List<LoanScheduleEntity> unroundedLoanSchedules,
            List<LoanScheduleEntity> allExistingLoanSchedules) {

        if (!interestType.equals(InterestType.FLAT)) {
            return super.round(graceType, gracePeriodDuration, loanAmount, interestType, unroundedLoanSchedules,
                    allExistingLoanSchedules);
        }

        Collections.sort(unroundedLoanSchedules);
        List<LoanScheduleEntity> roundedLoanSchedules = new ArrayList<LoanScheduleEntity>();
        RepaymentTotals totals = loanScheduleInstallmentRounder.calculateInitialTotals_v2(unroundedLoanSchedules,
                loanAmount, allExistingLoanSchedules);

        Integer gracePeriodInstallmentsCount = countGracePeriodInstallments(unroundedLoanSchedules, graceType,
                gracePeriodDuration);

        roundGracePeriodInstallments(gracePeriodInstallmentsCount, graceType, gracePeriodDuration,
                unroundedLoanSchedules, roundedLoanSchedules, totals);

        roundNonGracePeriodInstallments(gracePeriodInstallmentsCount, unroundedLoanSchedules, roundedLoanSchedules,
                totals);

        Collections.sort(roundedLoanSchedules);

        return roundedLoanSchedules;
    }

    private int countGracePeriodInstallments(List<LoanScheduleEntity> unroundedLoanSchedules, GraceType graceType,
            Short gracePeriodDuration) {
        for (int installmentNum = 1; installmentNum <= unroundedLoanSchedules.size(); installmentNum++) {
            if (!loanScheduleInstallmentRounder.isGraceInstallment_v2(installmentNum, graceType, gracePeriodDuration)) {
                return installmentNum - 1;
            }
        }
        return unroundedLoanSchedules.size();
    }

    private void roundGracePeriodInstallments(Integer gracePeriodInstallmentsCount, GraceType graceType,
            Short gracePeriodDuration, List<LoanScheduleEntity> unroundedLoanSchedules,
            List<LoanScheduleEntity> roundedLoanSchedules, RepaymentTotals totals) {
        int installmentNum = 0;
        while (installmentNum < gracePeriodInstallmentsCount) {
            LoanScheduleEntity currentInstallment = unroundedLoanSchedules.get(installmentNum);
            LoanScheduleEntity roundedInstallment = currentInstallment;
            installmentNum++;
            roundedInstallment = loanScheduleInstallmentRounder.roundAndAdjustGraceInstallment_v2(roundedInstallment);
            loanScheduleInstallmentRounder.updateRunningTotals_v2(totals, roundedInstallment);
            roundedLoanSchedules.add(roundedInstallment);
        }
    }

    private void roundNonGracePeriodInstallments(Integer gracePeriodInstallmentsCount,
            List<LoanScheduleEntity> unroundedLoanSchedules, List<LoanScheduleEntity> roundedLoanSchedules,
            RepaymentTotals totals) {
        int installmentNum;
        for (installmentNum = unroundedLoanSchedules.size() - 1; installmentNum >= gracePeriodInstallmentsCount; installmentNum--) {
            LoanScheduleEntity currentInstallment = unroundedLoanSchedules.get(installmentNum);
            LoanScheduleEntity roundedInstallment = currentInstallment;
            if (installmentNum != gracePeriodInstallmentsCount) {
                loanScheduleInstallmentRounder.roundAndAdjustButLastNonGraceInstallment_v2(roundedInstallment);
                loanScheduleInstallmentRounder.updateRunningTotals_v2(totals, roundedInstallment);
            } else {
                loanScheduleInstallmentRounder.roundAndAdjustLastInstallment_v2(roundedInstallment, totals);
            }
            roundedLoanSchedules.add(roundedInstallment);
        }
    }
}
