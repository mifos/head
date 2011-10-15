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

package org.mifos.security;

import org.apache.commons.lang.StringUtils;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AuthenticationAuthorizationServiceFacadeImpl implements AuthenticationAuthorizationServiceFacade {

    private final PersonnelDao personnelDao;
    private final UserDetailsChecker userDetailsChecker;

    @Autowired
    public AuthenticationAuthorizationServiceFacadeImpl(PersonnelDao personnelDao, UserDetailsChecker userDetailsChecker) {
        this.personnelDao = personnelDao;
        this.userDetailsChecker = userDetailsChecker;
    }

    /**
     * used by spring security when authenticating user at login.
     */
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException, DataAccessException {

        if (StringUtils.isBlank(username)) {
            throw new UsernameNotFoundException(LoginConstants.KEYINVALIDUSER);
        }

        MifosUser userDetails = personnelDao.findAuthenticatedUserByUsername(username);

        if (userDetails == null) {
            throw new UsernameNotFoundException(LoginConstants.KEYINVALIDUSER);
        }

        userDetailsChecker.check(userDetails);

        return userDetails;
    }

    @Override
    public void reloadUserDetailsForSecurityContext(String username) {
        UserDetails userSecurityDetails = loadUserByUsername(username);
        MifosUser reloadedUserDetails = (MifosUser) userSecurityDetails;

        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext == null) {
            securityContext = new SecurityContextImpl();
            SecurityContextHolder.setContext(securityContext);
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(reloadedUserDetails, reloadedUserDetails, reloadedUserDetails.getAuthorities());
        securityContext.setAuthentication(authentication);
    }

}
