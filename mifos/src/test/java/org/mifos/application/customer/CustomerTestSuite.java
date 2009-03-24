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
import org.mifos.application.customer.business.TestCustomerBO;
import org.mifos.application.customer.business.TestCustomerTrxnDetailEntity;
import org.mifos.application.customer.business.service.TestCustomerBusinessService;
import org.mifos.application.customer.center.business.CenterBOTest;
import org.mifos.application.customer.center.business.service.TestCenterBusinessService;
import org.mifos.application.customer.center.persistence.TestCenterPersistence;
import org.mifos.application.customer.center.struts.action.CenterActionTest;
import org.mifos.application.customer.client.business.ClientIntegrationTest;
import org.mifos.application.customer.client.business.ClientBoTest;
import org.mifos.application.customer.client.business.service.ClientBusinessServiceTest;
import org.mifos.application.customer.client.persistence.ClientPersistenceTest;
import org.mifos.application.customer.client.struts.action.ClientTransferActionTest;
import org.mifos.application.customer.client.struts.action.TestClientCustAction;
import org.mifos.application.customer.group.business.GroupBOTest;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryEntityTest;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryUpdaterTest;
import org.mifos.application.customer.group.business.service.GroupBusinessServiceTest;
import org.mifos.application.customer.group.persistence.GroupPersistenceTest;
import org.mifos.application.customer.group.struts.action.AddGroupMembershipActionTest;
import org.mifos.application.customer.group.struts.action.GroupActionTest;
import org.mifos.application.customer.group.struts.action.GroupTransferActionTest;
import org.mifos.application.customer.persistence.TestCustomerPersistence;
import org.mifos.application.customer.struts.action.CustActionTest;
import org.mifos.application.customer.struts.action.CustHistoricalDataActionTest;
import org.mifos.application.customer.struts.action.TestCustSearchAction;
import org.mifos.application.customer.struts.action.TestCustomerAccountAction;
import org.mifos.application.customer.struts.action.TestCustomerAction;
import org.mifos.application.customer.struts.action.TestCustomerApplyAdjustmentAction;
import org.mifos.application.customer.struts.action.TestCustomerNotesAction;
import org.mifos.application.customer.struts.action.TestEditCustomerStatusAction;
import org.mifos.application.customer.struts.uihelpers.CustomerUIHelperFnTest;
import org.mifos.application.customer.util.helpers.TestCustomerHelpers;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    TestCenterPersistence.class,
    TestCustomerPersistence.class,
    TestCustomerBusinessService.class,
    TestCustomerTrxnDetailEntity.class,
    TestCustomerApplyAdjustmentAction.class,
    TestCustomerAction.class,
    TestEditCustomerStatusAction.class,
    TestCustomerNotesAction.class,
    ClientIntegrationTest.class,
    ClientBoTest.class,
    TestCustomerBO.class,
    CustomerUIHelperFnTest.class,
    CenterBOTest.class,
    TestCenterBusinessService.class,
    CenterActionTest.class,
    TestClientCustAction.class,
    ClientTransferActionTest.class,
    AddGroupMembershipActionTest.class,
    CustHistoricalDataActionTest.class,
    GroupBusinessServiceTest.class,
    GroupPersistenceTest.class,
    GroupActionTest.class,
    GroupBOTest.class,
    CustActionTest.class,
    GroupTransferActionTest.class,
    TestCustomerAccountAction.class,
    ClientPersistenceTest.class,
    ClientBusinessServiceTest.class,
    TestCustSearchAction.class,
    CustomerViewTest.class,
    TestCustomerHelpers.class,
    GroupPerformanceHistoryUpdaterTest.class,
    GroupPerformanceHistoryEntityTest.class   
})

public class CustomerTestSuite extends TestSuite {
    // placeholder class for above annotations
}
