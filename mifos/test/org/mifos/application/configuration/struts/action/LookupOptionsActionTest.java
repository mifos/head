package org.mifos.application.configuration.struts.action;

import org.mifos.application.configuration.struts.actionform.LookupOptionsActionForm;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.ResourceLoader;

public class LookupOptionsActionTest extends MifosMockStrutsTestCase{

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
		assertEquals("Mr", salutations[0]);
		assertEquals("Mrs", salutations[1]);
		
		
		
		
	}
}
