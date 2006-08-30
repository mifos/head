/**

 * CheckListAction.java    version: 1.0

 

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

package org.mifos.application.checklist.struts.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.checklist.business.handlers.CheckListBusinessProcessor;
import org.mifos.application.checklist.struts.actionforms.CheckListActionForm;
import org.mifos.application.checklist.util.helpers.CheckListHelper;
import org.mifos.application.checklist.util.resources.CheckListConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.struts.action.MifosBaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.framework.util.valueobjects.Context;

/**
 * Checklist action class through which all actions related to check list module
 * happen
 */

public class CheckListAction extends MifosBaseAction {

	/**
	 * Constructor used to take all the properties of super class
	 */
	public CheckListAction() {
		super();		
	}

	/**
	 * This method is called before converting action form to value object on every method call.
	 * if on a particular method conversion is not required , it returns false, otherwise returns true
	 * @return Returns whether the action form should be converted or not
	 */
	
	protected boolean isActionFormToValueObjectConversionReq(String methodName) {
		if (null != methodName	&& (methodName.equals("manage")	|| methodName.equalsIgnoreCase("previous")	|| methodName.equalsIgnoreCase("update") || methodName.equals("create"))) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * overrides super method
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@TransactionDemarcate(validateAndResetToken=true)
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
		Context context = (Context) request.getSession().getAttribute(Constants.CONTEXT);
		if (null != context) {
			CheckListActionForm caf = (CheckListActionForm) form;
			CheckListHelper.saveInContext("Type", Short.valueOf(caf.getTypeId()), context);
			CheckListHelper.saveInContext("Level", Short.valueOf(caf.getCategoryId()), context);
			CheckListHelper.saveInContext("Name", caf.getType(), context);
		}
		return super.create(mapping, form, request, response);
	}

	/**
	 * Returns keyMethod map to be added to the keyMethodMap of the super class.
	 * 
	 * @return Map<String, String>
	 */
	public Map<String, String> appendToMap() {
		Map<String, String> methodHashMap = new HashMap<String, String>();
		methodHashMap.put(CheckListConstants.LOADPARENT,CheckListConstants.LOADPARENT);
		methodHashMap.put(CheckListConstants.LOADALL,CheckListConstants.LOADALL);
		methodHashMap.put(CheckListConstants.GETPARENT,CheckListConstants.GETPARENT);
		return methodHashMap;
	}

	/**
	 * overriden getpath method returns the path used to find valueobject
	 * ,business processor and dao of checklist module
	 * 
	 * @return java.lang.String
	 */

	protected String getPath() {
		// TODO Auto-generated method stub
		return "CheckList";
	}

	
	/**
	 * method used to load all the statuses depending upon the selected category
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward loadParent(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		String forward = CheckListConstants.FORWARDLOADPARENTSUCESS;
		CheckListActionForm checkListActionForm = (CheckListActionForm) form;
		String fromPage = checkListActionForm.getInput();
		if (fromPage.equalsIgnoreCase("manage")) {
			forward = "manage_success";
		}		
		loadChildStatus(mapping, form, request, response);
		return mapping.findForward(forward);
	}

	
	
	/**
	 * Helper method to load the parent
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 */
	private void loadChildStatus(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		if (null != context) {
			CheckListActionForm caf = (CheckListActionForm) form;
			if (caf.getTypeId().equalsIgnoreCase("")) {
				context.removeAttribute("StateMaster");
			}			
			else {
				CheckListHelper.saveInContext("Type", Short.valueOf(caf.getTypeId()), context);
				CheckListHelper.saveInContext("Level", Short.valueOf(caf.getCategoryId()), context);
				CheckListBusinessProcessor clbp = new CheckListBusinessProcessor();
				try {
					clbp.loadParent(context);
				} catch (SystemException se) {
					se.printStackTrace();
				} catch (ApplicationException ae) {
					ae.printStackTrace();
				}
			}
		}
	}

	
	/**
	 * this method is called when the user wants to view all the checklists
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward loadall(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		CheckListBusinessProcessor checkListBusinessProcessor = new CheckListBusinessProcessor();
		try {
			checkListBusinessProcessor.getCheckListNames(context);
		} catch (SystemException e) {			
			e.printStackTrace();
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		String forward = CheckListConstants.FORWARDONLOADALL;
		return mapping.findForward(forward);
	}

	 
	/**
	 * customPreview method is used to forward to two jsps depending on frompage 
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
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		ActionForward forward = null;
		CheckListActionForm checkListActionForm = (CheckListActionForm) form;
		String fromPage = checkListActionForm.getInput();
		Short previousStatusId = checkListActionForm.getPreviousStatusId();		
		CheckListHelper.saveInContext("fromPage", fromPage, context);
		CheckListHelper.saveInContext("previousStatusId", previousStatusId,context);
		if (fromPage.equalsIgnoreCase("manage")) {
			forward = mapping.findForward("manage_preview");
		}
		return forward;
	}

	
	/**
	 *  this method is used to forward to two jsps depending upon frompage
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
		ActionForward forward = null;
		CheckListActionForm checkListActionForm = (CheckListActionForm) form;
		String fromPage = checkListActionForm.getInput();
		if (fromPage.equalsIgnoreCase("manage")) {
			forward = mapping.findForward("manage_success");
		}
		return forward;
	}

	/**
	 * overrides super method
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		if (null != context) {
			CheckListActionForm caf = (CheckListActionForm) form;			
			request.getSession().setAttribute("status", caf.getStatusOfCheckList());
			CheckListHelper.saveInContext("Type", Short.valueOf(caf.getTypeId()), context);
			CheckListHelper.saveInContext("Level", Short.valueOf(caf.getCategoryId()), context);		
			}
		return super.update(mapping, form, request, response);
	}

	/**
	 * overrides super method
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@TransactionDemarcate(saveToken = true)
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		CheckListActionForm caf = (CheckListActionForm) form;
		request.getSession().setAttribute("status", caf.getStatusOfCheckList());
		if (null != context) {			
			CheckListHelper.saveInContext("Level", Short.valueOf(caf.getCategoryId()), context);
			CheckListHelper.saveInContext("Type", Short.valueOf(caf.getTypeId()), context);
		}
		return super.get(mapping, form, request, response);
	}

	/**
	 * this method is used to differentiate preview buttons in creation and edit page.
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
		String input = request.getParameter("input");
		if(null != input)
			if (input.equalsIgnoreCase("manage")) {	
				return mapping.findForward("manage_failure");
			} else {	
				return mapping.findForward("preview_failure");
			}
		return null;
	}
	
	/**
	 * overrides super method
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@TransactionDemarcate(joinToken = true)
	public ActionForward manage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		if (null != context) {
			CheckListActionForm caf = (CheckListActionForm) form;			
			request.getSession().setAttribute("status", caf.getStatusOfCheckList());
			CheckListHelper.saveInContext("Type", Short.valueOf(caf.getTypeId()), context);
			CheckListHelper.saveInContext("Level", Short.valueOf(caf.getCategoryId()), context);		
			}
		return super.manage(mapping, form, request, response);
	}
}
