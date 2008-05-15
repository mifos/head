package org.mifos.application.accounts.loan.business;

import org.mifos.application.productdefinition.business.AmountRange;

/**
 * this class is used for persisting the max min loan amount with account creation
 * so that it will refer to these values when editing
 */

public class MaxMinLoanAmount extends AmountRange {

	@SuppressWarnings("unused")
	// see .hbm.xml file
	private Integer accountId;
	@SuppressWarnings("unused")
	// see .hbm.xml file
	private LoanBO loan;

	public MaxMinLoanAmount() {
		this(null, null, null);
	}

	public MaxMinLoanAmount(Double maxLoanAmount, Double minLoanAmount,
			LoanBO loanBO) {
		super(minLoanAmount, maxLoanAmount);
		this.loan = loanBO;
		this.accountId = null;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public LoanBO getLoanBO() {
		return loan;
	}

	public void setLoanBO(LoanBO loanBO) {
		this.loan = loanBO;
	}
}
