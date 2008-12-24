package org.mifos.application.productdefinition.business;

public class LoanAmountFromLoanCycleBO extends LoanAmountOption {
	@SuppressWarnings("unused")
	private Short loanAmountFromLoanCycleID;
	private Short rangeIndex;

	public LoanAmountFromLoanCycleBO(Double minLoanAmount,
			Double maxLoanAmount, Double defaultLoanAmount, Short rangeIndex,
			LoanOfferingBO loanOffering) {
		super(minLoanAmount, maxLoanAmount, defaultLoanAmount, loanOffering);
		this.rangeIndex = rangeIndex;
	}

	public LoanAmountFromLoanCycleBO() {
		this(null, null, null, null, null);
	}

	public Short getRangeIndex() {
		return rangeIndex;
	}

	public boolean sameRangeIndex(Short otherRangeIndex) {
		return rangeIndex.equals(otherRangeIndex);
	}
}
