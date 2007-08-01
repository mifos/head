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

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String branchId = request.getParameter("branchId");
		if (branchId == null || !branchId.equals("-2")) {
			chain.doFilter(request, response);
		}
		else {
			showError(request, response);
		}
	}

	private void showError(ServletRequest request, ServletResponse response)
			throws IOException, ServletException {
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		String errorMessage = "Please Select the required items";
		httpServletResponse.sendRedirect("/mifos/run?__report="
				+ request.getParameter("__report") + "&reportName="
				+ request.getParameter("reportName") + "&userId="
				+ request.getParameter("userId") + "&__format="
				+ request.getParameter("__format") + "&message=" + errorMessage);
	}

	public void init(FilterConfig arg0) throws ServletException {
	}

	public void destroy() {
	}


}
