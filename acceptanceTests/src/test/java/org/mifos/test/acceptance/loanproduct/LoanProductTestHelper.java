package org.mifos.test.acceptance.loanproduct;

import java.util.Arrays;

import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductConfirmationPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.EditLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.EditLoanProductPreviewPage;
import org.mifos.test.acceptance.framework.loanproduct.LoanProductDetailsPage;
import org.mifos.test.acceptance.framework.loanproduct.ViewLoanProductsPage;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.util.StringUtil;

import com.thoughtworks.selenium.Selenium;

public class LoanProductTestHelper {

    private final Selenium selenium;
    private final NavigationHelper navigationHelper;

    public LoanProductTestHelper(Selenium selenium) {
        this.selenium = selenium;
        this.navigationHelper = new NavigationHelper(selenium);
    }

    public DefineNewLoanProductPage navigateToDefineNewLoanPageAndFillMandatoryFields(
            DefineNewLoanProductPage.SubmitFormParameters formParameters) {
        formParameters.setOfferingShortName(StringUtil.getRandomString(4));
        return navigateToDefineNewLoanProductPage().fillLoanParameters(formParameters);
    }

    public DefineNewLoanProductConfirmationPage defineNewLoanProduct(
            DefineNewLoanProductPage.SubmitFormParameters formParameters, String... fees) {
        DefineNewLoanProductPage productPage = navigateToDefineNewLoanProductPage().fillLoanParameters(formParameters);
        if (fees != null) {
            for (String feeName : fees) {
                productPage.addFee(feeName);
            }
        }
        return productPage.submitAndGotoNewLoanProductPreviewPage().submit();
    }

    public DefineNewLoanProductPage navigateToDefineNewLoanProductPage() {
        return new NavigationHelper(selenium).navigateToAdminPage().navigateToDefineLoanProduct();
    }

    public DefineNewLoanProductPage.SubmitFormParameters defineLoanProductParameters(int defInstallments,
            int defaultLoanAmount, int defaultInterestRate, int interestType) {
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper
                .getWeeklyLoanProductParameters();
        formParameters.setDefInstallments(String.valueOf(defInstallments));
        formParameters.setDefaultLoanAmount(String.valueOf(defaultLoanAmount));
        formParameters.setInterestTypes(interestType);
        formParameters.setDefaultInterestRate(String.valueOf(defaultInterestRate));
        return formParameters;
    }

    private String getLastLoanAmountRangeAtRow(Integer row) {
        return selenium.getTable("noOfInstallFromLastTable." + row + ".0");
    }

    private String getDefaultOfInstallments(Integer row) {
        return selenium.getTable("noOfInstallFromLastTable." + row + ".3");
    }

    public String[] getDefaultNoOfInstallmentsForClients(String[] clients, String productName) {
        Float clientLastAmount;
        navigationHelper.navigateToLoanProductDetailsPage(productName);
        Integer rowCount = (selenium.getXpathCount("//table[@id='noOfInstallFromLastTable']/tbody/tr").intValue() - 1);
        Float actualRowValue;
        String[] clientsInstallments = new String[clients.length];
        int j = 0;
        for (String client : clients) {
            clientLastAmount = Float.parseFloat(navigationHelper.navigateToClientViewDetailsPage(client)
                    .getLastLoanAmount());
            navigationHelper.navigateToLoanProductDetailsPage(productName);
            for (int i = 1; i < rowCount; i++) {
                actualRowValue = Float.parseFloat(getLastLoanAmountRangeAtRow(i).split("-")[1]);
                if (clientLastAmount < actualRowValue) {
                    clientsInstallments[j] = getDefaultOfInstallments(i);
                    break;
                }
            }
            j++;
        }
        return clientsInstallments;
    }
    
    public void editLoanProductIncludeInLoanCounter(String loanProduct, boolean includeInLoanCounter) {
        EditLoanProductPage editLoanProductPage = navigationHelper.navigateToAdminPage().
                navigateToViewLoanProducts().
                viewLoanProductDetails(loanProduct).
                editLoanProduct();
        DefineNewLoanProductPage.SubmitFormParameters formParameters = new DefineNewLoanProductPage.SubmitFormParameters();
        formParameters.setIncludeInLoanCounter(includeInLoanCounter);
        editLoanProductPage.submitIncludeInLoanCounter(formParameters).submit();
    }

    public void editLoanProductIncludeQuestionsGroups(String loanProduct, String... questionGroup) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ViewLoanProductsPage viewLoanProducts = adminPage.navigateToViewLoanProducts();
        LoanProductDetailsPage loanProductDetailsPage = viewLoanProducts.viewLoanProductDetails(loanProduct);
        EditLoanProductPage editLoanProductPage = loanProductDetailsPage.editLoanProduct();
        DefineNewLoanProductPage.SubmitFormParameters formParameters = new DefineNewLoanProductPage.SubmitFormParameters();
        formParameters.setQuestionGroups(Arrays.asList(questionGroup));
        EditLoanProductPreviewPage editLoanProductPreviewPage = editLoanProductPage.submitQuestionGroupChanges(formParameters);
        editLoanProductPreviewPage.submit();
    }
    
    public EditLoanProductPreviewPage editLoanProductWithParameters(String loanProduct, 
            DefineNewLoanProductPage.SubmitFormParameters productParams) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        adminPage.navigateToViewLoanProducts().viewLoanProductDetails(loanProduct);
        new DefineNewLoanProductPage().fillLoanParameters(productParams).submitPage();
        return new EditLoanProductPreviewPage(selenium);
    }
    
    public LoanProductDetailsPage navigateToViewLoanProductDetailsPage(String loanProduct) {
    	return navigationHelper.navigateToAdminPage().navigateToViewLoanProducts().viewLoanProductDetails(loanProduct);
    }
    
    public LoanProductDetailsPage defineNewLoanProduct(DefineNewLoanProductPage.SubmitFormParameters productParams) {
        DefineNewLoanProductPage defineNewLoanProductPage = navigationHelper
            .navigateToAdminPage()
            .navigateToDefineLoanProduct();
        defineNewLoanProductPage.fillLoanParameters(productParams);
        return defineNewLoanProductPage
            .submitAndGotoNewLoanProductPreviewPage()
            .submit()
            .navigateToViewLoanDetailsPage();
    }
    
    public void enableInterestWaiver(String loanProduct, boolean interestWaiver) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ViewLoanProductsPage viewLoanProducts = adminPage.navigateToViewLoanProducts();
        LoanProductDetailsPage loanProductDetailsPage = viewLoanProducts.viewLoanProductDetails(loanProduct);
        EditLoanProductPage editLoanProductPage = loanProductDetailsPage.editLoanProduct();
        DefineNewLoanProductPage.SubmitFormParameters formParameters = new DefineNewLoanProductPage.SubmitFormParameters();
        formParameters.setInterestWaiver(interestWaiver);
        EditLoanProductPreviewPage editLoanProductPreviewPage = editLoanProductPage.submitInterestWaiverChanges(formParameters);
        editLoanProductPreviewPage.submit();
    }
}
