
package org.mifos.security;

import org.apache.commons.lang.StringUtils;
import org.mifos.config.Localization;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.springframework.context.i18n.LocaleContextHolder;
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

        LocaleContextHolder.setLocale(Localization.getInstance().getMainLocale());

        if (StringUtils.isBlank(username)) {
            throw new UsernameNotFoundException(LoginConstants.KEYINVALIDUSER);
        }

        MifosUser userDetails = personnelDao.findAuthenticatedUserByUsername(username);

        if (userDetails == null) {
            throw new UsernameNotFoundException(LoginConstants.KEYINVALIDUSER);
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
