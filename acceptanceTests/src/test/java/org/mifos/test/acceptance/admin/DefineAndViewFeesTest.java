package org.mifos.test.acceptance.admin;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.FeesCreatePage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"smoke", "fees", "acceptance", "no_db_unit"})
public class DefineAndViewFeesTest extends UiTestCaseBase {

    private FeeTestHelper feeTestHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        feeTestHelper = new FeeTestHelper(null,new NavigationHelper(selenium));
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyViewFeesTableContentsTest() throws Exception {
        defineFee("ClientFee", "All Customers");
        defineFee("ProductFee", "Loans");
        feeTestHelper.viewClientFees("ClientFee");
        feeTestHelper.viewProductFees("ProductFee");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void createPeriodicFees() throws Exception {
        SubmitFormParameters feeParameters = feeTestHelper.getFeeParameters(StringUtil.getRandomString(5), "Group", false, SubmitFormParameters.PERIODIC_FEE_FREQUENCY, 6, "6201 - Miscelleneous Income");
        feeParameters.setFeeFrequencyType(feeParameters.PERIODIC_FEE_FREQUENCY);
        feeParameters.setFeeRecurrenceType(feeParameters.MONTHLY_FEE_RECURRENCE);
        feeParameters.setMonthRecurAfter(2);
        feeTestHelper.defineFees(feeParameters);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void createOneTimeFees() {
        SubmitFormParameters feeParameters = feeTestHelper.getFeeParameters(StringUtil.getRandomString(5), "All Customers", false, SubmitFormParameters.ONETIME_FEE_FREQUENCY, 20, "31301 - Fees");
        feeParameters.setCustomerCharge("Upfront");
        feeTestHelper.defineFees(feeParameters);
    }

    private void defineFee(String feeName, String categoryType) {
        SubmitFormParameters feeParameters = feeTestHelper.getFeeParameters(feeName, categoryType, false, SubmitFormParameters.ONETIME_FEE_FREQUENCY, 20, "31301 - Fees");
        feeParameters.setCustomerCharge("Upfront");
        feeTestHelper.defineFees(feeParameters);
    }

}
