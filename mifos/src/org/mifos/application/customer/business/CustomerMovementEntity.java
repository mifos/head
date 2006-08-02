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

package org.mifos.application.customer.business;

import java.util.Date;

import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.util.helpers.Status;
import org.mifos.framework.business.PersistentObject;

/**
 * @author sumeethaec
 * 
 */
public class CustomerMovementEntity extends PersistentObject {

	private final Integer customerMovementId;

	private Short status;

	private final Date startDate;
	
	private Date endDate;

	private final CustomerBO customer;

	private final OfficeBO office;

	private final PersonnelBO personnel;

	/*
	 * Adding a default constructor is hibernate's requirement and should not be
	 * used to create a valid Object.
	 */
	protected CustomerMovementEntity(){
		this.customerMovementId = null;
		this.customer = null;
		this.office = null;
		this.personnel = null;
		this.startDate = null;
	}
	
	public CustomerMovementEntity(CustomerBO customer, PersonnelBO personnel, Date startDate){
		this.customer = customer;
		this.office = customer.getOffice();
		this.personnel = personnel;
		this.startDate = startDate;
		this.customerMovementId = null;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public OfficeBO getOffice() {
		return office;
	}

	public PersonnelBO getPersonnel() {
		return personnel;
	}
	
	public void updateStatus(Status status){
		this.status = status.getValue();
	}

	public boolean isActive(){
		return status.equals(Status.ACTIVE.getValue());
	}
}
