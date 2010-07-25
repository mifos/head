package org.mifos.dto.domain;


public class CreateOrUpdateProductCategory {

    private Short userId;
    private Short branchId;

    private Short productTypeEntityId;
    private String productCategoryName;
    private String productCategoryDesc;
    private Short productCategoryStatusId;

    public CreateOrUpdateProductCategory(Short userId, Short branchId, Short productTypeEntityId,
            String productCategoryName, String productCategoryDesc, Short productCategoryStatusId) {
        super();
        this.userId = userId;
        this.branchId = branchId;
        this.productTypeEntityId = productTypeEntityId;
        this.productCategoryName = productCategoryName;
        this.productCategoryDesc = productCategoryDesc;
        this.productCategoryStatusId = productCategoryStatusId;
    }

    public Short getUserId() {
        return this.userId;
    }
    public void setUserId(Short userId) {
        this.userId = userId;
    }
    public Short getBranchId() {
        return this.branchId;
    }
    public void setBranchId(Short branchId) {
        this.branchId = branchId;
    }
    public Short getProductTypeEntityId() {
        return this.productTypeEntityId;
    }
    public void setProductTypeEntityId(Short productTypeEntityId) {
        this.productTypeEntityId = productTypeEntityId;
    }
    public String getProductCategoryName() {
        return this.productCategoryName;
    }
    public void setProductCategoryName(String productCategoryName) {
        this.productCategoryName = productCategoryName;
    }
    public String getProductCategoryDesc() {
        return this.productCategoryDesc;
    }
    public void setProductCategoryDesc(String productCategoryDesc) {
        this.productCategoryDesc = productCategoryDesc;
    }
    public Short getProductCategoryStatusId() {
        return this.productCategoryStatusId;
    }
    public void setProductCategoryStatusId(Short productCategoryStatusId) {
        this.productCategoryStatusId = productCategoryStatusId;
    }
}
