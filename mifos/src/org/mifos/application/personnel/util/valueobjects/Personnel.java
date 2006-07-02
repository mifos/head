/**
 
 * Personnel.java    version: xxx
 
 
 
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

package org.mifos.application.personnel.util.valueobjects;


import java.util.HashSet;
import java.util.Set;
import java.sql.Date;

import org.mifos.framework.util.valueobjects.ValueObject;

import org.mifos.application.master.util.valueobjects.SupportedLocales;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;

	
/**
 * This is the personnel valueobject which would be persisted in the database.
 * This is has got associations with other objects which are present in it as composition.
 * @author ashishsm
 *
 */
public class Personnel extends ValueObject {
	
	/**Denotes the unique identifier for personnel which is an auto generated running number */
	private Short personnelId;
	
	/**Denotes the level of the personnel e.g. LoanOfficer or NON-LoanOfficer */
	private PersonnelLevel level;
	
	/**Denotes the systemId of the personnel */
	private String globalPersonnelNum;
	
	/** Denotes the office object to which this personnel belongs. */
	private Office office;
	
	/**Denotes the personnel title e.g. CFO/Accountant etc.*/
	private Integer title ;
	
	/**Denotes the name of the personnel that will be displayed on UI. It conctenates, first, second, secondlast
	 * and last name */
	private String displayName;
	
	/**Denotes the status id of the personnel.*/
	private Short personnelStatus;
	
	/**Denotes the date of birth  of the personnel. The date of birth attribute is saved in database from personne details.
	 * It is kept here to do easy conversion between action form and value object through date converter. */
	private Date dob;
	
	/**Denotes the date on which user joined MDI. The dateOfJoiningMFI attribute is saved in database from personne details.
	 * It is kept here to do easy conversion between action form and value object through date converter. */
	private Date dateOfJoiningMFI;
	
	/** The supportedlocales object which identifies the preferred locale of the user.*/
	private SupportedLocales preferredLocale;
	
	/**Denotes the searchId for the user. It will help in searching personnels in case of various hierarchy levels*/
	private String searchId;
	
	/**Denotes the number of childeren, that this personnel has, based on hierarchy. Presently not in use */
	private Integer maxChildCount;
	
	/**Denotes the password of the personnel */
	private String password;
	
	/**Denotes the encrypted password of the personnel */
	private byte[] encriptedPassword;
	
	/**Denotes the user login name */
	private String userName;
	
	/**Denotes the emailId for the user */
	private String emailId;
	
	/**It is a flag that denotes whether user password has been changed or not. It is for the first time change password */
	private Short passwordChanged;
	
	/**Denotes the personnel id who has created this user */
	private Short createdBy;
	
	/**Denotes the date on which this user has beeen created */
	private Date createdDate;
	
	/**Denotes the personnel id who has updated this user last*/
	private Short updatedBy;
	
	/**Denotes the date on which this user has beeen updated last */
	private Date updatedDate;
	
	/**Denotes the last login date of the user */
	private Date lastLogin;
	
	/**It is a flag that tells whether the user is locked or not*/
	private Short locked;
	
	/**Denotes the count of number of tries when user has failed to login */
	private Short noOfTries;
	
	/** The personnel details object for the personnel */
	private PersonnelDetails personnelDetails;
	
	/** Denotes set of roles a personnel is associated with. This is a one to many association with personnel roles 
	 *  as entity.
	 */
	private Set personnelRolesSet;
	
	/** Denotes The set of custom field values for personnel. */
   	private Set customFieldSet;

   	/**Denotes the version no for the personnel */
	private Integer versionNo;

	
	/**
     * Simple constructor of Personnel instances.
     */
	public Personnel() {
		this.level=new PersonnelLevel();
		this.personnelDetails = new PersonnelDetails();
		this.preferredLocale=new SupportedLocales();
		this.customFieldSet = new HashSet();
	}
	
	/**
	 * This method returns the name by which a Personnel object will be stored in the context object. 
	 * Framework will set personnel object to request with this name. 
	 * @return The name by which the personnel object can be referred to
	 */
	public String getResultName(){
		return PersonnelConstants.PERSONNEL_VO;
	}
	
