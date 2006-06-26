/**

 * EditLoanStatusActionForm.java    version: 1.0



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
package org.mifos.application.accounts.loan.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.valueobjects.AccountNotes;
import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.struts.actionforms.MifosActionForm;
import org.mifos.framework.util.helpers.Constants;

public class EditLoanStatusActionForm extends MifosActionForm{
	private String accountId;
	private String globalAccountNum;
	private String accountName;	
	private String currentStatusId;
	private String versionNo;
	private String newStatusId;
	private String flagId;
	
	/**Denotes the button value for jsps*/
	private String btn;
	private AccountNotes notes;
	
	/**Denotes the list of selected checkboxes on the status preview page*/
	private String[] selectedItems;
	
	public EditLoanStatusActionForm(){
		notes = new AccountNotes();
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getCurrentStatusId() {
		return currentStatusId;
	}
	public void setCurrentStatusId(String currentStatusId) {
		this.currentStatusId = currentStatusId;
	}
	public String getGlobalAccountNum() {
		return globalAccountNum;
	}
	public void setGlobalAccountNum(String globalAccountNum) {
		this.globalAccountNum = globalAccountNum;
	}
	public String getFlagId() {
		return flagId;
	}
	public void setFlagId(String flagId) {
		this.flagId = flagId;
	}
	public String getNewStatusId() {
		return newStatusId;
	}
	public void setNewStatusId(String newStatusId) {
		this.newStatusId = newStatusId;
	}
	public String getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}
	public AccountNotes getNotes() {
		return notes;
	}
	public void setNotes(AccountNotes notes) {
		this.notes = notes;
	}
	
	/**
	 * Return the value of the selectedItems attribute.	
	 * @return Returns the selectedItems.
	 */
	public String[] getSelectedItems() {
		return selectedItems;
	}

	/**
	 * Sets the value of the selectedItems attribute.
	 * @param selectedItems The selectedItems to set.
	 */
	public void setSelectedItems(String[] selectedItems) {
		this.selectedItems = selectedItems;
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
	 * This method is used in addition to validation framework to do input data validations before proceeding.  
	 * @param mapping
	 * @param request
	 * @return ActionErrors
	 * @throws ApplicationException
	 */
	public ActionErrors customValidate(ActionMapping mapping, HttpServletRequest request) throws ApplicationException {
		String methodCalled= request.getParameter("method");
		
		if(null !=methodCalled) {
			if( (AccountConstants.METHOD_CANCEL).equals(methodCalled) || 
			    (AccountConstants.METHOD_LOAD).equals(methodCalled) || 
			    (AccountConstants.METHOD_SEARCH).equals(methodCalled)||
			    (AccountConstants.METHOD_PREVIOUS).equals(methodCalled)){
					request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
			}else if(CustomerConstants.METHOD_PREVIEW.equals(methodCalled)){
					return handleStatusPreviewValidations();
			}else if((AccountConstants.METHOD_UPDATE).equals(methodCalled)){
				return handleUpdateStatus(request);
			}
		}
		return null;	
	}
	
	/**
	 * This method is helper method to do validations  that checklist has been ticked or not
	 * request instance of HttpServletRequest
	 * @return ActionErrors
	 */
	private ActionErrors handleUpdateStatus(HttpServletRequest request){
		ActionErrors errors = null;
		Object obj=request.getParameter("chklistSize");
		if(request.getParameter("selectedItems")==null){
			selectedItems=null;
		}
		if(obj!=null){
			int totalItems = new Integer(obj.toString()).intValue();
			if((totalItems>0 && selectedItems==null) ||(selectedItems!=null && totalItems!=selectedItems.length)){
				errors = new ActionErrors();
				errors.add(LoanConstants.INCOMPLETE_CHECKLIST,new ActionMessage(LoanConstants.INCOMPLETE_CHECKLIST));
			}
		
		}
		request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
		return errors;
	}
	
	/**
	 * This method is helper method to do data validations before updating the status   
	 * @return ActionErrors
	 */
	private ActionErrors handleStatusPreviewValidations(){
		ActionErrors errors = null;
		if(newStatusId==null){
			errors = new ActionErrors();
			errors.add(LoanConstants.MANDATORY_SELECT,new ActionMessage(LoanConstants.MANDATORY_SELECT,AccountConstants.STATUS));
		}else {			
			if(Short.valueOf(newStatusId).shortValue()==LoanConstants.CLOSED || Short.valueOf(newStatusId).shortValue()==LoanConstants.CANCELLED){
				//chekck if flag ha been selected
				if(ValidateMethods.isNullOrBlank(flagId)){
					if(null==errors){
						errors = new ActionErrors();
					}
					errors.add(LoanConstants.MANDATORY_SELECT,new ActionMessage(LoanConstants.MANDATORY_SELECT,LoanConstants.FLAG));
				}
			}
		}
		if(ValidateMethods.isNullOrBlank(notes.getComment())){
			if(null==errors){
				errors = new ActionErrors();
			}
			errors.add(LoanConstants.MANDATORY_TEXTBOX,new ActionMessage(LoanConstants.MANDATORY_TEXTBOX,AccountConstants.NOTES));
		}else if(notes.getComment().length()>LoanConstants.COMMENT_LENGTH){
			//status length is more than 500, throw an exception
			if(null==errors){
				errors = new ActionErrors();
			}
			errors.add(LoanConstants.MAX_LENGTH,new ActionMessage(LoanConstants.MAX_LENGTH,GroupConstants.NOTES,LoanConstants.COMMENT_LENGTH));
		}
		return errors;
	}
}
