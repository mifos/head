package org.mifos.framework.components.audit.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.components.audit.util.helpers.AuditConfigurtion;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestAuditInterceptor extends MifosTestCase {
	
protected AccountBO accountBO = null;

	protected CustomerBO center = null;

	protected CustomerBO group = null;

	private CustomerBO client = null;

	private AccountPersistence accountPersistence = null;

	private UserContext userContext;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userContext = TestObjectFactory.getContext();
		accountPersistence = new AccountPersistence();
	}

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

	public void testUpdateLoanForLogging() throws Exception{
		Date startDate = new Date(System.currentTimeMillis());
		Date newDate = incrementCurrentDate(14);
		accountBO = getLoanAccount();
		accountBO.setUserContext(TestObjectFactory.getUserContext());
		HibernateUtil.getInterceptor().createInitialValueMap(accountBO);

		((LoanBO) accountBO).setDisbursementDate(newDate);
		((LoanBO) accountBO).setCollateralNote("Added note");
		((LoanBO) accountBO).setInterestDeductedAtDisbursement(false);
		((LoanBO) accountBO).setGracePeriodDuration(Short.valueOf("2"));
		((LoanBO) accountBO).setBusinessActivityId(Integer.valueOf("2"));

		((LoanBO) accountBO).updateLoan();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		group = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());

		List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(
				EntityType.LOAN.getValue(), accountBO.getAccountId());
		assertEquals(1, auditLogList.size());
		assertEquals(EntityType.LOAN.getValue(), auditLogList.get(0)
				.getEntityType());
		assertEquals(4, auditLogList.get(0).getAuditLogRecords().size());
		for (AuditLogRecord auditLogRecord : auditLogList.get(0)
				.getAuditLogRecords()) {
			if (auditLogRecord.getFieldName().equalsIgnoreCase(
					"Collateral Notes")) {
				auditLogRecord.getOldValue().equalsIgnoreCase("-");
				auditLogRecord.getNewValue().equalsIgnoreCase("Added note");
			} else if (auditLogRecord.getFieldName().equalsIgnoreCase(
					"Service Charge deducted At Disbursement")) {
				auditLogRecord.getOldValue().equalsIgnoreCase("1");
				auditLogRecord.getNewValue().equalsIgnoreCase("0");
			} else if (auditLogRecord.getFieldName().equalsIgnoreCase(
					" Service Charge deducted At Disbursement")) {
				auditLogRecord.getOldValue().equalsIgnoreCase("0");
				auditLogRecord.getNewValue().equalsIgnoreCase("2");
			}
		}
		TestObjectFactory.cleanUpChangeLog();

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
		createInitialCustomers();
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());
		return TestObjectFactory.createLoanAccount("42423142341", group, Short
				.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
	}
	
	private void createInitialCustomers() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
	}

}
