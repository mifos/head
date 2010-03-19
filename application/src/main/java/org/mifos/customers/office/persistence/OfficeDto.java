package org.mifos.customers.office.persistence;

public class OfficeDto {

    private final Short id;
    private final String name;
    private final String searchId;

    public OfficeDto(final Short officeId, String officeName, String searchId) {
        this.id = officeId;
        this.name = officeName;
        this.searchId = searchId;
    }

    public String getSearchId() {
        return this.searchId;
    }

    public Short getId() {
        return this.id;
    }

    public String getName() {
        return this.name.trim();
    }
}
