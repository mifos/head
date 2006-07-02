/**

* CustomerMovement.java   version: 1.0



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

import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author sumeethaec
 *
 */
public class CustomerMovement extends ValueObject{
	private Integer customerMovementId;
	private Short status;
	private Date startDate;
	private Date endDate;
	private Short updatedBy;
	private Date updatedDate;
	private Customer customer;
	private Office office;
	private Personnel personnel;
	/**
	 * Method which returns the customer	
	 * @return Returns the customer.
	 */
	public Customer getCustomer() {
		return customer;
	}
	/**
	 * Method which sets the customer
	 * @param customer The customer to set.
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	/**
	 * Method which returns the endDate	
	 * @return Returns the endDate.
	 */
	public Date getEndDate() {
		return endDate;
	}
	/**
	 * Method which sets the endDate
	 * @param endDate The endDate to set.
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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
	 * Method which returns the personnel	
	 * @return Returns the personnel.
	 */
	public Personnel getPersonnel() {
		return personnel;
	}
	/**
	 * Method which sets the personnel
	 * @param personnel The personnel to set.
	 */
	public void setPersonnel(Personnel personnel) {
		this.personnel = personnel;
	}
	/**
	 * Method which returns the startDate	
	 * @return Returns the startDate.
	 */
	public Date getStartDate() {
		return startDate;
	}
	/**
	 * Method which sets the startDate
	 * @param startDate The startDate to set.
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	/**
	 * Method which returns the status	
	 * @return Returns the status.
	 */
	public Short getStatus() {
		return status;
	}
	/**
	 * Method which sets the status
	 * @param status The status to set.
	 */
	public void setStatus(Short status) {
		this.status = status;
	}
	/**
	 * Method which returns the updatedDate	
	 * @return Returns the updatedDate.
	 */
	public Date getUpdatedDate() {
		return updatedDate;
	}
	/**
	 * Method which sets the updatedDate
	 * @param updatedDate The updatedDate to set.
	 */
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	/**
	 * Method which returns the updatedBy	
	 * @return Returns the updatedBy.
	 */
	public Short getUpdatedBy() {
		return updatedBy;
	}
	/**
	 * Method which sets the updatedBy
	 * @param updatedBy The updatedBy to set.
	 */
	public void setUpdatedBy(Short updatedBy) {
		this.updatedBy = updatedBy;
	}
	/**
	 * Method which returns the customerMovementId	
	 * @return Returns the customerMovementId.
	 */
	public Integer getCustomerMovementId() {
		return customerMovementId;
	}
	/**
	 * Method which sets the customerMovementId
	 * @param customerMovementId The customerMovementId to set.
	 */
	public void setCustomerMovementId(Integer customerMovementId) {
		this.customerMovementId = customerMovementId;
	}
	
}
