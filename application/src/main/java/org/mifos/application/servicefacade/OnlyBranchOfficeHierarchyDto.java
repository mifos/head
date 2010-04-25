package org.mifos.application.servicefacade;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import org.mifos.customers.center.struts.action.OfficeHierarchyDto;
import org.mifos.customers.office.business.OfficeDetailsDto;

public class OnlyBranchOfficeHierarchyDto implements Serializable {

    public static final String IDENTIFIER = "branchOnlyOffices";
    
    private final List<OfficeDetailsDto> levels;
    private final String loggedInOfficeSearchId;
    private final Locale preferredLocaleOfUser;
    private final List<OfficeHierarchyDto> branchOnlyOfficeHierarchy;

    public OnlyBranchOfficeHierarchyDto(Locale preferredLocaleOfUser, List<OfficeDetailsDto> levels,
            String loggedInOfficeSearchId, List<OfficeHierarchyDto> branchOnlyOfficeHierarchy) {
        this.preferredLocaleOfUser = preferredLocaleOfUser;
        this.levels = levels;
        this.loggedInOfficeSearchId = loggedInOfficeSearchId;
        this.branchOnlyOfficeHierarchy = branchOnlyOfficeHierarchy;
    }

    public List<OfficeDetailsDto> getLevels() {
        return this.levels;
    }

    public Locale getPreferredLocaleOfUser() {
        return this.preferredLocaleOfUser;
    }

    public String getLoggedInOfficeSearchId() {
        return this.loggedInOfficeSearchId;
    }

    public List<OfficeHierarchyDto> getBranchOnlyOfficeHierarchy() {
        return this.branchOnlyOfficeHierarchy;
    }
}
