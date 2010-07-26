package org.mifos.dto.screen;

public class ProductCategoryDto {

    private final String productCategoryName;
    private final Short productCategoryStatusId;
    private final String globalProductCategoryNumber;

    public ProductCategoryDto(String productCategoryName, Short productCategoryStatusId, String globalProductCategoryNumber) {
        this.productCategoryName = productCategoryName;
        this.productCategoryStatusId = productCategoryStatusId;
        this.globalProductCategoryNumber = globalProductCategoryNumber;
    }

    public String getProductCategoryName() {
        return this.productCategoryName;
    }
    public Short getProductCategoryStatusId() {
        return this.productCategoryStatusId;
    }
    public String getGlobalProductCategoryNumber() {
        return this.globalProductCategoryNumber;
    }
}
