package org.mifos.application.accounts.loan.business;

import java.util.Date;
import java.util.Locale;

import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Money;

public class LoanActivityView {
	
	private Integer id;
	private Date actionDate;
	private String activity;
	private Money principal = new Money();
	private Money interest = new Money();
	private Money fees = new Money();
	private Money penalty = new Money();
	private Money total = new Money();
	private Money runningBalancePrinciple = new Money();
	private Money runningBalanceInterest = new Money();
	private Money runningBalanceFees = new Money();
	private Money runningBalancePenalty = new Money();
	private Locale locale=null;
	private String userPrefferedDate=null;
	private java.sql.Timestamp timeStamp;
	
	public Date getActionDate() {
		return actionDate;
	}
	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public Money getFees() {
		return fees;
	}
	public void setFees(Money fees) {
		this.fees = fees;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Money getInterest() {
		return interest;
	}
	public void setInterest(Money interest) {
		this.interest = interest;
	}
	public Locale getLocale() {
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	public Money getPenalty() {
		return penalty;
	}
	public void setPenalty(Money penalty) {
		this.penalty = penalty;
	}
	public Money getPrincipal() {
		return principal;
	}
	public void setPrincipal(Money principal) {
		this.principal = principal;
	}
	public Money getRunningBalanceFees() {
		return runningBalanceFees;
	}
	public void setRunningBalanceFees(Money runningBalanceFees) {
		this.runningBalanceFees = runningBalanceFees;
	}
	public Money getRunningBalanceInterest() {
		return runningBalanceInterest;
	}
	public void setRunningBalanceInterest(Money runningBalanceInterest) {
		this.runningBalanceInterest = runningBalanceInterest;
	}
	public Money getRunningBalancePrinciple() {
		return runningBalancePrinciple;
	}
	public void setRunningBalancePrinciple(Money runningBalancePrinciple) {
		this.runningBalancePrinciple = runningBalancePrinciple;
	}
	public java.sql.Timestamp getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(java.sql.Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}
	public Money getTotal() {
		return total;
	}
	public void setTotal(Money total) {
		this.total = total;
	}
	
	public Money getRunningBalancePenalty() {
		return runningBalancePenalty;
	}
	public void setRunningBalancePenalty(Money runningBalancePenalty) {
		this.runningBalancePenalty = runningBalancePenalty;
	}
	public String getUserPrefferedDate() {
		return DateHelper.getUserLocaleDate(getLocale(),getActionDate().toString());
	}
	

}
