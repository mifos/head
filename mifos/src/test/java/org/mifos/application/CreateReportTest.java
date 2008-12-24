package org.mifos.application;

import static junitx.framework.StringAssert.assertContains;
import static junitx.framework.StringAssert.assertNotContains;
import static org.mifos.application.ui.DispatchTestUtil.dispatch;
import static org.mifos.application.ui.DispatchTestUtil.getSuccessfulDocument;
import static org.mifos.application.ui.DispatchTestUtil.makeRequest;
import junit.framework.TestCase;

import org.mifos.application.ui.DispatchTestUtil;
import org.mifos.application.ui.Dispatcher;

import servletunit.HttpServletRequestSimulator;
import servletunit.HttpServletResponseSimulator;

public class CreateReportTest extends TestCase {

	public void testCreateReportPage() throws Exception {
		HttpServletResponseSimulator response = dispatch("/reports/create");
		String out = getSuccessfulDocument(response);
		assertNotContains("page is for features", out);
		assertContains("Report name", out);
		assertContains("input name=\"name\"", out);
	}
	
	public void testCreateReport() throws Exception {
		HttpServletResponseSimulator response = 
			new HttpServletResponseSimulator();
		HttpServletRequestSimulator request =
			makeRequest("/reports/create", 
				HttpServletRequestSimulator.POST);
		request.addParameter("name", "newReport");
		new Dispatcher().service(request, response);
		assertEquals(Dispatcher.CREATED, response.getStatusCode());
		assertEquals("https://test-server:123/context/developer/reports/1", 
			response.getHeader("Location"));
	}
	
	public void testRunReportPage() throws Exception {
		HttpServletResponseSimulator response = dispatch("/reports/53");
		/* A forward might make more sense, but we'll stick with the
		   redirect for now (not sure how to forward to a jsp and all). */
		assertEquals(303, response.getStatusCode());
		assertEquals(
			"https://test-server:123/context/reportsUserParamsAction.do" +
			"?method=loadAddList&reportId=53",
			response.getHeader("Location"));
	}
	
	public void testBadUrlUnderReports() throws Exception {
		DispatchTestUtil.verifyNotFound("/reports/53/6");
	}

}
