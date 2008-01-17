package org.mifos.application.reports.business;

import static org.mifos.application.reports.util.helpers.CollectionSheetReportConstants.REPORT_PARAMS_ARRAY;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.reports.business.validator.Errors;
import org.mifos.framework.servlet.ModifiableParameterServletRequest;
import org.mifos.framework.util.helpers.NumberUtils;
import org.mifos.framework.util.helpers.ServletUtils;

public class DetailedAgingPortfolioReportParameters implements ReportParameterForm{

	public static final String BRANCH_ID_PARAM = "branchId";
	public static final String LOAN_OFFICER_ID_PARAM = "loanOfficerId";
	public static final String LOAN_PRODUCT_ID = "loanProductId";
	
	public static final String BRANCH_ID_INVALID_MSG = "branchId.invalid";
	public static final String LOAN_OFFICER_ID_INVALID_MSG = "loanOfficerId.invalid";
	public static final String LOAN_PRODUCT_ID_INVALID_MSG = "loanProductId.invalid";

	private final String branchId;
	private final String loanOfficerId;
	private final String productId;

	public static final String VALID_ID = "1";

	public static final String INVALID_ID = "-2";

	DetailedAgingPortfolioReportParameters(String branchId, String loanOfficerId,String productId) {
		this.branchId = branchId;
		this.loanOfficerId = loanOfficerId;
		this.productId = productId;
	}

	public void validate(Errors errors) {
	    addErrorIfInvalid(errors, branchId, BRANCH_ID_PARAM, BRANCH_ID_INVALID_MSG);	
	    addErrorIfInvalid(errors, loanOfficerId, LOAN_OFFICER_ID_PARAM, LOAN_OFFICER_ID_INVALID_MSG);
	    addErrorIfInvalid(errors, productId, LOAN_PRODUCT_ID, LOAN_PRODUCT_ID_INVALID_MSG);
	}

	private void addErrorIfInvalid(Errors errors, String parameter,
			String fieldName, String errorCode) {
		if (!NumberUtils.isDigits(parameter)
				|| Integer.valueOf(INVALID_ID).equals(Integer.valueOf(parameter))) {
			errors.rejectValue(fieldName, errorCode);
		}
	}

	public static DetailedAgingPortfolioReportParameters build(HttpServletRequest request) {
		return new DetailedAgingPortfolioReportParameters(
				ServletUtils.getParameter(request, BRANCH_ID_PARAM),
				ServletUtils.getParameter(request, LOAN_OFFICER_ID_PARAM),
				ServletUtils.getParameter(request, LOAN_PRODUCT_ID)
				);
	}

	public void removeRequestParameters(ModifiableParameterServletRequest modifiedRequest, Errors errors) {
		for (String reportParam : REPORT_PARAMS_ARRAY) {
			if (errors.getFieldError(reportParam) != null)
				modifiedRequest.removeParameter(reportParam);
		}
	}
}
