package org.mifos.framework.struts.plugin;

import java.net.URISyntaxException;
import java.util.Map;

import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.util.helpers.ResourceLoader;

public class TestEnumPlugin extends MifosMockStrutsTestCase{

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

	public void testCustomFieldType() throws Exception{
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method","load");
		addRequestParameter("recordOfficeId","0");
		addRequestParameter("recordLoanOfficerId","0");
		actionPerform();
		Map constantMap = (Map)context.getAttribute("CustomFieldType");
		assertNotNull(constantMap);
		assertEquals("NUMERIC", constantMap.get("NUMERIC").toString());
	}

	public void testIfAllConstantFilesAreLoaded(){
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method","load");
		addRequestParameter("recordOfficeId","0");
		addRequestParameter("recordLoanOfficerId","0");
		actionPerform();
		assertNotNull(context.getAttribute("FeeFrequencyType"));
		assertNotNull(context.getAttribute("FeeCategory"));
		assertNotNull(context.getAttribute("FeeLevel"));
		assertNotNull(context.getAttribute("FeePayment"));
		assertNotNull(context.getAttribute("FeeStatus"));
		assertNotNull(context.getAttribute("RateAmountFlag"));
		assertNotNull(context.getAttribute("RecurrenceType"));
		assertNotNull(context.getAttribute("AccountTypes"));
		assertNotNull(context.getAttribute("OfficeStatus"));
		assertNotNull(context.getAttribute("OfficeLevel"));
		assertNotNull(context.getAttribute("CustomerStatus"));
		assertNotNull(context.getAttribute("CustomerLevel"));
		assertNotNull(context.getAttribute("PrdStatus"));
		assertNotNull(context.getAttribute("AccountState"));
		assertNotNull(context.getAttribute("RecommendedAmountUnit"));
	}

}