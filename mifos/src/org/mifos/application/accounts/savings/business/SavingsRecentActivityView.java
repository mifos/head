package org.mifos.application.accounts.savings.business;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

import org.mifos.framework.struts.tags.DateHelper;

public class SavingsRecentActivityView implements Serializable{
	
	private Integer accountTrxnId;
	private Date actionDate;
	private String activity;
	private String amount;
	private String runningBalance;
	private Locale locale=null;
	private String userPrefferedDate=null;
	
	public Integer getAccountTrxnId() {
		return accountTrxnId;
	}
	public void setAccountTrxnId(Integer accountTrxnId) {
		this.accountTrxnId = accountTrxnId;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public Date getActionDate() {
		return actionDate;
	}
	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}
	public String getRunningBalance() {
		return runningBalance;
	}
	public void setRunningBalance(String runningBalance) {
		this.runningBalance = runningBalance;
	}
	public Locale getLocale() {
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	public String getUserPrefferedDate() {
		return DateHelper.getUserLocaleDate(getLocale(),getActionDate().toString());
	}
	
}
