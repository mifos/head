package org.mifos.test.acceptance.admin;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.ClientDataImportReviewPage;
import org.mifos.test.acceptance.framework.admin.ClientDataImportSaveSummaryPage;
import org.mifos.test.acceptance.framework.testhelpers.AdminTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@SuppressWarnings("PMD.SignatureDeclareThrowsException")
@Test(singleThreaded = true, groups = { "client", "acceptance", "import" })
public class ClientDataImportTest extends UiTestCaseBase {
    private AdminTestHelper adminTestHelper;
    String[] arrayOfErrors;

    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        adminTestHelper = new AdminTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
        new DateTimeUpdaterRemoteTestingService(selenium).resetDateTime();
    }

    @Test(enabled = true)
    public void importSavingAccountsToClientTest() {
        String importFile = this.getClass().getResource("/ClientDataImportTest.xls").toString();
        
        String succesNumber = "4";
        String errorNumber = "4";
        arrayOfErrors = buildArrayOfErrorsForImportSavingsTest();

        ClientDataImportReviewPage reviewPage = adminTestHelper.loadClientDataImportFileAndSubmitForReview(importFile);
        reviewPage.validateErrors(arrayOfErrors);
        reviewPage.validateSuccesText(succesNumber);
        ClientDataImportSaveSummaryPage summaryPage = reviewPage.saveSuccessfullRows();
        summaryPage.verifySuccesString(succesNumber);
        summaryPage.verifyErrorString(errorNumber);
    }

    private String[] buildArrayOfErrorsForImportSavingsTest() {
        String[] arrayString = { "Error in row 7: Incomplete meeting data",
                "Error in row 8, Column Activation date: Activation Date can be set only for Active status",
                "Error in row 9, Column Loan Officer: Loan officer cannot be specified for a client being added to a group",
                "Error in row 12, Column Activation date: Activation Date can be set only for Active status" };
        return arrayString;
    }
}
