package org.mifos.application.reports.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.application.reports.business.ReportParameterForm;
import org.mifos.application.reports.business.validator.CollectionSheetReportParamValidator;
import org.mifos.application.reports.business.validator.DetailedAgingPortfolioReportParamValidator;
import org.mifos.application.reports.business.validator.Errors;
import org.mifos.application.reports.business.validator.ReportParameterValidator;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.servlet.ModifiableParameterServletRequest;

public class BirtReportValidationAction extends HttpServlet {

	private static final String ERRORS = "reportErrors";
	private static Map<String, ReportParameterValidator<ReportParameterForm>> reportValidators = new HashMap<String, ReportParameterValidator<ReportParameterForm>>();

	static {
		reportValidators.put("report/LayoutReport.rptdesign",
				CollectionSheetReportParamValidator.INSTANCE);
		reportValidators.put("report/DetailedAgingPortfolioAtRisk.rptdesign", 
				DetailedAgingPortfolioReportParamValidator.INSTANCE);
		reportValidators.put("report/ActiveLoansByLoanOfficer.rptdesign", 
				DetailedAgingPortfolioReportParamValidator.INSTANCE);		
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		validateReportParameters(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private void validateReportParameters(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		ReportParameterValidator validator = reportValidators.get(request
				.getParameter("__report"));
		if (validator == null || request.getParameter("branchId") == null) {
			request.getRequestDispatcher("/run").forward(request, response);
			return;
		}

		ReportParameterForm form = validator.buildReportParameterForm(request);
		Errors errors = new Errors(((UserContext) request.getSession()
				.getAttribute("UserContext")).getPreferredLocale());
		validator.validate(form, errors);
		ModifiableParameterServletRequest modifiedRequest = new ModifiableParameterServletRequest(
				request);

		if (errors.hasErrors()) {
			request.setAttribute(ERRORS, errors);
			validator.removeRequestParameters(modifiedRequest, form, errors);
		}
		request.getRequestDispatcher("/run").forward(modifiedRequest, response);
	}
}
