/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.application.master.business;

import org.mifos.accounts.productdefinition.business.ProductTypeEntity;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;

public class InterestTypesEntity extends MasterDataEntity {
    private java.lang.String descripton;
    private ProductTypeEntity productType;

    protected InterestTypesEntity() {
        super();
    }

    public InterestTypesEntity(InterestType interestType) {
        super(interestType.getValue());
    }

    public java.lang.String getDescripton() {
        return descripton;
    }

    public void setDescripton(java.lang.String descripton) {
        this.descripton = descripton;
    }

    public ProductTypeEntity getProductType() {
        return productType;
    }

    public void setProductType(ProductTypeEntity productType) {
        this.productType = productType;
    }

    public InterestType asEnum() {
        return InterestType.fromInt(getId());
    }

}
