/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.dto.screen;

import java.util.List;

public class ProductMixPreviewDto {

    private String productTypeNameKey;
    private String productName;
    private List<String> allowedProductMix;
    private List<String> notAllowedProductMix;

    public String getProductTypeNameKey() {
        return this.productTypeNameKey;
    }

    public void setProductTypeNameKey(String productTypeNameKey) {
        this.productTypeNameKey = productTypeNameKey;
    }

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<String> getAllowedProductMix() {
        return this.allowedProductMix;
    }

    public void setAllowedProductMix(List<String> allowedProductMix) {
        this.allowedProductMix = allowedProductMix;
    }

    public List<String> getNotAllowedProductMix() {
        return this.notAllowedProductMix;
    }

    public void setNotAllowedProductMix(List<String> notAllowedProductMix) {
        this.notAllowedProductMix = notAllowedProductMix;
    }

}
