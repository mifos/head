package org.mifos.application.personnel.struts.action;

import java.net.URISyntaxException;

import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestPersonAction extends MifosMockStrutsTestCase {
	private String flowKey;

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
		UserContext userContext = TestObjectFactory.getUserContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		Flow flow = new Flow();
		flowKey = String.valueOf(System.currentTimeMillis());
		FlowManager flowManager = new FlowManager();
		flowManager.addFLow(flowKey, flow);
		request.getSession(false).setAttribute(Constants.FLOWMANAGER,
				flowManager);
	}
	
	public void testChooseOffice(){
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.chooseOffice.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.chooseOffice_success.toString());
	}
	public void testLoad() throws Exception{
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.load.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY,flowKey);
		addRequestParameter("officeId","1");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
		
/*		assertNotNull(SessionUtils.getAttribute(MasterConstants.PERSONNEL_TITLE, request));
		assertNotNull(SessionUtils.getAttribute(MasterConstants.PERSONNEL_LEVELS, request));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request));
*/	}
}
