/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.application.checklist.struts.actionforms;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.checklist.util.resources.CheckListConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FilePaths;
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
		UserContext userContext = getUserContext(request);
		Locale locale = userContext.getPreferredLocale();
		ResourceBundle resources = ResourceBundle.getBundle(FilePaths.CHECKLIST_RESOURCE, locale);
		String checklistStatus = resources.getString(CheckListConstants.CHECKLIST_STATUS_RESOURCE);
		ActionErrors errors = new ActionErrors();
		String method = request.getParameter("method");
		if (method.equals(Methods.preview.toString()))
			errors = validateFields(locale);
		if (method.equals(Methods.managePreview.toString())) {
			errors = validateFields(locale);
			if (StringUtils.isNullOrEmpty(getChecklistStatus()))
				addError(errors, "checklistStatus",
						CheckListConstants.MANDATORY,
						checklistStatus);
		}
		if (!method.equals(Methods.validate.toString()))
			request.setAttribute("methodCalled", method);
		return errors;
	}

	private ActionErrors validateFields(Locale locale) {
		
		ResourceBundle resources = ResourceBundle.getBundle(FilePaths.CHECKLIST_RESOURCE, locale);
		String checklistName = resources.getString(CheckListConstants.CHECKLIST_NAME_RESOURCE);
		String itemsStr = resources.getString(CheckListConstants.CHECKLIST_ITEMS_RESOURCE);
		String type = resources.getString(CheckListConstants.CHECKLIST_TYPE_RESOURCE);
		String state = resources.getString(CheckListConstants.CHECKLIST_DISPLAY_STATUS_RESOURCE);
		String details = resources.getString(CheckListConstants.CHECKLIST_DETAIL_RESOURCE);
		ActionErrors errors = new ActionErrors();
		if (StringUtils.isNullOrEmpty(getChecklistName()))
			addError(errors, "checklistName", CheckListConstants.MANDATORY,
					checklistName);
		else if (getChecklistName().length() > 100)
			addError(errors, "checklistName", CheckListConstants.MAX_LENGTH,
					checklistName, "100");
		for (String items : getDetailsList())
			if (items.length() > 250)
				addError(errors, "details", CheckListConstants.MAX_LENGTH,
						itemsStr, "250");
		if (StringUtils.isNullOrEmpty(getMasterTypeId()))
			addError(errors, "masterTypeId", CheckListConstants.MANDATORY,
					type);
		if (StringUtils.isNullOrEmpty(getStateId()))
			addError(errors, "stateId", CheckListConstants.MANDATORY,
					state);
		if (getValidCheckListDetails().size() == 0)
			addError(errors, "detailsList", CheckListConstants.MANDATORY,
					details);
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
			SessionUtils.setCollectionAttribute(CheckListConstants.DETAILS, detailsList, request);
		} catch (PageExpiredException pee) {
		}
	}
}
