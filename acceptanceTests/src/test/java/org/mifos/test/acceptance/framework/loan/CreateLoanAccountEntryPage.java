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

package org.mifos.test.acceptance.framework.loan;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

@SuppressWarnings("PMD")
public class CreateLoanAccountEntryPage extends MifosPage {

    String continueButton = "loancreationdetails.button.continue";

    public void verifyPage() {
        this.verifyPage("LoanCreationDetail");
    }

    public void verifyAdditionalFeesAreEmpty() {
        Assert.assertEquals(selenium.getSelectedValue("selectedFeeId[0]"), "");
        Assert.assertEquals(selenium.getValue("selectedFeeAmount[0]"), "");

        Assert.assertEquals(selenium.getSelectedValue("selectedFeeId[1]"), "");
        Assert.assertEquals(selenium.getValue("selectedFeeAmount[1]"), "");

        Assert.assertEquals(selenium.getSelectedValue("selectedFeeId[2]"), "");
        Assert.assertEquals(selenium.getValue("selectedFeeAmount[2]"), "");
    }

    public CreateLoanAccountEntryPage(Selenium selenium) {
        super(selenium);
        verifyPage("LoanCreationDetail");
    }

    public CreateLoanAccountConfirmationPage submitAndNavigateToLoanAccountConfirmationPage(CreateLoanAccountSubmitParameters formParameters) {
        CreateLoanAccountReviewInstallmentPage reviewInstallmentsPage = submitAndNavigateToLoanReviewInstallmentsPage(formParameters);
        CreateLoanAccountPreviewPage previewPage = reviewInstallmentsPage.clickPreviewAndNavigateToPreviewPage();
        return previewPage.submitForApprovalAndNavigateToConfirmationPage();
//        return navigateToConfirmationPage();
    }

    public QuestionResponsePage submitAndNavigateToQuestionResponsePage() {
        submitAndWaitForPage();
        return new QuestionResponsePage(selenium);
    }

    public CreateLoanAccountConfirmationPage submitAndNavigateToLoanAccountConfirmationPage(CreateLoanAccountSubmitParameters formParameters,
                                                                                            QuestionResponseParameters responseParameters) {
        submitLoanAccount(formParameters);
        populateCreateLoanQuestionResponsesIfNeeded(responseParameters, selenium.getAttribute("page.id@title"));
        return navigateToConfirmationPage();
    }

    private void populateCreateLoanQuestionResponsesIfNeeded(QuestionResponseParameters responseParameters, String pageId) {
        if (StringUtils.equalsIgnoreCase(pageId, "captureQuestionResponse")) {
            QuestionResponsePage responsePage = new QuestionResponsePage(selenium);
            responsePage.populateAnswers(responseParameters);
            responsePage.navigateToNextPage();
        }
    }

    private CreateLoanAccountConfirmationPage navigateToConfirmationPage() {
        selenium.click("schedulePreview.button.preview");
        waitForPageToLoad();
        selenium.isVisible("createloanpreview.button.submitForApproval");
        selenium.click("createloanpreview.button.submitForApproval");
        waitForPageToLoad();
        return new CreateLoanAccountConfirmationPage(selenium);
    }

    private CreateLoanAccountConfirmationPage navigateToConfirmationPageSaveForLaterButton() {
        selenium.click("schedulePreview.button.preview");
        waitForPageToLoad();
        selenium.isVisible("createloanpreview.button.saveForLater");
        selenium.click("createloanpreview.button.saveForLater");
        waitForPageToLoad();
        return new CreateLoanAccountConfirmationPage(selenium);
    }

    public CreateLoanAccountReviewInstallmentPage submitAndNavigateToLoanReviewInstallmentsPage(CreateLoanAccountSubmitParameters formParameters) {
        submitLoanAccount(formParameters);
        return new CreateLoanAccountReviewInstallmentPage(selenium).verifyPage();
    }

