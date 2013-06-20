package org.mifos.clientportfolio.newloan.domain;

import java.util.List;

import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.RepaymentTotals;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.framework.util.helpers.Money;

public interface LoanScheduleRounderHelper {

	RepaymentTotals calculateInitialTotals_v2(
			List<LoanScheduleEntity> unroundedLoanSchedules, Money loanAmount,
			List<LoanScheduleEntity> allExistingLoanSchedules);

	boolean isGraceInstallment_v2(int installmentNum, GraceType graceType, Short gracePeriodDuration);

	LoanScheduleEntity roundAndAdjustGraceInstallment_v2(LoanScheduleEntity roundedInstallment);

	void roundAndAdjustNonGraceInstallmentForDecliningEPI_v2(LoanScheduleEntity roundedInstallment);
	
	void roundAndAdjustButLastNonGraceInstallment_v2(LoanScheduleEntity roundedInstallment);
	
	void roundAndAdjustButLastNonGraceInstallment_v2(LoanScheduleEntity roundedInstallment, RepaymentTotals totals);

	void updateRunningTotals_v2(RepaymentTotals totals, LoanScheduleEntity roundedInstallment);

	void roundAndAdjustLastInstallment_v2(LoanScheduleEntity roundedInstallment, RepaymentTotals totals);
}
