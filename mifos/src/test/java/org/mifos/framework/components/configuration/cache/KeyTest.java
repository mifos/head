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

package org.mifos.framework.components.configuration.cache;

import junit.framework.TestCase;

import org.mifos.framework.TestUtils;

public class KeyTest extends TestCase {

    public void testEquals() throws Exception {
        Key equal1 = new Key((short) 1, "a");
        Key equal2 = new Key((short) 1, "a");
        Key equal3 = new Key((short) 1, "A");
        Key notEqual1 = new Key((short) 1, "b");
        Key notEqual2 = new Key((short) 2, "a");
        Key notEqual3 = new Key(null, null);
        Key subclass = new Key((short) 1, "a") {
        };

        TestUtils.verifyBasicEqualsContract(new Key[] { equal1, equal2, equal3 }, new Key[] { notEqual1, notEqual2,
                notEqual3, subclass });
    }

}