    private void submitLoanAccount(CreateLoanAccountSubmitParameters formParameters) {
        if(formParameters.getAmount() != null){
            selenium.type("loancreationdetails.input.sumLoanAmount",formParameters.getAmount());
        }
        if (formParameters.isGracePeriodTypeNone()) {
            Assert.assertFalse(selenium.isEditable("loancreationdetails.input.gracePeriod"));
        }
        if (formParameters.getLsimFrequencyWeeks() != null)
        {
            selenium.click("loancreationdetails.input.frequencyWeeks");
            selenium.type("loancreationdetails.input.weekFrequency",formParameters.getLsimWeekFrequency());
            selenium.select("weekDay", "label=Friday");
        }
        if (formParameters.getLsimMonthTypeDayOfMonth() != null)
        {
            selenium.click("montlyOption0");
            selenium.type("loancreationdetails.input.dayOfMonth", formParameters.getLsimDayOfMonth());
        }
        if (formParameters.getLsimMonthTypeNthWeekdayOfMonth() != null)
        {
            selenium.click("montlyOption1");
            selenium.select("monthRank", formParameters.getLsimMonthRank());
            selenium.select("monthWeek", formParameters.getLsimWeekDay());
        }
        if (formParameters.getDd() != null && formParameters.getMm() != null && formParameters.getYy() != null){
            setDisbursalDate(formParameters.getDd(), formParameters.getMm(), formParameters.getYy());
        }
        if(formParameters.getLoanPurpose() != null){
        	selenium.select("loanPurposeId", "label="+formParameters.getLoanPurpose());
        }
        fillAdditionalFee(formParameters);
        submitAndWaitForPage();
    }

    public CreateLoanAccountEntryPage fillAdditionalFee(CreateLoanAccountSubmitParameters formParameters){
        if(formParameters != null)
        {
            if(formParameters.getAdditionalFee1()!=null) {
                selenium.select("selectedFeeId0", "label=" + formParameters.getAdditionalFee1());
            }
            if(formParameters.getAdditionalFee2()!=null) {
                selenium.select("selectedFeeId1", "label=" + formParameters.getAdditionalFee2());
            }
            if(formParameters.getAdditionalFee3()!=null) {
                selenium.select("selectedFeeId2", "label=" + formParameters.getAdditionalFee3());
            }
        }
        return this;
    }

    public CreateLoanAccountConfirmationPage submitAndNavigateToGLIMLoanAccountConfirmationPage() {
        submitAndWaitForPage();
        CreateLoanAccountReviewInstallmentPage reviewInstallmentPage = new CreateLoanAccountReviewInstallmentPage(this.selenium).verifyPage();
        CreateLoanAccountPreviewPage previewPage = reviewInstallmentPage.clickPreviewAndNavigateToPreviewPage();
        return previewPage.submitForApprovalAndNavigateToConfirmationPage();
//        return navigateToConfirmationPage();
    }

    public CreateLoanAccountConfirmationPage submitAndNavigateToGLIMLoanAccountConfirmationPageSaveForLaterButton() {
        submitAndWaitForPage();
        CreateLoanAccountReviewInstallmentPage reviewInstallmentPage = new CreateLoanAccountReviewInstallmentPage(this.selenium).verifyPage();
        CreateLoanAccountPreviewPage previewPage = reviewInstallmentPage.clickPreviewAndNavigateToPreviewPage();
        return previewPage.submitForLaterAndNavigateToConfirmationPage();
//        submitAndWaitForPage();
//        return navigateToConfirmationPageSaveForLaterButton();
    }

    public CreateLoanAccountCashFlowPage submitAndNavigateToCreateLoanAccountCashFlowPage() {
        submitAndWaitForPage();
        return new CreateLoanAccountCashFlowPage(selenium);
    }

    public void submitAndWaitForPage() {
        selenium.click(continueButton);
        waitForPageToLoad();
    }

    public LoanAccountPage continuePreviewSubmitAndNavigateToDetailsPage() {
        submitAndWaitForPage();
        selenium.click("schedulePreview.button.preview");
        waitForPageToLoad();
        selenium.click("createloanpreview.button.submitForApproval");
        waitForPageToLoad();
        selenium.click("CreateLoanAccountConfirmation.link.viewLoanDetails");
        waitForPageToLoad();
        return new LoanAccountPage(selenium);
    }

    public HomePage navigateToHomePage(){
        selenium.click("id=header.link.home");
        waitForPageToLoad();
        return new HomePage(selenium);
    }

    public void selectAdditionalFees() {
        selenium.select("selectedFeeId0", "label=oneTimeFee");
        selenium.type("selectedFeeId0Amount", "6.6");

        selenium.select("selectedFeeId1", "label=oneTimeFee");
        selenium.type("selectedFeeId1Amount", "3.3");
    }

