/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.customers.business;

import java.util.Date;

import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.util.helpers.Money;

/**
 * This class encapsulate the customer historical data
 */
public class CustomerHistoricalDataEntity extends AbstractEntity {

    private final Short historicalId;

    private String productName;

    private Money loanAmount;

    private Money totalAmountPaid;

    private Money interestPaid;

    private Integer missedPaymentsCount;

    private Integer totalPaymentsCount;

    private String notes;

    private Integer loanCycleNumber;

    private final CustomerBO customer;

    private Date mfiJoiningDate;

    private Date createdDate;
    
    private Short createdBy;
    
    private Date updatedDate;
    
    private Short updatedBy;
    
    private Integer versionNo;

    public CustomerHistoricalDataEntity(CustomerBO customer) {
        this.customer = customer;
        this.historicalId = null;
    }

    /*
     * Adding a default constructor is hibernate's requirement and should not be
     * used to create a valid Object.
     */
    protected CustomerHistoricalDataEntity() {
        this.historicalId = null;
        this.customer = null;
    }

    public void setMfiJoiningDate(Date mfiJoiningDate) {
        this.mfiJoiningDate = mfiJoiningDate;
    }

    public Date getMfiJoiningDate() {
        return mfiJoiningDate;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }

    public void setLoanCycleNumber(Integer loanCycleNumber) {
        this.loanCycleNumber = loanCycleNumber;
    }

    public Integer getLoanCycleNumber() {
        return loanCycleNumber;
    }

    public void setTotalAmountPaid(Money totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }

    public Money getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setInterestPaid(Money interestPaid) {
        this.interestPaid = interestPaid;
    }

    public Money getInterestPaid() {
        return interestPaid;
    }

    public void setMissedPaymentsCount(Integer missedPaymentsCount) {
        this.missedPaymentsCount = missedPaymentsCount;
    }

    public Integer getMissedPaymentsCount() {
        return missedPaymentsCount;
    }

    public void setTotalPaymentsCount(Integer totalPaymentsCount) {
        this.totalPaymentsCount = totalPaymentsCount;
    }

    public Integer getTotalPaymentsCount() {
        return totalPaymentsCount;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public void setLoanAmount(Money loanAmount) {
        this.loanAmount = loanAmount;
    }

    public Money getLoanAmount() {
        return loanAmount;
    }
    
    
    public Short getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Short createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Short getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Short updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

}
