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

package org.mifos.test.acceptance.framework.testhelpers;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.center.MeetingParameters;
import org.mifos.test.acceptance.framework.client.ChooseOfficePage;
import org.mifos.test.acceptance.framework.client.ClientCloseReason;
import org.mifos.test.acceptance.framework.client.ClientStatus;
import org.mifos.test.acceptance.framework.client.ClientViewChangeLogPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.client.CreateClientConfirmationPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterFamilyDetailsPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterMfiDataPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterPersonalDataPage;
import org.mifos.test.acceptance.framework.client.CreateClientPreviewDataPage;
import org.mifos.test.acceptance.framework.client.GroupSearchAddClientPage;
import org.mifos.test.acceptance.framework.customer.CustomerChangeStatusPage;
import org.mifos.test.acceptance.framework.customer.CustomerChangeStatusPreviewPage;
import org.mifos.test.acceptance.framework.group.EditCustomerStatusParameters;
import org.mifos.test.acceptance.framework.group.GroupSearchPage;
import org.mifos.test.acceptance.framework.group.GroupViewDetailsPage;
import org.mifos.test.acceptance.framework.loan.ChargeParameters;
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionnairePage;
import org.mifos.test.acceptance.framework.questionnaire.ViewQuestionResponseDetailPage;
import org.mifos.test.acceptance.util.StringUtil;

import java.util.Map;

public class ClientTestHelper {

    private final NavigationHelper navigationHelper;
    private final Selenium selenium;
    public static final String ACTIVE = "Active";
    public static final String PENDING_APPROVAL = "Application Pending Approval";
    public static final String PARTIAL_APPLICATION = "Partial Application";


    public ClientTestHelper(Selenium selenium) {
        this.navigationHelper = new NavigationHelper(selenium);
        this.selenium = selenium;
    }

    public ClientViewDetailsPage changeCustomerStatus(ClientViewDetailsPage clientDetailsPage) {

        CustomerChangeStatusPage statusChangePage = clientDetailsPage.navigateToCustomerChangeStatusPage();
        EditCustomerStatusParameters editCustomerStatusParameters = new EditCustomerStatusParameters();
        editCustomerStatusParameters.setClientStatus(ClientStatus.PARTIAL);
        editCustomerStatusParameters.setNote("Status change");

        CustomerChangeStatusPreviewPage statusPreviewPage = statusChangePage.setChangeStatusParametersAndSubmit(editCustomerStatusParameters);
        ClientViewDetailsPage clientDetailsPage2 = statusPreviewPage.submitAndGotoClientViewDetailsPage();
        clientDetailsPage2.verifyStatus(PARTIAL_APPLICATION);
        clientDetailsPage2.verifyNotes(editCustomerStatusParameters.getNote());


        CustomerChangeStatusPage statusChangePage2 = clientDetailsPage2.navigateToCustomerChangeStatusPage();
        editCustomerStatusParameters.setClientStatus(ClientStatus.PENDING_APPROVAL);
        editCustomerStatusParameters.setNote("notes");
        CustomerChangeStatusPreviewPage statusPreviewPage2 =
                statusChangePage2.setChangeStatusParametersAndSubmit(editCustomerStatusParameters);


        ClientViewDetailsPage clientDetailsPage3 = statusPreviewPage2.submitAndGotoClientViewDetailsPage();
        clientDetailsPage3.verifyNotes(editCustomerStatusParameters.getNote());
        clientDetailsPage2.verifyStatus(PENDING_APPROVAL);

        CustomerChangeStatusPage statusChangePage3 = clientDetailsPage3.navigateToCustomerChangeStatusPage();
        editCustomerStatusParameters.setClientStatus(ClientStatus.ACTIVE);
        editCustomerStatusParameters.setNote("notes");
        CustomerChangeStatusPreviewPage statusPreviewPage3 =
                statusChangePage3.setChangeStatusParametersAndSubmit(editCustomerStatusParameters);


        ClientViewDetailsPage clientDetailsPage4 = statusPreviewPage3.submitAndGotoClientViewDetailsPage();
        clientDetailsPage4.verifyNotes(editCustomerStatusParameters.getNote());
        clientDetailsPage3.verifyStatus(ACTIVE);

        CustomerChangeStatusPage statusChangePage4 = clientDetailsPage4.navigateToCustomerChangeStatusPage();

        ClientViewDetailsPage clientDetailsPage5 = statusChangePage4.cancelAndGotoClientViewDetailsPage();
        clientDetailsPage5.verifyNotes(editCustomerStatusParameters.getNote());
        return clientDetailsPage5;
    }

