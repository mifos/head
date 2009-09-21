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

package org.mifos.test.acceptance.framework.savings;

public class DepositWithdrawalSavingsParameters {
    public static final String CASH = "Cash";
    public static final String CHEQUE = "Cheque";
    public static final String DEPOSIT = "Deposit";
    public static final String WITHDRAWAL = "Withdrawal";

    private String trxnDateDD;
    private String trxnDateMM;
    private String trxnDateYYYY;
    private String savingsReceiptId;
    private String savingsReceiptDateDD;
    private String savingsReceiptDateMM;
    private String savingsReceiptDateYYYY;
    private String amount;
    private String paymentType;
    private String trxnType;

    public String getTrxnDateDD() {
        return this.trxnDateDD;
    }

    public void setTrxnDateDD(String trxnDateDD) {
        this.trxnDateDD = trxnDateDD;
    }

    public String getTrxnDateMM() {
        return this.trxnDateMM;
    }

    public void setTrxnDateMM(String trxnDateMM) {
        this.trxnDateMM = trxnDateMM;
    }

    public String getTrxnDateYYYY() {
        return this.trxnDateYYYY;
    }

    public void setTrxnDateYYYY(String trxnDateYYYY) {
        this.trxnDateYYYY = trxnDateYYYY;
    }

    public String getReceiptId() {
        return this.savingsReceiptId;
    }

    public void setReceiptId(String receiptId) {
        this.savingsReceiptId = receiptId;
    }

    public String getReceiptDateDD() {
        return this.savingsReceiptDateDD;
    }

    public void setReceiptDateDD(String receiptDateDD) {
        this.savingsReceiptDateDD = receiptDateDD;
    }

    public String getReceiptDateMM() {
        return this.savingsReceiptDateMM;
    }

    public void setReceiptDateMM(String receiptDateMM) {
        this.savingsReceiptDateMM = receiptDateMM;
    }

    public String getReceiptDateYYYY() {
        return this.savingsReceiptDateYYYY;
    }

    public void setReceiptDateYYYY(String receiptDateYYYY) {
        this.savingsReceiptDateYYYY = receiptDateYYYY;
    }

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPaymentType() {
        return this.paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getTrxnType() {
        return this.trxnType;
    }

    public void setTrxnType(String trxnType) {
        this.trxnType = trxnType;
    }

    /**
     * Maps the type string to a value that's used to choose the correct choice
     * in the select box. We do this to ensure that the parameters are
     * independent of locale.
     */
    @SuppressWarnings("PMD.OnlyOneReturn")
    public int getPaymentTypeValue() {
        if (CASH.equals(paymentType)) {
            return 1;
        }
        if (CHEQUE.equals(paymentType)) {
            return 3;
        }

        return 0;
    }

    @SuppressWarnings("PMD.OnlyOneReturn")
    public int getTrxnTypeValue() {
        if (DEPOSIT.equals(trxnType)) {
            return 6;
        }
        if (WITHDRAWAL.equals(trxnType)) {
            return 7;
        }

        return 0;
    }

}
