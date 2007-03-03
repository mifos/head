package org.mifos.framework.struts.plugin;

import org.mifos.application.accounts.savings.struts.action.SavingsAction;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestInitializerPlugin extends MifosMockStrutsTestCase {

	private SavingsOfferingBO offering;

	@Override
	public void setUp()throws Exception{
		super.setUp();
		setServletConfigFile(
			ResourceLoader.getURI("WEB-INF/web.xml").getPath());
		setConfigFile(ResourceLoader.getURI(
			"org/mifos/framework/struts/util/helpers/struts-config.xml"
		).getPath());
		
		request.getSession(true);
		createFlowAndAddToRequest(SavingsAction.class);
		request.getSession().setAttribute(Constants.USERCONTEXT, 
			TestUtils.makeUser(1));
		
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

	public void testLabelConstants() throws Exception{
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method","load");
		addRequestParameter("recordOfficeId","0");
		addRequestParameter("recordLoanOfficerId","0");
		performNoErrors();
		assertEquals(ConfigurationConstants.BRANCHOFFICE,
				(String)context.getAttribute("LABEL_"+ ConfigurationConstants.BRANCHOFFICE.toUpperCase()));
	}

}
