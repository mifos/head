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

package org.mifos.dto.domain;

public class CreateOrUpdateProductCategory {

    private final Short productTypeEntityId;
    private final String productCategoryName;
    private final String productCategoryDesc;
    private final Short productCategoryStatusId;
    private final String globalPrdCategoryNum;

    public CreateOrUpdateProductCategory(Short productTypeEntityId, String productCategoryName,
            String productCategoryDesc, Short productCategoryStatusId, String globalPrdCategoryNum) {
        this.productTypeEntityId = productTypeEntityId;
        this.productCategoryName = productCategoryName;
        this.productCategoryDesc = productCategoryDesc;
        this.productCategoryStatusId = productCategoryStatusId;
        this.globalPrdCategoryNum = globalPrdCategoryNum;
    }

    public Short getProductTypeEntityId() {
        return this.productTypeEntityId;
    }

    public String getProductCategoryName() {
        return this.productCategoryName;
    }

    public String getProductCategoryDesc() {
        return this.productCategoryDesc;
    }

    public Short getProductCategoryStatusId() {
        return this.productCategoryStatusId;
    }

    public String getGlobalPrdCategoryNum() {
        return this.globalPrdCategoryNum;
    }
}