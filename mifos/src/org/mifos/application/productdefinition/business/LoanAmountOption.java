package org.mifos.application.productdefinition.business;


public abstract class LoanAmountOption extends AmountRange {
	private Double defaultLoanAmount;
	private final LoanOfferingBO loanOffering;

	public LoanAmountOption(Double minLoanAmount, Double maxLoanAmount,
			Double defaultLoanAmount, LoanOfferingBO loanOffering) {
		super(minLoanAmount, maxLoanAmount);
		this.defaultLoanAmount = defaultLoanAmount;
		this.loanOffering = loanOffering;
	}

	public Double getDefaultLoanAmount() {
		return defaultLoanAmount;
	}

	public void setDefaultLoanAmount(Double defaultLoanAmount) {
		this.defaultLoanAmount = defaultLoanAmount;
	}

	public LoanOfferingBO getLoanOffering() {
		return loanOffering;
	}
}
