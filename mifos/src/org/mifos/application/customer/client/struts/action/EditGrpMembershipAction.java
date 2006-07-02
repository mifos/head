/**

 * EditGrpMembershipAction.java    version: xxx

 

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

package org.mifos.application.customer.client.struts.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.client.util.valueobjects.Client;
import org.mifos.application.customer.group.struts.actionforms.GroupActionForm;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.group.util.helpers.GroupTransferInput;
import org.mifos.application.customer.group.util.helpers.LinkParameters;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.PathConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.struts.action.MifosSearchAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This class acts as an Action class for editing group membership of a client.
 * @author ashishsm
 *
 */
public class EditGrpMembershipAction extends MifosSearchAction {

	/**An insatnce of the logger which is used to log statements */
	private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER);
	/** 
	 * This method is called by framework to add extra methods needed for client transfer module.
	 * It calls updateParent method in business processor
	 * @return Map of extra methods needed for group module
	 */	
	public Map<String,String> appendToMap(){
		
		Map<String,String> methodHashMap = new HashMap<String,String>();
		//TODO change this code------------------------
		methodHashMap.putAll(super.appendToMap());
		methodHashMap.put(ClientConstants.METHOD_LOAD_GROUP_TRANSFER,ClientConstants.METHOD_LOAD_GROUP_TRANSFER);
		methodHashMap.put(ClientConstants.METHOD_LOAD_TRANSFER,ClientConstants.METHOD_LOAD_TRANSFER);
		methodHashMap.put(ClientConstants.METHOD_CONFIRM_GROUP_TRANSFER,ClientConstants.METHOD_CONFIRM_GROUP_TRANSFER);
		methodHashMap.put(ClientConstants.METHOD_CONFIRM_BRANCH_TRANSFER,ClientConstants.METHOD_CONFIRM_BRANCH_TRANSFER);
		methodHashMap.put(ClientConstants.METHOD_UPDATE_BRANCH,ClientConstants.METHOD_UPDATE_BRANCH);
		return methodHashMap;
			
	}
		
		
	/* (non-Javadoc)
	 * @see org.mifos.framework.struts.action.MifosBaseAction#getPath()
	 */
	
	protected String getPath() {
		
		return PathConstants.CLIENT_TRANSFER_CHANGE;
	}
	
	/** 
	 * This method is called to show group search page for client tranfer across a group in same office.
	 * @param mapping indicates action mapping defined in struts-config.xml 
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */	
	public ActionForward loadGroupTransfer(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction(ClientConstants.METHOD_LOAD_GROUP_TRANSFER);
		delegate(context,request);	
		SessionUtils.setAttribute(CustomerConstants.CUSTOMER_SEARCH_INPUT,context.getBusinessResults(CustomerConstants.CUSTOMER_SEARCH_INPUT) , request.getSession());
		return mapping.findForward(ClientConstants.GROUP_TRANSFER_SEARCH_PAGE);
	}
	
	/** 
	 * This method is called to load the page for office list, 
	 * from where user can select the office to which group is to be transferred
	 * It calls the loadTransfer method on businessprocessor, that in turn loads list of all offices 
	 * @param mapping indicates action mapping defined in struts-config.xml 
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */	
	public ActionForward loadTransfer(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		
		//call loadTransfer method in GroupBP
		//context.setBusinessAction(ClientConstants.METHOD_LOAD_TRANSFER);
		//delegate(context,request);
		
		//return loadTransfer success forward
		return mapping.findForward(ClientConstants.BRANCH_TRANSFER_SEARCH_PAGE);	
	}
	
	/** 
	 * This method is called to show the confirmation page for transferring client across a different group.
	 * It forwards to confirm transfer page, from where user can update branch office of the group.
	 * @param mapping indicates action mapping defined in struts-config.xml 
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */		
	public ActionForward confirmBranchTransfer(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {

		return mapping.findForward(ClientConstants.BRANCH_TRANSFER_CONFIRMATION_PAGE);
	}
	
	/** 
	 * This method is called to show the confirmation page for transferring client across a different group.
	 * It forwards to confirm transfer page, from where user can update branch office of the group.
	 * @param mapping indicates action mapping defined in struts-config.xml 
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */		
	public ActionForward confirmGroupTransfer(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {

		return mapping.findForward(ClientConstants.GROUP_TRANSFER_CONFIRMATION_PAGE);
	}
	
	
	
	
	
	/**
	 * This method obtains the current client details that were present in the session and puts it into the context
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
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		context.addBusinessResults(ClientConstants.CLIENT_TRANSFER , (Client)SessionUtils.getAttribute(ClientConstants.OLDCLIENT , request.getSession()));
		return null;	
	}
	/**
	 * This method is called to update the client when the clients group membership has been changed. Once the 
	 * details have been updated, the old client details are removed from session and the call is forwarded to the details 
	 * page for the client.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@TransactionDemarcate(validateAndResetToken =true)
	public ActionForward update(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		ActionForward forward =super.update(mapping , form, request, response);
		request.getSession().removeAttribute(ClientConstants.OLDCLIENT);
		return forward;
		
	}
	
	/** 
	 * This method is called when client is transferred in different branch.
	 * It calls updateBranchRSM method in business processor
	 * @param mapping indicates action mapping defined in struts-config.xml 
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	@TransactionDemarcate(validateAndResetToken =true)
	public ActionForward updateBranch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
			
		//get the gpTransferInput object from  session and set the office id and office name for the center
		HttpSession session = request.getSession();
		context.addBusinessResults("transferBranchObject" , (Client)SessionUtils.getAttribute(ClientConstants.OLDCLIENT , request.getSession()));
		 logger.debug("CUSTOMER VERSION IN EDIT GRP MEMSHIP ACTION: "+ ((Client)SessionUtils.getAttribute(ClientConstants.OLDCLIENT , request.getSession())).getVersionNo()) ;
		context.setBusinessAction(GroupConstants.UPDATE_BRANCH_RSM);
		delegate(context,request);
		request.getSession().removeAttribute(ClientConstants.OLDCLIENT);
		return mapping.findForward(GroupConstants.UPDATE_BRANCH_SUCCESS);	
	}

}
