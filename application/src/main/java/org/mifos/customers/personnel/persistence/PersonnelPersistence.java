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

package org.mifos.customers.personnel.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelRoleEntity;
import org.mifos.customers.personnel.business.PersonnelTemplate;
import org.mifos.customers.personnel.business.PersonnelView;
import org.mifos.customers.personnel.exceptions.PersonnelException;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.personnel.util.helpers.PersonnelStatus;
import org.mifos.customers.util.helpers.CustomerSearchConstants;
import org.mifos.customers.util.helpers.Param;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryInputs;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.Persistence;
import org.mifos.security.authentication.AuthenticationDao;
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.mifos.security.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.security.util.UserContext;

public class PersonnelPersistence extends Persistence {

    // TODO : Move to PersonnelRoleEntity
    private final RolesPermissionsPersistence rolesPermissionsPersistence = new RolesPermissionsPersistence();

    @SuppressWarnings("unchecked")
    @Deprecated
    public List<PersonnelView> getActiveLoanOfficersInBranch(Short levelId, Short officeId, Short userId,
            Short userLevelId) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("levelId", levelId);
        queryParameters.put("userId", userId);
        queryParameters.put("userLevelId", userLevelId);
        queryParameters.put("officeId", officeId);
        queryParameters.put("statusId", PersonnelStatus.ACTIVE.getValue());
        List<PersonnelView> queryResult = executeNamedQuery(
                NamedQueryConstants.MASTERDATA_ACTIVE_LOANOFFICERS_INBRANCH, queryParameters);
        return queryResult;
    }

    public PersonnelBO getPersonnel(Short personnelId) throws PersistenceException {
        if (personnelId == null) {
            return null;
        }
        return (PersonnelBO) getPersistentObject(PersonnelBO.class, personnelId);
    }

    public boolean isUserExist(String userName) throws PersistenceException {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("USER_NAME", userName);
        Number count = (Number) execUniqueResultNamedQuery(NamedQueryConstants.GET_PERSONNEL_WITH_NAME, queryParameters);
        if (count != null) {
            return count.longValue() > 0;
        }

        return false;
    }

    public boolean isUserExistWithGovernmentId(String governmentId) throws PersistenceException {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("GOVT_ID", governmentId);
        Number count = (Number) execUniqueResultNamedQuery(NamedQueryConstants.GET_PERSONNEL_WITH_GOVERNMENTID,
                queryParameters);
        if (count != null) {
            return count.longValue() > 0;
        }
        return false;
    }

    public boolean isUserExist(String displayName, Date dob) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("DISPLAY_NAME", displayName);
        queryParameters.put("DOB", dob);
        Number count = (Number) execUniqueResultNamedQuery(NamedQueryConstants.GET_PERSONNEL_WITH_DOB_AND_DISPLAYNAME,
                queryParameters);
        if (count != null) {
            return count.longValue() > 0;
        }
        return false;
    }

    public boolean getActiveChildrenForLoanOfficer(Short personnelId, Short officeId) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("userId", personnelId);
        queryParameters.put("officeId", officeId);
        Number count = (Number) execUniqueResultNamedQuery(NamedQueryConstants.GET_ACTIVE_CUSTOMERS_FOR_LO,
                queryParameters);
        if (count != null) {
            return count.longValue() > 0;
        }
        return false;
    }

    public boolean getAllChildrenForLoanOfficer(Short personnelId, Short officeId) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("userId", personnelId);
        queryParameters.put("officeId", officeId);
        Long count = (Long) execUniqueResultNamedQuery(NamedQueryConstants.GET_ALL_CUSTOMERS_FOR_LO, queryParameters);
        if (count != null) {
            return count > 0 ? true : false;
        }
        return false;
    }

    public PersonnelBO getPersonnelByGlobalPersonnelNum(String globalPersonnelNum) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("globalPersonnelNum", globalPersonnelNum);

        PersonnelBO personnelBO = (PersonnelBO) execUniqueResultNamedQuery(NamedQueryConstants.PERSONNEL_BY_SYSTEM_ID,
                queryParameters);
        if (personnelBO != null) {
            return personnelBO;
        }
        return null;
    }

    public QueryResult getAllPersonnelNotes(Short personnelId) throws PersistenceException {
        QueryResult notesResult = null;
        try {
            Session session = null;
            notesResult = QueryFactory.getQueryResult("NotesSearch");
            session = notesResult.getSession();
            Query query = session.getNamedQuery(NamedQueryConstants.GETALLPERSONNELNOTES);
            query.setInteger("PERSONNEL_ID", personnelId);
            notesResult.executeQuery(query);
        } catch (HibernateProcessException hpe) {
            throw new PersistenceException(hpe);
        } catch (HibernateSearchException hse) {
            throw new PersistenceException(hse);
        }
        return notesResult;
    }

    public Integer getPersonnelRoleCount(Short roleId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("roleId", roleId);
        Number count = (Number) execUniqueResultNamedQuery(NamedQueryConstants.GET_PERSONNEL_ROLE_COUNT,
                queryParameters);
        return count.intValue();
    }

    /**
     * @deprecated use {@link AuthenticationDao#findPersonnelByUsername(String)}.
     */
    @Deprecated
    public PersonnelBO getPersonnelByUserName(String personnelUserName) throws PersistenceException {
        PersonnelBO personnelBO = null;
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("USER_NAME", personnelUserName);
        personnelBO = (PersonnelBO) execUniqueResultNamedQuery(NamedQueryConstants.GETPERSONNELBYNAME, queryParameters);
        return personnelBO;
    }

    public PersonnelBO getPersonnelByDisplayName(String personnelDisplayName) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("DISPLAY_NAME", personnelDisplayName);
        PersonnelBO personnelBO = (PersonnelBO) execUniqueResultNamedQuery(
                NamedQueryConstants.GETPERSONNELBYDISPLAYNAME, queryParameters);
        return personnelBO;
    }

    public PersonnelBO findPersonnelById(final Short id) {
        return (PersonnelBO) getSession().get(PersonnelBO.class, id);
    }

    public void updateWithCommit(PersonnelBO personnelBO) throws PersistenceException {
        super.createOrUpdate(personnelBO);
        try {
            StaticHibernateUtil.commitTransaction();
        } catch (HibernateException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new PersistenceException(e);
        }

    }

    public QueryResult search(String searchString, Short userId) throws PersistenceException {
        String[] namedQuery = new String[2];
        List<Param> paramList = getParamList(new PersonnelPersistence().getPersonnel(userId));

        if (searchString.contains(" ")) {
            paramList.add(typeNameValue("String", "USER_NAME1", searchString.substring(0, searchString.indexOf(" "))));
            paramList.add(typeNameValue("String", "USER_NAME2", searchString.substring(searchString.indexOf(" ") + 1,
                    searchString.length())));
        } else {
            paramList.add(typeNameValue("String", "USER_NAME1", searchString));
            paramList.add(typeNameValue("String", "USER_NAME2", ""));
        }
        namedQuery[0] = NamedQueryConstants.PERSONNEL_SEARCH_COUNT;
        namedQuery[1] = NamedQueryConstants.PERSONNEL_SEARCH;
        paramList.add(typeNameValue("String", "USER_NAME", searchString + "%"));
        return getQueryResults(paramList, namedQuery);
    }

    @SuppressWarnings("unchecked")
    public List<PersonnelBO> getActiveLoanOfficersUnderOffice(Short officeId) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(CustomerSearchConstants.OFFICEID, officeId);
        queryParameters.put(CustomerSearchConstants.PERSONNELLEVELID, PersonnelLevel.LOAN_OFFICER.getValue());
        queryParameters.put(PersonnelConstants.LOANOFFICERACTIVE, PersonnelStatus.ACTIVE.getValue());
        return executeNamedQuery(NamedQueryConstants.GET_ACTIVE_LOAN_OFFICER_UNDER_USER, queryParameters);
    }

    @SuppressWarnings("unchecked")
    public List<SupportedLocalesEntity> getSupportedLocales() throws PersistenceException {
        return executeNamedQuery(NamedQueryConstants.SUPPORTED_LOCALE_LIST, null);
    }

    public List getAvailableLanguages() throws PersistenceException {
        return executeNamedQuery(NamedQueryConstants.AVAILABLE_LANGUAGES, null);
    }

    public PersonnelBO createPersonnel(UserContext userContext, PersonnelTemplate template)
            throws PersistenceException, ValidationException, PersonnelException {
        OfficeBO office = new OfficePersistence().getOffice(template.getOfficeId());
        if (office == null) {
            throw new ValidationException(PersonnelConstants.OFFICE);
        }
        int numberOfRoles = template.getRoleIds().size();
        ArrayList<RoleBO> roles = new ArrayList<RoleBO>(numberOfRoles);
        RoleBO role;
        for (int i = 0; i < numberOfRoles; i++) {
            role = rolesPermissionsPersistence.getRole(template.getRoleIds().get(i));
            if (role == null) {
                throw new ValidationException(PersonnelConstants.ROLES_LIST);
            }
            roles.add(role);
        }

        PersonnelBO personnelBO = new PersonnelBO(template.getPersonnelLevel(), office, template.getTitleId(), template
                .getPreferredLocale(), template.getPassword(), template.getUserName(), template.getEmailId(), roles,
                template.getCustomFields(), template.getName(), template.getGovernmentIdNumber(), template
                        .getDateOfBirth(), template.getMaritalStatusId(), template.getGenderId(), template
                        .getDateOfJoiningMFI(), template.getDateOfJoiningBranch(), template.getAddress(), userContext
                        .getId());
        personnelBO.save();
        return personnelBO;
    }

    @SuppressWarnings("unchecked")
    public List<PersonnelBO> getAllPersonnel() throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();

        List<PersonnelBO> queryResult = executeNamedQuery(NamedQueryConstants.GET_ALL_PERSONNEL, queryParameters);
        return queryResult;

    }

    @SuppressWarnings("unchecked")
    public List<PersonnelBO> getActiveBranchManagersUnderOffice(Short officeId, final RoleBO role)
            throws PersistenceException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CustomerSearchConstants.OFFICEID, officeId);
        params.put(CustomerSearchConstants.PERSONNELSTATUSID, PersonnelStatus.ACTIVE.getValue());
        List activeBranchManagers = executeNamedQuery(NamedQueryConstants.GET_ACTIVE_BRANCH_MANAGER_UNDER_OFFICE,
                params);
        return (List<PersonnelBO>) CollectionUtils.select(activeBranchManagers, new Predicate() {
            public boolean evaluate(Object object) {
                Set<PersonnelRoleEntity> applicableRoles = ((PersonnelBO) object).getPersonnelRoles();
                return CollectionUtils.exists(applicableRoles, new Predicate() {
                    public boolean evaluate(Object object) {
                        return ((PersonnelRoleEntity) object).getRole().equals(role);
                    }
                });
            }
        });
    }

    private List<Param> getParamList(PersonnelBO personnel) {
        List<Param> paramList = new ArrayList<Param>();
        paramList.add(typeNameValue("String", "SEARCH_ALL", personnel.getOffice().getSearchId() + "%"));
        paramList.add(typeNameValue("Short", "USERID", personnel.getPersonnelId()));
        paramList.add(typeNameValue("Short", "LOID", PersonnelLevel.LOAN_OFFICER.getValue()));
        paramList.add(typeNameValue("Short", "USERLEVEL_ID", personnel.getLevelEnum().getValue()));

        return paramList;

    }

    private QueryResult getQueryResults(List<Param> paramList, String[] namedQuery) throws PersistenceException {

        QueryResult queryResult = QueryFactory.getQueryResult(PersonnelConstants.USER_LIST);
        QueryInputs queryInputs = new QueryInputs();
        queryInputs.setQueryStrings(namedQuery);
        queryInputs.setParamList(paramList);
        queryInputs.setPath("org.mifos.customers.personnel.util.helpers.UserSearchResultsView");
        queryInputs.setAliasNames(getAliasNames());
        try {
            queryResult.setQueryInputs(queryInputs);
        } catch (HibernateSearchException e) {
            throw new PersistenceException(e);
        }
        return queryResult;
    }

    private String[] getAliasNames() {
        String[] aliasNames = { "officeId", "officeName", "personnelId", "globalPersonnelNum", "personnelName" };
        return aliasNames;

    }
}
