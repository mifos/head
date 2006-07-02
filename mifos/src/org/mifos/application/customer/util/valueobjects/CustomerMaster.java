/**

 * CustomerMaster.java    version: xxx



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

/**
 * @author sumeethaec
 *
 */
public class CustomerMaster {

	/** The composite primary key value. */
    private Integer customerId;

    /**The global customer number*/
    private String globalCustNum;

    /** The value of the simple displayName property. */
    private String displayName;
    /**The statsu of the customer*/
    private Short statusId;
    
    private Integer versionNo;
    /**
     * Indicates the level of the customer as to if it is a client,group,center.
     */
    private Short customerLevelId;
    
    /**
     * This has been added for passing in the link to check roles and permission.
     */
    private Short officeId;
    
    /**
     * This has been added for passing in the link to check roles and permission.
     */
    private Short personnelId;
	/**
	 * Method which returns the customerId
	 * @return Returns the customerId.
	 */
    
    private Short recurAfter;
    public CustomerMaster(){
     
    }

    public CustomerMaster(java.lang.Integer customerId , java.lang.String displayName , java.lang.String globalCustNum , java.lang.Short statusId)
    {
    	this.customerId = customerId;
    	this.displayName = displayName;
    	this.globalCustNum = globalCustNum;
    	this.statusId = statusId;
    }
    
    /**
     * This constructor is called when instantiating the object from the query Retrieve Customer Master and CustomerUtilDAO.
     * @param customerId
     * @param displayName
     * @param globalCustNum
     * @param statusId
     * @param customerLevelId
     */
    public CustomerMaster(java.lang.Integer customerId , java.lang.String displayName , java.lang.String globalCustNum , java.lang.Short statusId,Short customerLevelId,Integer versionNo,Short officeId,Short personnelId)
    {
    	this.customerId = customerId;
    	this.displayName = displayName;
    	this.globalCustNum = globalCustNum;
    	this.statusId = statusId;
    	this.customerLevelId = customerLevelId; 
    	this.versionNo = versionNo;
    	this.officeId = officeId;
    	this.personnelId = personnelId;
    }
    
    public CustomerMaster(java.lang.Integer customerId , java.lang.String displayName , java.lang.String globalCustNum , java.lang.Short statusId,Short customerLevelId,Integer versionNo,Short officeId,Short personnelId,Short recurAfter)
    {
    	this.customerId = customerId;
    	this.displayName = displayName;
    	this.globalCustNum = globalCustNum;
    	this.statusId = statusId;
    	this.customerLevelId = customerLevelId; 
    	this.versionNo = versionNo;
    	this.officeId = officeId;
    	this.personnelId = personnelId;
    	this.recurAfter = recurAfter;
    }


	public Integer getCustomerId() {
		return customerId;
	}

	/**
	 * Method which sets the customerId
	 * @param customerId The customerId to set.
	 */
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	/**
	 * Method which returns the displayName
	 * @return Returns the displayName.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Method which sets the displayName
	 * @param displayName The displayName to set.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Method which returns the globalCustNum
	 * @return Returns the globalCustNum.
	 */
	public String getGlobalCustNum() {
		return globalCustNum;
	}

	/**
	 * Method which sets the globalCustNum
	 * @param globalCustNum The globalCustNum to set.
	 */
	public void setGlobalCustNum(String globalCustNum) {
		this.globalCustNum = globalCustNum;
	}

	/**
	 * Method which returns the statusId
	 * @return Returns the statusId.
	 */
	public Short getStatusId() {
		return statusId;
	}

	/**
	 * Method which sets the statusId
	 * @param statusId The statusId to set.
	 */
	public void setStatusId(Short statusId) {
		this.statusId = statusId;
	}

	/**
	 * @return Returns the customerLevelId}.
	 */
	public Short getCustomerLevelId() {
		return customerLevelId;
	}

	/**
	 * @param customerLevelId The customerLevelId to set.
	 */
	public void setCustomerLevelId(Short customerLevelId) {
		this.customerLevelId = customerLevelId;
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

	public Short getRecurAfter() {
		return recurAfter;
	}

	public void setRecurAfter(Short recurAfter) {
		this.recurAfter = recurAfter;
	}

}
