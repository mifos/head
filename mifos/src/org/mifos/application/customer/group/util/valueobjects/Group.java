/**

 * Group.java    version: 1.0



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

package org.mifos.application.customer.group.util.valueobjects;

import java.util.Set;

import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.valueobjects.Customer;

/**
 * This class denotes the Group (row in customer table) object and all attributes associated with it.
 * It has a composition of other objects like Custom fields, fees, personnel etc., since it inherits from Customer
 * @author navitas
 */
public class Group extends Customer {
	
	/**Denotes the flag for the group */
	private Short flagId;
	
	/**Denotes the parent customer of the group (if any)*/
	private Customer parentCustomer;
	
	/**Denotes the programs assigned to the group
	private Set customerProgram;
	*/
	/** Constructor for the Group */
	public Group(){
	}
	
	/**
	 * This method returns the name by which a Group object will be stored in the context object. Framework will set
	 * group object to request with this name. 
	 * @return The name by which the group object can be referred to
	 */
	public String getResultName(){
		return GroupConstants.GROUPVO;
	}
	
	/**
     * Return the value of the flagId attribute.
     * @return Short
     */
	public Short getFlagId() {
		return flagId;
	}
	
	/**
     * Sets the value of flagId
     * @param flagId
     */
	public void setFlagId(Short flagId) {
		this.flagId = flagId;
	}
	
	/**
     * Return the value of the customerProgram, that user has selected
     * @return Set
     *
	public Set getCustomerProgram() {
		return customerProgram;
	}
	
	/**
     * Sets the value of customerProgram
     * @param customerProgram
     *
	public void setCustomerProgram(Set customerProgram) {
		this.customerProgram = customerProgram;
	}
	*/
	/**
     * Sets the value of parentCustomer
     * @param parentCustomer
     */
	public void setParentCustomer(Customer parentCustomer)
	{
		this.parentCustomer = parentCustomer;	
	}
	
	/**
     * Return the value of the parentCustomer attribute.
     * @return Customer
     */
	public Customer getParentCustomer()
	{
	   return parentCustomer;	
	}
	
}