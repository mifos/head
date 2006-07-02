/**

 * Customer.java    version: xxx



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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.accounts.util.valueobjects.CustomerAccount;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author ashishsm
 *
 */
/**
 * A class that represents a row in the 'customer' table.
 * This class may be customized as it is never re-generated
 * after being created.
 */
public class Customer extends ValueObject {
	/**
	 * Simple constructor of Customer instances.
	 */
	public Customer() {
		this.office = new Office();
		this.customerFormedByPersonnel = new Personnel();
		this.customerAddressDetail = new CustomerAddressDetail();
		this.customerDetail = new CustomerDetail();
		this.customFieldSet = new HashSet<CustomerCustomField>();
		//this.customerNameDetail = new CustomerNameDetail();
		this.customerDetail = new CustomerDetail();
		this.customerAccount = new CustomerAccount();
		this.customerHierarchy = new CustomerHierarchy();
		this.customerLevel = new CustomerLevel();
		this.customerPositions = new TreeSet();
		this.customerNote = new CustomerNote();
		this.historicalData = new CustomerHistoricalData();
		this.customerAccounts = new HashSet();

	}

	/** The composite primary key value. */
	private Integer customerId;

	/** The value of the customerLevel association. */
	private CustomerLevel customerLevel;

	/** The value of the personnel association. */
	private Personnel personnel;
	private Personnel customerFormedByPersonnel;
	/** The value of the simple globalCustNum property. */
	private String globalCustNum;

	/** The value of the simple branchId property. */
	private Office office;

	/** The value of the simple statusId property. */
	private Short statusId;

	/** The value of the simple displayName property. */
	private String displayName;

	/** The value of the simple displayAddress property. */
	private String displayAddress;

	/** The value of the simple externalId property. */
	private String externalId;

	/** The value of the simple dateOfBirth property. */
	private java.sql.Date dateOfBirth;

	/** The value of the simple groupFlag property. */
	private Short groupFlag;

	/** The value of the simple trained property. */
	private Short trained;

	/** The value of the simple trainedDate property. */
	private java.sql.Date trainedDate;

	/** The value of the simple createdDate property. */
	private java.sql.Date createdDate;

	/** The value of the simple mfiJoiningDate property. */
	private java.sql.Date mfiJoiningDate;

	/** The value of the simple updatedDate property. */
	private java.sql.Date updatedDate;

	/** The value of the simple searchId property. */
	private String searchId;

	/** The value of the simple searchId property. */
	private String governmentId;

	/** The value of the simple maxChildCount property. */
	private Integer maxChildCount;

	/** The value of the simple hoUpdated property. */
	private Short hoUpdated;

	/** The value of the simple clientConfidential property. */
	private Short clientConfidential;

	private Date customerActivationDate;

	private Short createdBy;

	private Short updatedBy;

	/**
	 * The set of custom field values.
	 */
	private Set<CustomerCustomField> customFieldSet;

	private Set customerPositions;

	private Set customerFlag;

	private Customer parentCustomer;

	private Set customerAccounts = null;

	private CustomerAddressDetail customerAddressDetail;

	// private CustomerNameDetail customerNameDetail;
	private CustomerDetail customerDetail;

	private CustomerAccount customerAccount;

	private CustomerMeeting customerMeeting;

	private CustomerHierarchy customerHierarchy;

	private CustomerNote customerNote;

	private Map positionMap = new HashMap();

	private Map positionVersionMap = new HashMap();

	private Integer versionNo;

	private CustomerHistoricalData historicalData;

	private Short blackListed;

	private Short personnelId;

	/**
	 * @return Returns the personnelId.
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

	public Short getBlackListed() {
		return blackListed;
	}

	public void setBlackListed(Short blackListed) {
		this.blackListed = blackListed;
	}

	/**
	 * Return the simple primary key value that identifies this object.
	 * @return Integer
	 */
	public Integer getCustomerId() {
		return customerId;
	}

	/**
	 * Set the simple primary key value that identifies this object.
	 * @param customerId
	 */
	public void setCustomerId(Integer customerId) {

		this.customerId = customerId;
	}

