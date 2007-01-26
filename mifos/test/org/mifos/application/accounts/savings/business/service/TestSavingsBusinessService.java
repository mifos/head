package org.mifos.application.accounts.savings.business.service;

import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.PrdOfferingView;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestConstants;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSavingsBusinessService extends MifosTestCase {
	private SavingsBusinessService service;

	private CustomerBO center;

	private CustomerBO group;

	private SavingsBO savings;

	private SavingsOfferingBO savingsOffering;

	private SavingsOfferingBO savingsOffering1;

	private SavingsOfferingBO savingsOffering2;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		service = new SavingsBusinessService();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(savings);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.removeObject(savingsOffering1);
		TestObjectFactory.removeObject(savingsOffering2);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetSavingProducts() throws Exception {
		createInitialObjects();
		savingsOffering1 = createSavingsOffering("SavingPrd1", "cadf");
		savingsOffering2 = createSavingsOffering("SavingPrd2", "a1lt");
		List<PrdOfferingView> products = service.getSavingProducts(null, group
				.getCustomerLevel(), CustomerConstants.GROUP_LEVEL_ID);
		assertEquals(Integer.valueOf("2").intValue(), products.size());
		assertEquals("Offerng name for the first product do not match.",
				products.get(0).getPrdOfferingName(), "SavingPrd1");
		assertEquals("Offerng name for the second product do not match.",
				products.get(1).getPrdOfferingName(), "SavingPrd2");
	}

	public void testGetSavingProductsForInvalidConnection() {
		createInitialObjects();
		savingsOffering1 = createSavingsOffering("SavingPrd1", "cadf");
		savingsOffering2 = createSavingsOffering("SavingPrd2", "a1lt");
		TestObjectFactory.simulateInvalidConnection();
		try {
			service.getSavingProducts(null,
					group.getCustomerLevel(), CustomerConstants.GROUP_LEVEL_ID);
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}
	}

	public void testRetrieveCustomFieldsDefinition() throws Exception {
		List<CustomFieldDefinitionEntity> customFields = service
				.retrieveCustomFieldsDefinition();
		assertNotNull(customFields);
		assertEquals(TestConstants.SAVINGS_CUSTOMFIELDS_NUMBER, customFields
				.size());
	}

	public void testRetrieveCustomFieldsDefinitionForInvalidConnection() {
		TestObjectFactory.simulateInvalidConnection();
		try {
			service.retrieveCustomFieldsDefinition();
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}

	}

	public void testFindById() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering("SavingPrd1", "kh6y");
		savings = createSavingsAccount("FFFF", savingsOffering,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION);
		SavingsBO savings1 = service.findById(savings.getAccountId());
		assertNotNull(savings1);
	}

	public void testFindByIdForInvalidConnection() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering("SavingPrd1", "kh6y");
		savings = createSavingsAccount("FFFF", savingsOffering,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION);
		TestObjectFactory.simulateInvalidConnection();
		try {
			service.findById(savings.getAccountId());
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}

	}

	public void testFindBySystemId() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering("SavingPrd1", "cadf");
		savings = createSavingsAccount("YYYY", savingsOffering,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION);
		SavingsBO savings1 = service.findBySystemId(savings.getGlobalAccountNum());
		
		assertEquals(savings.getAccountId(), savings1.getAccountId());
	}

	public void testFindBySystemIdForInvalidConnection() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering("SavingPrd1", "cadf");
		savings = createSavingsAccount("YYYY", savingsOffering,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION);
		TestObjectFactory.simulateInvalidConnection();
		try {
			service.findBySystemId("YYYY");
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}
	}

	public void testGetAllClosedAccounts() throws Exception {
		createInitialObjects();
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());

		Date startDate = new Date(System.currentTimeMillis());
		savingsOffering = TestObjectFactory.createSavingsOffering("SavingPrd1",
				Short.valueOf("2"), new Date(System.currentTimeMillis()), Short
						.valueOf("2"), 300.0, Short.valueOf("1"), 1.2, 200.0,
				200.0, Short.valueOf("2"), Short.valueOf("1"), meetingIntCalc,
				meetingIntPost);
		savings = TestObjectFactory.createSavingsAccount("432434", center,
				AccountState.SAVINGS_ACC_CLOSED.getValue(), startDate,
				savingsOffering);
		List<SavingsBO> savingsAccounts = service.getAllClosedAccounts(center
				.getCustomerId());
		assertEquals(1, savingsAccounts.size());
	}

	public void testGetAllClosedAccountsForInvalidConnection() throws Exception {
		createInitialObjects();
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());

		Date startDate = new Date(System.currentTimeMillis());
		savingsOffering = TestObjectFactory.createSavingsOffering("SavingPrd1",
				Short.valueOf("2"), new Date(System.currentTimeMillis()), Short
						.valueOf("2"), 300.0, Short.valueOf("1"), 1.2, 200.0,
				200.0, Short.valueOf("2"), Short.valueOf("1"), meetingIntCalc,
				meetingIntPost);
		savings = TestObjectFactory.createSavingsAccount("432434", center,
				AccountState.SAVINGS_ACC_CLOSED.getValue(), startDate,
				savingsOffering);
		TestObjectFactory.simulateInvalidConnection();
		try {
			service.getAllClosedAccounts(center.getCustomerId());
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}
	}

	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
	}

	public static SavingsOfferingBO createSavingsOffering(String offeringName,
			String shortName) {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());
		return TestObjectFactory.createSavingsOffering(offeringName, shortName,
				Short.valueOf("2"), new Date(System.currentTimeMillis()), Short
						.valueOf("2"), 300.0, Short.valueOf("1"), 1.2, 200.0,
				200.0, Short.valueOf("2"), Short.valueOf("1"), meetingIntCalc,
				meetingIntPost);
	}

	/**
	 * Deprecated in favor of 
	 * {@link #createSavingsAccount(String, SavingsOfferingBO, AccountState)}
	 */
	private SavingsBO createSavingsAccount(String globalAccountNum,
			SavingsOfferingBO savingsOffering, short accountStateId)
			throws Exception {
		AccountState state = AccountState.fromShort(accountStateId);
		return createSavingsAccount(globalAccountNum, savingsOffering, state);
	}

	private SavingsBO createSavingsAccount(String globalAccountNum, SavingsOfferingBO savingsOffering, AccountState state) throws Exception {
		UserContext userContext = new UserContext();
		userContext.setId(new Short("1"));
		userContext.setBranchGlobalNum("1001");
		return TestObjectFactory.createSavingsAccount(globalAccountNum, group,
				state, new Date(), savingsOffering, userContext);
	}
}
