package org.mifos.application.accounts.loan.business;

import org.mifos.application.productdefinition.business.InstallmentRange;

/**
 * this class is used for saving max min no of installmment with the loan account creation
 * so that it will refer to these values when editing the account. 
 */

public class MaxMinNoOfInstall extends InstallmentRange {

	@SuppressWarnings("unused")
	// see .hbm.xml file
	private Integer accountId;
	@SuppressWarnings("unused")
	// see .hbm.xml file
	private LoanBO loan;

	public MaxMinNoOfInstall() {
		this(null, null, null);
	}

	public MaxMinNoOfInstall(Short maxLoanAmount, Short minLoanAmount,
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
