/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.mifos.test.acceptance.framework.AbstractPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

@SuppressWarnings("PMD.CyclomaticComplexity")
public class DefineNewLoanProductPage extends AbstractPage {

    String configureVariableInstalmentsCheckbox = "canConfigureVariableInstallments";
    String minInstalmentGapTextBox = "minimumGapBetweenInstallments";
    String maxInstalmentGapTextBox = "maximumGapBetweenInstallments";
    String previewButton = "createLoanProduct.button.preview";
    String minInstalmentAmountTextBox = "minimumInstallmentAmount";
    String cashFlowCheckbox = "cashFlowValidation";
    String cashFlowThresholdTextBox = "cashFlowThreshold";
    String indebtentRate = "indebtednessRatio";
    String repaymentCapacity = "repaymentCapacity";

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

    public DefineNewLoanProductPage selectFixedRepaymentSchedule(SubmitFormParameters formParameters) {
        if(formParameters.isFixedRepaymentSchedule()){
            selenium.check("createLoanProduct.checkbox.fixedRepaymentSchedule");
        }else{
            selenium.uncheck("createLoanProduct.checkbox.fixedRepaymentSchedule");
        }
        return this;
    }

    @SuppressWarnings("PMD.TooManyFields")
    // lots of fields ok for form input case
    public static class SubmitFormParameters {
        // interest types
        public static final int FLAT = 1;
        public static final int DECLINING_BALANCE = 2;
        public static final int DECLINING_BALANCE_EPI = 4;
        public static final int DECLINING_BALANCE_INTEREST_RECALCULATION = 5;

        // applicable for
        public static final int CLIENTS = 1;
        public static final int GROUPS = 2;

        // freq of installments
        public static final int WEEKS = 1;
        public static final int MONTHS = 2;

        // grace period type
        public static final int NONE = 1;
        public static final int GRACE_ON_ALL_REPAYMENTS = 2;
        public static final int PRINCIPAL_ONLY_GRACE = 3;

        // Status
        public static final int ACTIVE = 1;
        public static final int INACTIVE = 4;

        // Calculate # of Installments as
        public static final int SAME_FOR_ALL_LOANS = 1;
        public static final int BY_LAST_LOAN_AMOUNT = 2;
        public static final int BY_LOAN_CYCLE = 3;
        public static final int MAX_CYCLES = 6;

        // product category
        public static final String CATEGORY_OTHER = "Other";
        public static final int OTHER = 1;

        private String branch;
        private String offeringName;
        private String offeringShortName;
        private String description;
        private String category;
        private int applicableFor;
        private int calculateLoanAmount = SAME_FOR_ALL_LOANS;
        private String minLoanAmount;
        private String maxLoanAmount;
        private String defaultLoanAmount;
        private String[][] cycleLoanAmount = new String[MAX_CYCLES][3];
        private String[][] amountsByLastLoanAmount = new String[MAX_CYCLES][4];
        private int interestTypes;
        private String minInterestRate;
        private String maxInterestRate;
        private String defaultInterestRate;
        private int freqOfInstallments;
        private int calculateInstallments = SAME_FOR_ALL_LOANS;
        private String minInstallemnts = "1";
        private String defInstallments;
        private String maxInstallments;
        private String[][] cycleInstallments = new String[MAX_CYCLES][3];
        private String[][] installmentsByLastLoanAmount = new String[MAX_CYCLES][4];
        private int gracePeriodType;
        private String gracePeriodDuration;
        private String interestGLCode;
        private String principalGLCode;
        private boolean interestWaiver;
        private boolean includeInLoanCounter;
        private boolean fixedRepaymentSchedule;
        private List<String> questionGroups;
        private String startDateDd;
        private String startDateMm;
        private String startDateYy;
        private int status;
        private int productCategory;
        private final List<String> fees = new ArrayList<String>();
        private final List<String> penalties = new ArrayList<String>();
        private String additionalFee1;
        private String additionalFee2;
        private String additionalFee3;

        public String getMinInstallemnts() {
            return this.minInstallemnts;
        }

        public void setMinInstallemnts(String minInstallemnts) {
            this.minInstallemnts = minInstallemnts;
        }

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

