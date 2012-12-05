package org.mifos.application.admin.servicefacade;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;


public interface CoaServiceFacade {
    @PreAuthorize("isFullyAuthenticated()")
    List<CoaDto> getList(Short id);

    @PreAuthorize("isFullyAuthenticated()")
    CoaDto getCoaDTO(Short id);
    
    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_MODIFY_CHART_OF_ACCOUNTS')")
    void create(CoaDto coaDto);
    
    @PreAuthorize("isFullyAuthenticated()")
    boolean canModifyCOA();
    
    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_MODIFY_CHART_OF_ACCOUNTS')")
    void delete(Short id);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_MODIFY_CHART_OF_ACCOUNTS')")
    void modify(CoaDto coaDto);
}
