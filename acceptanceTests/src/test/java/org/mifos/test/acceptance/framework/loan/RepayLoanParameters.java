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

public class RepayLoanParameters {
    public static final String CASH = "Cash";
    public static final String VOUCHER = "Voucher";
    public static final String CHEQUE = "Cheque";
    public static final String TRANSFER_FROM_SAVINGS = "Transfer from Savings";

    private String modeOfRepayment;
    private String receiptId;
    private String receiptDateDD;
    private String receiptDateMM;
    private String receiptDateYYYY;
    private String accountForTransfer;

    public String getModeOfRepayment() {
        return this.modeOfRepayment;
    }
    public void setModeOfRepayment(String modeOfRepayment) {
        this.modeOfRepayment = modeOfRepayment;
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
    public String getReceiptDateMM() {
        return this.receiptDateMM;
    }
    public void setReceiptDateMM(String receiptDateMM) {
        this.receiptDateMM = receiptDateMM;
    }
    public String getReceiptDateYYYY() {
        return this.receiptDateYYYY;
    }
    public void setReceiptDateYYYY(String receiptDateYYYY) {
        this.receiptDateYYYY = receiptDateYYYY;
    }
    
    public String getAccountForTransfer() {
        return this.accountForTransfer;
    }
    public void setAccountForTransfer(String accountForTransfer) {
        this.accountForTransfer = accountForTransfer;
    }

    /**
     * Used to select the right option.
     */
    @SuppressWarnings("PMD.OnlyOneReturn")
    public int getModeOfRepaymentValue() {
        if (CASH.equals(modeOfRepayment)) { return 1; }
        if (VOUCHER.equals(modeOfRepayment)) { return 2; }
        if (CHEQUE.equals(modeOfRepayment)) { return 3; }
        if (TRANSFER_FROM_SAVINGS.equals(modeOfRepayment)) { return 4; }
        return 0;
    }
}
