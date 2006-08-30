/**

 * MifosSearchWizardAction.java    version: 1.0

 

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
package org.mifos.framework.struts.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.MethodInvoker;
import org.mifos.framework.util.helpers.ResourceConstants;
import org.mifos.framework.util.valueobjects.Context;

public abstract class MifosSearchWizardAction extends MifosSearchAction {

	protected abstract String getPath();
	
	/**
	 * Returns a <code>HashMap<String,String></code> which has a entry for the method 
	 * to be called when the user clicks on Next button in wizard like pages 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#appendToMap()
	 */
	public Map<String,String> appendToMap()
	{
		Map<String,String> keyMethodMap = super.appendToMap();
		keyMethodMap.put(ResourceConstants.NEXT,   "next");
		return keyMethodMap;
	}
	
	/**
	 * Returns an action forward by the name "next_success".
	 * It delegates the call to the business processor which should have the business logic 
	 * required for this method. 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward next(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		ActionForward forward = null;
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction("next");
		forward = (ActionForward)MethodInvoker.invokeWithNoException(this, "customNext", new Object[]{mapping,form,request,response}, new Class[]{ActionMapping.class,ActionForm.class,HttpServletRequest.class,HttpServletResponse.class});
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("After visit Next", false, null);
		delegate(context,request);
		if(null != forward){
			return forward;
		}else{
			return mapping.findForward(Constants.NEXT_SUCCESS);
		}
		
		
		
	}
}
