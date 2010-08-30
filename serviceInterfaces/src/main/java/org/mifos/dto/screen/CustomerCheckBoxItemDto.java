package org.mifos.dto.screen;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="should disable at filter level and also for pmd - not important for us")
public class CustomerCheckBoxItemDto extends CheckBoxItemDto {

    private String name;
    private String lookUpName;
    private final Short customerStatusId;
    private final Short customerLevelId;

    public CustomerCheckBoxItemDto(Short checklistId, String checklistName, Short checklistStatus, String name, String lookUpName,
            Short customerStatusId, Short customerLevelId) {
        super(checklistId, checklistName, checklistStatus);
        this.name = name;
        this.lookUpName = lookUpName;
        this.customerStatusId = customerStatusId;
        this.customerLevelId = customerLevelId;
    }

    public String getName() {
        return this.name;
    }

    public String getLookUpName() {
        return this.lookUpName;
    }

    public Short getCustomerStatusId() {
        return this.customerStatusId;
    }

    public Short getCustomerLevelId() {
        return this.customerLevelId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLookUpName(String lookUpName) {
        this.lookUpName = lookUpName;
    }
}
