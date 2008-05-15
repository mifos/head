package org.mifos.application.productdefinition.business;

public class LoanAmountSameForAllLoanBO extends LoanAmountOption {
	private final Short loanAmountSameForAllLoanID;

	public LoanAmountSameForAllLoanBO(Double minLoanAmount,
			Double maxLoanAmount, Double defaultLoanAmount,
			LoanOfferingBO loanOffering) {
		super(minLoanAmount, maxLoanAmount, defaultLoanAmount, loanOffering);
		this.loanAmountSameForAllLoanID = null;
	}

	public LoanAmountSameForAllLoanBO() {
		this(null, null, null, null);
	}

	public Short getLoanAmountSameForAllLoanID() {
		return loanAmountSameForAllLoanID;
	}
}
