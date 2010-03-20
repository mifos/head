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

package org.mifos.test.acceptance.framework.loan;

public class PaymentParameters {
    public static final String CASH = "Cash";
    public static final String CHEQUE = "Cheque";

    private String transactionDateDD;
    private String transactionDateMM;
    private String transactionDateYYYY;

    private String amount;
    private String paymentType;

    private String receiptId;

    private String receiptDateDD;
    private String receiptDateMM;
    private String receiptDateYYYY;

    public String getTransactionDateDD() {
        return this.transactionDateDD;
    }
    public void setTransactionDateDD(String transactionDateDD) {
        this.transactionDateDD = transactionDateDD;
    }
    public String getTransactionDateMM() {
        return this.transactionDateMM;
    }
    public void setTransactionDateMM(String transactionDateMM) {
        this.transactionDateMM = transactionDateMM;
    }
    public String getTransactionDateYYYY() {
        return this.transactionDateYYYY;
    }
    public void setTransactionDateYYYY(String transactionDateYYYY) {
        this.transactionDateYYYY = transactionDateYYYY;
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
    public String getReceiptDateMM() {
        return this.receiptDateMM;
    }
    public void setReceiptDateMM(String receiptDateMM) {
        this.receiptDateMM = receiptDateMM;
    }
    public String getReceiptId() {
        return this.receiptId;
    }
    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }
    public String getReceiptDateDD() {
        return this.receiptDateDD;
    }
    public void setReceiptDateDD(String receiptDateDD) {
        this.receiptDateDD = receiptDateDD;
    }
    public String getReceiptDateYYYY() {
        return this.receiptDateYYYY;
    }
    public void setReceiptDateYYYY(String receiptDateYYYY) {
        this.receiptDateYYYY = receiptDateYYYY;
    }

    /**
     * Maps the method of payment string to a value that's used to choose the right element in the drop-down box.
     */
    @SuppressWarnings("PMD.OnlyOneReturn")
    public int getPaymentTypeValue() {
        if (CASH.equals(paymentType)) { return 1; }
        if (CHEQUE.equals(paymentType)) { return 3; }

        return -1;
    }
}