        public String getGracePeriodDuration() {
            return this.gracePeriodDuration;
        }

        public void setGracePeriodDuration(String gracePeriodDuration) {
            this.gracePeriodDuration = gracePeriodDuration;
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

        public boolean isIncludeInLoanCounter() {
            return includeInLoanCounter;
        }

        public void setIncludeInLoanCounter(boolean includeInLoanCounter) {
            this.includeInLoanCounter = includeInLoanCounter;
        }

        public boolean isFixedRepaymentSchedule() {
            return fixedRepaymentSchedule;
        }

        public void setFixedRepaymentSchedule(boolean fixedRepaymentSchedule) {
            this.fixedRepaymentSchedule = fixedRepaymentSchedule;
        }

        public List<String> getQuestionGroups() {
            return questionGroups;
        }

        public void setQuestionGroups(List<String> questionGroups) {
            this.questionGroups = questionGroups;
        }

        public void setCalculateInstallments(int calculateInstallments) {
            this.calculateInstallments = calculateInstallments;
        }

        public int getCalculateInstallments() {
            return calculateInstallments;
        }

        public void setCycleInstallments(String[][] cycleInstallments) {
            this.cycleInstallments = cycleInstallments.clone();
        }

        public String[][] getCycleInstallments() {
            return cycleInstallments.clone();
        }

        public String getMinCycleInstallment(int row) {
            return cycleInstallments[row][0];
        }

        public void setMinCycleInstallment(int row, String value) {
            this.cycleInstallments[row][0] = value;
        }

        public String getMaxCycleInstallment(int row) {
            return cycleInstallments[row][1];
        }

        public void setMaxCycleInstallment(int row, String value) {
            this.cycleInstallments[row][1] = value;
        }

        public String getDefCycleInstallment(int row) {
            return cycleInstallments[row][2];
        }

        public void setDefCycleInstallment(int row, String value) {
            this.cycleInstallments[row][2] = value;
        }

        public void setCalculateLoanAmount(int calculateLoanAmount) {
            this.calculateLoanAmount = calculateLoanAmount;
        }

        public int getCalculateLoanAmount() {
            return calculateLoanAmount;
        }

        public void setCycleLoanAmount(String[][] cycleLoanAmount) {
            this.cycleLoanAmount = cycleLoanAmount.clone();
        }

        public String[][] getCycleLoanAmount() {
            return cycleLoanAmount.clone();
        }

        public String getMinCycleLoanAmount(int row) {
            return cycleLoanAmount[row][0];
        }

        public void setMinCycleLoanAmount(int row, String value) {
            this.cycleLoanAmount[row][0] = value;
        }

        public String getMaxCycleLoanAmount(int row) {
            return cycleLoanAmount[row][1];
        }

        public void setMaxCycleLoanAmount(int row, String value) {
            this.cycleLoanAmount[row][1] = value;
        }

        public String getDefCycleLoanAmount(int row) {
            return cycleLoanAmount[row][2];
        }

        public void setDefCycleLoanAmount(int row, String value) {
            this.cycleLoanAmount[row][2] = value;
        }

        public void setInstallmentsByLastLoanAmount(String[][] installmentsByLastLoanAmount) {
            this.installmentsByLastLoanAmount = installmentsByLastLoanAmount.clone();
        }

        public String[][] getInstallmentsByLastLoanAmount() {
            return installmentsByLastLoanAmount.clone();
        }

        public String getLastInstallmentByLastLoanAmount(int row) {
            return installmentsByLastLoanAmount[row][0];
        }

        public void setLastInstallmentByLastLoanAmount(int row, String value) {
            this.installmentsByLastLoanAmount[row][0] = value;
        }

        public String getMinInstallmentByLastLoanAmount(int row) {
            return installmentsByLastLoanAmount[row][1];
        }

        public void setMinInstallmentByLastLoanAmount(int row, String value) {
            this.installmentsByLastLoanAmount[row][1] = value;
        }

        public String getMaxInstallmentByLastLoanAmount(int row) {
            return installmentsByLastLoanAmount[row][2];
        }

        public void setMaxInstallmentByLastLoanAmount(int row, String value) {
            this.installmentsByLastLoanAmount[row][2] = value;
        }

        public String getDefInstallmentByLastLoanAmount(int row) {
            return installmentsByLastLoanAmount[row][3];
        }

        public void setDefInstallmentByLastLoanAmount(int row, String value) {
            this.installmentsByLastLoanAmount[row][3] = value;
        }

        public void setAmountsByLastLoanAmount(String[][] amountsByLastLoanAmount) {
            this.amountsByLastLoanAmount = amountsByLastLoanAmount.clone();
        }

        public String[][] getAmountsByLastLoanAmount() {
            return amountsByLastLoanAmount.clone();
        }

        public String getLastAmountByLastLoanAmount(int row) {
            return amountsByLastLoanAmount[row][0];
        }

        public void setLastAmountByLastLoanAmount(int row, String value) {
            this.amountsByLastLoanAmount[row][0] = value;
        }

        public String getMinAmountByLastLoanAmount(int row) {
            return amountsByLastLoanAmount[row][1];
        }

        public void setMinAmountByLastLoanAmount(int row, String value) {
            this.amountsByLastLoanAmount[row][1] = value;
        }

        public String getMaxAmountByLastLoanAmount(int row) {
            return amountsByLastLoanAmount[row][2];
        }

        public void setMaxAmountByLastLoanAmount(int row, String value) {
            this.amountsByLastLoanAmount[row][2] = value;
        }

        public String getDefAmountByLastLoanAmount(int row) {
            return amountsByLastLoanAmount[row][3];
        }

        public void setDefAmountByLastLoanAmount(int row, String value) {
            this.amountsByLastLoanAmount[row][3] = value;
        }

        public String getStartDateDd() {
            return this.startDateDd;
        }

        public void setStartDateDd(String startDateDd) {
            this.startDateDd = startDateDd;
        }

        public String getStartDateMm() {
            return this.startDateMm;
        }

        public void setStartDateMm(String startDateMm) {
            this.startDateMm = startDateMm;
        }

        public String getStartDateYy() {
            return this.startDateYy;
        }

        public void setStartDateYy(String startDateYy) {
            this.startDateYy = startDateYy;
        }

        public int getStatus() {
            return this.status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getProductCategory() {
            return this.productCategory;
        }

        public void setProductCategory(int productCategory) {
            this.productCategory = productCategory;
        }

        public String getAdditionalFee1() {
            return this.additionalFee1;
        }

        public void setAdditionalFee1(String additionalFee1) {
            this.additionalFee1 = additionalFee1;
        }

        public String getAdditionalFee2() {
            return this.additionalFee2;
        }

        public void setAdditionalFee2(String additionalFee2) {
            this.additionalFee2 = additionalFee2;
        }

        public String getAdditionalFee3() {
            return this.additionalFee3;
        }

        public void setAdditionalFee3(String additionalFee3) {
            this.additionalFee3 = additionalFee3;
        }
        
        public List<String> getFees() {
			return fees;
		}
        
        public void addFee(String feeName){
        	fees.add(feeName);
        }
        
        public List<String> getPenalties() {
            return penalties;
        }
        
        public void addPenalty(String penaltyName) {
            penalties.add(penaltyName);
        }
    }
    
    private void addSelectionFee(String feeName){
    	selenium.addSelection("feeId", "label="+feeName);
    }
    
    private void selectFee(String feeName){
    	selenium.select("feeId", "label="+feeName);
    }
    
    private void addSelectedFees(){
    	selenium.click("LoanFeesList.button.add");
    }
    
    private void selectAddedFee(String feeName){
    	selenium.addSelection("prdOfferinFees", "label="+feeName);
    }
    
    private void removeAddedFees(){
    	selenium.click("LoanFeesList.button.remove");
    }
    
    public void removeFees(List<String> fees){
    	for (String fee : fees) {
			selectAddedFee(fee);
		}
    	removeAddedFees();
    }
    
    private void addSelectionPenalty(String penaltyName){
        selenium.addSelection("penaltyId", "label="+penaltyName);
    }
    
    private void selectPenalty(String penaltyName){
        selenium.select("penaltyId", "label="+penaltyName);
    }
    
    private void addSelectedPenalties(){
        selenium.click("LoanPenaltiesList.button.add");
    }
    
    private void selectAddedPenalty(String penaltyName){
        selenium.addSelection("prdOfferinPenalties", "label="+penaltyName);
    }
    
    private void removeAddedPenalties(){
        selenium.click("LoanPenaltiesList.button.remove");
    }
    
    public void removePenalties(List<String> penalties){
        for (String penalty : penalties) {
            selectAddedPenalty(penalty);
        }
        removeAddedPenalties();
    }

    @SuppressWarnings("PMD.NPathComplexity")
    public DefineNewLoanProductPage fillLoanParameters(SubmitFormParameters parameters) {
        selenium.type("createLoanProduct.input.prdOffering", parameters.getOfferingName());
        selenium.type("createLoanProduct.input.prdOfferingShortName", parameters.getOfferingShortName());
        selenium.type("createLoanProduct.input.description", parameters.getDescription());
        selenium.select("prdCategory", "label=" + parameters.getCategory());
        selenium.select("prdApplicableMaster", "value=" + parameters.getApplicableFor());
        selenium.click("name=loanAmtCalcType value=" + parameters.getCalculateLoanAmount());
        if(parameters.getCalculateLoanAmount() == SubmitFormParameters.SAME_FOR_ALL_LOANS) {
            selenium.type("minLoanAmount", parameters.getMinLoanAmount().replaceAll(",", ""));
            selenium.type("maxLoanAmount", parameters.getMaxLoanAmount().replaceAll(",", ""));
            selenium.type("defaultLoanAmount", parameters.getDefaultLoanAmount().replaceAll(",", ""));
        }
        else if(parameters.getCalculateLoanAmount() == SubmitFormParameters.BY_LAST_LOAN_AMOUNT) {
            for(int i = 1; i <= SubmitFormParameters.MAX_CYCLES; i++) {
                selenium.typeKeys("endRangeLoanAmt"+i, parameters.getLastAmountByLastLoanAmount(i-1).replaceAll(",", ""));
                selenium.fireEvent("endRangeLoanAmt"+i, "blur");
                selenium.type("lastLoanMinLoanAmt"+i, parameters.getMinAmountByLastLoanAmount(i-1).replaceAll(",", ""));
                selenium.type("lastLoanMaxLoanAmt"+i, parameters.getMaxAmountByLastLoanAmount(i-1).replaceAll(",", ""));
                selenium.type("lastLoanDefaultLoanAmt"+i, parameters.getDefAmountByLastLoanAmount(i-1).replaceAll(",", ""));
            }
        }
        else {
            for(int i = 1; i <= SubmitFormParameters.MAX_CYCLES; i++) {
                selenium.type("cycleLoanMinLoanAmt"+i, parameters.getMinCycleLoanAmount(i-1).replaceAll(",", ""));
                selenium.type("cycleLoanMaxLoanAmt"+i, parameters.getMaxCycleLoanAmount(i-1).replaceAll(",", ""));
                selenium.type("cycleLoanDefaultLoanAmt"+i, parameters.getDefCycleLoanAmount(i-1).replaceAll(",", ""));
            }
        }
        selectWaiverInterest(parameters);
        setInterestRateType(parameters.getInterestTypes());
        selenium.type("createLoanProduct.input.maxInterestRate", parameters.getMaxInterestRate());
        selenium.type("createLoanProduct.input.minInterestRate", parameters.getMinInterestRate());
        selenium.type("createLoanProduct.input.defInterestRate", parameters.getDefaultInterestRate());
        selenium.click("name=freqOfInstallments value=" + parameters.getFreqOfInstallments());
        selenium.click("name=calcInstallmentType value=" + parameters.getCalculateInstallments());
        if(parameters.getCalculateInstallments() == SubmitFormParameters.SAME_FOR_ALL_LOANS) {
            selenium.type("minNoInstallments", parameters.getMinInstallemnts());
            selenium.type("maxNoInstallments", parameters.getMaxInstallments());
            selenium.type("defNoInstallments", parameters.getDefInstallments());
        }
        else if(parameters.getCalculateInstallments() == SubmitFormParameters.BY_LAST_LOAN_AMOUNT) {
            for(int i = 1; i <= SubmitFormParameters.MAX_CYCLES; i++) {
                selenium.typeKeys("endInstallmentRange"+i, parameters.getLastInstallmentByLastLoanAmount(i-1).replaceAll(",", ""));
                selenium.fireEvent("endInstallmentRange"+i, "blur");
                selenium.type("minLoanInstallment"+i, parameters.getMinInstallmentByLastLoanAmount(i-1).replaceAll(",", ""));
                selenium.type("maxLoanInstallment"+i, parameters.getMaxInstallmentByLastLoanAmount(i-1).replaceAll(",", ""));
                selenium.type("defLoanInstallment"+i, parameters.getDefInstallmentByLastLoanAmount(i-1).replaceAll(",", ""));
            }
        }
        else {
            for(int i = 1; i <= SubmitFormParameters.MAX_CYCLES; i++) {
                selenium.type("minCycleInstallment"+i, parameters.getMinCycleInstallment(i-1));
                selenium.type("maxCycleInstallment"+i, parameters.getMaxCycleInstallment(i-1));
                selenium.type("defCycleInstallment"+i, parameters.getDefCycleInstallment(i-1));
            }
        }
        
        if(parameters.getFees().size()>0){
        	for (int i=0;i<parameters.getFees().size();i++){
        		if(i==0){
        			selectFee(parameters.getFees().get(i));
        		}else{
    				addSelectionFee(parameters.getFees().get(i));
        		}
			}
        	addSelectedFees();
        }
        
        if(parameters.getPenalties().size()>0){
            for (int i=0;i<parameters.getPenalties().size();i++){
                if(i==0){
                    selectPenalty(parameters.getPenalties().get(i));
                }else{
                    addSelectionPenalty(parameters.getPenalties().get(i));
                }
            }
            addSelectedPenalties();
        }
        
        selenium.select("gracePeriodType", "value=" + parameters.getGracePeriodType());
        selenium.fireEvent("gracePeriodType", "change");
        if(parameters.getGracePeriodType()>1){
            selenium.type("gracePeriodDuration", parameters.getGracePeriodDuration());
        }
        selenium.select("interestGLCode", "label=" + parameters.getInterestGLCode());
        selenium.select("principalGLCode", "label=" + parameters.getPrincipalGLCode());
        selectQuestionGroups(parameters.getQuestionGroups());
        selectSourceOfFund("Funding Org A");
        return this;
    }
    
    public void setInterestRateType(int interestType){
    	selenium.select("interestTypes", "value=" + interestType);
    }

    private void selectSourceOfFund(String sourceOfFund) {
        selenium.addSelection("name=fundId", "label=" + sourceOfFund);
        selenium.click("SrcFundsList.button.add");
    }

    private void selectWaiverInterest(SubmitFormParameters parameters){
        if(parameters.isInterestWaiver()){
            selenium.check("createLoanProduct.checkbox.waiverInterest");
        }else{
            selenium.uncheck("createLoanProduct.checkbox.waiverInterest");
        }
    }

    private void selectQuestionGroups(List<String> questionGroups) {
        if (CollectionUtils.isNotEmpty(questionGroups)) {
            for (String questionGroup : questionGroups) {
                selenium.addSelection("name=id", questionGroup);
            }
            selenium.click("SrcQGList.button.add");
        }
    }

    public DefineNewLoanProductPreviewPage submitAndGotoNewLoanProductPreviewPage() {
        submit();
        return new DefineNewLoanProductPreviewPage(selenium);
    }

    public DefineNewLoanProductPage submitWithErrors() {
        submit();
        return this;
    }
    
    public DefineNewLoanProductPage submitWithErrors(String error) {
        submit();
        selenium.isTextPresent(error);
        return this;
    }

    private void submit() {
        selenium.click(previewButton);
        waitForPageToLoad();
    }

    public DefineNewLoanProductPage verifyVariableInstalmentOptionsDefaults() {

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
        return this;
    }

    public DefineNewLoanProductPage verifyVariableInstalmentOptionsFields() {
        fillInstalmentOptionsAndSubmit("text,", "text,", "text,");
        isTextPresentInPage("The min installment amount for variable installments is invalid because only positive numbers and decimal separator are allowed");
        Assert.assertTrue(!selenium.getValue(maxInstalmentGapTextBox).contains("text") & !selenium.getValue(maxInstalmentGapTextBox).contains(","));
        Assert.assertTrue(!selenium.getValue(minInstalmentGapTextBox).contains("text") & !selenium.getValue(minInstalmentGapTextBox).contains(","));

        fillInstalmentOptionsAndSubmit("1000", "1000", "1");
        isTextPresentInPage("Minimum gap must be less than 4 digits for loans with variable installments");
        isTextPresentInPage("Maximum gap must be less than 4 digits for loans with variable installments");

        fillInstalmentOptionsAndSubmit("-1", "-1", "-1");
        isTextPresentInPage("Minimum gap must not be zero or negative for loans with variable installments");
        isTextPresentInPage("Minimum gap must not be zero or negative for loans with variable installments");
        isTextPresentInPage("The min installment amount for variable installments is invalid because only positive numbers and decimal separator are allowed");

        fillInstalmentOptionsAndSubmit("0", "0", "0");
        isTextPresentInPage("Minimum gap must not be zero or negative for loans with variable installments");
        isTextPresentInPage("Minimum gap must not be zero or negative for loans with variable installments");

        fillInstalmentOptionsAndSubmit("1", "10", "1");
        isTextPresentInPage("Minimum gap must be less than the maximum gap for loans with variable installments");

        fillVariableInstalmentOption("", "", "");
//        Assert.assertTrue(selenium.isTextPresent("Minimum gap must be less than the maximum gap for loans with variable installments"));
        return this;
    }

    private void fillInstalmentOptionsAndSubmit(String maxGap, String minGap, String minInstalmentAmount) {
        fillVariableInstalmentOption(maxGap, minGap, minInstalmentAmount);
        submitWithErrors();
    }

    public DefineNewLoanProductPage fillVariableInstalmentOption(String maxGap, String minGap, String minInstalmentAmount) {
        if (!selenium.isChecked(configureVariableInstalmentsCheckbox)) {
            selectVariableInstalmentAndWaitForLoad();
        }
        selenium.type(maxInstalmentGapTextBox, maxGap);
        selenium.type(minInstalmentGapTextBox, minGap);
        selenium.type(minInstalmentAmountTextBox, minInstalmentAmount);
        return this;
    }

    public DefineNewLoanProductPage checkConfigureVariableInstalmentsCheckbox(){
        selenium.click(configureVariableInstalmentsCheckbox);
        return this;
    }

    public DefineNewLoanProductPage fillGracePeriod (int gracePeriodtype, String gracePeriodDuration){
      	selenium.select("gracePeriodType", "value=" + gracePeriodtype);
    	selenium.type("gracePeriodDuration", gracePeriodDuration);
        return this;
    }
    
    private DefineNewLoanProductPage selectVariableInstalmentAndWaitForLoad() {
        checkConfigureVariableInstalmentsCheckbox();
        waitForElementToPresent(minInstalmentAmountTextBox);
        return this;
    }

    public DefineNewLoanProductPage verifyVariableInstalmentNotAvailable() {
        Assert.assertTrue(!selenium.isElementPresent(configureVariableInstalmentsCheckbox));
        return this;
    }

    public DefineNewLoanProductPage fillCashFlow(String warningThreshold, String indebtentValue, String repaymentValue) {
        if (!selenium.isChecked(cashFlowCheckbox)) {
            selenium.click(cashFlowCheckbox);
        }
        selenium.type(cashFlowThresholdTextBox, warningThreshold);
        selenium.type(indebtentRate, indebtentValue);
        selenium.type(repaymentCapacity, repaymentValue);
        return this;
    }

    public DefineNewLoanProductPage verifyCashFlowFieldDefault() {
        Assert.assertTrue(!selenium.isChecked(cashFlowCheckbox));
        Assert.assertTrue(!selenium.isVisible(cashFlowThresholdTextBox));

        selenium.click(cashFlowCheckbox);
        Assert.assertTrue(selenium.isVisible(cashFlowThresholdTextBox) & selenium.getValue(cashFlowThresholdTextBox).equals(""));
        Assert.assertTrue(selenium.isVisible(indebtentRate) & selenium.getValue(indebtentRate).equals(""));
        Assert.assertTrue(selenium.isVisible(repaymentCapacity) & selenium.getValue(repaymentCapacity).equals(""));
        return this;
    }

    public void verifyCashFlowFields() {
        verifyMaximumLimitForCashFlow();
        verifyNonNumericForCashFlow();
        verifyMinimumLimitForCashFlow();
        verifyDecimalsForCashFlow();
    }

    private void verifyDecimalsForCashFlow() {
        fillCashFlow("99.999", "45.001", "200.001");
        submit();
        isTextPresentInPage("The Warning Threshold is invalid because the number of digits after the decimal separator exceeds the allowed number 2");
        isTextPresentInPage("The Indebtedness Ratio is invalid because the number of digits after the decimal separator exceeds the allowed number 2");
        isTextPresentInPage("The Repayment Capacity is invalid because the number of digits after the decimal separator exceeds the allowed number 2");
        submitWithErrors();
    }

    private void verifyMinimumLimitForCashFlow() {
        fillCashFlow("-1", "-1", "149.9");
        submitWithErrors();
        isTextPresentInPage("The Indebtedness Ratio is invalid because only positive numbers and decimal separator are allowed");
        isTextPresentInPage("The Repayment Capacity is invalid because it is not in between 150.0 and 1000.0");
        isTextPresentInPage("The Warning Threshold is invalid because only positive numbers and decimal separator are allowed");
    }

    private void verifyNonNumericForCashFlow() {
        fillCashFlow("abc", "abc", "abc");
        submitWithErrors();
        isTextPresentInPage("The Warning Threshold is invalid because only positive numbers and decimal separator are allowed");
        isTextPresentInPage("The Indebtedness Ratio is invalid because only positive numbers and decimal separator are allowed");
        isTextPresentInPage("The Indebtedness Ratio is invalid because only positive numbers and decimal separator are allowed");
    }

    private void verifyMaximumLimitForCashFlow() {
        fillCashFlow("99.1", "50", "1000");
        submitWithErrors();
        isTextPresentInPage("The Warning Threshold is invalid because it is not in between 0.0 and 99.0");
        isTextPresentInPage("Inebtedness Ratio should be a value less than 50.0");
        isTextPresentInPage("Repayment Capacity should be a value less than 1000.0");
        fillCashFlow("100", "55", "1001");
        submitWithErrors();
        isTextPresentInPage("The Warning Threshold is invalid because it is not in between 0.0 and 99.0");
        isTextPresentInPage("The Indebtedness Ratio is invalid because it is not in between 0.0 and 50.0");
        isTextPresentInPage("The Repayment Capacity is invalid because it is not in between 150.0 and 1000.0");
    }

    private void isTextPresentInPage(String expectedText) {
        Assert.assertTrue(selenium.isTextPresent(expectedText),expectedText + " not found in the page");
    }

    public DefineNewLoanProductPage verifyBlockedInterestTypes() {
        verifyInterestBlockedForVariableInstallment(SubmitFormParameters.DECLINING_BALANCE_INTEREST_RECALCULATION);
        verifyInterestBlockedForVariableInstallment(SubmitFormParameters.DECLINING_BALANCE_EPI);
        return this;
    }

    private void verifyInterestBlockedForVariableInstallment(int interestType) {
        selenium.select("interestTypes", "value=" + interestType);
        fillInstalmentOptionsAndSubmit("10", "10", "100");
        submit();
        isTextPresentInPage("The selected interest type is invalid for variable installment loan product");
    }

    public void verifyFeeTypesBlocked(String[] feeNames) {
        for (String feeName : feeNames) {
            selenium.addSelection("feeId", "label=" + feeName);
        }
        selenium.click("LoanFeesList.button.add");
        submit();
        for (String feeName : feeNames) {
            isTextPresentInPage(feeName + " fee cannot be applied to variable installment loan product");
        }
    }
    public void addFee(String feeName){
        selenium.addSelection("feeId", "label=" + feeName);
        selenium.click("LoanFeesList.button.add");
    }

    public void verifyErrorInForm(String error) {
        Assert.assertTrue(selenium.isTextPresent(error));
    }

    public void fillStartDate(String yyyy, String mm, String dd) {
        selenium.type("startDate", dd + "/" + mm + "/" + yyyy);
    }
}
