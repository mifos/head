package org.mifos.application.productdefinition.business;

import static org.mifos.framework.util.helpers.NumberUtils.isBetween;

public class LoanAmountFromLastLoanAmountBO extends LoanAmountOption {
	private Double startRange;
	private Double endRange;
	private final Short loanAmountFromLastLoanID;

	public LoanAmountFromLastLoanAmountBO(Double minLoanAmount,
			Double maxLoanAmount, Double defaultLoanAmount, Double startRange,
			Double endRange, LoanOfferingBO loanOffering) {
		super(minLoanAmount , maxLoanAmount, defaultLoanAmount, loanOffering);
		this.endRange = endRange;
		this.startRange = startRange;
		this.loanAmountFromLastLoanID = null;
	}

	public LoanAmountFromLastLoanAmountBO() {
		this(null, null, null, null, null, null);
	}

	public Double getEndRange() {
		return endRange;
	}

	public Double getStartRange() {
		return startRange;
	}

	public Short getLoanAmountFromLastLoanID() {
		return loanAmountFromLastLoanID;
	}

	public void setEndRange(Double endRange) {
		this.endRange = endRange;
	}

	public void setStartRange(Double startRange) {
		this.startRange = startRange;
	}

	boolean isLoanAmountBetweenRange(Double lastLoanAmount) {
		return isBetween(getStartRange(),
				getEndRange(), lastLoanAmount);
	}
}
