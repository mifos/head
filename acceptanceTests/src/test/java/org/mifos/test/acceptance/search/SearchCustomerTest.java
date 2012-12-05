package org.mifos.test.acceptance.search;

import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.search.SearchResultsPage;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"search", "acceptance", "ui"})
public class SearchCustomerTest extends SearchTestBase {
    private AppLauncher appLauncher;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml", dataSource, selenium);
    }

    @AfterMethod
    public void tearDown() {
        (new MifosPage(selenium)).logout();
        new DateTimeUpdaterRemoteTestingService(selenium).resetDateTime();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void searchCustomerTest() throws Exception {
        String searchPhrase = "Client Veronica Abisya";
        SearchResultsPage page = searchFor(appLauncher, searchPhrase);
        page.verifySearchResults(1, "Client - Veronica Abisya");
    }
}
