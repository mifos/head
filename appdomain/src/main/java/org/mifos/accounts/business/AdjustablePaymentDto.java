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

package org.mifos.accounts.business;

import java.io.Serializable;
import java.util.Date;

import org.mifos.framework.util.helpers.Money;

public class AdjustablePaymentDto implements Serializable {

    private static final long serialVersionUID = 7265723376225551977L;

    private Integer paymentId;

    private Money amount;
    private String paymentType;
    private Date paymentDate;
    private Date receiptDate;
    private String receiptId;
    //globalAccNum used apply adjustemnt for 
    //new group loan account
    private String globalAccountNum;

    public AdjustablePaymentDto(final Integer paymentId, final Money amount, final String paymentType,
            final Date paymentDate, final Date receiptDate, final String receiptId) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentType = paymentType;
        this.paymentDate = paymentDate;
        this.receiptDate = receiptDate;
        this.receiptId = receiptId;
        this.globalAccountNum = null;
    }
    
    public AdjustablePaymentDto(final Integer paymentId, final Money amount, final String paymentType,
            final Date paymentDate, final Date receiptDate, final String receiptId, String globalAccountNum) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentType = paymentType;
        this.paymentDate = paymentDate;
        this.receiptDate = receiptDate;
        this.receiptId = receiptId;
        this.globalAccountNum = globalAccountNum;
    }

    public Money getAmount() {
        return amount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getGlobalAccountNum() {
        return globalAccountNum;
    }

    public void setGlobalAccountNum(String globalAccountNum) {
        this.globalAccountNum = globalAccountNum;
    }
}
