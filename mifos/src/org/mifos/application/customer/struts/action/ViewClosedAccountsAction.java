/**
 
 * ViewClosedAccounts.java    version: xxx
 
 
 
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

package org.mifos.application.customer.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.framework.struts.action.MifosBaseAction;


/**
 * This class is used as Action for viewing all closed accounts of a customer.
 */
public class ViewClosedAccountsAction extends MifosBaseAction {
	
	/**
	 * 
	 */
	public ViewClosedAccountsAction() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see org.mifos.framework.struts.action.MifosBaseAction#getPath()
	 */
	
	protected String getPath() {
		return null;
	}
	
	/**
	 * No need to override.
	 * @see org.mifos.framework.struts.action.MifosBaseAction#search(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)	throws Exception
	{
		return super.search(mapping, form, request, response);
	}
	
	/**
	 * No need to override.
	 * @see org.mifos.framework.struts.action.MifosBaseAction#search(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)	throws Exception
	{
		return super.cancel(mapping, form, request, response);
	}
	
	/**
	 * @param mapping
	 * @param form
	 * @param request
	 * @param httpservletresponse
	 * @return
	 * @throws Exception
	 */
	public ActionForward customSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse httpservletresponse)
	throws Exception
	{
		return null;
	}
	
	/**
	 * This returns an ActionForward depending upon where we came from to this page.This is inferred using the searchString in the searchNode.
	 * Thus an appropriate ActionForward is returned.It should be a chained action with a get being called again on the correponding module with the ID.  
	 * @param mapping
	 * @param form
	 * @param request
	 * @param httpservletresponse
	 * @return
	 * @throws Exception
	 */
	public ActionForward customCancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse httpservletresponse)
	throws Exception
	{
		return null;
	}
	
	
}
