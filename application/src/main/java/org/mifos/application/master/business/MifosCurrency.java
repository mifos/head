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

package org.mifos.application.master.business;

import java.math.RoundingMode;
import org.mifos.framework.business.PersistentObject;

/**
 * This class denotes the currency object. It contains information such as the
 * currency name , the display symbol, Whether this currency has been chosen as
 * the default currency for the MFI. This class is immutable and hence all the
 * setter methods are private. The class is final and the mapping for this class
 * specifies lazy=false so that hibernate doesnt initialize a proxy
 */
public final class MifosCurrency extends PersistentObject {

    public static final short CEILING_MODE = 1;
    public static final short FLOOR_MODE = 2;
    public static final short HALF_UP_MODE = 3;

    /** The composite primary key value. */
    private Short currencyId;

    /** The value of the simple currencyName property. */
    private String currencyName;

    /** The value of the simple displaySymbol property. */
    private String displaySymbol;

    /** The value of the simple roundUpDown property. */
    private Short roundingMode;

    /** The value of the simple roundingAmount property. */
    private Float roundingAmount;

    /** The value of the simple defaultCurrency property. */
    private Short defaultCurrency;

    private Short defaultDigitsAfterDecimal;

    private String currencyCode;

    /**
     * This constructor will be used if the currency has to be created through
     * the UI.
     */
    public MifosCurrency(Short currencyId, String currencyName, String displaySymbol, Short roundingMode,
            Float roundingAmount, Short defaultCurrency, Short defaultDigitsAfterDecimal, String currencyCode) {
        this.currencyId = currencyId;
        this.currencyName = currencyName;
        this.displaySymbol = displaySymbol;
        this.roundingMode = roundingMode;
        this.roundingAmount = roundingAmount;
        this.defaultCurrency = defaultCurrency;
        this.defaultDigitsAfterDecimal = defaultDigitsAfterDecimal;
        this.currencyCode = currencyCode;
    }

    protected MifosCurrency() {
    }

    public Short getCurrencyId() {
        return currencyId;
    }

    @SuppressWarnings("unused")
    // See .hbm.xml file
    private void setCurrencyId(Short currencyId) {
        this.currencyId = currencyId;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    @SuppressWarnings("unused")
    // See .hbm.xml file
    private void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public Short getDefaultCurrency() {
        return defaultCurrency;
    }

    @SuppressWarnings("unused")
    // See .hbm.xml file
    private void setDefaultCurrency(Short defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public boolean isDefaultCurrency() {
        return (this.defaultCurrency.shortValue() == 1) ? true : false;
    }

    public String getDisplaySymbol() {
        return displaySymbol;
    }

    @SuppressWarnings("unused")
    // See .hbm.xml file
    private void setDisplaySymbol(String displaySymbol) {
        this.displaySymbol = displaySymbol;
    }

    public RoundingMode getRoundingModeEnum() {

        if (roundingMode == CEILING_MODE) {
            return RoundingMode.CEILING;
        } else if (roundingMode == FLOOR_MODE) {
            return RoundingMode.FLOOR;
        } else if (roundingMode == HALF_UP_MODE) {
            return RoundingMode.HALF_UP;
        } else {
            throw new IllegalStateException("bad rounding mode " + roundingMode);
        }

    }

    public Short getRoundingMode() {
        return roundingMode;
    }

    void setRoundingMode(Short roundingMode) {
        this.roundingMode = roundingMode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        final MifosCurrency other = (MifosCurrency) obj;
        if (currencyId == null) {
            if (other.currencyId != null)
                return false;
        } else if (!currencyId.equals(other.currencyId))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return currencyId == null ? 0 : currencyId.hashCode();
    }

    public Float getRoundingAmount() {
        return roundingAmount;
    }

    @SuppressWarnings("unused")
    // See .hbm.xml file
    private void setRoundingAmount(Float roundingAmount) {
        this.roundingAmount = roundingAmount;
    }

    public Short getDefaultDigitsAfterDecimal() {
        return defaultDigitsAfterDecimal;
    }

    @SuppressWarnings("unused")
    // See .hbm.xml file
    private void setDefaultDigitsAfterDecimal(Short defaultDigitsAfterDecimal) {
        this.defaultDigitsAfterDecimal = defaultDigitsAfterDecimal;
    }

}
