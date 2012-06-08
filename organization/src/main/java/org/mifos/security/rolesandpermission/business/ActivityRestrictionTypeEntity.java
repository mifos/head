/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;


import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.framework.business.AbstractEntity;

/**
 * This class holds a type of activity restriction ({@link RoleActivityRestrictionBO}).
 * There is also enum class {@link ActivityRestrictionType}.
 */
@NamedQueries({
    @NamedQuery(name="getAllActivityRestrictionTypes", query="from ActivityRestrictionTypeEntity activityRestrictionTypeEntity order by activityRestrictionTypeEntity.id"),
    @NamedQuery(name="getActivityRestrictionTypeById", query="from ActivityRestrictionTypeEntity activityRestrictionTypeEntity WHERE activityRestrictionTypeEntity.id = :id")
})
@Entity
@Table(name = "activity_restriction_type")
public class ActivityRestrictionTypeEntity extends AbstractEntity {

    @Id
    @Column(name = "activity_restriction_type_id", nullable = false)
    private Short id;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private ActivityEntity activityEntity;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lookup_id")
    private LookUpValueEntity lookUpValue;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="activity_restriction_type_id")
    private Set<RoleActivityRestrictionBO> roleActivityRestrictions = new HashSet<RoleActivityRestrictionBO>(0);
    
    public ActivityRestrictionTypeEntity() {
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public ActivityEntity getActivityEntity() {
        return activityEntity;
    }

    public void setActivityEntity(ActivityEntity activityEntity) {
        this.activityEntity = activityEntity;
    }

    public LookUpValueEntity getLookUpValue() {
        return lookUpValue;
    }

    public void setLookUpValue(LookUpValueEntity lookUpValue) {
        this.lookUpValue = lookUpValue;
    }

    public Set<RoleActivityRestrictionBO> getRoleActivityRestrictions() {
        return roleActivityRestrictions;
    }

    public void setRoleActivityRestrictions(Set<RoleActivityRestrictionBO> roleActivityRestrictions) {
        this.roleActivityRestrictions = roleActivityRestrictions;
    }
    
}
