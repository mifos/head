/**

 * EditSavingsStatusActionForm.java    version: 1.0

 

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
package org.mifos.application.accounts.savings.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.business.AccountNotesEntity;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.struts.actionforms.AccountAppActionForm;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.util.helpers.Constants;

/**
 * @author rohitr
 * 
 */
public class EditSavingsStatusActionForm extends AccountAppActionForm {

	private static final long serialVersionUID = 1456L;

	public EditSavingsStatusActionForm() {
		accountNotes = new AccountNotesEntity();
	}

	private String accountName;

	private String currentStatusId;

	private String newStatusId;

	private String flagId;

	private AccountNotesEntity accountNotes;

	private String[] selectedItems;

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

	public String[] getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(String[] selectedItems) {
		this.selectedItems = selectedItems;
	}

	public AccountNotesEntity getAccountNotes() {
		return accountNotes;
	}

	public void setAccountNotes(AccountNotesEntity accountNotes) {
		this.accountNotes = accountNotes;
	}

	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = null;
		String methodCalled = request.getParameter(MethodNameConstants.METHOD);
		if (null != methodCalled) {
			if ((MethodNameConstants.CANCEL).equals(methodCalled)
					|| (MethodNameConstants.LOAD).equals(methodCalled)
					|| (MethodNameConstants.PREVIOUS).equals(methodCalled)
					|| (MethodNameConstants.VALIDATE).equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION, Boolean
						.valueOf(true));
			} else if (MethodNameConstants.PREVIEW.equals(methodCalled)) {
				errors = handleStatusPreviewValidations(request, methodCalled,
						errors);
			} else if ((MethodNameConstants.UPDATE).equals(methodCalled)) {
				errors = handleUpdateStatus(request);
			}
		}
		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", methodCalled);
		}
		return errors;
	}
	
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
				errors.add(SavingsConstants.INCOMPLETE_CHECKLIST,new ActionMessage(SavingsConstants.INCOMPLETE_CHECKLIST));
			}
		
		}
		request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
		return errors;
	}

	private ActionErrors handleStatusPreviewValidations(
			HttpServletRequest request, String methodCalled, ActionErrors errors) {
		if (newStatusId == null) {
			errors = new ActionErrors();
			errors.add(SavingsConstants.MANDATORY, new ActionMessage(
					SavingsConstants.MANDATORY, AccountConstants.STATUS));
		} else {
			if (Short.valueOf(newStatusId).shortValue() == AccountStates.SAVINGS_ACC_CANCEL) {
				// chekck if flag ha been selected
				if (ValidateMethods.isNullOrBlank(flagId)) {
					if (null == errors) {
						errors = new ActionErrors();
					}
					errors.add(SavingsConstants.MANDATORY_SELECT,
							new ActionMessage(
									SavingsConstants.MANDATORY_SELECT,
									SavingsConstants.FLAG));
				}
			}
		}
		if (ValidateMethods.isNullOrBlank(accountNotes.getComment())) {
			if (null == errors) {
				errors = new ActionErrors();
			}
			errors.add(SavingsConstants.MANDATORY, new ActionMessage(
					SavingsConstants.MANDATORY, AccountConstants.NOTES));
		} else if (accountNotes.getComment().length() > SavingsConstants.COMMENT_LENGTH) {
			// status length is more than 500, throw an exception
			if (null == errors) {
				errors = new ActionErrors();
			}
			errors.add(SavingsConstants.MAX_LENGTH, new ActionMessage(
					SavingsConstants.MAX_LENGTH, SavingsConstants.NOTES,
					SavingsConstants.COMMENT_LENGTH));
		}
		return errors;
	}

}
