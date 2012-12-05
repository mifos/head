package org.mifos.application.admin.servicefacade;

public class CoaDto {

    private Short accountId;

    private Short parentId;
    
    private String parentGlCode;
    
    private String accountName;
    
    private String glCode;
    
    private boolean modifiable;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getGlCodeString() {
        return glCode;
    }

    public void setGlCodeString(String glCode) {
        this.glCode = glCode;
    }

    public boolean isModifiable() {
        return modifiable;
    }

    public void setModifiable(boolean modifiable) {
        this.modifiable = modifiable;
    }

    public String getParentGlCode() {
        return parentGlCode;
    }

    public void setParentGlCode(String parentGlCode) {
        this.parentGlCode = parentGlCode;
    }

    public Short getParentId() {
        return parentId;
    }

    public void setParentId(Short parentId) {
        this.parentId = parentId;
    }

    public Short getAccountId() {
        return accountId;
    }

    public void setAccountId(Short accountId) {
        this.accountId = accountId;
    }
}
