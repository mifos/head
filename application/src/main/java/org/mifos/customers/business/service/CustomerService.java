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

package org.mifos.customers.business.service;

import java.util.List;

import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.CenterUpdate;
import org.mifos.application.servicefacade.GroupUpdate;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.security.util.UserContext;

public interface CustomerService {

    void updateCenter(UserContext userContext, CenterUpdate centerUpdate, CenterBO center);

    void create(CustomerBO customer, MeetingBO meeting, List<AccountFeesEntity> accountFees);

    void createGroup(GroupBO group, MeetingBO meeting, List<AccountFeesEntity> accountFees) throws CustomerException;

    void updateGroup(UserContext userContext, GroupUpdate groupUpdate, GroupBO groupBO) throws CustomerException;

    GroupBO transferGroupTo(GroupBO group, CenterBO transferToCenter) throws CustomerException;

    GroupBO transferGroupTo(GroupBO group, OfficeBO transferToOffice) throws CustomerException;

    void updateCenterStatus(CenterBO center, CustomerStatus newStatus) throws CustomerException;

    void updateGroupStatus(GroupBO group, CustomerStatus oldStatus, CustomerStatus newStatus) throws CustomerException;

    void updateClientStatus(ClientBO client, CustomerStatus oldStatus, CustomerStatus newStatus, UserContext userContext, Short flagId, String notes) throws CustomerException;
}
