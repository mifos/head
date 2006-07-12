package org.mifos.application.accounts.loan.business;

import java.sql.Date;

import org.mifos.framework.business.PersistentObject;

public class LoanPerformanceHistoryEntity extends PersistentObject {

	private Integer id;

	private Integer noOfPayments;

	private Integer noOfMissedPayments;

	private Integer daysInArrears;

	private Date loanMaturityDate;

	private LoanBO loan;
	
	public LoanPerformanceHistoryEntity() {
		this.noOfPayments = 0;
		this.noOfMissedPayments = 0;
		this.daysInArrears = 0;
	}

	

	public Integer getId() {
		return id;
	}



	public void setId(Integer id) {
		this.id = id;
	}



	public Integer getDaysInArrears() {
		return getLoan().getDaysInArrears();
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
		return getLoan().getMissedPaymentCount();
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
