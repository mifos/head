package org.mifos.application.accounts.loan.struts.uihelpers;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.MONTHLY;
import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_SECOND_MONTH;

import java.util.Date;
import java.util.Locale;

import org.mifos.application.accounts.loan.struts.action.LoanAccountAction;
import org.mifos.application.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanUIHelperFnTest extends MifosMockStrutsTestCase {

	private UserContext userContext;

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml").getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/accounts/struts-config.xml").getPath());
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext",
				TestObjectFactory.getActivityContext());
		createFlow(request, LoanAccountAction.class);
	}

	public void testGetCurrrentDate() {
		Locale locale = new Locale("EN");
		assertEquals(DateUtils.getCurrentDate(locale), LoanUIHelperFn
				.getCurrrentDate(locale));
	}

	public void testGetMeetingRecurrence() throws Exception {
		UserContext userContext = TestObjectFactory.getContext();
		MeetingBO meeting = TestObjectFactory.getNewMeeting(MONTHLY, 
				EVERY_SECOND_MONTH, CUSTOMER_MEETING, MONDAY);
		assertEquals("2 month(s)", LoanUIHelperFn.getMeetingRecurrence(meeting,
				userContext));
	}

	public void testGetDoubleValue() {
		assertEquals("2.2", LoanUIHelperFn.getDoubleValue(new Double(2.2)));
		assertEquals("0.0", LoanUIHelperFn.getDoubleValue(null));
	}

	public void testRepaymentScheduleInstallment() {
		RepaymentScheduleInstallment repaymentScheduleInstallment = new RepaymentScheduleInstallment();
		long l = System.currentTimeMillis();
		repaymentScheduleInstallment.setDueDate(new Date(l));
		repaymentScheduleInstallment.setFees(new Money("100.0"));
		repaymentScheduleInstallment.setInstallment(10);
		repaymentScheduleInstallment.setInterest(new Money("100.0"));
		repaymentScheduleInstallment.setLocale(new Locale("1"));
		repaymentScheduleInstallment.setMiscFees(new Money("100.0"));
		repaymentScheduleInstallment.setMiscPenalty(new Money("100.0"));
		repaymentScheduleInstallment.setPrincipal(new Money("100.0"));

		double m = new Money("100").getAmountDoubleValue();
		assertEquals("Due date", new Date(l), repaymentScheduleInstallment
				.getDueDate());
		assertEquals("fees", m, repaymentScheduleInstallment.getFees()
				.getAmountDoubleValue());
		assertEquals("Installment", "10", repaymentScheduleInstallment
				.getInstallment().toString());
		assertEquals("Interest", m, repaymentScheduleInstallment.getFees()
				.getAmountDoubleValue());
		assertEquals("Locale", "1", repaymentScheduleInstallment.getLocale()
				.toString());
		assertEquals("Misc fees", m, repaymentScheduleInstallment.getMiscFees()
				.getAmountDoubleValue());
		assertEquals("Misc penalty", m, repaymentScheduleInstallment
				.getMiscPenalty().getAmountDoubleValue());
		assertEquals("principal", m, repaymentScheduleInstallment
				.getPrincipal().getAmountDoubleValue());
	}
}
