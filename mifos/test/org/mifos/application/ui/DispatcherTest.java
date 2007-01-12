package org.mifos.application.ui;

import static junitx.framework.StringAssert.assertContains;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.mifos.framework.TestUtils;
import org.mifos.framework.persistence.DatabaseVersionPersistence;

import servletunit.HttpServletRequestSimulator;
import servletunit.HttpServletResponseSimulator;
import servletunit.ServletContextSimulator;

public class DispatcherTest extends TestCase {
	
	public void testMainPage() throws Exception {
		HttpServletResponseSimulator response = dispatch();
		assertEquals(200, response.getStatusCode());
		String out = response.getWriterBuffer().toString();
		TestUtils.assertWellFormedDocument(out);
		assertContains("page is for features", out);
		assertContains("Database version = " + 
			DatabaseVersionPersistence.APPLICATION_VERSION, out);
		assertContains(
			"<a href=\"custSearchAction.do?method=getHomePage\">Home</a>", 
			out);
	}

	private HttpServletResponseSimulator dispatch() throws Exception {
		ServletContext context = new ServletContextSimulator();
		HttpServletRequestSimulator request = 
			new HttpServletRequestSimulator(context);
		HttpServletResponseSimulator response = 
			new HttpServletResponseSimulator();
		request.setMethod(HttpServletRequestSimulator.GET);
		new Dispatcher().service(request, response);
		return response;
	}

}
