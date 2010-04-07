package org.mifos.test.acceptance.reports;

import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.ViewReportCategoriesPage;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests if the Report Categories page lists the expected Report Categories
 */

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(groups={"reports", "ui", "workInProgress"})
public class ViewReportCategoriesTest extends UiTestCaseBase {
    private AppLauncher appLauncher;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
    }

    @AfterMethod
    public void tearDown() {
        (new MifosPage(selenium)).logout();
    }

    /**
     * Verify that expected Report Categories exist on the page
     */
    public void verifyReportCategoriesExist() {
        AdminPage adminPage = loginAndGoToAdminPage();
        ViewReportCategoriesPage page = adminPage.navigateToViewReportCategories();
        page.verifyPage();
        String[] expectedData = new String[]{
                "Client Detail",
                "Performance",
                "Center",
                "Loan Product Detail",
                "Status",
                "Analysis",
                "Miscellaneous"
        };

        page.verifyReportCategoriesExist(expectedData);
    }

    private AdminPage loginAndGoToAdminPage() {
        HomePage homePage = appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials();
        homePage.verifyPage();
        AdminPage adminPage = homePage.navigateToAdminPage();
        adminPage.verifyPage();
        return adminPage;
    }
}
