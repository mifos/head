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

package org.mifos.application.personnel.business;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.mifos.application.master.util.valueobjects.SupportedLocales;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.struts.plugin.helper.EntityMasterConstants;

/**
 * This is the personnel valueobject which would be persisted in the database.
 * This is has got associations with other objects which are present in it as
 * composition.
 * 
 * @author ashishsm
 * 
 */
public class PersonnelBO extends BusinessObject {

	private Short personnelId;

	private PersonnelLevelEntity level;

	private String globalPersonnelNum;

	private OfficeBO office;

	private Integer title;

	private String displayName;

	private Short personnelStatus;

	private Date dob;

	private Date dateOfJoiningMFI;

	private SupportedLocales preferredLocale;

	private String searchId;

	private Integer maxChildCount;

	private String password;

	private byte[] encriptedPassword;

	private String userName;

	private String emailId;

	private Short passwordChanged;

	private Date lastLogin;

	private Short locked;

	private Short noOfTries;

	private PersonnelDetailsEntity personnelDetails;

	private Set<PersonnelRoleEntity> personnelRoles;

	private Set<PersonnelCustomFieldView> customFields;

	public PersonnelBO() {
		this.level = new PersonnelLevelEntity();
		this.personnelDetails = new PersonnelDetailsEntity();
		this.preferredLocale = new SupportedLocales();
		this.customFields = new HashSet<PersonnelCustomFieldView>();
	}

	public Set<PersonnelCustomFieldView> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Set<PersonnelCustomFieldView> customFields) {
		this.customFields = customFields;
	}
	public void setCustomField(PersonnelCustomFieldView customField) {
		if( null!=customField)
		{
			customField.setPersonnelId(this.personnelId);
			this.customFields.add(customField);
		}
		
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getGlobalPersonnelNum() {
		return globalPersonnelNum;
	}

	public void setGlobalPersonnelNum(String globalPersonnelNum) {
		this.globalPersonnelNum = globalPersonnelNum;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public PersonnelLevelEntity getLevel() {
		return level;
	}

	public void setLevel(PersonnelLevelEntity level) {
		this.level = level;
	}

	public Integer getMaxChildCount() {
		return maxChildCount;
	}

	public void setMaxChildCount(Integer maxChildCount) {
		this.maxChildCount = maxChildCount;
	}

	public OfficeBO getOffice() {
		return office;
	}

	public void setOffice(OfficeBO office) {
		this.office = office;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private Short getPasswordChanged() {
		return passwordChanged;
	}

	private void setPasswordChanged(Short passwordChanged) {
		this.passwordChanged = passwordChanged;
	}

	public boolean isPasswordChange(){
		return this.passwordChanged>0;
	}
	public void addPasswordChange(boolean passwordChanged)
	{
		setPasswordChanged((passwordChanged?Short.valueOf("1"):Short.valueOf("0")));
	}
	public void alterPasswordChange(boolean passwordChanged){
		setPasswordChanged((passwordChanged?Short.valueOf("1"):Short.valueOf("0")));
	}
	
	public PersonnelDetailsEntity getPersonnelDetails() {
		return personnelDetails;
	}

	public void setPersonnelDetails(PersonnelDetailsEntity personnelDetails) {
		this.personnelDetails = personnelDetails;
	}

	public Short getPersonnelId() {
		return personnelId;
	}

	public void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}

	public Short getPersonnelStatus() {
		return personnelStatus;
	}

	public void setPersonnelStatus(Short personnelStatus) {
		this.personnelStatus = personnelStatus;
	}

	public SupportedLocales getPreferredLocale() {
		return preferredLocale;
	}

	public void setPreferredLocale(SupportedLocales preferredLocale) {
		this.preferredLocale = preferredLocale;
	}

	public String getSearchId() {
		return searchId;
	}

	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}

	public Integer getTitle() {
		return title;
	}

	public void setTitle(Integer title) {
		this.title = title;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	private Short getLocked() {
		return locked;
	}

	private void setLocked(Short locked) {
		this.locked = locked;
	}
	
	public boolean isLock(){
		return this.locked>0;
	}
	public void addLock(boolean lock ){
		setLocked((lock?Short.valueOf("1"):Short.valueOf("0")));
	}
	

	public Short getNoOfTries() {
		return noOfTries;
	}

	public void setNoOfTries(Short noOfTries) {
		this.noOfTries = noOfTries;
	}

	public byte[] getEncriptedPassword() {
		return encriptedPassword;
	}

	public void setEncriptedPassword(byte[] encriptedPassword) {
		this.encriptedPassword = encriptedPassword;
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

	public Set<PersonnelRoleEntity> getPersonnelRoles() {
		return personnelRoles;
	}

	public void setPersonnelRoles(Set<PersonnelRoleEntity> personnelRoles) {
		this.personnelRoles = personnelRoles;
	}

	@Override
	public Short getEntityID() {
		return EntityMasterConstants.Personnel;
	}
}
