package org.mifos.application.office.struts;

import java.net.URISyntaxException;
import java.util.List;

import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestOfficeAction extends MifosMockStrutsTestCase {
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
	}
	
	public void testLoad(){
		setRequestPathInfo("/offAction.do");
		addRequestParameter("method","load");
		addRequestParameter("officeLevel","5");
        actionPerform();
		verifyForward("load_success");
		List<OfficeView>  parents =(List<OfficeView>) request.getSession().getAttribute(OfficeConstants.PARENTS);
		assertEquals(2,parents.size());
		List<OfficeView>  levels =(List<OfficeView>) request.getSession().getAttribute(OfficeConstants.OFFICELEVELLIST);
		assertEquals(4,levels.size());

	}
	public void testLoadParent(){
		setRequestPathInfo("/offAction.do");
		addRequestParameter("method","loadParent");
		addRequestParameter("officeLevel","2");
        actionPerform();
		verifyForward("load_success");
		List<OfficeView>  parents =(List<OfficeView>) request.getSession().getAttribute(OfficeConstants.PARENTS);
		assertEquals(1,parents.size());
	}
}
