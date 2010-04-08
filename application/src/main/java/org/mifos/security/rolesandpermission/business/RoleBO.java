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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.security.rolesandpermission.exceptions.RolesPermissionException;
import org.mifos.security.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.security.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.security.util.UserContext;

public class RoleBO extends AbstractBusinessObject {

    private Short id = null;

    private String name;

    private Date createdDate;
    
    private Short createdBy;
    
    private Date updatedDate;
    
    private Short updatedBy;
    
    private Integer versionNo;

    private final Set<RoleActivityEntity> activities = new HashSet<RoleActivityEntity>();

    MifosLogger logger;

    private RolesPermissionsPersistence rolesPermissionPersistence = new RolesPermissionsPersistence();

    protected RoleBO() {
        logger = MifosLogManager.getLogger(LoggerConstants.ROLEANDPERMISSIONLOGGER);
    }

    RoleBO(MifosLogger logger, int id) {
        this.logger = logger;
        this.id = (short) id;
    }

    public RoleBO(UserContext userContext, String roleName, List<ActivityEntity> activityList)
            throws RolesPermissionException {
        super(userContext);
        logger = MifosLogManager.getLogger(LoggerConstants.ROLEANDPERMISSIONLOGGER);
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
        List<ActivityEntity> activityList = new ArrayList<ActivityEntity>();
        for (RoleActivityEntity roleActivityEntity : activities) {
            activityList.add(roleActivityEntity.getActivity());
        }
        return activityList;
    }

    public List<Short> getActivityIds() {
        List<Short> ids = new ArrayList<Short>();
        for (RoleActivityEntity roleActivityEntity : activities) {
            ids.add(roleActivityEntity.getActivity().getId());
        }
        return ids;
    }
    
        public Short getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Short createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Short getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Short updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    public void save() throws RolesPermissionException {
        logger.info("Saving role");
        try {
            rolesPermissionPersistence.createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new RolesPermissionException(e);
        }
        logger.info("Role saved");
    }

    public void delete() throws RolesPermissionException {
        logger.info("Deleting role");
        validateIfRoleAssignedToPersonnel();
        try {
            rolesPermissionPersistence.delete(this);
        } catch (PersistenceException e) {
            throw new RolesPermissionException(e);
        }
        logger.info("Role deleted");
    }

    public void update(Short perosnnelId, String roleName, List<ActivityEntity> activityList)
            throws RolesPermissionException {
        logger.info("Updating role");
        validateRoleName(roleName);
        validateActivities(activityList);
        name = roleName;
        updateRoleActivities(activityList);
        setUpdateDetails(perosnnelId);
        try {
            rolesPermissionPersistence.createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new RolesPermissionException(e);
        }
        logger.info("Role updated");
    }

    private void updateRoleActivities(List<ActivityEntity> activityList) {
        // Removing activities
        List<Short> activityIds = getActivityIds(activityList);
        for (Iterator<RoleActivityEntity> iter = activities.iterator(); iter.hasNext();) {
            RoleActivityEntity roleActivityEntity = iter.next();
            if (!activityIds.contains(roleActivityEntity.getActivity().getId())) {
                iter.remove();
            }

        }
        // Adding activities
        activityIds = getActivityIds(getActivities());
        for (ActivityEntity activityEntity : activityList) {
            if (!activityIds.contains(activityEntity.getId())) {
                RoleActivityEntity roleActivityEntity = new RoleActivityEntity(this, activityEntity);
                activities.add(roleActivityEntity);
            }
        }
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
            RoleActivityEntity roleActivityEntity = new RoleActivityEntity(this, activityEntity);
            activities.add(roleActivityEntity);
        }
    }

    private void validateRoleName(String roleName) throws RolesPermissionException {
        logger.info("Checking rolename for empty or null");
        if (StringUtils.isBlank(roleName)) {
            throw new RolesPermissionException(RolesAndPermissionConstants.KEYROLENAMENOTSPECIFIED);
        }
        logger.info("Checking for duplicate rolename");
        try {
            if (name == null || !name.trim().equalsIgnoreCase(roleName.trim())) {
                if (rolesPermissionPersistence.getRole(roleName.trim()) != null) {
                    throw new RolesPermissionException(RolesAndPermissionConstants.KEYROLEALREADYEXIST);
                }
            }
        } catch (PersistenceException e) {
            throw new RolesPermissionException(e);
        }
        logger.info("Checking rolename done");
    }

    private void validateActivities(List<ActivityEntity> activityList) throws RolesPermissionException {
        logger.info("Validating activities");
        if (null == activityList || activityList.size() == 0) {
            throw new RolesPermissionException(RolesAndPermissionConstants.KEYROLEWITHNOACTIVITIES);
        }
        logger.info("Activities validated");
    }

    private void validateIfRoleAssignedToPersonnel() throws RolesPermissionException {
        logger.info("Validating if role is assigned to personnel");
        if (isRoleAssignedToPersonnel()) {
            throw new RolesPermissionException(RolesAndPermissionConstants.KEYROLEASSIGNEDTOPERSONNEL);
        }
        logger.info("Validation done for role assigned to personnel");
    }

    private boolean isRoleAssignedToPersonnel() throws RolesPermissionException {
        Integer count;
        try {
            count = new PersonnelPersistence().getPersonnelRoleCount(id);
        } catch (PersistenceException e) {
            throw new RolesPermissionException(e);
        }
        return (null != count && count > 0) ? true : false;
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
