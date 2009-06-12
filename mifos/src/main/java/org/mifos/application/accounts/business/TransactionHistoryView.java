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

package org.mifos.application.accounts.business;

import java.util.Date;
import java.util.Locale;

import org.mifos.framework.business.View;
import org.mifos.framework.util.helpers.DateUtils;

public class TransactionHistoryView extends View implements Comparable<TransactionHistoryView> {
    private Date transactionDate;

    private Integer paymentId;

    private Integer accountTrxnId;

    private String type;

    private String glcode;

    private String debit = "-";

    private String credit = "-";

    private String balance;

    private String clientName = "-";

    private Date postedDate;

    private String postedBy;

    private String notes = "-";

    private Locale locale = null;

    public Integer getAccountTrxnId() {
        return accountTrxnId;
    }

    public void setAccountTrxnId(Integer accountTrxnId) {
        this.accountTrxnId = accountTrxnId;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getDebit() {
        return debit;
    }

    public void setDebit(String debit) {
        this.debit = debit;
    }

    public String getGlcode() {
        return glcode;
    }

    public void setGlcode(String glcode) {
        this.glcode = glcode;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserPrefferedPostedDate() {
        return DateUtils.getUserLocaleDate(getLocale(), getPostedDate().toString());
    }

    public String getUserPrefferedTransactionDate() {
        return DateUtils.getUserLocaleDate(getLocale(), getTransactionDate().toString());
    }

    public void setFinancialEnteries(Integer trxnId, Date trxnDate, String actionType, String glCode, String debit,
            String credit, Date postedDate, String notes) {
        this.accountTrxnId = trxnId;
        this.transactionDate = trxnDate;
        this.type = actionType;
        this.glcode = glCode;
        this.debit = debit;
        this.credit = credit;
        this.postedDate = postedDate;
        this.notes = notes;
    }

    public void setAccountingEnteries(Integer paymentId, String balance, String clientName, String postedBy) {
        this.paymentId = paymentId;
        this.balance = balance;
        this.clientName = clientName;
        this.postedBy = postedBy;
    }

    /**
     * This is only written for test purposes. The idea is to guarantee the
     * order in which these objects can be organized in a list - first compare
     * by postedDates, if dates are equal - 'Credit' should come before 'Debit'.
     * There is no philosophical reasoning behind this ordering, just something
     * we can count on --> this should be changed to suite a production need if
     * one exists. The tests that rely on this ordering include:
     * {@link SavingsActionTest#testSuccessfullGetTransactionHistory()}
     */
    public int compareTo(TransactionHistoryView o) {
        int dateCompare = this.getPostedDate().compareTo(o.getPostedDate());
        if (dateCompare != 0)
            return dateCompare;
        else if (!this.getDebit().equals("-"))
            return !o.getDebit().equals("-") ? this.getDebit().compareTo(o.getDebit()) : 1;
        else if (!this.getCredit().equals("-"))
            return !o.getCredit().equals("-") ? this.getCredit().compareTo(o.getCredit()) : -1;
        else
            return 0;
    }
}
