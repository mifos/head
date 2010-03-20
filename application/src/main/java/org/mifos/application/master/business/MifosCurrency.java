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

package org.mifos.application.master.business;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * This class denotes the currency object. It contains information such as the
 * currency name , the display symbol, Whether this currency has been chosen as
 * the default currency for the MFI. This class is immutable and hence all the
 * setter methods are private. The class is final and the mapping for this class
 * specifies lazy=false so that hibernate doesn't initialize a proxy
 */
@NamedQueries(
 {
  @NamedQuery(
    name="getCurrency",
    query="from MifosCurrency currency where currency.currencyCode = :currencyCode"
  )
 }
)
@Entity
@Table(name = "CURRENCY")
public final class MifosCurrency implements Serializable {

    public static final short CEILING_MODE = 1;
    public static final short FLOOR_MODE = 2;
    public static final short HALF_UP_MODE = 3;

    private Short currencyId;

    /** English multiple-word descriptive name. */
    private String currencyName;

    private BigDecimal roundingAmount;

    /** ISO 4217 currency code. */
    private String currencyCode;

    public MifosCurrency(Short currencyId, String currencyName, BigDecimal roundingAmount, String currencyCode) {
        this.currencyId = currencyId;
        this.currencyName = currencyName;
        this.roundingAmount = roundingAmount;
        this.currencyCode = currencyCode;
    }

    protected MifosCurrency() {
    }

    @Id
    @GeneratedValue
    @Column(name = "CURRENCY_ID", nullable = false)
    public Short getCurrencyId() {
        return currencyId;
    }

    @SuppressWarnings("unused")
    private void setCurrencyId(Short currencyId) {
        this.currencyId = currencyId;
    }

    @Column(name = "CURRENCY_NAME")
    public String getCurrencyName() {
        return currencyName;
    }

    @SuppressWarnings("unused")
    private void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    @Column(name = "CURRENCY_CODE")
    public String getCurrencyCode() {
        return currencyCode;
    }

    @SuppressWarnings("unused")
    private void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Column(name = "ROUNDING_AMOUNT")
    public BigDecimal getRoundingAmount() {
        return roundingAmount;
    }

    @SuppressWarnings("unused")
    private void setRoundingAmount(BigDecimal roundingAmount) {
        this.roundingAmount = roundingAmount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MifosCurrency other = (MifosCurrency) obj;
        if (currencyId == null) {
            if (other.currencyId != null) {
                return false;
            }
        } else if (!currencyId.equals(other.currencyId)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return currencyId == null ? 0 : currencyId.hashCode();
    }

}
