package org.mifos.framework.components.batchjobs.helpers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanCalculationTest;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.loan.business.TestLoanBO;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryEntity;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class PortfolioAtRiskCalculationTest extends MifosTestCase{
	
	public PortfolioAtRiskCalculationTest() throws SystemException, ApplicationException {
        super();
    }

    private static final double DELTA = 0.00000001;

    private AccountBO account1 = null;

	private AccountBO account2 = null;

	private CenterBO center;

	private CenterBO center1 = null;

	private GroupBO group;

	private GroupBO group1;

	private ClientBO client;

	private ClientBO client1 = null;

	private ClientBO client2 = null;

	private OfficeBO officeBO;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			TestObjectFactory.cleanUp(account2);
			TestObjectFactory.cleanUp(account1);
			TestObjectFactory.cleanUp(client1);
			TestObjectFactory.cleanUp(client2);
			TestObjectFactory.cleanUp(client);
			TestObjectFactory.cleanUp(group);
			TestObjectFactory.cleanUp(group1);
			TestObjectFactory.cleanUp(center);
			TestObjectFactory.cleanUp(center1);
			TestObjectFactory.cleanUp(officeBO);
		} catch (Exception e) {
			// TODO Whoops, cleanup didnt work, reset db
			TestDatabase.resetMySQLDatabase();
		}
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	private void createInitialObject() {
		Date startDate = new Date(System.currentTimeMillis());

		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loandsdasd", "fsad", startDate, meeting);
		account1 = TestObjectFactory.createLoanAccount("42423142341", group,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate,
				loanOffering);
		loanOffering = TestObjectFactory.createLoanOffering("Loandfas", "dsvd",
				ApplicableTo.CLIENTS, startDate, 
				PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, 
				InterestType.FLAT, meeting);
		account2 = TestObjectFactory.createLoanAccount("42427777341", client,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate,
				loanOffering);
	}

	
	private void createPayment(LoanBO loan, Money amountPaid) throws Exception
	{
		Set<AccountActionDateEntity> actionDateEntities = loan.getAccountActionDates();
        LoanScheduleEntity[] paymentsArray = LoanCalculationTest.getSortedAccountActionDateEntity(actionDateEntities, 
				6);
        PersonnelBO personnelBO =  new PersonnelPersistence().getPersonnel(TestObjectFactory.getContext().getId());
        LoanScheduleEntity loanSchedule = paymentsArray[0];
        Short paymentTypeId = PaymentTypes.CASH.getValue();
        PaymentData paymentData = PaymentData.createPaymentData(amountPaid, personnelBO, paymentTypeId, loanSchedule.getActionDate());
		loan.applyPayment(paymentData, true);
		paymentData = PaymentData.createPaymentData(amountPaid, personnelBO, paymentTypeId, loanSchedule.getActionDate());
		loan.applyPayment(paymentData, true);
	}
	
	private void changeFirstInstallmentDate(AccountBO accountBO,
			int numberOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day
				- numberOfDays);
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
			TestLoanBO.setActionDate(accountActionDateEntity,new java.sql.Date(
					currentDateCalendar.getTimeInMillis()));
			break;
		}
	}
	
	public void testGeneratePortfolioAtRiskForTaskNoPayment() throws Exception {
		createInitialObject();
		TestObjectFactory.flushandCloseSession();
		group = TestObjectFactory.getGroup(group
				.getCustomerId());
		client = TestObjectFactory.getClient(client
				.getCustomerId());
		
		for (AccountBO account : group.getAccounts()) {
			if (account.getType() == AccountTypes.LOAN_ACCOUNT) {
				changeFirstInstallmentDate(account, 31);
				((LoanBO)account).handleArrears();
			}
		}
		for (AccountBO account : client.getAccounts()) {
			if (account.getType() == AccountTypes.LOAN_ACCOUNT) {
				changeFirstInstallmentDate(account, 31);
				((LoanBO)account).handleArrears();
			}
		}
		TestObjectFactory.flushandCloseSession();
		group = TestObjectFactory.getGroup(group
				.getCustomerId());
		double portfolioAtRisk = PortfolioAtRiskCalculation.generatePortfolioAtRiskForTask(group.getCustomerId(), group.getOffice().getOfficeId(), 
				     group.getOffice().getSearchId());
		assertEquals(1.0, portfolioAtRisk, DELTA);
				
		center = TestObjectFactory.getCenter(center
				.getCustomerId());
		group = TestObjectFactory.getGroup(group
				.getCustomerId());
		client = TestObjectFactory.getClient(client
				.getCustomerId());
		account1 = TestObjectFactory.getObject(AccountBO.class,
				account1.getAccountId());
		account2 = TestObjectFactory.getObject(AccountBO.class,
				account2.getAccountId());
	}
	
	public void testGeneratePortfolioAtRiskForTaskSomePayments() throws Exception {
		createInitialObject();
		TestObjectFactory.flushandCloseSession();
		group = TestObjectFactory.getGroup(group
				.getCustomerId());
		client = TestObjectFactory.getClient(client
				.getCustomerId());
		
		for (AccountBO account : group.getAccounts()) {
			if (account.getType() == AccountTypes.LOAN_ACCOUNT) {
				LoanBO loan = (LoanBO)account;
				createPayment(loan, new Money("200"));
			}
		}
		for (AccountBO account : client.getAccounts()) {
			if (account.getType() == AccountTypes.LOAN_ACCOUNT) {
				LoanBO loan = (LoanBO)account;
				changeFirstInstallmentDate(account, 31);
				createPayment(loan, new Money("200"));
				loan.handleArrears();
			}
		}
		TestObjectFactory.flushandCloseSession();
		group = TestObjectFactory.getGroup(group
				.getCustomerId());
		double portfolioAtRisk = PortfolioAtRiskCalculation.generatePortfolioAtRiskForTask(group.getCustomerId(), group.getOffice().getOfficeId(), 
			     group.getOffice().getSearchId());
		assertEquals(0.5, portfolioAtRisk, DELTA);
		
		center = TestObjectFactory.getCenter(center
				.getCustomerId());
		group = TestObjectFactory.getGroup(group
				.getCustomerId());
		client = TestObjectFactory.getClient(client
				.getCustomerId());
		account1 = TestObjectFactory.getObject(AccountBO.class,
				account1.getAccountId());
		account2 = TestObjectFactory.getObject(AccountBO.class,
				account2.getAccountId());
	}
	


}
