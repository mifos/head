/**

 * FundActionForm  version: 1.0



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

package org.mifos.application.fund.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.fund.util.helpers.FundConstants;
import org.mifos.application.master.util.valueobjects.GLCode;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.struts.actionforms.MifosActionForm;
import org.mifos.framework.util.helpers.Constants;

/**
 * FundActionForm class contains all getter and setter methods for
 * Fund Module
 * 
 * @author imtiyazmb
 * 
 */

public class FundActionForm extends MifosActionForm {	
	
	/**
	 * @return Returns the fundId.
	 */
	public String getFundId() {
		return fundId;
	}

	/**
	 * @param fundId The fundId to set.
	 */
	public void setFundId(String fundId) {
		this.fundId = fundId;
	}

	public FundActionForm(){
		glCode=new GLCode();
	}
	private static final long serialVersionUID = -5462456510477953927L;
	/**
	 * The value of fundId
	 */
	private String fundId;
	/**
	 * The value of fundName
	 */
	private String fundName;
	/**
	 * The value of glCode
	 */
	private GLCode glCode;
	
	
	/**
	 * @return Returns the glCode.
	 */
	public GLCode getGlCode() {
		return glCode;
	}

	/**
	 * @param glCode The glCode to set.
	 */
	public void setGlCode(GLCode glCode) {
		this.glCode = glCode;
	}

	/**
	 * @return Returns the fundName.
	 */
	public String getFundName() {
		return fundName;
	}

	/**
	 * @param fundName The fundName to set.
	 */
	public void setFundName(String fundName) {
		this.fundName = fundName;
	}
	
	/**
	 * This method is the method which would be called for any validation in the application.
	 * This method checks the method of the action called and based on that it will call validate of
	 * the super class and the customValidate of the sub class. If the call is for validate method, it
	 * will return empty ActionErrors.  
	 * Here validation is done for mandatory requirement for the fund name and gl code during a create and editing of details
	 * @param mapping
	 * @param request
	 * @return ActionErrors
	 * 
	 * @see org.apache.struts.validator.ValidatorForm#validate(org.apache.struts.action.ActionMapping,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	public final ActionErrors customValidate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors;
		String methodCalled = request.getParameter("method");		
		if (null != methodCalled) 
		{		
			System.out.println("*************************Inside Fund Action Form Custom Validate" +methodCalled);
			MifosLogManager.getLogger(LoggerConstants.FUNDLOGGER).info("Inside Fund Action Form Custom Validate" +methodCalled);
			if (CustomerConstants.METHOD_LOAD.equals(methodCalled) || 
				FundConstants.METHOD_GET_ALL_FUNDS.equals(methodCalled) || 
				CustomerConstants.METHOD_GET.equals(methodCalled) || 
				CustomerConstants.METHOD_MANAGE.equals(methodCalled) || 
				CustomerConstants.METHOD_CREATE.equals(methodCalled)||
				CustomerConstants.METHOD_UPDATE.equals(methodCalled)||
				CustomerConstants.METHOD_PREVIOUS.equals(methodCalled))
			{
				
				request.setAttribute(Constants.SKIPVALIDATION, Boolean.valueOf(true));
				System.out.println("*************************Skipping validation");
			}			
			
		}	
		return null;
	}
	/**
	 * The reset method is called before setting the values into the actionform.In this method,
	 * the values are cleared and the default values are set.
	 *  
	 */
	
	/*public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);			
		this.fundName=null;
		this.glCode=null;	
	}*/
}
