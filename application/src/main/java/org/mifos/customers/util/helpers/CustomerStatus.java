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

package org.mifos.customers.util.helpers;

import org.mifos.customers.group.util.helpers.GroupConstants;

public enum CustomerStatus {
    CLIENT_PARTIAL(Short.valueOf("1")), CLIENT_PENDING(Short.valueOf("2")), CLIENT_ACTIVE(Short.valueOf("3")), CLIENT_HOLD(
            Short.valueOf("4")), CLIENT_CANCELLED(Short.valueOf("5")), CLIENT_CLOSED(Short.valueOf("6")),

    GROUP_PARTIAL(GroupConstants.PARTIAL_APPLICATION), GROUP_PENDING(GroupConstants.PENDING_APPROVAL), GROUP_ACTIVE(
            GroupConstants.ACTIVE), GROUP_HOLD(GroupConstants.HOLD), GROUP_CANCELLED(GroupConstants.CANCELLED), GROUP_CLOSED(
            GroupConstants.CLOSED),

    CENTER_ACTIVE(Short.valueOf("13")), CENTER_INACTIVE(Short.valueOf("14"));

    private Short value;

    private CustomerStatus(Short value) {
        this.value = value;
    }

    public Short getValue() {
        return value;
    }

    public static CustomerStatus fromInt(int value) {
        for (CustomerStatus status : CustomerStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new RuntimeException("no customer status " + value);
    }

    public boolean isGroupPartialOrGroupPending() {
        return isGroupPartial() || isGroupPending();
    }

    public boolean isGroupActive() {
        return CustomerStatus.GROUP_ACTIVE.getValue().equals(this.value);
    }

    public boolean isGroupClosed() {
        return CustomerStatus.GROUP_CLOSED.getValue().equals(this.value);
    }

    public boolean isGroupPartial() {
        return CustomerStatus.GROUP_PARTIAL.getValue().equals(this.value);
    }

    public boolean isGroupCancelled() {
        return CustomerStatus.GROUP_CANCELLED.getValue().equals(this.value);
    }

    public boolean isGroupPending() {
        return CustomerStatus.GROUP_PENDING.getValue().equals(this.value);
    }

    public boolean isClientPending() {
        return CustomerStatus.CLIENT_PENDING.getValue().equals(this.value);
    }
}
