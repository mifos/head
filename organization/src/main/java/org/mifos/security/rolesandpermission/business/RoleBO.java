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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.security.rolesandpermission.exceptions.RolesPermissionException;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NamedQueries({ @NamedQuery(name = "getRoleForGivenName", query = "FROM RoleBO role WHERE role.name =:RoleName"),
        @NamedQuery(name = "getAllRoles", query = "from RoleBO role order by role.name") })
@Entity
@Table(name = "role")
public class RoleBO extends AbstractBusinessObject {

    private static final Logger logger = LoggerFactory.getLogger(RoleBO.class);

    @Id
    @GeneratedValue(generator = "generator")
    @GenericGenerator(name = "generator", strategy = "increment")
    @Column(name = "role_id", nullable = false)
    private Short id = null;

    @Column(name = "role_name")
    private String name;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "created_by")
    private Short createdBy;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "updated_by")
    private Short updatedBy;

    @Version
    @Column(name = "version_no", nullable = false)
    private Integer versionNo;

    @ManyToMany
    @JoinTable(name = "roles_activity", joinColumns = { @JoinColumn(name = "role_id", referencedColumnName = "role_id") }, inverseJoinColumns = { @JoinColumn(name = "activity_id", referencedColumnName = "activity_id") })
    private final Set<ActivityEntity> activities = new HashSet<ActivityEntity>(0);

    @OneToMany(cascade = { CascadeType.ALL}, mappedBy="role")
    private final Set<RoleActivityRestrictionBO> restrictions = new HashSet<RoleActivityRestrictionBO>(0);
    
    @SuppressWarnings("unused")
    private RoleBO() {
    }

    public RoleBO(UserContext userContext, String roleName, List<ActivityEntity> activityList) throws RolesPermissionException {
        super(userContext);
        logger.info("Creating a new role:" + roleName);
        name = roleName;
        createRoleActivites(activityList);
        setCreateDetails();
        logger.info("New role created:" + roleName);
    }
    
    public RoleBO(UserContext userContext, String roleName, List<ActivityEntity> activityList, List<RoleActivityRestrictionBO> restrictionsList) throws RolesPermissionException{
        this(userContext, roleName, activityList);
        createRoleActivitiesRestrictions(restrictionsList);
    }

    public Short getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<ActivityEntity> getActivities() {
        return activities;
    }

    public Set<RoleActivityRestrictionBO> getRestrictions() {
        return restrictions;
    }

    public List<Short> getActivityIds() {
        List<Short> ids = new ArrayList<Short>();
        for (ActivityEntity activity : activities) {
            ids.add(activity.getId());
        }
        return ids;
    }

    @Override
    public Short getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(Short createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Date getCreatedDate() {
        return createdDate;
    }

    @Override
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public Short getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdatedBy(Short updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public Date getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Override
    public Integer getVersionNo() {
        return versionNo;
    }

    @Override
    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    public void update(Short perosnnelId, String roleName, List<ActivityEntity> activityList) throws RolesPermissionException {
        logger.info("Updating role");
        name = roleName;
        createRoleActivites(activityList);
        setUpdateDetails(perosnnelId);
        logger.info("Role updated");
    }

    public void updateWithActivitiesRestrictions(Short perosnnelId, String roleName, List<ActivityEntity> activityList,
            List<RoleActivityRestrictionBO> activityRestrictionBOList) throws RolesPermissionException {     
        this.restrictions.clear();
        if (activityRestrictionBOList != null){
            this.restrictions.addAll(activityRestrictionBOList);
        }
        update(perosnnelId, roleName, activityList);
    }
    
    private void createRoleActivites(List<ActivityEntity> activityList) {
        activities.clear();
        if(activityList != null) {
            activities.addAll(activityList);
        }
    }
    
    private void createRoleActivitiesRestrictions(List<RoleActivityRestrictionBO> activitiesRestrictions) {
        restrictions.clear();
        for ( RoleActivityRestrictionBO activityRestrictionBO : activitiesRestrictions) {
            activityRestrictionBO.setRole(this);
            restrictions.add(activityRestrictionBO);
        }
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((name == null) ? 0 : name.hashCode());
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
        final RoleBO other = (RoleBO) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RoleBO [id=" + id + ", name=" + name + "]";
    }
}
