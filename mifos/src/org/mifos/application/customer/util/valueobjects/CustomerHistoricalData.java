/**

 * CustomerHistoricalData.java    version: 1.0



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
package org.mifos.application.customer.util.valueobjects;

import java.sql.Date;

import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.ValueObject;

import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.application.customer.util.valueobjects.Customer;

/**
 * A class that represents a row in the 'customer_historicaldata' table.
 */
public class CustomerHistoricalData extends ValueObject{
	
	 /** The primary key value. */
	 private Short historicalId;
	 
	 /** The value of the simple customerId property. */
	 private Integer customerId;
	 
	 /** The value of the simple productName property. */
	 private String productName;
	 
	 /** The value of the simple loanAmount property. */
	 
	 private Money loanAmount;
	 
	 /** The value of the simple totalAmountPaid property. */
	 private Money totalAmountPaid;
	 
	 /** The value of the simple interestPaid property. */
	 private Money interestPaid;
	 
	 /** The value of the simple missedPaymentsCount property. */
	 private Integer missedPaymentsCount;
	 
	 /** The value of the simple totalPaymentsCount property. */
	 private Integer totalPaymentsCount;
	 
	 /** The value of the simple notes property. */
	 private String notes;
	 
	 /** The value of the simple loanCycleNumber property. */
	 private Integer loanCycleNumber;
	 
	 /** The value of the simple versionNo property. */
	 private Integer versionNo;
	 
	 /** The customer composition*/
	 private Customer customer;
	 
	 /** The personnel composition for createdBy property. */
	 private Personnel createdBy;
	 
	 /** The personnel composition for updatedBy property. */
	 private Personnel updatedBy;
	 
	 /** The value of the simple createdDate property. */
	 private Date createdDate;
	 
	 /** The value of the simple updatedDate property. */
	 private Date updatedDate;
	 
	 /** The value of the simple mfiJoiningDate property. */
	 private Date mfiJoiningDate;

	/**
	 * Simple constructor of CustomerHistoricalData
	 */
	 public CustomerHistoricalData() {}

	/**
     * Sets the value for mfi joining date
     * @param mfiJoiningDate
     */
	 public void setMfiJoiningDate(java.sql.Date mfiJoiningDate)
	 {
		 this.mfiJoiningDate = mfiJoiningDate;
	 }
	 
	/**
     * Return the value of mfi joining date
     * @return java.sql.Date
     */
	 public java.sql.Date getMfiJoiningDate()
	 {
		return mfiJoiningDate;
	 }
	 
	/**
     * Set the value for created date
     * @param createdDate
     */
 	 public void setCreatedDate(java.sql.Date createdDate)
	 {	this.createdDate = createdDate;
	 }
 	 
    /**
     * Return the value created date
     * @return java.sql.Date
     */
	 public java.sql.Date getCreatedDate()
	 {
		return createdDate;
	 }

	/**
     * Set the value for updated date
     * @param updatedDate
     */
 	 public void setUpdatedDate(java.sql.Date updatedDate)
	 {
		this.updatedDate = updatedDate;
	 }
 	 
 	/**
     * Return the value of last updated date
     * @return java.sql.Date
     */
 	 public java.sql.Date getUpdatedDate()
	 {
		return updatedDate;
	 }

 	/**
     * Set the customer associated to the historical data
     * @param customer
     */
	 public void setCustomer(Customer customer)
	 {
		this.customer = customer;
		if(customer != null)
		   this.mfiJoiningDate = customer.getMfiJoiningDate();
	 }
	 
    /**
     * Return the customer associated to this record
     * @return Customer
     */
	 public Customer getCustomer()
	 {
		return customer;
	 }
	 
	 /**
     * Return the personnel who created this record
     * @return Personnel
     */
	 public Personnel getCreatedBy()
	 {
		return createdBy;
	 }
	 
	 /**
     * Set the personnel who created this record
     * @param createdBy
     */
	 public void setCreatedBy(Personnel createdBy)
	 {
		this.createdBy = createdBy;
	 }

	 /**
     * Return the personnel who updated this record
     * @return Personnel
     */
	 public Personnel getUpdatedBy()
	 {
		return updatedBy;
	 }

