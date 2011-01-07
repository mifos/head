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
package org.mifos.application.servicefacade;

import java.math.BigDecimal;

/**
 *
 */
public class CollectionSheetCustomerSavingDto {

    private Integer customerId;
    private Integer accountId;
    private Short productId;
    private String productShortName;
    private Short recommendedAmountUnitId;
    private Short currencyId;
    private BigDecimal depositDue = BigDecimal.ZERO;
    private BigDecimal depositPaid = BigDecimal.ZERO;

    public CollectionSheetCustomerSavingDto() {
        // default constructor for hibernate
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

    public Short getProductId() {
        return this.productId;
    }

    public void setProductId(final Short productId) {
        this.productId = productId;
    }

    public String getProductShortName() {
        return this.productShortName;
    }

    public void setProductShortName(final String productShortName) {
        this.productShortName = productShortName;
    }

    public Short getCurrencyId() {
        return this.currencyId;
    }

    public void setCurrencyId(final Short currencyId) {
        this.currencyId = currencyId;
    }

    public Short getRecommendedAmountUnitId() {
        return this.recommendedAmountUnitId;
    }

    public void setRecommendedAmountUnitId(final Short recommendedAmountUnitId) {
        this.recommendedAmountUnitId = recommendedAmountUnitId;
    }

    public void setDepositDue(final BigDecimal depositDue) {
        if (depositDue != null) {
            this.depositDue = depositDue;
        }
    }

    public void setDepositPaid(final BigDecimal depositPaid) {
        if (depositPaid != null) {
            this.depositPaid = depositPaid;
        }
    }

    public Double getTotalDepositAmount() {
        return depositDue.subtract(depositPaid).doubleValue();
    }

    public BigDecimal getDepositDue() {
        return this.depositDue;
    }

    public BigDecimal getDepositPaid() {
        return this.depositPaid;
    }
}
