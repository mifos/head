/**

* ProductCategoryAction.java    version: xxx



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
import org.mifos.application.productdefinition.struts.actionforms.ProductCategoryActionForm;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.struts.action.MifosBaseAction;

/**
 * This class is the Action for Product category.It contains method to delegate to the
 * business processor and then to forward the response to the specified pages.
 */
public class ProductCategoryAction extends MifosBaseAction {

	public ProductCategoryAction() {
	}
	
	private MifosLogger prdDefLogger=MifosLogManager.getLogger(LoggerConstants.PRDDEFINITIONLOGGER);
	/**
	 * returns String which uniquely identifies the dependency element in Dependency.xml
	 * 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#getPath()
	 */
	protected String getPath() {
		return ProductDefinitionConstants.GETPATHPRODUCTCATEGORY;
	}

	/**
	 * This method is called when the requested  method is load. It sets the action form, if any, present in
	 * the session to null.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customLoad(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("Inside Custom Load method of ProductCategoryAction");
		request.getSession().setAttribute(ProductDefinitionConstants.MIFOSPRDDEFACTIONFORM,null);
		return mapping.findForward(ProductDefinitionConstants.LOAD);
	}

	/**
	 * This method is called when the requested  method is preview. 
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customPreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		prdDefLogger.debug("Inside Custom Preview method of ProductCategoryAction");
		String input = request.getParameter(ProductDefinitionConstants.INPUT);
		if (input.equals(ProductDefinitionConstants.INPUTDETAILS)) {
			return mapping.findForward(ProductDefinitionConstants.MANAGEPREVIEW);
		}
		return mapping.findForward(ProductDefinitionConstants.CREATEPREVIEW);
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
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		prdDefLogger.debug("Inside cancel method of ProductCategoryAction");
		request.getSession().setAttribute(ProductDefinitionConstants.MIFOSPRDDEFACTIONFORM, null);
		String input = request.getParameter(ProductDefinitionConstants.INPUT);
		if (input.equals(ProductDefinitionConstants.INPUTDETAILS)) {
			return mapping.findForward(ProductDefinitionConstants.DETAILSCANCELFORWARD);
		}
		return mapping.findForward(ProductDefinitionConstants.ADMINCANCELFORWARD);
	}

	/**
	 * Returns the action forward based on the input because the same method
	 * call requires to take users to different jsps.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customPrevious(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("Inside custom Previous method of ProductCategoryAction");
		String input = request.getParameter(ProductDefinitionConstants.INPUT);
		if (input.equals(ProductDefinitionConstants.INPUTDETAILS)) {
			return mapping.findForward(ProductDefinitionConstants.DETAILSPREVIOUSFORWARD);
		}
		return mapping.findForward(ProductDefinitionConstants.ADMINPREVIOUSFORWARD);
	}

	/**
	 * This method is called when the requested  method is create.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customCreate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("Inside custom Create method of ProductCategoryAction");
		return mapping.findForward(ProductDefinitionConstants.CREATE);
	}

	/**
	 * This method is called when the requested  method is get.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customGet(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("Inside custom Manage method of ProductCategoryAction");
		return mapping.findForward(ProductDefinitionConstants.GET);
	}

	/**
	 * This method is called when the requested  method is manage.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customManage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("Inside custom Manage method of ProductCategoryAction");
		return mapping.findForward(ProductDefinitionConstants.MANAGE);
	}

	/**
	 * This has to return an ActionForward which is an actionforward chain and
	 * after update it has to perform a get to get the new details as the user
	 * goes to detail page after update.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customUpdate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		prdDefLogger.debug("Inside custom Update method of ProductCategoryAction");
		return mapping.findForward(ProductDefinitionConstants.UPDATE);
	}

	/**
	 * This method is called when the requested  method is search.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customSearch(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("Inside custom Search method of ProductCategoryAction");
		return mapping.findForward(ProductDefinitionConstants.SEARCH);
	}
	
	/**
	 * This is the method which would be called when any "validate" operation fails.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customValidate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String methodCalled = (String) request.getAttribute(ProductDefinitionConstants.METHODCALLED);
		String input = ((ProductCategoryActionForm) form).getInput();
		String forward = null;
		if (null != methodCalled) {
			if (methodCalled.equalsIgnoreCase(ProductDefinitionConstants.PREVIEWMETHOD)) {
				if (null != input && ProductDefinitionConstants.INPUTDETAILS.equalsIgnoreCase(input)) {
					forward = ProductDefinitionConstants.DETAILSPREVIOUSFORWARD;
				} else {
					forward = ProductDefinitionConstants.ADMINPREVIOUSFORWARD;
				}
			}
		}
		if (null != forward) {
			prdDefLogger.info("After validation failure redirecting to "+mapping.findForward(forward));
			return mapping.findForward(forward);
		}
		return null;

	}
	
	/**
	 * This method is used to skip action form to value object conversion.
	 */
	public boolean isActionFormToValueObjectConversionReq(String method) {
		if(null!=method) {
			if(ProductDefinitionConstants.CANCELMETHOD.equals(method)|| 
					ProductDefinitionConstants.MANAGEMETHOD.equals(method)) {
				prdDefLogger.info("Skipping ActionForm to ValueObjectConversion for "+method+" method");
				return false;
			}
		}
		return true;
	}
	
}
