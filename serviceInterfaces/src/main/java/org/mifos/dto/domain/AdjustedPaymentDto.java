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
package org.mifos.dto.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class AdjustedPaymentDto implements Serializable {

    private static final long serialVersionUID = 6842718215097913178L;

    private Date paymentDate;
    private String amount;
    private Short paymentType;
    
    private Integer accountId;
    private List<AdjustedPaymentDto> memberPayments; 
    
    public AdjustedPaymentDto(final String amount, final Date paymentDate, final Short paymentType) {
        this.paymentDate = (Date)paymentDate.clone();
        this.amount = amount;
        this.paymentType = paymentType;
    }

    public AdjustedPaymentDto(String amount, Date paymentDate,  Short paymentType, Integer accountId) {
        this.paymentDate = (Date)paymentDate.clone();
        this.amount = amount;
        this.paymentType = paymentType;
        this.accountId = accountId;
    }
    
    public AdjustedPaymentDto(String amount, Date paymentDate, Short paymentType, Integer accountId,
            List<AdjustedPaymentDto> memberPayments) {
        this.paymentDate = (Date)paymentDate.clone();
        this.amount = amount;
        this.paymentType = paymentType;
        this.accountId = accountId;
        this.memberPayments = memberPayments;
    }
    public Date getPaymentDate() {
        return (Date)paymentDate.clone();
    }
    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = (Date)paymentDate.clone();
    }
    public String getAmount() {
        return amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }
    public Short getPaymentType() {
        return paymentType;
    }
    public void setPaymentType(Short paymentType) {
        this.paymentType = paymentType;
    }
    
    public Integer getAccountId() {
        return accountId;
    }
    
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
    
    public List<AdjustedPaymentDto> getMemberPayments() {
        return memberPayments;
    }
    
    public void setMemberPayments(List<AdjustedPaymentDto> memberPayments) {
        this.memberPayments = memberPayments;
    }
    
}
