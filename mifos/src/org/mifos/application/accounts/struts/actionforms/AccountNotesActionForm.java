/**

 * AccountNotesActionForm.java    version: 1.0



 * Copyright © 2005-2006 Grameen Foundation USA

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
package org.mifos.application.accounts.struts.actionforms;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.struts.actionforms.MifosSearchActionForm;
import org.mifos.framework.util.helpers.Constants;

import org.mifos.application.accounts.util.helpers.AccountConstants;

/**
 * This class denotes the form bean for the Account Note.
 * It consists of the fields for which the user inputs values
 * @author navitas
 */
public class AccountNotesActionForm extends MifosSearchActionForm{
	
	/**Denotes the account id to which note is associated*/
	private String accountId;
	
	/**Denotes the type of account*/
	private String accountTypeId;

	/**Denotes the name of the account*/
	private String accountName;

	/**Denotes the comment entered*/
	private String comment;
	
	private String globalAccountNum;
	/**Denotes the property for btn */
	private String btn;
	
	/**Denotes the cancelBtn property*/
	private String cancelBtn;
	
	/**
 	 * Return the value of the cancelBtn attribute.
 	 * @return String
 	 */
	public String getCancelBtn() {
		return cancelBtn;
	}
	
	/**
  	 * Sets the value of canclBtn
 	 * @param cancelBtn
 	 */
	public void setCancelBtn(String cancelBtn) {
		this.cancelBtn = cancelBtn;
	}

	/**
  	 * Return the value of the comment attribute.
 	 * @return String
 	 */
	public String getComment() {
		return comment;
	}
	
	/**
     * Sets the value of comment
     * @param comment
     */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
     * Return the value of the accountId attribute.
     * @return String
     */
	public String getAccountId() {
		return accountId;
	}

	/**
     * Sets the value of accountId
     * @param accountId
     */
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
	/**
     * Return the value of the btn attribute.
     * @return String
     */
	public String getBtn() {
		return btn;
	}

	/**
     * Sets the value of the btn attribute.
     * @param btn
     */
	public void setBtn(String btn) {
		this.btn = btn;
	}
	
	/**
     * Return the value of the accountTypeId attribute.
     * @return String
     */
	public String getAccountTypeId() {
		return accountTypeId;
	}

	/**
     * Sets the value of the accountTypeId attribute.
     * @param accountTypeId
     */
	public void setAccountTypeId(String accountTypeId) {
		this.accountTypeId= accountTypeId;
	}
	
	/**
     * Return the value of the accountName attribute.
     * @return String
     */
	public String getAccountName() {
		return accountName;
	}

	/**
     * Sets the value of the accountTypeId attribute.
     * @param accountTypeId
     */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
	public String getGlobalAccountNum() {
		return globalAccountNum;
	}

	public void setGlobalAccountNum(String globalAccountNum) {
		this.globalAccountNum = globalAccountNum;
	}
	
	/**
	 * This method is used in addition to validation framework to do input data validations before proceeding.  
	 * @param mapping
	 * @param request
	 * @return ActionErrors
	 * @throws ApplicationException
	 */
	public ActionErrors customValidate(ActionMapping mapping, HttpServletRequest request) throws ApplicationException {
		String methodCalled= request.getParameter("method");
		if(null !=methodCalled) {
			if(AccountConstants.METHOD_CANCEL.equals(methodCalled) || 
			  (AccountConstants.METHOD_SEARCH_NEXT).equals(methodCalled)||
			  (AccountConstants.METHOD_SEARCH).equals(methodCalled)||
			  (AccountConstants.METHOD_GET).equals(methodCalled)||
			  (AccountConstants.METHOD_SEARCH_PREV).equals(methodCalled)||	 
			   AccountConstants.METHOD_LOAD.equals(methodCalled)){
					request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
			}
	}
		return null;	
	}
	
}
