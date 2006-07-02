/**

 * AccountActionForm.java    version: xxx



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

package org.mifos.application.accounts.struts.actionforms;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.framework.struts.actionforms.MifosSearchActionForm;

/**
 * This class acts as base action form class for all types of accounts.
 * @author ashishsm
 *
 */
public class AccountActionForm extends MifosSearchActionForm {

	/**
	 *
	 */
	public AccountActionForm() {
		super();
		customer = new Customer();


	}


/** The  primary key value. */
	private Integer accountId;

	/** The value of the customer association. */
	//private Customer customer;
	private Integer versionNo;

	private Customer customer;


    /** The value of the accountState association. */
    private Short accountStateId;

    /** The value of the accountType association. */
    private Short accountTypeId;

    /** The value of the simple globalAccountNum property. */
    private java.lang.String globalAccountNum;

    /** The value of the simple officeId property. */
    private java.lang.Integer officeId;

    /** The value of the simple personnelId property. */
    private java.lang.Integer personnelId;


    /**
     * This is a helper map used to store Accountfee objects which are added from ui based on the index.
     */
    protected HashMap<Integer,AccountFees> accountFeesMap = new HashMap<Integer,AccountFees>();
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
	 * @return Returns the officeId}.
	 */
	public java.lang.Integer getOfficeId() {
		return officeId;
	}
	/**
	 * @param officeId The officeId to set.
	 */
	public void setOfficeId(java.lang.Integer officeId) {
		this.officeId = officeId;
	}
	/**
	 * @return Returns the personnelId}.
	 */
	public java.lang.Integer getPersonnelId() {
		return personnelId;
	}
	/**
	 * @param personnelId The personnelId to set.
	 */
	public void setPersonnelId(java.lang.Integer personnelId) {
		this.personnelId = personnelId;
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
	 * This is the method which is used for conversion from action form to value object.
	 * It converts a set out of the map .
	 * @return
	 */
	public Set getAccountFeesSetHelper(){
		Set<AccountFees> accountFeeSet = new HashSet<AccountFees>();
		Set<Integer> accountFeesMapKeys = accountFeesMap.keySet();
		if(null != accountFeesMapKeys && ! accountFeesMapKeys.isEmpty()){
			for(Integer accountFeeMapKey: accountFeesMapKeys){

				AccountFees accountFees = accountFeesMap.get(accountFeeMapKey);

				accountFeeSet.add(accountFees);
			}
		}
		return accountFeeSet;
	}

	/**
	 * This is the method which is called from UI to get the account fees.It gets the fees object using the
	 * getaccountFeesHelper.
	 * @param index
	 * @return
	 */
	public AccountFees getAccountFees(int index) {
		return getAccountFeesHelper(index);

	}

	/**
	 * This method returns the AccountFees object based on the idex from the feesMap.
	 * The index is used as key in the accountFeesMap. If the accountFeemap does not have any value for that key
	 * a new fees object is created and set in the map.
	 * @param index
	 * @return
	 */
	private AccountFees getAccountFeesHelper(int index) {
		// TODO SYSOUT IS TO BE REMOVED
		// // System.out.println("Inside getAccountFeesHelper with index " + index);
		if(accountFeesMap.containsKey(Integer.valueOf(index))){
			return accountFeesMap.get(Integer.valueOf(index));
		}else{
			AccountFees accountFee = new AccountFees();
			Fees fees = new Fees();
			accountFee.setFees(fees);
			accountFeesMap.put(Integer.valueOf(index),accountFee);
			return accountFee;
		}


	}
	/**
	 * This is called from LoanAction to clear the map before load because load is also called when the user changes his/her selection in the drop down of loan offerings.
	 * @return Returns the accountFeesMap}.
	 */
	public HashMap<Integer, AccountFees> getAccountFeesMap() {
		return accountFeesMap;
	}
	/**
	 * This is called from LoanAction to clear the map before load because load is also called when the user changes his/her selection in the drop down of loan offerings.
	 * @param accountFeesMap The accountFeesMap to set.
	 */
	public void setAccountFeesMap(HashMap<Integer, AccountFees> accountFeesMap) {
		this.accountFeesMap = accountFeesMap;
	}






}
