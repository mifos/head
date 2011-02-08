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

package org.mifos.test.acceptance.framework.group;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.center.CenterViewDetailsPage;
import org.mifos.test.acceptance.framework.customer.CustomerChangeStatusPage;
import org.mifos.test.acceptance.framework.loan.AttachSurveyPage;
import org.mifos.test.acceptance.framework.loan.ClosedAccountsPage;
import org.mifos.test.acceptance.framework.questionnaire.ViewQuestionResponseDetailPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class GroupViewDetailsPage extends MifosPage {

    public final static String STATUS_PENDING_APPROVAL = "Pending Approval";
    public final static String STATUS_PARTIAL_APPLICATION = "Partial Application";
    public final static String STATUS_ACTIVE = "Active";

    public GroupViewDetailsPage() {
        super();
    }

    public GroupViewDetailsPage(Selenium selenium) {
        super(selenium);
        verifyPage("ViewGroupDetails");
    }

    @Deprecated
    public void verifyStatus(String status) {
        Assert.assertTrue(selenium.isTextPresent(status), "Expected string: " + status);
    }

    public String getGroupStatus(){
        return selenium.getText("viewgroupdetails.text.status");
    }

    public String getMeetingSchedule(){
        return selenium.getText("viewgroupdetails.text.meetingSchedule");
    }

    public String getCancelCloseReason(){
        return selenium.getText("viewgroupdetails.text.closeCancelReason");
    }

    public String getNumberOfClientsInGroup(){
        return selenium.getText("viewgroupdetails.text.numberOfClientsInGroup");
    }

    public String getAvgIndyvidualLoanSize(){
        return selenium.getText("viewgroupdetails.text.avgIndyvidualLoanSize");
    }

    public String getTotalLoanPortfolio(){
        return selenium.getText("viewgroupdetails.text.totalLoanPortfolio");
    }

    public void verifyNumberOfClientsInGroup(String numberOfClientsInGroup){
        Assert.assertEquals(getNumberOfClientsInGroup(),numberOfClientsInGroup);
    }

    public void verifyAvgIndyvidualLoanSize(String avgIndyvidualLoanSize){
        Assert.assertEquals(getAvgIndyvidualLoanSize(),avgIndyvidualLoanSize);
    }

    public void verifyTotalLoanPortfolio(String totalLoanPortfolio){
        Assert.assertEquals(getTotalLoanPortfolio(), totalLoanPortfolio);
    }

    public void verifyMeetingSchedule(String meetingShedule){
        Assert.assertEquals(getMeetingSchedule(),meetingShedule);
    }

    public void verifyStatus(EditCustomerStatusParameters editCustomerStatusParameters) {
        Assert.assertEquals(getGroupStatus(), editCustomerStatusParameters.getGroupStatus().getStatusText());
        if(editCustomerStatusParameters.getGroupStatus().equals(GroupStatus.CANCELLED)){
            Assert.assertEquals(getCancelCloseReason(), editCustomerStatusParameters.getCancelReason().getPurposeText());
        }
        else{
            if(editCustomerStatusParameters.getGroupStatus().equals(GroupStatus.CLOSED)){
                Assert.assertEquals(getCancelCloseReason(), editCustomerStatusParameters.getCloseReason().getPurposeText());
            }
        }
    }

    public CenterSearchTransferGroupPage editCenterMembership() {
        selenium.click("viewgroupdetails.link.editCenterMembership");
        waitForPageToLoad();
        return new CenterSearchTransferGroupPage(selenium);
    }

    public void verifyLoanOfficer(String loanOfficer) {
        Assert.assertTrue(selenium.isTextPresent(loanOfficer), "Expected string: " + loanOfficer);

    }

    public ClosedAccountsPage navigateToClosedAccountsPage() {
        selenium.click("viewgroupdetails.link.viewAllClosedAccounts");
        waitForPageToLoad();
        return new ClosedAccountsPage(selenium);
    }

    public CustomerChangeStatusPage navigateToEditGroupStatusPage() {
        selenium.click("viewgroupdetails.link.editStatus");
        waitForPageToLoad();
        return new CustomerChangeStatusPage(selenium);
    }

    public HistoricalDataPage navigateToHistoricalDataPage() {
        selenium.click("viewgroupdetails.link.viewHistoricalData");
        waitForPageToLoad();
        return new HistoricalDataPage(selenium);
    }

    public ChangeLogPage navigateToChangeLogPage() {
        selenium.click("viewgroupdetails.link.viewChangeLog");
        waitForPageToLoad();
        return new ChangeLogPage(selenium);
    }

    public EditCenterMembershipSearchPage navigateToEditCenterMembership() {
        selenium.click("viewgroupdetails.link.editCenterMembership");
        waitForPageToLoad();
        return new EditCenterMembershipSearchPage(selenium);
    }

    public AttachSurveyPage navigateToAttachSurveyPage() {
        selenium.click("viewgroupdetails.link.attachSurvey");
        waitForPageToLoad();
        return new AttachSurveyPage(selenium);
    }

    public ViewQuestionResponseDetailPage navigateToViewAdditionalInformationPage() {
        selenium.click("groupdetail.link.questionGroups");
        waitForPageToLoad();
        return new ViewQuestionResponseDetailPage(selenium);
    }

    public void verifyLoanDoesntExist(String loanID) {
        Assert.assertFalse(selenium.isTextPresent(loanID));
    }

    public CenterViewDetailsPage navigateToGroupsCenter(String centerName) {
        selenium.click("link="+centerName);
        waitForPageToLoad();
        return new CenterViewDetailsPage(selenium);
    }
}
