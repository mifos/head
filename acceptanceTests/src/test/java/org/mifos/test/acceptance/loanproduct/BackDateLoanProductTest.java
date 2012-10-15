package org.mifos.test.acceptance.loanproduct;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPreviewPage;
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loanproduct", "acceptance", "no_db_unit"})
public class BackDateLoanProductTest extends UiTestCaseBase {
    
    private CustomPropertiesHelper propertiesHelper;
            
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") 
    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        propertiesHelper = new CustomPropertiesHelper(selenium);
    }
    
    private DefineNewLoanProductPage preparePageForTest() {
        NavigationHelper navigationHelper = new NavigationHelper(selenium);
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        DefineNewLoanProductPage newLoanProductPage = adminPage.navigateToDefineLoanProduct();
        SubmitFormParameters parameters = 
                FormParametersHelper.getMonthlyLoanProductParameters();
        return newLoanProductPage.fillLoanParameters(parameters);
    }

    @Test(enabled=true)
    public void cannotCreateLoanProductInPastWhenBackDatingIsDisabled() {
        propertiesHelper.setBackDatedLoanProductCreationAllowed(false);
        DefineNewLoanProductPage newLoanProductPage = preparePageForTest();
        newLoanProductPage.fillStartDate("1111", "11", "11");
        newLoanProductPage = newLoanProductPage.submitWithErrors();

        Assert.isTrue(newLoanProductPage.getSelenium().isTextPresent(
                "The Start date can be anything between current date and 1 year" +
                " from the current date."));
    }
    
    @Test(enabled=true)
    public void canCreateLoanProductInPastWhenBackDatingIsEnabled() {
        propertiesHelper.setBackDatedLoanProductCreationAllowed(true);
        DefineNewLoanProductPage newLoanProductPage = preparePageForTest();
        newLoanProductPage.fillStartDate("1111", "11", "11");
        DefineNewLoanProductPreviewPage previewPage = newLoanProductPage
                .submitAndGotoNewLoanProductPreviewPage();
        previewPage.submit();
    }
    
    @Test(enabled=true)
    public void cannotCreateLoanProductMoreThanOneYearInFutureWhenBackDatingIsEnabled() {
        propertiesHelper.setBackDatedLoanProductCreationAllowed(true);
        DefineNewLoanProductPage newLoanProductPage = preparePageForTest();
        newLoanProductPage.fillStartDate("9999", "11", "11");
        newLoanProductPage = newLoanProductPage.submitWithErrors();

        Assert.isTrue(newLoanProductPage.getSelenium().isTextPresent(
                "The Start date can be anything up to 1 year" +
                " from the current date."));
    }
    
    @AfterMethod
    public void logOut() {
       (new MifosPage(selenium)).logout();
    }
}