    public ClientViewDetailsPage changeCustomerStatus(ClientViewDetailsPage clientDetailsPage, ClientStatus targetStatus) {

        CustomerChangeStatusPage statusChangePage = clientDetailsPage.navigateToCustomerChangeStatusPage();
        EditCustomerStatusParameters editCustomerStatusParameters = new EditCustomerStatusParameters();
        editCustomerStatusParameters.setClientStatus(targetStatus);
        editCustomerStatusParameters.setNote("Status change");

        CustomerChangeStatusPreviewPage statusPreviewPage = statusChangePage.setChangeStatusParametersAndSubmit(editCustomerStatusParameters);
        ClientViewDetailsPage newClientDetailsPage = statusPreviewPage.submitAndGotoClientViewDetailsPage();
        newClientDetailsPage.verifyStatus(targetStatus.getStatusText());
        newClientDetailsPage.verifyNotes(editCustomerStatusParameters.getNote());

        return newClientDetailsPage;
    }

    public ClientViewDetailsPage changeCustomerStatus(String clientName, EditCustomerStatusParameters editCustomerStatusParameters) {
        ClientViewDetailsPage clientDetailsPage = navigationHelper.navigateToClientViewDetailsPage(clientName);
        CustomerChangeStatusPage statusChangePage = clientDetailsPage.navigateToCustomerChangeStatusPage();
        CustomerChangeStatusPreviewPage statusPreviewPage = statusChangePage.setChangeStatusParametersAndSubmit(editCustomerStatusParameters);
        ClientViewDetailsPage newClientDetailsPage = statusPreviewPage.submitAndGotoClientViewDetailsPage();
        newClientDetailsPage.verifyStatus(editCustomerStatusParameters.getClientStatus().getStatusText());
        newClientDetailsPage.verifyNotes(editCustomerStatusParameters.getNote());
        return newClientDetailsPage;
    }

    public ClientViewDetailsPage activateClient(String clientName) {
        EditCustomerStatusParameters editCustomerStatusParameters = new EditCustomerStatusParameters();
        editCustomerStatusParameters.setClientStatus(ClientStatus.ACTIVE);
        editCustomerStatusParameters.setNote("Activate Client");
        return changeCustomerStatus(clientName, editCustomerStatusParameters);
    }

    public QuestionResponsePage navigateToQuestionResponsePageWhenCloseClientAccount(String clientName) {
        EditCustomerStatusParameters editCustomerStatusParameters = new EditCustomerStatusParameters();
        editCustomerStatusParameters.setClientStatus(ClientStatus.CLOSED);
        editCustomerStatusParameters.setClientCloseReason(ClientCloseReason.LEFT_PROGRAM);
        editCustomerStatusParameters.setNote("Close client account");
        ClientViewDetailsPage clientDetailsPage = navigationHelper.navigateToClientViewDetailsPage(clientName);
        CustomerChangeStatusPage statusChangePage = clientDetailsPage.navigateToCustomerChangeStatusPage();
        return statusChangePage.changeStatusAndNavigateToQuestionResponsePage(editCustomerStatusParameters);
    }

    public ClientViewDetailsPage closeClientWithQG(String clientName, QuestionResponseParameters responseParamsAfterModyfication){
        EditCustomerStatusParameters editCustomerStatusParameters = new EditCustomerStatusParameters();
        editCustomerStatusParameters.setClientStatus(ClientStatus.CLOSED);
        editCustomerStatusParameters.setClientCloseReason(ClientCloseReason.LEFT_PROGRAM);
        editCustomerStatusParameters.setNote("Close client account");
        ClientViewDetailsPage clientDetailsPage = navigationHelper.navigateToClientViewDetailsPage(clientName);
        CustomerChangeStatusPage statusChangePage = clientDetailsPage.navigateToCustomerChangeStatusPage();
        QuestionResponsePage questionResponsePage = statusChangePage.changeStatusAndNavigateToQuestionResponsePage(editCustomerStatusParameters);
        questionResponsePage.populateAnswers(responseParamsAfterModyfication);
        questionResponsePage.navigateToNextPage();
        new CustomerChangeStatusPreviewPage(selenium).submitAndGotoClientViewDetailsPage();
        return new ClientViewDetailsPage(selenium);
    }

