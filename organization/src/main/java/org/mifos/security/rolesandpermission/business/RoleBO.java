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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.security.rolesandpermission.exceptions.RolesPermissionException;
import org.mifos.security.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NamedQueries({
    @NamedQuery(name="getRoleForGivenName",query="FROM RoleBO role WHERE role.name =:RoleName"),
    @NamedQuery(name="getAllRoles",query="from RoleBO role order by role.name")
})

@Entity
@Table(name = "role")
public class RoleBO extends AbstractBusinessObject {

    private static final Logger logger = LoggerFactory.getLogger(RoleBO.class);

    @GenericGenerator(name = "generator", strategy = "increment")
    @Id
    @GeneratedValue(generator = "generator")
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
    @JoinTable(name="roles_activity",
            joinColumns={ @JoinColumn(name="role_id", referencedColumnName="role_id")},
            inverseJoinColumns={ @JoinColumn(name="activity_id", referencedColumnName="activity_id")})
    private final List<ActivityEntity> activities = new ArrayList<ActivityEntity>(0);

    RoleBO() {
    }

    RoleBO(int id) {
        this.id = (short) id;
    }

    public RoleBO(UserContext userContext, String roleName, List<ActivityEntity> activityList)
            throws RolesPermissionException {
        super(userContext);
        logger.info("Creating a new role");
        validateRoleName(roleName);
        validateActivities(activityList);
        name = roleName;
        createRoleActivites(activityList);
        setCreateDetails();
        logger.info("New role created");
    }

    public Short getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ActivityEntity> getActivities() {
        return activities;
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

    public void update(Short perosnnelId, String roleName, List<ActivityEntity> activityList)
            throws RolesPermissionException {
        logger.info("Updating role");
        validateRoleName(roleName);
        validateActivities(activityList);
        name = roleName;
        updateRoleActivities(activityList);
        setUpdateDetails(perosnnelId);
        logger.info("Role updated");
    }

    private void updateRoleActivities(List<ActivityEntity> activityList) {
        // Removing activities
        activities.clear();
        // Adding activities
        activities.addAll(activityList);
    }

    private List<Short> getActivityIds(List<ActivityEntity> activityList) {
        List<Short> activityIds = new ArrayList<Short>();
        for (ActivityEntity activityEntity : activityList) {
            activityIds.add(activityEntity.getId());
        }
        return activityIds;
    }

    private void createRoleActivites(List<ActivityEntity> activityList) {
        for (ActivityEntity activityEntity : activityList) {
            activities.add(activityEntity);
        }
    }

    public void validateRoleName(String roleName) throws RolesPermissionException {
        if (StringUtils.isBlank(roleName)) {
            throw new RolesPermissionException(RolesAndPermissionConstants.KEYROLENAMENOTSPECIFIED);
        }
    }

    private void validateActivities(List<ActivityEntity> activityList) throws RolesPermissionException {
        logger.info("Validating activities");
        if (null == activityList || activityList.size() == 0) {
            throw new RolesPermissionException(RolesAndPermissionConstants.KEYROLEWITHNOACTIVITIES);
        }
        logger.info("Activities validated");
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((id == null) ? 0 : id.hashCode());
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
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
}
