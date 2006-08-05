package org.mifos.application.accounts.business;

import java.sql.Date;
import java.util.List;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.StatesInitializationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestAccountStateMachine extends MifosTestCase {

	private AccountBO accountBO;
	private CenterBO center;
	private GroupBO group;
	private ClientBO client;
	private SavingsPersistence savingsPersistence;
	private MeetingBO meeting;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		savingsPersistence = new SavingsPersistence();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetStatusList() throws StatesInitializationException {
		createInitialObjects();
		accountBO  = getLoanAccount(client,meeting);
		AccountStateMachines.getInstance().initialize((short) 1, (short) 1,AccountTypes.LOANACCOUNT.getValue());
		List<AccountStateEntity> stateList = accountBO.getStatusList();
		assertEquals(2,stateList.size());
	}
	
	public void testGetStatusName() throws ApplicationException, SystemException {
		createInitialObjects();
		accountBO  = getLoanAccount(client,meeting);
		AccountStateMachines.getInstance().initialize((short) 1, (short) 1,AccountTypes.LOANACCOUNT.getValue());
		assertEquals("Closed- Rescheduled",accountBO.getStatusName((short) 1, (short) 8));
	}
	
	public void testGetFlagName() throws ApplicationException, SystemException {
		createInitialObjects();
		accountBO  = getLoanAccount(client,meeting);
		AccountStateMachines.getInstance().initialize((short) 1, (short) 1,AccountTypes.LOANACCOUNT.getValue());
		assertEquals("Withdraw",accountBO.getFlagName((short) 1));
	}
	
	public void testIsTransitionAllowed() throws StatesInitializationException, ApplicationException {
		createInitialObjects();
		accountBO  = getLoanAccount(client,meeting);
		AccountStateEntity accountStateEntity1 = new AccountStateEntity(Short.valueOf("8"));
		AccountStateEntity accountStateEntity2 = new AccountStateEntity(Short.valueOf("10"));
		AccountStateMachines.getInstance().initialize((short) 1, (short) 1,AccountTypes.LOANACCOUNT.getValue());
		assertTrue("This state change is allowed",AccountStateMachines.getInstance().isTransitionAllowed(accountBO,accountStateEntity1));
		assertFalse("This state change is not allowed",AccountStateMachines.getInstance().isTransitionAllowed(accountBO,accountStateEntity2));
		
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
	
	private LoanBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short
						.valueOf("1"), 300.0, 1.2, Short.valueOf("3"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", customer, Short
				.valueOf("5"), startDate, loanOffering);

	}
}
