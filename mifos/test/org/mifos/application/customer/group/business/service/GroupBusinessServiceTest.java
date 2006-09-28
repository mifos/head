package org.mifos.application.customer.group.business.service;

import java.sql.Date;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryEntity;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class GroupBusinessServiceTest extends MifosTestCase {
	private MeetingBO meeting;

	private CustomerBO center;

	private CustomerBO group;
	
	private CustomerBO client;
	
	private SavingsTestHelper helper = new SavingsTestHelper();

	private SavingsOfferingBO savingsOffering;

	private LoanBO loanBO;

	private SavingsBO savingsBO1;
	
	private SavingsBO savingsBO2;

	private GroupBusinessService groupBusinessService;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		groupBusinessService = (GroupBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Group);
	}

	@Override
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(loanBO);
		TestObjectFactory.cleanUp(savingsBO1);
		TestObjectFactory.cleanUp(savingsBO2);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetGroupBySystemId() throws Exception{
		center = createCenter("Center_Active_test");
		String groupName = "Group_Active_test";
		group = createGroup(groupName);
		client = createClient("Client_Active_test");
		savingsBO2 = getSavingsAccount(center,"fsaf5","ads5");
		savingsBO1 = getSavingsAccount(group,"fsaf6","ads6");
		loanBO = getLoanAccount(group);
		HibernateUtil.closeSession();
		loanBO = (LoanBO) TestObjectFactory.getObject(LoanBO.class, loanBO.getAccountId());
		savingsBO2 = (SavingsBO) TestObjectFactory.getObject(SavingsBO.class, savingsBO2.getAccountId());
		savingsBO1 = (SavingsBO) TestObjectFactory.getObject(SavingsBO.class, savingsBO1.getAccountId());
		group = groupBusinessService.getGroupBySystemId(group.getGlobalCustNum());
		assertNotNull(group);
		assertEquals(groupName, group.getDisplayName());
		assertEquals(3,group.getAccounts().size());
		assertEquals(1,group.getOpenLoanAccounts().size());
		assertEquals(1,group.getOpenSavingAccounts().size());
		assertEquals(CustomerStatus.GROUP_ACTIVE.getValue(),group.getCustomerStatus().getId());
		assertEquals(1,((GroupPerformanceHistoryEntity)group.getPerformanceHistory()).getActiveClientCount().intValue());
		HibernateUtil.closeSession();
		loanBO = (LoanBO) TestObjectFactory.getObject(LoanBO.class, loanBO.getAccountId());
		savingsBO1 = (SavingsBO) TestObjectFactory.getObject(SavingsBO.class, savingsBO1.getAccountId());
		savingsBO2 = (SavingsBO) TestObjectFactory.getObject(SavingsBO.class, savingsBO2.getAccountId());
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class,	center.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class,	client.getCustomerId());
	}

	public void testSuccessfulGet() throws Exception {
		center = createCenter("Center_Active_test");
		String groupName = "Group_Active_test";
		group = createGroup(groupName);
		client = createClient("Client_Active_test");
		savingsBO1 = getSavingsAccount(group,"fsaf6","ads6");
		loanBO = getLoanAccount(group);
		HibernateUtil.closeSession();
		loanBO = (LoanBO) TestObjectFactory.getObject(LoanBO.class, loanBO.getAccountId());
		savingsBO1 = (SavingsBO) TestObjectFactory.getObject(SavingsBO.class, savingsBO1.getAccountId());
		group = groupBusinessService.getGroup(group.getCustomerId());
		assertNotNull(group);
		assertEquals(groupName, group.getDisplayName());
		assertEquals(3,group.getAccounts().size());
		assertEquals(1,group.getOpenLoanAccounts().size());
		assertEquals(1,group.getOpenSavingAccounts().size());
		assertEquals(CustomerStatus.GROUP_ACTIVE.getValue(),group.getCustomerStatus().getId());
		assertEquals(1,((GroupPerformanceHistoryEntity)group.getPerformanceHistory()).getActiveClientCount().intValue());
		HibernateUtil.closeSession();
		loanBO = (LoanBO) TestObjectFactory.getObject(LoanBO.class, loanBO.getAccountId());
		savingsBO1 = (SavingsBO) TestObjectFactory.getObject(SavingsBO.class, savingsBO1.getAccountId());
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class,	center.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class,	client.getCustomerId());
	}

	public void testFailureGet() throws Exception {
		center = createCenter("Center_Active_test");
		String groupName = "Group_Active_test";
		group = createGroup(groupName);
		HibernateUtil.closeSession();
		TestObjectFactory.simulateInvalidConnection();
		try {
			groupBusinessService.getGroup(group.getCustomerId());
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();
	}
	
	private GroupBO createGroup(String groupName){
		return TestObjectFactory.createGroup(groupName, CustomerStatus.GROUP_ACTIVE.getValue(), 
				"1.1.1", center, new Date(System.currentTimeMillis()));
	}
	
	private CenterBO createCenter(String name) {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createCenter(name, CustomerStatus.CENTER_ACTIVE.getValue(), "1.4",
				meeting, new Date(System.currentTimeMillis()));
	}
	
	private ClientBO createClient(String clientName){
		return TestObjectFactory.createClient(clientName,CustomerStatus.CLIENT_ACTIVE.getValue(), 
				"1.1.1", group, new Date(System.currentTimeMillis()));
	}
	
	private LoanBO getLoanAccount(CustomerBO customerBO) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", customerBO, Short
				.valueOf("5"), startDate, loanOffering);

	}

	private SavingsBO getSavingsAccount(CustomerBO customerBO,String offeringName,String shortName) throws Exception {
		savingsOffering = helper.createSavingsOffering(offeringName,shortName);
		return TestObjectFactory.createSavingsAccount("000100000000017", customerBO,
				AccountStates.SAVINGS_ACC_APPROVED, new Date(System
						.currentTimeMillis()), savingsOffering);
	}
}
