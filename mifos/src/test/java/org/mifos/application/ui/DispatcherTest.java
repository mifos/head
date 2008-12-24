package org.mifos.application.ui;

import static junitx.framework.StringAssert.assertContains;
import static org.mifos.application.ui.DispatchTestUtil.dispatch;
import static org.mifos.application.ui.DispatchTestUtil.dispatchPost;
import static org.mifos.application.ui.DispatchTestUtil.getSuccessfulDocument;
import junit.framework.TestCase;

import org.mifos.framework.persistence.DatabaseVersionPersistence;

import servletunit.HttpServletResponseSimulator;

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
			"<a href=\"/context/custSearchAction.do?method=getHomePage\">Home</a>", 
			out);
		assertContains(
			"<a href=\"reports/create\">Create report</a>",
			out);
	}

	public void testGetNotFound() throws Exception {
		DispatchTestUtil.verifyNotFound("/foo/bar");
	}

	public void testPostNotFound() throws Exception {
		HttpServletResponseSimulator response = dispatchPost("/foo/bar");
		assertEquals(404, response.getStatusCode());
	}

}
