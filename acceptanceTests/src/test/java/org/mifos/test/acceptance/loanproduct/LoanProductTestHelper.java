package org.mifos.test.acceptance.loanproduct;

import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductConfirmationPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
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

    public DefineNewLoanProductPage navigateToDefineNewLoanPangAndFillMandatoryFields(
            DefineNewLoanProductPage.SubmitFormParameters formParameters) {
        formParameters.setOfferingShortName(StringUtil.getRandomString(4));
        return navigateToDefineNewLoanProductPage().fillLoanParameters(formParameters);
    }

    public DefineNewLoanProductConfirmationPage defineNewLoanProduct(
            DefineNewLoanProductPage.SubmitFormParameters formParameters) {
        return navigateToDefineNewLoanProductPage().fillLoanParameters(formParameters)
                .submitAndGotoNewLoanProductPreviewPage().submit();
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
}
