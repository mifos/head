/**

 * OfficeHierarchyAction.java    version: 1.0

 

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
package org.mifos.application.office.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.framework.struts.action.MifosBaseAction;
import org.mifos.framework.util.helpers.TransactionDemarcate;

/**
 * This class represent the office hierarchy action it is used to
 * configure and unconfigure the officeLevels in the system
 * @author rajenders
 *
 */
public class OfficeHierarchyAction extends MifosBaseAction {

	 @TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ActionForward forward=null;
		
		// In case of exception ,while updating officehierarchy we want to save the token ,
		//so that we do not get doubble summit exception 
		try {

			forward= super.update(mapping, form, request, response);
		}
		catch( OfficeException oe){
			saveToken(request);
			throw oe;
		}
		return forward;
	}

	/* (non-Javadoc)
	 * @see org.mifos.framework.struts.action.MifosBaseAction#getPath()
	 */
	@Override
	protected String getPath() {

		
		return OfficeConstants.OFFICEHIERARCHY_DEPENDENCY_NAME;
	}

}
