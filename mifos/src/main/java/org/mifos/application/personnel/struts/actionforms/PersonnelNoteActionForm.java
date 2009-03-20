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
 
package org.mifos.application.personnel.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.util.helpers.StringUtils;

public class PersonnelNoteActionForm extends ValidatorActionForm {
	private String personnelId;

	private String personnelName;

	private String officeName;

	private String comment;

	private String commentDate;

	private String globalPersonnelNum;

	public String getCommentDate() {
		return commentDate;
	}

	public void setCommentDate(String commentDate) {
		this.commentDate = commentDate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	public String getPersonnelName() {
		return personnelName;
	}

	public void setPersonnelName(String personnelName) {
		this.personnelName = personnelName;
	}

	public String getGlobalPersonnelNum() {
		return globalPersonnelNum;
	}

	public void setGlobalPersonnelNum(String globalPersonnelNum) {
		this.globalPersonnelNum = globalPersonnelNum;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		String methodCalled = request.getParameter(Methods.method.toString());
		ActionErrors errors = null;
		if(null !=methodCalled) {
			if (Methods.preview.toString().equals(methodCalled)) {
				errors = handlePreviewValidations(request,errors);
			}
		}
		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", methodCalled);
		}
		return errors;
	}

	private ActionErrors handlePreviewValidations(HttpServletRequest request,ActionErrors errors) {
		if (!StringUtils.isNullAndEmptySafe(getComment())) {
			if (null == errors) {
				errors = new ActionErrors();
			}
			errors.add(PersonnelConstants.ERROR_MANDATORY_TEXT_AREA, new ActionMessage(
					PersonnelConstants.ERROR_MANDATORY_TEXT_AREA , PersonnelConstants.NOTES));
		} else if (getComment().length() > PersonnelConstants.COMMENT_LENGTH) {
			if (null == errors) {
				errors = new ActionErrors();
			}
			errors.add(PersonnelConstants.MAXIMUM_LENGTH, new ActionMessage(
					PersonnelConstants.MAXIMUM_LENGTH, PersonnelConstants.NOTES,
					PersonnelConstants.COMMENT_LENGTH));
		}
		return errors;
	}
}
