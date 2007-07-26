package org.mifos.application.reports.struts.action;

import org.mifos.application.reports.business.ReportsBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.util.helpers.Constants;
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

	public void testPreviewFailureWhenReportTitleIsEmpty() {
		setRequestPathInfo("/birtReportsUploadAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("reportTitle", "");
		addRequestParameter("reportCategoryId", "1");
		addRequestParameter("isActive", "1");
		actionPerform();
		verifyForwardPath("/birtReportsUploadAction.do?method=validate");
	}
	
	public void testPreviewFailureWhenReportCategoryIdIsEmpty() {
		setRequestPathInfo("/birtReportsUploadAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("reportTitle", "existTitle");
		addRequestParameter("reportCategoryId", "");
		addRequestParameter("isActive", "1");
		actionPerform();
		verifyForwardPath("/birtReportsUploadAction.do?method=validate");
	}
	
	public void testPreviewFailureWhenIsActiveIsEmpty() {
		setRequestPathInfo("/birtReportsUploadAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("reportTitle", "exsitTitle");
		addRequestParameter("reportCategoryId", "1");
		addRequestParameter("isActive", "");
		actionPerform();
		verifyForwardPath("/birtReportsUploadAction.do?method=validate");
	}
	
	public void testEdit() {
		setRequestPathInfo("/birtReportsUploadAction.do");
		addRequestParameter("method", "edit");
		addRequestParameter("reportId", "1");
		actionPerform();
		ReportsBO report = (ReportsBO) request.getAttribute(Constants.BUSINESS_KEY);
		assertEquals("1", report.getReportId().toString());
		verifyNoActionErrors();
		verifyForward(ActionForwards.edit_success.toString());
	}

}
