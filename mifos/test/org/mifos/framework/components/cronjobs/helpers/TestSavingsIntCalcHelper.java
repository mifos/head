package org.mifos.framework.components.cronjobs.helpers;

import static org.mifos.application.meeting.util.helpers.MeetingType.SAVINGS_INTEREST_CALCULATION_TIME_PERIOD;
import static org.mifos.application.meeting.util.helpers.MeetingType.SAVINGS_INTEREST_POSTING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.MONTHLY;
import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_MONTH;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.TestAccountPaymentEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.TestSavingsBO;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSavingsIntCalcHelper extends MifosTestCase {
	private UserContext userContext;

	private CustomerBO group;

	private CustomerBO center;

	private SavingsBO savings1;

	private SavingsBO savings2;

	private SavingsBO savings3;

	private SavingsBO savings4;

	private SavingsOfferingBO savingsOffering1;

	private SavingsOfferingBO savingsOffering2;

	private SavingsOfferingBO savingsOffering3;

	private SavingsOfferingBO savingsOffering4;

	private SavingsTestHelper helper = new SavingsTestHelper();

	private MifosCurrency currency = Configuration.getInstance()
			.getSystemConfig().getCurrency();

	SavingsPersistence persistence = new SavingsPersistence();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userContext = new UserContext();
		userContext.setId(new Short("1"));
		userContext.setLocaleId(new Short("1"));
		Set<Short> set = new HashSet<Short>();
		set.add(Short.valueOf("1"));
		userContext.setRoles(set);
		userContext.setLevelId(Short.valueOf("2"));
		userContext.setName("mifos");
		userContext.setPereferedLocale(new Locale("en", "US"));
		userContext.setBranchId(new Short("1"));
		userContext.setBranchGlobalNum("0001");
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(savings1);
		TestObjectFactory.cleanUp(savings2);
		TestObjectFactory.cleanUp(savings3);
		TestObjectFactory.cleanUp(savings4);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testIntCalculation() throws Exception {
		createInitialObjects();
		PersonnelBO createdBy = new PersonnelPersistence()
				.getPersonnel(userContext.getId());
		TestSavingsBO.setNextIntCalcDate(savings1,helper.getDate("01/05/2006"));
		TestSavingsBO.setActivationDate(savings1,helper.getDate("05/03/2006"));

		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(
				savings1, new Money(currency, "1000.0"), new Money(currency,
						"1000.0"), helper.getDate("10/03/2006"),
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings1, createdBy,
				group);
		TestAccountPaymentEntity.addAccountPayment(payment,savings1);
		
		savings1.update();
		HibernateUtil.getSessionTL().flush();

		payment = helper.createAccountPaymentToPersist(savings1, new Money(
				currency, "500.0"), new Money(currency, "1500.0"), helper
				.getDate("20/03/2006"),
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings1, createdBy,
				group);
		TestAccountPaymentEntity.addAccountPayment(payment,savings1);
		savings1.update();
		HibernateUtil.getSessionTL().flush();

		payment = helper.createAccountPaymentToPersist(savings1, new Money(
				currency, "600.0"), new Money(currency, "900.0"), helper
				.getDate("10/04/2006"),
				AccountConstants.ACTION_SAVINGS_WITHDRAWAL, savings1,
				createdBy, group);
		TestAccountPaymentEntity.addAccountPayment(payment,savings1);
		savings1.update();
		HibernateUtil.getSessionTL().flush();

		payment = helper.createAccountPaymentToPersist(savings1, new Money(
				currency, "800.0"), new Money(currency, "1700.0"), helper
				.getDate("20/04/2006"),
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings1, createdBy,
				group);
		TestAccountPaymentEntity.addAccountPayment(payment,savings1);
		savings1.update();
		HibernateUtil.getSessionTL().flush();
		
		TestSavingsBO.setNextIntCalcDate(savings4,helper.getDate("01/05/2006"));
		TestSavingsBO.setActivationDate(savings4,helper.getDate("10/04/2006"));

		payment = helper.createAccountPaymentToPersist(savings4, new Money(
				currency, "1000.0"), new Money(currency, "1000.0"), helper
				.getDate("20/04/2006"),
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings4, createdBy,
				group);
		TestAccountPaymentEntity.addAccountPayment(payment,savings4);
		savings4.update();
		HibernateUtil.getSessionTL().flush();

		payment = helper.createAccountPaymentToPersist(savings4, new Money(
				currency, "500.0"), new Money(currency, "1500.0"), helper
				.getDate("25/04/2006"),
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings4, createdBy,
				group);
		TestAccountPaymentEntity.addAccountPayment(payment,savings4);
		savings4.update();
		HibernateUtil.getSessionTL().flush();

		payment = helper.createAccountPaymentToPersist(savings4, new Money(
				currency, "600.0"), new Money(currency, "900.0"), helper
				.getDate("28/04/2006"),
				AccountConstants.ACTION_SAVINGS_WITHDRAWAL, savings4,
				createdBy, group);
		TestAccountPaymentEntity.addAccountPayment(payment,savings4);
		savings4.update();
		HibernateUtil.getSessionTL().flush();

		payment = helper.createAccountPaymentToPersist(savings4, new Money(
				currency, "800.0"), new Money(currency, "1700.0"), helper
				.getDate("30/04/2006"),
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings4, createdBy,
				group);
		TestAccountPaymentEntity.addAccountPayment(payment,savings4);
		savings4.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		Calendar cal = Calendar.getInstance(Configuration.getInstance()
				.getSystemConfig().getMifosTimeZone());
		cal.setTime(helper.getDate("01/05/2006"));
		SavingsIntCalcTask savingsIntCalcTask = new SavingsIntCalcTask();
		((SavingsIntCalcHelper) savingsIntCalcTask.getTaskHelper()).execute(cal
				.getTimeInMillis());

		savings1 = persistence.findById(savings1.getAccountId());
		savings4 = persistence.findById(savings4.getAccountId());

		assertEquals(helper.getDate("01/06/2006"), savings1
				.getNextIntCalcDate());
		assertEquals(15.4, savings1.getInterestToBePosted()
				.getAmountDoubleValue());

		assertEquals(4.3, savings4.getInterestToBePosted()
				.getAmountDoubleValue());
		assertEquals(helper.getDate("01/06/2006"), savings4
				.getNextIntCalcDate());
	}

	private void createInitialObjects() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);

		savingsOffering1 = createSavingsOffering("prd1", "34vf", Short
				.valueOf("1"));
		savingsOffering2 = createSavingsOffering("prd2", "4frg", Short
				.valueOf("1"));
		savingsOffering3 = createSavingsOffering("prd3", "c23e", Short
				.valueOf("1"));
		savingsOffering4 = createSavingsOffering("prd4", "cwer", Short
				.valueOf("2"));
		savings1 = helper.createSavingsAccount(savingsOffering1, group,
				AccountState.SAVINGS_ACC_APPROVED, userContext);
		savings2 = helper.createSavingsAccount(savingsOffering2, group,
				AccountState.SAVINGS_ACC_PARTIALAPPLICATION, userContext);
		savings3 = helper.createSavingsAccount(savingsOffering3, group,
				AccountState.SAVINGS_ACC_PENDINGAPPROVAL, userContext);
		savings4 = helper.createSavingsAccount(savingsOffering4, group,
				AccountState.SAVINGS_ACC_APPROVED, userContext);
	}

	private SavingsOfferingBO createSavingsOffering(String offeringName,
			String shortName, Short interestCalcType) throws Exception {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getNewMeeting(MONTHLY, EVERY_MONTH, 
						SAVINGS_INTEREST_CALCULATION_TIME_PERIOD, MONDAY));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getNewMeeting(MONTHLY, EVERY_MONTH,
						SAVINGS_INTEREST_POSTING, MONDAY));
		return TestObjectFactory.createSavingsOffering(offeringName, shortName,
				Short.valueOf("2"), new Date(System.currentTimeMillis()), Short
						.valueOf("2"), 300.0, Short.valueOf("1"), 12.0, 200.0,
				200.0, Short.valueOf("2"), interestCalcType, meetingIntCalc,
				meetingIntPost);
	}

}
