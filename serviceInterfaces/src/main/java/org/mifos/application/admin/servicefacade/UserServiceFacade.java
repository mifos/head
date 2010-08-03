package org.mifos.application.admin.servicefacade;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.prepost.PreAuthorize;

public interface UserServiceFacade {

    @PreAuthorize("isFullyAuthenticated()")
    void searchUser(String searchString, Short userId, HttpServletRequest request);
}
