package org.mifos.application.customer;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.customer.business.CustomerViewTest;
import org.mifos.application.customer.business.TestCustomerBO;
import org.mifos.application.customer.business.TestCustomerTrxnDetailEntity;
import org.mifos.application.customer.business.service.CustomerBusinessServiceTest;
import org.mifos.application.customer.center.business.CenterBOTest;
import org.mifos.application.customer.center.business.service.TestCenterBusinessService;
import org.mifos.application.customer.center.persistence.TestCenterPersistence;
import org.mifos.application.customer.center.struts.action.CenterActionTest;
import org.mifos.application.customer.client.business.TestClientBO;
import org.mifos.application.customer.client.business.service.ClientBusinessServiceTest;
import org.mifos.application.customer.client.persistence.ClientPersistenceTest;
import org.mifos.application.customer.client.struts.action.ClientTransferActionTest;
import org.mifos.application.customer.client.struts.action.TestClientCustAction;
import org.mifos.application.customer.group.business.GroupBOTest;
import org.mifos.application.customer.group.business.service.GroupBusinessServiceTest;
import org.mifos.application.customer.group.persistence.GroupPersistenceTest;
import org.mifos.application.customer.group.struts.action.AddGroupMembershipActionTest;
import org.mifos.application.customer.group.struts.action.GroupActionTest;
import org.mifos.application.customer.group.struts.action.GroupTransferActionTest;
import org.mifos.application.customer.persistence.CustomerPersistenceTest;
import org.mifos.application.customer.struts.action.CustActionTest;
import org.mifos.application.customer.struts.action.CustHistoricalDataActionTest;
import org.mifos.application.customer.struts.action.CustSearchActionTest;
import org.mifos.application.customer.struts.action.TestCustomerAccountAction;
import org.mifos.application.customer.struts.action.TestCustomerAction;
import org.mifos.application.customer.struts.action.TestCustomerApplyAdjustmentAction;
import org.mifos.application.customer.struts.action.TestCustomerNotesAction;
import org.mifos.application.customer.struts.action.TestEditCustomerStatusAction;
import org.mifos.application.customer.struts.uihelpers.CustomerUIHelperFnTest;
import org.mifos.application.customer.util.helpers.TestCustomerHelpers;

public class CustomerTestSuite extends TestSuite {

	public static Test suite() throws Exception {
		CustomerTestSuite testSuite = new CustomerTestSuite();
		testSuite.addTestSuite(TestCenterPersistence.class);
		testSuite.addTestSuite(CustomerPersistenceTest.class);
		testSuite.addTestSuite(CustomerBusinessServiceTest.class);
		testSuite.addTestSuite(TestCustomerTrxnDetailEntity.class);
		testSuite.addTestSuite(TestCustomerApplyAdjustmentAction.class);
		testSuite.addTestSuite(TestCustomerAction.class);
		testSuite.addTestSuite(TestEditCustomerStatusAction.class);
		testSuite.addTestSuite(TestCustomerNotesAction.class);
		testSuite.addTestSuite(TestClientBO.class);
		testSuite.addTestSuite(TestCustomerBO.class);
		testSuite.addTestSuite(CustomerUIHelperFnTest.class);
		testSuite.addTestSuite(CenterBOTest.class);
		testSuite.addTestSuite(TestCenterBusinessService.class);
		testSuite.addTestSuite(CenterActionTest.class);
		testSuite.addTestSuite(TestClientCustAction.class);
		testSuite.addTestSuite(ClientTransferActionTest.class);
		testSuite.addTestSuite(AddGroupMembershipActionTest.class);	
		testSuite.addTestSuite(CustHistoricalDataActionTest.class);
		testSuite.addTestSuite(GroupBusinessServiceTest.class);
		testSuite.addTestSuite(GroupPersistenceTest.class);
		testSuite.addTestSuite(GroupActionTest.class);
		testSuite.addTestSuite(GroupBOTest.class);
		testSuite.addTestSuite(CustActionTest.class);
		testSuite.addTestSuite(GroupTransferActionTest.class);
		testSuite.addTestSuite(TestCustomerAccountAction.class);
		testSuite.addTestSuite(ClientPersistenceTest.class);
		testSuite.addTestSuite(ClientBusinessServiceTest.class);
		testSuite.addTestSuite(CustSearchActionTest.class);
		testSuite.addTestSuite(CustomerViewTest.class);
		testSuite.addTestSuite(TestCustomerHelpers.class);

		return testSuite;
	}
}
