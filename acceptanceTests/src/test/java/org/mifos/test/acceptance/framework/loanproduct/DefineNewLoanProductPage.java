/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.test.acceptance.framework.loanproduct;

import org.mifos.test.acceptance.framework.AbstractPage;

import com.thoughtworks.selenium.Selenium;
import org.testng.Assert;

public class DefineNewLoanProductPage extends AbstractPage {

    String configureVariableInstalmentsCheckbox = "canConfigureVariableInstallments";
    String minInstalmentGapTextBox = "minimumGapBetweenInstallments";
    String maxInstalmentGapTextBox = "maximumGapBetweenInstallments";
    String previewButton = "createLoanProduct.button.preview";
    String minInstalmentAmountTextBox = "minimumInstallmentAmount";

    public DefineNewLoanProductPage() {
        super();
    }

    public DefineNewLoanProductPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
          this.verifyPage("CreateLoanProduct");

    }

    public DefineNewLoanProductPage submitPage() {
        return this;
    }

    @SuppressWarnings("PMD.TooManyFields") // lots of fields ok for form input case
   public static class SubmitFormParameters {

        // interest types
        public static final int FLAT = 1;

        public static final int DECLINING_BALANCE = 2;

        public static final int DECLINING_BALANCE_EPI = 4;

        // applicable for
        public static final int CLIENTS = 1;
        public static final int GROUPS = 2;
        // freq of installments
        public static final int WEEKS = 1;
        public static final int MONTHS = 2;

        // grace period type
        public static final int NONE = 1;
        private String branch;

        private String offeringName;
        private String offeringShortName;

        private String description;

        private String category;
        private int applicableFor;
        private String minLoanAmount;
        private String maxLoanAmount;
        private String defaultLoanAmount;
        private int interestTypes;
        private String minInterestRate;
        private String maxInterestRate;
        private String defaultInterestRate;
        private int freqOfInstallments;
        private String defInstallments;
        private String maxInstallments;
        private int gracePeriodType;
        private String interestGLCode;
        private String principalGLCode;
        private boolean interestWaiver;
        public String getBranch() {
            return this.branch;
        }
        public void setBranch(String branch) {
            this.branch = branch;
        }
        public String getOfferingName() {
            return this.offeringName;
        }
        public void setOfferingName(String offeringName) {
            this.offeringName = offeringName;
        }


        public String getOfferingShortName() {
            return this.offeringShortName;
        }

        public void setOfferingShortName(String offeringShortName) {
            this.offeringShortName = offeringShortName;
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCategory() {
            return this.category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public int getApplicableFor() {
            return this.applicableFor;
        }

        public void setApplicableFor(int applicableFor) {
            this.applicableFor = applicableFor;
        }

        public String getMinLoanAmount() {
            return this.minLoanAmount;
        }

        public void setMinLoanAmount(String minLoanAmount) {
            this.minLoanAmount = minLoanAmount;
        }

        public String getMaxLoanAmount() {
            return this.maxLoanAmount;
        }

        public void setMaxLoanAmount(String maxLoanAmount) {
            this.maxLoanAmount = maxLoanAmount;
        }

        public String getDefaultLoanAmount() {
            return this.defaultLoanAmount;
        }

        public void setDefaultLoanAmount(String defaultLoanAmount) {
            this.defaultLoanAmount = defaultLoanAmount;
        }

        public int getInterestTypes() {
            return this.interestTypes;
        }

        public void setInterestTypes(int interestTypes) {
            this.interestTypes = interestTypes;
        }

        public String getMinInterestRate() {
            return this.minInterestRate;
        }

        public void setMinInterestRate(String minInterestRate) {
            this.minInterestRate = minInterestRate;
        }

        public String getMaxInterestRate() {
            return this.maxInterestRate;
        }

        public void setMaxInterestRate(String maxInterestRate) {
            this.maxInterestRate = maxInterestRate;
        }

        public String getDefaultInterestRate() {
            return this.defaultInterestRate;
        }

        public void setDefaultInterestRate(String defaultInterestRate) {
            this.defaultInterestRate = defaultInterestRate;
        }

        public String getDefInstallments() {
            return this.defInstallments;
        }

        public void setFreqOfInstallments(int freqOfInstallments) {
            this.freqOfInstallments = freqOfInstallments;
        }

        public int getFreqOfInstallments() {
            return freqOfInstallments;
        }

        public void setDefInstallments(String defInstallments) {
            this.defInstallments = defInstallments;
        }

        public String getMaxInstallments() {
            return this.maxInstallments;
        }

        public void setMaxInstallments(String maxInstallments) {
            this.maxInstallments = maxInstallments;
        }

        public int getGracePeriodType() {
            return this.gracePeriodType;
        }

        public void setGracePeriodType(int gracePeriodType) {
            this.gracePeriodType = gracePeriodType;
        }

        public String getInterestGLCode() {
            return this.interestGLCode;
        }

        public void setInterestGLCode(String interestGLCode) {
            this.interestGLCode = interestGLCode;
        }

        public String getPrincipalGLCode() {
            return this.principalGLCode;
        }

        public void setPrincipalGLCode(String principalGLCode) {
            this.principalGLCode = principalGLCode;
        }

       public void setInterestWaiver(boolean interestWaiver) {
           this.interestWaiver = interestWaiver;
       }

       public boolean isInterestWaiver() {
           return interestWaiver;
       }

    }

    public void fillLoanParameters(SubmitFormParameters parameters) {
        selenium.type("createLoanProduct.input.prdOffering", parameters.getOfferingName());
        selenium.type("createLoanProduct.input.prdOfferingShortName", parameters.getOfferingShortName());
        selenium.type("createLoanProduct.input.description", parameters.getDescription());
        selenium.select("prdCategory", "label=" + parameters.getCategory());
        selenium.select("prdApplicableMaster", "value=" + parameters.getApplicableFor());
        selenium.type("minLoanAmount", parameters.getMinLoanAmount());
        selenium.type("maxLoanAmount", parameters.getMaxLoanAmount());
        selenium.type("defaultLoanAmount", parameters.getDefaultLoanAmount());
        selenium.select("interestTypes", "value=" + parameters.getInterestTypes());
        selenium.type("createLoanProduct.input.maxInterestRate", parameters.getMaxInterestRate());
        selenium.type("createLoanProduct.input.minInterestRate", parameters.getMinInterestRate() );
        selenium.type("createLoanProduct.input.defInterestRate", parameters.getDefaultInterestRate());
        selenium.click("name=freqOfInstallments value=" + parameters.getFreqOfInstallments());
        selenium.type("maxNoInstallments", parameters.getMaxInstallments());
        selenium.type("defNoInstallments", parameters.getDefInstallments());
        selenium.select("gracePeriodType", "value=" + parameters.getGracePeriodType());
        selenium.select("interestGLCode", "label=" + parameters.getInterestGLCode());
        selenium.select("principalGLCode", "label=" + parameters.getPrincipalGLCode());
    }

    public DefineNewLoanProductPreviewPage submitAndGotoNewLoanProductPreviewPage() {
        selenium.click(previewButton);
        waitForPageToLoad();
        return new DefineNewLoanProductPreviewPage(selenium);
    }

    public void verifyVariableInstalmentOptionsDefaults() {

        Assert.assertTrue(!selenium.isChecked(configureVariableInstalmentsCheckbox)
                & !selenium.isVisible(maxInstalmentGapTextBox)
                & !selenium.isVisible(minInstalmentGapTextBox)
                & !selenium.isVisible(minInstalmentAmountTextBox));
        selectVariableInstalmentAndWaitForLoad();
        Assert.assertTrue(selenium.isVisible(maxInstalmentGapTextBox) & selenium.isEditable(maxInstalmentGapTextBox));
        Assert.assertTrue(selenium.isVisible(minInstalmentGapTextBox) & selenium.isEditable(minInstalmentGapTextBox));
        Assert.assertTrue(selenium.isVisible(minInstalmentAmountTextBox) & selenium.isEditable(minInstalmentAmountTextBox));
        Assert.assertTrue(selenium.getValue(minInstalmentGapTextBox).equals("1"));
        Assert.assertTrue(selenium.getValue(maxInstalmentGapTextBox).equals(""));
        Assert.assertTrue(selenium.getValue(minInstalmentAmountTextBox).equals(""));
    }

    public void verifyVariableInstalmentOptionsFields() {
        fillInstalmentOptionsAndSubmit("text,", "text,", "text,");
        Assert.assertTrue(selenium.isTextPresent("The min installment amount for variable installments is invalid because only numbers or decimal separator are allowed"));
        Assert.assertTrue(!selenium.getValue(maxInstalmentGapTextBox).contains("text") & !selenium.getValue(maxInstalmentGapTextBox).contains(","));
        Assert.assertTrue(!selenium.getValue(minInstalmentGapTextBox).contains("text") & !selenium.getValue(minInstalmentGapTextBox).contains(","));

        fillInstalmentOptionsAndSubmit("1000","1000", "1");
        Assert.assertTrue(selenium.isTextPresent("Minimum gap must be less than 4 digits for loans with variable installments"));
        Assert.assertTrue(selenium.isTextPresent("Maximum gap must be less than 4 digits for loans with variable installments"));

        fillInstalmentOptionsAndSubmit("-1","-1", "-1");
        Assert.assertTrue(selenium.isTextPresent("Minimum gap must not be zero or negative for loans with variable installments"));
        Assert.assertTrue(selenium.isTextPresent("Minimum gap must not be zero or negative for loans with variable installments"));
        Assert.assertTrue(selenium.isTextPresent("The min installment amount for variable installments is invalid because only numbers or decimal separator are allowed."));

        fillInstalmentOptionsAndSubmit("0","0", "0");
        Assert.assertTrue(selenium.isTextPresent("Minimum gap must not be zero or negative for loans with variable installments"));
        Assert.assertTrue(selenium.isTextPresent("Minimum gap must not be zero or negative for loans with variable installments"));

        fillInstalmentOptionsAndSubmit("1", "10", "1");
        Assert.assertTrue(selenium.isTextPresent("Minimum gap must be less than the maximum gap for loans with variable installments"));

        fillVariableInstalmentOption("","","");
//        Assert.assertTrue(selenium.isTextPresent("Minimum gap must be less than the maximum gap for loans with variable installments"));
    }

    private void fillInstalmentOptionsAndSubmit(String maxGap, String minGap, String minInstalmentAmount) {
        fillVariableInstalmentOption(maxGap, minGap, minInstalmentAmount);
        submitAndGotoNewLoanProductPreviewPage();
    }

    public void fillVariableInstalmentOption(String maxGap, String minGap, String minInstalmentAmount) {
        if (!selenium.isChecked(configureVariableInstalmentsCheckbox)){
            selectVariableInstalmentAndWaitForLoad();
        }
        selenium.type(maxInstalmentGapTextBox, maxGap);
        selenium.type(minInstalmentGapTextBox, minGap);
        selenium.type(minInstalmentAmountTextBox, minInstalmentAmount);
    }

    private void selectVariableInstalmentAndWaitForLoad() {
        selenium.click(configureVariableInstalmentsCheckbox);
        waitForElementToPresent(minInstalmentAmountTextBox);
    }

    public void verifyVariableInstalmentNotAvailable() {
        Assert.assertTrue(!selenium.isElementPresent(configureVariableInstalmentsCheckbox));
    }


}
