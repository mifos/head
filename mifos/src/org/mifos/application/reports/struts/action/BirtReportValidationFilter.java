package org.mifos.application.reports.struts.action;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class BirtReportValidationFilter implements Filter {

	private String errorMessage;
	private FilterConfig filterConfig;

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		errorMessage = "";
		String currentReport = request.getParameter("__report");

		HashMap<String, String> errorMessages = new HashMap<String, String>();
		errorMessages.put("branchId", "Please select a Branch Office.");
		errorMessages.put("loanOfficerId", "Please select a Loan Officer.");
		errorMessages.put("loanProductId", "Please select a Loan Procuct.");

		reportFilter(currentReport,
				"report/ActiveLoansByLoanOfficer.rptdesign", errorMessages,
				request);
		reportFilter(currentReport,
				"report/DetailedAgingPortfolioAtRisk.rptdesign", errorMessages,
				request);

		if (errorMessage.length() == 0) {
			chain.doFilter(request, response);
		}
		else {
			showError(request, response);

		}
	}

	private void reportFilter(String currentReport, String expectedReport,
			HashMap<String, String> parameters, ServletRequest request) {
		if (currentReport.equals(expectedReport)) {
			for (String parameter : parameters.keySet()) {
				if (request.getParameter(parameter) != null
						&& request.getParameter(parameter).equals("-2")) {
					addErrorMessage(parameters.get(parameter));
				}
			}
		}
	}

	private void addErrorMessage(String currentErrorMessage) {
		errorMessage += (currentErrorMessage + "~");
	}

	private void showError(ServletRequest request, ServletResponse response)
			throws IOException, ServletException {
		
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;		
		httpServletResponse.sendRedirect("run?__report="
				+ request.getParameter("__report") + "&reportName="
				+ request.getParameter("reportName") + "&userId="
				+ request.getParameter("userId") + "&__format="
				+ request.getParameter("__format") + "&mifosErrorMessage="
				+ errorMessage);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	public void destroy() {
	}


}
