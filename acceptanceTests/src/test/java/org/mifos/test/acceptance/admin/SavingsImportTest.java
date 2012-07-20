package org.mifos.test.acceptance.admin;

import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.ImportSavingsReviewPage;
import org.mifos.test.acceptance.framework.admin.ImportSavingsSaveSummaryPage;
import org.mifos.test.acceptance.framework.admin.ManageRolePage;
import org.mifos.test.acceptance.framework.savingsproduct.SavingsProductParameters;
import org.mifos.test.acceptance.framework.testhelpers.AdminTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.SavingsProductHelper;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@SuppressWarnings("PMD.SignatureDeclareThrowsException")
@Test(singleThreaded = true, groups = { "client", "acceptance", "import" })
public class SavingsImportTest extends UiTestCaseBase {
    private SavingsProductHelper savingsProductHelper;
    private AdminTestHelper adminTestHelper;
    private NavigationHelper navigationHelper;
    String[] arrayOfErrors;

    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        savingsProductHelper = new SavingsProductHelper(selenium);
        adminTestHelper = new AdminTestHelper(selenium);
        navigationHelper = new NavigationHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @Test(enabled = true)
    public void importSavingAccountsToClientTest() {
        ManageRolePage manageRolePage = navigationHelper.navigateToAdminPage().navigateToViewRolesPage().navigateToManageRolePage("Admin");
        try {
            manageRolePage.enablePermission("8_8");
        } catch (AssertionError ex) {
            Logger.getAnonymousLogger().info("Permission is marked.");
        }
        manageRolePage.submitAndGotoViewRolesPage();
        String succesNumber = "1";
        String errorNumber = "5";
        arrayOfErrors = buildArrayOfErrorsForImportSavingsTest();
        String importFile = this.getClass().getResource("/ImportSavingsAccountsTest.xls").toString();
        SavingsProductParameters parameters = savingsProductHelper.getGenericSavingsProductParameters(new DateTime(),
                SavingsProductParameters.VOLUNTARY, SavingsProductParameters.CLIENTS);
        parameters.setProductInstanceName("importSavings");
        parameters.setShortName("IMP");
        try {
            savingsProductHelper.createSavingsProduct(parameters);
        } catch (AssertionError e) {
            Logger.getAnonymousLogger().info("Product exists");
        }
        ImportSavingsReviewPage reviewPage = adminTestHelper.loadImportSavingsFileAndSubmitForReview(importFile);
        reviewPage.validateErrors(arrayOfErrors);
        reviewPage.validateSuccesText(succesNumber);
        ImportSavingsSaveSummaryPage summaryPage = reviewPage.saveSuccessfullRows();
        summaryPage.verifySuccesString(succesNumber);
        summaryPage.verifyErrorString(errorNumber);
    }

    private String[] buildArrayOfErrorsForImportSavingsTest() {
        String[] arrayString = { "Error in row 3, Column 2: Customer with global id 2 not found",
                "Error in row 4, Column 3: Missing product name",
                "Error in row 5, Column 3: Active and applicable product with name WrongProductName not found",
                "Error in row 6, Column 4: Missing account status name",
                "Error in row 7, Column 4: Saving status is incorrect" };
        return arrayString;
    }
}
