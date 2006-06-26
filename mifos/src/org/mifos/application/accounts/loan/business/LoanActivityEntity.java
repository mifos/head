package org.mifos.application.accounts.loan.business;

import java.sql.Timestamp;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.service.PersonnelPersistenceService;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.Money;

public class LoanActivityEntity extends PersistentObject {

	private Integer id;

	private PersonnelBO personnel;

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

	private AccountBO account;

	public LoanActivityEntity() {
		trxnCreatedDate = new Timestamp(System.currentTimeMillis());
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

	public AccountBO getAccount() {
		return account;
	}

	public void setAccount(AccountBO account) {
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

	public PersonnelBO getPersonnel() {
		return personnel;
	}

	public void setPersonnel(PersonnelBO personnel) {
		this.personnel = personnel;
	}

	public void setActivityDetails(LoanSummaryEntity loanSummary,Short personnelId,
			Money principal, Money interest, Money fees, Money penalty,String comments) {
		PersonnelBO personnel = new PersonnelPersistenceService().getPersonnel(personnelId);
		setPrincipal(principal);
		setInterest(interest);
		setPenalty(penalty);
		setFee(fees);
		setPrincipalOutstanding(loanSummary.getOriginalPrincipal().subtract(
				loanSummary.getPrincipalPaid()));
		setFeeOutstanding(loanSummary.getOriginalFees().subtract(
				loanSummary.getFeesPaid()));
		setPenaltyOutstanding(loanSummary.getOriginalPenalty().subtract(
				loanSummary.getPenaltyPaid()));
		setInterestOutstanding(loanSummary.getOriginalInterest().subtract(
				loanSummary.getInterestPaid()));
		
		setPersonnel(personnel);
		setComments(comments);
		trxnCreatedDate = new Timestamp(System.currentTimeMillis());
	}

}
