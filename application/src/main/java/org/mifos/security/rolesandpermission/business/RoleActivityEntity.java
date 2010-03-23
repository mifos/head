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

package org.mifos.security.rolesandpermission.business;

import org.mifos.framework.business.PersistentObject;

public class RoleActivityEntity extends PersistentObject {

    private RoleBO role;

    private ActivityEntity activity;

    protected RoleActivityEntity() {
    }

    public RoleActivityEntity(RoleBO role, ActivityEntity activity) {
        this.role = role;
        this.activity = activity;
    }

    public ActivityEntity getActivity() {
        return activity;
    }

    public RoleBO getRole() {
        return role;
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0 == null) {
            return false;
        }

        if (arg0.getClass() != RoleActivityEntity.class) {
            return false;
        }

        RoleActivityEntity roleActivityEntity = (RoleActivityEntity) arg0;
        if (this.getRole().getId() == null || roleActivityEntity.getRole().getId() == null
                || this.getActivity().getId() == null || roleActivityEntity.getActivity().getId() == null) {
            return false;
        }
        if (this.getRole().getId().equals(roleActivityEntity.getRole().getId())
                && this.getActivity().getId().equals(roleActivityEntity.getActivity().getId())) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

}
