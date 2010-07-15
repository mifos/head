package org.mifos.dto.screen;

import java.util.List;

public class ProductDto {

    //should add user context info here or those will be obtained by the course of the controller
    private final List<ProductCategoryDto> productCategoryList;
    private final List<ProductMixDto> productMixList;

    public ProductDto(List<ProductCategoryDto> productCategoryList, List<ProductMixDto> productMixList) {
        this.productCategoryList = productCategoryList;
        this.productMixList = productMixList;
    }

    public List<ProductCategoryDto> getProductCategoryList() {
        return this.productCategoryList;
    }

    public List<ProductMixDto> getProductMixList() {
        return this.productMixList;
    }
}
