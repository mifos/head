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

package org.mifos.application.servicefacade;

import java.util.List;

import org.joda.time.DateTime;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.center.struts.actionforms.CenterCustActionForm;
import org.mifos.customers.client.struts.actionforms.ClientCustActionForm;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.struts.action.GroupSearchResultsDto;
import org.mifos.customers.group.struts.actionforms.GroupCustActionForm;
import org.mifos.customers.util.helpers.SavingsDetailDto;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.security.util.UserContext;

public interface CustomerServiceFacade {

    OnlyBranchOfficeHierarchyDto retrieveBranchOnlyOfficeHierarchy(UserContext userContext);

    CenterFormCreationDto retrieveCenterFormCreationData(CenterCreation centerCreation, UserContext userContext);

    GroupFormCreationDto retrieveGroupFormCreationData(GroupCreation groupCreation);

    ClientFormCreationDto retrieveClientFormCreationData(UserContext userContext, Short groupFlag, Short officeId, String parentGroupId);

    CustomerDetailsDto createNewCenter(CenterCustActionForm actionForm, MeetingBO meeting, UserContext userContext) throws ApplicationException;

    CustomerDetailsDto createNewGroup(GroupCustActionForm actionForm, MeetingBO meeting, UserContext userContext) throws ApplicationException;

    CustomerDetailsDto createNewClient(ClientCustActionForm actionForm, MeetingBO meeting, UserContext userContext, List<SavingsDetailDto> allowedSavingProducts) throws ApplicationException;

    CenterDto retrieveCenterDetailsForUpdate(Integer centerId, UserContext userContext);

    CenterDto retrieveGroupDetailsForUpdate(String globalCustNum, UserContext userContext);

    void updateCenter(UserContext userContext, CenterUpdate centerUpdate);

    void updateGroup(UserContext userContext, GroupUpdate groupUpdate);

    CustomerSearch search(String searchString, UserContext userContext) throws ApplicationException;

    CenterHierarchySearchDto isCenterHierarchyConfigured(Short loggedInUserBranchId);

    GroupBO transferGroupToCenter(String globalCustNum, String centerSystemId, UserContext userContext, Integer previousGroupVersionNo) throws ApplicationException;

    GroupBO transferGroupToBranch(String globalCustNum, Short officeIdValue, UserContext userContext, Integer previousGroupVersionNo) throws ApplicationException;

    void updateCustomerStatus(Integer customerId, Integer versionNo, String flagId, String newStatusId, String notes, UserContext userContext) throws ApplicationException;

    boolean isGroupHierarchyRequired();

    GroupSearchResultsDto searchGroups(boolean searchForAddingClientsToGroup, String normalizedSearchString, Short loggedInUserId);

    ClientFamilyDetailsDto retrieveClientFamilyDetails();

    ProcessRulesDto previewClient(String governmentId, DateTime dateOfBirth, String clientName);

    ClientPersonalInfoDto retrieveClientPersonalInfoForUpdate(String clientSystemId, UserContext userContext);

    ClientRulesDto retrieveClientDetailsForPreviewingEditOfPersonalInfo(ClientDetailDto clientDetailDto);

    void updateClientPersonalInfo(UserContext userContext, Integer oldClientVersionNumber, Integer customerId,
            ClientCustActionForm actionForm);

    ClientFamilyInfoDto retrieveFamilyInfoForEdit(String globalCustNum, UserContext userContext);

    void updateFamilyInfo(Integer customerId, UserContext userContext, Integer versionNo, ClientCustActionForm actionForm);

    ClientMfiInfoDto retrieveMfiInfoForEdit(String clientSystemId, UserContext userContext);

    void updateClientMfiInfo(Integer clientId, Integer oldVersionNumber, UserContext userContext, ClientCustActionForm actionForm);
}