/**

 * UserContext.java    version: 1

 

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

package org.mifos.framework.security.util;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.mifos.application.personnel.util.helpers.PersonnelLevel;

/**
 * Information about a user, including ID's of their roles.
 * 
 * Accessed from jsp's.
 */
public class UserContext implements Serializable {
	
	/**
	 * This would hold the name of the user who has loged in
	 */
	private String name;

	/**
	 * This would hold the id of the the user
	 */
	private Short id;

	private String userGlobalNo;

	/**
	 * Set of roles id's associated with the user
	 */
	private Set roles;

	private Short branchId;

	private String branchGlobalNum;

	private Short levelId;

	private Short localeId;

	private Locale preferredLocale;

	private Short mfiLocaleId;
	
	private Locale mfiLocale;

	/**
	 * Last login time of the user
	 */
	private Date lastLogin;

	/**
	 * This would hold whether user password has been changed or not
	 */
	private Short passwordChanged;

	/**
	 * This would hold the levelId of the loggen in user 
	 */
	private Short officeLevelId;
	
	public UserContext() {
	}
	
	public UserContext(Locale preferredLocale) {
		this.preferredLocale = preferredLocale;
	}

	public Short getOfficeLevelId() {
		return officeLevelId;
	}

	public void setOfficeLevelId(Short officeLevelId) {
		this.officeLevelId = officeLevelId;
	}

	public Short getPasswordChanged() {
		return passwordChanged;
	}

	public void setPasswordChanged(Short passwordChanged) {
		this.passwordChanged = passwordChanged;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Short getLevelId() {
		return levelId;
	}
	
	public PersonnelLevel getLevel() {
		return PersonnelLevel.fromInt(levelId);
	}

	public void setLevelId(Short levelId) {
		this.levelId = levelId;
	}
	
	public void setLevel(PersonnelLevel level) {
		setLevelId(level.getValue());
	}

	public Short getBranchId() {
		return branchId;
	}

	public void setBranchId(Short branchId) {
		this.branchId = branchId;
	}

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set getRoles() {
		return roles;
	}

	public void setRoles(Set roles) {
		this.roles = roles;
	}

	public String getBranchGlobalNum() {
		return branchGlobalNum;
	}

	public void setBranchGlobalNum(String branchGlobalNum) {
		this.branchGlobalNum = branchGlobalNum;
	}

	public Short getLocaleId() {
		return localeId;
	}

	public void setLocaleId(Short localeId) {
		this.localeId = localeId;
	}

	public Locale getPreferredLocale() {
		return preferredLocale;
	}

	public void setPreferredLocale(Locale preferredLocale) {
		this.preferredLocale = preferredLocale;
	}

	public String getUserGlobalNo() {
		return userGlobalNo;
	}

	public void setUserGlobalNo(String userGlobalNo) {
		this.userGlobalNo = userGlobalNo;
	}

	public Short getMfiLocaleId() {
		return mfiLocaleId;
	}

	public void setMfiLocaleId(Short mfiLocaleId) {
		this.mfiLocaleId = mfiLocaleId;
	}

	public Locale getMfiLocale() {
		return mfiLocale;
	}

	public void setMfiLocale(Locale mfiLocale) {
		this.mfiLocale = mfiLocale;
	}

}
