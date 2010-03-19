package org.mifos.application.servicefacade;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import org.mifos.customers.center.struts.action.OfficeHierarchyDto;
import org.mifos.customers.office.business.OfficeView;

public class OnlyBranchOfficeHierarchyDto implements Serializable {

    public static final String IDENTIFIER = "branchOnlyOffices";
    
    private final List<OfficeView> levels;
    private final String loggedInOfficeSearchId;
    private final Locale preferredLocaleOfUser;
    private final List<OfficeHierarchyDto> branchOnlyOfficeHierarchy;

    public OnlyBranchOfficeHierarchyDto(Locale preferredLocaleOfUser, List<OfficeView> levels,
            String loggedInOfficeSearchId, List<OfficeHierarchyDto> branchOnlyOfficeHierarchy) {
        this.preferredLocaleOfUser = preferredLocaleOfUser;
        this.levels = levels;
        this.loggedInOfficeSearchId = loggedInOfficeSearchId;
        this.branchOnlyOfficeHierarchy = branchOnlyOfficeHierarchy;
    }

    public List<OfficeView> getLevels() {
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
