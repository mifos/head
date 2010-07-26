package org.mifos.dto.screen;

import java.util.List;

public class ProductCategoryDisplayDto {

    private final List<ProductCategoryTypeDto> productCategoryTypeList;
    private final List<ProductCategoryDto> productCategoryDtoList;

    public ProductCategoryDisplayDto(List<ProductCategoryTypeDto> productCategoryTypeList, List<ProductCategoryDto> productCategoryDtoList) {
        this.productCategoryTypeList = productCategoryTypeList;
        this.productCategoryDtoList = productCategoryDtoList;
    }

    public List<ProductCategoryTypeDto> getProductCategoryTypeList() {
        return this.productCategoryTypeList;
    }

    public List<ProductCategoryDto> getProductCategoryDtoList() {
        return this.productCategoryDtoList;
    }
}
