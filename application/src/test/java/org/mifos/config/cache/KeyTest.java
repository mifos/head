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

package org.mifos.config.cache;

import junit.framework.TestCase;

import org.mifos.framework.TestUtils;

public class KeyTest extends TestCase {

    public void testEquals() throws Exception {
        Key x = new Key((short) 1, "a");
        Key notx = new Key((short) 2, "a");
        Key y = new Key((short) 1, "a");
        Key z = new Key((short) 1, "a");

        TestUtils.assertEqualsAndHashContract(x, notx, y, z);
    }

}
