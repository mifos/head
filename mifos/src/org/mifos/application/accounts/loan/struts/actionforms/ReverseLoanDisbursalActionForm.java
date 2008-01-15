package org.mifos.application.accounts.loan.struts.actionforms;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.StringUtils;

public class ReverseLoanDisbursalActionForm extends BaseActionForm {
	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	private String searchString;

	private String note;

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		logger.debug("reset method called");
		super.reset(mapping, request);
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		logger.debug("Inside validate method");
		String method = request.getParameter(Methods.method.toString());
		ActionErrors errors = new ActionErrors();
		if (method.equals(Methods.load.toString())) {
			checkValidationForLoad(errors, getUserContext(request));
		} else if (method.equals(Methods.preview.toString())) {
			checkValidationForPreview(errors, getUserContext(request)
					.getPreferredLocale());
		}
		if (!errors.isEmpty()) {
			request.setAttribute("methodCalled", method);
		}
		logger.debug("outside validate method");
		return errors;
	}

	private void checkValidationForLoad(ActionErrors errors, UserContext userContext) {
		if (StringUtils.isNullOrEmpty(getSearchString())) {
			addError(errors, "SearchString",
					LoanConstants.ERROR_LOAN_ACCOUNT_ID, getLabel(
							ConfigurationConstants.LOAN, userContext));
		}
	}

	private void checkValidationForPreview(ActionErrors errors,
			Locale userLocale) {
		if (StringUtils.isNullOrEmpty(getNote()))
			addError(errors, LoanConstants.NOTE, LoanConstants.MANDATORY,
					LoanConstants.NOTE);
		else if (getNote().length() > 500)
			addError(errors, LoanConstants.NOTE, LoanConstants.MAX_LENGTH,
					LoanConstants.NOTE, String
							.valueOf(LoanConstants.COMMENT_LENGTH));
	}

}
