/**

 * CustomerNoteActionForm.java    version: 1.0



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
package org.mifos.application.customer.struts.actionforms;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.struts.actionforms.MifosSearchActionForm;
import org.mifos.framework.util.helpers.Constants;

/**
 * This class denotes the form bean for the Customer Note.
 * It consists of the fields for which the user inputs values
 * @author navitas
 */
public class CustomerNoteActionForm extends MifosSearchActionForm{
	
	/**Denotes the customer id to which note is associated*/
	private String customerId;
	
	/**Denotes the comment entered*/
	private String comment;
	
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
     * Return the value of the customerId attribute.
     * @return String
     */
	public String getCustomerId() {
		return customerId;
	}

	/**
     * Sets the value of customerId
     * @param customerId
     */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
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
			if(CustomerConstants.METHOD_CANCEL.equals(methodCalled) || 
			  (CustomerConstants.METHOD_SEARCH_NEXT).equals(methodCalled)||
			  (CustomerConstants.METHOD_SEARCH).equals(methodCalled)||
			  (CustomerConstants.METHOD_GET).equals(methodCalled)||
			  (CustomerConstants.METHOD_SEARCH_PREV).equals(methodCalled)||	 
			   CustomerConstants.METHOD_LOAD.equals(methodCalled)){
					request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
			}
	}
		return null;	
	}
}
