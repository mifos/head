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
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
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

	@Override
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
		userContext.setId(PersonnelConstants.SYSTEM_USER);
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

	
	public void testGetSavingsOfferingsNotMixed() throws Exception {
		savingsOffering = createSavingsOfferingBO();
		assertEquals(1, new SavingsPrdPersistence()
				.getSavingsOfferingsNotMixed(Short.valueOf("1")).size());
		TestObjectFactory.removeObject(savingsOffering);
	}

	public void testGetAllActiveSavingsProducts() throws Exception {
		savingsOffering = createSavingsOfferingBO();
		assertEquals(1, new SavingsPrdPersistence()
				.getAllActiveSavingsProducts().size());
		TestObjectFactory.removeObject(savingsOffering);
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
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
	}
	
	

	private SavingsOfferingBO createSavingsOfferingBO() {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());
		return TestObjectFactory.createSavingsProduct(
				"Savings Product", "SAVP", ApplicableTo.CLIENTS, 
				new Date(System.currentTimeMillis()), 
				PrdStatus.SAVINGS_ACTIVE, 300.0, 
				RecommendedAmountUnit.PER_INDIVIDUAL, 1.2, 
				200.0, 200.0, 
				SavingsType.VOLUNTARY, InterestCalcType.MINIMUM_BALANCE, 
				meetingIntCalc, meetingIntPost);
	}

}
