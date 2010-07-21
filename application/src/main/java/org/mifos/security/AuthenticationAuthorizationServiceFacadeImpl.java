
package org.mifos.security;

import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.security.login.util.helpers.LoginConstants;
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

        if (StringUtils.isBlank(username)) {
            ResourceBundle resources = ResourceBundle.getBundle(FilePaths.LOGIN_UI_PROPERTY_FILE);
            String errorMessage = resources.getString(LoginConstants.KEYINVALIDUSER);
            throw new UsernameNotFoundException(errorMessage);
        }

        MifosUser userDetails = personnelDao.findAuthenticatedUserByUsername(username);

        if (userDetails == null) {
            ResourceBundle resources = ResourceBundle.getBundle(FilePaths.LOGIN_UI_PROPERTY_FILE);
            String errorMessage = resources.getString(LoginConstants.KEYINVALIDUSER);
            throw new UsernameNotFoundException(errorMessage);
        }
        return userDetails;
    }

}
