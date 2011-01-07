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

package org.mifos.framework.util.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.AccountingRules;

/**
 * Utilities for working with monetary values that handle null.
 */
public class MoneyUtils {

    /**
     * Hide constructor of a static util
     */
    private MoneyUtils() {
    }

    public static Double getMoneyDoubleValue(Money money) {
        return money == null ? null : money.getAmountDoubleValue();
    }

    /**
     * WARNING: This method is using rounding the amount using
     * <code> digitAfterDecimal </code>. Why do we need to roundoff the amount
     * using digitAfterDecimal
     *
     * @deprecated use {@link Money#getAmount()}
     *
     * <br/>
     * <br/>
     *             FIXME this probably is a bug so remove it after replacing its
     *             usages
     */
    @Deprecated
    public static BigDecimal getMoneyAmount(Money money, Short digitsAfterDecimal) {
        return money.getAmount().setScale(digitsAfterDecimal, RoundingMode.HALF_UP);
    }

    public static Money createMoney(MifosCurrency currency, double amount) {
        return createMoney(currency, BigDecimal.valueOf(amount));
    }

    public static Money createMoney(MifosCurrency currency, BigDecimal amount) {
        return new Money(currency, amount);
    }

    /**
     * @deprecated use {@link MoneyUtils#zero(MifosCurrency)}
     * @return zero Money
     */
    @Deprecated
    public static Money zero() {
        return zero(Money.getDefaultCurrency());
    }

    public static Money zero(MifosCurrency currency) {
        return new Money(currency, BigDecimal.ZERO);
    }

    /**
     * Does the rounding based on <br />
     * {@link Money#round(Money, BigDecimal, RoundingMode)} with <br />
     * {@link AccountingRules#getInitialRoundOffMultiple()} (Default 1) <br />
     * {@link AccountingRules#getInitialRoundingMode()} (Default
     * {@link RoundingMode#HALF_UP}
     *
     * @param money
     * @return {@link Money}
     */
    public static Money initialRound(Money money) {
        BigDecimal initialRoundOffMutiple = AccountingRules.getInitialRoundOffMultiple(money.getCurrency());
        RoundingMode initialRoundingMode = AccountingRules.getInitialRoundingMode();
        return Money.round(money, initialRoundOffMutiple, initialRoundingMode);
    }

    /**
     * Does the rounding based on <br />
     * {@link Money#round(Money, BigDecimal, RoundingMode)} with <br />
     * {@link AccountingRules#getFinalRoundOffMultiple()} (Default 1) <br />
     * {@link AccountingRules#getFinalRoundingMode()} (Default
     * {@link RoundingMode#CEILING}
     *
     * @param money
     * @return {@link Money}
     */
    public static Money finalRound(Money money) {
        BigDecimal finalRoundOffMutiple = AccountingRules.getFinalRoundOffMultiple(money.getCurrency());
        RoundingMode finalRoundingMode = AccountingRules.getFinalRoundingMode();
        return Money.round(money, finalRoundOffMutiple, finalRoundingMode);
    }

    public static boolean isRoundedAmount(Money money) {
        return money.equals(initialRound(money)) && money.equals(finalRound(money));
    }

    public static boolean isRoundedAmount(final Double amount) {
        return isRoundedAmount(new Money(Money.getDefaultCurrency(), new BigDecimal(amount)));
    }

    /**
     * Does the rounding based on <br />
     * {@link Money#round(Money, BigDecimal, RoundingMode)} with <br />
     * {@link AccountingRules#getCurrencyRoundingMode()} (Default
     * {@link RoundingMode#HALF_UP}
     *
     * @param money
     * @return {@link Money}
     */
    public static Money currencyRound(Money money) {
        BigDecimal digitAfterDecimaMultiple = AccountingRules.getDigitsAfterDecimalMultiple(money.getCurrency());
        RoundingMode currencyRoundingMode = AccountingRules.getCurrencyRoundingMode();
        return Money.round(money, digitAfterDecimaMultiple, currencyRoundingMode);
    }

    public static boolean isCurrenciesDifferent(Money m1, Money m2) {
        return !m1.getCurrency().equals(m2.getCurrency());
    }
}
