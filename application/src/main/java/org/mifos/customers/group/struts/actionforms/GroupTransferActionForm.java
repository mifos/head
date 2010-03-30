/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.customers.group.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;

public class GroupTransferActionForm extends BaseActionForm {
    private String officeId;
    private String officeName;
    private String centerSystemId;
    private String centerName;
    private String comment;
    private String assignedLoanOfficerId;
    private Short isActive = 0;

    public Short getIsActive() {
        return isActive;
    }

    public void setIsActive(Short isActived) {
        this.isActive = isActived;
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        String method = request.getParameter(Methods.method.toString());
        if (method.equals(Methods.removeGroupMemberShip.toString())) {
            errors.add(super.validate(mapping, request));
            if (StringUtils.isBlank(getAssignedLoanOfficerId())) {
                if (isActive == Constants.YES) {
                    errors.add(GroupConstants.ASSIGNED_LOAN_OFFICER_REQUIRED, new ActionMessage(
                            GroupConstants.ASSIGNED_LOAN_OFFICER_REQUIRED));

                }

            }

        }
        if (!method.equals(Methods.validate.toString())) {
            request.setAttribute(GroupConstants.METHODCALLED, method);
        }
        return errors;
    }

    public String getAssignedLoanOfficerId() {
        return assignedLoanOfficerId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setAssignedLoanOfficerId(String assignedLoanOfficerId) {
        this.assignedLoanOfficerId = assignedLoanOfficerId;
    }

    public String getCenterSystemId() {
        return centerSystemId;
    }

    public void setCenterSystemId(String centerSystemId) {
        this.centerSystemId = centerSystemId;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
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

    public Short getOfficeIdValue() {
        return getShortValue(officeId);
    }
}
