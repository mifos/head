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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.servicefacade.CenterCreation;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelDto;
import org.mifos.customers.personnel.business.PersonnelRoleEntity;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.personnel.util.helpers.PersonnelStatus;
import org.mifos.security.MifosUser;
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.mifos.security.util.SecurityConstants;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

public class PersonnelDaoHibernate implements PersonnelDao {

    private final GenericDao genericDao;

    public PersonnelDaoHibernate(GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    @Override
    public void save(PersonnelBO user) {
        this.genericDao.createOrUpdate(user);
    }

    @Override
    public PersonnelBO findPersonnelById(Short id) {
        if (id == null) {
            return null;
        }

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("PERSONNEL_ID", id);

        return (PersonnelBO) this.genericDao.executeUniqueResultNamedQuery("findPersonnelById", queryParameters);
    }

    @Override
    public PersonnelBO findPersonnelByUsername(final String username) {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("USER_NAME", username);

        return (PersonnelBO) this.genericDao.executeUniqueResultNamedQuery(NamedQueryConstants.GETPERSONNELBYNAME, queryParameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    public MifosUser findAuthenticatedUserByUsername(String username) {
        PersonnelBO user = findPersonnelByUsername(username);
        if (user == null) {
            return null;
        }

        Set<Short> roleIds = new HashSet<Short>();
        for (PersonnelRoleEntity personnelRole : user.getPersonnelRoles()) {
            RoleBO role = personnelRole.getRole();
            roleIds.add(role.getId());
        }

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("ROLE_IDS", new ArrayList<Short>(roleIds));
        List<Short> activityIds = (List<Short>) this.genericDao.executeNamedQuery("findDistinctActivityIdsForGivenSetOfRoleIds", queryParameters);

        byte[] password = user.getEncryptedPassword();
        boolean enabled = user.isActive();
        boolean accountNonExpired = !user.isLocked();
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = !user.isLocked();

        List<GrantedAuthority> authorities = translateActivityIdsToGrantedAuthorities(activityIds);

        return new MifosUser(user.getPersonnelId(), user.getOffice().getOfficeId(), username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PersonnelDto> findActiveLoanOfficersForOffice(CenterCreation centerCreationDto) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("levelId", PersonnelLevel.LOAN_OFFICER.getValue());
        queryParameters.put("userId", centerCreationDto.getLoggedInUserId());
        queryParameters.put("userLevelId", centerCreationDto.getLoggedInUserLevelId());
        queryParameters.put("officeId", centerCreationDto.getOfficeId());
        queryParameters.put("statusId", PersonnelStatus.ACTIVE.getValue());

        List<PersonnelDto> queryResult = (List<PersonnelDto>) genericDao.executeNamedQuery(
                NamedQueryConstants.MASTERDATA_ACTIVE_LOANOFFICERS_INBRANCH, queryParameters);

        return queryResult;
    }

    @Override
    public PersonnelBO findByGlobalPersonnelNum(String globalNumber) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("globalPersonnelNum", globalNumber);

        return (PersonnelBO) this.genericDao.executeUniqueResultNamedQuery(NamedQueryConstants.PERSONNEL_BY_SYSTEM_ID, queryParameters);
    }

    private List<GrantedAuthority> translateActivityIdsToGrantedAuthorities(List<Short> activityIdList) {

        Map<Short, GrantedAuthority> activityToGrantedAuthorityMap = populateSecurityConstantToGrantedAuthorityMap();

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        if (activityIdList != null) {
            for (Short activityId : activityIdList) {
                if (activityToGrantedAuthorityMap.containsKey(activityId)) {
                    authorities.add(activityToGrantedAuthorityMap.get(activityId));
                }
            }
        }

        return authorities;
    }

    private Map<Short, GrantedAuthority> populateSecurityConstantToGrantedAuthorityMap() {

        // FIXME - keithw - just keeping adding the SecurityConstants and corresponding GrantedAuthority name for area
        Map<Short, GrantedAuthority> authoritiesMap = new HashMap<Short, GrantedAuthority>();

        // manage products ->
        authoritiesMap.put(SecurityConstants.DEFINE_NEW_PRODUCT_CATEGORIES, new GrantedAuthorityImpl(MifosUser.ROLE_CAN_CREATE_PRODUCT_CATEGORIES));
        authoritiesMap.put(SecurityConstants.EDIT_PRODUCT_CATEGORIES, new GrantedAuthorityImpl(MifosUser.ROLE_CAN_EDIT_PRODUCT_CATEGORIES));

        authoritiesMap.put(SecurityConstants.UPDATE_LATENESS_DORMANCY, new GrantedAuthorityImpl(MifosUser.UPDATE_LATENESS_DORMANCY));
        authoritiesMap.put(SecurityConstants.CAN_DEFINE_PRODUCT_MIX, new GrantedAuthorityImpl(MifosUser.ROLE_CAN_DEFINE_PRODUCT_MIX));
        authoritiesMap.put(SecurityConstants.CAN_EDIT_PRODUCT_MIX, new GrantedAuthorityImpl(MifosUser.ROLE_CAN_EDIT_PRODUCT_MIX));

        // Manage Organisation -> Data display and rules
        authoritiesMap.put(SecurityConstants.FUNDS_EDIT_FUNDS, new GrantedAuthorityImpl(MifosUser.CAN_EDIT_FUNDS));
        authoritiesMap.put(SecurityConstants.FUNDS_CREATE_FUNDS, new GrantedAuthorityImpl(MifosUser.CAN_CREATE_FUNDS));
        authoritiesMap.put(SecurityConstants.CAN_DEFINE_LABELS, new GrantedAuthorityImpl(MifosUser.CAN_DEFINE_LABELS));
        authoritiesMap.put(SecurityConstants.CAN_DEFINE_HIDDEN_MANDATORY_FIELDS, new GrantedAuthorityImpl(MifosUser.CAN_DEFINE_HIDDEN_MANDATORY_FIELDS));
        authoritiesMap.put(SecurityConstants.CAN_DEFINE_ACCEPTED_PAYMENT_TYPE, new GrantedAuthorityImpl(MifosUser.CAN_DEFINE_ACCEPTED_PAYMENT_TYPES));

        // System Information
        authoritiesMap.put(SecurityConstants.CAN_VIEW_SYSTEM_INFO, new GrantedAuthorityImpl(MifosUser.VIEW_SYSTEM_INFO));

        // Shutdown
        authoritiesMap.put(SecurityConstants.CAN_OPEN_SHUTDOWN_PAGE, new GrantedAuthorityImpl(MifosUser.CAN_OPEN_SHUTDOWN_PAGE));
        authoritiesMap.put(SecurityConstants.CAN_SHUT_DOWN_MIFOS, new GrantedAuthorityImpl(MifosUser.CAN_SHUT_DOWN_MIFOS));

        return authoritiesMap;
    }
}