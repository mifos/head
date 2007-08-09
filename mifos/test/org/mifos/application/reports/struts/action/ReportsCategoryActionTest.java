package org.mifos.application.reports.struts.action;

import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.util.helpers.ResourceLoader;

public class ReportsCategoryActionTest extends MifosMockStrutsTestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/reports/struts-config.xml").getPath());
	}

	public void testShouldForwardToDefineNewCategoryPage() throws Exception {
		setRequestPathInfo("/reportsCategoryAction.do");
		addRequestParameter("method", "loadDefineNewCategoryPage");
		actionPerform();
		verifyForward("load_success");
		verifyForwardPath("/pages/application/reports/jsp/defineNewReportsCategory.jsp");
		verifyNoActionErrors();
	}
}
