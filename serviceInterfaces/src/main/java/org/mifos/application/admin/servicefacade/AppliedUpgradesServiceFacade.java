package org.mifos.application.admin.servicefacade;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

public interface AppliedUpgradesServiceFacade {
    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_VIEW_SYSTEM_INFO')")
    List<Integer> getAppliedUpgrades();
}
