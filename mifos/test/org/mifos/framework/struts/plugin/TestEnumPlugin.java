package org.mifos.framework.struts.plugin;

import java.util.ArrayList;
import java.util.Map;

import org.mifos.application.accounts.savings.struts.action.SavingsAction;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.EnumsNotLoadedException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestEnumPlugin extends MifosMockStrutsTestCase {

	private SavingsOfferingBO offering;

	@Override
	public void setUp()throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml").getPath());
		setConfigFile(ResourceLoader.getURI("org/mifos/framework/struts/util/helpers/struts-config.xml").getPath());
		request.getSession(true);
		createFlowAndAddToRequest(SavingsAction.class);
		request.getSession().setAttribute(Constants.USERCONTEXT, 
			TestUtils.makeUser());
		
		offering = TestObjectFactory.createSavingsOffering(
			"Offering1", "s1", 
			SavingsType.MANDATORY, ApplicableTo.CLIENTS);
		addRequestParameter("selectedPrdOfferingId", 
			offering.getPrdOfferingId().toString());
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(offering);
		super.tearDown();
	}

	public void testCustomFieldType() throws Exception{
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method","load");
		addRequestParameter("recordOfficeId","0");
		addRequestParameter("recordLoanOfficerId","0");
		performNoErrors();
		Map constantMap = (Map)context.getAttribute("CustomFieldType");
		assertNotNull(constantMap);
		assertEquals("NUMERIC", constantMap.get("NUMERIC").toString());
	}

	public void testIfAllConstantFilesAreLoaded(){
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method","load");
		addRequestParameter("recordOfficeId","0");
		addRequestParameter("recordLoanOfficerId","0");
		performNoErrors();
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

	public void testEnumPluginException() throws Exception {
		EnumPlugin enumPlugin = new EnumPlugin();
		ArrayList<String> enumPluginClasses = new ArrayList<String>();
		enumPluginClasses.add("org.mifos.doesNotExist");
		try {
			enumPlugin.buildClasses(enumPluginClasses);
			fail();
		} catch (EnumsNotLoadedException expected) {
			assertEquals("exception.framework.SystemException", 
				expected.getKey());
		}
	}

}
