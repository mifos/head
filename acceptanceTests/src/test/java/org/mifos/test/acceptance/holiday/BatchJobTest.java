package org.mifos.test.acceptance.holiday;

import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.testhelpers.BatchJobHelper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class BatchJobTest extends UiTestCaseBase{


    private AppLauncher appLauncher;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        appLauncher = new AppLauncher(selenium);
    }

    @Test(enabled = false)
    public void testRunAllBatchJobs(){
        loginAndNavigateToAdminPage();

        new BatchJobHelper(selenium).runAllBatchJobs();
    }

    private AdminPage loginAndNavigateToAdminPage() {
        return appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials().navigateToAdminPage();

    }

}
