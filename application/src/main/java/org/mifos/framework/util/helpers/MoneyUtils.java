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

public class MoneyUtils {

    public static Money add(Money firstAmount, Money secondAmount) {
        Money sum = firstAmount == null ? secondAmount : firstAmount.add(secondAmount);
        return sum;
    }

    public static Double getMoneyDoubleValue(Money money) {
        return money == null ? null : money.getAmountDoubleValue();
    }

    public static BigDecimal getMoneyAmount(Money money, Short digitsAfterDecimal) {
        BigDecimal amount = money.getAmount();
        MifosCurrency currency = money.getCurrency();
        if (amount == null)
            return null;
        if (currency == null)
            return amount;
        return amount.setScale(digitsAfterDecimal, RoundingMode.HALF_UP);
    }
}
