package org.mifos.framework.components.cronjobs.helpers;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanArrearsAgingEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.TestLoanBO;
import org.mifos.application.accounts.loan.persistance.LoanPersistance;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.cronjobs.helpers.LoanArrearsAgingHelper;
import org.mifos.framework.components.cronjobs.helpers.LoanArrearsAgingTask;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestLoanArrearsAgingHelper extends MifosTestCase {
	private LoanArrearsAgingHelper loanArrearsAgingHelper;

	private CustomerBO center;

	private CustomerBO group;

	private MeetingBO meeting;

	private LoanBO loanAccount1;

	private LoanBO loanAccount2;

	private LoanBO loanAccount3;

	private LoanBO loanAccount4;
	
	private short recurAfter = 1;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		LoanArrearsAgingTask loanArrearsAgingTask = new LoanArrearsAgingTask();
		loanArrearsAgingHelper = (LoanArrearsAgingHelper)loanArrearsAgingTask.getTaskHelper();
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, recurAfter, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(loanAccount1);
		TestObjectFactory.cleanUp(loanAccount2);
		TestObjectFactory.cleanUp(loanAccount3);
		TestObjectFactory.cleanUp(loanAccount4);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		loanArrearsAgingHelper = null;
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	public void testExecute() throws Exception {
		loanAccount1 = getLoanAccount(group, meeting,
				AccountState.LOANACC_ACTIVEINGOODSTANDING, "off1");
		loanAccount2 = getLoanAccount(group, meeting,
				AccountState.LOANACC_BADSTANDING,"off2");
		loanAccount3 = getLoanAccount(group, meeting,
				AccountState.LOANACC_DBTOLOANOFFICER,"off3");
		loanAccount4 = getLoanAccount(group, meeting,
				AccountState.LOANACC_PENDINGAPPROVAL,"off4");

		assertNull(loanAccount1.getLoanArrearsAgingEntity());
		assertNull(loanAccount2.getLoanArrearsAgingEntity());
		assertNull(loanAccount3.getLoanArrearsAgingEntity());
		assertNull(loanAccount4.getLoanArrearsAgingEntity());

		setDisbursementDateAsOldDate(loanAccount1, 15, Short.valueOf("1"));
		loanAccount1.update();
		HibernateUtil.commitTransaction();

		setDisbursementDateAsOldDate(loanAccount2, 22, Short.valueOf("3"));
		loanAccount2.update();
		HibernateUtil.commitTransaction();

		setDisbursementDateAsOldDate(loanAccount3, 15,Short.valueOf("1"));
		loanAccount3.update();
		HibernateUtil.commitTransaction();

		setDisbursementDateAsOldDate(loanAccount4, 22, Short.valueOf("1"));
		loanAccount4.update();
		HibernateUtil.commitTransaction();
		
		loanArrearsAgingHelper.execute(System.currentTimeMillis());
		
		loanAccount1 = new LoanPersistance().getAccount(loanAccount1
				.getAccountId());
		loanAccount2 = new LoanPersistance().getAccount(loanAccount2
				.getAccountId());
		loanAccount3 = new LoanPersistance().getAccount(loanAccount3
				.getAccountId());
		loanAccount4 = new LoanPersistance().getAccount(loanAccount4
				.getAccountId());
		
		assertNotNull(loanAccount1.getLoanArrearsAgingEntity());
		assertNotNull(loanAccount2.getLoanArrearsAgingEntity());
		assertNull(loanAccount3.getLoanArrearsAgingEntity());
		assertNull(loanAccount4.getLoanArrearsAgingEntity());
		LoanArrearsAgingEntity entityAccount1 = loanAccount1
				.getLoanArrearsAgingEntity();
		LoanArrearsAgingEntity entityAccount2 = loanAccount2
				.getLoanArrearsAgingEntity();
		
		assertEquals(new Money("100"), entityAccount1.getOverduePrincipal());
		assertEquals(new Money("12"), entityAccount1.getOverdueInterest());
		assertEquals(new Money("112"), entityAccount1.getOverdueBalance());

		assertEquals(Short.valueOf("15"), entityAccount1.getDaysInArrears());
		
		assertEquals(new Money("300"), entityAccount2.getOverduePrincipal());
		assertEquals(new Money("36"), entityAccount2.getOverdueInterest());
		assertEquals(new Money("336"), entityAccount2.getOverdueBalance());
		
		assertEquals(Short.valueOf("22"), entityAccount2.getDaysInArrears());
		
		assertForLoanArrearsAgingEntity(loanAccount1);
		assertForLoanArrearsAgingEntity(loanAccount2);
		
		group=TestObjectFactory.getObject(CustomerBO.class, group.getCustomerId());
		center = group.getParentCustomer();
	}

	private void assertForLoanArrearsAgingEntity(LoanBO loanAccount){
		LoanArrearsAgingEntity loanArrearsAgingEntity = loanAccount
		.getLoanArrearsAgingEntity();
		
		assertEquals(loanAccount.getLoanSummary().getPrincipalDue(),
				loanArrearsAgingEntity.getUnpaidPrincipal());
		assertEquals(loanAccount.getLoanSummary().getInterestDue(),
				loanArrearsAgingEntity.getUnpaidInterest());
		assertEquals(loanAccount.getLoanSummary().getPrincipalDue().add(
				loanAccount.getLoanSummary().getInterestDue()), loanArrearsAgingEntity
				.getUnpaidBalance());
		
		assertEquals(loanAccount.getTotalPrincipalAmountInArrears(),
				loanArrearsAgingEntity.getOverduePrincipal());
		assertEquals(loanAccount.getTotalInterestAmountInArrears(),
				loanArrearsAgingEntity.getOverdueInterest());
		assertEquals(loanAccount.getTotalPrincipalAmountInArrears().add(
				loanAccount.getTotalInterestAmountInArrears()), loanArrearsAgingEntity
				.getOverdueBalance());

		assertEquals(group.getCustomerId(), loanArrearsAgingEntity.getCustomer().getCustomerId());
		assertEquals(center.getCustomerId(), loanArrearsAgingEntity.getParentCustomer().getCustomerId());
		assertEquals(group.getDisplayName(), loanArrearsAgingEntity.getCustomerName());
	}
	
	private LoanBO getLoanAccount(CustomerBO customer, MeetingBO meeting,
			AccountState accountState, String offName) throws AccountException {
		return TestObjectFactory.createLoanAccount("42423142341",
				customer, accountState.getValue(), new Date(), createLoanOffering(offName));		
	}

	private LoanOfferingBO createLoanOffering(String offName){
		Date startDate = new Date(System.currentTimeMillis());
		return TestObjectFactory.createLoanOffering(
				offName, offName, startDate, meeting);
	}
	
	private void setDisbursementDateAsOldDate(LoanBO account, int days, Short installmentSize) {
		Date startDate = offSetCurrentDate(days);
		LoanBO loan = account;
		TestLoanBO.modifyDisbursmentDate(loan,startDate);
		for (AccountActionDateEntity actionDate : loan.getAccountActionDates()){
			if(actionDate.getInstallmentId().shortValue()<=installmentSize.shortValue())
				TestLoanBO.setActionDate(actionDate,offSetGivenDate(
						actionDate.getActionDate(), days));
		}
	}
	
	private java.sql.Date offSetCurrentDate(int noOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day - noOfDays);
		return new java.sql.Date(currentDateCalendar.getTimeInMillis());
	}

	private java.sql.Date offSetGivenDate(Date date, int numberOfDays) {
		Calendar dateCalendar = new GregorianCalendar();
		dateCalendar.setTimeInMillis(date.getTime());
		int year = dateCalendar.get(Calendar.YEAR);
		int month = dateCalendar.get(Calendar.MONTH);
		int day = dateCalendar.get(Calendar.DAY_OF_MONTH);
		dateCalendar = new GregorianCalendar(year, month, day - numberOfDays);
		return new java.sql.Date(dateCalendar.getTimeInMillis());
	}
}
