package org.mifos.dto.screen;

@edu.umd.cs.findbugs.annotations.SuppressWarnings
public class ProductCategoryDetailsDto {

    private final String productCategoryName;
    private final Short productCategoryStatusId;
    private final Short productTypeId;
    private final String productCategoryDesc;
    private final String productTypeName;

    public ProductCategoryDetailsDto(String productCategoryName, Short productCategoryStatusId, Short productTypeId,
            String productCategoryDesc, String productTypeName) {
        this.productCategoryName = productCategoryName;
        this.productCategoryStatusId = productCategoryStatusId;
        this.productTypeId = productTypeId;
        this.productCategoryDesc = productCategoryDesc;
        this.productTypeName = productTypeName;
    }

    public String getProductCategoryName() {
        return this.productCategoryName;
    }
    public Short getProductCategoryStatusId() {
        return this.productCategoryStatusId;
    }
    public Short getProductTypeId() {
        return this.productTypeId;
    }
    public String getProductCategoryDesc() {
        return this.productCategoryDesc;
    }
    public String getProductTypeName() {
        return this.productTypeName;
    }
}
