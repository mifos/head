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

package org.mifos.test.acceptance.framework.loan;

import com.thoughtworks.selenium.Selenium;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.mifos.test.acceptance.framework.AbstractPage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;
import org.testng.Assert;

public class CreateLoanAccountEntryPage extends AbstractPage {

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
        submitAndNavigateToLoanPreviewPage(formParameters);
        return navigateToConfirmationPage();
    }

    public CreateLoanAccountConfirmationPage submitAndNavigateToLoanAccountConfirmationPage(CreateLoanAccountSubmitParameters formParameters,
                                                                                            QuestionResponseParameters responseParameters) {
        submitLoanAccount(formParameters);
        populateQuestionResponsesIfNeeded(responseParameters, selenium.getAttribute("page.id@title"));
        return navigateToConfirmationPage();
    }

    private void populateQuestionResponsesIfNeeded(QuestionResponseParameters responseParameters, String pageId) {
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

    public ViewInstallmentDetailsPage submitAndNavigateToLoanPreviewPage(CreateLoanAccountSubmitParameters formParameters) {
        submitLoanAccount(formParameters);
        return new ViewInstallmentDetailsPage(selenium);
    }

    private void submitLoanAccount(CreateLoanAccountSubmitParameters formParameters) {
        selenium.type("loancreationdetails.input.sumLoanAmount",formParameters.getAmount());
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

        submit();
    }

    public CreateLoanAccountConfirmationPage submitAndNavigateToGLIMLoanAccountConfirmationPage() {
        submit();
        return navigateToConfirmationPage();

    }

    public CreateLoanAccountConfirmationPage submitAndNavigateToGLIMLoanAccountConfirmationPageSaveForLaterButton() {
        submit();
        return navigateToConfirmationPageSaveForLaterButton();

    }

    private void submit() {
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
        selenium.select("selectedFee[0].feeId", "label=One Time Upfront Fee");
        selenium.type("loancreationdetails.input.feeAmount", "6.6");

        selenium.select("selectedFee[1].feeId", "label=One Time Upfront Fee");
        selenium.type("selectedFee[1].amount", "3.3");
    }

    public void selectTwoClientsForGlim() {
        selenium.click("glimLoanForm.input.select");
        selenium.type("glimLoanForm.input.loanAmount", "1234");

        selenium.click("clients[1]");
        selenium.type("clientDetails[1].loanAmount", "4321");
    }

    public void selectPurposeForGlim() {
        selenium.select("clientDetails[0].businessActivity", "label=0003-Goat Purchase");

        selenium.select("clientDetails[1].businessActivity", "label=0010-Camel");
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
        selenium.isVisible("schedulePreview.button.preview");
        return  new CreateLoanAccountReviewInstallmentPage(selenium);
    }

    public CreateLoanAccountConfirmationPage clickContinueAndNavigateToLoanAccountConfirmationPage() {
        submit();
        return navigateToConfirmationPage();

    }

//    public CreateLoanAccountConfirmationPage clickContinue() {
//        selenium.click("loancreationdetails.button.continue");
//        waitForPageToLoad();
//        selenium.isVisible("schedulePreview.button.preview");
//        return new CreateLoanAccountConfirmationPage(selenium);
//    }

    public void checkTotalAmount(String expectedTotalAmount) {
        Assert.assertEquals(selenium.getValue("sumLoanAmount"), expectedTotalAmount);
    }

    public CreateLoanAccountEntryPage verifyVariableInstalmentsInLoanProductSummery(String maxGap, String minGap, String minInstalmentAmount) {
        if ("".equals(maxGap)) {
            Assert.assertTrue(selenium.isTextPresent("Maximum gap between installments: N/A"));
        } else {
            Assert.assertTrue(selenium.isTextPresent("Maximum gap between installments: " + maxGap  + " days"));
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

        selenium.type("disbursementDateDD",dd);
        selenium.fireEvent("name=disbursementDateDD", "blur");

        selenium.type("disbursementDateMM",mm);
        selenium.fireEvent("name=disbursementDateMM", "blur");

        selenium.type("disbursementDateYY",yyyy);
        selenium.fireEvent("name=disbursementDateYY", "blur");

        return this;
    }

    public CreateLoanAccountCashFlowPage clickContinueToNavigateToCashFlowPage() {
        selenium.click(continueButton);
        selenium.waitForPageToLoad("3000");
        return new CreateLoanAccountCashFlowPage(selenium);
    }

    public CreateLoanAccountEntryPage setInstallments(int noOfInstallment) {
        typeText("noOfInstallments",String.valueOf(noOfInstallment));
        return this;
    }

    public CreateLoanAccountEntryPage verifyInterestTypeInLoanCreation(String interestTypeName) {
        Assert.assertTrue(selenium.isTextPresent("Interest Rate Type:"));
        Assert.assertTrue(selenium.isTextPresent(interestTypeName));
        return this;
    }

    public CreateLoanAccountEntryPage verifyInvalidFeeBlocked(String[] fees) {
        for (int index = 0; index < fees.length; index++) {
            String fee = fees[index];
            selenium.select("selectedFee[" + index + "].feeId",fee);
        }
        submit();
        for (String fee : fees) {
            Assert.assertTrue(selenium.isTextPresent(fee + " fee cannot be applied to loan with variable installments"));
        }
        for (int index = 0; index < fees.length; index++) {
            selenium.select("selectedFee[" + index + "].feeId","--Select--");
        }
        return this;
    }
}
