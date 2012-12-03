package org.mifos.test.acceptance.admin;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.ClientDataImportReviewPage;
import org.mifos.test.acceptance.framework.admin.ClientDataImportSaveSummaryPage;
import org.mifos.test.acceptance.framework.testhelpers.AdminTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.OfficeHelper;
import org.mifos.test.acceptance.framework.testhelpers.UserHelper;
import org.mifos.test.acceptance.framework.user.CreateUserParameters;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@SuppressWarnings("PMD.SignatureDeclareThrowsException")
@Test(singleThreaded = true, groups = { "client", "acceptance", "import" })
public class ClientDataImportTest extends UiTestCaseBase {
    private AdminTestHelper adminTestHelper;
    private OfficeHelper officeHelper;
    String[] arrayOfErrors;

    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        adminTestHelper = new AdminTestHelper(selenium);
        officeHelper = new OfficeHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
        new DateTimeUpdaterRemoteTestingService(selenium).resetDateTime();
    }

    @Test(enabled = true)
    public void importSavingAccountsToClientTest() {
        createOfficeAndLoanOfficer();
        
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
        String[] arrayString = { "Error in row 5, Column Recruited by: Empty mandatory field",
                "Error in row 7: Incomplete meeting data",
                "Error in row 8, Column Activation date: Activation Date can be set only for Active status",
                "Error in row 12, Column Activation date: Activation Date can be set only for Active status" };
        return arrayString;
    }
    
    private void createOfficeAndLoanOfficer(){
        officeHelper.defineOffice("dataImportOffice", "DATA", "Branch Office", "Head Office(Mifos HO )");
        UserHelper userHelper = new UserHelper(selenium);
        CreateUserParameters userParameters = new CreateUserParameters();
        userParameters.setFirstName("loan");
        userParameters.setLastName("officerTest");
        userParameters.setDateOfBirthDD("11");
        userParameters.setDateOfBirthMM("11");
        userParameters.setDateOfBirthYYYY("1950");
        String userName = StringUtil.getRandomString(6);
        String password = StringUtil.getRandomString(6);
        userParameters.setPassword(password);
        userParameters.setPasswordRepeat(password);
        userParameters.setUserName(userName);
        userParameters.setUserLevel(CreateUserParameters.LOAN_OFFICER);
        userParameters.setGender(CreateUserParameters.MALE);
        userHelper.createUser(userParameters, "dataImportOffice");
    }
}
