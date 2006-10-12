package org.mifos.framework.struts.plugin;

import java.net.URISyntaxException;
import java.util.Map;

import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.framework.util.helpers.ResourceLoader;

import org.mifos.framework.MifosMockStrutsTestCase;

public class TestInitializerPlugin extends MifosMockStrutsTestCase{

	@Override
	public void setUp()throws Exception{
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml").getPath());
			setConfigFile(ResourceLoader.getURI("org/mifos/framework/struts/util/helpers/struts-config.xml").getPath());
		} catch (URISyntaxException e) {

			e.printStackTrace();
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	public void testLabelConstants() throws Exception{
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method","load");
		addRequestParameter("recordOfficeId","0");
		addRequestParameter("recordLoanOfficerId","0");
		actionPerform();
		assertEquals(ConfigurationConstants.BRANCHOFFICE,
				(String)context.getAttribute("LABEL_"+ ConfigurationConstants.BRANCHOFFICE.toUpperCase()));
	}
}