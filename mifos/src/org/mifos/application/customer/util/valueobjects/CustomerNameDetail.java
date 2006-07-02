/**

 * CustomerNameDetail.java    version: xxx



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

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author ashishsm
 *
 */
/**
 * A class that represents a row in the 'customer_name_detail' table.
 * This class may be customized as it is never re-generated
 * after being created.
 */
public class CustomerNameDetail extends ValueObject{
	/**
	 * Simple constructor of CustomerNameDetail instances.
	 */
	public CustomerNameDetail() {
		this.customer = new Customer();
	}

	/** The composite primary key value. */
	private Integer customerNameId;

	/** The value of the simple customerId property. */
	private Customer customer;

	/** The value of the simple nameType property. */
	private Short nameType;

	/** The value of the simple salutation property. */
	private Integer salutation;

	/** The value of the simple firstName property. */
	private String firstName;

	/** The value of the simple middleName property. */
	private String middleName;

	/** The value of the simple lastName property. */
	private String lastName;

	/** The value of the simple secondLastName property. */
	private String secondLastName;

	/** The value of the simple secondMiddleName property. */
	private String secondMiddleName;

	/** The value of the simple displayName property. */
	private String displayName;

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
	 * Return the value of the NAME_TYPE column.
	 * @return Short
	 */
	public Short getNameType() {
		return this.nameType;
	}

	/**
	 * Set the value of the NAME_TYPE column.
	 * @param nameType
	 */
	public void setNameType(Short nameType) {
		this.nameType = nameType;
	}

	/**
	 * Return the value of the SALUTATION column.
	 * @return Integer
	 */
	public Integer getSalutation() {
		return this.salutation;
	}

	/**
	 * Set the value of the SALUTATION column.
	 * @param salutation
	 */
	public void setSalutation(Integer salutation) {
		this.salutation = salutation;
	}

	/**
	 * Return the value of the FIRST_NAME column.
	 * @return String
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * Set the value of the FIRST_NAME column.
	 * @param firstName
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Return the value of the MIDDLE_NAME column.
	 * @return String
	 */
	public String getMiddleName() {
		return this.middleName;
	}

	/**
	 * Set the value of the MIDDLE_NAME column.
	 * @param middleName
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * Return the value of the LAST_NAME column.
	 * @return String
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * Set the value of the LAST_NAME column.
	 * @param lastName
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Return the value of the SECOND_LAST_NAME column.
	 * @return String
	 */
	public String getSecondLastName() {
		return this.secondLastName;
	}

	/**
	 * Set the value of the SECOND_LAST_NAME column.
	 * @param secondLastName
	 */
	public void setSecondLastName(String secondLastName) {
		this.secondLastName = secondLastName;
	}

	/**
	 * Return the value of the SECOND_MIDDLE_NAME column.
	 * @return String
	 */
	public String getSecondMiddleName() {
		return this.secondMiddleName;
	}

	/**
	 * Set the value of the SECOND_MIDDLE_NAME column.
	 * @param secondMiddleName
	 */
	public void setSecondMiddleName(String secondMiddleName) {
		this.secondMiddleName = secondMiddleName;
	}

	/**
	 * @return Returns the customerNameId}.
	 */
	public Integer getCustomerNameId() {
		return customerNameId;
	}

	/**
	 * @param customerNameId The customerNameId to set.
	 */
	public void setCustomerNameId(Integer customerNameId) {
		this.customerNameId = customerNameId;
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

}
