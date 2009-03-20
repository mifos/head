/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.framework.security.util;

import java.util.Date;
import java.util.Set;

public class PersonRoles {
	/**
	 * This would hold the personnel id
	 */
    private	Short id;
    /**
     * This would hold the personnel login name 
     */
    private	String loginName;
    /**
     * This would hold the personnel display name 
     */
    private	String displayName;
    /**
     * This would hold the no tries made by user 
     */
    private	Short noOfTries;
    /**
     * This would hold the user password in encrypted form
     */
    private	byte[] password;
    /**
     * This would hold whether user password has been changed or not
     */
    private	Short passwordChanged;
    /**
     * This would hold the user perfered locale
     */
    private	Short preferedLocale;
    /**
     * This would hold the set of roles associated with the user
     */
    private	Set roles;
    /**
     * This would hold the user id
     */
    private	Short officeid;
    /**
     * This would hold the personnel status whether he is active or inactive
     */
    private	Short personnelStatus;
    /**
     * This would hold the personnel level id
     */
    private	Short levelId;
    /**
     * This would hold the personnel last login date 
     */
    private	Date lastLogin ;
    /**
     * This would hold whether personnel is locked or not
     */
    private	Short locked ;
	/**
	 * This Function returns the locked
	 * @return Returns the locked.
	 */
	public Short getLocked() {
		return locked;
	}
	/**
	 * This function set the locked
	 * @param locked The locked to set.
	 */
	public void setLocked(Short locked) {
		this.locked = locked;
	}
	/**
	 * This Function returns the personnelStatus
	 * @return Returns the personnelStatus.
	 */
	public Short getPersonnelStatus() {
		return personnelStatus;
	}
	/**
	 * This function set the personnelStatus
	 * @param personnelStatus The personnelStatus to set.
	 */
	public void setPersonnelStatus(Short personnelStatus) {
		this.personnelStatus = personnelStatus;
	}
	/**
	 * This Function returns the officeid
	 * @return Returns the officeid.
	 */
	public Short getOfficeid() {
		return officeid;
	}
	/**
	 * This function set the officeid
	 * @param officeid The officeid to set.
	 */
	public void setOfficeid(Short officeid) {
		this.officeid = officeid;
	}
	/**
	 * 
	 */
	public PersonRoles() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * This Function returns the displayName
	 * @return Returns the displayName.
	 */
	public String getDisplayName() {
		return displayName;
	}
	/**
	 * This function set the displayName
	 * @param displayName The displayName to set.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	/**
	 * This Function returns the id
	 * @return Returns the id.
	 */
	public Short getId() {
		return id;
	}
	/**
	 * This function set the id
	 * @param id The id to set.
	 */
	public void setId(Short id) {
		this.id = id;
	}
	/**
	 * This Function returns the loginName
	 * @return Returns the loginName.
	 */
	public String getLoginName() {
		return loginName;
	}
	/**
	 * This function set the loginName
	 * @param loginName The loginName to set.
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	/**
	 * This Function returns the noOfTries
	 * @return Returns the noOfTries.
	 */
	public Short getNoOfTries() {
		return noOfTries;
	}
	/**
	 * This function set the noOfTries
	 * @param noOfTries The noOfTries to set.
	 */
	public void setNoOfTries(Short noOfTries) {
		this.noOfTries = noOfTries;
	}
	/**
	 * This Function returns the password
	 * @return Returns the password.
	 */
	public byte[] getPassword() {
		return password;
	}
	/**
	 * This function set the password
	 * @param password The password to set.
	 */
	public void setPassword(byte[] password) {
		this.password = password;
	}
	/**
	 * This Function returns the passwordChanged
	 * @return Returns the passwordChanged.
	 */
	public Short getPasswordChanged() {
		return passwordChanged;
	}
	/**
	 * This function set the passwordChanged
	 * @param passwordChanged The passwordChanged to set.
	 */
	public void setPasswordChanged(Short passwordChanged) {
		this.passwordChanged = passwordChanged;
	}
	/**
	 * This Function returns the preferedLocale
	 * @return Returns the preferedLocale.
	 */
	public Short getPreferedLocale() {
		return preferedLocale;
	}
	/**
	 * This function set the preferedLocale
	 * @param preferedLocale The preferedLocale to set.
	 */
	public void setPreferedLocale(Short preferedLocale) {
		this.preferedLocale = preferedLocale;
	}
	/**
	 * This Function returns the roles
	 * @return Returns the roles.
	 */
	public Set getRoles() {
		return roles;
	}
	/**
	 * This function set the roles
	 * @param roles The roles to set.
	 */
	public void setRoles(Set roles) {
		this.roles = roles;
	}
	/**
	 * This Function returns the levelId
	 * @return Returns the levelId.
	 */
	public Short getLevelId() {
		return levelId;
	}
	/**
	 * This function set the levelId
	 * @param levelId The levelId to set.
	 */
	public void setLevelId(Short levelId) {
		this.levelId = levelId;
	}
	/**
	 * This Function returns the lastLogin
	 * @return Returns the lastLogin.
	 */
	public Date getLastLogin() {
		return lastLogin;
	}
	/**
	 * This function set the lastLogin
	 * @param lastLogin The lastLogin to set.
	 */
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
}
