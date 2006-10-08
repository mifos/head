package org.mifos.application.checklist.struts.actionforms;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.checklist.util.resources.CheckListConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.StringUtils;

public class ChkListActionForm extends BaseActionForm {

	private boolean isCustomer;

	private String masterTypeId;

	private String masterTypeName;

	private String stateId;

	private String stateName;

	private String checklistName;

	private List<String> detailsList;

	private String checklistStatus;
	
	private String type;

	public ChkListActionForm() {
		detailsList = new ArrayList<String>();
	}

	public List<String> getDetailsList() {
		return detailsList;
	}

	public void setDetailsList(List<String> detailsList) {
		this.detailsList = detailsList;
	}

	public String getDetailsList(int i) {
		while (i >= detailsList.size()) {
			detailsList.add("");
		}
		return detailsList.get(i).toString();
	}

	public void setDetailsList(int i, String string) {
		if (detailsList.size() - 1 < i) {
			getDetailsList(i);
		}
		this.detailsList.set(i, string);
	}

	public String getChecklistStatus() {
		return checklistStatus;
	}

	public void setChecklistStatus(String checklistStatus) {
		this.checklistStatus = checklistStatus;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getChecklistName() {
		return checklistName;
	}

	public void setChecklistName(String checklistName) {
		this.checklistName = checklistName;
	}

	public String getStateId() {
		return stateId;
	}

	public void setStateId(String stateId) {
		this.stateId = stateId;
	}

	public boolean getIsCustomer() {
		return isCustomer;
	}

	public void setIsCustomer(boolean isCustomer) {
		this.isCustomer = isCustomer;
	}

	public String getMasterTypeId() {
		return masterTypeId;
	}

	public void setMasterTypeId(String masterTypeId) {
		this.masterTypeId = masterTypeId;
	}

	public String getMasterTypeName() {
		return masterTypeName;
	}

	public void setMasterTypeName(String masterTypeName) {
		this.masterTypeName = masterTypeName;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	@Override
	public ActionErrors validate(ActionMapping mappping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String method = request.getParameter("method");
		if (method.equals(Methods.preview.toString())) {
			if (StringUtils.isNullOrEmpty(getChecklistName()))
				addError(errors, "checklistName", CheckListConstants.MANDATORY,
						CheckListConstants.CHECKLIST_NAME);
			if (getChecklistName().length() > 200)
				addError(errors, "checklistName",
						CheckListConstants.MAX_LENGTH,
						CheckListConstants.CHECKLIST_NAME, "500");
			if (StringUtils.isNullOrEmpty(getMasterTypeId()))
				addError(errors, "masterTypeId", CheckListConstants.MANDATORY,
						CheckListConstants.TYPE_COMBO);
			if (StringUtils.isNullOrEmpty(getStateId()))
				addError(errors, "masterTypeId", CheckListConstants.MANDATORY,
						CheckListConstants.STATE_COMBO);
			if (getDetailsList().size() == 0)
				addError(errors, "detailsList", CheckListConstants.MANDATORY,
						CheckListConstants.DETAILS);
		}
		if (!method.equals(Methods.validate.toString())) {
			request.setAttribute("methodCalled", method);
		}
		return errors;
	}

}
