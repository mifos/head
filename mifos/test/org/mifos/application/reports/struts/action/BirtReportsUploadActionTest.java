package org.mifos.application.reports.struts.action;

import org.mifos.application.reports.business.ReportsCategoryBO;
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

	public void testPreview() {
		setRequestPathInfo("/birtReportsUploadAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("reportTitle", "template 1");
		addRequestParameter("reportCategoryId", "1");
		// TODO: Form file?
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		ReportsCategoryBO category = (ReportsCategoryBO) request
				.getAttribute("category");
		assertNotNull(category);
		assertEquals(1, (int) category.getReportCategoryId());
	}
}
