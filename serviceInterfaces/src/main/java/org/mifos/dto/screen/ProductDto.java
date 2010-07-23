package org.mifos.dto.screen;

import java.util.List;

public class ProductDto {

    private final List<ProductCategoryTypeDto> productCategoryList;
    private final List<ProductMixDto> productMixList;

    public ProductDto(List<ProductCategoryTypeDto> productCategoryList, List<ProductMixDto> productMixList) {
        this.productCategoryList = productCategoryList;
        this.productMixList = productMixList;
    }

    public List<ProductCategoryTypeDto> getProductCategoryList() {
        return this.productCategoryList;
    }

    public List<ProductMixDto> getProductMixList() {
        return this.productMixList;
    }
}
