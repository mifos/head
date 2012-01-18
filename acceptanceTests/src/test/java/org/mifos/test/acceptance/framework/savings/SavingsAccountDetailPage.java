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

package org.mifos.test.acceptance.framework.savings;

import org.mifos.test.acceptance.util.StringUtil;
import org.mifos.test.acceptance.framework.AbstractPage;
import org.mifos.test.acceptance.framework.loan.AccountAddNotesPage;
import org.mifos.test.acceptance.framework.loan.AccountNotesPage;
import org.mifos.test.acceptance.framework.loan.AccountChangeStatusPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.loan.AttachSurveyPage;
import org.mifos.test.acceptance.framework.questionnaire.ViewQuestionResponseDetailPage;

@SuppressWarnings("PMD.SystemPrintln")
public class SavingsAccountDetailPage extends AbstractPage {

    public SavingsAccountDetailPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("savingsaccountdetail");
    }

    public String getTotalAmountDue(){
        return selenium.getText("savingsaccountdetail.text.totalAmountDue");
    }

    public void verifyTotalAmountDue(String totalAmountDue){
        Assert.assertEquals(getTotalAmountDue(), totalAmountDue);
    }

    public void verifySavingsAmount(String amount) {
        Assert.assertTrue(selenium.isTextPresent(StringUtil.formatNumber(amount)));
    }

    public void verifyDate(String date){
        Assert.assertTrue(selenium.isTextPresent(date));
    }

    public void verifySavingsProduct(String savingsProduct) {
        Assert.assertTrue(selenium.isTextPresent(savingsProduct));
    }
    public void verifyStatus(String status){
        Assert.assertEquals(selenium.getText("savingsaccountdetail.status.text"),status);
    }
    public AccountAddNotesPage navigateToAddNotesPage() {
        selenium.click("savingsaccountdetail.link.addANotes");
        waitForPageToLoad();
        return new AccountAddNotesPage(selenium);
    }

    public ViewDepositDueDetailsPage navigateToViewDepositDueDetails(){
        selenium.click("savingsaccountdetail.link.viewDepositDueDetails");
        waitForPageToLoad();
        return new ViewDepositDueDetailsPage(selenium);
    }

    public AccountNotesPage navigateToAccountNotesPage() {
        selenium.click("savingsaccountdetail.link.seeAllNotes");
        waitForPageToLoad();
        return new AccountNotesPage(selenium);
    }

    public SavingsDepositWithdrawalPage navigateToDepositWithdrawalPage() {
        selenium.click("savingsaccountdetail.link.makeDepositWithdrawal");
        waitForPageToLoad();
        return new SavingsDepositWithdrawalPage(selenium);
    }

    public SavingsCloseAccountPage navigateToCloseAccount() {
        selenium.click("savingsaccountdetail.link.closeAccount");
        waitForPageToLoad();
        return new SavingsCloseAccountPage(selenium);
    }

    public AccountChangeStatusPage navigateToEditAccountStatus() {
        selenium.click("savingsaccountdetail.link.editAccountStatus");
        waitForPageToLoad();
        return new AccountChangeStatusPage(selenium);
    }

    public String getAccountId() {
            return selenium.getText("savingsaccountdetail.text.savingsId");
    }

    public SavingsApplyAdjustmentPage navigateToApplyAdjustmentPage() {
        selenium.click("savingsaccountdetail.link.applyAdjustment");
        waitForPageToLoad();
        return new SavingsApplyAdjustmentPage(selenium);
    }

    public AttachSurveyPage navigateToAttachSurveyPage() {
        selenium.click("link=Attach a Question Group");
        waitForPageToLoad();
        return new AttachSurveyPage(selenium);
    }

    public ViewQuestionResponseDetailPage navigateToLatestViewQuestionResponseDetailPage(String questionGroupName) {
        int linkID = Integer.parseInt(selenium.getAttribute("link="+questionGroupName+"@id"));
        linkID++;
        if(!selenium.isElementPresent("id="+linkID)) {
            linkID--;
        }
        selenium.click("id="+linkID);
        waitForPageToLoad();
        return new ViewQuestionResponseDetailPage(selenium);
    }
    
    public TransactionHistoryPage navigateToTransactionHistoryPage() {
        selenium.click("savingsaccountdetail.link.viewTransactionHistory");
        waitForPageToLoad();
        
        return new TransactionHistoryPage(selenium);
    }
    
}