package org.mifos.framework.components.cronjob.helpers;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.mifos.framework.MifosTestCase;

import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.cronjobs.helpers.SavingsIntPostingHelper;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
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
	private MifosCurrency currency = Configuration.getInstance().getSystemConfig().getCurrency();
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
		super.tearDown();
		TestObjectFactory.cleanUp(savings1);
		TestObjectFactory.cleanUp(savings2);
		TestObjectFactory.cleanUp(savings3);
		TestObjectFactory.cleanUp(savings4);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.cleanUp(group);
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	public void testInterestPosting()throws Exception{
		createInitialObjects();
		savings1.setNextIntPostDate(helper.getDate("31/03/2006"));
		savings1.setActivationDate(helper.getDate("05/03/2006"));
		savings1.setInterestToBePosted(new Money(currency, "500"));
		savings1.setSavingsBalance(new Money(currency, "250"));
		savings1.update();
		
		savings4.setNextIntPostDate(helper.getDate("31/03/2006"));
		savings4.setActivationDate(helper.getDate("15/03/2006"));
		savings4.setInterestToBePosted(new Money(currency, "800.40"));
		savings4.setSavingsBalance(new Money(currency, "250"));
		savings4.update();
		
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		Calendar cal = Calendar.getInstance(Configuration.getInstance().getSystemConfig().getMifosTimeZone());
		cal.setTime(helper.getDate("01/05/2006"));
		new SavingsIntPostingHelper().execute(cal.getTimeInMillis());
		
		savings1 = persistence.findById(savings1.getAccountId());
		savings2 = persistence.findById(savings2.getAccountId());
		savings3 = persistence.findById(savings3.getAccountId());
		savings4 = persistence.findById(savings4.getAccountId());
		
		assertEquals(0.0, savings1.getInterestToBePosted().getAmountDoubleValue());
		assertEquals(750.0, savings1.getSavingsBalance().getAmountDoubleValue());
		assertEquals(1,savings1.getAccountPayments().size());
		AccountPaymentEntity payment1 = savings1.getAccountPayments().iterator().next();
		assertEquals(500.0,payment1.getAmount().getAmountDoubleValue());
		assertEquals(helper.getDate("31/05/2006"),savings1.getNextIntPostDate());
		
		assertEquals(1050.4, savings4.getSavingsBalance().getAmountDoubleValue());
		assertEquals(0.0, savings4.getInterestToBePosted().getAmountDoubleValue());
		assertEquals(1,savings1.getAccountPayments().size());
		
		AccountPaymentEntity payment4 = savings4.getAccountPayments().iterator().next();
		assertEquals(800.4,payment4.getAmount().getAmountDoubleValue());
		assertEquals(helper.getDate("31/05/2006"),savings4.getNextIntPostDate());
		
		assertEquals(0,savings2.getAccountPayments().size());
		assertEquals(0,savings3.getAccountPayments().size());
	}
	
	private void createInitialObjects() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group_Active_test", Short
				.valueOf("9"), "1.1.1", center, new Date(System
				.currentTimeMillis()));

		savingsOffering1 = createSavingsOffering("prd1",Short.valueOf("1")); 
		savingsOffering2 = createSavingsOffering("prd2",Short.valueOf("1"));
		savingsOffering3 = createSavingsOffering("prd3",Short.valueOf("1"));
		savingsOffering4 = createSavingsOffering("prd4",Short.valueOf("2"));
		savings1 = helper.createSavingsAccount(savingsOffering1, group, AccountStates.SAVINGS_ACC_APPROVED, userContext);
		savings2 = helper.createSavingsAccount(savingsOffering2, group, AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, userContext);
		savings3 = helper.createSavingsAccount(savingsOffering3, group, AccountStates.SAVINGS_ACC_PENDINGAPPROVAL, userContext);
		savings4 = helper.createSavingsAccount(savingsOffering4, group, AccountStates.SAVINGS_ACC_APPROVED, userContext);
	}
	
	private SavingsOfferingBO createSavingsOffering(String offeringName, Short interestCalcType){
		MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getMeeting("2","2",Short.valueOf("2")));
		MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getMeeting("2","2",Short.valueOf("3")));
		return TestObjectFactory.createSavingsOffering(offeringName,Short.valueOf("2"),new Date(System.currentTimeMillis()),
				Short.valueOf("2"),300.0,Short.valueOf("1"),12.0,200.0,200.0,Short.valueOf("2"),interestCalcType,meetingIntCalc,meetingIntPost);
	}
}
