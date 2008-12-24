package org.mifos.application.customer.group.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.StringUtils;

public class GroupTransferActionForm extends BaseActionForm{
	private String officeId;
	private String officeName;
	private String centerSystemId;
	private String centerName;
	private String comment;
	private String assignedLoanOfficerId; 
	private Short isActive=0;
	
	public Short getIsActive() {
		return isActive;
	}
	public void setIsActive(Short isActived) {
		this.isActive = isActived;
	}
	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String method = request.getParameter(Methods.method.toString());
		if (method.equals(Methods.removeGroupMemberShip.toString())) {
			errors.add(super.validate(mapping, request));
					if (StringUtils.isNullOrEmpty(getAssignedLoanOfficerId())){
							if(isActive==Constants.YES){
								errors.add(GroupConstants.ASSIGNED_LOAN_OFFICER_REQUIRED, new ActionMessage(
										GroupConstants.ASSIGNED_LOAN_OFFICER_REQUIRED));

							}
					
				}
			
		}
		if (method != null && !method.equals(Methods.validate.toString()))
			request.setAttribute(GroupConstants.METHODCALLED,method);
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
