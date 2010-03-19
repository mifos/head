package org.mifos.customers.center.struts.action;

import java.util.List;

public class OfficeHierarchyDto implements Comparable<OfficeHierarchyDto> {

    private final Short officeId;
    private final String officeName;
    private final String searchId;
    private final boolean active;
    private final List<OfficeHierarchyDto> children;

    public OfficeHierarchyDto(Short officeId, String officeName, String searchId, boolean active,
            List<OfficeHierarchyDto> children) {
        this.officeId = officeId;
        this.officeName = officeName;
        this.searchId = searchId;
        this.active = active;
        this.children = children;
    }

    public String getOfficeName() {
        return this.officeName;
    }

    public List<OfficeHierarchyDto> getChildren() {
        return this.children;
    }

    public Short getOfficeId() {
        return this.officeId;
    }

    public String getSearchId() {
        return this.searchId;
    }

    public boolean isActive() {
        return this.active;
    }

    @Override
    public int compareTo(OfficeHierarchyDto other) {
        return officeName.compareTo(other.getOfficeName());
    }
}
