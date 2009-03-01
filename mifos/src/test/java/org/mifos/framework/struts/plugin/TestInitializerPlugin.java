package org.mifos.framework.struts.plugin;

import java.util.Date;

import org.mifos.application.accounts.savings.struts.action.SavingsAction;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestInitializerPlugin extends MifosMockStrutsTestCase {

	public TestInitializerPlugin() throws SystemException, ApplicationException {
        super();
    }

    private SavingsOfferingBO product;

	@Override
	public void setUp()throws Exception{
		super.setUp();
		
		request.getSession(true);
		createFlowAndAddToRequest(SavingsAction.class);
		request.getSession().setAttribute(Constants.USERCONTEXT, 
			TestUtils.makeUser());
		
		product = TestObjectFactory.createSavingsProduct(
			"Offering1", "s1", 
			SavingsType.MANDATORY, ApplicableTo.CLIENTS, 
			new Date(System.currentTimeMillis()));
		addRequestParameter("selectedPrdOfferingId", 
			product.getPrdOfferingId().toString());
	}
	
	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(product);
		super.tearDown();
	}

	public void testLabelConstants() throws Exception{
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method","load");
		addRequestParameter("recordOfficeId","0");
		addRequestParameter("recordLoanOfficerId","0");
		performNoErrors();
		assertEquals(ConfigurationConstants.BRANCHOFFICE,
			(String)context.getAttribute(
				"LABEL_"+ ConfigurationConstants.BRANCHOFFICE.toUpperCase()));
	}

}
