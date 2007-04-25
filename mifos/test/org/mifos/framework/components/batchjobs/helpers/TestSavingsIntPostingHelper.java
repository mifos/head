package org.mifos.framework.components.batchjobs.helpers;

import static org.mifos.application.meeting.util.helpers.MeetingType.SAVINGS_INTEREST_CALCULATION_TIME_PERIOD;
import static org.mifos.application.meeting.util.helpers.MeetingType.SAVINGS_INTEREST_POSTING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.MONTHLY;
import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_SECOND_MONTH;

import java.util.Calendar;
import java.util.Date;

import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.savings.business.SavingsActivityEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.TestSavingsBO;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.batchjobs.helpers.SavingsIntPostingHelper;
import org.mifos.framework.components.batchjobs.helpers.SavingsIntPostingTask;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSavingsIntPostingHelper extends MifosTestCase {
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
		userContext = TestUtils.makeUser();
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

	public void testInterestPosting() throws Exception {
		createInitialObjects();
		TestSavingsBO.setNextIntPostDate(savings1,helper.getDate("31/03/2006"));
		TestSavingsBO.setActivationDate(savings1,helper.getDate("05/03/2006"));
		TestSavingsBO.setInterestToBePosted(savings1,new Money(currency, "500"));
		TestSavingsBO.setBalance(savings1,new Money("250"));
		
		savings1.update();

		TestSavingsBO.setNextIntPostDate(savings4,helper.getDate("31/03/2006"));
		TestSavingsBO.setActivationDate(savings4,helper.getDate("15/03/2006"));
		TestSavingsBO.setInterestToBePosted(savings4,new Money(currency, "800.40"));
		TestSavingsBO.setBalance(savings4,new Money("250"));
		savings4.update();

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		Calendar cal = Calendar.getInstance(Configuration.getInstance()
				.getSystemConfig().getMifosTimeZone());
		cal.setTime(helper.getDate("01/05/2006"));
		SavingsIntPostingTask savingsIntPostingTask = new SavingsIntPostingTask();
		((SavingsIntPostingHelper) savingsIntPostingTask.getTaskHelper())
				.execute(cal.getTimeInMillis());

		savings1 = persistence.findById(savings1.getAccountId());
		savings2 = persistence.findById(savings2.getAccountId());
		savings3 = persistence.findById(savings3.getAccountId());
		savings4 = persistence.findById(savings4.getAccountId());

		assertEquals(0.0, savings1.getInterestToBePosted()
				.getAmountDoubleValue());
		assertEquals(750.0, savings1.getSavingsBalance().getAmountDoubleValue());
		assertEquals(1, savings1.getAccountPayments().size());
		AccountPaymentEntity payment1 = savings1.getAccountPayments()
				.iterator().next();
		assertEquals(500.0, payment1.getAmount().getAmountDoubleValue());
		assertEquals(helper.getDate("31/05/2006"), savings1
				.getNextIntPostDate());

		assertEquals(1, savings1.getSavingsActivityDetails().size());
		for (SavingsActivityEntity activity : savings1
				.getSavingsActivityDetails())
			assertEquals(DateUtils
					.getDateWithoutTimeStamp(getDate("31/03/2006").getTime()),
					DateUtils.getDateWithoutTimeStamp(activity
							.getTrxnCreatedDate().getTime()));

		assertEquals(1050.4, savings4.getSavingsBalance()
				.getAmountDoubleValue());
		assertEquals(0.0, savings4.getInterestToBePosted()
				.getAmountDoubleValue());
		assertEquals(1, savings4.getAccountPayments().size());

		AccountPaymentEntity payment4 = savings4.getAccountPayments()
				.iterator().next();
		assertEquals(800.4, payment4.getAmount().getAmountDoubleValue());
		assertEquals(helper.getDate("31/05/2006"), savings4
				.getNextIntPostDate());

		assertEquals(1, savings1.getSavingsActivityDetails().size());
		for (SavingsActivityEntity activity : savings1
				.getSavingsActivityDetails())
			assertEquals(DateUtils
					.getDateWithoutTimeStamp(getDate("31/03/2006").getTime()),
					DateUtils.getDateWithoutTimeStamp(activity
							.getTrxnCreatedDate().getTime()));

		assertEquals(0, savings2.getAccountPayments().size());
		assertEquals(0, savings3.getAccountPayments().size());
	}

	private void createInitialObjects() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", 
				CustomerStatus.GROUP_ACTIVE, center);

		savingsOffering1 = createSavingsOffering("prd1", "ssdr", Short
				.valueOf("1"));
		savingsOffering2 = createSavingsOffering("prd2", "aser", Short
				.valueOf("1"));
		savingsOffering3 = createSavingsOffering("prd3", "zx23", Short
				.valueOf("1"));
		savingsOffering4 = createSavingsOffering("prd4", "wsas", Short
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
				.createMeeting(TestObjectFactory.getNewMeeting(MONTHLY, 
						EVERY_SECOND_MONTH, SAVINGS_INTEREST_CALCULATION_TIME_PERIOD,
						MONDAY));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getNewMeeting(MONTHLY, 
						EVERY_SECOND_MONTH, SAVINGS_INTEREST_POSTING, MONDAY));
		return TestObjectFactory.createSavingsOffering(offeringName, shortName, ApplicableTo.GROUPS, new Date(System.currentTimeMillis()), 
				Short
										.valueOf("2"), 300.0, Short.valueOf("1"), 12.0, 
				200.0, 200.0, Short.valueOf("2"), interestCalcType, 
				meetingIntCalc, meetingIntPost);
	}
}
