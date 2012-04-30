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

import org.joda.time.LocalDate;

public class FundTransferDto implements Serializable {
    private static final long serialVersionUID = -5264787314987636463L;

    final String sourceGlobalAccountNum;
    final String targetGlobalAccountNum;
    final BigDecimal amount;
    final LocalDate trxnDate;
    final LocalDate receiptDate;
    final String receiptId;

    public FundTransferDto(final String sourceGlobaAccountNum, final String targetGlobalAccountNum,
            final BigDecimal amount, final LocalDate trxnDate, final LocalDate receiptDate, final String receiptId) {
        this.sourceGlobalAccountNum = sourceGlobaAccountNum;
        this.targetGlobalAccountNum = targetGlobalAccountNum;
        this.amount = amount;
        this.trxnDate = trxnDate;
        this.receiptDate = receiptDate;
        this.receiptId = receiptId;
    }

    public String getSourceGlobalAccountNum() {
        return sourceGlobalAccountNum;
    }

    public String getTargetGlobalAccountNum() {
        return targetGlobalAccountNum;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getTrxnDate() {
        return trxnDate;
    }

    public LocalDate getReceiptDate() {
        return receiptDate;
    }

    public String getReceiptId() {
        return receiptId;
    }
}
