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

import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.io.Serializable;

/**
 * Information about a user, including ID's of their roles.
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

	/**
	 * This would hold the user globel no
	 */
	private String userGlobalNo;

	/**
	 * This would hold the set of roles id's associated with the user
	 */
	private Set roles;

	/**
	 * This would hold the branchid of the user
	 */
	private Short branchId;

	/**
	 * This would hold the branch globel no
	 */
	private String branchGlobalNum;

	/**
	 * This would hold the level Id of the user
	 */
	private Short levelId;

	/**
	 * This would hold the User locale id
	 */
	private Short localeId;

	/**
	 * This would hold the perefered locale of the user
	 */
	private Locale pereferedLocale;

	/**
	 * This would hold the MFI locale
	 */
	private Short mfiLocaleId;
	
	/**
	 * This would hold the mfi loacle
	 */

	private Locale mfiLocale;
	/**
	 * This would hold the last login time of the user
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

	public void setLevelId(Short levelId) {
		this.levelId = levelId;
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

	public Locale getPereferedLocale() {
		return pereferedLocale;
	}

	public void setPereferedLocale(Locale pereferedLocale) {
		this.pereferedLocale = pereferedLocale;
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
