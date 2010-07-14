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


public class ProductMixDto {

    private final Short productCategoryTypeID;
    private final Short prdOfferingId;
    private final Short productTypeID;
    private final String prdOfferingName;

    public ProductMixDto(Short productCategoryTypeID, Short prdOfferingId, Short productTypeID, String prdOfferingName) {
        this.productCategoryTypeID = productCategoryTypeID;
        this.prdOfferingId = prdOfferingId;
        this.productTypeID = productTypeID;
        this.prdOfferingName = prdOfferingName;
    }

    public Short getProductCategoryTypeID() {
        return this.productCategoryTypeID;
    }

    public Short getPrdOfferingId() {
        return this.prdOfferingId;
    }

    public Short getProductTypeID() {
        return this.productTypeID;
    }

    public String getPrdOfferingName() {
        return this.prdOfferingName;
    }
}