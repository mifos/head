/**

 * ClosedAccSearchAction.java    version: xxx

 

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
package org.mifos.application.accounts.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.struts.actionforms.ClosedAccSearchActionForm;
import org.mifos.application.accounts.util.helpers.ClosedAccSearchConstants;
import org.mifos.framework.struts.action.MifosSearchAction;

/**
 * @author mohammedn
 *
 */
public class ClosedAccSearchAction extends MifosSearchAction {

	/**
	 * default constructor
	 */
	public ClosedAccSearchAction() {
	}

	/* (non-Javadoc)
	 * @see org.mifos.framework.struts.action.MifosBaseAction#getPath()
	 */
	@Override
	protected String getPath() {
		return ClosedAccSearchConstants.GETPATHCLOSEDACCSEARCH;
	}

	/* (non-Javadoc)
	 * @see org.mifos.framework.struts.action.MifosBaseAction#search(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward customSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ClosedAccSearchActionForm closedAccSearchActionForm=(ClosedAccSearchActionForm)form;
		String input=closedAccSearchActionForm.getInput();
		if(input != null &&input.equals(ClosedAccSearchConstants.VIEWALLACCACTIVITY)) {
			return mapping.findForward(ClosedAccSearchConstants.SEARCHCUSTOMERACTIVITYSUCCESS);
		}
		if(input != null &&input.equals(ClosedAccSearchConstants.VIEWCLIENTCHARGES)) {
			return mapping.findForward(ClosedAccSearchConstants.SEARCHCLIENTCHARGESSUCCESS);
		}
		if(input != null &&input.equals(ClosedAccSearchConstants.VIEWGROUPCHARGES)) {
			return mapping.findForward(ClosedAccSearchConstants.SEARCHGROUPCHARGESSUCCESS);
		}
		if(input != null &&input.equals(ClosedAccSearchConstants.VIEWCENTERCHARGES)) {
			return mapping.findForward(ClosedAccSearchConstants.SEARCHCENTERCHARGESSUCCESS);
		}
		if(input != null &&input.equals(ClosedAccSearchConstants.VIEWCLIENTCHANGELOG)) {
			return mapping.findForward(ClosedAccSearchConstants.SEARCHCLIENTLOGSUCCESS);
		}
		if(input != null &&input.equals(ClosedAccSearchConstants.VIEWGROUPCHANGELOG)) {
			return mapping.findForward(ClosedAccSearchConstants.SEARCHGROUPCHANGELOGSUCCESS);
		}
		if(input != null &&input.equals(ClosedAccSearchConstants.VIEWCENTERCHANGELOG)) {
			return mapping.findForward(ClosedAccSearchConstants.SEARCHCENTERCHANGELOGSUCCESS);
		}
		if(input != null &&input.equals(ClosedAccSearchConstants.VIEW_GROUP_CLOSED_ACCOUNTS)) {
			return mapping.findForward(ClosedAccSearchConstants.SEARCH_GROUP_CLOSED_ACCOUNT_SUCCESS);
		}
		if(input != null &&input.equals(ClosedAccSearchConstants.VIEW_CLIENT_CLOSED_ACCOUNTS)) {
			return mapping.findForward(ClosedAccSearchConstants.SEARCH_CLIENT_CLOSED_ACCOUNT_SUCCESS);
		}
		if(input != null &&input.equals(ClosedAccSearchConstants.VIEW_CENTER_CLOSED_ACCOUNTS)) {
			System.out.println("**************************mapping.findForward(ClosedAccSearchConstants.SEARCH_CENTER_CLOSED_ACCOUNT_SUCCESS)::::"+mapping.findForward(ClosedAccSearchConstants.SEARCH_CENTER_CLOSED_ACCOUNT_SUCCESS));
			return mapping.findForward(ClosedAccSearchConstants.SEARCH_CENTER_CLOSED_ACCOUNT_SUCCESS);
		}
		
		return null;
	}
	
	public ActionForward customCancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ClosedAccSearchActionForm closedAccSearchActionForm=(ClosedAccSearchActionForm)form;
		String input=closedAccSearchActionForm.getInput();
		
		if(input != null &&input.equals(ClosedAccSearchConstants.VIEW_GROUP_CLOSED_ACCOUNTS)) {
			return mapping.findForward(ClosedAccSearchConstants.GROUP_DETAILS_PAGE);
		}
		if(input != null &&input.equals(ClosedAccSearchConstants.VIEW_CLIENT_CLOSED_ACCOUNTS)) {
			return mapping.findForward(ClosedAccSearchConstants.CLIENT_DETAILS_PAGE);
		}
		if(input != null &&input.equals(ClosedAccSearchConstants.VIEW_CENTER_CLOSED_ACCOUNTS)) {
			return mapping.findForward(ClosedAccSearchConstants.CENTER_DETAILS_PAGE);
		}
		if(input != null &&input.equals(ClosedAccSearchConstants.VIEWGROUPCHANGELOG)) {
			return mapping.findForward(ClosedAccSearchConstants.GROUP_DETAILS_PAGE);
		}
		if(input != null &&input.equals(ClosedAccSearchConstants.VIEWGROUPCHANGELOG)) {
			return mapping.findForward(ClosedAccSearchConstants.GROUP_DETAILS_PAGE);
		}
		if(input != null &&input.equals(ClosedAccSearchConstants.VIEWCENTERCHANGELOG)) {
			return mapping.findForward(ClosedAccSearchConstants.CENTER_DETAILS_PAGE);
		}
		if(input != null &&input.equals(ClosedAccSearchConstants.VIEWCLIENTCHANGELOG)) {
			return mapping.findForward(ClosedAccSearchConstants.CLIENT_DETAILS_PAGE);
		}
		
		return null;
	}
}
