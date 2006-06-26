package org.mifos.application.accounts.loan.util.valueobjects;

import java.sql.Timestamp;

import org.mifos.application.accounts.util.valueobjects.Account;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.ValueObject;

public class LoanActivity extends ValueObject {

	private Integer id;

	private Short personnelId;
	
	private String comments;
	
	private Money principal;

	private Money principalOutstanding;

	private Money interest;

	private Money interestOutstanding;

	private Money fee;

	private Money feeOutstanding;

	private Money penalty;

	private Money penaltyOutstanding;
	
    private Timestamp trxnCreatedDate;	
	
	private Account account;
	
	public LoanActivity(){
		trxnCreatedDate=new Timestamp(System.currentTimeMillis());
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Money getFee() {
		return fee;
	}

	public void setFee(Money fee) {
		this.fee = fee;
	}

	public Money getFeeOutstanding() {
		return feeOutstanding;
	}

	public void setFeeOutstanding(Money feeOutstanding) {
		this.feeOutstanding = feeOutstanding;
	}

	public Money getInterestOutstanding() {
		return interestOutstanding;
	}

	public void setInterestOutstanding(Money interestOutstanding) {
		this.interestOutstanding = interestOutstanding;
	}

	public Money getPenalty() {
		return penalty;
	}

	public void setPenalty(Money penalty) {
		this.penalty = penalty;
	}

	public Money getPenaltyOutstanding() {
		return penaltyOutstanding;
	}

	public void setPenaltyOutstanding(Money penaltyOutstanding) {
		this.penaltyOutstanding = penaltyOutstanding;
	}

	public Money getPrincipalOutstanding() {
		return principalOutstanding;
	}

	public void setPrincipalOutstanding(Money principalOutstanding) {
		this.principalOutstanding = principalOutstanding;
	}

	public Timestamp getTrxnCreatedDate() {
		return trxnCreatedDate;
	}

	public void setTrxnCreatedDate(Timestamp trxnCreatedDate) {
		this.trxnCreatedDate = trxnCreatedDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Short getPersonnelId() {
		return personnelId;
	}

	public void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}

	public Money getInterest() {
		return interest;
	}

	public void setInterest(Money interest) {
		this.interest = interest;
	}

	public Money getPrincipal() {
		return principal;
	}

	public void setPrincipal(Money principal) {
		this.principal = principal;
	}

}
