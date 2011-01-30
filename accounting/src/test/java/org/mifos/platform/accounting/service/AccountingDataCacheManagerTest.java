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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.platform.accounting.AccountingDto;

public class AccountingDataCacheManagerTest {

    AccountingDataCacheManager cacheManager;

    @Before
    public void setUp() throws Exception {
        cacheManager = new AccountingDataCacheManager();
        String testingDir = System.getProperty("java.io.tmpdir") + "/accounting/data/";
        cacheManager.setAccoutingDataCachePath(testingDir);
        cacheManager.deleteCacheDir();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAccoutingDataFromCache() throws Exception {
        List<AccountingDto> data = new ArrayList<AccountingDto>();
        data.add(new AccountingDto("branch", "2010-10-12", "RECEIPT", "234324", "GLCODE NAME", "5", "546"));
        data.add(new AccountingDto("branch", "2010-10-12", "RECEIPT", "234324", "GLCODE NAME", "5", "546"));
        LocalDate date = new LocalDate(2010, 10, 12);
        String cacheFileName = cacheManager.getCacheFileName(date, date);
        cacheManager.writeAccountingDataToCache(data, cacheFileName);
        List<AccountingDto> accountingData = cacheManager.getExportDetails(cacheFileName);
        Assert.assertEquals(2, accountingData.size());
        DecimalFormat df = new DecimalFormat("#.0", new DecimalFormatSymbols(Locale.ENGLISH));
        for (AccountingDto dto : accountingData) {
            Assert.assertEquals("branch;2010-10-12;RECEIPT;234324;GLCODE NAME;"+df.format(5.0)+";"+df.format(546.0), dto.toString());
            Assert.assertEquals("GLCODE NAME", dto.getGlCodeName());
        }
    }
}
