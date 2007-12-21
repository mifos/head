package org.mifos.framework.util.helpers;

import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;

public class FlowManagerHelperTest extends MifosMockStrutsTestCase {

	private FlowManagerHelper flowManagerHelper = null;

	private String flowKey = "";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UserContext userContext = TestUtils.makeUserWithLocales();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		flowKey = createFlow(request, FlowManagerHelperTest.class);
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		flowManagerHelper = new FlowManagerHelper();
		SessionUtils.setAttribute("test", "test", request);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetFlow() {
		FlowManager manager = (FlowManager) SessionUtils.getAttribute(
				Constants.FLOWMANAGER, request.getSession());
		Flow flow = (Flow)flowManagerHelper.getFlow(manager, flowKey);
		assertEquals("test", (String)flow.getObjectFromSession("test"));
	}

	public void testGetFlowFlowManagerString() {
		FlowManager manager = (FlowManager) SessionUtils.getAttribute(
				Constants.FLOWMANAGER, request.getSession());
		assertEquals(
			"test", 
			(String)flowManagerHelper.getFlow(manager, flowKey, "test"));
		addRequestParameter(Constants.CURRENTFLOWKEY, "");
		assertNull(flowManagerHelper.getFlow(manager, null, "test"));
	}
}