    public ClientViewDetailsPage createClientWithQuestionGroups(CreateClientEnterPersonalDataPage.SubmitFormParameters parameters, String group, QuestionResponseParameters responseParams) {
        QuestionResponsePage questionResponsePage = navigateToQuestionResponsePage(parameters, group);
        questionResponsePage.populateAnswers(responseParams);
        CreateClientEnterMfiDataPage createClientEnterMfiDataPage = questionResponsePage.navigateToNextPageClientCreation();
        CreateClientPreviewDataPage clientPreviewDataPage = createClientEnterMfiDataPage.navigateToPreview();
        clientPreviewDataPage.submit();
        return navigateToClientViewDetails(parameters);
    }

    public QuestionResponsePage navigateToQuestionResponsePage(CreateClientEnterPersonalDataPage.SubmitFormParameters parameters, String group) {
        return navigationHelper
                .navigateToClientsAndAccountsPage()
                .navigateToCreateNewClientPage()
                .selectGroup(group)
                .create(parameters)
                .submitAndGotoCaptureQuestionResponsePage();
    }

    private GroupSearchAddClientPage navigateToGroupSearchAddClientResult(String clientName, String groupName) {
        return navigationHelper.navigateToClientViewDetailsPage(clientName)
                .navigateToEditMeetingSchedule()
                .addGroupMembership()
                .searchGroup(groupName);
    }

    public void tryAddClientToClosedOrOnHoldGroup(String clientName, String groupName) {
        navigateToGroupSearchAddClientResult(clientName, groupName)
                .verifyNoResult();
    }

    public void addClientToGroupWithErrorGroupLowerStatus(String clientName, String groupName) {
        navigateToGroupSearchAddClientResult(clientName, groupName)
                .selectGroupToAdd(groupName)
                .submitAddGroupWithErrorGroupLowerStatus();
    }

    public void transferClientToGroupWithErrors(String clientName, String groupName) {
        navigationHelper
                .navigateToClientViewDetailsPage(clientName)
                .navigateToEditRemoveGroupMembership()
                .searchGroup(groupName)
                .selectGroupToAdd(groupName)
                .submitAddGroupWithError();
    }

    public void addClientToGroup(String clientName, String groupName) {
        ClientViewDetailsPage clientViewDetailsPage = navigateToGroupSearchAddClientResult(clientName, groupName)
                .selectGroupToAdd(groupName)
                .submitAddGroup();
        clientViewDetailsPage.verifyGroupMembership(groupName);
        String clientMeetingschedule = clientViewDetailsPage.getMeetingSchedule();
        navigationHelper.navigateToGroupViewDetailsPage(groupName).verifyMeetingSchedule(clientMeetingschedule);
    }

