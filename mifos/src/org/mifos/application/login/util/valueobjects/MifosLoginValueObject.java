/**

 * MifosLoginValueObject.java   version: 1.0



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

package org.mifos.application.login.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * The class is the ValueObject for the Login.
 */
public class MifosLoginValueObject extends ValueObject {

	/**
	 * Serial Version Id for Serialization
	 */
	private static final long serialVersionUID = 185643058658765934L;

	// ----------------------instance variables-----------------------
	/**
	 * User Name entered by the user for login
	 */
	private String userName;
	
	/**
	 * password entered by the user for login
	 */
	private String password;
	
	/**
	 * old password entered by the user for changing password
	 */
	private String oldPassword;
	
	/**
	 * New password entered by the user for changing password
	 */
	private String newPassword;
	
	/**
	 * confirm password entered by the user for changing password
	 */
	private String confirmPassword;
	
	/**
	 * Id of the user
	 */
	private String userId;
	
	//----------------------public methods-----------------------
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
}
