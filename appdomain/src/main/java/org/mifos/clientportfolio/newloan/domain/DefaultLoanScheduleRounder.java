package org.mifos.clientportfolio.newloan.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.RepaymentTotals;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.framework.util.helpers.Money;

import edu.emory.mathcs.backport.java.util.Collections;

public class DefaultLoanScheduleRounder implements LoanScheduleRounder {

	private final LoanScheduleRounderHelper loanScheduleInstallmentRounder;

	public DefaultLoanScheduleRounder(LoanScheduleRounderHelper loanScheduleInstallmentRounder) {
		this.loanScheduleInstallmentRounder = loanScheduleInstallmentRounder;
	}
	
	@Override
	public List<LoanScheduleEntity> round(GraceType graceType, Short gracePeriodDuration, Money loanAmount,
			InterestType interestType,
			List<LoanScheduleEntity> unroundedLoanSchedules,
			List<LoanScheduleEntity> allExistingLoanSchedules) {
		
		Collections.sort(unroundedLoanSchedules);
        List<LoanScheduleEntity> roundedLoanSchedules = new ArrayList<LoanScheduleEntity>();
        RepaymentTotals totals = loanScheduleInstallmentRounder.calculateInitialTotals_v2(unroundedLoanSchedules, loanAmount, allExistingLoanSchedules);
        int installmentNum = 0;
        for (Iterator<LoanScheduleEntity> it = unroundedLoanSchedules.iterator(); it.hasNext();) {
            LoanScheduleEntity currentInstallment = it.next();
            LoanScheduleEntity roundedInstallment = currentInstallment;
            installmentNum++;
            if (it.hasNext()) { // handle all but the last installment
                if (loanScheduleInstallmentRounder.isGraceInstallment_v2(installmentNum, graceType, gracePeriodDuration)) {
                    roundedInstallment = loanScheduleInstallmentRounder.roundAndAdjustGraceInstallment_v2(roundedInstallment);
                } else if (interestType.equals(InterestType.DECLINING_EPI)) {
                	loanScheduleInstallmentRounder.roundAndAdjustNonGraceInstallmentForDecliningEPI_v2(roundedInstallment);
                } else {
                	loanScheduleInstallmentRounder.roundAndAdjustButLastNonGraceInstallment_v2(roundedInstallment);
                }
                loanScheduleInstallmentRounder.updateRunningTotals_v2(totals, roundedInstallment);
            } else {
            	loanScheduleInstallmentRounder.roundAndAdjustLastInstallment_v2(roundedInstallment, totals);
            }
            roundedLoanSchedules.add(roundedInstallment);
        } // for
		return roundedLoanSchedules;
	}
}