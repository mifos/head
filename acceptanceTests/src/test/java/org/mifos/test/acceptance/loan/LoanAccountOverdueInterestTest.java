package org.mifos.test.acceptance.loan;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.PaymentParameters;
import org.mifos.test.acceptance.framework.loan.ViewRepaymentSchedulePage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.loanproduct.LoanProductTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loan", "acceptance", "ui", "no_db_unit"})
public class LoanAccountOverdueInterestTest extends UiTestCaseBase {
    
    private DateTime systemDateTime;
    private LoanTestHelper loanTestHelper;
    private LoanProductTestHelper loanProductTestHelper;
    private NavigationHelper navigationHelper;
    
    CustomPropertiesHelper propertiesHelper;
    
    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        propertiesHelper = new CustomPropertiesHelper(selenium);
        propertiesHelper.setOverdueInterestPaidFirst("true"); 
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        systemDateTime = new DateTime(2011, 3, 4, 12, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(systemDateTime);
        loanTestHelper = new LoanTestHelper(selenium);
        navigationHelper = new NavigationHelper(selenium);
        loanProductTestHelper = new LoanProductTestHelper(selenium);
        
        
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
        propertiesHelper.setOverdueInterestPaidFirst("false");
    }
    
    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyPayOverdueInstalment() throws Exception {
        SubmitFormParameters loanParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        loanParameters.setOfferingName("OverdueLoan");
        loanProductTestHelper.defineNewLoanProduct(loanParameters);
        
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Stu");
        searchParameters.setLoanProduct("OverdueLoan");
        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1012.0");
        LoanAccountPage loanPage = loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters);
        String loanId = loanPage.getAccountId();
        loanPage.changeAccountStatusToAccepted();
        DisburseLoanParameters disburseParameters = new DisburseLoanParameters();
        disburseParameters.setDisbursalDateDD(Integer.toString(systemDateTime.getDayOfMonth()));
        disburseParameters.setDisbursalDateMM(Integer.toString(systemDateTime.getMonthOfYear()));
        disburseParameters.setDisbursalDateYYYY(Integer.toString(systemDateTime.getYear()));
        disburseParameters.setPaymentType(PaymentParameters.CASH);
        loanTestHelper.disburseLoan(loanId, disburseParameters);
        
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        systemDateTime = systemDateTime.plusDays(20);
        dateTimeUpdaterRemoteTestingService.setDateTime(systemDateTime);
        loanPage = navigationHelper.navigateToLoanAccountPage(loanId);
        ViewRepaymentSchedulePage repaymentPage = loanPage.navigateToRepaymentSchedulePage();
        repaymentPage.verifyRepaymentScheduleTablePrincipal(3, 3, "17.3");
        repaymentPage.verifyRepaymentScheduleTableInterest(3, 4, "3.7 (0)");
        repaymentPage.verifyRepaymentScheduleTablePrincipal(4, 3, "17.4");
        repaymentPage.verifyRepaymentScheduleTableInterest(4, 4, "3.6 (0)");
        PaymentParameters params = new PaymentParameters();
        params.setTransactionDateDD(Integer.toString(systemDateTime.getDayOfMonth()));
        params.setTransactionDateMM(Integer.toString(systemDateTime.getMonthOfYear()));
        params.setTransactionDateYYYY(Integer.toString(systemDateTime.getYear()));
        params.setPaymentType(PaymentParameters.CASH);
        params.setAmount("4.2");
        loanPage = repaymentPage.navigateToApplyPaymentPage().submitAndNavigateToApplyPaymentConfirmationPage(params).submitAndNavigateToLoanAccountDetailsPage();
        loanPage = navigationHelper.navigateToLoanAccountPage(loanId);
        repaymentPage = loanPage.navigateToRepaymentSchedulePage();
        repaymentPage.verifyRepaymentScheduleTableAfterPayPrincipal(3, 3, "0");
        repaymentPage.verifyRepaymentScheduleTableAfterPayInterest(3, 4, "3.7");
        repaymentPage.verifyRepaymentScheduleTableAfterPayPrincipal(5, 3, "17.3");
        repaymentPage.verifyRepaymentScheduleTableAfterPayInterest(5, 4, "0 (3.7)");
        repaymentPage.verifyRepaymentScheduleTableAfterPayPrincipal(6, 3, "17.4");
        repaymentPage.verifyRepaymentScheduleTableAfterPayInterest(6, 4, "3.1 (0.5)");
    }
}
