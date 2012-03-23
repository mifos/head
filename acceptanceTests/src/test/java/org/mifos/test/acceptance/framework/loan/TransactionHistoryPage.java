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

package org.mifos.test.acceptance.framework.loan;

import junit.framework.Assert;

import org.mifos.test.acceptance.framework.AbstractPage;

import com.thoughtworks.selenium.Selenium;

@SuppressWarnings("PMD.SystemPrintln")
public class TransactionHistoryPage extends AbstractPage {

    public final static String TABLE_ID = "trxnhistoryList";
    public final static String TYPE_LOAN_DISBURSEMENT = "Loan Disbursement";

    public TransactionHistoryPage(Selenium selenium) {
        super(selenium);
        verifyPage("ViewTransactionHistory");
    }

    /**
     * verifies if the table has reversed transactions
     * @param payAmount
     * @param reversedNumber
     * @param notes
     */
    public void verifyTableForReversedValues(String payAmount, int reversedNumber, String notes) {
        for(int i = 0; i < reversedNumber; i++) {
            int row = i * 8 + 1;
            float amount = 0;
            for(int j = 0; j < 8; j++) {
                String value = getCredit(row+j);
                if(!value.contains("-")) {
                    amount += Float.parseFloat(value);
                }
            }
            Assert.assertEquals(amount, Float.parseFloat(payAmount)*2);
            Assert.assertEquals(getNotes(row), notes+(i+1));
            Assert.assertEquals(getNotes(row+1), notes+(i+1));
            Assert.assertEquals(getNotes(row+2), notes+(i+1));
            Assert.assertEquals(getNotes(row+3), notes+(i+1));
        }
    }

    /**
     * Verifies transaction history with total amount paid, and number of transactions.
     * @param amountPaid - the amount client paid
     * @param transactionCount - number of transactions made by client
     * @param maxRowCount - maximum number of rows in transaction table
     */
    public void verifyTransactionHistory(double amountPaid, int transactionCount, int maxRowCount) {
        double debitSum = 0;
        String paymentID = "";
        int paymentCount = 0;

        for(int i = 1;i <= maxRowCount; i++) {
            String debitValue = getDebit(i);
            if(!"-".equals(debitValue)) {
                String glCode = getGLCode(i);
                if(TYPE_LOAN_DISBURSEMENT.equals(getType(i))) {
                    continue;
                }
                if("11201 - Bank Account 1".equals(glCode)) {
                    debitSum += Double.valueOf(debitValue);
                }
                else if("5001 - Interest".equals(glCode) || "1505 - WFLoan".equals(glCode)) {
                    debitSum -= Double.valueOf(debitValue);
                }
                String rowPaymentID = getPaymentID(i);
                if(!paymentID.equals(rowPaymentID)) {
                    paymentID = rowPaymentID;
                    paymentCount++;
                }
            }
        }
        Assert.assertEquals(amountPaid, debitSum, amountPaid / 1000.0);
        Assert.assertEquals(transactionCount, paymentCount);
    }

    /**
     * Verifies transaction history with user name
     * @param userName
     */
    public void verifyPostedBy(String userName, int maxRowCount) {
        for(int i = 1;i <= maxRowCount; i++) {
            Assert.assertEquals(userName, getPostedBy(i));
        }
    }

    public String getPaymentID(int row) {
        return selenium.getTable(TABLE_ID+"."+row+".1");
    }

    public String getType(int row) {
        return selenium.getTable(TABLE_ID+"."+row+".3");
    }

    public String getGLCode(int row) {
        return selenium.getTable(TABLE_ID+"."+row+".4");
    }

    public String getDebit(int row) {
        return selenium.getTable(TABLE_ID+"."+row+".5");
    }

    public String getCredit(int row) {
        return selenium.getTable(TABLE_ID+"."+row+".6");
    }

    public String getPostedBy(int row){
        return selenium.getTable(TABLE_ID+"."+row+".9");
    }

    public String getNotes(int row) {
        return selenium.getTable(TABLE_ID+"."+row+".10");
    }
}