	 /**
     * Set the personnel who updated this record
     * @param updatedBy
     */
	public void setUpdatedBy(Personnel updatedBy)
	{
		this.updatedBy = updatedBy;
	}
	
	/**
     * Set the version number
     * @param versionNo
     */
	public void setVersionNo(Integer versionNo)
	{
		this.versionNo = versionNo;
	}
	
	/**
     * Return the current version number
     * @return Integer
     */
	public Integer getVersionNo()
	{
		return versionNo;
	}

	/**
     * Set the historical id , this set by hibernate
     * @param historicalId
     */
	public void setHistoricalId(Short historicalId)
	{
		this.historicalId = historicalId;
	}
	
	/**
	 * Return the historical id
     * @return Short
     */
	public Short getHistoricalId()
	{
		return historicalId;
	}

	/**
     * Set the customer id
     * @param customerId
     */

	public void setCustomerId(Integer customerId)
	{
		this.customerId = customerId;
	}
	
	/**
     * Return the customer
     * @return Integer
     */
	public Integer getCustomerId()
	{
		return customerId;
	}

	/**
     * Set the notes
     * @param notes
     */

	public void setNotes(String notes)
	{
		this.notes = notes;
	}
	
	/**
     * Return the note associated
     * @return String
     */
	public String getNotes()
	{
		return notes;
	}

	/**
     * Set the loan cycle number
     * @param loanCycleNumber
     */
	public void setLoanCycleNumber(Integer loanCycleNumber)
	{
		this.loanCycleNumber = loanCycleNumber;
	}

	/**
     * Return the note loanCycleNumber
     * @return Integer
     */
	public Integer getLoanCycleNumber()
	{
		return loanCycleNumber;
	}

	/**
     * Set the TotalAmountPaid
     * @param totalAmountPaid
     */
	public void setTotalAmountPaid(Money totalAmountPaid)
	{
		this.totalAmountPaid = totalAmountPaid;
	}
	
	/**
     * Return the note totalAmountPaid
     * @return Double
     */
	public Money getTotalAmountPaid()
	{
		return totalAmountPaid;
	}
	
	/**
     * Set the InterestPaid
     * @param interestPaid
     */
	public void setInterestPaid(Money interestPaid)
	{
		this.interestPaid = interestPaid;
	}
	
	/**
     * Return the interestPaid
     * @return Double
     */
	public Money getInterestPaid()
	{
		return interestPaid;
	}

	/**
     * Set the MissedPaymentsCount
     * @param missedPaymentsCount
     */
	public void setMissedPaymentsCount(Integer missedPaymentsCount)
	{
		this.missedPaymentsCount = missedPaymentsCount;
	}
	
	/**
     * Return the missedPaymentsCount
     * @return Integer
     */
	public Integer getMissedPaymentsCount()
	{
		return missedPaymentsCount;
	}

	/**
     * Set the TotalPaymentsCount
     * @param totalPaymentsCount
     */
	public void setTotalPaymentsCount(Integer totalPaymentsCount)
	{
		this.totalPaymentsCount = totalPaymentsCount;
	}
	
	/**
     * Return the totalPaymentsCount
     * @return Integer
     */
	public Integer getTotalPaymentsCount()
	{
		return totalPaymentsCount;
	}

	/**
     * Set the ProductName
     * @param productName
     */
	public void setProductName(String productName)
	{
		this.productName = productName;
	}
	
	/**
     * Return the productName
     * @return String
     */

	public String getProductName()
	{
		return productName;
	}
	
	/**
     * Set the LoanAmount
     * @param loanAmount
     */
	
	public void setLoanAmount(Money loanAmount)
	{
		this.loanAmount = loanAmount;
	}

	/**
     * Return the loanAmount
     * @return Double
     */
	public Money getLoanAmount()
	{
		return loanAmount;
	}

   /**
	 * This method returns the name by which a CustomerHistoricalData object will be stored in the context object. 
	 * Framework will set CustomerHistoricalData object to request with this name. 
	 * @return The name by which the CustomerHistoricalData  object can be referred to
	 */
	public String getResultName(){
		return CustomerConstants.CUSTOMER_HISTORICAL_DATA_VO;
	}

}
