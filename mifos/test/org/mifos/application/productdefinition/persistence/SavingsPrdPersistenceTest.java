package org.mifos.application.productdefinition.persistence;

import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class SavingsPrdPersistenceTest extends MifosTestCase {

	private CustomerBO group;

	private CustomerBO center;

	private SavingsBO savings;

	private SavingsOfferingBO savingsOffering;

	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(savings);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testRetrieveSavingsAccountsForPrd() throws Exception {
		SavingsTestHelper helper = new SavingsTestHelper();
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering("fsaf6", "ads6");
		UserContext userContext = new UserContext();
		userContext.setId(Short.valueOf("1"));
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		HibernateUtil.closeSession();
		List<SavingsBO> savingsList = new SavingsPrdPersistence()
				.retrieveSavingsAccountsForPrd(savingsOffering
						.getPrdOfferingId());
		assertEquals(Integer.valueOf("1").intValue(), savingsList.size());
		savings = new SavingsPersistence().findById(savings.getAccountId());
	}

	public void testGetTimePerForIntCalcAndFreqPost()
			throws PersistenceException, ProductDefinitionException {
		savingsOffering = createSavingsOfferingBO();
		savingsOffering = new SavingsPrdPersistence()
				.getSavingsProduct(savingsOffering.getPrdOfferingId());
		assertNotNull("The time period for Int calc should not be null",
				savingsOffering.getTimePerForInstcalc());
		assertNotNull("The freq for Int post should not be null",
				savingsOffering.getFreqOfPostIntcalc());
		TestObjectFactory.removeObject(savingsOffering);
	}

	public void testDormancyDays() throws Exception {
		assertEquals(Short.valueOf("30"), new SavingsPrdPersistence()
				.retrieveDormancyDays());
	}

	public void testGetSavingsApplicableRecurrenceTypes() throws Exception {
		assertEquals(2, new SavingsPrdPersistence()
				.getSavingsApplicableRecurrenceTypes().size());
	}
	
	public void testGetAllSavingsProducts() throws Exception {
		savingsOffering = createSavingsOfferingBO();
		assertEquals(1, new SavingsPrdPersistence()
				.getAllSavingsProducts().size());
		TestObjectFactory.removeObject(savingsOffering);
	}

	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingForToday(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
	}
	
	

	private SavingsOfferingBO createSavingsOfferingBO() {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingForToday(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingForToday(1, 1, 4, 2));
		return TestObjectFactory.createSavingsOffering("Savings Product",
				"SAVP", (short) 1, new Date(System.currentTimeMillis()),
				(short) 2, 300.0, (short) 1, 1.2, 200.0, 200.0, (short) 2,
				(short) 1, meetingIntCalc, meetingIntPost);
	}

}
