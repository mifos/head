package org.mifos.ui.core.controller;

import org.hibernate.validator.constraints.NotEmpty;

public class ProductCategoryFormBean {

    private String productType;

    @NotEmpty
    private String productTypeId;

    @NotEmpty
    private String productCategoryName;

    private String productCategoryDesc;

    @NotEmpty
    private String productCategoryStatusId;

    private String globalPrdCategoryNum;

    public String getProductType() {
        return this.productType;
    }
    public void setProductType(String productType) {
        this.productType = productType;
    }
    public String getProductTypeId() {
        return this.productTypeId;
    }
    public void setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
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
    public String getProductCategoryStatusId() {
        return this.productCategoryStatusId;
    }
    public void setProductCategoryStatusId(String productCategoryStatusId) {
        this.productCategoryStatusId = productCategoryStatusId;
    }
    public String getGlobalPrdCategoryNum() {
        return this.globalPrdCategoryNum;
    }
    public void setGlobalPrdCategoryNum(String globalPrdCategoryNum) {
        this.globalPrdCategoryNum = globalPrdCategoryNum;
    }
}
