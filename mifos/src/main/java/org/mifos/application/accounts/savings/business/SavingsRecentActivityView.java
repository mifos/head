/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.application.accounts.savings.business;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

import org.mifos.framework.util.helpers.DateUtils;

public class SavingsRecentActivityView implements Serializable{
	
	private Integer accountTrxnId;
	private Date actionDate;
	private String activity;
	private String amount;
	private String runningBalance;
	private Locale locale=null;
	
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
		return DateUtils.getUserLocaleDate(getLocale(), getActionDate().toString());
	}
	
}
