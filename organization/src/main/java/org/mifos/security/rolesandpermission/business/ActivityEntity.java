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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.framework.business.AbstractEntity;

@NamedQueries({
        @NamedQuery(name="getAllActivities",query="from ActivityEntity ae order by ae.id"),
        @NamedQuery(name="activityEntity.getActivityEntityByLookUpValueEntity",
                    query="from ActivityEntity ae where ae.activityNameLookupValues=:aLookUpValueEntity"),
        @NamedQuery(name="getActivityById",query="from ActivityEntity ae where ae.id = :ACTIVITY_ID")
})

@Entity
@Table(name = "activity")
public class ActivityEntity extends AbstractEntity {

    @Id
    @Column(name = "activity_id", nullable = false)
    private final Short id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ActivityEntity parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_name_lookup_id")
    private final LookUpValueEntity activityNameLookupValues;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "description_lookup_id")
    private final LookUpValueEntity descriptionLookupValues;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="roles_activity",
            joinColumns={@JoinColumn(name="activity_id", referencedColumnName="activity_id")},
            inverseJoinColumns={@JoinColumn(name="role_id", referencedColumnName="role_id")})
    private final Set<RoleBO> roles = new HashSet<RoleBO>(0);
    
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name="activity_id")
    private final Set<ActivityRestrictionTypeEntity> activityRestrictionTypes = new HashSet<ActivityRestrictionTypeEntity>(0);

    private transient String description;
    private transient String activityName;

    protected ActivityEntity() {
        this.id = null;
        this.parent = null;
        this.activityNameLookupValues = null;
        this.descriptionLookupValues = null;
    }

    ActivityEntity(int id) {
        this.id = (short) id;
        this.parent = null;
        this.activityNameLookupValues = null;
        this.descriptionLookupValues = null;
    }

    public ActivityEntity(short id, ActivityEntity parentActivityEntity, LookUpValueEntity lookUpValueEntity) {
        this.id = id;
        this.parent = parentActivityEntity;
        this.activityNameLookupValues = lookUpValueEntity;
        this.descriptionLookupValues = this.activityNameLookupValues;
    }

    public Short getId() {
        return id;
    }

    public ActivityEntity getParent() {
        return parent;
    }

    public LookUpValueEntity getActivityNameLookupValues() {
        return activityNameLookupValues;
    }

    public LookUpValueEntity getDescriptionLookupValues() {
        return descriptionLookupValues;
    }

    public String getDescription() {
        return this.description;
//        return ApplicationContextProvider.getBean(MessageLookup.class).lookup(getActivityNameLookupValues());
    }

    public String getActivityName() {
        return this.activityName;
//        return ApplicationContextProvider.getBean(MessageLookup.class).lookup(getActivityNameLookupValues());
    }

    public void setParent(ActivityEntity parent) {
        this.parent = parent;
    }

    public Set<RoleBO> getRoles() {
        return roles;
    }

    public Set<Short> getRoleIds() {
        Set<Short> roleIds = new HashSet<Short>();
        for(RoleBO role:roles) {
            roleIds.add(role.getId());
        }
        return roleIds;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Set<ActivityRestrictionTypeEntity> getActivityRestrictionTypes() {
        return activityRestrictionTypes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((parent == null) ? 0 : parent.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ActivityEntity other = (ActivityEntity) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (parent == null) {
            if (other.parent != null) {
                return false;
            }
        } else if (!parent.equals(other.parent)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ActivityEntity [id=" + id + "]";
    }
}
