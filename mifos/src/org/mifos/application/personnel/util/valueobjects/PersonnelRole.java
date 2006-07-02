/**

 * PersonnelRole.java    version: xxx

 

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

import java.util.Set;

import org.mifos.application.rolesandpermission.util.valueobjects.Role;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This represents the roles assigned to a personnel.This is mapped as entity type in the database and have a one to many association with the personnel and also one to many association with th roles.
 * @author ashishsm
 *
 */
public class PersonnelRole extends ValueObject{

	/**Denotes the primary key. It is running , auto generated number */
	private Integer personnelRoleId;
	
	/**Denotes the instance of the role to be added to the personnel */
	private Role role;
	
	/**Denotes the personnel object for which role is being added */
	private Personnel personnel;

	/**
     * Simple constructor of PersonnelRole instances.
     */
	public PersonnelRole() {
		super();
		
	}
	/**
     * Return the value of the personnelRoleId.
     * @return Integer
     */
	public Integer getPersonnelRoleId() {
		return personnelRoleId;
	}

	/**
     * Sets the personnel personnelRoleId
     * @param personnelRoleId
     */
	public void setPersonnelRoleId(Integer personnelRoleId) {
		this.personnelRoleId = personnelRoleId;
	}

	/**
     * Return the role object.
     * @return role
     */
	public Role getRole() {
		return role;
	}

	/**
     * Sets the role object
     * @param role
     */
	public void setRole(Role role) {
		this.role = role;
	}

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
	  
}
