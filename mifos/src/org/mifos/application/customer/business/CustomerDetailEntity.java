/**

 * CustomerDetail.java    version: xxx



 * Copyright © 2005-2006 Grameen Foundation USA

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

package org.mifos.application.customer.business;

import java.util.Date;

import org.mifos.framework.business.PersistentObject;

/**
 * This class encapsulate the details of the customer
 * 
 * @author ashishsm
 * 
 */
public class CustomerDetailEntity extends PersistentObject {

	private Short customerDetailId;

	private Integer customerId;

	private CustomerBO customer;

	private Integer ethinicity;

	private Integer citizenship;

	private Integer handicapped;

	private Integer businessActivities;

	private Integer maritalStatus;

	private Integer educationLevel;

	private Short numChildren;

	private Short gender;

	private Date dateStarted;

	private String handicappedDetails;

	public CustomerDetailEntity() {
	}

	public Short getCustomerDetailId() {
		return customerDetailId;
	}

	public void setCustomerDetailId(Short customerDetailId) {

		this.customerDetailId = customerDetailId;
	}

	public Integer getEthinicity() {
		return this.ethinicity;
	}

	public void setEthinicity(Integer ethinicity) {
		this.ethinicity = ethinicity;
	}

	public Integer getCitizenship() {
		return this.citizenship;
	}

	public void setCitizenship(Integer citizenship) {
		this.citizenship = citizenship;
	}

	public Integer getHandicapped() {
		return this.handicapped;
	}

	public void setHandicapped(Integer handicapped) {
		this.handicapped = handicapped;
	}
	public Integer getBusinessActivities() {
		return this.businessActivities;
	}

	public void setBusinessActivities(Integer businessActivities) {
		this.businessActivities = businessActivities;
	}

	public Integer getMaritalStatus() {
		return this.maritalStatus;
	}

	public void setMaritalStatus(Integer maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public Integer getEducationLevel() {
		return this.educationLevel;
	}

	public void setEducationLevel(Integer educationLevel) {
		this.educationLevel = educationLevel;
	}

	public Short getNumChildren() {
		return this.numChildren;
	}

	public void setNumChildren(Short numChildren) {
		this.numChildren = numChildren;
	}

	public Short getGender() {
		return this.gender;
	}

	public void setGender(Short gender) {
		this.gender = gender;
	}

	public java.util.Date getDateStarted() {
		return this.dateStarted;
	}

	public void setDateStarted(java.util.Date dateStarted) {
		this.dateStarted = dateStarted;
	}

	public String getHandicappedDetails() {
		return this.handicappedDetails;
	}

	public void setHandicappedDetails(String handicappedDetails) {
		this.handicappedDetails = handicappedDetails;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public CustomerBO getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerBO customer) {
		this.customer = customer;
	}
}