	/**
	 * Returns the customFieldSet.
	 * @return Set
	 */
	public Set getCustomFieldSet() {
		return customFieldSet;
	}
	
	/**
	 * Sets the value of customFieldSet
	 * @param customFieldSet The customFieldSet to set.
	 */
	public void setCustomFieldSet(Set customFieldSet) {
		if(customFieldSet != null){
			for(Object obj : customFieldSet){
				((PersonnelCustomField)obj).setPersonnelId(personnelId);
			}
		}

		this.customFieldSet = customFieldSet;
	}

	/**
	 * Returns the customFieldSet.
	 * @return Short
	 */
	public Short getCreatedBy() {
		return createdBy;
	}
	
	/**
	 * Sets the value of createdBy
	 * @param createdBy
	 */
	public void setCreatedBy(Short createdBy) {
		this.createdBy = createdBy;
	}
	
	/**
	 * Returns the createdDate.
	 * @return Date
	 */
	public Date getCreatedDate() {
		return createdDate;
	}
	
	/**
	 * Sets the value of createdDate
	 * @param createdDate
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	/**
	 * Returns the emailId.
	 * @return String
	 */
	public String getEmailId() {
		return emailId;
	}
	
	/**
	 * Sets the value of emailId
	 * @param emailId
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
	/**
	 * Returns the globalPersonnelNum.
	 * @return String
	 */
	public String getGlobalPersonnelNum() {
		return globalPersonnelNum;
	}
	
	/**
	 * Sets the value of globalPersonnelNum
	 * @param globalPersonnelNum 
	 */
	public void setGlobalPersonnelNum(String globalPersonnelNum) {
		this.globalPersonnelNum = globalPersonnelNum;
	}
	
	/**
	 * Returns the lastLogin.
	 * @return Date
	 */
	public Date getLastLogin() {
		return lastLogin;
	}
	
	/**
	 * Sets the value of lastLogin
	 * @param lastLogin 
	 */
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
	
	/**
	 * Returns the level for the personnel.
	 * @return PersonnelLevel
	 */
	public PersonnelLevel getLevel() {
		return level;
	}
	
	/**
	 * Sets the value of level
	 * @param level 
	 */
	public void setLevel(PersonnelLevel level) {
		this.level = level;
	}
	
	/**
	 * Returns the maxChildCount.
	 * @return Integer
	 */
	public Integer getMaxChildCount() {
		return maxChildCount;
	}
	
	/**
	 * Sets the value of maxChildCount
	 * @param maxChildCount 
	 */
	public void setMaxChildCount(Integer maxChildCount) {
		this.maxChildCount = maxChildCount;
	}
	
	/**
	 * Returns the office of which user belongs.
	 * @return Office
	 */
	public Office getOffice() {
		return office;
	}
	
	/**
	 * Sets the value of office
	 * @param office 
	 */
	public void setOffice(Office office) {
		this.office = office;
	}
	
	/**
	 * Returns the password.
	 * @return String
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Sets the value of password
	 * @param password 
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Returns the passwordChanged.
	 * @return Short
	 */
	public Short getPasswordChanged() {
		return passwordChanged;
	}
	
	/**
	 * Sets the value of passwordChanged
	 * @param passwordChanged 
	 */
	public void setPasswordChanged(Short passwordChanged) {
		this.passwordChanged = passwordChanged;
	}
	
	/**
	 * Returns the personnelDetails.
	 * @return PersonnelDetails
	 */
	public PersonnelDetails getPersonnelDetails() {
		return personnelDetails;
	}
	
	/**
	 * Sets the value of personnelDetails
	 * @param personnelDetails 
	 */
	public void setPersonnelDetails(PersonnelDetails personnelDetails) {
		this.personnelDetails = personnelDetails;
	}
	
	/**
	 * Returns the personnelId.
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
	 * Returns the personnelRolesSet.
	 * @return Set
	 */
	public Set getPersonnelRolesSet() {
		return personnelRolesSet;
	}
	
