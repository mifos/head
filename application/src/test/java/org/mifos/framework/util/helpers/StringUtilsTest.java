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

package org.mifos.framework.util.helpers;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.mifos.security.activity.DynamicLookUpValueCreationTypes;
import org.testng.annotations.Test;

@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class StringUtilsTest extends TestCase {

    public void testCamelCase() {
       Assert.assertEquals("AbcDef_ghIjKL", SearchUtils.camelCase("aBc dEF_gh-iJ  k.l"));
    }

    public void testGenerateLookupName() {
        String newElementText = "OfficeLevels";
        String lookupName = SearchUtils.generateLookupName(DynamicLookUpValueCreationTypes.DBUpgrade.name(),
                newElementText);
       Assert.assertEquals(0, lookupName.indexOf("DBUpgrade"));
        String tooLong = "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
                + "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
                + "0";
        lookupName = SearchUtils.generateLookupName(DynamicLookUpValueCreationTypes.DBUpgrade.name(), tooLong);
       Assert.assertEquals(100, lookupName.length());

    }

}
