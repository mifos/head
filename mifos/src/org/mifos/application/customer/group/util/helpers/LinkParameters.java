/**

 * LinkParameters.java    version: 1.0



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
package org.mifos.application.customer.group.util.helpers;

import java.io.Serializable;

/**
 * This class is the helper class to show links at top of the jsp pages
 */
public class LinkParameters implements Serializable {
	
	/**Denotes the global customer number*/
	private String globalCustNum;
	
	/**Denotes the customer Id*/	
	private Integer customerId;
	
	/**Denotes the customer Name*/	
	private String customerName;
	
	/**Denotes the level of the customer, group/center/client*/	
	private Short levelId;
	
	/**Denotes the customer parent gloabal customer num(if parent exists)*/
	private String customerParentGCNum;

	/**Denotes the customer parent Id(if parent name)*/
	private String customerParentName;
	
	/**Denotes the customer parent gloabal customer num(if parent exists)*/
	private String customerCenterGCNum;

	/**Denotes the customer parent Id(if parent name)*/
	private String customerCenterName;

	/**Denotes the customer officeId*/
	private Short customerOfficeId;
	
	/**Denotes the customer officeName*/
	private String customerOfficeName;

	/**
     * Return the value of the customerId attribute.
     * @return Integer
     */
	public Integer getCustomerId() {
		return customerId;
	}
	
	/**
     * Sets the value of customerId
     * @param customerId
     */
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	/**
     * Return the value of the customerName attribute.
     * @return String
     */
	public String getCustomerName() {
		return customerName;
	}

	/**
     * Sets the value of customerName
     * @param customerName
     */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	/**
     * Return the value of the globalCustNum attribute.
     * @return String
     */
	public String getGlobalCustNum() {
		return globalCustNum;
	}

	/**
     * Sets the value of globalCustNum
     * @param globalCustNum
     */
	public void setGlobalCustNum(String globalCustNum) {
		this.globalCustNum = globalCustNum;
	}
	
	/**
     * Return the value of the customerOfficeId attribute.
     * @return Short
     */
	public Short getCustomerOfficeId() {
		return customerOfficeId;
	}

	/**
     * Sets the value of customerOfficeId
     * @param customerOfficeId
     */
	public void setCustomerOfficeId(Short customerOfficeId) {
		this.customerOfficeId = customerOfficeId;
	}

	/**
     * Return the value of the customerOfficeName attribute.
     * @return Integer
     */
	public String getCustomerOfficeName() {
		return customerOfficeName;
	}
	
	/**
     * Sets the value of customerOfficeName
     * @param customerOfficeName
     */
	public void setCustomerOfficeName(String customerOfficeName) {
		this.customerOfficeName = customerOfficeName;
	}

	/**
     * Return the value of the customerParentGCNum attribute.
     * @return Integer
     */
	public String getCustomerParentGCNum() {
		return customerParentGCNum;
	}

	/**
     * Sets the value of customerParentGCNum
     * @param customerParentGCNum
     */
	public void setCustomerParentGCNum(String customerParentGCNum) {
		this.customerParentGCNum = customerParentGCNum;
	}

	/**
     * Return the value of the customerParentName attribute.
     * @return String
     */
	public String getCustomerParentName() {
		return customerParentName;
	}

	/**
     * Sets the value of customerParentName
     * @param customerParentName
     */
	public void setCustomerParentName(String customerParentName) {
		this.customerParentName = customerParentName;
	}
	
	/**
     * Return the value of the levelId attribute.
     * @return Short
     */
	public Short getLevelId() {
		return levelId;
	}

	/**
     * Sets the value of levelId
     * @param levelId
     */
	public void setLevelId(Short levelId) {
		this.levelId = levelId;
	}

	/**
	 * Method which returns the customerCenterGCNum	
	 * @return Returns the customerCenterGCNum.
	 */
	public String getCustomerCenterGCNum() {
		return customerCenterGCNum;
	}

	/**
	 * Method which sets the customerCenterGCNum
	 * @param customerCenterGCNum The customerCenterGCNum to set.
	 */
	public void setCustomerCenterGCNum(String customerCenterGCNum) {
		this.customerCenterGCNum = customerCenterGCNum;
	}

	/**
	 * Method which returns the customerCenterName	
	 * @return Returns the customerCenterName.
	 */
	public String getCustomerCenterName() {
		return customerCenterName;
	}

	/**
	 * Method which sets the customerCenterName
	 * @param customerCenterName The customerCenterName to set.
	 */
	public void setCustomerCenterName(String customerCenterName) {
		this.customerCenterName = customerCenterName;
	}
}
