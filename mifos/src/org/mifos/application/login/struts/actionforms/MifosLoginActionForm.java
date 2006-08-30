/**

 * MifosLoginActionForm.java   version: 1.0



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */
package org.mifos.application.login.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.struts.actionforms.MifosActionForm;
import org.mifos.framework.util.helpers.Constants;

/**
 * This class is the ActionForm associated with the Login Action.
 */
public class MifosLoginActionForm extends MifosActionForm {
	
	// ----------------------constructors-----------------------
	
	/**
	 * Default constructor
	 */
	public MifosLoginActionForm() {
		super();
	}

	/**
	 * Serial Version Id for Serialization
	 */
	private static final long serialVersionUID = 1557589324750876423L;

	// ----------------------instance variables-----------------------
	/**
	 * User Name entered by the user
	 */
	private String userName;
	
	/**
	 * password entered by the user
	 */
	private String password;
	
	/**
	 * old password entered by the user
	 */
	private String oldPassword;
	
	/**
	 * New password entered by the user
	 */
	private String newPassword;

	/**
	 * confirm password entered by the user
	 */
	private String confirmPassword;
	
	/**
	 * Id of the user
	 */
	public String userId;
	
	//----------------------public methods-----------------------
	
	/**
	 * The reset method is used to reset all the values to null.
	 * @see org.apache.struts.validator.ValidatorForm#reset(org.apache.struts.action.ActionMapping,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		MifosLogManager.getLogger(LoggerConstants.LOGINLOGGER).info(
				"In Login Reset");
		userName = null;
		password = null;
		oldPassword=null;
		newPassword=null;
		confirmPassword=null;
		userId=null;
		super.reset(mapping, request);
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	
	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
	/**
	 * This method is to skip validation for load method
	 * @param mapping
	 * @param request
	 * @return
	 */
	public ActionErrors customValidate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors=new ActionErrors();
		String methodCalled= request.getParameter("method");
		if(null !=methodCalled) {
			MifosLogManager.getLogger(LoggerConstants.LOGINLOGGER).info("In Savings Product Action Form Custom Validate");
			if("load".equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
			}
		}
		return errors;
	}

}
