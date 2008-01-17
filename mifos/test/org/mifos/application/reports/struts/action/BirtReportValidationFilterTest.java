package org.mifos.application.reports.struts.action;

import junit.framework.TestCase;
import servletunit.FilterChainSimulator;
import servletunit.HttpServletRequestSimulator;
import servletunit.HttpServletResponseSimulator;
import servletunit.ServletContextSimulator;

public class BirtReportValidationFilterTest extends TestCase {
	public void testValidationForCollectionSheetReportWhenBranchIdIsSelect()
			throws Exception {
		BirtReportValidationFilter validationFilter = new BirtReportValidationFilter();
		HttpServletRequestSimulator request = new HttpServletRequestSimulator(
						new ServletContextSimulator());
		HttpServletResponseSimulator response = new HttpServletResponseSimulator();
		FilterChainSimulator filterChain = new FilterChainSimulator();
		request.setParameterValue("branchId", new String[]{"-2"});
		validationFilter.doFilter(request,
				response, filterChain);
		assertEquals(HttpServletResponseSimulator.SC_MOVED_TEMPORARILY,response.getStatusCode());
		assertEquals("/mifos", response.getHeader("Location"));
		
	}
}
