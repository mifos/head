package org.mifos.security;

import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AuthenticationAuthorizationServiceFacadeImpl implements AuthenticationAuthorizationServiceFacade {

    private final PersonnelDao personnelDao;

    public AuthenticationAuthorizationServiceFacadeImpl(PersonnelDao personnelDao) {
        this.personnelDao = personnelDao;
    }

    /**
     * used by spring security when authenticating user at login.
     */
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException, DataAccessException {

        return personnelDao.findAuthenticatedUserByUsername(username);
    }

}
