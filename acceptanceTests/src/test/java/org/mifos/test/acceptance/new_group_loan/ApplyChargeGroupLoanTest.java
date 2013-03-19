package org.mifos.test.acceptance.new_group_loan;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.AccountActivityPage;
import org.mifos.test.acceptance.framework.loan.ChargeParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.GLIMClient;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.ViewRepaymentSchedulePage;
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(singleThreaded = true, enabled = true, groups = { "acceptance", "loan", "no_db_unit" })
public class ApplyChargeGroupLoanTest extends UiTestCaseBase {

    private LoanTestHelper loanTestHelper;
    private CustomPropertiesHelper customPropertiesHelper;

    @Override
    @BeforeMethod(alwaysRun = true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void setUp() throws Exception {
        super.setUp();
        loanTestHelper = new LoanTestHelper(selenium);
        customPropertiesHelper = new CustomPropertiesHelper(selenium);
        customPropertiesHelper.setNewGroupLoanWithMembers(true);
    }

    @AfterMethod
    public void logOut() {
        customPropertiesHelper.setNewGroupLoanWithMembers(false);
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void applyMiscPenalty() throws Exception {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2013, 02, 8, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Default Group");
        searchParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");

        List<GLIMClient> glimClients = new ArrayList<GLIMClient>();
        glimClients.add(new GLIMClient(0, "Stu1233266299995 Client1233266299995 Client Id: 0002-000000012", "1500", null));
        glimClients.add(new GLIMClient(2, "Stu1233266319760 Client1233266319760 Client Id: 0002-000000014", "1500", null));

        LoanAccountPage loanAccountPage = loanTestHelper.createGroupLoanAccount(searchParameters, glimClients);
        String loanId = loanAccountPage.getAccountId();

        String penaltyAmount = "100";
        List<String> individualPenaltyAmounts = new ArrayList<String>();
        individualPenaltyAmounts.add("20");
        individualPenaltyAmounts.add("80");

        ChargeParameters params = new ChargeParameters();
        params.setType(ChargeParameters.MISC_PENALTY);
        params.setAmount(penaltyAmount);
        params.setGroupLoanIndividualAmounts(individualPenaltyAmounts);

        loanAccountPage = loanTestHelper.applyChargeToGroupLoan(loanId, params);
        verifyPenaltySummaryAndActivity(loanAccountPage, penaltyAmount, "Misc penalty applied", 2);
        verifyRepaymentSchedulePage(loanAccountPage, penaltyAmount);

        for (int i = 0; i < glimClients.size(); i++) {
            LoanAccountPage individualLoanAccountPage = 
                    loanAccountPage.navigateToIndividualLoanAccountPageFromPendingApprovalGroupLoan(i);
            verifyPenaltySummaryAndActivity(individualLoanAccountPage, individualPenaltyAmounts.get(i),
                    "Misc penalty applied", 2);
            verifyRepaymentSchedulePage(individualLoanAccountPage, individualPenaltyAmounts.get(i));
            individualLoanAccountPage.navigateToGroupLoanPageFromIndividualLoanPage();
        }
    }

    private void verifyPenaltySummaryAndActivity(LoanAccountPage loanAccountPage, String penalty, String activity, int row) {
        Assert.assertEquals(loanAccountPage.getPenaltyPaid(), "0");
        Assert.assertEquals(loanAccountPage.getPenaltyBalance(), penalty);
        AccountActivityPage accountActivityPage = loanAccountPage.navigateToAccountActivityPage();
        Assert.assertEquals(accountActivityPage.getLastPenalty(row), penalty);
        Assert.assertEquals(accountActivityPage.getActivity(row), activity);
        loanAccountPage.navigateBack();
    }

    private void verifyRepaymentSchedulePage(LoanAccountPage loanAccountPage, String penalty) {
        ViewRepaymentSchedulePage repaymentSchedulePage = loanAccountPage.navigateToRepaymentSchedulePage();
        repaymentSchedulePage.verifyRepaymentScheduleTablePenalties(3, penalty);
        loanAccountPage.navigateBack();
    }

}
