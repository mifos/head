
/**

 * CustomerPositionDisplay.java    version: xxx



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

import java.io.Serializable;

/**
* A class that is used for displaying customer positions
*/
public class CustomerPositionDisplay implements Serializable
{
	private Integer customerId;
	private String positionName;
	private String customerName;
	private Integer positionId;
	
	 /**
	  * Return the  customer name
	  * @return String
	  */
	public String getCustomerName()
	{
		return customerName;
	}
	 /**
      * Sets the customer name
	  * @param customerName
	  */
	public void setCustomerName(String customerName)
	{
		this.customerName = customerName;
	}
	
	 /**
	  * Return the  position name
	  * @return String
	  */	
	public String getPositionName()
	{
		return positionName;
	}
	/**
	 * Sets the position name
	 * @param positionName
	 */
	public void setPositionName(String positionName)
	{
		this.positionName = positionName;
	}
	
	/**
	 * Return the  customerId
	 * @return String
	 */
	public Integer getCustomerId() {
		return customerId;
	}
	/**
     * Sets the customerId 
     * @param customerId
     */
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	/**
     * gets the positionId 
     * @param positionId
     */
	public Integer getPositionId() {
		return positionId;
	}
	/**
     * Sets the positionId 
     * @param positionId
     */
	public void setPositionId(Integer positionId) {
		this.positionId = positionId;
	}
}
