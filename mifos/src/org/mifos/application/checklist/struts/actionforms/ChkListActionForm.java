package org.mifos.application.checklist.struts.actionforms;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.checklist.util.resources.CheckListConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class ChkListActionForm extends BaseActionForm {

	private String text;

	private String checkListId;

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

	public String getCheckListId() {
		return checkListId;
	}

	public void setCheckListId(String checkListId) {
		this.checkListId = checkListId;
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
		if(StringUtils.isNullOrEmpty(string))
			return;
		while (this.detailsList.size() <= i)
			this.detailsList.add(new String());
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getValidCheckListDetails(){
		List<String> validCheckList = new ArrayList<String>();
		for(String detail : getDetailsList())
			if(!StringUtils.isNullOrEmpty(detail))
				validCheckList.add(detail);
		return validCheckList;
	}

	@Override
	public ActionErrors validate(ActionMapping mappping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String method = request.getParameter("method");
		if (method.equals(Methods.preview.toString()))
			errors = validateFields();
		if (method.equals(Methods.managePreview.toString())) {
			errors = validateFields();
			if (StringUtils.isNullOrEmpty(getChecklistStatus()))
				addError(errors, "checklistStatus",
						CheckListConstants.MANDATORY,
						CheckListConstants.CHECKLIST_STATUS);
		}
		if (!method.equals(Methods.validate.toString()))
			request.setAttribute("methodCalled", method);
		return errors;
	}

	private ActionErrors validateFields() {
		ActionErrors errors = new ActionErrors();
		if (StringUtils.isNullOrEmpty(getChecklistName()))
			addError(errors, "checklistName", CheckListConstants.MANDATORY,
					CheckListConstants.CHECKLIST_NAME);
		else if (getChecklistName().length() > 100)
			addError(errors, "checklistName", CheckListConstants.MAX_LENGTH,
					CheckListConstants.CHECKLIST_NAME, "100");
		for (String items : getDetailsList())
			if (items.length() > 250)
				addError(errors, "details", CheckListConstants.MAX_LENGTH,
						CheckListConstants.CHECKLIST_ITEMS, "250");
		if (StringUtils.isNullOrEmpty(getMasterTypeId()))
			addError(errors, "masterTypeId", CheckListConstants.MANDATORY,
					CheckListConstants.TYPE_COMBO);
		if (StringUtils.isNullOrEmpty(getStateId()))
			addError(errors, "stateId", CheckListConstants.MANDATORY,
					CheckListConstants.STATE_COMBO);
		if (getValidCheckListDetails().size() == 0)
			addError(errors, "detailsList", CheckListConstants.MANDATORY,
					CheckListConstants.DETAILS);
		return errors;
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		String method = request.getParameter("method");
		if (method.equals(Methods.preview.toString()) || method.equals(Methods.managePreview.toString())
				|| method.equals("getStates")) {
			detailsList.clear();
			detailsList = null;
			detailsList = new ArrayList<String>();
		}
		try {
			if (null != request.getParameter(Constants.CURRENTFLOWKEY))
				request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter("currentFlowKey"));
			SessionUtils.setAttribute(CheckListConstants.DETAILS, detailsList, request);
		} catch (PageExpiredException pee) {
		}
	}
}
