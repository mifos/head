package org.mifos.dto.screen;

public class ProductCategoryTypeDto {

    private final Short productTypeID;
    private final String productName;

    public ProductCategoryTypeDto(Short productTypeID, String productName) {
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
