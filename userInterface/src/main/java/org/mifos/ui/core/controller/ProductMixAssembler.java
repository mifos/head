/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.ui.core.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mifos.dto.domain.ProductTypeDto;
import org.mifos.dto.screen.ProductMixPreviewDto;

public class ProductMixAssembler {

    public ProductMixFormBean createFormBean(List<ProductTypeDto> productTypes) {

        ProductMixFormBean formBean = new ProductMixFormBean();
        Map<String, String> productTypeOptions = new LinkedHashMap<String, String>();

        for (ProductTypeDto productTypeDto : productTypes) {
            productTypeOptions.put(productTypeDto.getProductTypeId().toString(), productTypeDto.getProductTypeKey());
        }

        formBean.setProductTypeOptions(productTypeOptions);
        return formBean;
    }

    public ProductMixPreviewDto createProductMixPreview(ProductMixFormBean formBean) {
        String selectedProductTypeNameKey = formBean.getProductTypeOptions().get(formBean.getProductTypeId());
        String selectedProductName = formBean.getProductNameOptions().get(formBean.getProductId());

        List<String> allowedProductMix = findMatchingProducts(formBean, formBean.getAllowed());
        List<String> notAllowedProductMix = findMatchingProducts(formBean, formBean.getNotAllowed());

        ProductMixPreviewDto preview = new ProductMixPreviewDto();
        preview.setProductTypeNameKey(selectedProductTypeNameKey);
        preview.setProductName(selectedProductName);
        preview.setAllowedProductMix(allowedProductMix);
        preview.setNotAllowedProductMix(notAllowedProductMix);

        return preview;
    }

    private List<String> findMatchingProducts(ProductMixFormBean formBean, String[] allowed) {
        List<String> allowedProductNames = new ArrayList<String>();
        if (null != allowed) {
            for (String allowedKey : allowed) {
                if (formBean.getAllowedProductOptions().containsKey(allowedKey)) {
                    allowedProductNames.add(formBean.getAllowedProductOptions().get(allowedKey));
                } else if (formBean.getNotAllowedProductOptions().containsKey(allowedKey)) {
                    allowedProductNames.add(formBean.getNotAllowedProductOptions().get(allowedKey));
                }
            }
        }

        return allowedProductNames;
    }
}