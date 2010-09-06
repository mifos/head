package org.mifos.dto.screen;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings
public class AccountCheckBoxItemDto extends CheckBoxItemDto {

    private String name;
    private String lookUpName;
    private final Short accountStateId;
    private final Short productTypeId;

    public AccountCheckBoxItemDto(Short checklistId, String checklistName, Short checklistStatus, String name, String lookUpName,
            Short accountStateId, Short productTypeId) {
        super(checklistId, checklistName, checklistStatus);
        this.name = name;
        this.lookUpName = lookUpName;
        this.accountStateId = accountStateId;
        this.productTypeId = productTypeId;
    }

    public String getName() {
        return this.name;
    }

    public String getLookUpName() {
        return this.lookUpName;
    }

    public Short getAccountStateId() {
        return this.accountStateId;
    }

    public Short getProductTypeId() {
        return this.productTypeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLookUpName(String lookUpName) {
        this.lookUpName = lookUpName;
    }
}
