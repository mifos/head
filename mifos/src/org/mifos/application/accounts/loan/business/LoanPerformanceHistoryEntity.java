package org.mifos.application.accounts.loan.business;

import java.sql.Date;

import org.mifos.framework.business.PersistentObject;

public class LoanPerformanceHistoryEntity extends PersistentObject {

	private Integer accountId;

	private Integer no_of_payments;

	private Integer no_of_missed_payments;

	private Integer days_in_arrears;

	private Date loan_maturity_date;

	private LoanBO loan;

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Integer getDays_in_arrears() {
		return days_in_arrears;
	}

	public void setDays_in_arrears(Integer days_in_arrears) {
		this.days_in_arrears = days_in_arrears;
	}

	public Date getLoan_maturity_date() {
		return loan_maturity_date;
	}

	public void setLoan_maturity_date(Date loan_maturity_date) {
		this.loan_maturity_date = loan_maturity_date;
	}

	public Integer getNo_of_missed_payments() {
		return no_of_missed_payments;
	}

	public void setNo_of_missed_payments(Integer no_of_missed_payments) {
		this.no_of_missed_payments = no_of_missed_payments;
	}

	public Integer getNo_of_payments() {
		return no_of_payments;
	}

	public void setNo_of_payments(Integer no_of_payments) {
		this.no_of_payments = no_of_payments;
	}

	public LoanBO getLoan() {
		return loan;
	}

	public void setLoan(LoanBO loan) {
		this.loan = loan;
	}

	public void setPerformanceHistoryDetails(Integer no_of_payments,
			Integer no_of_missed_payments, Integer days_in_arrears,
			Date loan_maturity_date) {
		this.no_of_payments = no_of_payments;
		this.no_of_missed_payments = no_of_missed_payments;
		this.days_in_arrears = days_in_arrears;
		this.loan_maturity_date = loan_maturity_date;

	}

}
