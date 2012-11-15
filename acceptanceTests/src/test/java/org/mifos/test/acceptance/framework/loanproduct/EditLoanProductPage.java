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

import org.apache.commons.collections.CollectionUtils;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

import java.util.List;

@SuppressWarnings("PMD.CyclomaticComplexity")
public class EditLoanProductPage extends MifosPage {

    String configureVariableInstalmentsCheckbox = "canConfigureVariableInstallments";
    String minInstalmentGapTextBox = "minimumGapBetweenInstallments";
    String maxInstalmentGapTextBox = "maximumGapBetweenInstallments";
    String minInstalmentAmountTextBox = "minimumInstallmentAmount";
    String cashFlowCheckbox = "cashFlowValidation";
    String cashFlowThresholdTextBox = "cashFlowThreshold";
    String indebtednessRatioTextBox = "indebtednessRatio";
    String repaymentCapacityTextBox = "repaymentCapacity";
    public EditLoanProductPage(Selenium selenium) {
        super(selenium);
    }

    public EditLoanProductPage verifyPage() {
        verifyPage("EditLoanProduct");
        return this;
    }
    
    @SuppressWarnings("PMD")
    public EditLoanProductPreviewPage submitAndNavigateToEditLoanProductPreviewPage(SubmitFormParameters parameters) {
        typeTextIfNotEmpty("EditLoanProduct.input.name", parameters.getOfferingName());
        typeTextIfNotEmpty("EditLoanProduct.input.shortName", parameters.getOfferingShortName());
        typeTextIfNotEmpty("EditLoanProduct.input.description", parameters.getDescription());
        selenium.select("prdCategory", "label=" + parameters.getCategory());
        selenium.select("prdApplicableMaster", "value=" + parameters.getApplicableFor());
        
        selenium.click("name=loanAmtCalcType value=" + parameters.getCalculateLoanAmount());
        
        if(parameters.getCalculateLoanAmount() == SubmitFormParameters.SAME_FOR_ALL_LOANS) {
        	typeTextIfNotEmpty("minLoanAmount", parameters.getMinLoanAmount().replaceAll(",", ""));
        	typeTextIfNotEmpty("maxLoanAmount", parameters.getMaxLoanAmount().replaceAll(",", ""));
        	typeTextIfNotEmpty("defaultLoanAmount", parameters.getDefaultLoanAmount().replaceAll(",", ""));
        }
        else if(parameters.getCalculateLoanAmount() == SubmitFormParameters.BY_LAST_LOAN_AMOUNT) {
            for(int i = 1; i <= SubmitFormParameters.MAX_CYCLES; i++) {
                selenium.typeKeys("endRangeLoanAmt"+i, parameters.getLastAmountByLastLoanAmount(i-1).replaceAll(",", ""));
                selenium.fireEvent("endRangeLoanAmt"+i, "blur");
                typeTextIfNotEmpty("lastLoanMinLoanAmt"+i, parameters.getMinAmountByLastLoanAmount(i-1).replaceAll(",", ""));
                typeTextIfNotEmpty("lastLoanMaxLoanAmt"+i, parameters.getMaxAmountByLastLoanAmount(i-1).replaceAll(",", ""));
                typeTextIfNotEmpty("lastLoanDefaultLoanAmt"+i, parameters.getDefAmountByLastLoanAmount(i-1).replaceAll(",", ""));
            }
        }
        else {
            for(int i = 1; i <= SubmitFormParameters.MAX_CYCLES; i++) {
                typeTextIfNotEmpty("cycleLoanMinLoanAmt"+i, parameters.getMinCycleLoanAmount(i-1).replaceAll(",", ""));
                typeTextIfNotEmpty("cycleLoanMaxLoanAmt"+i, parameters.getMaxCycleLoanAmount(i-1).replaceAll(",", ""));
                typeTextIfNotEmpty("cycleLoanDefaultLoanAmt"+i, parameters.getDefCycleLoanAmount(i-1).replaceAll(",", ""));
            }
        }
        
        if(parameters.isInterestWaiver()){
            selenium.check("EditLoanProduct.input.includeInterestWaiver");
        }else{
            selenium.uncheck("EditLoanProduct.input.includeInterestWaiver");
        }
        selenium.select("interestTypes", "value=" + parameters.getInterestTypes());
        typeTextIfNotEmpty("EditLoanProduct.input.maxInterestRate", parameters.getMaxInterestRate());
        typeTextIfNotEmpty("EditLoanProduct.input.minInterestRate", parameters.getMinInterestRate());
        typeTextIfNotEmpty("EditLoanProduct.input.defaultInterestRate", parameters.getDefaultInterestRate());
        selenium.click("name=freqOfInstallments value=" + parameters.getFreqOfInstallments());
        selenium.click("name=calcInstallmentType value=" + parameters.getCalculateInstallments());
        if(parameters.getCalculateInstallments() == SubmitFormParameters.SAME_FOR_ALL_LOANS) {
            typeTextIfNotEmpty("minNoInstallments", parameters.getMinInstallemnts());
            typeTextIfNotEmpty("maxNoInstallments", parameters.getMaxInstallments());
            typeTextIfNotEmpty("defNoInstallments", parameters.getDefInstallments());
        }
        else if(parameters.getCalculateInstallments() == SubmitFormParameters.BY_LAST_LOAN_AMOUNT) {
            for(int i = 1; i <= SubmitFormParameters.MAX_CYCLES; i++) {
                selenium.typeKeys("endInstallmentRange"+i, parameters.getLastInstallmentByLastLoanAmount(i-1).replaceAll(",", ""));
                selenium.fireEvent("endInstallmentRange"+i, "blur");
                typeTextIfNotEmpty("minLoanInstallment"+i, parameters.getMinInstallmentByLastLoanAmount(i-1).replaceAll(",", ""));
                typeTextIfNotEmpty("maxLoanInstallment"+i, parameters.getMaxInstallmentByLastLoanAmount(i-1).replaceAll(",", ""));
                typeTextIfNotEmpty("defLoanInstallment"+i, parameters.getDefInstallmentByLastLoanAmount(i-1).replaceAll(",", ""));
            }
        }
        else {
            for(int i = 1; i <= SubmitFormParameters.MAX_CYCLES; i++) {
                typeTextIfNotEmpty("minCycleInstallment"+i, parameters.getMinCycleInstallment(i-1));
                typeTextIfNotEmpty("maxCycleInstallment"+i, parameters.getMaxCycleInstallment(i-1));
                typeTextIfNotEmpty("defCycleInstallment"+i, parameters.getDefCycleInstallment(i-1));
            }
        }
        
        fillFeesAndPenalties(parameters);
        
        selenium.select("gracePeriodType", "value=" + parameters.getGracePeriodType());
        selenium.fireEvent("gracePeriodType", "change");
        if(parameters.getGracePeriodType()>1){
            typeTextIfNotEmpty("gracePeriodDuration", parameters.getGracePeriodDuration());
        }
        selenium.select("interestGLCode", "label=" + parameters.getInterestGLCode());
        selenium.select("principalGLCode", "label=" + parameters.getPrincipalGLCode());
        selectQuestionGroups(parameters.getQuestionGroups());
        
        return editSubmit();
    }
    
