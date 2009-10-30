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

package org.mifos.application.master.util.helpers;

import org.mifos.config.LocalizedTextLookup;

/**
 * 
 * PaymentTypes represents the three built in PaymentTypeEntity
 * values (CASH, VOUCHER, CHEQUE).  Note that unlike other 
 * enums that represent a fixed set of values that can only
 * change with a software update, in this case PyamentTypeEntities
 * can be dynamically added and renamed by the end user.  As 
 * a result, it should never be assumed that the three values
 * in this enum are the only payment type values in the system.
 *
 */
public enum PaymentTypes implements LocalizedTextLookup {
    CASH((short) 1), VOUCHER((short) 2), CHEQUE((short) 3);

    Short value;

    PaymentTypes(Short value) {
        this.value = value;
    }

    public Short getValue() {
        return value;
    }

    public static PaymentTypes getPaymentType(int value) {
        for (PaymentTypes paymentType : PaymentTypes.values()) {
            if (paymentType.value == value) {
                return paymentType;
            }
        }
        throw new RuntimeException("can't find payment type " + value);
    }

    public PaymentTypes next() {
        if (this == CHEQUE) {
            return CASH;
        }
        return getPaymentType(value + 1);
    }

    public String getPropertiesKey() {
        return "PaymentTypes." + toString();
    }

}
