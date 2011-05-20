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

import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;


public class EditLoanAccountInformationPage extends MifosPage {
    public EditLoanAccountInformationPage(Selenium selenium) {
        super(selenium);
        this.verifyPage("EditLoanAccount");
    }

    public EditPreviewLoanAccountPage submitAndNavigateToAccountInformationPreviewPage() {

        selenium.click("editLoanAccount.button.preview");

        waitForPageToLoad();
        return new EditPreviewLoanAccountPage(selenium);
    }

    public EditLoanAccountInformationPage submitWithErrors() {

        selenium.click("editLoanAccount.button.preview");

        waitForPageToLoad();
        return new EditLoanAccountInformationPage(selenium);
    }

    public  void editExternalID(EditLoanAccountInformationParameters params) {
        selenium.type("externalId", params.getExternalID());
    }


    public EditLoanAccountInformationPage editAccountParams(CreateLoanAccountSubmitParameters accountSubmitParameters, EditLoanAccountInformationParameters editAccountParameters) {
        if (accountSubmitParameters.getAmount() != null) {
            selenium.type("editLoanAccount.input.loanAmount", accountSubmitParameters.getAmount());
        }
        if (editAccountParameters.getGracePeriod() != null) {
            selenium.type("editLoanAccount.input.gracePeriod", editAccountParameters.getGracePeriod());
        }
        if (editAccountParameters.getCollateralNotes() != null) {
            selenium.type("editLoanAccount.textbox.collateralnotes", editAccountParameters.getCollateralNotes());
        }
        if (editAccountParameters.getCollateralType() != null) {
            selenium.select("editLoanAccount.select.collateraltype", "label=" + editAccountParameters.getCollateralType());
        }
        if (editAccountParameters.getExternalID() != null) {
            selenium.type("editLoanAccount.input.externalid", editAccountParameters.getExternalID());
        }
        if (editAccountParameters.getPurposeOfLoan() != null) {
            selenium.select("editLoanAccount.input.purposeofloan", "label=" + editAccountParameters.getPurposeOfLoan());
        }
        return this;
    }

    public void verifyGLIMClient(int clientNumber, String expectedClientName, String loanAmount, String loanPurpose){
        Assert.assertEquals(selenium.getText("GLIMLoanAccounts.clientName." + clientNumber), expectedClientName);
        selenium.isChecked("clients[" + clientNumber + "]");
        Assert.assertEquals(selenium.getValue("clientDetails[" + clientNumber + "].loanAmount"), loanAmount);
        Assert.assertEquals(selenium.getSelectedLabel("clientDetails[" + clientNumber + "].businessActivity"), loanPurpose);
    }

    public void verifyErrorInForm(String error) {
        Assert.assertTrue(selenium.isTextPresent(error));
    }
    
    public void verifyLoanAmount(String loanAmount) {
        Assert.assertEquals(selenium.getValue("editLoanAccount.input.loanAmount"), loanAmount);
    }
    
    public void verifyInterestRate(String interestRate) {
        Assert.assertEquals(selenium.getValue("editLoanAccount.input.interestRate"), interestRate);
    }
    
    public void verifyInstallments(String installments) {
        Assert.assertEquals(selenium.getValue("editLoanAccount.input.numberOfInstallments"), installments);
    }
    
    public void verifyDisbursalDate(String disbursalDateDD, String disbursalDateMM, String disbursalDateYYYY) {
        Assert.assertEquals(selenium.getValue("disbursementDateDD"), disbursalDateDD);
        Assert.assertEquals(selenium.getValue("disbursementDateMM"), disbursalDateMM);
        Assert.assertEquals(selenium.getValue("disbursementDateYY"), disbursalDateYYYY);
    }
}
