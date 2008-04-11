package org.mifos.application.reports.struts.action;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.Constants;

public class TestReportsAction extends MifosMockStrutsTestCase {
	private UserContext userContext;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userContext = TestUtils.makeUser();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
	}

	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testVerifyForwardOfNonExistentReportThrowsSecurityError() {
		addRequestParameter("viewPath", "report_designer");
		setRequestPathInfo("/reportsAction.do");
		addRequestParameter("method", "getReportPage");
		actionPerform();
		ActionErrors errors = (ActionErrors) request
						.getAttribute(Globals.ERROR_KEY);
		ActionMessage retrievedMessage = (ActionMessage)(errors).get(
						SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED).next();
		ActionMessage expectedErrorMessage = new ActionMessage(
				SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
		assertEquals(expectedErrorMessage.toString(), retrievedMessage.toString());
	}
}
