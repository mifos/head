package org.mifos.application.accounts.loan.business.service;

import static java.util.Arrays.asList;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanActivityView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.configuration.business.service.ConfigurationBusinessService;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.business.service.GroupBusinessService;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestLoanBusinessService extends MifosTestCase {

	protected AccountBO accountBO = null;

	protected CustomerBO center = null;

	protected CustomerBO group = null;

	protected AccountPersistence accountPersistence;

	protected LoanBusinessService loanBusinessService;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		loanBusinessService = new LoanBusinessService();
		accountPersistence = new AccountPersistence();
	}

	@Override
	protected void tearDown() throws Exception {
		try {
		    // NOTE: Incomplete Initialization
			accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
					AccountBO.class, accountBO.getAccountId());
			group = (CustomerBO) HibernateUtil.getSessionTL().get(CustomerBO.class,
					group.getCustomerId());
			center = (CustomerBO) HibernateUtil.getSessionTL().get(
					CustomerBO.class, center.getCustomerId());
			TestObjectFactory.cleanUp(accountBO);
			TestObjectFactory.cleanUp(group);
			TestObjectFactory.cleanUp(center);
		} catch (Exception e) {
			// TODO Whoops, cleanup didnt work, reset db
			TestDatabase.resetMySQLDatabase();
		}

		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testFindBySystemId() throws Exception {
		accountBO = getLoanAccount();
		loanBusinessService = new LoanBusinessService();
		LoanBO loanBO = loanBusinessService.findBySystemId(accountBO
				.getGlobalAccountNum());
		assertEquals(loanBO.getGlobalAccountNum(), accountBO
				.getGlobalAccountNum());
		assertEquals(loanBO.getAccountId(), accountBO.getAccountId());
	}
	public void testFindIndividualLoans() throws Exception {
		accountBO = getLoanAccount();
		loanBusinessService = new LoanBusinessService();
		List<LoanBO> listLoanBO = loanBusinessService.findIndividualLoans(accountBO
				.getAccountId().toString());
		assertEquals(0,listLoanBO.size());
	}	
	public void testGetLoanAccountsActiveInGoodBadStanding() throws Exception {
		accountBO = getLoanAccount();
		loanBusinessService = new LoanBusinessService();
		List<LoanBO> loanBO = loanBusinessService
				.getLoanAccountsActiveInGoodBadStanding(accountBO.getCustomer()
						.getCustomerId());
		assertEquals(Short.valueOf("1"), loanBO.get(0).getAccountType()
				.getAccountTypeId());
		assertNotNull(loanBO.size());

	}

	public void testGetRecentActivityView() throws SystemException,
			NumberFormatException, ApplicationException {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center_Active", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		accountBO = TestObjectFactory.createLoanAccount("42423142341", group,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, 
				startDate,
				loanOffering);
		HibernateUtil.closeSession();

		applyPayments();

		accountBO = accountPersistence.getAccount(accountBO.getAccountId());
		List<LoanActivityView> loanRecentActivityView = loanBusinessService
				.getRecentActivityView(accountBO.getGlobalAccountNum(), Short
						.valueOf("1"));

		assertEquals(3, loanRecentActivityView.size());
		assertNotNull(loanRecentActivityView);
	}

	private void applyPayments() throws PersistenceException, AccountException {
		Set<AccountActionDateEntity> actionDates = 
			accountBO.getAccountActionDates();
		// Is this always true or does it depend on System.currentTimeMillis?
//		assertEquals(6, actionDates.size());
		for (AccountActionDateEntity actionDate : actionDates) {
			assertNotNull(actionDate);
			accountBO = accountPersistence.getAccount(accountBO.getAccountId());
			PaymentData paymentData = createPaymentViewObject(accountBO);
			accountBO.applyPaymentWithPersist(paymentData);
			TestObjectFactory.updateObject(accountBO);
		}
	}

	public void testGetAllActivityView() throws SystemException,
			NumberFormatException, ApplicationException {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center_Active", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		accountBO = TestObjectFactory.createLoanAccount("42423142341", group,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate,
				loanOffering);
		HibernateUtil.closeSession();

		applyPayments();

		accountBO = accountPersistence.getAccount(accountBO.getAccountId());
		List<LoanActivityView> loanAllActivityView = loanBusinessService
				.getAllActivityView(accountBO.getGlobalAccountNum(), Short
						.valueOf("1"));

		assertNotNull(loanAllActivityView);
		assertEquals(6, loanAllActivityView.size());
		for(LoanActivityView view : loanAllActivityView) {
			assertNotNull(view.getActivity());
			assertNotNull(view.getUserPrefferedDate());
			assertNotNull(view.getActionDate().getTime());
			assertEquals(new Money("100.0"),view.getFees());
			assertNotNull(view.getId());
			assertEquals(new Money("12.0"),view.getInterest());
			assertNull(view.getLocale());
			assertEquals(new Money("0.0"),view.getPenalty());
			assertEquals(new Money("100.0"),view.getPrincipal());
			assertEquals(new Money("212.0"),view.getTotal());
			assertNotNull(view.getTimeStamp());
			assertEquals(new Money("-100.0"),view.getRunningBalanceFees());
			assertEquals(new Money("24.0"),view.getRunningBalanceInterest());
			assertEquals(new Money("0.0"),view.getRunningBalancePenalty());
			assertEquals(new Money("200.0"),view.getRunningBalancePrinciple());
			break;
		}
	}

	public void testGetAllLoanAccounts() throws Exception {
		accountBO = getLoanAccount();
		loanBusinessService = new LoanBusinessService();
		List<LoanBO> loanAccounts = loanBusinessService.getAllLoanAccounts();
		assertNotNull(loanAccounts);
		assertEquals(1,loanAccounts.size());
	}

	private AccountBO getLoanAccount() {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, 
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate,
				loanOffering);
	}

	private PaymentData createPaymentViewObject(AccountBO accountBO) {
		PaymentData paymentData = PaymentData.createPaymentData(new Money(TestObjectFactory
				.getMFICurrency(), "212.0"), accountBO.getPersonnel(), Short
				.valueOf("1"), new Date(System.currentTimeMillis()));
		paymentData.setRecieptDate(new Date(System.currentTimeMillis()));
		paymentData.setRecieptNum("423423");
		return paymentData;
	}
	
	public void testgetActiveLoansForAllClientsUnderGroup() throws Exception {
		int groupLoanAccountId = 1;
		GroupBO groupMock = createMock(GroupBO.class);
		CustomerBO clientMock = createMock(ClientBO.class);
		LoanBO groupLoanMock = createMock(LoanBO.class);
		ConfigurationBusinessService configServiceMock = createMock(ConfigurationBusinessService.class);
		LoanBO loanMock1 = createMock(LoanBO.class);
		LoanBO loanMock2 = createMock(LoanBO.class);
		AccountBusinessService accountBusinessServiceMock = createMock(AccountBusinessService.class);

		expect(accountBusinessServiceMock.getCoSigningClientsForGlim(groupLoanAccountId))
				.andReturn(Arrays.asList(clientMock));
		expect(configServiceMock.isGlimEnabled()).andReturn(true);
		expect(loanMock1.isActiveLoanAccount()).andReturn(true);
		expect(loanMock2.isActiveLoanAccount()).andReturn(false);
		expect(groupLoanMock.getAccountId()).andReturn(groupLoanAccountId);
		
		expect(clientMock.getAccounts()).andReturn(
				new HashSet(asList(loanMock1, loanMock2)));

		replay(groupMock, clientMock, loanMock1, loanMock2, groupLoanMock,
				configServiceMock, accountBusinessServiceMock);

		assertEquals(asList(loanMock1), new LoanBusinessService(
				new LoanPersistence(), configServiceMock,
				accountBusinessServiceMock)
				.getActiveLoansForAllClientsAssociatedWithGroupLoan(groupLoanMock));

		verify(groupMock, clientMock, loanMock1, loanMock2, groupLoanMock,
				configServiceMock, accountBusinessServiceMock);
		
	}
	
}