    private void fillFeesAndPenalties(SubmitFormParameters parameters) {
    	if(parameters.getFees().size()>0){
            for (int i=0;i<parameters.getFees().size();i++){
                if(i==0){
                	selenium.select("feeId", "label="+parameters.getFees().get(i));
        		}else{
        			selenium.addSelection("feeId", "label="+parameters.getFees().get(i));
                }
            }
            selenium.click("LoanFeesList.button.add");
        }
        
        if(parameters.getPenalties().size()>0){
            for (int i=0;i<parameters.getPenalties().size();i++){
                if(i==0){
                	 selenium.select("penaltyId", "label="+parameters.getPenalties().get(i));
                }else{
                	selenium.addSelection("penaltyId", "label="+parameters.getPenalties().get(i));
                }
            }
            selenium.click("LoanPenaltiesList.button.add");
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
    
    public EditLoanProductPreviewPage submitDescriptionAndInterestChanges(SubmitFormParameters parameters) {
        selenium.type("EditLoanProduct.input.description", parameters.getDescription());
        selenium.type("EditLoanProduct.input.maxInterestRate", parameters.getMaxInterestRate());
        selenium.type("EditLoanProduct.input.minInterestRate", parameters.getMinInterestRate() );
        selenium.type("EditLoanProduct.input.defaultInterestRate", parameters.getDefaultInterestRate());
        return editSubmit();
    }

    public EditLoanProductPreviewPage submitRequiredDescriptionAndInterestChanges(SubmitFormParameters parameters) {
            selenium.type("EditLoanProduct.input.name", parameters.getOfferingName());
            selenium.type("EditLoanProduct.input.shortName", parameters.getOfferingShortName());
            selenium.type("EditLoanProduct.input.description", parameters.getDescription());
            selenium.type("startDateDD", parameters.getStartDateDd());
            selenium.type("startDateMM", parameters.getStartDateMm());
            selenium.type("startDateYY", parameters.getStartDateYy());
            selenium.type("EditLoanProduct.input.maxInterestRate", parameters.getMaxInterestRate());
            selenium.type("EditLoanProduct.input.minInterestRate", parameters.getMinInterestRate() );
            selenium.type("EditLoanProduct.input.defaultInterestRate", parameters.getDefaultInterestRate());

            if(parameters.getProductCategory()==0) {
                selenium.select("EditLoanProduct.input.category", "value=");
            } else {
                selenium.select("EditLoanProduct.input.category", "value="+parameters.getProductCategory());
            }
            if (parameters.getApplicableFor()==0) {
                selenium.select("EditLoanProduct.input.applicableFor", "value=");
            } else {
                selenium.select("EditLoanProduct.input.applicableFor", "value=" + parameters.getApplicableFor());
            }

            if(parameters.getStatus()==0) {
                selenium.select("EditLoanProduct.input.status","value=");
            } else {
                selenium.select("EditLoanProduct.input.status","value=" + parameters.getStatus());
            }

            if(parameters.getInterestTypes()==0) {
                selenium.select("EditLoanProduct.input.interestTypes", "value=" );
            } else {
                selenium.select("EditLoanProduct.input.interestTypes", "value=" + parameters.getInterestTypes());
            }
        return editSubmit();
    }

    public void verifyModifiedDescriptionAndInterest(SubmitFormParameters formParameters) {
        Assert.assertEquals(getDescription(), formParameters.getDescription());
        Assert.assertEquals(getMinInterestRate(), formParameters.getMinInterestRate());
        Assert.assertEquals(getMaxInterestRate(), formParameters.getMaxInterestRate());
        Assert.assertEquals(getDefaultInterestRate(), formParameters.getDefaultInterestRate());
    }

    public void verifyModifiedLoanProduct(SubmitFormParameters formParameters) {
        Assert.assertEquals(getDescription(), formParameters.getDescription());
        Assert.assertEquals(getMinInterestRate(), formParameters.getMinInterestRate());
        Assert.assertEquals(getMaxInterestRate(), formParameters.getMaxInterestRate());
        Assert.assertEquals(getDefaultInterestRate(), formParameters.getDefaultInterestRate());
        Assert.assertEquals(selenium.getValue("EditLoanProduct.input.name"), formParameters.getOfferingName());
        Assert.assertEquals(selenium.getValue("EditLoanProduct.input.shortName"), formParameters.getOfferingShortName());
        Assert.assertEquals(selenium.getValue("startDateDD"), formParameters.getStartDateDd());
        Assert.assertEquals(selenium.getValue("startDateMM"), formParameters.getStartDateMm());
        Assert.assertEquals(selenium.getValue("startDateYY"), formParameters.getStartDateYy());

        Assert.assertEquals(selenium.getSelectedValue("EditLoanProduct.input.applicableFor"), Integer.toString(formParameters.getApplicableFor()));
        Assert.assertEquals(selenium.getSelectedValue("EditLoanProduct.input.status"), Integer.toString(formParameters.getStatus()));
        Assert.assertEquals(selenium.getSelectedValue("EditLoanProduct.input.interestTypes"), Integer.toString(formParameters.getInterestTypes()));
        Assert.assertEquals(selenium.getSelectedValue("EditLoanProduct.input.category"), Integer.toString(formParameters.getProductCategory()));
    }

    private String getDefaultInterestRate() {
        return selenium.getValue("EditLoanProduct.input.defaultInterestRate");
    }

    private String getMaxInterestRate() {
        return selenium.getValue("EditLoanProduct.input.maxInterestRate");
    }

    private String getMinInterestRate() {
        return selenium.getValue("EditLoanProduct.input.minInterestRate");
    }

    private String getDescription() {
        return selenium.getValue("EditLoanProduct.input.description");
    }

    public EditLoanProductPreviewPage submitInterestWaiverChanges(SubmitFormParameters formParameters) {
        if (formParameters.isInterestWaiver()) {
            selenium.check("EditLoanProduct.input.includeInterestWaiver");
        } else {
            selenium.uncheck("EditLoanProduct.input.includeInterestWaiver");
        }
        return editSubmit();
    }

    public EditLoanProductPreviewPage submitIncludeInLoanCounter(SubmitFormParameters formParameters) {
        if (formParameters.isIncludeInLoanCounter()) {
            selenium.check("EditLoanProduct.input.includeInLoanCycleCounter");
        } else {
            selenium.uncheck("EditLoanProduct.input.includeInLoanCycleCounter");
        }
        return editSubmit();
    }

    public EditLoanProductPreviewPage submitQuestionGroupChanges(SubmitFormParameters formParameters) {
        List<String> questionGroups = formParameters.getQuestionGroups();
        if (CollectionUtils.isNotEmpty(questionGroups)) {
            for (String questionGroup : questionGroups) {
                selenium.addSelection("name=id", questionGroup);
            }
            selenium.click("SrcQGList.button.add");
        }
        return editSubmit();
    }
    public EditLoanProductPreviewPage submitVariableInstalmentChange(String maxGap, String minGap, String minInstalmentAmount) {
        if (!selenium.isChecked(configureVariableInstalmentsCheckbox)){
            selenium.click(configureVariableInstalmentsCheckbox);
        }
        selenium.waitForCondition("selenium.isVisible('minimumInstallmentAmount')","10,000");
        selenium.type(maxInstalmentGapTextBox, maxGap);
        selenium.type(minInstalmentGapTextBox, minGap);
        selenium.type(minInstalmentAmountTextBox, minInstalmentAmount.replace(",", ""));
        return editSubmit();
    }
    public EditLoanProductPreviewPage editSubmit() {
        selenium.click("EditLoanProduct.button.preview");
        waitForPageToLoad();
        return new EditLoanProductPreviewPage(selenium);
    }

    public void verifyVariableInstalmentOptionDisabled() {
        Assert.assertTrue(!selenium.isElementPresent(configureVariableInstalmentsCheckbox));
    }

    public EditLoanProductPage setCashFlowThreshold(String warningThreshold) {
        if (!selenium.isChecked(cashFlowCheckbox)) {
            selenium.click(cashFlowCheckbox);
        }
        selenium.type(cashFlowThresholdTextBox, warningThreshold);
        return this;
    }
    
    public EditLoanProductPage setIndebtednessRate(String warningThreshold) {
        selenium.type(indebtednessRatioTextBox, warningThreshold);
        return this;
    }
    
    public EditLoanProductPage setRepaymentCapacity(String warningThreshold) {
        selenium.type(repaymentCapacityTextBox, warningThreshold);
        return this;
    }

    public EditLoanProductPage verifyCashFlowDefaultsInEditProduct() {
        Assert.assertTrue(!selenium.isChecked(cashFlowCheckbox));
        Assert.assertTrue(!selenium.isVisible(cashFlowThresholdTextBox));

        selenium.click(cashFlowCheckbox);
        Assert.assertTrue(selenium.isVisible(cashFlowThresholdTextBox));
        return this;
    }

}
