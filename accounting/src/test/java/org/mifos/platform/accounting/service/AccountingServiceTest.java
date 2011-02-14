/*
 * Copyright Grameen Foundation USA
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.accounting.AccountingDto;
import org.mifos.platform.accounting.dao.IAccountingDao;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AccountingDataCacheManager.class)
public class AccountingServiceTest {

    @Mock
    private IAccountingDao accountingDao;

    @Mock
    private AccountingDataCacheManager cacheManager;

    IAccountingService accountingService;

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
    }

    @Test
    public void testGetExports() {
        LocalDate finTrxnStartDate = new LocalDate(2011,1,4);
        when(cacheManager.isAccountingDataAlreadyInCache(any(String.class))).thenReturn(false);
        when(accountingDao.getStartDateOfFinancialTransactions()).thenReturn(finTrxnStartDate);
        Assert.assertEquals(38, Days.daysBetween(finTrxnStartDate, new LocalDate()).getDays());
        Assert.assertEquals(10,accountingService.getLastTenExports(0).size());
        // Start from previous day
        Assert.assertTrue(accountingService.getLastTenExports(0).get(0).getStartDate().equals("2011-02-10"));
        Assert.assertEquals(10,accountingService.getLastTenExports(10).size());
        Assert.assertEquals(10,accountingService.getLastTenExports(20).size());
        Assert.assertEquals(8,accountingService.getLastTenExports(30).size());
        Assert.assertTrue(accountingService.getLastTenExports(30).get(7).getStartDate().equals("2011-01-04"));
        Assert.assertEquals(0,accountingService.getLastTenExports(40).size());
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
