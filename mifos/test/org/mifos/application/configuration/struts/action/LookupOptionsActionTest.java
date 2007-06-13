package org.mifos.application.configuration.struts.action;

import org.mifos.application.configuration.struts.actionform.LookupOptionsActionForm;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LookupOptionsActionTest extends MifosMockStrutsTestCase{

	private UserContext userContext;

	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml").getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/configuration/struts-config.xml")
				.getPath());
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext",
				TestObjectFactory.getActivityContext());
		
	}

	private boolean compareLists(String[] first, String[] second) {
		if (first.length != second.length) return false;
		for (int index = 0; index < first.length; ++index) {
			if (first[index].compareTo(second[index]) != 0) {
				return false;
			}
		}
		return true;
	}
	
	public void testLoad() throws Exception {
		setRequestPathInfo("/lookupOptionsAction.do");
		addRequestParameter("method", "load");
		performNoErrors();
		verifyForward(ActionForwards.load_success.toString());
		LookupOptionsActionForm lookupOptionsActionForm = 
			(LookupOptionsActionForm) request
				.getSession().getAttribute("lookupoptionsactionform");
		String[] salutations = lookupOptionsActionForm.getSalutations();
		String[] EXPECTED_SALUTATIONS = {"Mr","Mrs","Ms"};
		assertTrue(compareLists(salutations, EXPECTED_SALUTATIONS));
		
	}
}
