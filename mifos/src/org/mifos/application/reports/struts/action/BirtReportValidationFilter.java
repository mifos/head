package org.mifos.application.reports.struts.action;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class BirtReportValidationFilter implements Filter {
	
	private String errorMessages;

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		errorMessages = "";
		filter(request.getParameter("__report"), "report/ActiveLoansByLoanOfficer.rptdesign", request.getParameter("branchId"), "Please select a Branch Office.");
		filter(request.getParameter("__report"), "report/DetailedAgingPortfolioAtRisk.rptdesign", request.getParameter("branchId"), "Please select a Branch Office.");
		if(errorMessages.length() == 0) {
			chain.doFilter(request, response);
		}
		else {
			showError(request, response);
		}
	}

	private void filter(String actualReport, String expectedReport, String requireData,String errorMessage) {
		if(actualReport.equals(expectedReport)) {
			if(requireData !=null && requireData.equals("-2")){
				addErrorMessage(errorMessage);
			}
		}
	}
	
	private void addErrorMessage(String errorMessage) {
		errorMessages += errorMessage;
	}

	private void showError(ServletRequest request, ServletResponse response)
			throws IOException, ServletException {
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse
				.sendRedirect("/mifos/run?__report="
						+ request.getParameter("__report") + "&reportName="
						+ request.getParameter("reportName") + "&userId="
						+ request.getParameter("userId") + "&__format="
						+ request.getParameter("__format") + "&message="
						+ errorMessages);
	}

	public void init(FilterConfig arg0) throws ServletException {
	}

	public void destroy() {
	}


}
