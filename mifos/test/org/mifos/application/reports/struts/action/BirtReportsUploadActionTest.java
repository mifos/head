package org.mifos.application.reports.struts.action;

import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.util.helpers.ResourceLoader;

public class BirtReportsUploadActionTest extends MifosMockStrutsTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/reports/struts-config.xml").getPath());
	}
	
	public void testGetBirtReportsUploadPage() {
		setRequestPathInfo("/birtReportsUploadAction.do");
		addRequestParameter("method", "getBirtReportsUploadPage");
		addRequestParameter("viewPath", "administerreports_path");
		actionPerform();
		verifyForward("uploadreports_path");
		verifyNoActionErrors();
	}

}
