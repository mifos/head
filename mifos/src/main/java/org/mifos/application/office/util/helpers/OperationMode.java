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

package org.mifos.application.office.util.helpers;

/**
 * Represent the operating mode of the office
 */
public enum OperationMode {
    LOCAL_SERVER(0), REMOTE_SERVER(1);

    private short value;

    private OperationMode(int value) {
        this.value = (short) value;
    }

    public Short getValue() {
        return value;
    }

    public static OperationMode fromInt(int id) {
        for (OperationMode candidate : OperationMode.values()) {
            if (candidate.value == id) {
                return candidate;
            }
        }
        throw new RuntimeException("no operation mode " + id);
    }

}