    public void deleteClientGroupMembership(String clientName, String note) {
        ClientViewDetailsPage clientViewDetailsPage = navigationHelper.navigateToClientViewDetailsPage(clientName);
        String oldMeetingshedule = clientViewDetailsPage.getMeetingSchedule();
        String groupName = clientViewDetailsPage.getGroupMembership();

        GroupViewDetailsPage groupViewDetailsPage = navigationHelper.navigateToGroupViewDetailsPage(groupName);
        Integer activeClients = Integer.parseInt(groupViewDetailsPage.getNumberOfClientsInGroup());
        String avgIndyvidualLoanSize = groupViewDetailsPage.getAvgIndyvidualLoanSize();
        String totalLoanPortfolio = groupViewDetailsPage.getTotalLoanPortfolio();

        clientViewDetailsPage = navigationHelper.navigateToClientViewDetailsPage(clientName);
        clientViewDetailsPage = clientViewDetailsPage.navigateToEditRemoveGroupMembership()
                .deleteGroupMembership()
                .confirmDeleteGroupMembership(note);

        clientViewDetailsPage.verifyMeetingSchedule(oldMeetingshedule);
        clientViewDetailsPage.verifyNotes(note);
        ClientViewChangeLogPage changeLogPage = clientViewDetailsPage.navigateToClientViewChangeLog();
        if ("groupFlag".equals(changeLogPage.getLastEntryFieldName())) {
            changeLogPage.verifyLastEntryOnChangeLog("groupFlag", "1", "0", "mifos");
            changeLogPage.verifyEntryOnChangeLog(2, "Group Name", groupName, "-", "mifos");
        }
        else {
            changeLogPage.verifyLastEntryOnChangeLog("Group Name", groupName, "-", "mifos");
            changeLogPage.verifyEntryOnChangeLog(2, "groupFlag", "1", "0", "mifos");
        }

        groupViewDetailsPage = navigationHelper.navigateToGroupViewDetailsPage(groupName);
        groupViewDetailsPage.verifyNumberOfClientsInGroup(Integer.toString(activeClients - 1));
        groupViewDetailsPage.verifyAvgIndyvidualLoanSize(avgIndyvidualLoanSize);
        groupViewDetailsPage.verifyTotalLoanPortfolio(totalLoanPortfolio);
    }

    public void deleteClientGroupMembershipWithError(String clientName) {
        navigationHelper
                .navigateToClientViewDetailsPage(clientName)
                .navigateToEditRemoveGroupMembership()
                .deleteGroupMembership()
                .confirmDeleteGroupMembershipFail();
    }

    public ClientViewDetailsPage createClientAndVerify(String loanOfficer, String officeName) {
        CreateClientEnterPersonalDataPage.SubmitFormParameters formParameters = createClient(loanOfficer, officeName);
        return navigateToClientViewDetails(formParameters);
    }

    public CreateClientEnterPersonalDataPage.SubmitFormParameters createClient(String loanOfficer, String officeName) {
        CreateClientEnterPersonalDataPage clientPersonalDataPage = navigateToPersonalDataPage(officeName);
        CreateClientEnterPersonalDataPage.SubmitFormParameters formParameters = FormParametersHelper.getClientEnterPersonalDataPageFormParameters();
        clientPersonalDataPage = clientPersonalDataPage.create(formParameters);
        clientPersonalDataPage.submitAndGotoCreateClientEnterMfiDataPage();
        navigateToConfirmationPage(loanOfficer);
        return formParameters;
    }

    public ClientViewDetailsPage createClientWithQuestionGroups(String loanOfficer, String officeName, Map<String, String> choiceTags, String answer) {
        CreateClientEnterPersonalDataPage clientPersonalDataPage = navigateToPersonalDataPage(officeName);
        CreateClientEnterPersonalDataPage.SubmitFormParameters formParameters = FormParametersHelper.getClientEnterPersonalDataPageFormParameters();
        clientPersonalDataPage = clientPersonalDataPage.create(formParameters);
        QuestionResponsePage questionResponsePage = clientPersonalDataPage.submitAndGotoCaptureQuestionResponsePage();
        questionResponsePage.populateTextAnswer("name=questionGroups[0].sectionDetails[0].questions[0].value", answer);
        questionResponsePage.populateSmartSelect("txtListSearch", choiceTags);
        questionResponsePage.navigateToNextPage();
        navigateToConfirmationPage(loanOfficer);
        return navigateToClientViewDetails(formParameters);
    }

    private ClientViewDetailsPage navigateToClientViewDetails(CreateClientEnterPersonalDataPage.SubmitFormParameters formParameters) {
        ClientViewDetailsPage clientViewDetailsPage = new CreateClientConfirmationPage(selenium).navigateToClientViewDetailsPage();
        clientViewDetailsPage.verifyName(formParameters.getFirstName() + " " + formParameters.getLastName());
        clientViewDetailsPage.verifyDateOfBirth(formParameters.getDateOfBirthDD(), formParameters.getDateOfBirthMM(), formParameters.getDateOfBirthYYYY());
        clientViewDetailsPage.verifySpouseFather(formParameters.getSpouseFirstName() + " " + formParameters.getSpouseLastName());
        clientViewDetailsPage.verifyHandicapped(formParameters.getHandicapped());
        return clientViewDetailsPage;
    }

