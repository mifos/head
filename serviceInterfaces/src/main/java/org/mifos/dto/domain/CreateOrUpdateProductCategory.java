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

    private Short userId;
    private Short branchId;

    private Short productTypeEntityId;
    private String productCategoryName;
    private String productCategoryDesc;
    private Short productCategoryStatusId;

    public CreateOrUpdateProductCategory(Short userId, Short branchId, Short productTypeEntityId,
            String productCategoryName, String productCategoryDesc, Short productCategoryStatusId) {
        super();
        this.userId = userId;
        this.branchId = branchId;
        this.productTypeEntityId = productTypeEntityId;
        this.productCategoryName = productCategoryName;
        this.productCategoryDesc = productCategoryDesc;
        this.productCategoryStatusId = productCategoryStatusId;
    }

    public Short getUserId() {
        return this.userId;
    }
    public void setUserId(Short userId) {
        this.userId = userId;
    }
    public Short getBranchId() {
        return this.branchId;
    }
    public void setBranchId(Short branchId) {
        this.branchId = branchId;
    }
    public Short getProductTypeEntityId() {
        return this.productTypeEntityId;
    }
    public void setProductTypeEntityId(Short productTypeEntityId) {
        this.productTypeEntityId = productTypeEntityId;
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
    public Short getProductCategoryStatusId() {
        return this.productCategoryStatusId;
    }
    public void setProductCategoryStatusId(Short productCategoryStatusId) {
        this.productCategoryStatusId = productCategoryStatusId;
    }
}
