/**

 * RecentAccountActivity.java    version: xxx

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */

package org.mifos.application.accounts.loan.util.valueobjects;

import java.sql.Date;
import java.util.Locale;

import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class is used to show recent account activity on loan account details page.
 * @author ashishsm
 *
 */


public class RecentAccountActivity extends ValueObject {

	/**
	 * 
	 */
	public RecentAccountActivity() {
		super();
		
	}
	
	
	/**
	 * This constructor is used to create the object through named query.
	 * @param activityDate
	 * @param description
	 * @param amount
	 * @param postedBy
	 */
	public RecentAccountActivity(java.util.Date activityDate,String description,Money amount,String postedBy){
		this.activityDate = new java.sql.Date(activityDate.getTime());
		this.description = description;
		this.amount = amount;
		this.postedBy=postedBy;
	}
	public RecentAccountActivity(String description,Double amount,String postedBy){
	}
	
	private Date activityDate;
	
	private String description;
	
	private Money amount;
	
	private String postedBy;
	
	private Locale locale=null;
	
	private String userPrefferedDate=null;
	/**
	 * @return Returns the activityDate}.
	 */
	public Date getActivityDate() {
		return activityDate;
	}

	/**
	 * @param activityDate The activityDate to set.
	 */
	public void setActivityDate(Date activityDate) {
		this.activityDate = activityDate;
	}

	/**
	 * @return Returns the amount}.
	 */
	public Money getAmount() {
		return removeSign(amount);
	}

	/**
	 * @param amount The amount to set.
	 */
	public void setAmount(Money amount) {
		this.amount = amount;
	}

	/**
	 * @return Returns the description}.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return Returns the postedBy}.
	 */
	public String getPostedBy() {
		return postedBy;
	}

	/**
	 * @param postedBy The postedBy to set.
	 */
	public void setPostedBy(String postedBy) {
		this.postedBy = postedBy;
	}


	public String getUserPrefferedDate() {
		return DateHelper.getUserLocaleDate(getLocale(),getActivityDate().toString()); 
	}



	public Locale getLocale() {
		return locale;
	}


	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	
	private Money removeSign(Money amount){
		if(amount!=null && amount.getAmountDoubleValue()<0)
			return amount.negate();
		else
			return amount;
	}
	
}
