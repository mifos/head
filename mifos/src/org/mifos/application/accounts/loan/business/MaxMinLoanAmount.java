package org.mifos.application.accounts.loan.business;

import org.mifos.framework.business.PersistentObject;

/**
 * this class is used for persisting the max min loan amount with account creation
 * so that it will refer to these values when editing
 */

public class MaxMinLoanAmount extends PersistentObject {

	@SuppressWarnings("unused")
	// see .hbm.xml file
	private Integer accountId;
	@SuppressWarnings("unused")
	// see .hbm.xml file
	private LoanBO loan;
	private Double minLoanAmount;
	private Double maxLoanAmount;

	public MaxMinLoanAmount() {
		super();
		this.accountId = null;
		this.loan = null;
		this.maxLoanAmount = null;
		this.minLoanAmount = null;
	}

	public MaxMinLoanAmount(Double maxLoanAmount, Double minLoanAmount,
			LoanBO loanBO) {
		this.loan = loanBO;
		this.accountId = null;
		this.minLoanAmount = minLoanAmount;
		this.maxLoanAmount = maxLoanAmount;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Double getMaxLoanAmount() {
		return maxLoanAmount;
	}

	public void setMaxLoanAmount(Double maxLoanAmount) {
		this.maxLoanAmount = maxLoanAmount;
	}

	public Double getMinLoanAmount() {
		return minLoanAmount;
	}

	public void setMinLoanAmount(Double minLoanAmount) {
		this.minLoanAmount = minLoanAmount;
	}

	public LoanBO getLoanBO() {
		return loan;
	}

	public void setLoanBO(LoanBO loanBO) {
		this.loan = loanBO;
	}
}