    public void unselectAdditionalFee() {
        selenium.select("selectedFeeId1", "label=--Select--");
    }
    
    public CreateLoanAccountEntryPage unselectAdditionalFees() {
    	for(int i=0; i<3; i++){
    		selenium.select("selectedFeeId"+i, "label=--Select--");
    	}
    	return this;
    }
    
    public CreateLoanAccountEntryPage applyAdditionalFees(String[] fees) {
    	for(int i=0; i<fees.length && i<3; i++){
    		selenium.select("selectedFeeId"+i, "label="+fees[i]);
    	}
    	return this;
    }
    
    public CreateLoanAccountEntryPage submitWithErrors(List<String> errors) {
    	submitAndWaitForPage();
    	for (String error : errors) {
    		selenium.isTextPresent(error);
		}
        return this;
    }

    public void selectTwoClientsForGlim() {
        selenium.click("clientSelectForGroup[0]");
        selenium.type("clientAmount[0]", "1234");

        selenium.click("clientSelectForGroup[1]");
        selenium.type("clientAmount[1]", "4321");
    }

    public void selectPurposeForGlim() {
        selenium.select("clientLoanPurposeId[0]", "label=0003-Goat Purchase");
        selenium.select("clientLoanPurposeId[1]", "label=0010-Camel");
    }

    public void selectGLIMClients(int clientNumber, String expectedClientName, String loanAmount) {
        selectGLIMClients(clientNumber, expectedClientName, loanAmount, null);
    }

    public void selectGLIMClients(int clientNumber, String expectedClientName, String loanAmount, String loanPurpose) {
        String clientName = selenium.getText("GLIMLoanAccounts.clientName." + clientNumber);
        String clientId = selenium.getText("GLIMLoanAccounts.clientId." + clientNumber);
        Assert.assertEquals(clientName + " Client Id: " + clientId, expectedClientName);
        selenium.check("clientSelectForGroup[" + clientNumber + "]");
        selenium.type("clientAmount[" + clientNumber + "]", loanAmount);
        if(loanPurpose!=null){
            selenium.select("clientLoanPurposeId[" + clientNumber + "]", "label=" + loanPurpose);
        }
    }

    public CreateLoanAccountReviewInstallmentPage clickContinue(){
        submitAndWaitForPage();
        return new CreateLoanAccountReviewInstallmentPage(selenium);
    }
    
    public CreateLoanAccountEntryPage clickContinueButExpectValidationFailure(){
        submitAndWaitForPage();
        return new CreateLoanAccountEntryPage(selenium);
    }

    public CreateLoanAccountConfirmationPage clickContinueAndNavigateToLoanAccountConfirmationPage() {
        submitAndWaitForPage();
        return navigateToConfirmationPage();
    }

    public void checkTotalAmount(String expectedTotalAmount) {
        Assert.assertEquals(selenium.getValue("glimsumloanamount"), expectedTotalAmount);
    }

