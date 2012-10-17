package org.mifos.test.acceptance.loan;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import junit.framework.Assert;

import org.dbunit.DatabaseUnitException;
import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.ManageRolePage;
import org.mifos.test.acceptance.framework.loan.ApplyPaymentConfirmationPage;
import org.mifos.test.acceptance.framework.loan.ApplyPaymentPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.PaymentParameters;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalParameters;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSearchParameters;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSubmitParameters;
import org.mifos.test.acceptance.framework.savings.DepositWithdrawalSavingsParameters;
import org.mifos.test.acceptance.framework.savings.SavingsAccountDetailPage;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.SavingsAccountHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(singleThreaded = true, groups = { "acceptance", "ui", "loan", "no_db_unit" })
public class SavingsPaymentTest extends UiTestCaseBase {

    private static final String CLIENT_GLOBAL_NUM = "Stu1233266063395 Client1233266063395";
    private static final int TRANSFER_AMOUNT = 100;
    private static final int SAVINGS_START_BALANCE = 1000;

    private NavigationHelper navigationHelper;
    private LoanTestHelper loanTestHelper;
    private SavingsAccountHelper savingsTestHelper;

    @BeforeMethod(alwaysRun = true)
    @SuppressWarnings("PMD")
    public void setUp() throws Exception {
        super.setUp();
        navigationHelper = new NavigationHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium);
        savingsTestHelper = new SavingsAccountHelper(selenium);
        setupTime();
    }

    @Test(dependsOnMethods = "testPermissionForPaymentFromSavings")
    public void testPaymentFromSavings() {
        String loanGlobalNum = setUpLoanAccount();
        String savingsGlobalNum = setUpSavingsAccount();
        LoanAccountPage loanAccountPage = makePaymentFromSavings(loanGlobalNum, savingsGlobalNum, TRANSFER_AMOUNT);
        verifyLoanAccount(loanAccountPage, String.valueOf(TRANSFER_AMOUNT));
        verifySavingsAccount(savingsGlobalNum, String.valueOf(SAVINGS_START_BALANCE - TRANSFER_AMOUNT));
    }

    private void setupTime() throws DatabaseUnitException, SQLException, IOException, URISyntaxException {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(
                selenium);
        DateTime targetTime = new DateTime(2011, 3, 13, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
    }

    private String setUpLoanAccount() {
        RedoLoanDisbursalParameters redoParams = new RedoLoanDisbursalParameters();
        redoParams.setDisbursalDateDD("12");
        redoParams.setDisbursalDateMM("03");
        redoParams.setDisbursalDateYYYY("2011");

        LoanAccountPage loanAccountPage = loanTestHelper.redoLoanDisbursal(CLIENT_GLOBAL_NUM,
                "ClientEmergencyLoan", redoParams, null, 0, false);
        return loanAccountPage.getAccountId();
    }

    private String setUpSavingsAccount() {
        CreateSavingsAccountSearchParameters searchParameters = new CreateSavingsAccountSearchParameters();
        searchParameters.setSearchString(CLIENT_GLOBAL_NUM);
        searchParameters.setSavingsProduct("MonthlyClientSavingsAccount");

        CreateSavingsAccountSubmitParameters submitAccountParameters = new CreateSavingsAccountSubmitParameters();
        submitAccountParameters.setAmount("100");

        SavingsAccountDetailPage savingsPage = savingsTestHelper.createSavingsAccount(searchParameters,
                submitAccountParameters);
        String savingsId = savingsPage.getAccountId();

        savingsTestHelper.activateSavingsAccount(savingsId);

        DepositWithdrawalSavingsParameters depositParams = new DepositWithdrawalSavingsParameters();
        depositParams.setAmount(String.valueOf(SAVINGS_START_BALANCE));
        depositParams.setPaymentType(DepositWithdrawalSavingsParameters.CASH);
        depositParams.setTrxnType(DepositWithdrawalSavingsParameters.DEPOSIT);
        savingsTestHelper.makeDepositOrWithdrawalOnSavingsAccount(savingsId, depositParams);

        return savingsId;
    }

    private LoanAccountPage makePaymentFromSavings(String loanGlobalNum, String savingsGlobalNum, int amount) {
        CreateLoanAccountSearchParameters loanSearchParams = new CreateLoanAccountSearchParameters();
        loanSearchParams.setSearchString(loanGlobalNum);
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanGlobalNum);

        ApplyPaymentPage applyPaymentPage = loanAccountPage.navigateToApplyPayment();
        PaymentParameters paymentParams = new PaymentParameters();
        paymentParams.setAmount(String.valueOf(amount));
        paymentParams.setPaymentType(PaymentParameters.TRANSFER);
        paymentParams.setSavingsAccountGlobalNum(savingsGlobalNum);
        paymentParams.setTransactionDateDD("13");
        paymentParams.setTransactionDateMM("03");
        paymentParams.setTransactionDateYYYY("2011");
        paymentParams.setSavingsAccountBalance(String.valueOf(SAVINGS_START_BALANCE));
        paymentParams.setSavingsAccountMaxWithdrawalAmount(String.valueOf(0));
        paymentParams.setSavingsAccountType("Voluntary");
        paymentParams.setSavingsAccountName("MonthlyClientSavingsAccount");
        
        ApplyPaymentConfirmationPage paymentConfirmationPage = applyPaymentPage
                .submitAndNavigateToApplyPaymentConfirmationPage(paymentParams);

        return paymentConfirmationPage.submitAndNavigateToLoanAccountDetailsPage();
    }

    private void verifyLoanAccount(LoanAccountPage loanAccountPage, String amountPaid) {
        loanAccountPage.verifyTotalAmountPaid(amountPaid);
        loanAccountPage.navigateToAccountActivityPage().verifyLastTotalPaid(amountPaid, 2);
    }

    private void verifySavingsAccount(String savingsGlobalNum, String expectedBalance) {
        SavingsAccountDetailPage savingsPage = navigationHelper.navigateToSavingsAccountDetailPage(savingsGlobalNum);
        savingsPage.verifySavingsAmount(expectedBalance);
    }
    
    public void testPermissionForPaymentFromSavings() {
        
        String loanGlobalNum = setUpLoanAccount();
        String savingsGlobalNum = setUpSavingsAccount();
        
        String errorMessage = "You do not have permission to make payment to the account using Savings Account Transfer";
        
        
        //first uncheck permission and see if access denied page is displayed
        //while trying transfer from savings
        ManageRolePage manageRolePage = navigationHelper.navigateToAdminPage().navigateToViewRolesPage().navigateToManageRolePage("Admin");
        
        if (manageRolePage.isPermissionEnable("5_1_11")) {
        	manageRolePage.disablePermission("5_1_11");
        }
        manageRolePage.submitAndGotoViewRolesPage();
        
        CreateLoanAccountSearchParameters loanSearchParams = new CreateLoanAccountSearchParameters();
        loanSearchParams.setSearchString(loanGlobalNum);
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanGlobalNum);
        
        ApplyPaymentPage applyPaymentPage = loanAccountPage.navigateToApplyPayment();
        PaymentParameters paymentParams = new PaymentParameters();
        paymentParams.setAmount(String.valueOf(TRANSFER_AMOUNT));
        paymentParams.setPaymentType(PaymentParameters.TRANSFER);
        paymentParams.setSavingsAccountGlobalNum(savingsGlobalNum);
        paymentParams.setTransactionDateDD("13");
        paymentParams.setTransactionDateMM("03");
        paymentParams.setTransactionDateYYYY("2011");
        paymentParams.setSavingsAccountBalance(String.valueOf(SAVINGS_START_BALANCE));
        paymentParams.setSavingsAccountMaxWithdrawalAmount(String.valueOf(0));
        paymentParams.setSavingsAccountType("Voluntary");
        paymentParams.setSavingsAccountName("MonthlyClientSavingsAccount");
        
        try {
            applyPaymentPage.submitAndNavigateToApplyPaymentConfirmationPage(paymentParams);
        } catch (AssertionError e) {
            Assert.assertTrue(selenium.isTextPresent(errorMessage));
        }
       
        
        //now enable permission and validate transfer success
        manageRolePage = navigationHelper.navigateToAdminPage().navigateToViewRolesPage().navigateToManageRolePage("Admin");
        if (!manageRolePage.isPermissionEnable("5_1_11")) {
        	manageRolePage.enablePermission("5_1_11");
        }
        manageRolePage.submitAndGotoViewRolesPage();
        
        navigationHelper.navigateToLoanAccountPage(loanGlobalNum)
        .navigateToApplyPayment()
        .submitAndNavigateToApplyPaymentConfirmationPage(paymentParams)
        .submitAndNavigateToLoanAccountDetailsPage();
        }
}
