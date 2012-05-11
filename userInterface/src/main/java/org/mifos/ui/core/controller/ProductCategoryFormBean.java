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

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class ProductCategoryFormBean {

    private String productType;

    @NotEmpty
    private String productTypeId;

    @NotEmpty
    private String productCategoryName;
    
    @Size(min = 0, max = 500)
    private String productCategoryDesc;

    @NotEmpty
    private String productCategoryStatusId;

    private String globalPrdCategoryNum;

    public String getProductType() {
        return this.productType;
    }
    public void setProductType(String productType) {
        this.productType = productType;
    }
    public String getProductTypeId() {
        return this.productTypeId;
    }
    public void setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
    }
    public String getProductCategoryName() {
        return this.productCategoryName;
    }
    public void setProductCategoryName(String productCategoryName) {
        this.productCategoryName = productCategoryName;
    }
    public String getProductCategoryDesc() {
        return this.productCategoryDesc;
    }
    public void setProductCategoryDesc(String productCategoryDesc) {
        this.productCategoryDesc = productCategoryDesc;
    }
    public String getProductCategoryStatusId() {
        return this.productCategoryStatusId;
    }
    public void setProductCategoryStatusId(String productCategoryStatusId) {
        this.productCategoryStatusId = productCategoryStatusId;
    }
    public String getGlobalPrdCategoryNum() {
        return this.globalPrdCategoryNum;
    }
    public void setGlobalPrdCategoryNum(String globalPrdCategoryNum) {
        this.globalPrdCategoryNum = globalPrdCategoryNum;
    }
}