    @SuppressWarnings("PMD")
    public CreateLoanAccountEntryPage verifyVariableInstalmentsInLoanProductSummery(String maxGap, String minGap, String minInstalmentAmount) {
        String expectedMaximumGap = "Maximum gap between installments:";
        boolean expectedMaximumGapResult = selenium.isTextPresent(expectedMaximumGap);
        if (!expectedMaximumGapResult) {
            Assert.fail(expectedMaximumGap + " was expected but not found.");
        }
        
        String expectedMaximumGapNumber = maxGap;
        if ("".equals(maxGap)) {
            expectedMaximumGapNumber = "N/A";
        } else {
            boolean expectedMaximumGapNumberResult = selenium.isTextPresent(expectedMaximumGapNumber);
            if (!expectedMaximumGapNumberResult) {
                Assert.fail(expectedMaximumGapNumber + " was expected but not found.");
            }            
        }
        
        String expectedMinimumGap = "Minimum gap between installments:";
        boolean expectedMinimumGapResult = selenium.isTextPresent(expectedMinimumGap);
        if (!expectedMinimumGapResult) {
            Assert.fail(expectedMinimumGap + " was expected but not found.");
        }
        
        String expectedMinimumGapNumber = minGap;
        boolean expectedMinimumGapNumberResult = selenium.isTextPresent(expectedMinimumGapNumber);
        if (!expectedMinimumGapNumberResult) {
            Assert.fail(expectedMinimumGapNumber + " was expected but not found.");
        }
        
        String expectedCanConfigure = "Can configure variable installments:";
        boolean canConfigureFound = selenium.isTextPresent(expectedCanConfigure);
        if (!canConfigureFound) {
            Assert.fail(expectedCanConfigure + " was expected but not found.");
        }
        
        String expectedCanConfigureValue = "Yes";
        boolean expectedCanConfigureValueFound = selenium.isTextPresent(expectedCanConfigureValue);
        if (!expectedCanConfigureValueFound) {
            Assert.fail(expectedCanConfigureValueFound + " was expected but not found.");
        }

//        String expectedMinInstalmentAmount = "Minimum installment amount:";
//        boolean expectedMinInstalmentAmountResult = selenium.isTextPresent(expectedMinInstalmentAmount);
//        if (!expectedMinInstalmentAmountResult) {
//            Assert.fail(expectedMinInstalmentAmount + " was expected but not found.");
//        }
//
//        String expectedMinInstalmentAmountNumber = minInstalmentAmount;
//        if ("".equals(minInstalmentAmount)) {
//            expectedMinInstalmentAmountNumber = "N/A";
//        } else {
//            boolean expectedMinInstalmentAmountNumberResult = selenium.isTextPresent(expectedMinInstalmentAmountNumber);
//            if (!expectedMinInstalmentAmountNumberResult) {
//                Assert.fail(expectedMinInstalmentAmountNumber + " was expected but not found.");
//            }            
//        }

        return this;
    }

    public CreateLoanAccountEntryPage verifyUncheckedVariableInstalmentsInLoanProductSummery() {
        Assert.assertTrue(!selenium.isTextPresent("Minimum gap between installments:"));
        Assert.assertTrue(!selenium.isTextPresent("Maximum gap between installments:"));
        Assert.assertTrue(!selenium.isTextPresent("Minimum installment amount:" )) ;
        Assert.assertTrue(!selenium.isTextPresent("Can configure variable installments: No"));
        return this;

    }

    public CreateLoanAccountEntryPage setDisbursalDate(DateTime validDisbursalDate) {
        String dd = DateTimeFormat.forPattern("dd").print(validDisbursalDate);
        String mm = DateTimeFormat.forPattern("MM").print(validDisbursalDate);
        String yyyy = DateTimeFormat.forPattern("yyyy").print(validDisbursalDate);
        setDisbursalDate(dd, mm, yyyy);
        return this;
    }

    public void setDisbursalDate(String dd, String mm, String yy) {
        selenium.type("disbursementDateDD",dd);
        selenium.fireEvent("name=disbursementDateDD", "blur");

        selenium.type("disbursementDateMM",mm);
        selenium.fireEvent("name=disbursementDateMM", "blur");

        selenium.type("disbursementDateYY",yy);
        selenium.fireEvent("name=disbursementDateYY", "blur");
    }
    
    public CreateLoanAccountCashFlowPage clickContinueToNavigateToCashFlowPage() {
        selenium.click(continueButton);
        selenium.waitForPageToLoad("3000");
        return new CreateLoanAccountCashFlowPage(selenium);
    }
    
    public CreateLoanAccountEntryPage setInstallments(int noOfInstallment) {
        typeText("numberOfInstallments", String.valueOf(noOfInstallment));
        return this;
    }

    public CreateLoanAccountEntryPage verifyInterestTypeInLoanCreation(String interestTypeName) {
        if(!selenium.isTextPresent("Interest rate type:")) {
            Assert.fail("Interest rate type: was expected but not found on page: " + selenium.getLocation());
        }
        
        if(!selenium.isTextPresent(interestTypeName)) {
            Assert.fail(interestTypeName + " was expected but not found on page: " + selenium.getLocation());
        }
        Assert.assertTrue(selenium.isTextPresent(interestTypeName));
        return this;
    }

    public CreateLoanAccountEntryPage verifyInvalidFeeBlocked(String[] fees) {
        for (int index = 0; index < fees.length; index++) {
            String fee = fees[index];
            selenium.select("selectedFeeId" + index, fee);
        }
        submitAndWaitForPage();
        for (String fee : fees) {
            String expectedErrorText = fee + " fee cannot be applied to loan with variable installments";
            if (!selenium.isTextPresent(expectedErrorText)) {
                Assert.fail(expectedErrorText + " was expected but not found on " + selenium.getLocation() + " with the following source <br/> " + selenium.getHtmlSource());
            }
        }
        for (int index = 0; index < fees.length; index++) {
            selenium.select("selectedFeeId" + index,"--Select--");
        }
        return this;
    }

