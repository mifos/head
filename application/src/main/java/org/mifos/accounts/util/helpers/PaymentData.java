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

package org.mifos.accounts.util.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.util.helpers.Money;

/*
 * Used to hold information entered about a payment made.
 * Currently this is populated from data entered on a web page.
 */
public class PaymentData {

    private Money totalAmount;

    private CustomerBO customer;

    private PersonnelBO personnel;

    private Date transactionDate;

    private String receiptNum;

    private Date receiptDate;

    private Short paymentTypeId;

    private String comment;

    /*
     * Holds information including the installment this payment is to be applied
     * towards and any installments in arrears.
     */
    private List<AccountPaymentData> accountPayments;

    private PaymentData(Money totalAmount, PersonnelBO personnel, Short paymentId, Date transactionDate) {
        accountPayments = new ArrayList<AccountPaymentData>();
        setTotalAmount(totalAmount);
        setPersonnel(personnel);
        setPaymentTypeId(paymentId);
        setTransactionDate(transactionDate);
    }

    private PaymentData(Money totalAmount, PersonnelBO personnel, Short paymentId, Date transactionDate,
            String receiptNum, Date receiptDate) {
        accountPayments = new ArrayList<AccountPaymentData>();
        setTotalAmount(totalAmount);
        setPersonnel(personnel);
        setPaymentTypeId(paymentId);
        setTransactionDate(transactionDate);
        setReceiptNum(receiptNum);
        setReceiptDate(receiptDate);
    }

    public static PaymentData createPaymentData(Money totalAmount, PersonnelBO personnel, Short paymentId,
            Date transactionDate) {
        return new PaymentData(totalAmount, personnel, paymentId, transactionDate);
    }

    public static PaymentData createReceiptPaymentData(ReceiptPaymentDataTemplate template) throws InvalidDateException {
        return new PaymentData(template.getTotalAmount(), template.getPersonnel(), template.getPaymentTypeId(),
                template.getTransactionDate(), template.getPaymentReceiptNumber(), template.getPaymentReceiptDate());
    }

    public static PaymentData createPaymentData(PaymentDataTemplate template) throws InvalidDateException {
        return new PaymentData(template.getTotalAmount(), template.getPersonnel(), template.getPaymentTypeId(),
                template.getTransactionDate());
    }

    public List<AccountPaymentData> getAccountPayments() {
        return accountPayments;
    }

    public Short getPaymentTypeId() {
        return paymentTypeId;
    }

    public PersonnelBO getPersonnel() {
        return personnel;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public String getReceiptNum() {
        return receiptNum;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    private void setPersonnel(PersonnelBO personnel) {
        this.personnel = personnel;
    }

    public CustomerBO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerBO customer) {
        this.customer = customer;
    }

    private void setPaymentTypeId(Short paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public void setReceiptNum(String receiptNum) {
        this.receiptNum = receiptNum;
    }

    private void setTotalAmount(Money totalAmount) {
        this.totalAmount = totalAmount;
    }

    private void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public void addAccountPaymentData(AccountPaymentData accountPaymentData) {
        accountPayments.add(accountPaymentData);
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


}
