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

public class UserContext implements Serializable{

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
	/**
	 * This function returns the officeLevelId
	 * @return Returns the officeLevelId.
	 */
	
	public Short getOfficeLevelId() {
		return officeLevelId;
	}

	/**
	 * This function sets the officeLevelId
	 * @param officeLevelId the officeLevelId to set.
	 */
	
	public void setOfficeLevelId(Short officeLevelId) {
		this.officeLevelId = officeLevelId;
	}

	/**
	 * @return Returns the passwordChanged.
	 */
	public Short getPasswordChanged() {
		return passwordChanged;
	}

	/**
	 * @param passwordChanged
	 *            The passwordChanged to set.
	 */
	public void setPasswordChanged(Short passwordChanged) {
		this.passwordChanged = passwordChanged;
	}

	/**
	 * This Function returns the lastLogin
	 * 
	 * @return Returns the lastLogin.
	 */
	public Date getLastLogin() {
		return lastLogin;
	}

	/**
	 * This function set the lastLogin
	 * 
	 * @param lastLogin
	 *            The lastLogin to set.
	 */
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	/**
	 * This Function returns the levelId
	 * 
	 * @return Returns the levelId.
	 */
	public Short getLevelId() {
		return levelId;
	}

	/**
	 * This function set the levelId
	 * 
	 * @param levelId
	 *            The levelId to set.
	 */
	public void setLevelId(Short levelId) {
		this.levelId = levelId;
	}

	/**
	 * This Function returns the branchId
	 * 
	 * @return Returns the branchId.
	 */
	public Short getBranchId() {
		return branchId;
	}

	/**
	 * This function set the branchId
	 * 
	 * @param branchId
	 *            The branchId to set.
	 */
	public void setBranchId(Short branchId) {
		this.branchId = branchId;
	}

	/**
	 * @return This method returns the id.
	 */
	public Short getId() {
		return id;
	}

	/**
	 * @param id
	 *            This method set the id .
	 */
	public void setId(Short id) {
		this.id = id;
	}

	/**
	 * @return This method returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            This method set the name .
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return This method returns the roles.
	 */
	public Set getRoles() {
		return roles;
	}

	/**
	 * @param roles
	 *            This method set the roles .
	 */
	public void setRoles(Set roles) {
		this.roles = roles;
	}

	/**
	 * This function returns the branchGlobalNum
	 * 
	 * @return Returns the branchGlobalNum.
	 */
	public String getBranchGlobalNum() {
		return branchGlobalNum;
	}

	/**
	 * This function sets the branchGlobalNum
	 * 
	 * @param branchGlobalNum
	 *            The branchGlobalNum to set.
	 */
	public void setBranchGlobalNum(String branchGlobalNum) {
		this.branchGlobalNum = branchGlobalNum;
	}

	/**
	 * This function returns the localeId
	 * 
	 * @return Returns the localeId.
	 */
	public Short getLocaleId() {
		return localeId;
	}

	/**
	 * This function sets the localeId
	 * 
	 * @param localeId
	 *            The localeId to set.
	 */
	public void setLocaleId(Short localeId) {
		this.localeId = localeId;
	}

	/**
	 * This function returns the pereferedLocale
	 * 
	 * @return Returns the pereferedLocale.
	 */
	public Locale getPereferedLocale() {
		return pereferedLocale;
	}

	/**
	 * This function sets the pereferedLocale
	 * 
	 * @param pereferedLocale
	 *            The pereferedLocale to set.
	 */
	public void setPereferedLocale(Locale pereferedLocale) {
		this.pereferedLocale = pereferedLocale;
	}

	/**
	 * This function returns the userGlobalNo
	 * 
	 * @return Returns the userGlobalNo.
	 */
	public String getUserGlobalNo() {
		return userGlobalNo;
	}

	/**
	 * This function sets the userGlobalNo
	 * 
	 * @param userGlobalNo
	 *            The userGlobalNo to set.
	 */
	public void setUserGlobalNo(String userGlobalNo) {
		this.userGlobalNo = userGlobalNo;
	}

	/**
	 * This function returns the mfiLocaleId
	 * 
	 * @return Returns the mfiLocaleId.
	 */

	public Short getMfiLocaleId() {
		return mfiLocaleId;
	}

	/**
	 * This function sets the mfiLocaleId
	 * 
	 * @param mfiLocaleId
	 *            the mfiLocaleId to set.
	 */

	public void setMfiLocaleId(Short mfiLocaleId) {
		this.mfiLocaleId = mfiLocaleId;
	}

	/**
	 * This function returns the mfiLocale
	 * @return Returns the mfiLocale.
	 */
	
	public Locale getMfiLocale() {
		return mfiLocale;
	}

	/**
	 * This function sets the mfiLocale
	 * @param mfiLocale the mfiLocale to set.
	 */
	
	public void setMfiLocale(Locale mfiLocale) {
		this.mfiLocale = mfiLocale;
	}
}
