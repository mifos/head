/*
 * Copyright Grameen Foundation USA
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
