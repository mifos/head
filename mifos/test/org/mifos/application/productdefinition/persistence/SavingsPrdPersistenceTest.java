package org.mifos.application.productdefinition.persistence;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.persistence.service.SavingsPersistenceService;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.InterestCalcTypeEntity;
import org.mifos.application.productdefinition.business.PrdApplicableMasterEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.business.SavingsTypeEntity;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;
import org.mifos.application.productdefinition.util.helpers.PrdApplicableMaster;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidUserException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
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

	public void testGetSavingsAccount() throws Exception {
		SavingsTestHelper helper = new SavingsTestHelper();
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
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
		savings = new SavingsPersistenceService().findById(savings
				.getAccountId());
	}

	public void testDormancyDays() throws Exception {
		assertEquals(Short.valueOf("30"), new SavingsPrdPersistence()
				.retrieveDormancyDays());
	}

	public void testBuildSavingsOfferingWithoutData() {
		try {
			SavingsOfferingBO savingsOffering = new SavingsOfferingBO(null,
					null, null, null, null, null, null, null, null, null, null,
					null, null, null);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildSavingsOfferingWithoutName()
			throws InvalidUserException, SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		try {
			SavingsOfferingBO savingsOffering = new SavingsOfferingBO(
					TestObjectFactory.getUserContext(), null, "S",
					productCategory, prdApplicableMaster, new Date(System
							.currentTimeMillis()), savingsType, intCalType,
					intCalcMeeting, intPostMeeting, new Money("10"), 10.0,
					depglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildSavingsOfferingWithShortNameGreaterThanFourDig()
			throws InvalidUserException, SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		try {
			SavingsOfferingBO savingsOffering = new SavingsOfferingBO(
					TestObjectFactory.getUserContext(), "Savings Offering",
					"Savings", productCategory, prdApplicableMaster, new Date(
							System.currentTimeMillis()), savingsType,
					intCalType, intCalcMeeting, intPostMeeting,
					new Money("10"), 10.0, depglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildSavingsOfferingWithStartDateLessThanCurrentDate()
			throws InvalidUserException, SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(-2);
		try {
			SavingsOfferingBO savingsOffering = new SavingsOfferingBO(
					TestObjectFactory.getUserContext(), "Savings Offering",
					"Savi", productCategory, prdApplicableMaster, startDate,
					savingsType, intCalType, intCalcMeeting, intPostMeeting,
					new Money("10"), 10.0, depglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildSavingsOfferingWithStartDateEqualToCurrentDate()
			throws InvalidUserException, SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(0);
		SavingsOfferingBO savingsOffering = new SavingsOfferingBO(
				TestObjectFactory.getUserContext(), "Savings Offering", "Savi",
				productCategory, prdApplicableMaster, startDate, savingsType,
				intCalType, intCalcMeeting, intPostMeeting, new Money("10"),
				10.0, depglCodeEntity, intglCodeEntity);
		assertNotNull(savingsOffering.getGlobalPrdOfferingNum());
		assertEquals(PrdStatus.SAVINGSACTIVE.getValue(), savingsOffering
				.getPrdStatus().getOfferingStatusId());

	}

	public void testBuildSavingsOfferingWithStartDateGreaterThanCurrentDate()
			throws InvalidUserException, SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(2);
		SavingsOfferingBO savingsOffering = new SavingsOfferingBO(
				TestObjectFactory.getUserContext(), "Savings Offering", "Savi",
				productCategory, prdApplicableMaster, startDate, savingsType,
				intCalType, intCalcMeeting, intPostMeeting, new Money("10"),
				10.0, depglCodeEntity, intglCodeEntity);
		assertNotNull(savingsOffering.getGlobalPrdOfferingNum());
		assertEquals(PrdStatus.SAVINGSINACTIVE.getValue(), savingsOffering
				.getPrdStatus().getOfferingStatusId());

	}

	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group_Active_test", Short
				.valueOf("9"), "1.1.1", center, new Date(System
				.currentTimeMillis()));
	}

	private MeetingBO getMeeting() {
		return TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
	}

	private java.sql.Date offSetCurrentDate(int noOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day + noOfDays);
		return new java.sql.Date(currentDateCalendar.getTimeInMillis());
	}

}
