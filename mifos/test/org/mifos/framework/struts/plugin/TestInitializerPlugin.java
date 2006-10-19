package org.mifos.framework.struts.plugin;

import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;

public class TestInitializerPlugin extends MifosMockStrutsTestCase {

	private String flowKey;

	@Override
	public void setUp()throws Exception{
		super.setUp();
		setServletConfigFile(
			ResourceLoader.getURI("WEB-INF/web.xml").getPath());
		setConfigFile(ResourceLoader.getURI(
			"org/mifos/framework/struts/util/helpers/struts-config.xml"
		).getPath());
		
		// These will avoid the PageExpiredException but we seem to need more
		// to avoid creating other problems.
		//request.getSession();
		//flowKey = createFlow(request, SavingsAction.class);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testLabelConstants() throws Exception{
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("method","load");
		addRequestParameter("recordOfficeId","0");
		addRequestParameter("recordLoanOfficerId","0");
		actionPerform();
		// TODO: Enable this.
//		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals(ConfigurationConstants.BRANCHOFFICE,
				(String)context.getAttribute("LABEL_"+ ConfigurationConstants.BRANCHOFFICE.toUpperCase()));
	}
}
