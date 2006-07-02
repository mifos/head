/**

 * Account.java    version: xxx

 

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

package org.mifos.application.accounts.util.valueobjects;

import java.sql.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class acts as base class for all types of accounts.
 * @author ashishsm
 *
 */
public class Account extends ValueObject{
	
/**
	 * 
	 */
	public Account() {
		super();
		accountFeesSet = new HashSet<AccountFees>();
		
	
	}
	
	private Set<AccountActionDate> accountActionDateSet ;
	
	/** The  primary key value. */
	private Integer accountId;
	
	/** The value of the customer association. */
	private Customer customer;
	private Integer versionNo;
	
	/** The value of the accountFeesSet one-to-many association. */
	private Set<AccountFees> accountFeesSet;
	
	
    /** The value of the accountState association. */
    private Short accountStateId;

    /** The value of the accountType association. */
    private Short accountTypeId;

    /** The value of the simple globalAccountNum property. */
    private java.lang.String globalAccountNum;

    /** The value of the simple officeId property. */
    private Short officeId;

    /** The value of the simple personnelId property. */
    private Short personnelId;
	
	private Short createdBy;
	
	private Date createdDate;
	
	private Short updatedBy;
	
	private Date updatedDate;
	
	private Date closedDate;
	
	private Set<AccountActivity> accountActivitySet=null;
	
	
	
	/**
	 * This is not used to insert or update stuff in the database, neither would this be initialized when the account object is created.
	 * This is just used to display stuff on the UI.
	 */
	private AccountFlagDetail accountFlagDetail;
	
	/**
	 * @return Returns the accountId}.
	 */
	public Integer getAccountId() {
		return accountId;
	}
	/**
	 * @param accountId The accountId to set.
	 */
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	/**
	 * @return Returns the customer}.
	 */
	public Customer getCustomer() {
		return customer;
	}
	/**
	 * @param customer The customer to set.
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	/**
	 * @return Returns the accountFeesSet}.
	 */
	public Set getAccountFeesSet() {
		return accountFeesSet;
	}
	
	
	/**
	 * Adds the fee to the set
	 * @param fees
	 */
	public void addFees(AccountFees accountFees){
		this.accountFeesSet.add(accountFees);
		
	}


	/**
	 * @return Returns the versionNo}.
	 */
	public Integer getVersionNo() {
		return versionNo;
	}


