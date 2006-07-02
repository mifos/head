/**

 * PersonnelDetails.java    version: xxx

 

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

package org.mifos.application.personnel.business;

import java.util.Date;

import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;

/**
 * This obect has values for extra fields of a personnel.
 * 
 * @author ashishsm
 * 
 */
public class PersonnelDetailsEntity extends PersistentObject {


	private Name name;
	
	private String governmentIdNumber;

	private Date dob;

	private Integer maritalStatus;

	private Integer gender;

	private Date dateOfJoiningMFI;

	private Date dateOfJoiningBranch;

	private Date dateOfLeavingBranch;


	private Short personnelId;

	private PersonnelBO personnel;
	
	private Address address;

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public PersonnelDetailsEntity() {
		super();

	}

	public PersonnelBO getPersonnel() {
		return personnel;
	}

	public void setPersonnel(PersonnelBO personnel) {
		this.personnel = personnel;
	}

	public Date getDateOfJoiningBranch() {
		return dateOfJoiningBranch;
	}

	public void setDateOfJoiningBranch(Date dateOfJoiningBranch) {
		this.dateOfJoiningBranch = dateOfJoiningBranch;
	}

	public Date getDateOfLeavingBranch() {
		return dateOfLeavingBranch;
	}

	public void setDateOfLeavingBranch(Date dateOfLeavingBranch) {
		this.dateOfLeavingBranch = dateOfLeavingBranch;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public Date getDateOfJoiningMFI() {
		return dateOfJoiningMFI;
	}

	public void setDateOfJoiningMFI(Date dateOfJoiningMFI) {
		this.dateOfJoiningMFI = dateOfJoiningMFI;
	}


	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getGovernmentIdNumber() {
		return governmentIdNumber;
	}

	public void setGovernmentIdNumber(String governmentIdNumber) {
		this.governmentIdNumber = governmentIdNumber;
	}


	public Integer getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(Integer maritalStatus) {
		this.maritalStatus = maritalStatus;
	}


	public Short getPersonnelId() {
		return personnelId;
	}

	public void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

}
