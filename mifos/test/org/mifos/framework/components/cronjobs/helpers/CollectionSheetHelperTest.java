package org.mifos.framework.components.cronjobs.helpers;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Query;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.TestLoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.TestSavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.collectionsheet.business.CollectionSheetBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.TestCustomerAccountBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.GraceType;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdApplicableMaster;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.cronjobs.helpers.CollectionSheetHelper;
import org.mifos.framework.components.cronjobs.helpers.CollectionSheetTask;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CollectionSheetHelperTest extends MifosTestCase {

	private CenterBO center;

	private GroupBO group;

	private MeetingBO meeting;

	private SavingsTestHelper helper = new SavingsTestHelper();

	private SavingsOfferingBO savingsOffering;
	
	private LoanBO loanBO;
	
	private SavingsBO savingsBO;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	public void tearDown()throws Exception  {
		TestObjectFactory.cleanUp(loanBO);
		TestObjectFactory.cleanUp(savingsBO);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	public void testExecute() throws Exception {
		createInitialObjects();
		loanBO = getLoanAccount(group, meeting);
		savingsBO = getSavingsAccount(center,"SAVINGS_OFFERING", "SAV");
		CollectionSheetHelper collectionSheetHelper = new CollectionSheetHelper(new CollectionSheetTask());

		for (AccountActionDateEntity accountActionDateEntity : center
				.getCustomerAccount().getAccountActionDates()) {
			TestCustomerAccountBO.setActionDate(accountActionDateEntity,offSetDate(
					accountActionDateEntity.getActionDate(), collectionSheetHelper.getDaysInAdvance()));
		}
		
		for(AccountActionDateEntity accountActionDateEntity : loanBO.getAccountActionDates()) {
			TestLoanBO.setActionDate(accountActionDateEntity,offSetDate(
					accountActionDateEntity.getActionDate(), collectionSheetHelper.getDaysInAdvance()));
		}
		
		for(AccountActionDateEntity accountActionDateEntity : savingsBO.getAccountActionDates()) {
			TestSavingsBO.setActionDate(accountActionDateEntity,offSetDate(
					accountActionDateEntity.getActionDate(), collectionSheetHelper.getDaysInAdvance()));
		}
		
		long runTime = System.currentTimeMillis();
		collectionSheetHelper.execute(runTime);
		
		List<CollectionSheetBO> collectionSheets = getCollectionSheets();
		assertEquals("Size of collectionSheets should be 1",1,collectionSheets.size());
		
		CollectionSheetBO collectionSheet = collectionSheets.get(0);
		
		// we need to trim off time information so that we can 
		// match the value returned by a java.sql.Date object which
		// also truncates all time information
		Calendar collectionSheetDate = new GregorianCalendar();
		collectionSheetDate.setTimeInMillis(runTime);
		collectionSheetDate.set(Calendar.HOUR_OF_DAY, 0);
		collectionSheetDate.set(Calendar.MINUTE, 0);
		collectionSheetDate.set(Calendar.SECOND, 0);
		collectionSheetDate.set(Calendar.MILLISECOND, 0);
		long normalizedRunTime = collectionSheetDate.getTimeInMillis();
		
		collectionSheetDate.roll(Calendar.DATE, collectionSheetHelper.getDaysInAdvance());
		long normalizedCollectionSheetTime = collectionSheetDate.getTimeInMillis();
		
		assertEquals(collectionSheet.getRunDate().getTime(), normalizedRunTime);  
		assertEquals(collectionSheet.getCollSheetDate().getTime(), normalizedCollectionSheetTime); 
		
		clearCollectionSheets(collectionSheets);
	}
	
	private void clearCollectionSheets(List<CollectionSheetBO> collectionSheets) {
		for(CollectionSheetBO collectionSheetBO : collectionSheets)
			TestObjectFactory.cleanUp(collectionSheetBO);
	}
	
	private SavingsBO getSavingsAccount(CustomerBO customer, String offeringName, String shortName)
			throws Exception {
		savingsOffering = helper.createSavingsOffering(offeringName, shortName);
		return TestObjectFactory.createSavingsAccount("000100000000017",
				customer, AccountStates.SAVINGS_ACC_APPROVED, new Date(System
						.currentTimeMillis()), savingsOffering);
	}

	private void createInitialObjects() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeeting(RecurrenceType.WEEKLY, TestObjectFactory.EVERY_WEEK, 
						MeetingType.CUSTOMER_MEETING, WeekDay.MONDAY));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
	}

	private LoanBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
		final double LOAN_AMOUNT = 300.0;
		final double INTEREST_RATE = 1.2;
		final short  NUMBER_OF_INSTALLMENTS = 3;
		
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan",
				"L",
				PrdApplicableMaster.GROUPS,
				startDate, 
				PrdStatus.LOANACTIVE.getValue(),
				LOAN_AMOUNT, 
				INTEREST_RATE,
				NUMBER_OF_INSTALLMENTS,  
				InterestType.FLAT,
				true, 
				true,
				meeting,
				GraceType.GRACEONALLREPAYMENTS);
		return TestObjectFactory.createLoanAccount("42423142341", customer,
				AccountState.LOANACC_ACTIVEINGOODSTANDING.getValue(), startDate, loanOffering);

	}
	
	private java.sql.Date offSetDate(Date date, int noOfDays) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		calendar = new GregorianCalendar(year, month, day + noOfDays);
		return new java.sql.Date(calendar.getTimeInMillis());
	}
	
	private List<CollectionSheetBO> getCollectionSheets() {
		Query query = HibernateUtil
				.getSessionTL()
				.createQuery(
						"from org.mifos.application.collectionsheet.business.CollectionSheetBO");
		return query.list();
	}
}
