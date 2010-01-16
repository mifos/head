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
 
package org.mifos.test.acceptance.framework.loanproduct;

import org.mifos.test.acceptance.framework.AbstractPage;

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
        selenium.click("createLoanProduct.button.preview");
        waitForPageToLoad();
        return new DefineNewLoanProductPreviewPage(selenium);
    }



}
