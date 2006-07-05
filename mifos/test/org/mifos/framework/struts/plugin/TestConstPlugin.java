package org.mifos.framework.struts.plugin;

import java.net.URISyntaxException;
import java.util.Map;

import org.mifos.framework.util.helpers.ResourceLoader;

import org.mifos.framework.MifosMockStrutsTestCase;

public class TestConstPlugin extends MifosMockStrutsTestCase{
	public void setUp()throws Exception{
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml").getPath());
			setConfigFile(ResourceLoader.getURI("org/mifos/framework/util/helpers/struts-config.xml").getPath());
		} catch (URISyntaxException e) {

			e.printStackTrace();
		}
	}
	
	/**
	 *This method performs an action to load Plugins defined in struts-config.xml.
	 */
	public void testMasterConstants() throws Exception{
		setRequestPathInfo("/mifosproddefaction.do");
		addRequestParameter("method","load");
		addRequestParameter("recordOfficeId","0");
		addRequestParameter("recordLoanOfficerId","0");
		actionPerform();
		Map constantMap = (Map)context.getAttribute("MasterConstants");
		assertNotNull(constantMap);
		assertEquals("PaymentType",constantMap.get("PAYMENT_TYPE"));
		assertEquals(Short.valueOf("1"),constantMap.get("CUSTOMFIELD_NUMBER"));
	}
	
	public void testIfAllConstantFilesAreLoaded(){
		setRequestPathInfo("/mifosproddefaction.do");
		addRequestParameter("method","load");
		addRequestParameter("recordOfficeId","0");
		addRequestParameter("recordLoanOfficerId","0");
		actionPerform();
		assertNotNull(context.getAttribute("Constants"));
		assertNotNull(context.getAttribute("MasterConstants"));
		assertNotNull(context.getAttribute("CustomerConstants"));
		assertNotNull(context.getAttribute("AccountStates"));
		assertNotNull(context.getAttribute("SavingsConstants"));
	}
	
}
