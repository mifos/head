package org.mifos.application.reports.struts.action;

import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.reports.util.helpers.ReportsConstants;
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
		verifyForward("load_success");
		verifyNoActionErrors();
	}

	public void testPreviewFailure() {
		setRequestPathInfo("/birtReportsUploadAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("reportTitle", "");
		addRequestParameter("reportCategoryId", "");
		actionPerform();
		verifyActionErrors(new String[] { ReportsConstants.ERROR_TITLE,
				ReportsConstants.ERROR_CATEGORYID,
				ReportsConstants.ERROR_FILEISNULL });
		verifyForwardPath("/birtReportsUploadAction.do?method=validate");
	}

}
