package org.mifos.application.accounts.loan.util.valueobjects;

import java.sql.Date;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.framework.util.valueobjects.ValueObject;

public class LoanPerformanceHistory extends ValueObject {
	private Integer id;

	private Integer noOfPayments;

	private Integer noOfMissedPayments;

	private Integer daysInArrears;

	private Date loanMaturityDate;

	private Loan loan;
	
	public LoanPerformanceHistory() {
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

	public Loan getLoan() {
		return loan;
	}

	public void setLoan(Loan loan) {
		this.loan = loan;
	}
}
