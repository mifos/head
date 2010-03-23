/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.customers.personnel.business;

import org.mifos.framework.business.PersistentObject;
import org.mifos.security.rolesandpermission.business.RoleBO;

public class PersonnelRoleEntity extends PersistentObject {

    private final Integer personnelRoleId;

    private final RoleBO role;

    private final PersonnelBO personnel;

    public PersonnelRoleEntity(RoleBO role, PersonnelBO personnel) {
        super();
        this.role = role;
        this.personnel = personnel;
        this.personnelRoleId = null;
    }

    protected PersonnelRoleEntity() {
        super();
        this.personnelRoleId = null;
        this.personnel = null;
        this.role = null;

    }

    public Integer getPersonnelRoleId() {
        return personnelRoleId;
    }

    public RoleBO getRole() {
        return role;
    }

    public PersonnelBO getPersonnel() {
        return personnel;
    }

}
