/**

 * AccountProcessAction.java    version: xxx

 

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
import org.mifos.application.productdefinition.struts.actionforms.AccountProcessFlowActionForm;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.struts.action.MifosBaseAction;

/**
 * @author ashishsm
 *
 */
public class AccountProcessFlowAction extends MifosBaseAction {

	/**
	 * 
	 */
	public AccountProcessFlowAction() {
		super();
		
	}

	/* (non-Javadoc)
	 * @see org.mifos.framework.struts.action.MifosBaseAction#getPath()
	 */
	@Override
	protected String getPath() {
		
		return ProductDefinitionConstants.GETPATHPROCESSFLOW;
	}
	
/**
	 * No need to override.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward load(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		return mapping.findForward(ProductDefinitionConstants.LOAD);
	}
	
/**
	 * The action returned from update has to be a chained action because it has to go to admin page after updating.It should also cleanup unwanted things from session.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward update(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		//AccountProcessFlowActionForm actionForm=(AccountProcessFlowActionForm)form;
		//System.out.println(actionForm.getClientsSubmitApproval()+"Client");
		//System.out.println(actionForm.getGroupsSubmitApproval()+"Group");
		//System.out.println(actionForm.getLoansSubmitApproval()+"Loan Submit");
		//System.out.println(actionForm.getLoanDisbursLO()+"Loan Lo");
		//System.out.println(actionForm.getSavingsSubmitApproval()+"Savings");
		//System.out.println(actionForm.getInsuranceSubmitApproval()+"Insurance");
		
		return mapping.findForward(ProductDefinitionConstants.UPDATE);
	}
	
/**
	 * No need to override.No need for it to call businessprocessor.It should also do cleaning up unwanted things from the session.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward cancel(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		return mapping.findForward(ProductDefinitionConstants.CANCEL);
		
	}

}
