package org.mifos.application.accounts.loan.struts.action;

import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountNotesEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.loan.business.LoanActivityEntity;
import org.mifos.application.accounts.loan.business.LoanActivityView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.LoanPaymentData;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestLoanAccountAction extends MifosMockStrutsTestCase {

	private UserContext userContext;

	protected AccountBO accountBO = null;

	protected CustomerBO center = null;

	protected CustomerBO group = null;
	
	private CustomerBO client = null;

	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
					.getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/framework/util/helpers/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
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
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		
	}

	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetAllActivity() {
		try {
			Date startDate = new Date(System.currentTimeMillis());
			accountBO = getLoanAccount(Short.valueOf("3"), startDate, 1);
			LoanBO loan = (LoanBO) accountBO;
			setRequestPathInfo("/loanAccountAction.do");
			addRequestParameter("method", "getAllActivity");
			addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
			actionPerform();
			verifyForward("getAllActivity_success");
			assertEquals(1,((List<LoanActivityView>)SessionUtils.getAttribute(LoanConstants.LOAN_ALL_ACTIVITY_VIEW,request.getSession())).size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testGetInstallmentDetails() {
			Date startDate = new Date(System.currentTimeMillis());
			accountBO = getLoanAccount(Short.valueOf("3"), startDate, 1);
			LoanBO loan = (LoanBO) accountBO;
			setRequestPathInfo("/loanAccountAction.do");
			addRequestParameter("method", "getInstallmentDetails");
			addRequestParameter("accountId", String
					.valueOf(loan.getAccountId()));
			actionPerform();
			verifyForward("viewInstmentDetails_success");
	}
	
	public void testGet() throws AccountException, SystemException{
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 1);
		LoanBO loan = (LoanBO) accountBO;
		
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("accountId", loan.getAccountId().toString());
		actionPerform();
		verifyForward("get_success");
		
		assertEquals(0,loan.getPerformanceHistory().getNoOfPayments().intValue());
		assertEquals(((LoanBO) accountBO).getTotalAmountDue().getAmountDoubleValue(), 212.0);
		modifyActionDateForFirstInstallment();
		assertEquals("Total no. of notes should be 5",5,accountBO.getAccountNotes().size());
		assertEquals("Total no. of recent notes should be 3",3,((List<AccountNotesEntity>)SessionUtils.getAttribute(LoanConstants.NOTES,request.getSession())).size());
	}
	
	public void testGetWithPayment() throws AccountException, SystemException, NumberFormatException, RepaymentScheduleException, FinancialException{
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 1);
		disburseLoan(startDate);
		LoanBO loan = (LoanBO) accountBO;
		
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("accountId", loan.getAccountId().toString());
		actionPerform();
		verifyForward("get_success");
		
		assertEquals("Total no. of notes should be 5",5,accountBO.getAccountNotes().size());
		assertEquals("Total no. of recent notes should be 3",3,((List<AccountNotesEntity>)SessionUtils.getAttribute(LoanConstants.NOTES,request.getSession())).size());
		
		assertEquals("Last payment action should be 'PAYMENT'",AccountActionTypes.DISBURSAL.getValue(),SessionUtils.getAttribute(AccountConstants.LAST_PAYMENT_ACTION,request.getSession()));
		client = (CustomerBO) HibernateUtil.getSessionTL().get(CustomerBO.class,client.getCustomerId());
		group = (CustomerBO) HibernateUtil.getSessionTL().get(CustomerBO.class,group.getCustomerId());
		center = (CustomerBO) HibernateUtil.getSessionTL().get(CustomerBO.class,center.getCustomerId());
	}
	
	private void modifyActionDateForFirstInstallment() {
		LoanScheduleEntity installment = (LoanScheduleEntity)accountBO.getAccountActionDate((short) 1);
		installment.setPrincipal(new Money("20.0"));
		installment.setPenalty(new Money("5.0"));
		installment.setInterest(new Money("10.0"));	
		installment.setActionDate(offSetCurrentDate(1));
		accountBO = saveAndFetch(accountBO);
	}
	
	private java.sql.Date offSetCurrentDate(int noOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day - noOfDays);
		return new java.sql.Date(currentDateCalendar.getTimeInMillis());
	}
	
	private AccountBO saveAndFetch(AccountBO account) {
		AccountPersistanceService accountPersistanceService = new AccountPersistanceService();
		accountPersistanceService.updateAccount(account);
		return accountPersistanceService.getAccount(account.getAccountId());
	}
	
	private AccountNotesEntity createAccountNotes(String comment){
		AccountNotesEntity accountNotes = new AccountNotesEntity();
		accountNotes.setCommentDate(new java.sql.Date(System
				.currentTimeMillis()));
		accountNotes.setPersonnel(TestObjectFactory.getPersonnel(userContext.getId()));
		accountNotes.setComment(comment);
		return accountNotes;
	}
	
	private void addNotes() {
		accountBO.addAccountNotes(createAccountNotes("Notes1"));
		TestObjectFactory.updateObject(accountBO);
		accountBO.addAccountNotes(createAccountNotes("Notes2"));
		TestObjectFactory.updateObject(accountBO);
		accountBO.addAccountNotes(createAccountNotes("Notes3"));
		TestObjectFactory.updateObject(accountBO);
		accountBO.addAccountNotes(createAccountNotes("Notes4"));
		TestObjectFactory.updateObject(accountBO);
		accountBO.addAccountNotes(createAccountNotes("Notes5"));
		TestObjectFactory.updateObject(accountBO);
	}
	
	private void applyPaymentandRetrieveAccount() throws AccountException,	SystemException {
		Date startDate = new Date(System.currentTimeMillis());
		PaymentData paymentData = new PaymentData(new Money(Configuration
				.getInstance().getSystemConfig().getCurrency(), "100.0"),
				accountBO.getPersonnel(), Short.valueOf("1"), startDate);
		paymentData.setRecieptDate(startDate);
		paymentData.setRecieptNum("5435345");
		AccountActionDateEntity actionDate = accountBO
				.getAccountActionDate(Short.valueOf("1"));
		LoanPaymentData loanPaymentData = new LoanPaymentData(actionDate);
		paymentData.addAccountPaymentData(loanPaymentData);
		accountBO.applyPayment(paymentData);
		HibernateUtil.commitTransaction();
	}
	
	private void disburseLoan(Date startDate) throws NumberFormatException, AccountException, RepaymentScheduleException, FinancialException, SystemException {
		((LoanBO) accountBO).disburseLoan("1234", startDate,Short.valueOf("1"), accountBO.getPersonnel(), startDate, Short.valueOf("1"));
		HibernateUtil.commitTransaction();
	}
	
	private AccountBO getLoanAccount(Short accountSate, Date startDate,
			int disbursalType) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",
				ClientConstants.STATUS_ACTIVE, "1.4.1.1", group, new Date(
						System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		accountBO = TestObjectFactory.createLoanAccountWithDisbursement(
				"99999999999", group, accountSate, startDate, loanOffering,
				disbursalType);
		LoanActivityEntity loanActivity = new LoanActivityEntity(accountBO,TestObjectFactory.getPersonnel(userContext.getId()),"testing",new Money("100"),new Money("100"),new Money("100"),new Money("100"),new Money("100"),new Money("100"),new Money("100"),new Money("100"));
		((LoanBO) accountBO).addLoanActivity(loanActivity);
		addNotes();
		TestObjectFactory.updateObject(accountBO);
		return accountBO;
	}
}
