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

package org.mifos.framework.util.helpers;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.AccountingRules;
import org.mifos.core.CurrencyMismatchException;

/**
 * This class represents Money objects in the system, it should be used for all
 * financial operations like addition,subtraction etc of money. As of now it
 * deals with only one currency but later it can be extended to handle currency
 * conversions while performing operations. This is an immutable class as the
 * money object is not supposed to be modified .
 *
 */
public final class Money implements Serializable, Comparable<Money> {

    /**
     * The precision used for internal calculations
     *  7 (before decimal) + 6(after decimal) = 13.
     *  Assuming that we are bounding the calculations to 7 digits
     *  before decimal, we get 6 digits after the decimal which is enough
     *  for the precision.
     *  <br><br>
     *  Why we bound the before decimal digits to 7 ?
     *  <br>see latest_schema.sql for amount (DECIMAL(10,3))
     *
     */
    private static int internalPrecision = 13;
    /**
     * The rounding mode used for internal calculations.
     */
    private static RoundingMode internalRoundingMode = RoundingMode.HALF_UP;

    private static MifosCurrency defaultCurrency = null;

    private final MifosCurrency currency;

    private final BigDecimal amount;

    /**
     * This creates a Money object with currency set to MFICurrency and amount
     * set to zero.
     */
    public Money(MifosCurrency currency) {
        this(currency, new BigDecimal(0));
    }

    public Money(MifosCurrency currency, String amount) {
        this(currency, new BigDecimal(amount));
    }

