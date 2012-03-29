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

package org.mifos.dto.screen;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2", "EQ_COMPARETO_USE_OBJECT_EQUALS"}, justification="should disable at filter level and also for pmd - not important for us")
public class TransactionHistoryDto implements Serializable, Comparable<TransactionHistoryDto> {

    private Date transactionDate;
    private Integer paymentId;
    private Integer accountTrxnId;
    private String type;
    private String glcode;
    private String glname;
    private String debit = "-";
    private String credit = "-";
    private String balance;
    private String clientName = "-";
    private Date postedDate;
    private String postedBy;
    private String notes = "-";
    private Locale locale = null;
    private String userPrefferedTransactionDate;
    private String userPrefferedPostedDate;

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

    public String getGlname() {
		return glname;
	}

	public void setGlname(String glname) {
		this.glname = glname;
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
        return userPrefferedPostedDate;
    }

    public String getUserPrefferedTransactionDate() {
        return userPrefferedTransactionDate;
    }

    public void setFinancialEnteries(Integer trxnId, Date trxnDate, String actionType, String glCode, String glName, String debit,
            String credit, Date postedDate, String notes) {
        this.accountTrxnId = trxnId;
        this.transactionDate = trxnDate;
        this.type = actionType;
        this.glcode = glCode;
        this.glname = glName;
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

    /*
     * This is only written for test purposes. The idea is to guarantee the
     * order in which these objects can be organized in a list - first compare
     * by postedDates, if dates are equal - 'Credit' should come before 'Debit'.
     * There is no philosophical reasoning behind this ordering, just something
     * we can count on --> this should be changed to suite a production need if
     * one exists. The tests that rely on this ordering include:
     * <b>SavingsActionStrutsTest#testSuccessfullGetTransactionHistory()</b>
     */
    @Override
	public int compareTo(TransactionHistoryDto o) {
        int dateCompare = this.getPostedDate().compareTo(o.getPostedDate());
        if (dateCompare != 0) {
            return dateCompare;
        } else if (!this.getDebit().equals("-")) {
            return !o.getDebit().equals("-") ? this.getDebit().compareTo(o.getDebit()) : 1;
        } else if (!this.getCredit().equals("-")) {
            return !o.getCredit().equals("-") ? this.getCredit().compareTo(o.getCredit()) : -1;
        } else {
            return 0;
        }
    }

    public void setUserPrefferedPostedDate(String userPrefferedPostedDate) {
        this.userPrefferedPostedDate = userPrefferedPostedDate;
    }

    public void setUserPrefferedTransactionDate(String userPrefferedTransactionDate) {
        this.userPrefferedTransactionDate = userPrefferedTransactionDate;
    }
}
