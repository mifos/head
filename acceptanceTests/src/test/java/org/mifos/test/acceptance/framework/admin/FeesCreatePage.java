package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.AbstractPage;
import com.thoughtworks.selenium.Selenium;

public class FeesCreatePage extends AbstractPage {

    public FeesCreatePage submitPage() {
        return this;
    }

    public FeesCreatePage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("feescreate");

    }

    @SuppressWarnings("PMD.TooManyFields")
    // lots of fields ok for form input case
    public static class SubmitFormParameters {

        public static final int MONTHLY_FEE_RECURRENCE = 2;
        public static final int WEEKLY_FEE_RECURRENCE = 1;
        public static final int ONETIME_FEE_FREQUENCY = 2;
        public static final int PERIODIC_FEE_FREQUENCY = 1;
        public static final int USD = 1;
        public static final int INR = 2;
        public static final int EUR = 3;
        public static final String ALL_CUSTOMERS = "All Customers";
        public static final String UPFRONT = "Upfront";
        private String feeName;
        private String categoryType;
        private boolean defaultFees;
        private int feeFrequencyType;
        private String customerCharge;
        private int feeRecurrenceType;
        private int weekRecurAfter;
        private int monthRecurAfter;
        private double rate;
        private double amount;
        private String glCode;
        private String feeFormula;
        private int currency;
        private String calcuateFeeAs;
        private int calculateType;
        public static final String LOAN = "Loans";

        public String getFeeFormula() {
            return this.feeFormula;
        }

        public void setFeeFormula(String feeFormula) {
            this.feeFormula = feeFormula;
        }

        public String getFeeName() {
            return this.feeName;
        }

        public String getCategoryType() {
            return this.categoryType;
        }

        public boolean isDefaultFees() {
            return this.defaultFees;
        }

        public int getFeeFrequencyType() {
            return this.feeFrequencyType;
        }

        public String getCustomerCharge() {
            return this.customerCharge;
        }

        public double getAmount() {
            return this.amount;
        }

        public String getGlCode() {
            return this.glCode;
        }

        public void setFeeName(String feeName) {
            this.feeName = feeName;
        }

        public void setCategoryType(String categoryType) {
            this.categoryType = categoryType;
        }

        public void setDefaultFees(boolean defaultFees) {
            this.defaultFees = defaultFees;
        }

        public void setFeeFrequencyType(int feeFrequencyType) {
            this.feeFrequencyType = feeFrequencyType;
        }

        public void setCustomerCharge(String customerCharge) {
            this.customerCharge = customerCharge;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public void setGlCode(String glCode) {
            this.glCode = glCode;
        }

        public double getRate() {
            return this.rate;
        }

        public void setRate(double rate) {
            this.rate = rate;
        }

        public int getFeeRecurrenceType() {
            return this.feeRecurrenceType;
        }

        public void setFeeRecurrenceType(int feeRecurrenceType) {
            this.feeRecurrenceType = feeRecurrenceType;
        }

        public int getWeekRecurAfter() {
            return this.weekRecurAfter;
        }

        public void setWeekRecurAfter(int weekRecurAfter) {
            this.weekRecurAfter = weekRecurAfter;
        }

        public int getMonthRecurAfter() {
            return this.monthRecurAfter;
        }

        public void setMonthRecurAfter(int monthRecurAfter) {
            this.monthRecurAfter = monthRecurAfter;
        }

        public int getCurrency() {
            return this.currency;
        }

        public void setCurrency(int currency) {
            this.currency = currency;
        }

        public String getCalcuateFeeAs() {
            return this.calcuateFeeAs;
        }

        public void setCalcuateFeeAs(String calcuateFeeAs) {
            this.calcuateFeeAs = calcuateFeeAs;
        }

        public int getCalculateType() {
            return this.calculateType;
        }

        public void setCalculateType(int calculateType) {
            this.calculateType = calculateType;
        }
    }

    public FeesCreatePage fillFeesParameters(SubmitFormParameters parameters) {
        selenium.type("feescreate.input.feeName", parameters.getFeeName());
        selenium.select("feescreate.label.categoryType", "label=" + parameters.getCategoryType());
        selenium.click("//input[@id='feescreate.button.feeFrequencyType' and @value='"
                + parameters.getFeeFrequencyType() + "']");

        if (parameters.feeFrequencyType == parameters.PERIODIC_FEE_FREQUENCY) {
            selenium.click("//input[@id='feescreate.button.feeRecurrenceType' and @value='"
                    + parameters.getFeeRecurrenceType() + "']");
        }
        if (parameters.weekRecurAfter != 0) {
            selenium.type("feescreate.input.weekRecurAfter", Integer.toString(parameters.getWeekRecurAfter()));
        }
        if (parameters.monthRecurAfter != 0) {
            selenium.type("feescreate.input.monthRecurAfter", Integer.toString(parameters.getMonthRecurAfter()));
        }
        if (parameters.getCustomerCharge() != null) {
            if (parameters.getCategoryType().equals(FeesCreatePage.SubmitFormParameters.LOAN)) {
                selenium.select("loanCharge", "label=" + parameters.getCustomerCharge());
            } else {
                selenium.select("feescreate.label.customerCharge", "label=" + parameters.getCustomerCharge());
            }
        }

        if (parameters.getAmount() != 0) {
            selenium.type("feescreate.input.amount", Double.toString(parameters.getAmount()));
        }
        if (parameters.getFeeFormula() != null) {
            selenium.select("feescreate.label.feeFormula", parameters.getFeeFormula());
            selenium.type("feescreate.input.rate", Double.toString(parameters.getRate()));
        }
        if(parameters.getCategoryType().equals("Loans")) {
            fillLoanParameters(parameters);
        }

        selenium.select("feescreate.label.glCode", "label=" + parameters.getGlCode());
        return this;
    }

    private void fillLoanParameters(SubmitFormParameters parameters){
        if (parameters.getCurrency() != 0) {
            selenium.select("currencyId", "value=" + Integer.toString(parameters.getCurrency()));
        }
        if(parameters.getCalcuateFeeAs() != null){
            selenium.type("feescreate.input.rate", "label=" + parameters.getCalcuateFeeAs());
        }
        if(parameters.getCalcuateFeeAs() != null){
            selenium.select("feescreate.label.feeFormula", "label=" + Integer.toString(parameters.getCalculateType()));
        }
    }
    public PreviewFeesCreatePage submitPageAndGotoPreviewFeesCreatePage() {
        selenium.click("feescreate.button.preview");
        waitForPageToLoad();
        return new PreviewFeesCreatePage(selenium);
    }

}