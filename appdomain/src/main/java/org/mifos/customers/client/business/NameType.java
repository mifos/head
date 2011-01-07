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

package org.mifos.customers.client.business;

public enum NameType {

    CLIENT(3), SPOUSE(2),

    /**
     * This value is supplied in test data where {@link #CLIENT} would seem to
     * make more sense. I do not yet have evidence that any of the tests which
     * supply this actually need this rather than CLIENT. Is this just a test
     * mistake? Or is there a real meaning for 1?
     */
    MAYBE_CLIENT(1);

    private final short value;

    private NameType(int value) {
        this.value = (short) value;
    }

    public short getValue() {
        return value;
    }

}
