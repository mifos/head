
package org.mifos.security;

import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

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
