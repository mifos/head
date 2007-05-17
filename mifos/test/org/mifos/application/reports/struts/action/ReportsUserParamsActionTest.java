package org.mifos.application.reports.struts.action;

import java.net.URISyntaxException;

import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;

public class ReportsUserParamsActionTest extends MifosMockStrutsTestCase {
	
	private UserContext userContext;


	@Override
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
		userContext = TestUtils.makeUser();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);

	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testLoadAddListShouldGoToBirtReport(){
		setRequestPathInfo("/reportsUserParamsAction.do");
		addRequestParameter("method", "loadAddList");
		addRequestParameter("reportId", "28");
		actionPerform();
		verifyForwardPath("/pages/application/reports/jsp/birtReport.jsp");
		
		
	}

}
