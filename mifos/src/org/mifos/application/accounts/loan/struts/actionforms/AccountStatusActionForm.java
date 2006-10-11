package org.mifos.application.accounts.loan.struts.actionforms;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import org.mifos.application.util.helpers.Methods;

import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.StringUtils;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
		if (this.accountRecords.size() <= i)
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
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String method = request.getParameter("method");
		if (method.equals(Methods.searchResults.toString())) {
			if (StringUtils.isNullOrEmpty(getOfficeId()))
				addError(errors, "officeId", "errors.mandatoryselect", "Branch");
			if (StringUtils.isNullOrEmpty(getPersonnelId()))
				addError(errors, "loanOfficer", "errors.mandatoryselect",
						"Loan Officer");
		}
		if (method.equals(Methods.update.toString())) {
			if (getApplicableAccountRecords().size() == 0)
				addError(errors, "records", "errors.alleastonerecord",
						"account");
			if (StringUtils.isNullOrEmpty(getComments()))
				addError(errors, "comments", "errors.mandatory", "notes");
			else if (getComments().length() > 500)
				addError(errors, "comments", "errors.maximumlength", "note",
						"500");
		}
		if (!method.equals(Methods.validate.toString()))
			request.setAttribute("methodCalled", method);
		return errors;
	}
}
