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

package org.mifos.platform.accounting.service;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.mifos.platform.accounting.service.AccountingCacheFileInfo;

public class AccountingCacheFileInfoTest {

    @Test
    public void testAccountingCacheFileInfo() {
        AccountingCacheFileInfo a = new AccountingCacheFileInfo(new DateTime(2010, 05, 07, 0, 0, 0, 0), "Mifos Accounting Export ", "2010-05-05 to 2010-06-07");
        Assert.assertEquals("Mifos Accounting Export ", a.getMfiPrefix());
        Assert.assertEquals(new DateTime(2010, 05, 07, 0, 0, 0, 0), a.getLastModified());
        Assert.assertEquals("2010-05-05", a.getStartDateInString());
        Assert.assertEquals("2010-06-07", a.getEndDateInString());
        Assert.assertEquals("2010-05-05 to 2010-06-07", a.getFileName());
    }
}
