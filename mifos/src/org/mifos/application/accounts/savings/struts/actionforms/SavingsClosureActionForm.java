/**
 
 * SavingsClosureActionForm.java    version: xxx
 
 
 
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

package org.mifos.application.accounts.savings.struts.actionforms;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;

public class SavingsClosureActionForm extends ValidatorActionForm{
	private String receiptId;
	private String receiptDate;
	private String customerId;
	private String paymentTypeId;
	private String notes;
	
	public SavingsClosureActionForm(){	}
	
	public String getCustomerId() {
		return customerId;
	}
	
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	public String getNotes() {
		return notes;
	}
	
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public String getPaymentTypeId() {
		return paymentTypeId;
	}
	
	public void setPaymentTypeId(String paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}
	
	public String getReceiptDate() {
		return receiptDate;
	}
	
	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}
	
	public String getReceiptId() {
		return receiptId;
	}
	
	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}
	
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		String method = request.getParameter("method");
		ActionErrors errors = null;
		
		if(method!=null && method.equals("load")||
						method.equals("previous")||
						method.equals("validate")||
						method.equals("close")||
						method.equals("cancel")){
		}else{
			UserContext userContext= (UserContext)SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY,request.getSession());
			ResourceBundle resources = ResourceBundle.getBundle ("org.mifos.application.accounts.savings.util.resources.SavingsUIResources", userContext.getPereferedLocale());
			errors = new ActionErrors();
			if( this.paymentTypeId==null||this.paymentTypeId.equals("")){
				errors.add(AccountConstants.ERROR_MANDATORY,new ActionMessage(AccountConstants.ERROR_MANDATORY,resources.getString("Savings.paymentType")));
			}
			
			if(this.receiptDate!=null && !this.receiptDate.equals("")){
				ActionErrors dateError = validateDate(this.receiptDate,resources.getString("Savings.receiptDate"),userContext);
				if( dateError!=null &&!dateError.isEmpty())
					errors.add(dateError);
			}

			if( this.customerId==null||this.customerId.equals("")){
				errors.add(AccountConstants.ERROR_MANDATORY,new ActionMessage(AccountConstants.ERROR_MANDATORY, resources.getString("Savings.ClientName")));
			}
			
			if( this.notes==null||this.notes.equals("")){
				errors.add(AccountConstants.ERROR_MANDATORY,new ActionMessage(AccountConstants.ERROR_MANDATORY, resources.getString("Savings.notes")));
			}
		}
		
		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", method);
		}
	
		return errors;
	}
	
	private ActionErrors validateDate(String date ,String fieldName,UserContext userContext){
		ActionErrors errors =null;
		java.sql.Date sqlDate=null;
		if( date!=null&&!date.equals("")){
			sqlDate=DateHelper.getLocaleDate(userContext.getPereferedLocale(),date);
			Calendar currentCalendar = new GregorianCalendar();
			int year=currentCalendar.get(Calendar.YEAR);
			int month=currentCalendar.get(Calendar.MONTH);
			int day=currentCalendar.get(Calendar.DAY_OF_MONTH);
			currentCalendar = new GregorianCalendar(year,month,day);
			java.sql.Date currentDate=new java.sql.Date(currentCalendar.getTimeInMillis());
			if(currentDate.compareTo(sqlDate) < 0 ) {
				errors = new ActionErrors();
				errors.add(AccountConstants.ERROR_FUTUREDATE,new ActionMessage(AccountConstants.ERROR_FUTUREDATE,fieldName));
			}
		}
		else
		{	errors = new ActionErrors();
			errors.add(AccountConstants.ERROR_MANDATORY,new ActionMessage(AccountConstants.ERROR_MANDATORY,fieldName));
		}
		return errors;
	}
}
