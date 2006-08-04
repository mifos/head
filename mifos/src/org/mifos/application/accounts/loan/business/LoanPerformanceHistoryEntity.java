package org.mifos.application.accounts.loan.business;

import java.sql.Date;

import org.mifos.framework.business.PersistentObject;

public class LoanPerformanceHistoryEntity extends PersistentObject {

	private final Integer id;

	private final LoanBO loan;

	private Integer noOfPayments;

	private Date loanMaturityDate;

	protected LoanPerformanceHistoryEntity() {
		super();
		this.id = null;
		this.loan = null;
	}

	public LoanPerformanceHistoryEntity(LoanBO loan) {
		this.id = null;
		this.loan = loan;
		this.noOfPayments = 0;
	}

	public Integer getDaysInArrears() {
		return loan.getDaysInArrears();
	}

	public Integer getTotalNoOfMissedPayments() {
		return loan.getMissedPaymentCount();
		
	}
	
	public Date getLoanMaturityDate() {
		return loanMaturityDate;
	}

	public void setLoanMaturityDate(Date loanMaturityDate) {
		this.loanMaturityDate = loanMaturityDate;
	}

	public Integer getNoOfPayments() {
		return noOfPayments;
	}

	public void setNoOfPayments(Integer noOfPayments) {
		this.noOfPayments = noOfPayments;
	}
}
