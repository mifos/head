package org.mifos.application.admin.struts.action;

import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;

public class TestAdminAction extends MifosMockStrutsTestCase {
	public TestAdminAction() throws SystemException, ApplicationException {
        super();
    }

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
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
	}

	@Override
	protected void tearDown() throws Exception{
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testVerifyAdminForward() {
		setRequestPathInfo("/AdminAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyForwardPath("/pages/application/admin/jsp/admin.jsp");
	}
}
