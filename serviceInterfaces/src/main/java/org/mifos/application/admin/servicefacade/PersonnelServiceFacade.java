package org.mifos.application.admin.servicefacade;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mifos.dto.domain.CreateOrUpdatePersonnelInformation;
import org.mifos.dto.screen.DefinePersonnelDto;
import org.mifos.dto.screen.PersonnelInformationDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface PersonnelServiceFacade {

    @PreAuthorize("isFullyAuthenticated()")
    void searchUser(String searchString, Short userId, HttpServletRequest request);

    @PreAuthorize("isFullyAuthenticated()")
    DefinePersonnelDto retrieveInfoForNewUserDefinition(Short officeId, Locale preferredLocale);

    @PreAuthorize("isFullyAuthenticated()")
    PersonnelInformationDto getPersonnelInformationDto(String globalCustNum);

    @PreAuthorize("isFullyAuthenticated()")
    String createPersonnelInformation(CreateOrUpdatePersonnelInformation personnel);
}
