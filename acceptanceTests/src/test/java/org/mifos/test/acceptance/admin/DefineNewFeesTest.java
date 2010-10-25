package org.mifos.test.acceptance.admin;

import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.FeesCreatePage;
import org.mifos.test.acceptance.framework.admin.FeesCreatePage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"smoke","fees","acceptance"})
public class DefineNewFeesTest extends UiTestCaseBase{

    private AppLauncher appLauncher;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void createOneTimeFees(){
        SubmitFormParameters formParameters = FormParametersHelper.getCreateOneTimeFeesParameters();
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();
        adminPage.defineNewFees(formParameters);
    }

    public static FeesCreatePage.SubmitFormParameters getCreateOneTimeFeesParameters(){
         FeesCreatePage.SubmitFormParameters formParameters =  new FeesCreatePage.SubmitFormParameters();
         formParameters.setFeeName(StringUtil.getRandomString(5));
         formParameters.setCategoryType("All Customers");
         formParameters.setDefaultFees(false);
         formParameters.setFeeFrequencyType(formParameters.ONETIME_FEE_FREQUENCY);
         formParameters.setCustomerCharge("Upfront");
         formParameters.setAmount(20);
         formParameters.setGlCode(31301);
         return formParameters;
     }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void createPeriodicFees() throws Exception {
        SubmitFormParameters formParameters = FormParametersHelper.getCreatePeriodicFeesParameters();
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();
        adminPage.defineNewFees(formParameters);
    }

    private AdminPage loginAndNavigateToAdminPage() {
        return appLauncher
         .launchMifos()
         .loginSuccessfullyUsingDefaultCredentials()
         .navigateToAdminPage();
     }

}
