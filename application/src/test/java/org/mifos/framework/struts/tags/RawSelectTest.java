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

package org.mifos.framework.struts.tags;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class RawSelectTest extends TestCase {

    public RawSelectTest() throws SystemException, ApplicationException {
        super();
    }

    public void testRawSelect() {
        Map<String, String> data = new HashMap<String, String>();
        data.put("key", "value");
        data.put("key1", "value1");
        RawSelect rawSelect = new RawSelect();
        rawSelect.setData(data);
        rawSelect.setMultiple("multiple");
        rawSelect.setName("name");
        rawSelect.setSize("1");
        rawSelect.setStyle("style");
        assertEquals(2, rawSelect.getData().size());
        assertEquals("multiple", rawSelect.getMultiple());
        assertEquals("name", rawSelect.getName());
        assertEquals("1", rawSelect.getSize());
        assertEquals("style", rawSelect.getStyle());
        assertTrue(rawSelect.toString().contains("Select the item"));
    }
}
