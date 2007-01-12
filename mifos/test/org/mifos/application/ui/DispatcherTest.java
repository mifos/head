package org.mifos.application.ui;

import static junitx.framework.StringAssert.assertContains;
import static junitx.framework.StringAssert.assertNotContains;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.dom4j.DocumentException;
import org.mifos.framework.TestUtils;
import org.mifos.framework.persistence.DatabaseVersionPersistence;

import servletunit.HttpServletRequestSimulator;
import servletunit.HttpServletResponseSimulator;
import servletunit.ServletContextSimulator;

public class DispatcherTest extends TestCase {
	
	public void testMainPageNoTrailingSlash() throws Exception {
		HttpServletResponseSimulator response = dispatch(null);
		assertEquals(303, response.getStatusCode());
		assertEquals("https://test-server:123/context/developer/",
			response.getHeader("Location"));
	}

	public void testMainPageWithTrailingSlash() throws Exception {
		HttpServletResponseSimulator response = dispatch("/");
		String out = getSuccessfulDocument(response);
		assertContains("page is for features", out);
		assertContains("Database version = " + 
			DatabaseVersionPersistence.APPLICATION_VERSION, out);
		assertContains(
			"<a href=\"custSearchAction.do?method=getHomePage\">Home</a>", 
			out);
		assertContains(
			"<a href=\"reports/create\">Create report</a>",
			out);
	}

	public void testCreateReportPage() throws Exception {
		HttpServletResponseSimulator response = dispatch("/reports/create");
		String out = getSuccessfulDocument(response);
		assertNotContains("page is for features", out);
	}
	
	public void testNotFound() throws Exception {
		HttpServletResponseSimulator response = dispatch("/foo/bar");
		assertEquals(404, response.getStatusCode());
	}

	private String getSuccessfulDocument(HttpServletResponseSimulator response) 
	throws DocumentException {
		assertEquals(200, response.getStatusCode());
		String out = response.getWriterBuffer().toString();
		TestUtils.assertWellFormedDocument(out);
		return out;
	}
	
	private HttpServletResponseSimulator dispatch(String path) 
	throws Exception {
		ServletContext context = new ServletContextSimulator();
		HttpServletRequestSimulator request = 
			new HttpServletRequestSimulator(context);
		HttpServletResponseSimulator response = 
			new HttpServletResponseSimulator();
		request.setMethod(HttpServletRequestSimulator.GET);
		request.setScheme("https");
		request.setServerName("test-server");
		request.setServerPort(123);
		request.setContextPath("/context");
		request.setServletPath("/developer");
		request.setPathInfo(path);
		new Dispatcher().service(request, response);
		return response;
	}

}
