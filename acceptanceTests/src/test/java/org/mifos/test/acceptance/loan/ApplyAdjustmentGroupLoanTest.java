package org.mifos.test.acceptance.loan;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.springframework.test.context.ContextConfiguration;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountConfirmationPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountPreviewPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountReviewInstallmentPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.PaymentParameters;
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;


@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, enabled = true,groups = {"acceptance", "loan", "no_db_unit"})

public class ApplyAdjustmentGroupLoanTest extends UiTestCaseBase {
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
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void adjustmentWithCommaTest() throws Exception{
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2013, 02, 8, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Default Group");
        searchParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");
        CreateLoanAccountEntryPage loanAccountEntryPage = loanTestHelper.navigateToCreateLoanAccountEntryPage(searchParameters);
        
        loanAccountEntryPage.setDisbursalDate(new DateTime(2013, 2, 8, 15, 0, 0, 0));
        loanAccountEntryPage.selectGLIMClients(0, "Stu1233266299995 Client1233266299995 Client Id: 0002-000000012", "1500", null);
        loanAccountEntryPage.selectGLIMClients(2, "Stu1233266319760 Client1233266319760 Client Id: 0002-000000014", "1500", null);
        CreateLoanAccountReviewInstallmentPage createLoanAccountReviewInstallmentPage = loanAccountEntryPage.navigateToReviewInstallmentsPage();
        CreateLoanAccountPreviewPage createLoanAccountPreviewPage = createLoanAccountReviewInstallmentPage.clickPreviewAndGoToReviewLoanAccountPage();
        CreateLoanAccountConfirmationPage createLoanAccountConfirmationPage = createLoanAccountPreviewPage.submitForApprovalAndNavigateToConfirmationPage();
        
        LoanAccountPage loanAccountPage = createLoanAccountConfirmationPage.navigateToLoanAccountDetailsPage();
        String loanId = loanAccountPage.getAccountId();
        EditLoanAccountStatusParameters statusParameters = new EditLoanAccountStatusParameters();
        statusParameters.setStatus(EditLoanAccountStatusParameters.APPROVED);
        statusParameters.setNote("Test new GLIM");
        loanTestHelper.changeLoanAccountStatus(loanId, statusParameters);
        
        DisburseLoanParameters params = new DisburseLoanParameters();
        params.setPaymentType(DisburseLoanParameters.CASH);
        loanTestHelper.disburseLoan(loanId, params);
        
        PaymentParameters paymentParameters = new PaymentParameters();
        paymentParameters.setAmount("2500.0");
        paymentParameters.setPaymentType(PaymentParameters.CASH);
        paymentParameters.setTransactionDateDD("08");
        paymentParameters.setTransactionDateMM("2");
        paymentParameters.setTransactionDateYYYY("2013");
        loanTestHelper.applyGroupPayment(loanId,paymentParameters);
        loanAccountPage.navigateToApplyAdjustment().fillAdjustmentFieldsWithoutRevertingAndSubmitGroupLoan("2500.0");
        Assert.assertFalse(selenium.isTextPresent("stack trace"));
        Assert.assertTrue(selenium.isTextPresent("2,500"));
        
    }
}