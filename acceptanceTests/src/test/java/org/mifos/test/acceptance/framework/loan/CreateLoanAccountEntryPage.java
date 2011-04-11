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

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
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
        Assert.assertEquals(selenium.getSelectedValue("selectedFee[0].feeId"), "");
        Assert.assertEquals(selenium.getValue("selectedFee[0].amount"), "");

        Assert.assertEquals(selenium.getSelectedValue("selectedFee[1].feeId"), "");
        Assert.assertEquals(selenium.getValue("selectedFee[1].amount"), "");

        Assert.assertEquals(selenium.getSelectedValue("selectedFee[2].feeId"), "");
        Assert.assertEquals(selenium.getValue("selectedFee[2].amount"), "");
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
        submit();
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
        waitForPageToLoad();
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
            selenium.click("loancreationdetails.input.monthType1");
            selenium.type("loancreationdetails.input.dayOfMonth", formParameters.getLsimDayOfMonth());
        }
        if (formParameters.getLsimMonthTypeNthWeekdayOfMonth() != null)
        {
            selenium.click("loancreationdetails.input.monthType2");
            selenium.select("monthRank", formParameters.getLsimMonthRank());
            selenium.select("monthWeek", formParameters.getLsimWeekDay());
        }
        if (formParameters.getDd() != null && formParameters.getMm() != null && formParameters.getYy() != null){
            setDisbursalDate(formParameters.getDd(), formParameters.getMm(), formParameters.getYy());
        }
        fillAdditionalFee(formParameters);
        submit();
    }

    public void fillAdditionalFee(CreateLoanAccountSubmitParameters formParameters){
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

    public CreateLoanAccountConfirmationPage submitAndNavigateToGLIMLoanAccountConfirmationPage() {
        submit();
        return navigateToConfirmationPage();

    }

    public CreateLoanAccountConfirmationPage submitAndNavigateToGLIMLoanAccountConfirmationPageSaveForLaterButton() {
        submit();
        return navigateToConfirmationPageSaveForLaterButton();

    }

    public CreateLoanAccountCashFlowPage submitAndNavigateToCreateLoanAccountCashFlowPage() {
        submit();
        return new CreateLoanAccountCashFlowPage(selenium);
    }

    public void submit() {
        selenium.click(continueButton);
        waitForPageToLoad();
    }

    public LoanAccountPage continuePreviewSubmitAndNavigateToDetailsPage() {
        submit();
        selenium.click("schedulePreview.button.preview");
        waitForPageToLoad();
        selenium.click("createloanpreview.button.submitForApproval");
        waitForPageToLoad();
        selenium.click("CreateLoanAccountConfirmation.link.viewLoanDetails");
        waitForPageToLoad();
        return new LoanAccountPage(selenium);
    }

    public HomePage navigateToHomePage(){
        selenium.click("id=clientsAndAccountsHeader.link.home");
        waitForPageToLoad();
        return new HomePage(selenium);
    }

    public void selectAdditionalFees() {
        selenium.select("selectedFeeId0", "label=One Time Upfront Fee");
        selenium.type("selectedFeeId0Amount", "6.6");

        selenium.select("selectedFeeId1", "label=One Time Upfront Fee");
        selenium.type("selectedFeeId1Amount", "3.3");
    }

    public void unselectAdditionalFee() {
        selenium.select("selectedFeeId1", "label=--Select--");
    }

    public void selectTwoClientsForGlim() {
        selenium.click("selectAll");
        selenium.type("clientAmount[0]", "1234");

        selenium.click("clients[1]");
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
        Assert.assertEquals(selenium.getText("GLIMLoanAccounts.clientName." + clientNumber), expectedClientName);
        selenium.check("clients[" + clientNumber + "]");
        selenium.type("clientDetails[" + clientNumber + "].loanAmount", loanAmount);
        if(loanPurpose!=null){
            selenium.select("clientDetails[" + clientNumber + "].businessActivity", "label=" + loanPurpose);
        }
    }

    public CreateLoanAccountReviewInstallmentPage clickContinue(){
        submit();
        return  new CreateLoanAccountReviewInstallmentPage(selenium);
    }

    public CreateLoanAccountConfirmationPage clickContinueAndNavigateToLoanAccountConfirmationPage() {
        submit();
        return navigateToConfirmationPage();

    }

    public void checkTotalAmount(String expectedTotalAmount) {
        Assert.assertEquals(selenium.getValue("sumLoanAmount"), expectedTotalAmount);
    }

    public CreateLoanAccountEntryPage verifyVariableInstalmentsInLoanProductSummery(String maxGap, String minGap, String minInstalmentAmount) {
        if ("".equals(maxGap)) {
            Assert.assertTrue(selenium.isTextPresent("Maximum gap between installments: N/A"));
        } else {
            String expectedMaximumGap = "Maximum gap between installments: " + maxGap;
            boolean result = selenium.isTextPresent(expectedMaximumGap);
            if (!result) {
                Assert.fail(expectedMaximumGap + " was expected but not found in, " + selenium.getHtmlSource());
            }
        }
        if ("".equals(minInstalmentAmount)) {
            Assert.assertTrue(selenium.isTextPresent("Minimum installment amount: N/A")) ;
        } else {
            Assert.assertTrue(selenium.isTextPresent("Minimum installment amount: " + minInstalmentAmount)) ;
        }
        Assert.assertTrue(selenium.isTextPresent("Minimum gap between installments: " + minGap));
        Assert.assertTrue(selenium.isTextPresent("Can configure variable installments: Yes"));
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

    private void setDisbursalDate(String dd, String mm, String yy) {
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
        typeText("numberOfInstallments",String.valueOf(noOfInstallment));
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
        submit();
        for (String fee : fees) {
            Assert.assertTrue(selenium.isTextPresent(fee + " fee cannot be applied to loan with variable installments"));
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
        Assert.assertTrue(selenium.isElementPresent("//span[@id='loancreationdetails.error.message']/li[text()='"+error+"']"));
    }
}
