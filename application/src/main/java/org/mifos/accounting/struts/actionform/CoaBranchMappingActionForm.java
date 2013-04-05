package org.mifos.accounting.struts.actionform;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounting.util.helpers.SimpleAccountingConstants;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.ConversionError;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.DoubleConversionResult;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoaBranchMappingActionForm  extends BaseActionForm {
	private static final Logger logger = LoggerFactory
			.getLogger(CoaBranchMappingActionForm.class);

private String branchoffice;
 private String loginName;
private String[] coaBranchMainHeadglcode;

public String[] getCoaBranchMainHeadglcode() {
	return coaBranchMainHeadglcode;
}

public String getLoginName() {
	return loginName;
}

public void setLoginName(String loginName) {
	this.loginName = loginName;
}

public void setCoaBranchMainHeadglcode(String[] coaBranchMainHeadglcode) {
	this.coaBranchMainHeadglcode = coaBranchMainHeadglcode;
}
public CoaBranchMappingActionForm() {
	//this.coaBranchMainHeadglcode = new String[10];
}
public String getBranchoffice() {
	return branchoffice;
}

public void setBranchoffice(String branchoffice) {
	this.branchoffice = branchoffice;
}
@Override
public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {
	logger.debug("CoaBranchMappingActionForm.validate");
	// request.setAttribute(Constants.CURRENTFLOWKEY,
	// request.getParameter(Constants.CURRENTFLOWKEY));
	ActionErrors errors = new ActionErrors();

	if (request.getParameter(SimpleAccountingConstants.METHOD)
			.equalsIgnoreCase(SimpleAccountingConstants.SUBMIT)) {
		return mandatoryCheck(getUserContext(request));
	}
	return errors;
}
@Override
public void reset(ActionMapping mapping, HttpServletRequest request) {
	logger.debug("reset method called");
	if (request.getParameter(SimpleAccountingConstants.METHOD).equals(
			"load")) {
		this.branchoffice = null;
		this.coaBranchMainHeadglcode = null;
	}
	}

private ActionErrors mandatoryCheck(UserContext userContext) {
	Locale locale = userContext.getPreferredLocale();
	ResourceBundle resources = ResourceBundle.getBundle(
			FilePaths.SIMPLE_ACCOUNTING_RESOURCE, locale);

	String branch_office = resources
			.getString(SimpleAccountingConstants.BRANCHOFFICE);
	String coa_name = resources
			.getString(SimpleAccountingConstants.COANAME);

	ActionErrors errors = new ActionErrors();




	if (getBranchoffice() == null || "".equals(getBranchoffice())) {
		errors.add(SimpleAccountingConstants.MANDATORYENTER,
				new ActionMessage(SimpleAccountingConstants.MANDATORYENTER,
						branch_office));
	}
	if (getCoaBranchMainHeadglcode() == null || "".equals(getCoaBranchMainHeadglcode())) {
		errors.add(SimpleAccountingConstants.MANDATORYENTER,
				new ActionMessage(SimpleAccountingConstants.MANDATORYENTER,
							coa_name));
	}
	return errors;
}

}
