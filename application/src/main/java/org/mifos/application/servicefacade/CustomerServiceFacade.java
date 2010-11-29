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

import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.struts.actionforms.ClientCustActionForm;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.struts.action.GroupSearchResultsDto;
import org.mifos.dto.screen.OnlyBranchOfficeHierarchyDto;
import org.mifos.framework.exceptions.ApplicationException;

public interface CustomerServiceFacade {

    OnlyBranchOfficeHierarchyDto retrieveBranchOnlyOfficeHierarchy();

    CustomerSearch search(String searchString) throws ApplicationException;

    GroupBO transferGroupToCenter(String globalCustNum, String centerSystemId, Integer previousGroupVersionNo) throws ApplicationException;

    GroupBO transferGroupToBranch(String globalCustNum, Short officeIdValue, Integer previousGroupVersionNo) throws ApplicationException;

    ClientBO transferClientToGroup(Integer parentGroupIdValue, String clientGlobalCustNum, Integer previousClientVersionNo) throws ApplicationException;

    boolean isGroupHierarchyRequired();

    GroupSearchResultsDto searchGroups(boolean searchForAddingClientsToGroup, String normalizedSearchString, Short loggedInUserId);

    ClientMfiInfoDto retrieveMfiInfoForEdit(String clientSystemId);

    void updateClientMfiInfo(Integer clientId, Integer oldVersionNumber, ClientCustActionForm actionForm) throws ApplicationException;
}