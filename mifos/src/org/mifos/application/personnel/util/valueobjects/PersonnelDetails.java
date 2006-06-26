/**

 * PersonnelDetails.java    version: xxx

 

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

package org.mifos.application.personnel.util.valueobjects;

import java.sql.Date;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This obect has values for extra fields of a personnel.  
 * @author ashishsm
 *
 */
public class PersonnelDetails extends ValueObject{

	/**
     * Simple constructor of Personnel instances.
     */
	public PersonnelDetails() {
		super();
		
	}
	/**Denotes the first name of the user */
	private String firstName;
	
	/**Denotes the middle name of the user */
	private String middleName;
	
	/**Denotes the second last name of the user */
	private String secondLastName;
	
	/**Denotes the last name of the user */
	private String lastName;
	
	/**Denotes the governemtnt id number of the user */
	private String governmentIdNumber;
	
	/**Denotes the date of birth of the user */
	private Date dob;
	
	/**Denotes the marital status of the user */
	private Integer maritalStatus;
	
	/**Denotes the gender of the user */
	private Integer gender;
	
	/**Denotes the date on which user has joined MFI */
	private Date dateOfJoiningMFI;
	
	/**Denotes the date on which user has joined branch. It is system generated date */
	private Date dateOfJoiningBranch;
	
	/**Denotes the date on which user has transferred to other branch. It is system generated date */
	private Date dateOfLeavingBranch;
	
	/**Denotes the line1 for the address field */
	private String address1;
	
	/**Denotes the line2 for the address field */
	private String address2;
	
	/**Denotes the line3 for the address field */
	private String address3;
	
	/**Denotes the city of the user address */
	private String city;
	
	/**Denotes the state of the user address*/
	private String state;
	
	/**Denotes the country of the user address*/
	private String country;
	
	/**Denotes the postalCode of the user address*/
	private String postalCode;
	
	/**Denotes the telephone of the user address*/
	private String telephone;
	
	/**Denotes the personnelId of the user, That is obtained from personnel object.*/
	private Short personnelId;
	
	/**Denotes the personnel composition in personnel details object*/
	private Personnel personnel;

	/**
     * Return the personnel object.
     * @return Personnel
     */
	public Personnel getPersonnel() {
		return personnel;
	}

	/**
     * Sets the personnel object
     * @param personnel
     */
	public void setPersonnel(Personnel personnel) {
		this.personnel = personnel;
	}

	/**
     * Return the value of the address1.
     * @return String
     */
	public String getAddress1() {
		return address1;
	}

	/**
     * Sets the value of address1
     * @param address1
     */
	public void setAddress1(String address1) {
		this.address1 = address1;
	}


	/**
     * Return the value of the address2.
     * @return String
     */
	public String getAddress2() {
		return address2;
	}


	/**
     * Sets the value of address2
     * @param address2
     */
	public void setAddress2(String address2) {
		this.address2 = address2;
	}


	/**
     * Return the value of the address3.
     * @return String
     */
	public String getAddress3() {
		return address3;
	}


	/**
     * Sets the value of address3
     * @param address3
     */
	public void setAddress3(String address3) {
		this.address3 = address3;
	}


	/**
     * Return the value of the city.
     * @return String
     */
	public String getCity() {
		return city;
	}


	/**
     * Sets the value of city
     * @param city
     */
	public void setCity(String city) {
		this.city = city;
	}

	/**
     * Return the value of the country.
     * @return String
     */
	public String getCountry() {
		return country;
	}


	/**
     * Sets the value of country
     * @param country
     */
	public void setCountry(String country) {
		this.country = country;
	}


	/**
     * Return the value of the dateOfJoiningBranch.
     * @return Date
     */
	public Date getDateOfJoiningBranch() {
		return dateOfJoiningBranch;
	}


	/**
     * Sets the value of dateOfJoiningBranch
     * @param dateOfJoiningBranch
     */
	public void setDateOfJoiningBranch(Date dateOfJoiningBranch) {
		this.dateOfJoiningBranch = dateOfJoiningBranch;
	}


