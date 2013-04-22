package org.mifos.config.servicefacade;

import java.io.IOException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public interface LogoServiceFacade {

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_CHANGE_MIFOS_LOGO')")
    void uploadNewLogo(CommonsMultipartFile file) throws IOException;

}
