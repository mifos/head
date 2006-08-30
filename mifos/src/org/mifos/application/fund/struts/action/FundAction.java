/**

 * FundAction.java    version: 1.0

 

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

package org.mifos.application.fund.struts.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.customer.center.struts.actionforms.CenterActionForm;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.fund.struts.actionforms.FundActionForm;
import org.mifos.application.fund.util.helpers.FundConstants;
import org.mifos.application.fund.util.valueobjects.Fund;
import org.mifos.application.master.util.valueobjects.GLCode;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.struts.action.MifosBaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.valueobjects.Context;

/**
 * Fund action class through which all actions related to fund module
 * happen
 */

public class FundAction extends MifosBaseAction {

	/**An instance of the logger which is used to log statements */
	private MifosLogger logger =MifosLogManager.getLogger(LoggerConstants.FUNDLOGGER);
	
	/**
	 * Returns keyMethod map to be added to the keyMethodMap of the base Action class.
	 * This method is called from <code>getKeyMethodMap<code> of the base Action Class.
	 * The map returned from this method is appended to the map of the <code>getKeyMethodMap<code> in the base action class and that will form the complete map for that action.
	 * This map will have method names of extra methods to be called in case of ClientCreationAction.
	 * @return
	 */
	public Map<String,String> appendToMap()
	{
		Map<String,String> methodHashMap = new HashMap<String,String>();

		//TODO change this code------------------------
		methodHashMap.putAll(super.appendToMap());
		methodHashMap.put(FundConstants.METHOD_GET_ALL_FUNDS, FundConstants.METHOD_GET_ALL_FUNDS);
		return methodHashMap;
	}
	/**
	 * This method is called before th laod page for center is called
	 * It sets this information in session and context.This should be removed after center was successfully created.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customLoad(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		this.clearActionForm(form);
		return null;
	}
	/**
	 * This method is called before converting action form to value object on every method call.
	 * if on a particular method conversion is not required , it returns false, otherwise returns true
	 * @return Returns whether the action form should be converted or not
	 */
	protected boolean isActionFormToValueObjectConversionReq(String methodName) {
		if (null != methodName	&& (methodName.equals("load"))) {			
			return false;
		} else {
			return true;
		}
	}
	
	
	/**
	 * overriden getpath method returns the path used to find valueobject
	 * business processor and dao of checklist module
	 * 
	 * @return java.lang.String
	 */

	protected String getPath() {		
		return FundConstants.FUNDSPATH;
	}
	
	/**
	  *	Method which is called to load the page to preview the details entered by the user. Depending on the input
	  * page, this method forwards to the respective preview page
	  * @param mapping The page to which the control passes to. This is specified in the struts-config.xml
	  * @param form The form bean associated with this action
	  * @param request Contains the request parameters
	  * @param response
	  * @return The mapping to the next page
	  */

	 public ActionForward customPreview(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
		FundActionForm fundActionForm = (FundActionForm)form;
		String forward = null;
		//obtaining the input page, so that the repective forward can be decided
		String fromPage =fundActionForm.getInput();
		Context context =(Context)request.getAttribute(Constants.CONTEXT);
		logger.debug("Inside customPreview and input page is: "+fromPage);
		//personnel object and display address is set only if input page is create or manage
		Fund fund = (Fund)context.getValueObject();
		//get selected loan officer name and set in center value object
		if(CenterConstants.INPUT_CREATE.equals(fromPage)){
			if(fundActionForm.getGlCode().getGlCodeId() !=null ||fundActionForm.getGlCode().getGlCodeId().shortValue() !=0 ){
				GLCode glCode = getGlCode(context,fundActionForm.getGlCode().getGlCodeId());
				fundActionForm.setGlCode(glCode);
				fund.setGlCode(glCode);
			}
			else{
				fund.setGlCode(null);
			}
		}
		//deciding forward page based on the input page
		if(  CenterConstants.INPUT_CREATE.equals(fromPage))
			forward = FundConstants.FUND_CREATE_PREVIEW_PAGE;
		else if(CenterConstants.INPUT_MANAGE.equals(fromPage ))
			forward = FundConstants.FUND_EDIT_PREVIEW_PAGE;
		logger.debug("forward: "+forward);
		return mapping.findForward(forward);
	}
	 /**
	  *	Method which is called to load the page to preview the details entered by the user. Depending on the input
	  * page, this method forwards to the respective preview page
	  * @param mapping The page to which the control passes to. This is specified in the struts-config.xml
	  * @param form The form bean associated with this action
	  * @param request Contains the request parameters
	  * @param response
	  * @return The mapping to the next page
	  */

	 public ActionForward customPrevious(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
		FundActionForm fundActionForm = (FundActionForm)form;
		String forward = null;
		//obtaining the input page, so that the repective forward can be decided
		String fromPage =fundActionForm.getInput();
		//deciding forward page based on the input page
		if(  CenterConstants.INPUT_CREATE.equals(fromPage))
			forward = FundConstants.FUND_CREATE_PAGE;
		else if(CenterConstants.INPUT_MANAGE.equals(fromPage ))
			forward = FundConstants.FUND_EDIT_PAGE;
		logger.debug("forward: "+forward);
		return mapping.findForward(forward);
	}
	 
