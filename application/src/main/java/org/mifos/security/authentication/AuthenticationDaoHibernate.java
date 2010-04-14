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

package org.mifos.security.authentication;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 *
 */
public class AuthenticationDaoHibernate implements AuthenticationDao {

    private final GenericDao genericDao = DependencyInjectedServiceLocator.locateGenericDao();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {

        PersonnelBO user = findPersonnelByUsername(username);
        byte[] password = user.getEncryptedPassword();
        boolean enabled = user.isActive();
        boolean accountNonExpired = !user.isLocked();
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = !user.isLocked();

        GrantedAuthority mifosUser = new GrantedAuthorityImpl("ROLE_MIFOS_USER");
        List<GrantedAuthority> authorities = Arrays.asList(mifosUser);

        return new MifosUser(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    @Override
    public final PersonnelBO findPersonnelByUsername(String username) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("USER_NAME", username);

        return (PersonnelBO) this.genericDao.executeUniqueResultNamedQuery(NamedQueryConstants.GETPERSONNELBYNAME, queryParameters);
    }
}