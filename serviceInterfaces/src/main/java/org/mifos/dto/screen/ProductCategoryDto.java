package org.mifos.dto.screen;

public class ProductCategoryDto {

    private final Short productTypeID;
    private final String productName;

    public ProductCategoryDto(Short productTypeID, String productName) {
        this.productTypeID = productTypeID;
        this.productName = productName;
    }

    public Short getProductTypeID() {
        return this.productTypeID;
    }

    public String getProductName() {
        return this.productName;
    }
}
