package org.mifos.application.accounts.loan.business;

import org.mifos.framework.business.PersistentObject;

/**
 * this class is used for saving max min no of installmment with the loan account creation
 * so that it will refer to these values when editing the account. 
 */

public class MaxMinNoOfInstall extends PersistentObject {

	@SuppressWarnings("unused")
	// see .hbm.xml file
	private Integer accountId;
	@SuppressWarnings("unused")
	// see .hbm.xml file
	private LoanBO loan;
	private Short minNoOfInstall;
	private Short maxNoOfInstallt;

	public MaxMinNoOfInstall() {
		super();
		this.accountId = null;
		this.loan = null;
		this.maxNoOfInstallt = null;
		this.minNoOfInstall = null;
	}

	public MaxMinNoOfInstall(Short maxLoanAmount, Short minLoanAmount,
			LoanBO loanBO) {
		this.loan = loanBO;
		this.accountId = null;
		this.minNoOfInstall = minLoanAmount;
		this.maxNoOfInstallt = maxLoanAmount;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}


	public Short getMaxNoOfInstallt() {
		return maxNoOfInstallt;
	}

	public void setMaxNoOfInstallt(Short maxNoOfInstallt) {
		this.maxNoOfInstallt = maxNoOfInstallt;
	}

	public Short getMinNoOfInstall() {
		return minNoOfInstall;
	}

	public void setMinNoOfInstall(Short minNoOfInstall) {
		this.minNoOfInstall = minNoOfInstall;
	}

	public LoanBO getLoanBO() {
		return loan;
	}

	public void setLoanBO(LoanBO loanBO) {
		this.loan = loanBO;
	}
}
