package org.mifos.platform.accounting.ui.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.accounting.service.AccountingService;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AccountingService.class)
public class AccountingDataControllerTest {

    @Mock
    private AccountingService accountingService;

    private AccountingDataController accountingDataController;

    @Before
    public void SetUp() {
        accountingDataController = new AccountingDataController(accountingService);
    }

    @Test
    public void testShowAccountingDataForWithNull() {
        accountingDataController.showAccountingDataFor("2010-05-10", "2010-05-10");
    }

    @Test
    public void testShowAccountingDataFor() {
        when(accountingService.hasAlreadyRanQuery(any(LocalDate.class), any(LocalDate.class))).thenReturn(true);
        when(accountingService.getExportOutputFileName(any(LocalDate.class), any(LocalDate.class))).thenReturn(
                "dummyfile");
        when(accountingService.getExportDetails(any(LocalDate.class), any(LocalDate.class))).thenReturn(null);
        accountingDataController.showAccountingDataFor("2010-05-10", "2010-05-10");
    }

    @Test
    public void testDeleteCacheDir() {
        accountingDataController.deleteCacheDir();
    }

    @Test
    public void testAccountingCacheInfo() {
        accountingDataController.listAllExports();
    }

}
