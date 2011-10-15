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

package org.mifos.dto.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import org.joda.time.DateTime;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID"}, justification="should disable at filter level and also for pmd - not important for us")
public class CashFlowDto implements Serializable {

    private final DateTime firstInstallmentDueDate;
    private final DateTime lastInstallmentDueDate;
    private final boolean captureCapitalLiabilityInfo;
    private final BigDecimal loanAmount;
    private final Double indebtednessRatio;
    private final Double repaymentCapacity;

    public CashFlowDto(DateTime firstInstallmentDueDate, DateTime lastInstallmentDueDate,
            boolean captureCapitalLiabilityInfo, BigDecimal loanAmount, Double indebtednessRatio, Double repaymentCapacity) {
        this.firstInstallmentDueDate = firstInstallmentDueDate;
        this.lastInstallmentDueDate = lastInstallmentDueDate;
        this.captureCapitalLiabilityInfo = captureCapitalLiabilityInfo;
        this.loanAmount = loanAmount;
        this.indebtednessRatio = indebtednessRatio;
        this.repaymentCapacity = repaymentCapacity;
    }

    public DateTime getFirstInstallmentDueDate() {
        return firstInstallmentDueDate;
    }

    public DateTime getLastInstallmentDueDate() {
        return lastInstallmentDueDate;
    }

    public boolean isCaptureCapitalLiabilityInfo() {
        return captureCapitalLiabilityInfo;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public Double getIndebtednessRatio() {
        return indebtednessRatio;
    }

    public Double getRepaymentCapacity() {
        return repaymentCapacity;
    }
}