package org.mifos.application.accounts.loan.util.valueobjects;

import java.sql.Date;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.framework.util.valueobjects.ValueObject;

public class LoanPerformanceHistory extends ValueObject {
	private Integer accountId;

	private Integer noOfPayments;

	private Integer noOfMissedPayments;

	private Integer daysInArrears;

	private Date loanMaturityDate;

	private LoanBO loan;
	
	public LoanPerformanceHistory() {
		this.noOfPayments = 0;
		this.noOfMissedPayments = 0;
		this.daysInArrears = 0;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Integer getDaysInArrears() {
		return daysInArrears;
	}

	public void setDaysInArrears(Integer daysInArrears) {
		this.daysInArrears = daysInArrears;
	}

	public Date getLoanMaturityDate() {
		return loanMaturityDate;
	}

	public void setLoanMaturityDate(Date loanMaturityDate) {
		this.loanMaturityDate = loanMaturityDate;
	}

	public Integer getNoOfMissedPayments() {
		return noOfMissedPayments;
	}

	public void setNoOfMissedPayments(Integer noOfMissedPayments) {
		this.noOfMissedPayments = noOfMissedPayments;
	}

	public Integer getNoOfPayments() {
		return noOfPayments;
	}

	public void setNoOfPayments(Integer noOfPayments) {
		this.noOfPayments = noOfPayments;
	}

	public LoanBO getLoan() {
		return loan;
	}

	public void setLoan(LoanBO loan) {
		this.loan = loan;
	}
}
