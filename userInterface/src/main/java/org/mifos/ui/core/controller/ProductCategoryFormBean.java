package org.mifos.ui.core.controller;

public class ProductCategoryFormBean {

    private String productType;
    private Short productTypeId;
    private String productCategoryName;
    private String productCategoryDesc;
    private Short productCategoryStatusId;
    private String globalPrdCategoryNum;

    public String getProductType() {
        return this.productType;
    }
    public void setProductType(String productType) {
        this.productType = productType;
    }
    public Short getProductTypeId() {
        return this.productTypeId;
    }
    public void setProductTypeId(Short productTypeId) {
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
    public Short getProductCategoryStatusId() {
        return this.productCategoryStatusId;
    }
    public void setProductCategoryStatusId(Short productCategoryStatusId) {
        this.productCategoryStatusId = productCategoryStatusId;
    }
    public String getGlobalPrdCategoryNum() {
        return this.globalPrdCategoryNum;
    }
    public void setGlobalPrdCategoryNum(String globalPrdCategoryNum) {
        this.globalPrdCategoryNum = globalPrdCategoryNum;
    }
}
