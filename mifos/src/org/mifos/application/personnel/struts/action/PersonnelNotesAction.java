/**

 * PersonnelNotesAction.java    version: xxx

 

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

package org.mifos.application.personnel.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.configuration.util.helpers.PathConstants;
import org.mifos.application.personnel.struts.actionforms.PersonnelNotesActionForm;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.struts.action.MifosSearchAction;
import org.mifos.framework.util.helpers.Constants;


/**
 * This denotes the action class for the personnel notes module. It uses the base class functions to create, update. 
 * It also includes implementations for cancel and validate
 * @author navitas
 */
public class PersonnelNotesAction extends MifosSearchAction {

	

  /** 
   *  Returns the path which uniquely identifies the element in the dependency.xml.
   *  This method implementaion is the framework requirement. 
   */
	protected String getPath() {
		
		return PathConstants.PERSONNEL_NOTES_PATH;
	}
	
	/**
	 * This method is added because we might require to do some cleanup whenever the user is clicking on cancel.
	 * The operation carried out in this method is again based on the input parameter which comes as a hiiden field 
	 * from jsp, because the same cancel leads the user to differnt pages depending on which jsp pages it is clicked.
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return -- Returns the actionforward where the request is supposed to be forwarded based on the input parameter.
	 * @throws Exception
	 */
	public ActionForward customCancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		return mapping.findForward(PersonnelConstants.USER_DETAILS_PAGE); 
	}

	/**
	 * This method is called whenever users saves a personnel notes in database.
	 * It is implemented to return mapping to next page.
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 * @throws Exception
	 */	
	public ActionForward customCreate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		return mapping.findForward(PersonnelConstants.USER_DETAILS_PAGE);
	}
	
	/**
	 * This method is called whenever user clicks add a new note on UI
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 * @throws Exception
	 */	
	public ActionForward customLoad(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		clearActionForm(form);
		return null;
	}
	
	/**
	 * This method is called whenever user clicks see all notes link on UI
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 * @throws Exception
	 */	
	public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		PersonnelNotesActionForm notesForm = (PersonnelNotesActionForm) form;
		notesForm.setSearchNode(Constants.SEARCH_NAME,PersonnelConstants.PERSONNEL_NOTES);
		return mapping.findForward(PersonnelConstants.GET_SUCCESS);
	}
	
	/**
	  * Method which is called to decide the pages on which the errors on failure of validation will be displayed 
	  * this method forwards as per the respective input page 
	  * @param mapping indicates action mapping defined in struts-config.xml 
	  * @param form The form bean associated with this action
	  * @param request Contains the request parameters
	  * @param response
	  * @return The mapping to the next page
	  * @throws Exception
	  */
	public ActionForward customValidate(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) {
		String forward = null;	
		String methodCalled= (String)request.getAttribute("methodCalled");
		PersonnelNotesActionForm personnelNotesActionForm = (PersonnelNotesActionForm)form;
			 //deciding forward page
			if(null !=methodCalled) {
				if(PersonnelConstants.METHOD_PREVIEW.equals(methodCalled))
					forward = PersonnelConstants.LOAD_SUCCESS;
			}
			return mapping.findForward(forward); 
	}

	/**
	  * This method will clear action form  
	  * @param form The form bean associated with this action
	  */
	void clearActionForm(ActionForm form){
		PersonnelNotesActionForm personnelNotesActionForm= (PersonnelNotesActionForm)form;
		personnelNotesActionForm.setComment("");
	}

}