    private CreateClientConfirmationPage navigateToConfirmationPage(String loanOfficer) {
        CreateClientEnterMfiDataPage.SubmitFormParameters mfiFormParameters = new CreateClientEnterMfiDataPage.SubmitFormParameters();
        mfiFormParameters.setLoanOfficerId(loanOfficer);

        MeetingParameters meetingFormParameters = new MeetingParameters();
        meetingFormParameters.setWeekFrequency("1");
        meetingFormParameters.setWeekDay(MeetingParameters.WeekDay.WEDNESDAY);
        meetingFormParameters.setMeetingPlace("Bangalore");

        mfiFormParameters.setMeeting(meetingFormParameters);

        CreateClientPreviewDataPage clientPreviewDataPage = new CreateClientEnterMfiDataPage(selenium).submitAndGotoCreateClientPreviewDataPage(mfiFormParameters);
        CreateClientConfirmationPage clientConfirmationPage = clientPreviewDataPage.submit();
        clientConfirmationPage.verifyPage();
        return clientConfirmationPage;
    }

    private CreateClientEnterPersonalDataPage navigateToPersonalDataPage(String officeName) {
        ClientsAndAccountsHomepage clientsAndAccountsPage = navigationHelper.navigateToClientsAndAccountsPage();
        GroupSearchPage groupSearchPage = clientsAndAccountsPage.navigateToCreateNewClientPage();
        ChooseOfficePage chooseOfficePage = groupSearchPage.navigateToCreateClientWithoutGroupPage();
        return chooseOfficePage.chooseOffice(officeName);
    }

    public CreateClientEnterPersonalDataPage createClient(String officeName, String dd, String mm, String yy) {
        CreateClientEnterPersonalDataPage clientPersonalDataPage = navigateToPersonalDataPage(officeName);
        CreateClientEnterPersonalDataPage.SubmitFormParameters formParameters = new CreateClientEnterPersonalDataPage.SubmitFormParameters();
        formParameters.setSalutation(CreateClientEnterPersonalDataPage.SubmitFormParameters.MRS);
        formParameters.setFirstName("test");
        formParameters.setLastName("Customer" + StringUtil.getRandomString(8));
        formParameters.setDateOfBirthDD(dd);
        formParameters.setDateOfBirthMM(mm);
        formParameters.setDateOfBirthYYYY(yy);
        formParameters.setGender(CreateClientEnterPersonalDataPage.SubmitFormParameters.FEMALE);
        formParameters.setPovertyStatus(CreateClientEnterPersonalDataPage.SubmitFormParameters.POOR);
        formParameters.setHandicapped("Yes");
        formParameters.setSpouseNameType(CreateClientEnterPersonalDataPage.SubmitFormParameters.FATHER);
        formParameters.setSpouseFirstName("father");
        formParameters.setSpouseLastName("lastname" + StringUtil.getRandomString(8));
        return clientPersonalDataPage.create(formParameters);
    }


    public CreateClientEnterPersonalDataPage createClientForFamilyInfo(String officeName, String dd, String mm, String yy) {
        CreateClientEnterPersonalDataPage clientPersonalDataPage = navigateToPersonalDataPage(officeName);
        CreateClientEnterPersonalDataPage.SubmitFormParameters formParameters = new CreateClientEnterPersonalDataPage.SubmitFormParameters();
        formParameters.setLastName("Customer" + StringUtil.getRandomString(8));
        formParameters.setSalutation(CreateClientEnterPersonalDataPage.SubmitFormParameters.MRS);
        formParameters.setFirstName("test");
        formParameters.setDateOfBirthYYYY(yy);
        formParameters.setLastName("Customer" + StringUtil.getRandomString(8));
        formParameters.setDateOfBirthDD(dd);
        formParameters.setDateOfBirthMM(mm);
        formParameters.setGender(CreateClientEnterPersonalDataPage.SubmitFormParameters.FEMALE);
        formParameters.setPovertyStatus(CreateClientEnterPersonalDataPage.SubmitFormParameters.POOR);
        formParameters.setHandicapped("Yes");
        return clientPersonalDataPage.createWithoutSpouse(formParameters);
    }

