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

package org.mifos.accounts.productdefinition.util.helpers;

import org.mifos.framework.business.service.DataTransferObject;

/**
 * This is a helper class which would be used when we need only certain details
 * of a product offering hence instead of loading the entire product offering
 * object we can load only this object using a query.
 */
public class PrdOfferingDto implements DataTransferObject {

    private Short prdOfferingId;

    private String prdOfferingName;

    private String globalPrdOfferingNum;

    public PrdOfferingDto() {
        super();
    }

    public PrdOfferingDto(Short prdOfferingId, String prdOfferingName, String globalPrdOfferingNum) {
        this.globalPrdOfferingNum = globalPrdOfferingNum;
        this.prdOfferingName = prdOfferingName;
        this.prdOfferingId = prdOfferingId;

    }

    public String getGlobalPrdOfferingNum() {
        return globalPrdOfferingNum;
    }

    public void setGlobalPrdOfferingNum(String globalPrdOfferingNum) {
        this.globalPrdOfferingNum = globalPrdOfferingNum;
    }

    public Short getPrdOfferingId() {
        return prdOfferingId;
    }

    public void setPrdOfferingId(Short prdOfferingId) {
        this.prdOfferingId = prdOfferingId;
    }

    public String getPrdOfferingName() {
        return prdOfferingName;
    }

    public void setPrdOfferingName(String prdOfferingName) {
        this.prdOfferingName = prdOfferingName;
    }
}