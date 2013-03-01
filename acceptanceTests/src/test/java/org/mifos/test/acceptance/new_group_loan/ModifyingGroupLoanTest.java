package org.mifos.test.acceptance.new_group_loan;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountInformationPage;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountInformationParameters;
import org.mifos.test.acceptance.framework.loan.GLIMClient;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, enabled = true,groups = {"acceptance", "loan", "no_db_unit"})

@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public class ModifyingGroupLoanTest extends UiTestCaseBase {
    private LoanTestHelper loanTestHelper;
    private CustomPropertiesHelper customPropertiesHelper;
    
    @Override
    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        super.setUp();
        loanTestHelper = new LoanTestHelper(selenium);
        customPropertiesHelper = new CustomPropertiesHelper(selenium);
        customPropertiesHelper.setNewGroupLoanWithMembers(true);
    }
    @AfterMethod
    public void logOut(){
        customPropertiesHelper.setNewGroupLoanWithMembers(false);
        (new MifosPage(selenium)).logout();
    }
    
    public void editAccountInfoTest() throws Exception{
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2013, 02, 9, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Default Group");
        searchParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");
        
        List<GLIMClient> glimClients = new ArrayList<GLIMClient>();
        glimClients.add(new GLIMClient(0, "Stu1233266299995 Client1233266299995 Client Id: 0002-000000012", "1500", null));
        glimClients.add(new GLIMClient(2, "Stu1233266319760 Client1233266319760 Client Id: 0002-000000014", "1500", null));
        LoanAccountPage loanAccountPage = loanTestHelper.createGroupLoanAccount(searchParameters, glimClients);
        
        EditLoanAccountInformationPage editLoanAccountInformationPage = loanAccountPage.navigateToEditAccountInformation();
        editLoanAccountInformationPage.setAmount("3500");
        editLoanAccountInformationPage.setInterestRate("33");
        editLoanAccountInformationPage.setNumberOfInstallments("8");
        
        EditLoanAccountInformationParameters editAccountParameters = new EditLoanAccountInformationParameters();
        editAccountParameters.setPurposeOfLoan("0004-Ox/Buffalo");
        editAccountParameters.setCollateralNotes("Test Edit new GLIM Loan");
        editAccountParameters.setExternalID("1234");
        
        editLoanAccountInformationPage.editAccountParams(new CreateLoanAccountSubmitParameters(), editAccountParameters);
        loanAccountPage = editLoanAccountInformationPage.submitAndNavigateToAccountInformationPreviewPage().submitAndNavigateToLoanAccountPage();
        verifyEditedAccountParameters(loanAccountPage);
        verifyModifyAccountParametersFromIndividualMemberAccount(loanAccountPage);
    }
    
    private void verifyEditedAccountParameters(LoanAccountPage loanAccountPage) {
        Assert.assertTrue(selenium.isTextPresent("Edit account information"));
        loanAccountPage.verifyInterestRate("33");
        loanAccountPage.verifyLoanAmount("3500");
        loanAccountPage.verifyNumberOfInstallments("8");
        loanAccountPage.verifyPurposeOfLoan("0004-Ox/Buffalo");
        loanAccountPage.verifyCollateralNotes("Test Edit new GLIM Loan");
        loanAccountPage.verifyExternalId("1234");
    }
    
    private void verifyModifyAccountParametersFromIndividualMemberAccount(LoanAccountPage loanAccountPage) {
        loanAccountPage.navigateToIndividualLoanAccountPageFromPendingApprovalGroupLoan(0);
        Assert.assertFalse(selenium.isTextPresent("Edit account information"));
        loanAccountPage.navigateBack();
    }
}