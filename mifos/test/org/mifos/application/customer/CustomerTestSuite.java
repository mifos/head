package org.mifos.application.customer;

import junit.framework.Test;
import junit.textui.TestRunner;

import org.mifos.application.customer.business.TestCustomerBO;
import org.mifos.application.customer.business.TestCustomerTrxnDetailEntity;
import org.mifos.application.customer.business.service.TestCustomerBusinessService;
import org.mifos.application.customer.center.business.CenterBOTest;
import org.mifos.application.customer.center.struts.action.CenterActionTest;
import org.mifos.application.customer.client.business.TestClientBO;
import org.mifos.application.customer.client.struts.action.ClientTransferActionTest;
import org.mifos.application.customer.client.struts.action.TestClientCustAction;
import org.mifos.application.customer.group.TestGroupBO;
import org.mifos.application.customer.group.business.service.GroupBusinessServiceTest;
import org.mifos.application.customer.group.persistence.GroupPersistenceTest;
import org.mifos.application.customer.group.struts.action.GroupActionTest;
import org.mifos.application.customer.persistence.TestCenterPersistence;
import org.mifos.application.customer.persistence.TestCustomerPersistence;
import org.mifos.application.customer.struts.action.CustHistoricalDataActionTest;
import org.mifos.application.customer.struts.action.TestCustomerAction;
import org.mifos.application.customer.struts.action.TestCustomerApplyAdjustmentAction;
import org.mifos.application.customer.struts.action.TestCustomerNotesAction;
import org.mifos.application.customer.struts.action.TestEditCustomerStatusAction;
import org.mifos.framework.MifosTestSuite;

public class CustomerTestSuite extends MifosTestSuite {

	public CustomerTestSuite() throws Exception {
		super();
		
	}
	public static void main(String[] args){
		try{
			Test testSuite = suite();
			TestRunner.run (testSuite);	
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static Test suite()throws Exception
	{
		CustomerTestSuite testSuite = new CustomerTestSuite();
		testSuite.addTestSuite(TestCenterPersistence.class);
		testSuite.addTestSuite(TestCustomerPersistence.class);
		testSuite.addTestSuite(TestCustomerBusinessService.class);
		testSuite.addTestSuite(TestCustomerTrxnDetailEntity.class);
		testSuite.addTestSuite(TestCustomerApplyAdjustmentAction.class);
		testSuite.addTestSuite(TestCustomerAction.class);
		testSuite.addTestSuite(TestEditCustomerStatusAction.class);
		testSuite.addTestSuite(TestCustomerNotesAction.class);
		testSuite.addTestSuite(TestClientBO.class);
		testSuite.addTestSuite(TestCustomerBO.class);
		testSuite.addTestSuite(TestGroupBO.class);
		testSuite.addTestSuite(CenterBOTest.class);
		testSuite.addTestSuite(CenterActionTest.class);
		testSuite.addTestSuite(TestClientCustAction.class);
		testSuite.addTestSuite(ClientTransferActionTest.class);
		testSuite.addTestSuite(CustHistoricalDataActionTest.class);
		testSuite.addTestSuite(GroupBusinessServiceTest.class);
		testSuite.addTestSuite(GroupPersistenceTest.class);
		testSuite.addTestSuite(GroupActionTest.class);
		return testSuite;
		
	}
}
