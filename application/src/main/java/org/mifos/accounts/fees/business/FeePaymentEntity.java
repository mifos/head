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

package org.mifos.accounts.fees.business;

import java.util.Set;

import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MasterDataEntity;

public class FeePaymentEntity extends MasterDataEntity {

    public FeePaymentEntity(FeePayment feePayment) {
        this.id = feePayment.getValue();
    }

    protected FeePaymentEntity() {
    }

    public boolean isTimeOfDisbursement() {
        return getId().equals(FeePayment.TIME_OF_DISBURSEMENT.getValue());
    }

    public FeePayment getFeePayment() {
        FeePayment[] feePayments = FeePayment.values();
        for (FeePayment feePayment : feePayments) {
            if (getId().equals(feePayment.getValue())) {
                return feePayment;
            }
        }
        return null;
    }

    private Short localeId;

    /** The composite primary key value */
    private Short id;

    /** The value of the lookupValue association. */
    private LookUpValueEntity lookUpValue;

    public Short getId() {
        return id;
    }

    protected void setId(Short id) {
        this.id = id;
    }

    public LookUpValueEntity getLookUpValue() {
        return lookUpValue;
    }

    protected void setLookUpValue(LookUpValueEntity lookUpValue) {
        this.lookUpValue = lookUpValue;
    }

    public Short getLocaleId() {
        return localeId;
    }

    public void setLocaleId(Short localeId) {
        this.localeId = localeId;
    }

    public String getName() {
        String name = MessageLookup.getInstance().lookup(getLookUpValue());
        return name;

    }

    public Set<LookUpValueLocaleEntity> getNames() {
        return getLookUpValue().getLookUpValueLocales();
    }

    protected void setName(String name) {
        MessageLookup.getInstance().updateLookupValue(getLookUpValue(), name);
    }
}
