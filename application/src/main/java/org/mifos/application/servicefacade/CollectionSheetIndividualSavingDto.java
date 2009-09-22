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
package org.mifos.application.servicefacade;

import java.math.BigDecimal;

/**
 *
 */
public class CollectionSheetIndividualSavingDto {

    private Integer parentCustomerId;
    private Integer customerId;
    private Integer accountId;
    private String productShortName;
    private Short productId;
    private BigDecimal depositDue = BigDecimal.ZERO;
    private BigDecimal depositPaid = BigDecimal.ZERO;
    
    public CollectionSheetIndividualSavingDto() {
        // default constructor for hibernate usage
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(final Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getAccountId() {
        return this.accountId;
    }

    public void setAccountId(final Integer accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getDepositDue() {
        return this.depositDue;
    }

    public void setDepositDue(final BigDecimal depositDue) {
        this.depositDue = depositDue;
    }

    public BigDecimal getDepositPaid() {
        return this.depositPaid;
    }

    public void setDepositPaid(final BigDecimal depositPaid) {
        this.depositPaid = depositPaid;
    }

    public Integer getParentCustomerId() {
        return this.parentCustomerId;
    }

    public void setParentCustomerId(final Integer parentCustomerId) {
        this.parentCustomerId = parentCustomerId;
    }

    public String getProductShortName() {
        return this.productShortName;
    }

    public void setProductShortName(final String productShortName) {
        this.productShortName = productShortName;
    }

    public Short getProductId() {
        return this.productId;
    }

    public void setProductId(final Short productId) {
        this.productId = productId;
    }
}
