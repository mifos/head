package org.mifos.test.acceptance.admin;

import org.mifos.test.acceptance.framework.admin.FeesCreatePage;
import org.mifos.test.acceptance.util.TestDataSetup;

import java.sql.SQLException;

public class FeeTestHelper {
    private final TestDataSetup dataSetup;

    public FeeTestHelper(TestDataSetup dataSetup) {
        this.dataSetup = dataSetup;
    }

    public String createFixedFee(String feeName, String feeType, String payWhen, int rateAmount, String baseRate) throws SQLException {
        FeesCreatePage.SubmitFormParameters feeParameters = new FeesCreatePage.SubmitFormParameters();
        feeParameters.setFeeName(feeName);
        feeParameters.setCategoryType(feeType);
        feeParameters.setFeeFrequencyType(FeesCreatePage.SubmitFormParameters.ONETIME_FEE_FREQUENCY);
        feeParameters.setCustomerCharge(payWhen);
        feeParameters.setRate(rateAmount);
        feeParameters.setFeeFormula(baseRate);
        feeParameters.setGlCode(31301);
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
        feeParameters.setGlCode(31301);
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
        feeParameters.setGlCode(31301);
        dataSetup.createFee(feeParameters);
        return feeName;
    }
}