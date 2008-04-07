package org.mifos.application.accounts.loan.struts.actionforms;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.StringUtils;
import java.util.ResourceBundle;

public class AccountStatusActionForm extends BaseActionForm {
	private String personnelId;

	private String officeId;

	private String officeName;

	private String loadOfficer;

	private String type;

	private String currentStatus;

	private String newStatus;

	private String comments;

	private List<String> accountRecords;

	public AccountStatusActionForm() {
		accountRecords = new ArrayList<String>();
	}

	public List<String> getAccountRecords() {
		return accountRecords;
	}

	public String getAccountRecords(int i) {
		while (i >= accountRecords.size())
			accountRecords.add("");
		return accountRecords.get(i).toString();
	}

	public void setAccountRecords(int i, String string) {
		while (this.accountRecords.size() <= i)
			this.accountRecords.add(new String());
		this.accountRecords.set(i, string);
	}

	public void setAccountRecords(List<String> accountRecords) {
		this.accountRecords = accountRecords;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}

	public String getLoadOfficer() {
		return loadOfficer;
	}

	public void setLoadOfficer(String loadOfficer) {
		this.loadOfficer = loadOfficer;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getPersonnelId() {
		return personnelId;
	}

	public void setPersonnelId(String personnelId) {
		this.personnelId = personnelId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(String newStatus) {
		this.newStatus = newStatus;
	}

	private List<String> getApplicableAccountRecords() {
		List<String> applicableRecords = new ArrayList<String>();
		for (String accountId : getAccountRecords())
			if (accountId != "")
				applicableRecords.add(accountId);
		return applicableRecords;
	}

	@Override
	// TODO: use localized strings for error messages rather than hardcoded
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		Locale locale = getUserContext(request).getPreferredLocale();
		ResourceBundle resources = ResourceBundle.getBundle (FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE, 
				locale);
		ActionErrors errors = new ActionErrors();
		String method = request.getParameter("method");
		if (method.equals(Methods.searchResults.toString())) {
			String branch = resources.getString("loan.branch");
			String loanOfficer = resources.getString("loan.loanOfficer");
			String currentStatus = resources.getString("loan.currentStatus");
			if (StringUtils.isNullOrEmpty(getOfficeId()))
				addError(errors, "officeId", "errors.mandatoryselect", branch);
			if (StringUtils.isNullOrEmpty(getPersonnelId()))
				addError(errors, "loanOfficer", "errors.mandatoryselect",
						loanOfficer);
			if (StringUtils.isNullOrEmpty(getCurrentStatus()))
				addError(errors, "currentStatus", "errors.mandatoryselect",
						currentStatus);
		}
		if (method.equals(Methods.update.toString())) {
			String account = resources.getString("loan.account");
			String notes = resources.getString("loan.notes");
			String note = resources.getString("loan.note");
			if (getApplicableAccountRecords().size() == 0)
				addError(errors, "records", "errors.alleastonerecord",
						account);
			if (StringUtils.isNullOrEmpty(getComments()))
				addError(errors, "comments", "errors.mandatory", notes);
			else if (getComments().length() > 500)
				addError(errors, "comments", "errors.maximumlength", note,
						"500");
		}
		if (!method.equals(Methods.validate.toString()))
			request.setAttribute("methodCalled", method);
		return errors;
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		String method = request.getParameter("method");
		if (method.equals(Methods.update.toString())) {
			accountRecords.clear();
			accountRecords = null;
			accountRecords = new ArrayList<String>();
		}
	}
}
