package org.mifos.application.login.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.ExceptionConstants;

public class LoginActionForm extends BaseActionForm {

	private String userName;
	private String input;
	private String password;
	private String oldPassword;
	private String newPassword;
	private String confirmPassword;

	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String method = request.getParameter(Methods.method.toString());
		if (method.equals(Methods.login.toString()) || method.equals(Methods.updatePassword.toString())) {
			errors.add(super.validate(mapping, request));
			if (method.equals(Methods.updatePassword.toString())) {
				if (newPassword.equals(oldPassword)){
					errors.add(LoginConstants.SAME_OLD_AND_NEW_PASSWORD, new ActionMessage(
							LoginConstants.SAME_OLD_AND_NEW_PASSWORD));
				}
			}
		}
		if (method != null && !method.equals(Methods.validate.toString()))
			request.setAttribute(LoginConstants.METHODCALLED,method);
		return errors;
	}
}
