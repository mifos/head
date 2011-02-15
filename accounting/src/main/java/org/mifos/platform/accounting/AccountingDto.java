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

package org.mifos.platform.accounting;

public class AccountingDto {

    private final String branchName;
    private final String voucherDate;
    private final String voucherType;
    private final String glCode;
    private final String glCodeName;
    private final String debit;
    private final String credit;

    public AccountingDto(String branchName, String voucherDate, String voucherType, String glCode, String glCodeName,
            String debit, String credit) {
        super();
        this.branchName = branchName;
        this.voucherDate = voucherDate;
        this.voucherType = voucherType;
        this.glCode = glCode;
        this.glCodeName = glCodeName;
        this.debit = debit;
        this.credit = credit;
    }

    public final String getBranchName() {
        return branchName;
    }

    public final String getVoucherDate() {
        return voucherDate;
    }

    public final String getVoucherType() {
        return voucherType;
    }

    public final String getGlCode() {
        return glCode;
    }

    public final String getGlCodeName() {
        return glCodeName;
    }

    public final String getDebit() {
        return debit;
    }

    public final String getCredit() {
        return credit;
    }

    // branchname;voucherdate;vouchertype;glcode;glname;debit;credit
    @Override
    public final String toString() {
        return branchName + ";" + voucherDate + ";" + voucherType + ";" + glCode + ";" + glCodeName + ";" + debit + ";"
                + credit;
    }

}