	/**
     * Return the value of the dateOfLeavingBranch.
     * @return Date
     */
	public Date getDateOfLeavingBranch() {
		return dateOfLeavingBranch;
	}


	/**
     * Sets the value of dateOfLeavingBranch
     * @param dateOfLeavingBranch
     */
	public void setDateOfLeavingBranch(Date dateOfLeavingBranch) {
		this.dateOfLeavingBranch = dateOfLeavingBranch;
	}


	/**
	 * Returns the dob.
	 * @return Date
	 */
	public Date getDob() {
		return dob;
	}
	
	/**
	 * Sets the value of dob. This value gets store in database.
	 * @param dob 
	 */
	public void setDob(Date dob) {
		this.dob = dob;
	}
	
	/**
	 * Returns the dateOfJoiningMFI.
	 * @return Date
	 */
	public Date getDateOfJoiningMFI() {
		return dateOfJoiningMFI;
	}
	
	/**
	 * Sets the value of dateOfJoiningMFI.This value gets store in database.
	 * @param dateOfJoiningMFI 
	 */
	public void setDateOfJoiningMFI(Date dateOfJoiningMFI) {
		this.dateOfJoiningMFI = dateOfJoiningMFI;
	}

	/**
     * Return the value of the firstName.
     * @return String
     */
	public String getFirstName() {
		return firstName;
	}

	/**
     * Sets the value of firstName
     * @param firstName
     */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
     * Return the value of the gender.
     * @return Integer
     */
	public Integer getGender() {
		return gender;
	}

	/**
     * Sets the value of gender
     * @param gender
     */
	public void setGender(Integer gender) {
		this.gender = gender;
	}

	/**
     * Return the value of the governmentIdNumber.
     * @return String
     */
	public String getGovernmentIdNumber() {
		return governmentIdNumber;
	}

	/**
     * Sets the value of governmentIdNumber
     * @param governmentIdNumber
     */
	public void setGovernmentIdNumber(String governmentIdNumber) {
		this.governmentIdNumber = governmentIdNumber;
	}

	/**
     * Return the value of the lastName.
     * @return String
     */
	public String getLastName() {
		return lastName;
	}

	/**
     * Sets the value of lastName
     * @param lastName
     */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
     * Return the value of the maritalStatus.
     * @return Integer
     */
	public Integer getMaritalStatus() {
		return maritalStatus;
	}

	/**
     * Sets the value of maritalStatus
     * @param maritalStatus
     */
	public void setMaritalStatus(Integer maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	/**
     * Return the value of the middleName.
     * @return String
     */
	public String getMiddleName() {
		return middleName;
	}

	/**
     * Sets the value of middleName
     * @param middleName
     */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
     * Return the value of the personnelId.
     * @return Short
     */
	public Short getPersonnelId() {
		return personnelId;
	}

	/**
     * Sets the value of personnelId
     * @param personnelId
     */
	public void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}

	/**
     * Return the value of the postalCode.
     * @return String
     */
	public String getPostalCode() {
		return postalCode;
	}

	/**
     * Sets the value of postalCode
     * @param postalCode
     */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
     * Return the value of the secondLastName.
     * @return String
     */
	public String getSecondLastName() {
		return secondLastName;
	}

	/**
     * Sets the value of secondLastName
     * @param secondLastName
     */
	public void setSecondLastName(String secondLastName) {
		this.secondLastName = secondLastName;
	}

	/**
     * Return the value of the state.
     * @return String
     */
	public String getState() {
		return state;
	}

	/**
     * Sets the value of state
     * @param state
     */
	public void setState(String state) {
		this.state = state;
	}

	/**
     * Return the value of the telephone.
     * @return String
     */
	public String getTelephone() {
		return telephone;
	}

	/**
     * Sets the value of telephone
     * @param telephone
     */
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	  
}
