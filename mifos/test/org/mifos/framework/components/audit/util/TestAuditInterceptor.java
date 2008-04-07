package org.mifos.framework.components.audit.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.components.audit.util.helpers.AuditLogView;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestAuditInterceptor extends MifosTestCase {

	protected AccountBO accountBO = null;

	protected CustomerBO center = null;

	protected CustomerBO group = null;

	private CustomerBO client = null;

	@Override
	protected void tearDown() throws Exception {
		if (accountBO != null)
			accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
					AccountBO.class, accountBO.getAccountId());
		if (group != null)
			group = (CustomerBO) HibernateUtil.getSessionTL().get(
					CustomerBO.class, group.getCustomerId());
		if (center != null)
			center = (CustomerBO) HibernateUtil.getSessionTL().get(
					CustomerBO.class, center.getCustomerId());
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testUpdateLoanForLogging() throws Exception {
		Date newDate = incrementCurrentDate(14);
		accountBO = getLoanAccount();
		accountBO.setUserContext(TestUtils.makeUser());
		HibernateUtil.getInterceptor().createInitialValueMap(accountBO);
		LoanBO loanBO = ((LoanBO) accountBO);
		((LoanBO) accountBO).updateLoan(false, loanBO.getLoanAmount(), loanBO
				.getInterestRate(), loanBO.getNoOfInstallments(), newDate,
				(short)2, TestObjectFactory.SAMPLE_BUSINESS_ACTIVITY_2, 
				"Added note", null,
				null);

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		group = TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());

		List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(
				EntityType.LOAN, accountBO.getAccountId());
		assertEquals(1, auditLogList.size());
		assertEquals(EntityType.LOAN.getValue(), auditLogList.get(0)
				.getEntityType());
		assertEquals(4, auditLogList.get(0).getAuditLogRecords().size());
		for (AuditLogRecord auditLogRecord : auditLogList.get(0)
				.getAuditLogRecords()) {
			if (auditLogRecord.getFieldName().equalsIgnoreCase(
					"Collateral Notes")) {
				assertEquals("-", auditLogRecord.getOldValue());
				assertEquals("Added note", auditLogRecord.getNewValue());
			} else if (auditLogRecord.getFieldName().equalsIgnoreCase(
					"Service Charge deducted At Disbursement")) {
				assertEquals("1", auditLogRecord.getOldValue());
				assertEquals("0", auditLogRecord.getNewValue());
			}
		}
		TestObjectFactory.cleanUpChangeLog();
	}

	public void testAuditLogView() {
		AuditLogView auditLogView = new AuditLogView();
		long currentTime = System.currentTimeMillis();
		Date date = new Date(currentTime);
		auditLogView.setDate(date.toString());
		auditLogView.setField("field");
		//auditLogView.setMfiLocale(new Locale("1"));
		auditLogView.setNewValue("new value");
		auditLogView.setOldValue("old value");
		auditLogView.setUser("user");
		assertEquals("value of date", 
				new Date(currentTime).toString(), 
				auditLogView.getDate());
		assertEquals("value of field", "field", auditLogView.getField());
		//assertEquals("value of Locale", new Locale("1"), auditLogView
		//		.getMfiLocale());
		assertEquals("value of new value", "new value", auditLogView
				.getNewValue());
		assertEquals("value of old value", "old value", auditLogView
				.getOldValue());
		assertEquals("value of user", "user", auditLogView.getUser());
	}

	private Date incrementCurrentDate(int noOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day + noOfDays);
		return DateUtils.getDateWithoutTimeStamp(currentDateCalendar
				.getTimeInMillis());
	}

	private AccountBO getLoanAccount() {
		Date startDate = new Date(System.currentTimeMillis());
		createInitialCustomers();
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, center.getCustomerMeeting().getMeeting());
		return TestObjectFactory.createLoanAccount("42423142341", group, 
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, 
				startDate,
				loanOffering);
	}

	private void createInitialCustomers() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
	}
}
