package org.mifos.application.customer.business;

import java.sql.Date;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanPerformanceHistoryEntity;
import org.mifos.application.accounts.loan.persistance.LoanPersistance;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientPerformanceHistoryEntity;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryEntity;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCustomerBO extends MifosTestCase {
	private AccountBO accountBO;
	private CenterBO center;
	private GroupBO group;
	private ClientBO client;
	private CustomerPersistence customerPersistence;
	LoanPersistance loanPersistence;
	private MeetingBO meeting;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		customerPersistence = new CustomerPersistence();
		loanPersistence = new LoanPersistance();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		
		HibernateUtil.closeSession();
	}
	
	private void createInitialObjects() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short
				.valueOf("13"), "1.4", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", GroupConstants.ACTIVE, "1.4.1", center, new Date(System
				.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",ClientConstants.STATUS_ACTIVE,"1.4.1.1",group,new Date(System
				.currentTimeMillis()));
	}
	
	private AccountBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short
						.valueOf("1"), 300.0, 1.2, Short.valueOf("3"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", customer, Short
				.valueOf("5"), startDate, loanOffering);

	}
	
	public void testGroupPerfObject() throws PersistenceException {
		createInitialObjects();
		GroupPerformanceHistoryEntity groupPerformanceHistory = new GroupPerformanceHistoryEntity();
		groupPerformanceHistory.setPerformanceHistoryDetails(Integer.valueOf("1"),new Money("100"),new Money("200"),new Money("300"),new Money("400"),new Money("500"));
		groupPerformanceHistory.setGroup(group);
		group.setGroupPerformanceHistory(groupPerformanceHistory);
		TestObjectFactory.updateObject(group);
		group = (GroupBO)customerPersistence.getBySystemId("Group",group.getCustomerLevel().getLevelId());
		assertEquals(group.getCustomerId(),group.getGroupPerformanceHistory().getCustomerId());
		assertEquals(Integer.valueOf("1"),group.getGroupPerformanceHistory().getClientCount());
		assertEquals(new Money("100"),group.getGroupPerformanceHistory().getLastGroupLoanAmount());
	}
	
	public void testClientPerfObject() throws PersistenceException {
		createInitialObjects();
		ClientPerformanceHistoryEntity clientPerformanceHistory = new ClientPerformanceHistoryEntity();
		clientPerformanceHistory.setPerformanceHistoryDetails(Integer.valueOf("1"),Integer.valueOf("1"),new Money("100"),new Money("200"),new Money("300"));
		clientPerformanceHistory.setClient(client);
		client.setClientPerformanceHistory(clientPerformanceHistory);
		TestObjectFactory.updateObject(client);
		client = (ClientBO)customerPersistence.getBySystemId("Client",client.getCustomerLevel().getLevelId());
		assertEquals(client.getCustomerId(),client.getClientPerformanceHistory().getCustomerId());
		assertEquals(Integer.valueOf("1"),client.getClientPerformanceHistory().getLoanCycleNumber());
		assertEquals(new Money("100"),client.getClientPerformanceHistory().getLastLoanAmount());
	}
	
	public void testLoanPerfObject() throws PersistenceException {
		Date currentDate = new Date(System.currentTimeMillis());
		createInitialObjects();
		accountBO = getLoanAccount(client,meeting);
		LoanPerformanceHistoryEntity loanPerformanceHistory = new LoanPerformanceHistoryEntity();
		loanPerformanceHistory.setPerformanceHistoryDetails(Integer.valueOf("1"),Integer.valueOf("1"),Integer.valueOf("1"),currentDate);
		LoanBO loanBO = (LoanBO)accountBO;
		loanPerformanceHistory.setLoan(loanBO);
		loanBO.setLoanPerformanceHistory(loanPerformanceHistory);
		TestObjectFactory.updateObject(loanBO);
		
		loanBO = (LoanBO) new AccountPersistanceService().getAccount(loanBO.getAccountId());
		assertEquals(loanBO.getAccountId(),loanBO.getLoanPerformanceHistory().getAccountId());
		assertEquals(Integer.valueOf("1"),loanBO.getLoanPerformanceHistory().getDays_in_arrears());
		assertEquals(Integer.valueOf("1"),loanBO.getLoanPerformanceHistory().getNo_of_missed_payments());
		assertEquals(Integer.valueOf("1"),loanBO.getLoanPerformanceHistory().getNo_of_payments());
		assertEquals(currentDate,loanBO.getLoanPerformanceHistory().getLoan_maturity_date());
	}

}
