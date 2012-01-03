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

package org.mifos.customers.personnel.business.service;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.config.business.MifosConfigurationManager;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.personnel.persistence.LegacyPersonnelDao;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.mifos.security.rolesandpermission.persistence.LegacyRolesPermissionsDao;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;

public class PersonnelBusinessService implements BusinessService {
    private static final String BRANCH_MANAGER_ROLE_NAME_KEY = "RolesAndPermissions.BranchManager.RoleName";
    private LegacyRolesPermissionsDao legacyRolesPermissionDao;
    private Configuration applicationConfiguration;
    private LegacyPersonnelDao legacyPersonnelDao;

    public PersonnelBusinessService() {
        this(ApplicationContextProvider.getBean(LegacyPersonnelDao.class), ApplicationContextProvider.getBean(LegacyRolesPermissionsDao.class));
    }

    @Autowired
    public PersonnelBusinessService(LegacyPersonnelDao legacyPersonnelDao,
                                    LegacyRolesPermissionsDao legacyRolesPermissionDao) {
        this.legacyPersonnelDao = legacyPersonnelDao;
        this.legacyRolesPermissionDao = legacyRolesPermissionDao;
        applicationConfiguration = MifosConfigurationManager.getInstance();
    }

    @Override
    public AbstractBusinessObject getBusinessObject(@SuppressWarnings("unused") UserContext userContext) {
        return null;
    }

    public PersonnelBO getPersonnel(Short personnelId) throws ServiceException {

        try {
            return legacyPersonnelDao.getPersonnel(personnelId);
        } catch (PersistenceException e) {

            throw new ServiceException(e);
        }
    }

    public OfficeBO getOffice(Short officeId) throws ServiceException {
        try {
            return getOfficePersistence().getOffice(officeId);
        } catch (PersistenceException e) {

            throw new ServiceException(e);
        }
    }

    protected OfficePersistence getOfficePersistence() {
        return new OfficePersistence();
    }

    public List<RoleBO> getRoles() throws ServiceException {
        try {
            return legacyRolesPermissionDao.getRoles();
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public RoleBO getRoleById(Short id) throws ServiceException {
        try {
            return legacyRolesPermissionDao.getRole(id);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public QueryResult getAllPersonnelNotes(Short personnelId) throws ServiceException {
        try {
            return legacyPersonnelDao.getAllPersonnelNotes(personnelId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * @deprecated use {@link PersonnelDao}.
     */
    @Deprecated
    public PersonnelBO getPersonnel(String personnelName) throws ServiceException {
        PersonnelBO personnel = null;
        try {
            personnel = legacyPersonnelDao.getPersonnelByUserName(personnelName);
            if (personnel == null) {
                throw new ServiceException(LoginConstants.KEYINVALIDUSER);
            }
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
        return personnel;
    }

    public QueryResult search(String searchString, Short userId) throws ServiceException {

        try {
            return legacyPersonnelDao.search(searchString, userId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<PersonnelBO> getAllPersonnel() throws ServiceException {
        try {
            return legacyPersonnelDao.getAllPersonnel();
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }

    public List<PersonnelBO> getActiveLoanOfficersUnderOffice(Short officeId) throws ServiceException {
        try {
            return legacyPersonnelDao.getActiveLoanOfficersUnderOffice(officeId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<PersonnelBO> getActiveBranchManagersUnderOffice(Short officeId) throws ServiceException {
        try {
            return legacyPersonnelDao.getActiveBranchManagersUnderOffice(officeId, legacyRolesPermissionDao
                    .getRole(applicationConfiguration.getString(BRANCH_MANAGER_ROLE_NAME_KEY)));
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

}
