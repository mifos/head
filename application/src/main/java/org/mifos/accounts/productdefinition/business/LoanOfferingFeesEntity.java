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

package org.mifos.accounts.productdefinition.business;

import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;

public class LoanOfferingFeesEntity extends PersistentObject {

    private final Short prdOfferingFeeId;

    private final LoanOfferingBO loanOffering;

    private final FeeBO fees;

    public LoanOfferingFeesEntity(LoanOfferingBO loanOffering, FeeBO fees) {
        this.prdOfferingFeeId = null;
        this.loanOffering = loanOffering;
        this.fees = fees;
    }

    protected LoanOfferingFeesEntity() {
        this.prdOfferingFeeId = null;
        this.loanOffering = null;
        this.fees = null;
    }

    public FeeBO getFees() {
        return this.fees;
    }

    public Short getPrdOfferingFeeId() {
        return prdOfferingFeeId;
    }

    public boolean isFeePresent(Short feeId) {
        return fees.getFeeId().equals(feeId);
    }

    @Override
    public boolean equals(Object object) {
        LoanOfferingFeesEntity prdOfferingFees = null;
        boolean value = false;
        if (object != null) {
            prdOfferingFees = (LoanOfferingFeesEntity) object;
            if (prdOfferingFees.getPrdOfferingFeeId().equals(this.prdOfferingFeeId)) {
                value = true;
            }
        }
        MifosLogManager.getLogger(LoggerConstants.PRDDEFINITIONLOGGER).info("In Equals of loanOffering fund:" + value);
        return value;
    }
}
