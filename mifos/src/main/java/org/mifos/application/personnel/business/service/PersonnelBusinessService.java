/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.application.personnel.business.service;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.mifos.application.configuration.persistence.ApplicationConfigurationPersistence;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.application.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.config.ConfigurationManager;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.UserContext;

public class PersonnelBusinessService implements BusinessService {
    private static final String BRANCH_MANAGER_ROLE_NAME_KEY = "RolesAndPermissions.BranchManager.RoleName";
    private RolesPermissionsPersistence rolesPermissionsPersistence;
    private Configuration applicationConfiguration;
    private PersonnelPersistence personnelPersistence;

    public PersonnelBusinessService() {
        personnelPersistence = new PersonnelPersistence();
        rolesPermissionsPersistence = new RolesPermissionsPersistence();
        applicationConfiguration = ConfigurationManager.getInstance();
    }

    @Override
    public BusinessObject getBusinessObject(UserContext userContext) {
        return null;
    }

    public List<PersonnelView> getActiveLoanOfficersInBranch(Short officeId, Short loggedInUserId,
            Short loggedInUserLevelId) throws ServiceException {
        try {
            return new PersonnelPersistence().getActiveLoanOfficersInBranch(PersonnelLevel.LOAN_OFFICER.getValue(),
                    officeId, loggedInUserId, loggedInUserLevelId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public PersonnelBO getPersonnel(Short personnelId) throws ServiceException {

        try {
            return new PersonnelPersistence().getPersonnel(personnelId);
        } catch (PersistenceException e) {

            throw new ServiceException(e);
        }
    }

    public OfficeBO getOffice(Short officeId) throws ServiceException {
        try {
            return new OfficePersistence().getOffice(officeId);
        } catch (PersistenceException e) {

            throw new ServiceException(e);
        }
    }

    public List<RoleBO> getRoles() throws ServiceException {
        try {
            return rolesPermissionsPersistence.getRoles();
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<SupportedLocalesEntity> getAllLocales() throws ServiceException {
        return new ApplicationConfigurationPersistence().getSupportedLocale();
    }

    public QueryResult getAllPersonnelNotes(Short personnelId) throws ServiceException {
        try {
            return new PersonnelPersistence().getAllPersonnelNotes(personnelId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public PersonnelBO getPersonnelByGlobalPersonnelNum(String globalPersonnelNum) throws ServiceException {
        try {
            return new PersonnelPersistence().getPersonnelByGlobalPersonnelNum(globalPersonnelNum);
        } catch (PersistenceException e) {

            throw new ServiceException(e);
        }
    }

    public PersonnelBO getPersonnel(String personnelName) throws ServiceException {
        PersonnelBO personnel = null;
        try {
            personnel = new PersonnelPersistence().getPersonnelByUserName(personnelName);
            if (personnel == null)
                throw new ServiceException(LoginConstants.KEYINVALIDUSER);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
        return personnel;
    }

    public QueryResult search(String searchString, Short officeId, Short userId) throws ServiceException {

        try {
            return new PersonnelPersistence().search(searchString, userId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<PersonnelBO> getAllPersonnel() throws ServiceException {
        try {
            return new PersonnelPersistence().getAllPersonnel();
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }

    public List<PersonnelBO> getActiveLoanOfficersUnderOffice(Short officeId) throws ServiceException {
        try {
            return new PersonnelPersistence().getActiveLoanOfficersUnderOffice(officeId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<SupportedLocalesEntity> getSupportedLocales() throws ServiceException {
        try {
            List<SupportedLocalesEntity> locales = new PersonnelPersistence().getSupportedLocales();
            return locales;
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<PersonnelBO> getActiveBranchManagersUnderOffice(Short officeId) throws ServiceException {
        try {
            return personnelPersistence.getActiveBranchManagersUnderOffice(officeId, rolesPermissionsPersistence
                    .getRole(applicationConfiguration.getString(BRANCH_MANAGER_ROLE_NAME_KEY)));
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

}
