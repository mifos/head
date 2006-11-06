package org.mifos.framework.components.cronjob.helpers;

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
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.collectionsheet.business.CollectionSheetBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.TestCustomerAccountBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
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
		for (AccountActionDateEntity accountActionDateEntity : center
				.getCustomerAccount().getAccountActionDates()) {
			TestCustomerAccountBO.setActionDate(accountActionDateEntity,offSetDate(
					accountActionDateEntity.getActionDate(), 1));
		}
		
		for(AccountActionDateEntity accountActionDateEntity : loanBO.getAccountActionDates()) {
			TestLoanBO.setActionDate(accountActionDateEntity,offSetDate(
					accountActionDateEntity.getActionDate(), 1));
		}
		
		for(AccountActionDateEntity accountActionDateEntity : savingsBO.getAccountActionDates()) {
			TestSavingsBO.setActionDate(accountActionDateEntity,offSetDate(
					accountActionDateEntity.getActionDate(), 1));
		}
		
		CollectionSheetHelper collectionSheetHelper = new CollectionSheetHelper(new CollectionSheetTask());
		collectionSheetHelper.execute(System.currentTimeMillis());
		
		List<CollectionSheetBO> collectionSheets = getCollectionSheets();
		assertEquals("Size of collectionSheets should be 1",1,collectionSheets.size());
		
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
				.getMeetingHelper(1, 1, 4, Calendar.DAY_OF_WEEK));
		center = TestObjectFactory.createCenter("Center",
				CustomerStatus.CENTER_ACTIVE.getValue(), "1.4", meeting,
				new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", GroupConstants.ACTIVE,
				"1.4.1", center, new Date(System.currentTimeMillis()));
	}

	private LoanBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", customer,
				Short.valueOf("5"), startDate, loanOffering);

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
		return (List<CollectionSheetBO>) query.list();
	}
}
