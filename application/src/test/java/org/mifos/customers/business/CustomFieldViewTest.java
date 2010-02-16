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

package org.mifos.customers.business;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.mifos.application.master.business.CustomFieldView;
import org.testng.annotations.Test;

@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class CustomFieldViewTest extends TestCase {

    public void testEmpty() throws Exception {
        // The main point here is that we shouldn't get
        // NullPointerException for these operations.

        CustomFieldView view = new CustomFieldView();
       Assert.assertEquals("org.mifos.application.master.business.CustomFieldView@0", view.toString());
        view.hashCode();

        CustomFieldView view2 = new CustomFieldView();
       Assert.assertTrue(view.equals(view2));
    }

}
