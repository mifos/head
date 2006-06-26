package org.mifos.application.reports.struts.action;


import servletunit.struts.MockStrutsTestCase;

public class TReportsAction extends MockStrutsTestCase {

	public TReportsAction(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testVerifyForwardOfReport(){
		addRequestParameter("viewPath", "report_designer");
		setRequestPathInfo("/reportsAction.do");
		addRequestParameter("method","getReportPage");
        actionPerform();
		verifyForwardPath("/pages/application/reports/jsp/report_designer.jsp");
	}

}
