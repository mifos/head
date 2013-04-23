package org.mifos.test.acceptance.guaranty;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
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
public class GuarantyCoreTest extends UiTestCaseBase {
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
    
    public void createAndDisburseGroupLoan() throws Exception{
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2013, 04, 9, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Default Group");
        searchParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");
        List<GLIMClient> glimClients = new ArrayList<GLIMClient>();
        glimClients.add(new GLIMClient(0, "Stu1233266299995 Client1233266299995 Client Id: 0002-000000012", "1000", null));
        glimClients.add(new GLIMClient(1, "Stu1233266309851 Client1233266309851 Client Id: 0002-000000013", "100", null));
        LoanAccountPage loanAccountPage = loanTestHelper.createGroupLoanAccount(searchParameters, glimClients);
        
        String loanId = loanAccountPage.getAccountId();
        EditLoanAccountStatusParameters statusParameters = new EditLoanAccountStatusParameters();
        statusParameters.setStatus(EditLoanAccountStatusParameters.APPROVED);
        statusParameters.setNote("Test apply Guaranty");
        loanTestHelper.changeLoanAccountStatus(loanId, statusParameters);
        
        DisburseLoanParameters disburseParams = new DisburseLoanParameters();
        disburseParams.setDisbursalDateDD("09");
        disburseParams.setDisbursalDateMM("04");
        disburseParams.setDisbursalDateYYYY("2013");
        disburseParams.setPaymentType(DisburseLoanParameters.CASH);
        loanAccountPage = loanAccountPage.disburseLoan(disburseParams);
        verifyGuarantyGroupIndividualLoan(loanAccountPage);
    }
    public void verifyGuarantyGroupIndividualLoan(LoanAccountPage loanAccountPage){
        String loanId = loanAccountPage.getAccountId();
        loanAccountPage.navigateToIndividualLoanAccountPage(1);
        String guarantiedLoanId=loanAccountPage.getAccountId();
        String guarantiedClientName="Stu1233266309851 Client1233266309851";
        String guarantorName="Holiday TestClient";
        String guarantorGlobalId= "0002-000000023";
        loanTestHelper.applyGuarantyFromLoanAccountPage(guarantorName, guarantorGlobalId);
        
        Assert.assertTrue(selenium.isTextPresent(guarantorName));
        selenium.open("viewClientDetails.ftl?globalCustNum=" + guarantorGlobalId);
        Assert.assertTrue(selenium.isTextPresent(guarantiedClientName));
        Assert.assertTrue(selenium.isTextPresent(guarantiedLoanId));
        loanTestHelper.repayLoan(loanId);
        selenium.open("viewClientDetails.ftl?globalCustNum=" + guarantorGlobalId);
        Assert.assertFalse(selenium.isTextPresent(guarantiedClientName));
        Assert.assertFalse(selenium.isTextPresent(guarantiedLoanId));
    }
}