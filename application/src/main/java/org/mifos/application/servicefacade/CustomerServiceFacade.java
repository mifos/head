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
import org.mifos.customers.business.CustomerCustomFieldEntity;
import org.mifos.customers.center.struts.actionforms.CenterCustActionForm;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.struts.actionforms.ClientCustActionForm;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.struts.action.GroupSearchResultsDto;
import org.mifos.customers.group.struts.actionforms.GroupCustActionForm;
import org.mifos.customers.util.helpers.SavingsDetailDto;
import org.mifos.dto.screen.OnlyBranchOfficeHierarchyDto;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.security.util.UserContext;

public interface CustomerServiceFacade {

    OnlyBranchOfficeHierarchyDto retrieveBranchOnlyOfficeHierarchy();

    CenterFormCreationDto retrieveCenterFormCreationData(CenterCreation centerCreation);

    GroupFormCreationDto retrieveGroupFormCreationData(GroupCreation groupCreation);

    ClientFormCreationDto retrieveClientFormCreationData(Short groupFlag, Short officeId, String parentGroupId);

    CustomerDetailsDto createNewCenter(CenterCustActionForm actionForm, MeetingBO meeting, List<CustomerCustomFieldEntity> customerCustomFields) throws ApplicationException;

    CustomerDetailsDto createNewGroup(GroupCustActionForm actionForm, MeetingBO meeting, List<CustomerCustomFieldEntity> customerCustomFields) throws ApplicationException;

    CustomerDetailsDto createNewClient(ClientCustActionForm actionForm, MeetingBO meeting, List<SavingsDetailDto> allowedSavingProducts, List<CustomerCustomFieldEntity> customFields) throws ApplicationException;

    CenterDto retrieveCenterDetailsForUpdate(Integer centerId);

    CenterDto retrieveGroupDetailsForUpdate(String globalCustNum);

    void updateCenter(CenterUpdate centerUpdate) throws ApplicationException;

    void updateGroup(GroupUpdate groupUpdate) throws ApplicationException;

    CustomerSearch search(String searchString) throws ApplicationException;

    CenterHierarchySearchDto isCenterHierarchyConfigured(Short loggedInUserBranchId);

    GroupBO transferGroupToCenter(String globalCustNum, String centerSystemId, Integer previousGroupVersionNo) throws ApplicationException;

    GroupBO transferGroupToBranch(String globalCustNum, Short officeIdValue, Integer previousGroupVersionNo) throws ApplicationException;

    ClientBO transferClientToGroup(Integer parentGroupIdValue, String clientGlobalCustNum, Integer previousClientVersionNo) throws ApplicationException;

    void updateCustomerStatus(Integer customerId, Integer versionNo, String flagId, String newStatusId, String notes) throws ApplicationException;

    boolean isGroupHierarchyRequired();

    GroupSearchResultsDto searchGroups(boolean searchForAddingClientsToGroup, String normalizedSearchString, Short loggedInUserId);

    ClientFamilyDetailsDto retrieveClientFamilyDetails();

    ProcessRulesDto previewClient(String governmentId, DateTime dateOfBirth, String clientName);

    ClientPersonalInfoDto retrieveClientPersonalInfoForUpdate(String clientSystemId);

    ClientRulesDto retrieveClientDetailsForPreviewingEditOfPersonalInfo();

    void updateClientPersonalInfo(UserContext userContext, Integer oldClientVersionNumber, Integer customerId,
            ClientCustActionForm actionForm) throws ApplicationException;

    ClientFamilyInfoDto retrieveFamilyInfoForEdit(String globalCustNum);

    void updateFamilyInfo(Integer customerId, Integer versionNo, ClientCustActionForm actionForm) throws ApplicationException;

    ClientMfiInfoDto retrieveMfiInfoForEdit(String clientSystemId);

    void updateClientMfiInfo(Integer clientId, Integer oldVersionNumber, ClientCustActionForm actionForm) throws ApplicationException;
}