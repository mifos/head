package org.mifos.application.admin.servicefacade;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mifos.dto.screen.DefinePersonnelDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface PersonnelServiceFacade {

    @PreAuthorize("isFullyAuthenticated()")
    void searchUser(String searchString, Short userId, HttpServletRequest request);

    DefinePersonnelDto retrieveInfoForNewUserDefinition(Short officeId, Locale preferredLocale);
}
