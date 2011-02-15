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

package org.mifos.platform.accounting.tally.message;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.platform.accounting.AccountingDto;
import org.mifos.platform.accounting.VoucherType;

public class TallyMessageBuilder {

    private final VoucherType voucherType;

    private final String branchName;

    private Date voucherDate;

    private final List<AllLedger> allLedgers = new ArrayList<AllLedger>();

    public TallyMessageBuilder(VoucherType voucherType, String branchName) throws TallyMessageBuilderException {
        this.voucherType = voucherType;
        this.branchName = branchName;
        if (this.voucherType == null) {
            throw new TallyMessageBuilderException("Voucher type can not be null.");
        }
        if (this.branchName == null || branchName.equals("")) {
            throw new TallyMessageBuilderException("Branch name can not be empty.");
        }
    }

    public final TallyMessageBuilder addCreditEntry(AccountingDto voucherEntry) throws TallyMessageBuilderException {
        BigDecimal amount = new BigDecimal(voucherEntry.getCredit());
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new TallyMessageBuilderException("Negative credit amount found:");
        }
        this.addAllLegderEntry(voucherEntry.getCredit(), voucherEntry.getGlCode(), false);
        return this;
    }

    public final TallyMessageBuilder addDebitEntry(AccountingDto voucherEntry) throws TallyMessageBuilderException {
        BigDecimal amount = new BigDecimal(voucherEntry.getDebit());
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new TallyMessageBuilderException("Negative debit amount found:");
        }
        this.addAllLegderEntry(voucherEntry.getDebit(), voucherEntry.getGlCode(), true);
        return this;
    }

    public final TallyMessageBuilder withVoucherDate(Date voucherDate) throws TallyMessageBuilderException {
        this.voucherDate = voucherDate;
        return this;
    }

    private TallyMessageBuilder addAllLegderEntry(String amount, String ledgerName, Boolean isDeemedPositive) {
        allLedgers.add(new AllLedger(ledgerName, isDeemedPositive, amount, branchName));
        return this;
    }

    public final TallyMessage build() throws TallyMessageBuilderException {
        if (voucherDate == null) {
            throw new TallyMessageBuilderException("Voucher date is required before building");
        }
        return new TallyMessage(voucherType, voucherDate, allLedgers);
    }
}