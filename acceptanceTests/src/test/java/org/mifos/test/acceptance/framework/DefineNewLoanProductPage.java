/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.test.acceptance.framework;

import com.thoughtworks.selenium.Selenium;

public class DefineNewLoanProductPage extends AbstractPage {
    
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


        private String branch;
        private String offeringName;
        private String offeringShortName;
        private String description;
        private String category;
        private String applicableFor;
        private String minLoanAmount;
        private String maxLoanAmount;
        private String defaultLoanAmount;
        private String interestTypes;
        private String minInterestRate;
        private String maxInterestRate;
        private String defaultInterestRate;
        private String freqOfInstallments;
        private String defInstallments;
        private String maxInstallments;
        private String gracePeriodType;
        private String interestGLCode;
        private String principalGLCode;
        

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

        public String getApplicableFor() {
            return this.applicableFor;
        }

        public void setApplicableFor(String applicableFor) {
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

        public String getInterestTypes() {
            return this.interestTypes;
        }

        public void setInterestTypes(String interestTypes) {
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

        public void setFreqOfInstallments(String freqOfInstallments) {
            this.freqOfInstallments = freqOfInstallments;
        }

        public String getFreqOfInstallments() {
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

        public String getGracePeriodType() {
            return this.gracePeriodType;
        }

        public void setGracePeriodType(String gracePeriodType) {
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
 
        
    }
        
    public DefineNewLoanProductPreviewPage submitAndGotoNewLoanProductPreviewPage(SubmitFormParameters parameters) {
        
        selenium.type("createLoanProduct.input.prdOffering", parameters.getOfferingName());
        selenium.type("createLoanProduct.input.prdOfferingShortName", parameters.getOfferingShortName());
        selenium.type("createLoanProduct.input.description", parameters.getDescription());
        selenium.select("prdCategory", "label=" + parameters.getCategory());
        selenium.select("prdApplicableMaster", "label=" + parameters.getApplicableFor());
        selenium.type("minLoanAmount", parameters.getMinLoanAmount());
        selenium.type("maxLoanAmount", parameters.getMaxLoanAmount());
        selenium.type("defaultLoanAmount", parameters.getDefaultLoanAmount());
        selenium.select("interestTypes", "label=" + parameters.getInterestTypes());
        selenium.type("createLoanProduct.input.maxInterestRate", parameters.getMaxInterestRate());
        selenium.type("createLoanProduct.input.minInterestRate", parameters.getMinInterestRate() );
        selenium.type("createLoanProduct.input.defInterestRate", parameters.getDefaultInterestRate());
        selenium.click("createLoanProduct.radio.freqOfInstallments" + parameters.getFreqOfInstallments()); 
        selenium.type("maxNoInstallments", parameters.getMaxInstallments());
        selenium.type("defNoInstallments", parameters.getDefInstallments());
        selenium.select("gracePeriodType", "label=" + parameters.getGracePeriodType());
        selenium.select("interestGLCode", "label=" + parameters.getInterestGLCode());
        selenium.select("principalGLCode", "label=" + parameters.getPrincipalGLCode());
        selenium.click("createLoanProduct.button.preview");
        waitForPageToLoad();
        return new DefineNewLoanProductPreviewPage(selenium);
    }



}
