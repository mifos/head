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

package org.mifos.customers.util.helpers;

public enum CustomerStatusFlag {

    CLIENT_CANCEL_WITHDRAW(1), CLIENT_CANCEL_REJECTED(2), CLIENT_CANCEL_BLACKLISTED(3), CLIENT_CANCEL_DUPLICATE(4), CLIENT_CANCEL_OTHER(
            5),

    CLIENT_CLOSED_TRANSFERRED(6), CLIENT_CLOSED_DUPLICATE(7), CLIENT_CLOSED_BLACKLISTED(8), CLIENT_CLOSED_LEFTPROGRAM(9), CLIENT_CLOSED_OTHER(
            10),

    GROUP_CANCEL_WITHDRAW(11), GROUP_CANCEL_REJECTED(12), GROUP_CANCEL_BLACKLISTED(13), GROUP_CANCEL_DUPLICATE(14), GROUP_CANCEL_OTHER(
            15),

    GROUP_CLOSED_TRANSFERRED(16), GROUP_CLOSED_DUPLICATE(17), GROUP_CLOSED_BLACKLISTED(18), GROUP_CLOSED_LEFTPROGRAM(19), GROUP_CLOSED_OTHER(
            20), ;

    private Short value;

    private CustomerStatusFlag(int value) {
        this.value = (short) value;
    }

    public Short getValue() {
        return value;
    }

    public static CustomerStatusFlag getStatusFlag(Short value) {
        for (CustomerStatusFlag statusFlag : CustomerStatusFlag.values()) {
            if (statusFlag.getValue().equals(value)) {
                return statusFlag;
            }
        }
        throw new RuntimeException("no status flag " + value);
    }

    public boolean isBlacklisted() {
        return this == CLIENT_CANCEL_BLACKLISTED || this == CLIENT_CLOSED_BLACKLISTED
                || this == GROUP_CANCEL_BLACKLISTED || this == GROUP_CLOSED_BLACKLISTED;
    }

}