    public CreateClientEnterFamilyDetailsPage createFamily(String fname, String lname, String dd, String mm, String yy, CreateClientEnterFamilyDetailsPage page) {
        CreateClientEnterFamilyDetailsPage.SubmitFormParameters formParameters = new CreateClientEnterFamilyDetailsPage.SubmitFormParameters();
        formParameters.setRelationship(CreateClientEnterFamilyDetailsPage.SubmitFormParameters.FATHER);
        formParameters.setFirstName(fname);
        formParameters.setLastName(lname);
        formParameters.setDateOfBirthDD(dd);
        formParameters.setDateOfBirthMM(mm);
        formParameters.setDateOfBirthYY(yy);
        formParameters.setGender(CreateClientEnterFamilyDetailsPage.SubmitFormParameters.MALE);
        formParameters.setLivingStatus(CreateClientEnterFamilyDetailsPage.SubmitFormParameters.TOGETHER);
        return page.createMember(formParameters);
    }

    public CreateClientEnterFamilyDetailsPage createFamilyWithoutLookups(Integer relation, Integer gender, Integer livingStatus, CreateClientEnterFamilyDetailsPage page) {
        CreateClientEnterFamilyDetailsPage.SubmitFormParameters formParameters = new CreateClientEnterFamilyDetailsPage.SubmitFormParameters();
        formParameters.setRelationship(relation);
        formParameters.setFirstName("fname");
        formParameters.setLastName("lname");
        formParameters.setDateOfBirthDD("11");
        formParameters.setDateOfBirthMM("1");
        formParameters.setDateOfBirthYY("2009");
        formParameters.setGender(gender);
        formParameters.setLivingStatus(livingStatus);
        return page.createMember(formParameters);
    }

    public CreateClientEnterFamilyDetailsPage createFamilyWithAllName(String fname, String lname, String mname, String slname, String dd, String mm, String yy, CreateClientEnterFamilyDetailsPage page){
        CreateClientEnterFamilyDetailsPage.SubmitFormParameters formParameters = new CreateClientEnterFamilyDetailsPage.SubmitFormParameters();
        formParameters.setRelationship(CreateClientEnterFamilyDetailsPage.SubmitFormParameters.FATHER);
        formParameters.setFirstName(fname);
        formParameters.setLastName(lname);
        formParameters.setMiddleName(mname);
        formParameters.setSecondLastName(slname);
        formParameters.setDateOfBirthDD(dd);
        formParameters.setDateOfBirthMM(mm);
        formParameters.setDateOfBirthYY(yy);
        formParameters.setGender(CreateClientEnterFamilyDetailsPage.SubmitFormParameters.MALE);
        formParameters.setLivingStatus(CreateClientEnterFamilyDetailsPage.SubmitFormParameters.TOGETHER);
        return page.createMemberWithAllNames(formParameters);
    }
    
    public CreateClientPreviewDataPage createClientMFIInformationAndGoToPreviewPage(String loanOfficer, CreateClientEnterMfiDataPage clientMfiDataPage) {
        CreateClientEnterMfiDataPage.SubmitFormParameters mfiFormParameters = new CreateClientEnterMfiDataPage.SubmitFormParameters();
        mfiFormParameters.setLoanOfficerId(loanOfficer);

        MeetingParameters meetingFormParameters = new MeetingParameters();
        meetingFormParameters.setWeekFrequency("1");
        meetingFormParameters.setWeekDay(MeetingParameters.WeekDay.WEDNESDAY);
        meetingFormParameters.setMeetingPlace("Mangalore");

        mfiFormParameters.setMeeting(meetingFormParameters);
        return clientMfiDataPage.submitAndGotoCreateClientPreviewDataPage(mfiFormParameters);
    }

    public ClientViewDetailsPage createNewClient(String groupName, CreateClientEnterPersonalDataPage.SubmitFormParameters clientParams) {
        return navigationHelper
                .navigateToClientsAndAccountsPage()
                .navigateToCreateNewClientPage()
                .selectGroup(groupName)
                .create(clientParams)
                .submitAndGotoCreateClientEnterMfiDataPage()
                .navigateToPreview()
                .submit()
                .navigateToClientViewDetailsPage();
    }

