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

package org.mifos.application.customer.util.helpers;

import static org.mifos.application.customer.util.helpers.CustomerStatusFlag.CLIENT_CANCEL_BLACKLISTED;
import static org.mifos.application.customer.util.helpers.CustomerStatusFlag.CLIENT_CANCEL_WITHDRAW;
import static org.mifos.application.customer.util.helpers.CustomerStatusFlag.CLIENT_CLOSED_BLACKLISTED;
import static org.mifos.application.customer.util.helpers.CustomerStatusFlag.CLIENT_CLOSED_OTHER;
import static org.mifos.application.customer.util.helpers.CustomerStatusFlag.GROUP_CANCEL_BLACKLISTED;
import static org.mifos.application.customer.util.helpers.CustomerStatusFlag.GROUP_CLOSED_BLACKLISTED;
import static org.mifos.application.customer.util.helpers.CustomerStatusFlag.GROUP_CLOSED_LEFTPROGRAM;
import junit.framework.Assert;
import junit.framework.TestCase;

import org.testng.annotations.Test;

@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class CustomerStatusFlagTest extends TestCase {

    public void testIsBlacklisted() throws Exception {
       Assert.assertTrue(CLIENT_CANCEL_BLACKLISTED.isBlacklisted());
       Assert.assertTrue(CLIENT_CLOSED_BLACKLISTED.isBlacklisted());
       Assert.assertTrue(GROUP_CANCEL_BLACKLISTED.isBlacklisted());
       Assert.assertTrue(GROUP_CLOSED_BLACKLISTED.isBlacklisted());
        Assert.assertFalse(CLIENT_CANCEL_WITHDRAW.isBlacklisted());
        Assert.assertFalse(CLIENT_CLOSED_OTHER.isBlacklisted());
        Assert.assertFalse(GROUP_CLOSED_LEFTPROGRAM.isBlacklisted());
    }

}