	/**
	 * Return the value of the CUSTOMER_LEVEL_ID column.
	 * @return CustomerLevel
	 */
	public CustomerLevel getCustomerLevel() {
		return this.customerLevel;
	}

	/**
	 * Set the value of the CUSTOMER_LEVEL_ID column.
	 * @param customerLevel
	 */
	public void setCustomerLevel(CustomerLevel customerLevel) {

		this.customerLevel = customerLevel;
	}

	/**
	 * Return the value of the GLOBAL_CUST_NUM column.
	 * @return String
	 */
	public String getGlobalCustNum() {
		return this.globalCustNum;
	}

	/**
	 * Set the value of the GLOBAL_CUST_NUM column.
	 * @param globalCustNum
	 */
	public void setGlobalCustNum(String globalCustNum) {
		this.globalCustNum = globalCustNum;
	}

	/**
	 * Return the value of the LOAN_OFFICER_ID column.
	 * @return Personnel
	 */
	public Personnel getPersonnel() {
		return this.personnel;
	}

	/**
	 * Set the value of the LOAN_OFFICER_ID column.
	 * @param personnel
	 */
	public void setPersonnel(Personnel personnel) {
		this.personnel = personnel;
	}

	/**
	 * Return the value of the STATUS_ID column.
	 * @return Short
	 */
	public Short getStatusId() {
		return this.statusId;
	}

	/**
	 * Set the value of the STATUS_ID column.
	 * @param statusId
	 */
	public void setStatusId(Short statusId) {
		this.statusId = statusId;
	}

	/**
	 * Return the value of the DISPLAY_NAME column.
	 * @return String
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * Set the value of the DISPLAY_NAME column.
	 * @param displayName
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Return the value of the DISPLAY_ADDRESS column.
	 * @return String
	 */
	public String getDisplayAddress() {
		return this.displayAddress;
	}

	/**
	 * Set the value of the DISPLAY_ADDRESS column.
	 * @param displayAddress
	 */
	public void setDisplayAddress(String displayAddress) {
		this.displayAddress = displayAddress;
	}

	/**
	 * Return the value of the EXTERNAL_ID column.
	 * @return String
	 */
	public String getExternalId() {
		return this.externalId;
	}

	/**
	 * Set the value of the EXTERNAL_ID column.
	 * @param externalId
	 */
	public void setExternalId(String externalId) {
		// System.out.println("setExternalId in VO with value: "+externalId);
		this.externalId = externalId;
	}

	/**
	 * Return the value of the DATE_OF_BIRTH column.
	 * @return java.util.Date
	 */
	public java.sql.Date getDateOfBirth() {
		return this.dateOfBirth;
	}