    public ClientViewDetailsPage navigateToClientViewDetailsPage(String clientName) {
        ClientViewDetailsPage clientViewDetailsPage = navigationHelper.navigateToClientViewDetailsPage(clientName);
        clientViewDetailsPage.verifyPage("ViewClientDetails");

        return clientViewDetailsPage;
    }

    public ClientViewDetailsPage editQuestionGroupResponses(ClientViewDetailsPage clientViewDetailsPage, String numberSection, String locator, String response) {
        ViewQuestionResponseDetailPage viewQuestionResponseDetailPage = clientViewDetailsPage.navigateToViewAdditionalInformationPage();
        viewQuestionResponseDetailPage.verifyPage();
        QuestionnairePage questionnairePage = viewQuestionResponseDetailPage.navigateToEditSection(numberSection);
        questionnairePage.verifyPage();
        selenium.type(locator, response);
        ClientViewDetailsPage clientViewDetailsPage2 = (ClientViewDetailsPage) questionnairePage.submit();
        clientViewDetailsPage2.verifyPage("ViewClientDetails");
        return clientViewDetailsPage2;
    }

    public void applyCharge(String clientSearchString, ChargeParameters chargeParameters) {
        navigationHelper.navigateToClientViewDetailsPage(clientSearchString)
                .navigateToViewClientChargesDetail()
                .navigateToApplyCharges()
                .applyChargeAndNaviagteToViewClientChargesDetail(chargeParameters);
    }
    
    public void verifyMeetingSchedule(String clientName, String meetingSchedule){
    	navigationHelper.navigateToClientViewDetailsPage(clientName)
    		.verifyMeetingSchedule(meetingSchedule);
    }

    /**
    * Adds client to group but fails cause either client or group has active loan and/or savings accounts.
    * @param clientName
    * @param groupName
    */
    public void addClientToGroupWithErrorActiveAccountExists(String clientName, String groupName) {
        navigateToGroupSearchAddClientResult(clientName, groupName)
        .selectGroupToAdd(groupName)
        .submitAddGroupWithErrorActiveAccountExists();
    }
    /**
     * Creates client for office and witch custom weekly meeting schedule.
     * @param loanOfficer assigned loan officer
     * @param office office's name
     * @param frequency weekly meeting frequency
     * @param weekDay day of the meeting
     * @param meetingPlace place of the meetings
     * @return
     */
    public ClientViewDetailsPage createClientWithCustomMFIInformation(String loanOfficer,
            String office, String frequency, MeetingParameters.WeekDay weekDay, String meetingPlace) {
        CreateClientEnterPersonalDataPage clientPersonalDataPage = navigateToPersonalDataPage(office);
        CreateClientEnterPersonalDataPage.SubmitFormParameters formParameters = FormParametersHelper.getClientEnterPersonalDataPageFormParameters();
        clientPersonalDataPage = clientPersonalDataPage.create(formParameters);
        clientPersonalDataPage.submitAndGotoCreateClientEnterMfiDataPage();
        CreateClientEnterMfiDataPage.SubmitFormParameters mfiFormParameters = new CreateClientEnterMfiDataPage.SubmitFormParameters();
        mfiFormParameters.setLoanOfficerId(loanOfficer);

        MeetingParameters meetingFormParameters = new MeetingParameters();
        meetingFormParameters.setWeekFrequency(frequency);
        meetingFormParameters.setWeekDay(weekDay);
        meetingFormParameters.setMeetingPlace(meetingPlace);

        mfiFormParameters.setMeeting(meetingFormParameters);

        CreateClientPreviewDataPage clientPreviewDataPage = new CreateClientEnterMfiDataPage(selenium).submitAndGotoCreateClientPreviewDataPage(mfiFormParameters);
        CreateClientConfirmationPage clientConfirmationPage = clientPreviewDataPage.submit();
        clientConfirmationPage.verifyPage();
        return navigateToClientViewDetails(formParameters);
        
    }
}
