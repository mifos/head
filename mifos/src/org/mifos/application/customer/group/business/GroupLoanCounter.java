package org.mifos.application.customer.group.business;

import static org.mifos.framework.util.helpers.NumberUtils.SHORT_ZERO;

import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.util.helpers.Transformer;

public class GroupLoanCounter {
	public static Transformer<GroupLoanCounter, Short> TRANSFORM_GROUP_LOAN_COUNTER_TO_LOAN_CYCLE = new Transformer<GroupLoanCounter, Short>() {
		public Short transform(GroupLoanCounter input) {
			return input.getLoanCycleCounter();
		}
	};

	@SuppressWarnings("unused")
	private Integer loanCounterId;

	@SuppressWarnings("unused")
	private GroupPerformanceHistoryEntity groupPeformanceHistory;

	private Short loanCycleCounter = SHORT_ZERO;

	private LoanOfferingBO loanOffering;

	public GroupLoanCounter(
			GroupPerformanceHistoryEntity groupPerformanceHistory,
			LoanOfferingBO loanOffering, YesNoFlag yesNoFlag) {
		this.groupPeformanceHistory = groupPerformanceHistory;
		this.loanOffering = loanOffering;
		updateLoanCounter(yesNoFlag);
	}

	protected GroupLoanCounter() {
	}

	public LoanOfferingBO getLoanOffering() {
		return loanOffering;
	}

	public void updateLoanCounter(YesNoFlag yesNoFlag) {
		if (yesNoFlag.yes())
			loanCycleCounter++;
		else loanCycleCounter--;
	}

	public boolean isOfSameProduct(PrdOfferingBO prdOffering) {
		return loanOffering.isOfSameOffering(prdOffering);
	}

	public Short getLoanCycleCounter() {
		return loanCycleCounter;
	}
}
