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

package org.mifos.security.util;

import java.util.regex.Pattern;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ActivityMapperTest extends TestCase {

    Pattern allowableActionName = Pattern.compile("([a-zA-Z])+");

    public void testNamesAcceptable() {
        for (ActionSecurity security : ActivityMapper.getInstance().getAllSecurity()) {
            String name = security.getActionName();
           Assert.assertTrue("unacceptable action name " + name, acceptableName(name));
        }
    }

    public void testMachinery() {
       Assert.assertTrue(acceptableName("openSesame"));
        Assert.assertFalse(acceptableName("/bin/sh"));
        Assert.assertFalse(acceptableName("/openSesame"));
        Assert.assertFalse(acceptableName("open,sesame"));
        Assert.assertFalse(acceptableName("open sesame"));
        Assert.assertFalse(acceptableName("openSesame "));
        Assert.assertFalse(acceptableName(""));
        Assert.assertFalse(acceptableName(null));
    }

    private boolean acceptableName(String name) {
        if (name == null) {
            return false;
        }
        return allowableActionName.matcher(name).matches();
    }
}
