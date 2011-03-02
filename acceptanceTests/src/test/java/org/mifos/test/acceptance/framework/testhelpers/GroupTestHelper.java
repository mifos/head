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

import org.mifos.test.acceptance.framework.group.CreateGroupEntryPage;
import org.mifos.test.acceptance.framework.group.CreateGroupEntryPage.CreateGroupSubmitParameters;
import org.mifos.test.acceptance.framework.group.CreateGroupPreviewDataPage;
import org.mifos.test.acceptance.framework.group.EditCustomerStatusParameters;
import org.mifos.test.acceptance.framework.group.GroupStatus;
import org.mifos.test.acceptance.framework.group.GroupViewDetailsPage;
import org.mifos.test.acceptance.framework.group.ViewGroupChargesDetailPage;
import org.mifos.test.acceptance.framework.loan.ChargeParameters;
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;

import com.thoughtworks.selenium.Selenium;

public class GroupTestHelper {

    private final NavigationHelper navigationHelper;
    private final Selenium selenium;

    public GroupTestHelper(Selenium selenium) {
        this.selenium = selenium;
        this.navigationHelper = new  NavigationHelper(selenium);
    }

    public GroupViewDetailsPage createNewGroup(String centerName, CreateGroupSubmitParameters groupParams) {
        return navigateToCreateGroupEntryPage(centerName)
            .submitNewGroupForApproval(groupParams)
            .navigateToGroupDetailsPage();
    }

    public GroupViewDetailsPage createNewGroupWithoutPendingForApproval(String centerName, CreateGroupSubmitParameters groupParams) {
        return navigateToCreateGroupEntryPage(centerName)
            .submitNewGroupForApprove(groupParams)
            .navigateToGroupDetailsPage();
    }

    public GroupViewDetailsPage createNewGroupPartialApplication(String centerName, CreateGroupSubmitParameters groupParams) {
        return navigateToCreateGroupEntryPage(centerName)
            .submitNewGroupForPartialApplication(groupParams)
            .navigateToGroupDetailsPage();
    }

    private CreateGroupEntryPage navigateToCreateGroupEntryPage(String centerName){
        return navigationHelper
        .navigateToClientsAndAccountsPage()
        .navigateToCreateNewGroupPage()
        .searchAndNavigateToCreateGroupPage(centerName);
    }

    public GroupViewDetailsPage changeGroupStatus(String groupName, EditCustomerStatusParameters editCustomerStatusParameters){
        GroupViewDetailsPage groupViewDetailsPage = navigationHelper
            .navigateToGroupViewDetailsPage(groupName)
            .navigateToEditGroupStatusPage()
            .setChangeStatusParametersAndSubmit(editCustomerStatusParameters)
            .navigateToGroupDetailsPage();
        groupViewDetailsPage.verifyStatus(editCustomerStatusParameters);
        return groupViewDetailsPage;
    }

    public GroupViewDetailsPage changeGroupCenterMembership(String groupName, String centerName) {
        return navigationHelper
            .navigateToGroupViewDetailsPage(groupName)
            .navigateToEditCenterMembership()
            .selectCenterAndNavigateToEditCenterMembershiConfirmationPage(centerName)
            .submitAndNavigateToGroupDetailsPage();
    }

    public ViewGroupChargesDetailPage applyCharge(String groupName, ChargeParameters chargeParameters) {
        return navigationHelper.navigateToGroupViewDetailsPage(groupName)
            .navigateToViewGroupChargesDetailPage()
            .navigateToApplyCharges()
            .applyChargeAndNaviagteToViewGroupChargesDetailPage(chargeParameters);
    }

    public GroupViewDetailsPage activateGroup(String groupName){
        EditCustomerStatusParameters editCustomerStatusParameters = new EditCustomerStatusParameters();
        editCustomerStatusParameters.setGroupStatus(GroupStatus.ACTIVE);
        editCustomerStatusParameters.setNote("Activate group");
        return changeGroupStatus(groupName, editCustomerStatusParameters);
    }

    public GroupViewDetailsPage createGroupWithQuestionGroupsEdited(CreateGroupSubmitParameters groupParams, String centerName, QuestionResponseParameters responseParams, QuestionResponseParameters responseParams2) {
        QuestionResponsePage responsePage = navigateToCreateGroupEntryPage(centerName)
            .submitAndNavigateToQuestionResponsePage(groupParams);
        responsePage.populateAnswers(responseParams);
        responsePage.navigateToNextPage();
        new CreateGroupPreviewDataPage(selenium).navigateToEditQuestionResponsePage();
        responsePage.populateAnswers(responseParams2);
        responsePage.navigateToNextPage();

        return new CreateGroupPreviewDataPage(selenium).submitForApproval().navigateToGroupDetailsPage();
    }

    public QuestionResponsePage navigateToQuestionResponsePageWhenCreatingGroup(CreateGroupSubmitParameters groupParams, String centerName) {
        return navigateToCreateGroupEntryPage(centerName)
            .submitAndNavigateToQuestionResponsePage(groupParams);
    }
}
