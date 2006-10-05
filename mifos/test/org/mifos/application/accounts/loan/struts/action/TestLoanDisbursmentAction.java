package org.mifos.application.accounts.loan.struts.action;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.struts.actionforms.LoanDisbursmentActionForm;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestLoanDisbursmentAction extends MifosMockStrutsTestCase {

	protected UserContext userContext = null;

	protected LoanBO accountBO = null;
	
	protected LoanBO loanBO = null;

	protected CustomerBO center = null;

	protected CustomerBO group = null;
	
	private Date currentDate = null;
	
	private Calendar calendar = null;
	
	private String flowKey;
	
	@Override
	protected void setUp() throws Exception {

		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
					.getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/application/accounts/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
		Flow flow = new Flow();
		flowKey = String.valueOf(System.currentTimeMillis());
		FlowManager flowManager = new FlowManager();
		flowManager.addFLow(flowKey, flow);
		request.getSession(false).setAttribute(Constants.FLOWMANAGER,
				flowManager);		
		setRequestPathInfo("/loanDisbursmentAction");
		 calendar = new GregorianCalendar();
		currentDate=new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short
				.valueOf(AccountStates.LOANACC_APPROVED),currentDate , 2);
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(loanBO);
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	private LoanBO getLoanAccount(Short accountSate, Date startDate,
			int disbursalType) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccountWithDisbursement(
				"99999999999", group, accountSate, startDate, loanOffering,
				disbursalType);

	}

	public void testLoad() throws Exception  {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("method", "load");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward(Constants.LOAD_SUCCESS);
		verifyNoActionErrors();
		assertNotNull(SessionUtils.getAttribute(
				MasterConstants.PAYMENT_TYPE,request));
		LoanDisbursmentActionForm actionForm = (LoanDisbursmentActionForm) request
				.getSession().getAttribute("loanDisbursmentActionForm");
		assertEquals(actionForm.getAmount(), accountBO
				.getAmountTobePaidAtdisburtail(new Date(System
						.currentTimeMillis())));
		assertEquals(actionForm.getLoanAmount(), accountBO.getLoanAmount());

	}

	public void testPreviewFailure_NomaadatoryFieds() {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
		String []errors = {AccountConstants.ERROR_MANDATORY,AccountConstants.ERROR_MANDATORY};
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "preview");
		actionPerform();

		verifyInputForward();
		verifyActionErrors(errors);
		

	}
	public void testPreviewFailure_futureTxnDate() {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
		
		Calendar calendar = new GregorianCalendar();
		calendar.roll(Calendar.YEAR,1);
		addRequestParameter("receiptDate", sdf.format(new Date(calendar.getTimeInMillis())));
		addRequestParameter("transactionDate", sdf.format(new Date(calendar.getTimeInMillis())));
		addRequestParameter("paymentTypeId", "1");
		String []errors = {AccountConstants.ERROR_FUTUREDATE,AccountConstants.ERROR_FUTUREDATE}; 
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "preview");
		actionPerform();

		verifyInputForward();
		verifyActionErrors(errors);
		

	}
	
	
	public void testPreviewSucess(){
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		addRequestParameter("receiptDate", sdf.format(currentDate));
		addRequestParameter("transactionDate", sdf.format(currentDate));
		addRequestParameter("paymentTypeId", "1");
		addRequestParameter("paymentModeOfPayment", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "preview");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(Constants.PREVIEW_SUCCESS);
		
	}
	
	public void testPreviuos(){
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("method", "previous");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward(Constants.PREVIOUS_SUCCESS);

	}

	public void testUpdate()throws Exception{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		addRequestParameter("receiptDate", sdf.format(currentDate));
		addRequestParameter("transactionDate", sdf.format(currentDate));
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO,request);
		request.getSession().setAttribute(Constants.USER_CONTEXT_KEY, userContext);
		addRequestParameter("paymentTypeId", "1");
		addRequestParameter("paymentModeOfPayment", "1");
		addRequestParameter("method", "update");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyForward(Constants.UPDATE_SUCCESS);

	}
	public void testUpdateNopaymentAtDisbursal()throws Exception{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		loanBO =getLoanAccount(Short
				.valueOf(AccountStates.LOANACC_APPROVED),currentDate , 3);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		addRequestParameter("receiptDate", sdf.format(currentDate));
		addRequestParameter("transactionDate", sdf.format(currentDate));
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO,request);
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		request.getSession().setAttribute(Constants.USER_CONTEXT_KEY, userContext);
		addRequestParameter("paymentTypeId", "1");
		addRequestParameter("method", "update");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(Constants.UPDATE_SUCCESS);
	
		
	}
}