	 /**
	  *	Method which is called to load the page to edit the fund details entered by the user. This sets the object 
	  * whose details have to be edited as the value object in the context.
	  * @param mapping The page to which the control passes to. This is specified in the struts-config.xml
	  * @param form The form bean associated with this action
	  * @param request Contains the request parameters
	  * @param response
	  * @return The mapping to the next page
	  */

	 public ActionForward customManage(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
		
		 FundActionForm fundActionForm = (FundActionForm)form;
		String forward = null;
		//obtaining the input page, so that the repective forward can be decided
		String fundId =fundActionForm.getFundId();
		Context context =(Context)request.getAttribute(Constants.CONTEXT);
		Iterator iteratorFund=((List)context.getSearchResultBasedOnName(FundConstants.ALL_FUNDLIST).getValue()).iterator();
		//get selected fund object and set as valueobject in the context
		if(!ValidateMethods.isNullOrBlank(fundId) ){
			//Obtaining the fund object which ahs been selected, so that its details can be edited
			while (iteratorFund.hasNext()){
				Fund fundItem=(Fund)iteratorFund.next();
				if(fundItem.getFundId().shortValue()== Short.valueOf(fundId).shortValue()){
					context.setValueObject(fundItem);
					context.addBusinessResults("OldFundName",fundItem.getFundName());
					break;
				}
			}
		}
		return null;
	}
	 
	 /**
	  *	Method which is called to load the page to view the list of fund currently in the system 
	  * @param mapping The page to which the control passes to. This is specified in the struts-config.xml
	  * @param form The form bean associated with this action
	  * @param request Contains the request parameters
	  * @param response
	  * @return The mapping to the next page
	  */

	 public ActionForward getAllFunds(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response)throws Exception{
		FundActionForm fundActionForm = (FundActionForm)form;
		String forward = FundConstants.ALL_FUNDS_PAGE;
		//obtaining the input page, so that the repective forward can be decided
		String fundId =fundActionForm.getInput();
		Context context =(Context)request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction(FundConstants.METHOD_GET_ALL_FUNDS);
		delegate(context , request);
		return mapping.findForward(forward);
	}
	 /**
	  * This method iterates through the list of glcodes. This retrieves the selected glcode object
	  * @param context
	  * @param glCodeId
	  * @return
	  */
	 private GLCode getGlCode(Context context, short glCodeId) {
			GLCode glCode = new GLCode();
			Iterator iteratorGLCode=((List)context.getSearchResultBasedOnName(FundConstants.GLCODELIST).getValue()).iterator();
			//Obtaining the name of the selected loan officer from the master list of loan officers
			while (iteratorGLCode.hasNext()){
				GLCode glCodeItem=(GLCode)iteratorGLCode.next();
				if(glCodeItem.getGlCodeId().shortValue()==glCodeId){
					glCode.setGlCodeId(glCodeItem.getGlCodeId());
					glCode.setGlCodeValue(glCodeItem.getGlCodeValue());
				}
			}
			return glCode;
		}
	 
	 /**
	  * This method is called to clear action form values, whenever a fresh request to create a fund comes in
	  * This is necessary because action form is stored in session
	  * @param mapping indicates action mapping defined in struts-config.xml
	  * @param form The form bean associated with this action
	  */
	private void clearActionForm(ActionForm form){
		FundActionForm fundForm = (FundActionForm)form;
		fundForm.setGlCode(new GLCode());
		fundForm.setFundName("");
		

	}
	 /**
	  *	Method which is called to decide the pages on which the errors on failure of validation will be displayed
	  * this method forwards to the respective input page
	  * @param mapping The page to which the control passes to. This is specified in the struts-config.xml
	  * @param form The form bean associated with this action
	  * @param request Contains the request parameters
	  * @param response
	  * @return The mapping to the next page
	  */
	public ActionForward customValidate(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) {

		String forward = null;
		String methodCalled= (String)request.getAttribute("methodCalled");
		logger.debug("methodCalled: "+methodCalled);
		FundActionForm fundForm = (FundActionForm)form;
		String fromPage =fundForm.getInput();
		//deciding forward page
		if(null !=methodCalled) {
			//depending on where the preview was called from, the action forwards to the page where the errors will be displayed
			if(CustomerConstants.METHOD_PREVIEW.equals(methodCalled)){
				if(  CenterConstants.INPUT_CREATE.equals(fromPage))
					forward =FundConstants.FUND_CREATE_PREVIEW__FAILUREPAGE;
				else if(CenterConstants.INPUT_MANAGE.equals(fromPage ))
					forward = FundConstants.FUND_EDIT_PREVIEW_FAILURPAGE;

			}
			logger.debug("forward: "+forward);

		}
		return mapping.findForward(forward);

	}
	
}
