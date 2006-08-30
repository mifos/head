/**

 * RoleSubObject.java    version: 1.0

 

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
package org.mifos.application.rolesandpermission.util.helpers;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This object would be used to keep the copy of some fields of the role object
 */
public class RoleSubObject extends ValueObject {

	private static final long serialVersionUID = 9992314235l;

	/**
	 * This would hold the Id of the role
	 */
	private Short roleId;

	/**
	 * This would hold the Name of the Role
	 */
	private String roleName;

	/**
	 * This would hold the version of the role
	 */
	private Integer version;

	/**
	 * @param roleId
	 * @param roleName
	 * @param version
	 */
	public RoleSubObject(Short roleId, String roleName, Integer version) {

		this.roleId = roleId;
		this.roleName = roleName;
		this.version = version;
	}

	/**
	 * This function returns the version
	 * 
	 * @return Returns the version.
	 */

	public Integer getVersion() {
		return version;
	}

	/**
	 * This function sets the version
	 * 
	 * @param version
	 *            the version to set.
	 */

	public void setVersion(Integer version) {
		this.version = version;
	}

	/**
	 * Two argument constructor
	 * 
	 * @param roleId
	 *            id of the role
	 * @param roleName
	 *            name of the role
	 */
	public RoleSubObject(Short roleId, String roleName) {

		this.roleId = roleId;
		this.roleName = roleName;
	}

	/**
	 * This function returns the roleId
	 * 
	 * @return Returns the roleId.
	 */
	public Short getRoleId() {
		return roleId;
	}

	/**
	 * This function sets the roleId
	 * 
	 * @param roleId
	 *            The roleId to set.
	 */
	public void setRoleId(Short roleId) {
		this.roleId = roleId;
	}

	/**
	 * This function returns the roleName
	 * 
	 * @return Returns the roleName.
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * This function sets the roleName
	 * 
	 * @param roleName
	 *            The roleName to set.
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}