	/**
	 * Set the value of the DATE_OF_BIRTH column.
	 * @param dateOfBirth
	 */
	public void setDateOfBirth(java.sql.Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * Return the value of the GROUP_FLAG column.
	 * @return Short
	 */
	public Short getGroupFlag() {
		return this.groupFlag;
	}

	/**
	 * Set the value of the GROUP_FLAG column.
	 * @param groupFlag
	 */
	public void setGroupFlag(Short groupFlag) {
		this.groupFlag = groupFlag;
	}

	/**
	 * Return the value of the TRAINED column.
	 * @return Short
	 */
	public Short getTrained() {
		return this.trained;
	}

	/**
	 * Set the value of the TRAINED column.
	 * @param trained
	 */
	public void setTrained(Short trained) {
		this.trained = trained;
	}

	/**
	 * Return the value of the TRAINED_DATE column.
	 * @return java.util.Date
	 */
	public java.sql.Date getTrainedDate() {
		return this.trainedDate;
	}

	/**
	 * Set the value of the TRAINED_DATE column.
	 * @param trainedDate
	 */
	public void setTrainedDate(java.sql.Date trainedDate) {
		this.trainedDate = trainedDate;
	}

	/**
	 * Return the value of the CREATED_DATE column.
	 * @return java.util.Date
	 */
	public java.sql.Date getCreatedDate() {
		return this.createdDate;
	}

	/**
	 * Set the value of the CREATED_DATE column.
	 * @param createdDate
	 */
	public void setCreatedDate(java.sql.Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * Return the value of the UPDATED_DATE column.
	 * @return java.util.Date
	 */
	public java.sql.Date getUpdatedDate() {
		return this.updatedDate;
	}

	/**
	 * Set the value of the UPDATED_DATE column.
	 * @param updatedDate
	 */
	public void setUpdatedDate(java.sql.Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	/**
	 * Return the value of the SEARCH_ID column.
	 * @return String
	 */
	public String getSearchId() {
		return this.searchId;
	}

	/**
	 * Set the value of the SEARCH_ID column.
	 * @param searchId
	 */
	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}

	/**
	 * Return the value of the MAX_CHILD_COUNT column.
	 * @return Integer
	 */
	public Integer getMaxChildCount() {
		return this.maxChildCount;
	}

	/**
	 * Set the value of the MAX_CHILD_COUNT column.
	 * @param maxChildCount
	 */
	public void setMaxChildCount(Integer maxChildCount) {
		this.maxChildCount = maxChildCount;
	}

	/**
	 * Return the value of the HO_UPDATED column.
	 * @return Short
	 */
	public Short getHoUpdated() {
		return this.hoUpdated;
	}

	/**
	 * Set the value of the HO_UPDATED column.
	 * @param hoUpdated
	 */
	public void setHoUpdated(Short hoUpdated) {
		this.hoUpdated = hoUpdated;
	}

	/**
	 * Return the value of the CLIENT_CONFIDENTIAL column.
	 * @return Short
	 */
	public Short getClientConfidential() {
		return this.clientConfidential;
	}

	/**
	 * Set the value of the CLIENT_CONFIDENTIAL column.
	 * @param clientConfidential
	 */
	public void setClientConfidential(Short clientConfidential) {
		this.clientConfidential = clientConfidential;
	}

	/**
	 * @return Returns the customFieldSet}.
	 */
	public Set<CustomerCustomField> getCustomFieldSet() {
		return customFieldSet;
	}

	/**
	 * @param customFieldSet The customFieldSet to set.
	 */
	public void setCustomFieldSet(Set<CustomerCustomField> customFieldSet) {
		for(CustomerCustomField obj : customFieldSet){
			if(obj.getFieldId()!=null){
				CustomerCustomField customField = getCustomerCustomField(obj.getFieldId());
				if(customField==null) {
					obj.setCustomer(this);
					this.customFieldSet.add(obj);
				} else {
					customField.setFieldValue(obj.getFieldValue());
				}
			}
		}
	}
	private CustomerCustomField getCustomerCustomField(Short fieldId) {
		if(null != this.customFieldSet && this.customFieldSet.size()>0) {
			for(CustomerCustomField obj : this.customFieldSet) {
				if(obj.getFieldId().equals(fieldId)) 
					return obj;
			}
		}
		return null;
	}
	/**
	 * @return Returns the customerAddressDetail}.
	 */
	public CustomerAddressDetail getCustomerAddressDetail() {
		return customerAddressDetail;
	}

	/**
	 * @param customerAddressDetail The customerAddressDetail to set.
	 */
	public void setCustomerAddressDetail(
			CustomerAddressDetail customerAddressDetail) {
		// System.out.println("set on customer address detail called "+customerAddressDetail);
		//if(null == customerAddressDetail.getCustomer()){
		if (null != customerAddressDetail) {
			customerAddressDetail.setCustomer(this);
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
					"Setting customer in customer Address detail");
		}

		//}
		this.customerAddressDetail = customerAddressDetail;
	}

	/**
	 * @return Returns the customerNameDetail}.
	 */
	/*public CustomerNameDetail getCustomerNameDetail() {
	 return customerNameDetail;
	 }

	 *//**
	 * @param customerNameDetail The customerNameDetail to set.
	 */
	/*
	 public void setCustomerNameDetail(CustomerNameDetail customerNameDetail) {
	 this.customerNameDetail = customerNameDetail;
	 }*/

	/**
	 * @return Returns the customerDetail}.
	 */
	public CustomerDetail getCustomerDetail() {
		return customerDetail;
	}

	/**
	 * @param customerDetail The customerDetail to set.
	 */
	public void setCustomerDetail(CustomerDetail customerDetail) {
		this.customerDetail = customerDetail;
	}

	/**
	 * Method which returns the office
	 * @return Returns the office.
	 */
	public Office getOffice() {
		return office;
	}

	/**
	 * Method which sets the office
	 * @param office The office to set.
	 */
	public void setOffice(Office office) {
		this.office = office;
	}

	/**
	 * @return Returns the customerAccount}.
	 */
	public CustomerAccount getCustomerAccount() {
		return customerAccount;
	}

	/**
	 * @param customerAccount The customerAccount to set.
	 */
	public void setCustomerAccount(CustomerAccount customerAccount) {
		this.customerAccount = customerAccount;
	}

	/**
	 * @return Returns the customerMeeting}.
	 */
	public CustomerMeeting getCustomerMeeting() {
		return customerMeeting;
	}

	/**
	 * @param customerMeeting The customerMeeting to set.
	 */
	public void setCustomerMeeting(CustomerMeeting customerMeeting) {
		if (null != customerMeeting)
			customerMeeting.setCustomer(this);
		this.customerMeeting = customerMeeting;
	}

	/**
	 * @return Returns the customerHierarchy}.
	 */
	public CustomerHierarchy getCustomerHierarchy() {
		return customerHierarchy;
	}

	/**
	 * @param customerHierarchy The customerHierarchy to set.
	 */
	public void setCustomerHierarchy(CustomerHierarchy customerHierarchy) {
		this.customerHierarchy = customerHierarchy;
	}

	public void addToCustomFieldSet(CustomerCustomField customerCustomField) {
		this.customFieldSet.add(customerCustomField);

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
	 * Method which returns the customerPositions
	 * @return Returns the customerPositions.
	 */
	public Set getCustomerPositions() {
		if (customerPositions != null) {
			Iterator<CustomerPosition> i = customerPositions.iterator();
			CustomerPosition c;
			while (i.hasNext()) {
				c = i.next();
				positionMap.put(c.getPositionId(), c.getCustomerPositionId());
				positionVersionMap.put(c.getPositionId(), c.getVersionNo());

			}
		}
		return customerPositions;
	}

	/**
	 * Method which sets the customerPositions
	 * @param customerPositions The customerPositions to set.
	 */
	public void setCustomerPositions(Set customerPositions) {
		if (customerPositions != null) {

			Iterator<CustomerPosition> i = customerPositions.iterator();
			while (i.hasNext()) {
				CustomerPosition c;
				c = i.next();
				if (c.getParentCustomer() == null)
					c.setParentCustomer(this);
				if (positionMap.size() != 0
						&& positionMap.get(c.getPositionId()) != null) {

					c.setCustomerPositionId((Integer) positionMap.get(c
							.getPositionId()));
					c.setVersionNo((Integer) positionVersionMap.get(c
							.getPositionId()));

				}

			}

		}
		this.customerPositions = customerPositions;

	}

	private void setHistoricalData(CustomerHistoricalData historicalData) {

		if (historicalData != null) {
			historicalData.setMfiJoiningDate(mfiJoiningDate);
		}

		this.historicalData = historicalData;

	}

	public void setCustomerHistoricalData(CustomerHistoricalData historicalData) {

		if (historicalData != null) {
			this.mfiJoiningDate = historicalData.getMfiJoiningDate();
			historicalData.setCustomer(this);

		}

		setHistoricalData(historicalData);

	}

	public CustomerHistoricalData getHistoricalData() {
		return historicalData;
	}

	public Set getCustomerFlag() {
		return customerFlag;
	}

	public void setCustomerFlag(Set customerFlag) {
		if (customerFlag != null) {
			Iterator<CustomerFlag> i = customerFlag.iterator();
			while (i.hasNext()) {
				CustomerFlag c;
				c = i.next();
				if (c.getCustomer() == null)
					c.setCustomer(this);

			}

			this.customerFlag = customerFlag;

		}
	}

	/**
	 * Method which returns the customerNote
	 * @return Returns the customerNote.
	 */
	public CustomerNote getCustomerNote() {
		return customerNote;
	}

	/**
	 * Method which sets the customerNote
	 * @param customerNote The customerNote to set.
	 */
	public void setCustomerNote(CustomerNote customerNote) {
		this.customerNote = customerNote;
	}

	/**
	 * Method which returns the mfiJoiningDate
	 * @return Returns the mfiJoiningDate.
	 */
	public java.sql.Date getMfiJoiningDate() {
		return mfiJoiningDate;
	}

	/**
	 * Method which sets the mfiJoiningDate
	 * @param mfiJoiningDate The mfiJoiningDate to set.
	 */
	public void setMfiJoiningDate(java.sql.Date mfiJoiningDate) {
		this.mfiJoiningDate = mfiJoiningDate;
	}

	/**
	 * This is a helper method used to transfer a set of selected fee from action form to valueobject.
	 * @param selectedFeeSet
	 */
	public void setSelectedFeeSet(Set<AccountFees> selectedFeeSet) {
		if (this.customerAccount != null && selectedFeeSet != null) {
			this.customerAccount.setAccountFeesSet(selectedFeeSet);
			for (AccountFees fee : selectedFeeSet)
				fee.setAccount(this.customerAccount);
		}
		Set<CustomerAccount> accountsSet = new HashSet<CustomerAccount>();
		accountsSet.add(this.customerAccount);
		this.customerAccounts = accountsSet;
		MifosLogManager
				.getLogger(LoggerConstants.CENTERLOGGER)
				.debug(
						"after setting set of selected fees in customerAccount object ");
	}

	public void setParentCustomer(Customer parentCustomer) {
		this.parentCustomer = parentCustomer;
	}

	public Customer getParentCustomer() {
		return parentCustomer;
	}

	public void setCreatedBy(Short createdBy) {
		this.createdBy = createdBy;
	}

	public void setUpdatedBy(Short updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Short getCreatedBy() {
		return createdBy;
	}

	public Short getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * Method which returns the governmentId
	 * @return Returns the governmentId.
	 */
	public String getGovernmentId() {
		return governmentId;
	}

	/**
	 * Method which sets the governmentId
	 * @param governmentId The governmentId to set.
	 */
	public void setGovernmentId(String governmentId) {
		this.governmentId = governmentId;
	}

	/**
	 * @return Returns the customerAccounts.
	 */
	public Set getCustomerAccounts() {
		return customerAccounts;
	}

	/**
	 * @param customerAccounts The customerAccounts to set.
	 */
	public void setCustomerAccounts(Set customerAccounts) {
		this.customerAccounts = customerAccounts;
	}

	public Date getCustomerActivationDate() {
		return customerActivationDate;
	}

	public void setCustomerActivationDate(Date activationDate) {
		this.customerActivationDate = activationDate;
	}
	
	public CustomerCustomField getCustomField(int index) {
		java.util.List<CustomerCustomField> customFieldList = new ArrayList<CustomerCustomField>(customFieldSet);
		Collections.sort(customFieldList,new Comparator<CustomerCustomField>() {
	        public int compare(CustomerCustomField o1, CustomerCustomField o2) {
		        Short s1 = o1.getFieldId();
		        Short s2 = o2.getFieldId();
		        return s1.compareTo(s2);
		      }
		  });
		return (CustomerCustomField) (customFieldList.get(index));
	}
	
	
	public void convertCustomFieldDateToDbformat(Locale locale)throws SystemException{
		for(CustomerCustomField customField : customFieldSet){
			String fieldValue = CustomFieldDefinition.convertDateToDbformat(customField.getFieldId(), customField.getFieldValue() ,locale);
			customField.setFieldValue(fieldValue);
		}
	}
	
	public Personnel getCustomerFormedByPersonnel() {
		return customerFormedByPersonnel;
	}

	public void setCustomerFormedByPersonnel(Personnel customerFormedByPersonnel) {
		this.customerFormedByPersonnel = customerFormedByPersonnel;
	}
	
}
