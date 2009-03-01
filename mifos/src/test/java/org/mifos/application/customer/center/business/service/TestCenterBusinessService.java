package org.mifos.application.customer.center.business.service;

import java.util.Date;

import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCenterBusinessService extends MifosTestCase {
	public TestCenterBusinessService() throws SystemException, ApplicationException {
        super();
    }

    private CustomerBO center;
	
	private CustomerBO group;
	
	private CustomerBO client;
	
	private SavingsTestHelper helper = new SavingsTestHelper();

	private SavingsOfferingBO savingsOffering;

	private SavingsBO savingsBO;

	private CenterBusinessService service;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		service = (CenterBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Center);
	}

	@Override
	public void tearDown() throws Exception {		
		try {
			TestObjectFactory.cleanUp(savingsBO);
			TestObjectFactory.cleanUp(client);
			TestObjectFactory.cleanUp(group);
			TestObjectFactory.cleanUp(center);
		} catch (Exception e) {
			// TODO Whoops, cleanup didnt work, reset db
			TestDatabase.resetMySQLDatabase();
		}
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	public void testGetCenter() throws Exception {
		center = createCenter("center1");
		createAccountsForCenter();
		savingsBO = getSavingsAccount(center,"fsaf6","ads6");
		HibernateUtil.closeSession();
		center = service.getCenter(center.getCustomerId());
		assertNotNull(center);
		assertEquals("center1", center.getDisplayName());
		assertEquals(2, center.getAccounts().size());
		assertEquals(0, center.getOpenLoanAccounts().size());
		assertEquals(1, center.getOpenSavingAccounts().size());
		assertEquals(CustomerStatus.CENTER_ACTIVE.getValue(), center.getCustomerStatus().getId());
		HibernateUtil.closeSession();
		retrieveAccountsToDelete();
	}
	
	public void testSuccessfulGet() throws Exception {
		center = createCenter("Center2");
		createAccountsForCenter();
		savingsBO = getSavingsAccount(center,"fsaf6","ads6");
		HibernateUtil.closeSession();
		center = service.getCenter(center.getCustomerId());
		assertNotNull(center);
		assertEquals("Center2", center.getDisplayName());
		assertEquals(2, center.getAccounts().size());
		assertEquals(0, center.getOpenLoanAccounts().size());
		assertEquals(1, center.getOpenSavingAccounts().size());
		assertEquals(CustomerStatus.CENTER_ACTIVE.getValue(), center.getCustomerStatus().getId());
		HibernateUtil.closeSession();
		retrieveAccountsToDelete();
	}

	public void testFailureGet() throws Exception {
		center = createCenter("Center1");
		HibernateUtil.closeSession();
		TestObjectFactory.simulateInvalidConnection();
		try {
			service.getCenter(center.getCustomerId());
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}finally{
			HibernateUtil.closeSession();
			}
	}
	
	public void testFailureFindBySystemId() throws Exception {
		center = createCenter("Center1");
		HibernateUtil.closeSession();
		TestObjectFactory.simulateInvalidConnection();
		try {
			service.findBySystemId(center.getGlobalCustNum());
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}finally{
		HibernateUtil.closeSession();
		}
	}
	
	public void testSearch() throws Exception{
		center = createCenter("center1");
	  QueryResult queryResult = 	service.search("center1",Short.valueOf("1"));
	  assertEquals(1,queryResult.getSize());
	  assertEquals(1,queryResult.get(0,10).size());	
	}
	
	public void testFailureSearch() throws Exception {
		center = createCenter("Center1");
		TestObjectFactory.simulateInvalidConnection();
		try {
			service.search("center1",Short.valueOf("1"));
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}finally{
		HibernateUtil.closeSession();
		}
	}
	private SavingsBO getSavingsAccount(CustomerBO customerBO,String offeringName,String shortName) throws Exception {
		savingsOffering = helper.createSavingsOffering(offeringName,shortName);
		return TestObjectFactory.createSavingsAccount("000100000000017", customerBO,
				AccountStates.SAVINGS_ACC_APPROVED, new Date(System
						.currentTimeMillis()), savingsOffering);
	}
	
	private CenterBO createCenter(String name) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		return TestObjectFactory.createCenter(name, meeting);
	}
	
	private GroupBO createGroup(String groupName){
		return TestObjectFactory.createGroupUnderCenter(groupName, CustomerStatus.GROUP_ACTIVE, center);
	}
	
	private ClientBO createClient(String clientName){
		return TestObjectFactory.createClient(clientName,
				CustomerStatus.CLIENT_ACTIVE, 
				group);
	}
	
	private void createAccountsForCenter() throws Exception {
		String groupName = "Group_Active_test";
		group = createGroup(groupName);
		client = createClient("Client_Active_test");
	}
	
	private void retrieveAccountsToDelete() {
		savingsBO = TestObjectFactory.getObject(SavingsBO.class, savingsBO.getAccountId());
		center = TestObjectFactory.getCenter(center.getCustomerId());
		group = TestObjectFactory.getGroup(group.getCustomerId());
		client = TestObjectFactory.getClient(client.getCustomerId());
	}
}
