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

package org.mifos.dto.screen;

import java.util.List;

import org.mifos.dto.domain.PrdOfferingDto;

public class ProductMixDetailsDto {

    private final Short prdOfferingId;
    private final String prdOfferingName;
    private final Short productTypeID;

    private final List<PrdOfferingDto> allowedPrdOfferingNames;
    private final List<PrdOfferingDto> notAllowedPrdOfferingNames;

    public ProductMixDetailsDto(Short prdOfferingId, String prdOfferingName, Short productTypeID,
            List<PrdOfferingDto> allowedPrdOfferingNames, List<PrdOfferingDto> notAllowedPrdOfferingNames) {
        this.prdOfferingId = prdOfferingId;
        this.prdOfferingName = prdOfferingName;
        this.productTypeID = productTypeID;
        this.allowedPrdOfferingNames = allowedPrdOfferingNames;
        this.notAllowedPrdOfferingNames = notAllowedPrdOfferingNames;
    }

    public Short getPrdOfferingId() {
        return this.prdOfferingId;
    }

    public String getPrdOfferingName() {
        return this.prdOfferingName;
    }

    public Short getProductTypeID() {
        return this.productTypeID;
    }

    public List<PrdOfferingDto> getAllowedPrdOfferingNames() {
        return this.allowedPrdOfferingNames;
    }

    public List<PrdOfferingDto> getNotAllowedPrdOfferingNames() {
        return this.notAllowedPrdOfferingNames;
    }
}