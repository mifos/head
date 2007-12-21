package org.mifos.application.accounts.loan.struts.action;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanSummaryEntity;
import org.mifos.application.accounts.loan.business.TestLoanBO;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestRepayLoanAction extends MifosMockStrutsTestCase {

	protected AccountBO accountBO = null;

	private CustomerBO center = null;

	private CustomerBO group = null;
	private UserContext userContext;
	private String flowKey;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, RepayLoanAction.class);
		accountBO = getLoanAccount();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		accountBO=(AccountBO)HibernateUtil.getSessionTL().get(AccountBO.class,accountBO.getAccountId());
	}
	
	@Override
	protected void tearDown() throws Exception {
		accountBO=(AccountBO)HibernateUtil.getSessionTL().get(AccountBO.class,accountBO.getAccountId());
		group=(CustomerBO)HibernateUtil.getSessionTL().get(CustomerBO.class,group.getCustomerId());
		center=(CustomerBO)HibernateUtil.getSessionTL().get(CustomerBO.class,center.getCustomerId());
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	public void testLoadRepayment() throws Exception{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/repayLoanAction");
		addRequestParameter("method", "loadRepayment");
		addRequestParameter("globalAccountNum", accountBO.getGlobalAccountNum());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward(Constants.LOAD_SUCCESS);
		Money amount =(Money)SessionUtils.getAttribute(LoanConstants.TOTAL_REPAYMENT_AMOUNT,request);
		assertEquals(amount,((LoanBO)accountBO).getTotalEarlyRepayAmount());		
	}
	
	public void testRepaymentPreview(){
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/repayLoanAction");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
	}
	
	public void testRepaymentPrevious(){
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/repayLoanAction");
		addRequestParameter("method", "previous");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward(Constants.PREVIOUS_SUCCESS);
	}
	
	public void testMakeRepaymentForCurrentDateSameAsInstallmentDate() throws Exception{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,accountBO,request);
		Money amount=((LoanBO)accountBO).getTotalEarlyRepayAmount();
		setRequestPathInfo("/repayLoanAction");
		addRequestParameter("method", "makeRepayment");
		addRequestParameter("globalAccountNum",accountBO.getGlobalAccountNum());
		addRequestParameter("paymentTypeId","1");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward(Constants.UPDATE_SUCCESS);
		
		assertEquals(accountBO.getAccountState().getId(),Short.valueOf(AccountStates.LOANACC_OBLIGATIONSMET));
		
		LoanSummaryEntity loanSummaryEntity=((LoanBO)accountBO).getLoanSummary();
		assertEquals(amount,loanSummaryEntity.getPrincipalPaid().add(loanSummaryEntity.getFeesPaid()).add(loanSummaryEntity.getInterestPaid()).add(loanSummaryEntity.getPenaltyPaid()));
		
	}
	
	public void testMakeRepaymentForCurrentDateLiesBetweenInstallmentDates() throws Exception{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		changeFirstInstallmentDate(accountBO);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,accountBO,request);
		Money amount=((LoanBO)accountBO).getTotalEarlyRepayAmount();
	
		setRequestPathInfo("/repayLoanAction");
		addRequestParameter("method", "makeRepayment");
		addRequestParameter("globalAccountNum",accountBO.getGlobalAccountNum());
		addRequestParameter("paymentTypeId","1");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward(Constants.UPDATE_SUCCESS);
		
		assertEquals(accountBO.getAccountState().getId(),Short.valueOf(AccountStates.LOANACC_OBLIGATIONSMET));
		
		LoanSummaryEntity loanSummaryEntity=((LoanBO)accountBO).getLoanSummary();
		assertEquals(amount,loanSummaryEntity.getPrincipalPaid().add(loanSummaryEntity.getFeesPaid()).add(loanSummaryEntity.getInterestPaid()).add(loanSummaryEntity.getPenaltyPaid()));
		
	}
	
	private void changeFirstInstallmentDate(AccountBO accountBO){
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH-1);
		currentDateCalendar = new GregorianCalendar(year, month, day);
		for(AccountActionDateEntity accountActionDateEntity:accountBO.getAccountActionDates()){
			TestLoanBO.setActionDate(accountActionDateEntity,new java.sql.Date(currentDateCalendar.getTimeInMillis()));
			break;
		}
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


}
