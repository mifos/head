package org.mifos.test.acceptance.admin;

import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.ImportLoansReviewPage;
import org.mifos.test.acceptance.framework.admin.ImportLoansSaveSummaryPage;
import org.mifos.test.acceptance.framework.admin.ImportSavingsReviewPage;
import org.mifos.test.acceptance.framework.admin.ImportSavingsSaveSummaryPage;
import org.mifos.test.acceptance.framework.savingsproduct.SavingsProductParameters;
import org.mifos.test.acceptance.framework.testhelpers.AdminTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.ClientTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.GroupTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.SavingsProductHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(singleThreaded = true, groups = {"admin","acceptance","ui","no_db_unit"})
public class SavingsImportTest extends UiTestCaseBase {
    NavigationHelper navigationHelper;
    ClientTestHelper clientTestHelper;
    GroupTestHelper groupTestHelper;
    SavingsProductHelper savingsProductHelper;
    AdminTestHelper adminTestHelper;
    String[] arrayOfErrors;
    
    @Override
    @BeforeMethod(alwaysRun = true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void setUp() throws Exception {
        super.setUp();
        navigationHelper=new NavigationHelper(selenium);
        clientTestHelper=new ClientTestHelper(selenium);
        groupTestHelper=new GroupTestHelper(selenium);
        savingsProductHelper=new SavingsProductHelper(selenium);
        adminTestHelper=new AdminTestHelper(selenium);

    }
    
    @AfterMethod(alwaysRun = true)
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }
    
    @Test(enabled=true)
    public void importSavingAccountsToClientTest(){
        String succesNumber="1";
        String errorNumber="27";
        SavingsProductParameters parameters = savingsProductHelper.getGenericSavingsProductParameters(new DateTime(), SavingsProductParameters.VOLUNTARY, SavingsProductParameters.CLIENTS);
        parameters.setProductInstanceName("importSavings");
        parameters.setShortName("IMP");
        try {
            savingsProductHelper.createSavingsProduct(parameters);
        } catch (AssertionError e){
            Logger.getAnonymousLogger().info("Product exists");
        }
        String importFile=this.getClass().getResource("/ImportSavingsAccountsTest.xls").toString();
        ImportSavingsReviewPage reviewPage=adminTestHelper.loadImportSavingsFileAndSubmitForReview(importFile);
        reviewPage.validateErrors(arrayOfErrors);
        reviewPage.validateSuccesText(succesNumber);
        ImportSavingsSaveSummaryPage summaryPage= reviewPage.saveSuccessfullRows();
        summaryPage.verifySuccesString(succesNumber);
        summaryPage.verifyErrorStroing(errorNumber);
    }
}
