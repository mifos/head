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

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.platform.accounting.AccountingDto;
import org.mifos.platform.accounting.VoucherType;
import org.mifos.platform.accounting.tally.message.AllLedger;
import org.mifos.platform.accounting.tally.message.TallyMessage;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TallyXMLGenerator {

    private static Configuration freemarkerConfiguration;

    private static TallyMessageGenerator tallyMessageGenerator = new TallyMessageGenerator();

    private static SimpleDateFormat tallyDateFormat = new SimpleDateFormat("yyyyMMdd");

    public static String getTallyXML(List<AccountingDto> accountingData, String fileName) throws Exception {

        Template temp = buildFreemarkerConfiguration().getTemplate("master.template");
        String masterData = "";
        try {
            List<TallyMessage> tallyMessages = tallyMessageGenerator.generateTallyMessages(accountingData);
            masterData = getMasterData(tallyMessages, fileName);
        } catch (Exception e) {
            masterData = "Contact admin: There was an error encountered :";
            masterData += e.getMessage();
            return masterData;
        }

        /* Create a data-model */
        Map<String, Object> root = new HashMap<String, Object>();
        Map<String, Object> tallyMessage = new HashMap<String, Object>();
        root.put("tallyMessage", tallyMessage);
        tallyMessage.put("headOfficeName", "HEAD OFFICE");
        tallyMessage.put("data", masterData);
        StringWriter bow = new StringWriter();
        temp.process(root, bow);
        return bow.toString();
    }

    private static String getMasterData(List<TallyMessage> tallyMessages, String fileName) throws IOException,
            TemplateException {
        String tallyMessagesOutput = "";
        for (TallyMessage tallyMessage : tallyMessages) {
            tallyMessagesOutput += getTallyMessageData(tallyMessage, fileName);
        }
        return tallyMessagesOutput;
    }

    private static String getTallyMessageData(TallyMessage tallyMessage, String fileName) throws TemplateException,
            IOException {
        Template temp = buildFreemarkerConfiguration().getTemplate("tally_mesage.template");
        /* Create a data-model */
        Map<String, Object> root = new HashMap<String, Object>();
        Map<String, Object> voucher = new HashMap<String, Object>();
        root.put("voucher", voucher);
        voucher.put("type", tallyMessage.getVoucherType().value);
        voucher.put("date", tallyDateFormat.format(tallyMessage.getVoucherDate()));
        voucher.put("fileName", fileName);
        voucher.put("data", getVoucherData(tallyMessage));
        StringWriter bow = new StringWriter();
        temp.process(root, bow);
        return bow.toString() + "\n";
    }

    private static String getVoucherData(TallyMessage tallyMessage) throws TemplateException, IOException {
        String allLedgersOutput = "";
        // see payment specs for tally integration
        if (tallyMessage.getVoucherType() == VoucherType.PAYMENT) {
            // Add Debit Accounts
            for (AllLedger allLedger : tallyMessage.getAllLedgers()) {
                if (allLedger.getIsDeemedPositive().equals(Boolean.TRUE)) {
                    allLedgersOutput += getAllLedgerData(allLedger);
                }
            }
            // Add Credit Accounts
            for (AllLedger allLedger : tallyMessage.getAllLedgers()) {
                if (allLedger.getIsDeemedPositive().equals(Boolean.FALSE)) {
                    allLedgersOutput += getAllLedgerData(allLedger);
                }
            }

        } else {
            for (AllLedger allLedger : tallyMessage.getAllLedgers()) {
                allLedgersOutput += getAllLedgerData(allLedger);
            }
        }
        return allLedgersOutput.substring(0, allLedgersOutput.length() - 1);
    }

    private static String getAllLedgerData(AllLedger allLedger) throws TemplateException, IOException {
        Template temp = buildFreemarkerConfiguration().getTemplate("all_ledgers.template");
        /* Create a data-model */
        Map<String, Object> root = new HashMap<String, Object>();
        Map<String, Object> ledger = new HashMap<String, Object>();
        root.put("ledger", ledger);
        ledger.put("name", allLedger.getLedgerName());
        if (allLedger.getIsDeemedPositive()) {
            ledger.put("isDeemedPositive", "Yes");
            ledger.put("amount", "-" + allLedger.getAmount());
        } else {
            ledger.put("isDeemedPositive", "No");
            ledger.put("amount", allLedger.getAmount());
        }
        ledger.put("branchName", allLedger.getBranchName());
        StringWriter bow = new StringWriter();
        temp.process(root, bow);
        return bow.toString() + "\n";
    }

    private static Configuration buildFreemarkerConfiguration() {
        if (freemarkerConfiguration == null) {
            freemarkerConfiguration = new Configuration();
            freemarkerConfiguration.setClassForTemplateLoading(TallyXMLGenerator.class, "");
            freemarkerConfiguration.setObjectWrapper(new DefaultObjectWrapper());
        }
        return freemarkerConfiguration;
    }

}
