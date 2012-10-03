/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.accounting.AccountingDto;
import org.mifos.platform.accounting.dao.AccountingDao;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AccountingDataCacheManager.class)
public class AccountingServiceTest {

    @Mock
    private AccountingDao accountingDao;

    @Mock
    private AccountingDataCacheManager cacheManager;

    AccountingService accountingService;

    @Before
    public void setUp() {
        DateTimeUtils.setCurrentMillisFixed(new DateTime(2011,02,11,14,0,0,0).getMillis());
        accountingService = new AccountingServiceImpl(cacheManager, accountingDao);
    }

    @After
    public void tearDown() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void shouldGetTallyOutputFileName() throws Exception {
        when(cacheManager.getTallyOutputFileName(any(LocalDate.class), any(LocalDate.class))).thenReturn(
                "DummyFileName");
        String fileName = accountingService.getExportOutputFileName(new LocalDate(2010, 8, 10), new LocalDate(2010, 8, 10));
        Assert.assertEquals("DummyFileName", fileName);
    }

    @Test
    public void shouldGetTallyOutputFromCache() throws Exception {
        when(cacheManager.getTallyOutputFileName(any(LocalDate.class), any(LocalDate.class))).thenReturn(
                "DummyFileName");
        List<AccountingDto> dataFromCache = new ArrayList<AccountingDto>();
        dataFromCache.add(new AccountingDto("branch", "2010-10-12", "RECEIPT", "234324", "GLCODE NAME", "5", "546"));
        dataFromCache.add(new AccountingDto("branch", "2010-10-12", "RECEIPT", "15249", "GLCODE NAME", "6", "544"));
        when(cacheManager.isAccountingDataAlreadyInCache(any(String.class))).thenReturn(true);
        when(accountingDao.getAccountingDataByDate(any(LocalDate.class), any(LocalDate.class))).thenReturn(
                dataFromCache);
        when(cacheManager.getExportDetails(any(String.class))).thenReturn(dataFromCache);
        String output = accountingService.getExportOutput(new LocalDate(2010, 8, 10), new LocalDate(2010, 8, 10));
        Assert.assertTrue("Should be receipt type", output.contains("VCHTYPE=\"Receipt\""));
        Assert.assertTrue("Date should be set to 20101012", output.contains("<DATE>20101012</DATE>"));
    }

    @SuppressWarnings("serial")
    @Test
    public void testGetExports() {
        LocalDate finTrxnStartDate = new LocalDate(2011,1,4);
        List<LocalDate> dateList = new ArrayList<LocalDate>() {{ add(new LocalDate().minusDays(1)); add(new LocalDate().minusDays(2));
            add(new LocalDate().minusDays(3)); add(new LocalDate().minusDays(4)); add(new LocalDate().minusDays(5)); 
            add(new LocalDate().minusDays(5)); add(new LocalDate().minusDays(6)); add(new LocalDate().minusDays(7)); 
            add(new LocalDate().minusDays(8)); add(new LocalDate().minusDays(9));
            }};
        List<LocalDate> dateList2 = new ArrayList<LocalDate>() {{ add(new LocalDate().minusDays(10)); add(new LocalDate().minusDays(11));
            add(new LocalDate().minusDays(12)); add(new LocalDate().minusDays(13)); add(new LocalDate().minusDays(14)); 
            }};
        Integer offset = 0;
        when(cacheManager.isAccountingDataAlreadyInCache(any(String.class))).thenReturn(false);
        when(accountingDao.getStartDateOfFinancialTransactions()).thenReturn(finTrxnStartDate);
        when(accountingDao.getTenTxnDate(finTrxnStartDate, new LocalDate().minusDays(1), offset)).thenReturn(dateList);
        Assert.assertEquals(38, Days.daysBetween(finTrxnStartDate, new LocalDate()).getDays());
        Assert.assertEquals(10,accountingService.getLastTenExports(0).size());
        Assert.assertTrue(accountingService.getLastTenExports(0).get(0).getStartDate().equals("2011-02-10"));
        // Start from previous day
        offset +=10;
        when(accountingDao.getTenTxnDate(finTrxnStartDate, new LocalDate().minusDays(1), offset)).thenReturn(dateList2);
        Assert.assertEquals(5,accountingService.getLastTenExports(10).size());
        Assert.assertTrue(accountingService.getLastTenExports(10).get(4).getStartDate().equals(new LocalDate().minusDays(14).toString()));
        Assert.assertEquals(0,accountingService.getLastTenExports(20).size());
    }


    @Test
    public void shouldReturnTrueOnDeleteDataDir() {
        when(cacheManager.deleteCacheDir()).thenReturn(true);
        Assert.assertTrue(accountingService.deleteCacheDir());
    }

    @Test
    public void testHasAlreadyRanQuery() {
        when(cacheManager.isAccountingDataAlreadyInCache(any(String.class))).thenReturn(true);
        Assert.assertTrue(accountingService.hasAlreadyRanQuery(new LocalDate(2010, 8, 10), new LocalDate(2010, 8, 10)));
    }

}
