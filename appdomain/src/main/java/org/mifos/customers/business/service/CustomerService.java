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

package org.mifos.customers.business.service;

import java.util.List;

import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.CustomerStatusUpdate;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerNoteEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.CustomerStatusFlag;
import org.mifos.dto.domain.CenterUpdate;
import org.mifos.dto.domain.ClientFamilyInfoUpdate;
import org.mifos.dto.domain.ClientMfiInfoUpdate;
import org.mifos.dto.domain.ClientPersonalInfoUpdate;
import org.mifos.dto.domain.GroupUpdate;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.security.util.UserContext;

public interface CustomerService {

    void createCenter(CenterBO center, MeetingBO meeting, List<AccountFeesEntity> accountFees);

    void createGroup(GroupBO group, MeetingBO meeting, List<AccountFeesEntity> accountFees) throws CustomerException;

    void createClient(ClientBO client, MeetingBO meeting, List<AccountFeesEntity> accountFees, List<SavingsOfferingBO> selectedOfferings) throws CustomerException;

    void updateCenter(UserContext userContext, CenterUpdate centerUpdate) throws ApplicationException;

    void updateGroup(UserContext userContext, GroupUpdate groupUpdate) throws ApplicationException;

    String transferGroupTo(GroupBO group, CenterBO transferToCenter) throws CustomerException;

    String transferGroupTo(GroupBO group, OfficeBO transferToOffice) throws CustomerException;

    ClientBO transferClientTo(UserContext userContext, Integer groupId, String clientGlobalCustNum, Integer previousClientVersionNo)  throws CustomerException;

    void updateCustomerStatus(UserContext userContext, CustomerStatusUpdate customerStatusUpdate) throws CustomerException;

    void updateCenterStatus(CenterBO center, CustomerStatus newStatus, CustomerStatusFlag customerStatusFlag, CustomerNoteEntity customerNote) throws CustomerException;

    void updateGroupStatus(GroupBO group, CustomerStatus oldStatus, CustomerStatus newStatus, CustomerStatusFlag customerStatusFlag, CustomerNoteEntity customerNote) throws CustomerException;

    void updateClientStatus(ClientBO client, CustomerStatus oldStatus, CustomerStatus newStatus, CustomerStatusFlag customerStatusFlag, CustomerNoteEntity customerNote) throws CustomerException;

    void updateClientPersonalInfo(UserContext userContext, ClientPersonalInfoUpdate personalInfo) throws CustomerException;
    
    void removeFromBlacklist(UserContext userContext, Integer customerId);

    void updateClientFamilyInfo(UserContext userContext, ClientFamilyInfoUpdate clientFamilyInfoUpdate) throws CustomerException;

    void updateClientMfiInfo(UserContext userContext, ClientMfiInfoUpdate clientMfiInfoUpdate) throws CustomerException;

    void updateCustomerMeetingSchedule(MeetingBO meeting, CustomerBO customer);

    void removeGroupMembership(ClientBO client, PersonnelBO loanOfficer, CustomerNoteEntity accountNotesEntity, Short localeId);

    void transferClientTo(ClientBO client, OfficeBO receivingBranch);
}