    public Money(MifosCurrency currency, BigDecimal amount) {
        checkCurrencyNotNull(currency);
        checkAmountNotNull(amount);
        this.currency = currency;
        this.amount = amount.setScale(internalPrecision, internalRoundingMode);
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public double getAmountDoubleValue() {
        return amount.doubleValue();
    }

    public MifosCurrency getCurrency() {
        return currency;
    }

    public static MifosCurrency getDefaultCurrency() {
        return defaultCurrency;
    }

    public static void setDefaultCurrency(MifosCurrency defaultCurrency) {
        Money.defaultCurrency = defaultCurrency;
    }

    /**
     * If the object passed as parameter is null or if its currency or amount is
     * null it returns this else performs the required operation and returns a
     * new Money object corresponding to the value.
     */
    public Money add(Money money) {
        if (money == null) {
            return this;
        }
        checkCurrenciesDifferent(this, money);
        return new Money(currency, amount.add(money.getAmount()));
    }

    /**
     * If the object passed as parameter is null or if its currency or amount is
     * null it returns this else performs the required operation and returns a
     * new Money object corresponding to the value.
     */
    public Money subtract(Money money) {
        if (money == null) {
            return this;
        }
        checkCurrenciesDifferent(this, money);
        return new Money(currency, amount.subtract(money.getAmount()));
    }

    public Money multiply(Double factor) {
        return multiply(new BigDecimal(factor));
    }

    public Money multiply(BigDecimal factor) {
        return new Money(currency, amount.multiply(factor).setScale(internalPrecision, internalRoundingMode));
    }

    public Money multiply(int intValue) {
        return multiply(new BigDecimal(intValue));
    }

    /**
     * Dividing by Money gives a fractional value <br>
     * <br>
     * e.g. Money(USD)/Money(USD) = fraction (no unit) <br>
     * <br>
     */
    public BigDecimal divide(Money money) {
        checkCurrenciesDifferent(this, money);
        return amount.divide(money.getAmount(), internalPrecision, internalRoundingMode);
    }

    public Money divide(BigDecimal factor) {
        return new Money(currency, amount.divide(factor.setScale(internalPrecision, internalRoundingMode),
                internalPrecision, internalRoundingMode));
    }

    public Money divide(Double value) {
        return divide(new BigDecimal(value));
    }

    public Money divide(Short shortVal) {
        return divide(new BigDecimal(shortVal));
    }

    public Money divide(Integer intVal) {
        return divide(new BigDecimal(intVal));
    }

    public Money negate() {
        // no need to set scale since negation preserves scale
        return new Money(currency, amount.negate());
    }

    /**
     * This method returns a new Money object with currency same as current
     * currency and amount calculated after rounding based on rounding mode and
     * roundingAmount where in both are obtained from MifosCurrency object. <br />
     * <br />
     * The rounding calculation is as follows:- Lets say we want to round 142.34
     * to nearest 50 cents and and rounding mode is ceil (i.e. to greater
     * number) we will divide 142.34 by .5 which will result in 284.68 now we
     * will round this to a whole number using ceil mode which will result in
     * 285 and then multiply 285 by 0.5 resulting in 142.5.
     *
     */
    public static Money round(Money money, BigDecimal roundOffMultiple, RoundingMode roundingMode) {
        // insure that we are using the correct internal precision
        BigDecimal roundingAmount = roundOffMultiple.setScale(internalPrecision, internalRoundingMode);
        // FIXME: are we loosing precision here
        // mathcontext only take cares of significant digits
        // not digit right to the decimal
        BigDecimal nearestFactor = money.getAmount().divide(roundingAmount,
                new MathContext(internalPrecision, internalRoundingMode));

        nearestFactor = nearestFactor.setScale(0, roundingMode);

        BigDecimal roundedAmount = nearestFactor.multiply(roundingAmount);
        return new Money(money.getCurrency(), roundedAmount);
    }

    /**
     * This method return true if the currency associated with the two money
     * objects is equal and also the compareTo method of BigDecimal return 0 for
     * the amount of the two money objects. It is not advisable to use equals
     * method of BigDecimal because it would return false for numbers like 10.0
     * and 10.00 instead we should use compareTo.
     *
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Money)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        Money money = (Money) obj;
        return this.currency.equals(money.getCurrency()) && (this.getAmount().compareTo(money.getAmount()) == 0);
    }

    @Override
    public int hashCode() {
        return this.currency.getCurrencyId() * 100 + this.getAmount().intValue();
    }

    @Override
    public String toString() {
        // FIXME string formating based on Accounting rule should be done in
        // MoneyUtil class
        // only string representation of BigDecimal should be returned here
        double doubleValue = amount.doubleValue();
        String format = "%." + AccountingRules.getDigitsAfterDecimal(getCurrency()).toString() + "f";
        String formatStr = String.format(Locale.ENGLISH, format, 0.0);
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
        DecimalFormat decimalFormat = null;
        if (numberFormat instanceof DecimalFormat) {
            decimalFormat = ((DecimalFormat) numberFormat);
            decimalFormat.applyPattern(formatStr);
            return decimalFormat.format(doubleValue);
        }
        return numberFormat.format(doubleValue);
    }

    public boolean isGreaterThan(Money money) {
        return this.compareTo(money) > 0;
    }

    public boolean isGreaterThanOrEqual(Money money) {
        return this.compareTo(money) >= 0;
    }

    public boolean isGreaterThanZero() {
        return this.getAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isGreaterThanOrEqualZero() {
        return this.getAmount().compareTo(BigDecimal.ZERO) >= 0;
    }

    public boolean isLessThan(Money money) {
        return this.compareTo(money) < 0;
    }

    public boolean isLessThanOrEqual(Money money) {
        return this.compareTo(money) <= 0;
    }

    public boolean isLessThanZero() {
        return this.getAmount().compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isLessThanOrEqualZero() {
        return this.getAmount().compareTo(BigDecimal.ZERO) <= 0;
    }

    public boolean isZero() {
        return this.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isNonZero() {
        return this.getAmount().compareTo(BigDecimal.ZERO) != 0;
    }

    private void checkCurrencyNotNull(MifosCurrency currency) {
        if (currency == null) {
            throw new NullPointerException(ExceptionConstants.CURRENCY_MUST_NOT_BE_NULL);
        }
    }

    private void checkAmountNotNull(BigDecimal amount) {
        if (amount == null) {
            throw new NullPointerException(ExceptionConstants.AMMOUNT_MUST_NOT_BE_NULL);
        }
    }

    private static void checkCurrenciesDifferent(Money m1, Money m2) {
        if (!m1.getCurrency().equals(m2.getCurrency())) {
            throw new CurrencyMismatchException(ExceptionConstants.ILLEGALMONEYOPERATION);
        }
    }

    @Override
    public int compareTo(Money money) {
        checkCurrenciesDifferent(this, money);
        return this.getAmount().compareTo(money.amount);
    }
}
