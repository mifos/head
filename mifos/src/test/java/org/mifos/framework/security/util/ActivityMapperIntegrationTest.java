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

package org.mifos.framework.security.util;

import java.util.regex.Pattern;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class ActivityMapperIntegrationTest extends MifosIntegrationTest {
    public ActivityMapperIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    Pattern allowableActionName = Pattern.compile("([a-zA-Z])+");

    @Test
    public void testNamesAcceptable() {
        for (ActionSecurity security : ActivityMapper.getInstance().getAllSecurity()) {
            String name = security.getActionName();
            assertTrue("unacceptable action name " + name, acceptableName(name));
        }
    }

    @Test
    public void testMachinery() {
        assertTrue(acceptableName("openSesame"));
        assertFalse(acceptableName("/bin/sh"));
        assertFalse(acceptableName("/openSesame"));
        assertFalse(acceptableName("open,sesame"));
        assertFalse(acceptableName("open sesame"));
        assertFalse(acceptableName("openSesame "));
        assertFalse(acceptableName(""));
        assertFalse(acceptableName(null));
    }

    private boolean acceptableName(String name) {
        if (name == null) {
            return false;
        }
        return allowableActionName.matcher(name).matches();
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ActivityMapperIntegrationTest.class);
    }

}