	/**
	 * Sets the value of personnelRolesSet
	 * @param personnelRolesSet 
	 */
	public void setPersonnelRolesSet(Set personnelRolesSet) {
		this.personnelRolesSet = personnelRolesSet;
	}
	
	/**
	 * Returns the personnelStatus.
	 * @return Short
	 */
	public Short getPersonnelStatus() {
		return personnelStatus;
	}
	
	/**
	 * Sets the value of personnelStatus
	 * @param personnelStatus 
	 */
	public void setPersonnelStatus(Short personnelStatus) {
		this.personnelStatus = personnelStatus;
	}
	
	/**
	 * Returns the preferredLocale.
	 * @return SupportedLocales
	 */
	public SupportedLocales getPreferredLocale() {
		return preferredLocale;
	}
	
	/**
	 * Sets the value of preferredLocale
	 * @param preferredLocale 
	 */
	public void setPreferredLocale(SupportedLocales preferredLocale) {
		this.preferredLocale = preferredLocale;
	}
	
	/**
	 * Returns the searchId.
	 * @return String
	 */
	public String getSearchId() {
		return searchId;
	}
	
	/**
	 * Sets the value of searchId
	 * @param searchId 
	 */
	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}
	
	/**
	 * Returns the user title.
	 * @return Integer
	 */	
	public Integer getTitle() {
		return title;
	}
	
	/**
	 * Sets the value of title
	 * @param title 
	 */
	public void setTitle(Integer title) {
		this.title = title;
	}

	/**
	 * Returns the updatedBy.
	 * @return Short
	 */
	public Short getUpdatedBy() {
		return updatedBy;
	}
	
	/**
	 * Sets the value of updatedBy
	 * @param updatedBy 
	 */
	public void setUpdatedBy(Short updatedBy) {
		this.updatedBy = updatedBy;
	}
	
	/**
	 * Returns the updatedDate.
	 * @return Date
	 */
	public Date getUpdatedDate() {
		return updatedDate;
	}
	
	/**
	 * Sets the value of updatedDate
	 * @param updatedDate 
	 */
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	
	/**
	 * Returns the userName.
	 * @return String
	 */
	public String getUserName() {
		return userName;
	}
	
	/**
	 * Sets the value of userName
	 * @param userName 
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * Returns the displayName.
	 * @return String
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Sets the value of displayName
	 * @param displayName 
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	/**
	 * Returns the versionNo.
	 * @return Integer
	 */
	public Integer getVersionNo() {
		return versionNo;
	}

	/**
	 * Sets the value of versionNo
	 * @param versionNo 
	 */
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}
	
	/**
	 * Returns the locked.
	 * @return Short
	 */
	public Short getLocked() {
		return locked;
	}
	
	/**
	 * Sets the value of locked
	 * @param locked 
	 */
	public void setLocked(Short locked) {
		this.locked = locked;
	}
	
	/**
	 * Returns the noOfTries.
	 * @return Short
	 */
	public Short getNoOfTries() {
		return noOfTries;
	}
	
	/**
	 * Sets the value of noOfTries
	 * @param noOfTries 
	 */
	public void setNoOfTries(Short noOfTries) {
		this.noOfTries = noOfTries;
	}

	/**
	 * Returns the encriptedPassword.
	 * @return byte[]
	 */
	public byte[] getEncriptedPassword() {
		return encriptedPassword;
	}
	
	/**
	 * Sets the value of encriptedPassword
	 * @param encriptedPassword 
	 */
	public void setEncriptedPassword(byte[] encriptedPassword) {
		this.encriptedPassword = encriptedPassword;
	}
	
	/**
	 * Returns the dob.
	 * @return Date
	 */
	public Date getDob() {
		return dob;
	}
	
	/**
	 * Sets the value of dob
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
	 * Sets the value of dateOfJoiningMFI
	 * @param dateOfJoiningMFI 
	 */
	public void setDateOfJoiningMFI(Date dateOfJoiningMFI) {
		this.dateOfJoiningMFI = dateOfJoiningMFI;
	}
}
