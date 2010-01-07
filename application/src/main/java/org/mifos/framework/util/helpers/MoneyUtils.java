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

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.mifos.application.master.business.MifosCurrency;

/**
 * Utilities for working with monetary values that handle null.
 */
public class MoneyUtils {
    
    public static final Money ZERO = new Money(Money.getDefaultCurrency());

    /**
     * Validates that the object specified is not null
     *
     * @param object  the object to check, not null
     * @throws NullPointerException if the input value is null
     */
    static void checkNotNull(Object object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Private constructor.
     */
    private MoneyUtils() {
    }
    
    
    public static Money add(Money firstAmount, Money secondAmount) {
        Money sum = firstAmount == null ? secondAmount : firstAmount.add(secondAmount);
        return sum;
    }

    public static Double getMoneyDoubleValue(Money money) {
        return money == null ? null : money.getAmountDoubleValue();
    }

    
    /**
     * WARNING: This method is using rounding the amount using <code> digitAfterDecimal </code>.
     * It is not according to the {@link Money#round(Money)} implementation
     * 
     * @deprecated use {@link Money#getAmount()}
     * 
     * <br/><br/> FIXME this probably is a bug so remove it after replacing its useage
     */
    @Deprecated
    public static BigDecimal getMoneyAmount(Money money, Short digitsAfterDecimal) {
        BigDecimal amount = money.getAmount();
        MifosCurrency currency = money.getCurrency();
        if (amount == null)
            return null;
        if (currency == null)
            return amount;
        return amount.setScale(digitsAfterDecimal, RoundingMode.HALF_UP);
    }
    
    public static Money createMoney(double amount) {
        return new Money(BigDecimal.valueOf(amount).toString());
    }
    
    public static Money createMoney(MifosCurrency currency, BigDecimal amount) {
        return new Money(currency, amount);
    }

    public static Money zero(MifosCurrency currency) {
        return new Money(currency, BigDecimal.ZERO);
    }
}
