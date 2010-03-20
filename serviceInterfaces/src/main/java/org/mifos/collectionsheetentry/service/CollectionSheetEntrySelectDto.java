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

package org.mifos.collectionsheetentry.service;


public class CollectionSheetEntrySelectDto {

    private Integer branchOfficeId;
    private Integer loanOfficerId;
    private Integer centerId;
    private int transactionDay;
    private int transactionMonth;
    private int transactionYear;
    private String paymentMode;
    private String receiptId;
    private int receiptDay;
    private int receiptMonth;
    private int receiptYear;

    public Integer getBranchOfficeId() {
        return branchOfficeId;
    }
    public void setBranchOfficeId(Integer branchOfficeId) {
        this.branchOfficeId = branchOfficeId;
    }
    public Integer getLoanOfficerId() {
        return loanOfficerId;
    }
    public void setLoanOfficerId(Integer loanOfficerId) {
        this.loanOfficerId = loanOfficerId;
    }
    public Integer getCenterId() {
        return centerId;
    }
    public void setCenterId(Integer centerId) {
        this.centerId = centerId;
    }
    public int getTransactionDay() {
        return transactionDay;
    }
    public void setTransactionDay(int transactionDay) {
        this.transactionDay = transactionDay;
    }
    public int getTransactionMonth() {
        return transactionMonth;
    }
    public void setTransactionMonth(int transactionMonth) {
        this.transactionMonth = transactionMonth;
    }
    public int getTransactionYear() {
        return transactionYear;
    }
    public void setTransactionYear(int transactionYear) {
        this.transactionYear = transactionYear;
    }
    public String getPaymentMode() {
        return paymentMode;
    }
    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
    public String getReceiptId() {
        return receiptId;
    }
    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }
    public int getReceiptDay() {
        return receiptDay;
    }
    public void setReceiptDay(int receiptDay) {
        this.receiptDay = receiptDay;
    }
    public int getReceiptMonth() {
        return receiptMonth;
    }
    public void setReceiptMonth(int receiptMonth) {
        this.receiptMonth = receiptMonth;
    }
    public int getReceiptYear() {
        return receiptYear;
    }
    public void setReceiptYear(int receiptYear) {
        this.receiptYear = receiptYear;
    }

}
