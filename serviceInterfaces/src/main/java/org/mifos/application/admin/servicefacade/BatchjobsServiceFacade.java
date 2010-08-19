package org.mifos.application.admin.servicefacade;

import java.util.List;

import javax.servlet.ServletContext;


import org.springframework.security.access.prepost.PreAuthorize;

public interface BatchjobsServiceFacade {

    @PreAuthorize("isFullyAuthenticated()")
    List<BatchjobsDto> getBatchjobs(ServletContext context);

}
