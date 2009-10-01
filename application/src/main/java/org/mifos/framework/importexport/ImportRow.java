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

package org.mifos.framework.importexport;

import java.util.Date;

public class ImportRow {
    private final Date transactionDate;
    private final String accountId;
    private final Double amount;
    private final String comment;

    public ImportRow(final Date transactionDate, final String accountId, final Double amount, final String comment) {
        this.transactionDate = transactionDate;
        this.accountId = accountId;
        this.amount = amount;
        this.comment = comment;
    }

    public Date getTransactionDate() {
        return this.transactionDate;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public Double getAmount() {
        return this.amount;
    }

    public String getComment() {
        return this.comment;
    }
}
