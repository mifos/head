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

package org.mifos.platform.accounting.tally.message;

import java.util.Date;
import java.util.List;

import org.mifos.platform.accounting.VoucherType;

public class TallyMessage {

    private final VoucherType voucherType;

    private final Date voucherDate;

    private final List<AllLedger> allLedgers;

    public VoucherType getVoucherType() {
        return this.voucherType;
    }

    public List<AllLedger> getAllLedgers() {
        return this.allLedgers;
    }

    public Date getVoucherDate() {
        return voucherDate;
    }

    protected TallyMessage(final VoucherType voucherType, final Date voucherDate, final List<AllLedger> allLedgers) {
        super();
        this.voucherType = voucherType;
        this.allLedgers = allLedgers;
        this.voucherDate = voucherDate;
    }

    @SuppressWarnings("unused")
    private TallyMessage() {
        this(null, null, null);
    }

    @Override
    public String toString() {
        String string = "";
        for (AllLedger allLedger : allLedgers) {
            string += allLedger + "\n";
        }
        string = voucherType + ";" + voucherDate + ";" + "\n" + string;
        return string;
    }
}
