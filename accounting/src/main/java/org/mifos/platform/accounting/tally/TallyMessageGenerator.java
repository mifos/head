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

package org.mifos.platform.accounting.tally;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.platform.accounting.AccountingDto;
import org.mifos.platform.accounting.VoucherType;
import org.mifos.platform.accounting.tally.message.TallyMessage;
import org.mifos.platform.accounting.tally.message.TallyMessageBuilder;
import org.mifos.platform.accounting.tally.message.TallyMessageBuilderException;

public class TallyMessageGenerator {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    public final List<TallyMessage> generateTallyMessages(List<AccountingDto> accountingData)
            throws TallyMessageBuilderException, ParseException {
        List<TallyMessage> tallyMessages = new ArrayList<TallyMessage>();

        for (int i = 1; i < accountingData.size(); i++) {
            List<AccountingDto> voucher = new ArrayList<AccountingDto>();
            voucher.add(accountingData.get(i - 1));
            AccountingDto prevLine = accountingData.get(i - 1);
            AccountingDto currentLine = accountingData.get(i);

            while (i < accountingData.size() && prevLine.getBranchName().equals(currentLine.getBranchName())
                    && prevLine.getVoucherDate().equals(currentLine.getVoucherDate())
                    && prevLine.getVoucherType().equals(currentLine.getVoucherType())) {
                voucher.add(currentLine);
                i++;
                prevLine = accountingData.get(i - 1);

                if (i < accountingData.size()) {
                    currentLine = accountingData.get(i);
                }
            }

            VoucherType voucherType = getVoucherType(voucher.get(0).getVoucherType());
            TallyMessageBuilder builder = new TallyMessageBuilder(voucherType, voucher.get(0).getBranchName());
            builder.withVoucherDate(getVoucherDate(voucher.get(0).getVoucherDate()));

            for (AccountingDto voucherEntry : voucher) {
                builder.addCreditEntry(voucherEntry);
                builder.addDebitEntry(voucherEntry);
            }

            tallyMessages.add(builder.build());

        }
        return tallyMessages;
    }

    private Date getVoucherDate(String voucherdate) throws ParseException {
        return DATE_FORMAT.parse(voucherdate);
    }

    private VoucherType getVoucherType(String vouchertype) throws TallyMessageBuilderException {
        if (vouchertype.equalsIgnoreCase("JOURNAL")) {
            return VoucherType.JOURNAL;
        } else if (vouchertype.equalsIgnoreCase("PAYMENT")) {
            return VoucherType.PAYMENT;
        } else if (vouchertype.equalsIgnoreCase("RECEIPT")) {
            return VoucherType.RECEIPT;
        }
        throw new TallyMessageBuilderException("No such voucher type supported:" + vouchertype);
    }
}