	/**
	 * @param versionNo The versionNo to set.
	 */
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}


	/**
	 * @param accountFeesSet The accountFeesSet to set.
	 */
	public void setAccountFeesSet(Set<AccountFees> accountFeesSet) {
		this.accountFeesSet = accountFeesSet;
	}

	
	


	/**
	 * @return Returns the globalAccountNum}.
	 */
	public java.lang.String getGlobalAccountNum() {
		return globalAccountNum;
	}


	/**
	 * @param globalAccountNum The globalAccountNum to set.
	 */
	public void setGlobalAccountNum(java.lang.String globalAccountNum) {
		this.globalAccountNum = globalAccountNum;
	}


	/**
	 * @return Returns the accountStateId}.
	 */
	public Short getAccountStateId() {
		return accountStateId;
	}


	/**
	 * @param accountStateId The accountStateId to set.
	 */
	public void setAccountStateId(Short accountStateId) {
		this.accountStateId = accountStateId;
	}


	/**
	 * @return Returns the accountTypeId}.
	 */
	public Short getAccountTypeId() {
		return accountTypeId;
	}


	/**
	 * @param accountTypeId The accountTypeId to set.
	 */
	public void setAccountTypeId(Short accountTypeId) {
		this.accountTypeId = accountTypeId;
	}


	/**
	 * @return Returns the personnelId}.
	 */
	public Short getPersonnelId() {
		return personnelId;
	}


	/**
	 * @param personnelId The personnelId to set.
	 */
	public void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}


	/**
	 * @return Returns the officeId}.
	 */
	public Short getOfficeId() {
		return officeId;
	}


	/**
	 * @param officeId The officeId to set.
	 */
	public void setOfficeId(Short officeId) {
		this.officeId = officeId;
	}
	/**
	 * @return Returns the createdBy}.
	 */
	public Short getCreatedBy() {
		return createdBy;
	}
	/**
	 * @param createdBy The createdBy to set.
	 */
	public void setCreatedBy(Short createdBy) {
		this.createdBy = createdBy;
	}
	/**
	 * @return Returns the createdDate}.
	 */
	public Date getCreatedDate() {
		return createdDate;
	}
	/**
	 * @param createdDate The createdDate to set.
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	/**
	 * @return Returns the updatedBy}.
	 */
	public Short getUpdatedBy() {
		return updatedBy;
	}
	/**
	 * @param updatedBy The updatedBy to set.
	 */
	public void setUpdatedBy(Short updatedBy) {
		this.updatedBy = updatedBy;
	}
	/**
	 * @return Returns the updatedDate}.
	 */
	public Date getUpdatedDate() {
		return updatedDate;
	}
	/**
	 * @param updatedDate The updatedDate to set.
	 */
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	
	public Date getClosedDate() {
		return closedDate;
	}
	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}
	public void setAccountFeesSetHelper(Set<AccountFees> accountFeesSet){
		if(null == accountFeesSet || accountFeesSet.isEmpty()){
			return;
		}
		for(AccountFees accountFees :accountFeesSet){
			
			Fees fee = accountFees.getFees();
			
		}
		this.accountFeesSet = accountFeesSet;
		
	}
	/**
	 * This is not used to insert or update stuff in the database, neither would this be initialized when the account object is created.
	 * This is just used to display stuff on the UI.
	 * @return Returns the accountFlagDetail}.
	 */
	public AccountFlagDetail getAccountFlagDetail() {
		return accountFlagDetail;
	}
	/**
	 * This is not used to insert or update stuff in the database, neither would this be initialized when the account object is created.
	 * This is just used to display stuff on the UI.
	 * @param accountFlagDetail The accountFlagDetail to set.
	 */
	public void setAccountFlagDetail(AccountFlagDetail accountFlagDetail) {
		this.accountFlagDetail = accountFlagDetail;
	}
	/**
	 * @return Returns the accountActivity.
	 */
	public Set<AccountActivity> getAccountActivitySet() {
		return accountActivitySet;
	}
	public void addAccountActivity(AccountActivity accountActivity)
	{
		accountActivity.setAccount(this);
		this.accountActivitySet.add(accountActivity);
		setAccountActivitySet(accountActivitySet);
	}
	/**
	 * @param accountActivity The accountActivity to set.
	 */
	public void setAccountActivitySet(Set<AccountActivity> accountActivitySet) {
		if(null!=accountActivitySet){
			for(Iterator itr = accountActivitySet.iterator();itr.hasNext();){
				AccountActivity accountActivity=(AccountActivity)itr.next();
				accountActivity.setAccount(this); 
			}
		}
		this.accountActivitySet = accountActivitySet;
	}

	public void addAccountActionDate(AccountActionDate accountActionDate)
	{
		accountActionDate.setAccount(this);
		this.accountActionDateSet.add(accountActionDate);
		setAccountActionDateSet(accountActionDateSet);
		
	}

	public Set<AccountActionDate> getAccountActionDateSet() {
		return accountActionDateSet;
	}

	public void setAccountActionDateSet(Set<AccountActionDate> accountActionDateSet) 
	{
		if(null!=accountActionDateSet){
			for(Iterator itr = accountActionDateSet.iterator();itr.hasNext();){
				AccountActionDate accountActionDate=(AccountActionDate)itr.next();
				accountActionDate.setAccount(this); 
			}
		}
		this.accountActionDateSet = accountActionDateSet;

	}
	
	public void removeAccountActionDate(AccountActionDate accountActionDateRemove)
	{
		AccountActionDate accDateToRemove = null;
		if(null!=accountActionDateSet){
			for(Iterator itr = accountActionDateSet.iterator();itr.hasNext();)
			{
				AccountActionDate accountActionDate=(AccountActionDate)itr.next();
				if(accountActionDate.equals(accountActionDateRemove))
					accDateToRemove = accountActionDate;
			}
			if(accDateToRemove != null)
				accountActionDateSet.remove(accDateToRemove);
		}

		
	}
	
}
