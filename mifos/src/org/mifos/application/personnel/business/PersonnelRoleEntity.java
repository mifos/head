/**

 * PersonnelRole.java    version: xxx

 

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

package org.mifos.application.personnel.business;

import org.mifos.application.rolesandpermission.util.valueobjects.Role;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This represents the roles assigned to a personnel.
 * 
 * @author ashishsm
 * 
 */
public class PersonnelRoleEntity extends ValueObject {

	private Integer personnelRoleId;

	private Role role;

	private PersonnelBO personnel;

	public PersonnelRoleEntity() {
		super();

	}

	public Integer getPersonnelRoleId() {
		return personnelRoleId;
	}

	public void setPersonnelRoleId(Integer personnelRoleId) {
		this.personnelRoleId = personnelRoleId;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public PersonnelBO getPersonnel() {
		return personnel;
	}

	public void setPersonnel(PersonnelBO personnel) {
		this.personnel = personnel;
	}

}
