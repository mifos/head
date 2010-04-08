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

package org.mifos.customers.client.business;

import static org.apache.commons.lang.math.NumberUtils.SHORT_ZERO;

import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.PrdOfferingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.util.helpers.Transformer;

public class LoanCounter extends AbstractEntity {
    public static Transformer<LoanCounter, Short> TRANSFORM_LOAN_COUNTER_TO_LOAN_CYCLE = new Transformer<LoanCounter, Short>() {
        public Short transform(LoanCounter input) {
            return input.getLoanCycleCounter();
        }
    };

    private final Integer loanCounterId;

    private final ClientPerformanceHistoryEntity clientPerfHistory;

    private Short loanCycleCounter = SHORT_ZERO;

    private final LoanOfferingBO loanOffering;

    protected LoanCounter() {
        this.loanCounterId = null;
        this.clientPerfHistory = null;
        this.loanOffering = null;
        this.loanCycleCounter = 0;
    }

    public LoanCounter(ClientPerformanceHistoryEntity clientPerfHistory, LoanOfferingBO loanOffering,
            YesNoFlag counterFlag) {
        this.loanCounterId = null;
        this.clientPerfHistory = clientPerfHistory;
        this.loanOffering = loanOffering;
        updateLoanCounter(counterFlag);
    }

    public ClientPerformanceHistoryEntity getClientPerfHistory() {
        return clientPerfHistory;
    }

    public LoanOfferingBO getLoanOffering() {
        return loanOffering;
    }

    public Integer getLoanCounterId() {
        return loanCounterId;
    }

    public Short getLoanCycleCounter() {
        return loanCycleCounter;
    }

    void setLoanCycleCounter(Short loanCycleCounter) {
        this.loanCycleCounter = loanCycleCounter;
    }

    void updateLoanCounter(YesNoFlag counterFlag) {
        if (counterFlag.yes()) {
            this.loanCycleCounter++;
        } else {
            this.loanCycleCounter--;
        }
    }

    public boolean isOfSameProduct(PrdOfferingBO prdOffering) {
        return loanOffering.isOfSameOffering(prdOffering);
    }
}