    @SuppressWarnings("PMD")
    public void verifyAllowedAmounts(String min, String max, String def) {
        min = min.substring(0, min.length()-2);
        max = max.substring(0, max.length()-2);
        final String expectedText = "(Allowed amount: " + min + " - " + max +")";
        final String expectedText2 = "(Allowed amount: " + min + ".0 - " + max +".0)";

        if (!selenium.isElementPresent("//span[@id='createloan.allowedamounttext']")) {
            Assert.fail("failed as span with id: createloan.allowedamounttext not on page: " +  selenium.getLocation());
        } else {
            String allowedAmountText = selenium.getText("//span[@id='createloan.allowedamounttext']");
        
            if (allowedAmountText.equalsIgnoreCase(expectedText) || allowedAmountText.equalsIgnoreCase(expectedText2)) {
                Assert.assertTrue(true);
            } else {
                Assert.fail(expectedText + " or " + expectedText2 + " was expected but not found on page. instead was: " + allowedAmountText);
            }
        
            Assert.assertEquals(selenium.getValue("loancreationdetails.input.sumLoanAmount"), def);
        }
    }

    public void verifyAllowedInterestRate(String min, String max, String def) {
        min = min.substring(0, min.length()-2);
        max = max.substring(0, max.length()-2);
        final String expectedText = "(Allowed interest rate: " + min + " - " + max + " %)";
        final String expectedText2 = "(Allowed interest rate: " + min + ".0 - " + max + ".0 %)";
        if (selenium.isTextPresent(expectedText) || selenium.isTextPresent(expectedText2)) {
            Assert.assertTrue(true);
        } else {
            Assert.fail(expectedText + " was expected but not found on page.");
        }
        Assert.assertEquals(selenium.getValue("loancreationdetails.input.interestRate"), def);
    }

    public void verifyAllowedInstallments(String min, String max, String def) {
        String expectedInstallmentText = "(Allowed number of installments: " + min + " - " + max + ")";
        if (selenium.isTextPresent(expectedInstallmentText)) {
            Assert.assertTrue(true);
        } else {
            Assert.fail(expectedInstallmentText + " was expected but not found on page.");
        }
        Assert.assertEquals(selenium.getValue("loancreationdetails.input.numberOfInstallments"), def);
    }

    public String getLoanAmount() {
        return selenium.getValue("loancreationdetails.input.sumLoanAmount");
    }
    
    public void verifyError(String error) {
        Assert.assertTrue(selenium.isElementPresent("//span[@id='loancreationdetails.error.message']/div/ul/li/span[text()='"+error+"']"));
    }
    
    public CreateLoanAccountReviewInstallmentPage navigateToReviewInstallmentsPage(){
        selenium.click("loancreationdetails.button.continue");
        waitForPageToLoad();
        return new CreateLoanAccountReviewInstallmentPage(selenium);
    }
            
    public ClientsAndAccountsHomepage cancel(){
        selenium.click("_eventId_cancel");
        waitForPageToLoad();
        return new ClientsAndAccountsHomepage(selenium);
    }
            
    public void setAmount(String amount) {
        selenium.type("loancreationdetails.input.sumLoanAmount", amount);
    }
    
    public void setInterestRate(String interestRate) {
        selenium.type("loancreationdetails.input.interestRate", interestRate);
    }
    
    public CreateLoanAccountEntryPage setInstallments(String noOfInstallment) {
        typeText("numberOfInstallments",noOfInstallment);
        return this;
    }
    
    public void verifyNoError(String error) {
        Assert.assertFalse(selenium.isElementPresent("//span[@id='loancreationdetails.error.message']/div/ul/li/span[text()='"+error+"']"));
    }
    
    public void verifyDisbsursalDate(String dd, String mm, String yyyy) {
        Assert.assertEquals(selenium.getValue("disbursementDateDD") + "-" + selenium.getValue("disbursementDateMM") + "-" + selenium.getValue("disbursementDateYY") , dd+ "-" +mm+ "-"+yyyy);
    }
}
