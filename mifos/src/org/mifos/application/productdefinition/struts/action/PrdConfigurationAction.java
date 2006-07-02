/**

 * PrdConfigurationAction.java    version: xxx

 

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

package org.mifos.application.productdefinition.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.struts.action.MifosBaseAction;

/**
 * This class is the Action for Product configuration.It contains method to delegate to the
 * business processor and then to forward the response to the specified pages.
 * 
* @author ashishsm
* 
*/
public class PrdConfigurationAction extends MifosBaseAction {

	/**
	 *default constructor 
	 */
	public PrdConfigurationAction() {
	}

	/**
	 * returns String which uniquely identifies the dependency element in Dependency.xml
	 * 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#getPath()
	 */
	protected String getPath() {
		return ProductDefinitionConstants.GETPATHPRDCONF;
	}
	
	/**
	 * This method is called when the update method is requestd.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customUpdate(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		
	return mapping.findForward(ProductDefinitionConstants.UPDATE);
	}
	
	/**
	 * This method is called when the requested  method is cancel. It forwards the response directly
	 * skipping delegation to the business processor. Returns the action forward based on the 
	 * input because the same method call requires to take users to different jsps.
	 * 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#cancel(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward cancel(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		return mapping.findForward(ProductDefinitionConstants.CANCEL);
	}
	
	/**
	 * This method is called when the requested  method is search.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customSearch(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		
		return mapping.findForward(ProductDefinitionConstants.SEARCH);
	}

/**
 * This method is used to skip action form to value object conversion.
 * 
 * @see org.mifos.framework.struts.action.MifosBaseAction#isActionFormToValueObjectConversionReq(java.lang.String)
 */
	protected boolean isActionFormToValueObjectConversionReq(String methodName) {
		if(methodName !=null) {
			if(ProductDefinitionConstants.CANCELMETHOD.equals(methodName) || 
					ProductDefinitionConstants.SEARCHMETHOD.equals(methodName)) {
				return false;
			}
		}
		return true;
	}
	
	
}
