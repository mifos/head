/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.application.customer;

import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mifos.application.customer.business.CustomerViewTest;
import org.mifos.application.customer.business.CustomerBOIntegrationTest;
import org.mifos.application.customer.business.CustomerTrxnDetailEntityIntegrationTest;
import org.mifos.application.customer.business.service.CustomerBusinessServiceIntegrationTest;
import org.mifos.application.customer.center.business.CenterBOIntegrationTest;
import org.mifos.application.customer.center.business.service.CenterBusinessServiceIntegrationTest;
import org.mifos.application.customer.center.persistence.CenterPersistenceIntegrationTest;
import org.mifos.application.customer.center.struts.action.CenterActionTest;
import org.mifos.application.customer.client.business.ClientIntegrationTest;
import org.mifos.application.customer.client.business.ClientBoTest;
import org.mifos.application.customer.client.business.service.ClientBusinessServiceIntegrationTest;
import org.mifos.application.customer.client.persistence.ClientPersistenceIntegrationTest;
import org.mifos.application.customer.client.struts.action.ClientTransferActionTest;
import org.mifos.application.customer.client.struts.action.ClientCustActionTest;
import org.mifos.application.customer.group.business.GroupBOIntegrationTest;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryEntityIntegrationTest;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryUpdaterIntegrationTest;
import org.mifos.application.customer.group.business.service.GroupBusinessServiceIntegrationTest;
import org.mifos.application.customer.group.persistence.GroupPersistenceIntegrationTest;
import org.mifos.application.customer.group.struts.action.AddGroupMembershipActionTest;
import org.mifos.application.customer.group.struts.action.GroupActionTest;
import org.mifos.application.customer.group.struts.action.GroupTransferActionTest;
import org.mifos.application.customer.persistence.CustomerPersistenceIntegrationTest;
import org.mifos.application.customer.struts.action.CustActionTest;
import org.mifos.application.customer.struts.action.CustHistoricalDataActionTest;
import org.mifos.application.customer.struts.action.CustSearchActionTest;
import org.mifos.application.customer.struts.action.CustomerAccountActionTest;
import org.mifos.application.customer.struts.action.CustomerActionTest;
import org.mifos.application.customer.struts.action.CustomerApplyAdjustmentActionTest;
import org.mifos.application.customer.struts.action.CustomerNotesActionTest;
import org.mifos.application.customer.struts.action.EditCustomerStatusActionTest;
import org.mifos.application.customer.struts.uihelpers.CustomerUIHelperFnTest;
import org.mifos.application.customer.util.helpers.CustomerHelpersIntegrationTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { CenterPersistenceIntegrationTest.class, CustomerPersistenceIntegrationTest.class,
        CustomerBusinessServiceIntegrationTest.class, CustomerTrxnDetailEntityIntegrationTest.class,
        CustomerApplyAdjustmentActionTest.class, CustomerActionTest.class, EditCustomerStatusActionTest.class,
        CustomerNotesActionTest.class, ClientIntegrationTest.class, ClientBoTest.class,
        CustomerBOIntegrationTest.class, CustomerUIHelperFnTest.class, CenterBOIntegrationTest.class,
        CenterBusinessServiceIntegrationTest.class, CenterActionTest.class, ClientCustActionTest.class,
        ClientTransferActionTest.class, AddGroupMembershipActionTest.class, CustHistoricalDataActionTest.class,
        GroupBusinessServiceIntegrationTest.class, GroupPersistenceIntegrationTest.class, GroupActionTest.class,
        GroupBOIntegrationTest.class, CustActionTest.class, GroupTransferActionTest.class,
        CustomerAccountActionTest.class, ClientPersistenceIntegrationTest.class,
        ClientBusinessServiceIntegrationTest.class, CustSearchActionTest.class, CustomerViewTest.class,
        CustomerHelpersIntegrationTest.class, GroupPerformanceHistoryUpdaterIntegrationTest.class,
        GroupPerformanceHistoryEntityIntegrationTest.class })
public class CustomerTestSuite extends TestSuite {
    // placeholder class for above annotations
}
