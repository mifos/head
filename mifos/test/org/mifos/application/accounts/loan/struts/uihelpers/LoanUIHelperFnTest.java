package org.mifos.application.accounts.loan.struts.uihelpers;

import java.util.Locale;

import org.mifos.application.accounts.loan.struts.action.LoanAccountAction;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanUIHelperFnTest extends MifosMockStrutsTestCase {

	private UserContext userContext;

	private String flowKey;

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
		flowKey = createFlow(request, LoanAccountAction.class);
	}
	public void testGetCurrrentDate() {
		Locale locale = new Locale("EN");
		assertEquals(DateHelper.getCurrentDate(locale), LoanUIHelperFn.getCurrrentDate(locale));
	}

	public void testGetMeetingRecurrence() throws Exception {
		UserContext userContext = TestObjectFactory.getContext();
		MeetingBO meeting = TestObjectFactory.getMeeting("2", "2", Short.valueOf("2"));
		assertEquals("2 month(s)", LoanUIHelperFn.getMeetingRecurrence(meeting, userContext));
	}

	public void testGetDoubleValue() {
		assertEquals("2.2", LoanUIHelperFn.getDoubleValue(new Double(2.2)));
		assertEquals("0.0", LoanUIHelperFn.getDoubleValue(null));
	}

}
