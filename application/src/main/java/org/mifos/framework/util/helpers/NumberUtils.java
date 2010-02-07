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

public class NumberUtils {

    public static Short convertIntegerToShort(Integer intValue) {
        if (intValue == null) {
            return null;
        }
        return intValue.shortValue();
    }
    
    public static Integer convertShortToInteger(Short shortValue) {
        if (shortValue == null) {
            return null;
        }
        return shortValue.intValue();
    }

    public static boolean isDigits(String number) {
        if (number == null)
            return false;
        if (number.startsWith("-"))
            number = number.substring(1);
        return org.apache.commons.lang.math.NumberUtils.isDigits(number);
    }

    public static BigDecimal getPercentage(Number part, Number full) {
        float fullValue = full.floatValue();
        if (fullValue == 0.0)
            return BigDecimal.ZERO;
        return BigDecimal.valueOf(part.floatValue() / fullValue * 100f);
    }

    public static boolean isBetween(Comparable start, Comparable end, Comparable value) {
        return start.compareTo(value) <= 0 && end.compareTo(value) >= 0;
    }

    public static Integer nullSafeValue(Integer value, int defaultValue) {
        if (value == null)
            return defaultValue;
        return value;
    }

    public static Integer nullSafeValue(Integer value) {
        return nullSafeValue(value, org.apache.commons.lang.math.NumberUtils.INTEGER_ZERO);
    }
}
