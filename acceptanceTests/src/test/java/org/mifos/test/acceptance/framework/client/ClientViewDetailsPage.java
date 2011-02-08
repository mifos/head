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

package org.mifos.test.acceptance.framework.client;

import static java.lang.String.format;

import java.util.LinkedHashMap;
import java.util.Map;

import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.customer.CustomerChangeStatusPage;
import org.mifos.test.acceptance.framework.loan.AttachSurveyPage;
import org.mifos.test.acceptance.framework.loan.ClosedAccountsPage;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.PerformanceHistoryAtributes;
import org.mifos.test.acceptance.framework.questionnaire.QuestionGroupResponsePage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionnairePage;
import org.mifos.test.acceptance.framework.questionnaire.ViewQuestionResponseDetailPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class ClientViewDetailsPage extends MifosPage {
    public static final String QUESTION_GROUP_DATE_JS = "window.document.getElementById('label.%s').innerHTML.trim()";
    public static final String QUESTION_GROUP_NAME_JS = "window.document.getElementById('questionGroupInstances').getElementsByTagName('a')[%d].innerHTML.trim()";
    public static final String QUESTION_GROUP_ID_JS = "window.document.getElementById('questionGroupInstances').getElementsByTagName('a')[%d].id";

    public ClientViewDetailsPage(Selenium selenium) {
        super(selenium);
        verifyPage("ViewClientDetails");
    }

    public String getHeading() {
        return selenium.getText("viewClientDetails.heading");
    }

    public String getStatus() {
        return selenium.getText("viewClientDetails.text.status");
    }

    public String getDateOfBirth() {
        return selenium.getText("viewClientDetails.text.dateOfBirth");
    }

    public String getSpouseFatherName() {
        return selenium.getText("viewClientDetails.text.spouseFatherName");
    }

    public String getPovertyStatus() {
        return selenium.getText("viewClientDetails.text.povertyStatus");
    }

    public String getHandicapped() {
        return selenium.getText("viewClientDetails.text.handicapped");
    }

    public String getNotes() {
        return selenium.getText("viewClientDetails.text.notes");
    }

    public void verifyHeading(String heading) {
        Assert.assertEquals(getHeading(), heading);
    }

    public String getMeetingSchedule(){
        return selenium.getText("viewClientDetails.text.meetingSchedule");
    }

    public String getGroupMembership(){
        return selenium.getText("viewClientDetails.text.groupMembership");
    }

    public void verifyMeetingSchedule(String meetingShedule){
        Assert.assertEquals(getMeetingSchedule(),meetingShedule);
    }

    public void verifyGroupMembership(String groupName){
        Assert.assertEquals(getGroupMembership(), groupName);
    }

    public void verifyName(String fullName) {
        Assert.assertTrue(getHeading().contains(fullName));
    }

    public void verifyStatus(String status) {
        Assert.assertEquals(getStatus(), status);
    }

    public void verifyDateOfBirth(String dd, String mm, String yyyy) {
        String dateOfBirth = getDateOfBirth();
        Assert.assertTrue(dateOfBirth.contains(dd));
        Assert.assertTrue(dateOfBirth.contains(mm));
        Assert.assertTrue(dateOfBirth.contains(yyyy));
    }

    public void verifySpouseFather(String spouseFatherName) {
        Assert.assertEquals(getSpouseFatherName(), spouseFatherName);
    }

    public void verifyPovertyStatus(String povertyStatus) {
        Assert.assertEquals(getPovertyStatus(), povertyStatus);
    }

    public void verifyHandicapped(String handicapped) {
        Assert.assertEquals(getHandicapped(), handicapped);
    }

    public void verifyNotes(String notes) {
        Assert.assertTrue(getNotes().contains(notes));
    }

    public ClientsAndAccountsHomepage navigateToClientsAndAccountsHomepage() {
        selenium.click("header.link.clientsAndAccounts");
        waitForPageToLoad();
        return new ClientsAndAccountsHomepage(selenium);
    }

    public CustomerChangeStatusPage navigateToCustomerChangeStatusPage() {
        selenium.click("viewClientDetails.link.editStatus");
        waitForPageToLoad();
        return new CustomerChangeStatusPage(selenium);
    }

    public ClientEditMFIPage navigateToEditMFIPage() {
        selenium.click("viewClientDetails.link.editMfiInformation");
        waitForPageToLoad();
        return new ClientEditMFIPage(selenium);
    }

    public ClientNotesPage navigateToNotesPage() {
        selenium.click("viewClientDetails.link.notesLink");
        waitForPageToLoad();
        return new ClientNotesPage(selenium);
    }

    public void verifyTextOnPage(String text) {
        Assert.assertTrue(selenium.isTextPresent(text));
    }

    public void verifyLoanDoesntExist(String loanID) {
        Assert.assertFalse(selenium.isTextPresent(loanID));
    }

    public ClientViewChangeLogPage navigateToClientViewChangeLog() {
        selenium.click("viewClientDetails.link.viewChangeLog");
        waitForPageToLoad();
        return new ClientViewChangeLogPage(selenium);
    }

    public ClientNotesPage navigateToAllNotesPage() {
        selenium.click("viewClientDetails.link.seeAllNotes");
        waitForPageToLoad();
        return new ClientNotesPage(selenium);
    }

    public ClosedAccountsPage navigateToClosedAccountsPage() {
        selenium.click("viewClientDetails.link.viewAllClosedAccounts");
        waitForPageToLoad();
        return new ClosedAccountsPage(selenium);
    }

    public ClientEditFamilyPage editFamilyInformation() {
        selenium.click("viewClientDetails.link.editFamilyInformation");
        waitForPageToLoad();
        return new ClientEditFamilyPage(selenium);
    }

    public ClientEditPersonalInfoPage editPersonalInformation(){
        selenium.click("viewClientDetails.link.editPersonalInformation");
        waitForPageToLoad();
        return new ClientEditPersonalInfoPage(selenium);
    }

    public AttachSurveyPage navigateToAttachSurveyPage() {
        selenium.click("viewClientDetails.link.attachSurvey");
        waitForPageToLoad();
        return new AttachSurveyPage(selenium);
    }

    public GroupSearchClientTransferPage navigateToEditRemoveGroupMembership(){
        selenium.click("viewClientDetails.link.editRemoveGroupMembership");
        waitForPageToLoad();
        return new GroupSearchClientTransferPage(selenium);
    }


    public Map<Integer, QuestionGroup> getQuestionGroupInstances() {
        int rows = Integer.valueOf(selenium.getEval("window.document.getElementById('questionGroupInstances').getElementsByTagName('a').length"));
        Map<Integer, QuestionGroup> instances = new LinkedHashMap<Integer, QuestionGroup>();
        for (int i = 0; i < rows - 1; i++) {
            String instanceId = selenium.getEval(format(QUESTION_GROUP_ID_JS, i));
            instances.put(new Integer(instanceId), new QuestionGroup(selenium.getEval(format(QUESTION_GROUP_NAME_JS, i)), //NOPMD
                    selenium.getEval(format(QUESTION_GROUP_DATE_JS, instanceId))));
        }
        return instances;
    }

    public QuestionGroupResponsePage navigateToQuestionGroupResponsePage(int instanceId) {
        selenium.click("id=" + instanceId);
        waitForPageToLoad();
        return new QuestionGroupResponsePage(selenium);
    }

    public QuestionnairePage getQuestionnairePage(String questionGroupTitle) {
        AttachSurveyPage attachSurveyPage = navigateToAttachSurveyPage();
        QuestionnairePage questionnairePage = attachSurveyPage.selectSurvey(questionGroupTitle);
        questionnairePage.verifyPage();
        return questionnairePage;
    }

    public ViewQuestionResponseDetailPage navigateToViewAdditionalInformationPage() {
        selenium.click("viewClientDetails.link.questionGroups");
        waitForPageToLoad();
        return new ViewQuestionResponseDetailPage(selenium);
    }

    public ViewClientChargesDetail navigateToViewClientChargesDetail(){
        selenium.click("viewClientDetails.link.viewDetails");
        waitForPageToLoad();
        return new ViewClientChargesDetail(selenium);
    }

    public EditMeetingPage navigateToEditMeetingSchedule() {
        selenium.click("viewClientDetails.link.editMeetingScheduleAddGroup");
        waitForPageToLoad();
        return new EditMeetingPage(selenium);
    }

    public LoanAccountPage navigateToOnlyLoanAccount() {
        selenium.click("viewClientDetails.link.viewLoanAccount");
        waitForPageToLoad();
        return new LoanAccountPage(selenium);
    }

    public void verifyLoanCycle(Integer loanCycle) {
        Assert.assertEquals(selenium.getText("viewClientDetails.text.loancycle"), loanCycle.toString());
    }

    public void verifyLastLoanAmount(String lastLoanAmount) {
        Assert.assertEquals(selenium.getText("viewClientDetails.text.lastloanamount"), lastLoanAmount);
    }

    public void verifynoOfActiveLoan(Integer count) {
        Assert.assertEquals(selenium.getText("viewClientDetails.text.noactiveloans"), count.toString());
    }

    public void verifyDelinquentPortfolio(Double delinquentPortfolioValue) {
        Assert.assertEquals(selenium.getText("viewClientDetails.text.delinquentportfolio"), delinquentPortfolioValue.toString());
    }

    public void verifyLoanCyclePerProduct(String product, Integer count) {
        Assert.assertEquals(selenium.getText("viewClientDetails.text."+product),count.toString());
    }

    public void verifyPerformanceHistory(PerformanceHistoryAtributes performanceHistoryAtributes){
        verifyLoanCycle(performanceHistoryAtributes.getLoanCycle());
        verifyLastLoanAmount(performanceHistoryAtributes.getAmountOfLastLoan());
        verifynoOfActiveLoan(performanceHistoryAtributes.getNoOfActiveLoan());
        if(performanceHistoryAtributes.getDelinquentPortfolio()!=null){
            verifyDelinquentPortfolio(performanceHistoryAtributes.getDelinquentPortfolio());
        }
        Map<String,Integer> loanCyclePerProduct = performanceHistoryAtributes.getLoanCyclePerProduct();
        for (String product : loanCyclePerProduct.keySet()) {
            verifyLoanCyclePerProduct(product, loanCyclePerProduct.get(product));
        }
    }
}

