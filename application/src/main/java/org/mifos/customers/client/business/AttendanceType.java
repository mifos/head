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

package org.mifos.customers.client.business;

/*
 * Encapsulates valid id values for {@link CustomerAttendanceType} class.
 */
public enum AttendanceType {

    PRESENT(1), ABSENT(2), APPROVED_LEAVE(3), LATE(4);

    private short value;

    private AttendanceType(int value) {
        this.value = (short) value;
    }

    public short getValue() {
        return value;
    }

    public static AttendanceType fromShort(short target) {
        for (AttendanceType candidate : values()) {
            if (candidate.getValue() == target) {
                return candidate;
            }
        }
        throw new RuntimeException("no attendance type " + target);
    }

}
