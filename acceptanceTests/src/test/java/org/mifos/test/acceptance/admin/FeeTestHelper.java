package org.mifos.test.acceptance.admin;

import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.FeesCreatePage;
import org.mifos.test.acceptance.framework.admin.ViewFeesPage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.util.TestDataSetup;

import java.sql.SQLException;

public class FeeTestHelper {
    private final TestDataSetup dataSetup;
    private final NavigationHelper navigationHelper;
    
    public FeeTestHelper(TestDataSetup dataSetup, NavigationHelper navigationHelper) {
        this.dataSetup = dataSetup;
        this.navigationHelper = navigationHelper;
    }

    public String createFixedFee(String feeName, String feeType, String payWhen, int rateAmount, String baseRate) throws SQLException {
        FeesCreatePage.SubmitFormParameters feeParameters = new FeesCreatePage.SubmitFormParameters();
        feeParameters.setFeeName(feeName);
        feeParameters.setCategoryType(feeType);
        feeParameters.setFeeFrequencyType(FeesCreatePage.SubmitFormParameters.ONETIME_FEE_FREQUENCY);
        feeParameters.setCustomerCharge(payWhen);
        feeParameters.setRate(rateAmount);
        feeParameters.setFeeFormula(baseRate);
        feeParameters.setGlCode("31301 -Fees");
        dataSetup.createFee(feeParameters);
        return feeName;
    }

    public String createNoRateFee(String feeName, String feeType, String payWhen, int amount) throws SQLException {
        FeesCreatePage.SubmitFormParameters feeParameters = new FeesCreatePage.SubmitFormParameters();
        feeParameters.setFeeName(feeName);
        feeParameters.setCategoryType(feeType);
        feeParameters.setFeeFrequencyType(FeesCreatePage.SubmitFormParameters.ONETIME_FEE_FREQUENCY);
        feeParameters.setCustomerCharge(payWhen);
        feeParameters.setAmount(amount);
        feeParameters.setFeeFormula(null);
        feeParameters.setGlCode("31301 - Fees");
        dataSetup.createFee(feeParameters);
        return feeName;
    }

    public String createPeriodicFee(String feeName, String feeType, int recurrenceType, int recurrenceInterval, int amount) throws SQLException {
        FeesCreatePage.SubmitFormParameters feeParameters = new FeesCreatePage.SubmitFormParameters();
        feeParameters.setFeeRecurrenceType(recurrenceType);
        feeParameters.setFeeName(feeName);
        feeParameters.setFeeFrequencyType(FeesCreatePage.SubmitFormParameters.PERIODIC_FEE_FREQUENCY);
        feeParameters.setAmount(amount);
        feeParameters.setCategoryType(feeType);
        feeParameters.setWeekRecurAfter(recurrenceInterval);
        feeParameters.setGlCode("31301 - Fees");
        dataSetup.createFee(feeParameters);
        return feeName;
    }    
	
    public String createPeriodicRateFee(String feeName, String feeType, int recurrenceType, int recurrenceInterval, double rate, String feeFormula) throws SQLException {
        FeesCreatePage.SubmitFormParameters feeParameters = new FeesCreatePage.SubmitFormParameters();
        feeParameters.setFeeRecurrenceType(recurrenceType);
        feeParameters.setFeeName(feeName);
        feeParameters.setFeeFrequencyType(FeesCreatePage.SubmitFormParameters.PERIODIC_FEE_FREQUENCY);
        feeParameters.setRate(rate);
        feeParameters.setFeeFormula(feeFormula);
        feeParameters.setCategoryType(feeType);
        feeParameters.setWeekRecurAfter(recurrenceInterval);
        feeParameters.setGlCode("31301 - Fees");
        dataSetup.createFee(feeParameters);
        return feeName;
    }
    
    
    
    public void defineFees(FeesCreatePage.SubmitFormParameters feeParameters) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        adminPage.defineNewFees(feeParameters);
    }

    public FeesCreatePage.SubmitFormParameters getFeeParameters(String feeName, String categoryType, boolean defaultFees,
                                                                int frequencyType, int amount, String glCode) {
        FeesCreatePage.SubmitFormParameters formParameters = new FeesCreatePage.SubmitFormParameters();
        formParameters.setFeeName(feeName);
        formParameters.setCategoryType(categoryType);
        formParameters.setDefaultFees(defaultFees);
        formParameters.setFeeFrequencyType(frequencyType);
        formParameters.setAmount(amount);
        formParameters.setGlCode(glCode);
        return formParameters;
    }

    public void viewClientFees(String expectedClientFees) {
        ViewFeesPage viewFeesPage = navigateToViewFeesPage();
        viewFeesPage.verifyClientFees(expectedClientFees);
    }

    public void viewProductFees(String expectedProductFees) {
        ViewFeesPage viewFeesPage = navigateToViewFeesPage();
        viewFeesPage.verifyProductFees(expectedProductFees);
    }

    private ViewFeesPage navigateToViewFeesPage() {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        return adminPage.navigateToViewFeesPage();
    }
}