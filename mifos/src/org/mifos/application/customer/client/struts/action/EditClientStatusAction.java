/**

 * EditClientStatusAction.java    version: xxx

 

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.customer.client.struts.actionforms.EditClientStatusActionForm;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.client.util.valueobjects.Client;
import org.mifos.application.customer.client.util.valueobjects.EditClientStatus;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.PathConstants;
import org.mifos.application.customer.util.valueobjects.CustomerNote;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.struts.action.MifosBaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This class acts as Action for edit client status module.
 * @author ashishsm
 *
 */
public class EditClientStatusAction extends MifosBaseAction {
/**An insatnce of the logger which is used to log statements */
	private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER);
	protected String getPath() {
		
		return PathConstants.CLIENT_STATUS_CHANGE;
	}
	
   /**
	 * This reads the next state id from the context and sets it in the request.
	 * So that same could be used on the jsp to show corresponding sections.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customLoad(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		
		EditClientStatusActionForm clientForm = (EditClientStatusActionForm)form;
		Context context =(Context)request.getAttribute(Constants.CONTEXT);
		context.addBusinessResults(ClientConstants.CLIENT_STATUS_CHANGE , (Client)SessionUtils.getAttribute(ClientConstants.OLDCLIENT , request.getSession()));
		clientForm.setStatusId(null); 
		clientForm.setFlagId(null);
		this.clearActionForm(form);
		return null;
	}
	/** 
	  * This method is called to clear action form values, whenever a fresh request to chnage status comes in
	  * This is necessary because action form is stored in session
	  * @param mapping indicates action mapping defined in struts-config.xml 
	  * @param form The form bean associated with this action
	  */		 
	private void clearActionForm(ActionForm form){
		EditClientStatusActionForm clientForm = (EditClientStatusActionForm)form;
		clientForm.setCustomerNote(new CustomerNote());
		clientForm.setSelectedItems(null);
		 
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
	public ActionForward customPreview(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		EditClientStatusActionForm clientForm = (EditClientStatusActionForm)form;
		EditClientStatus clientStatusVo=(EditClientStatus)context.getValueObject();
		if(clientStatusVo.getStatusId().shortValue()!=CustomerConstants.CLIENT_CANCELLED && clientStatusVo.getStatusId().shortValue()!=CustomerConstants.CLIENT_CLOSED && (clientStatusVo.getFlagId()!=null||clientStatusVo.getFlagId().shortValue()==0)){
			clientStatusVo.setFlagId(null);
			clientForm.setFlagId(null);
		}		
		
		return null;
	}
	
	/**
	 * No need to override.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 *//*
	public ActionForward customValidate(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		EditClientStatusActionForm clientForm = (EditClientStatusActionForm)form;
		//clientForm.setSelectedItems(null); 
		//String[] tempSelectedItems = clientForm.getSelectedItems();
		for(int i=0;i<clientForm.getSelectedItems().length ;i++){
			//if an already checked fee is unchecked then the value set to 0
			System.out.println("i:    "+i);
			System.out.println("******************REquest PRM: "+request.getParameter("selectedItems["+i+"]"));
			System.out.println("******************SELECETED PRM: "+clientForm.getSelectedItems()[i]);
			System.out.println("******************SELECETED LENGTH: "+clientForm.getSelectedItems().length);
			if(request.getParameter("selectedItems["+i+"]") == null && (clientForm.getSelectedItems()[i] == null ||(clientForm.getSelectedItems()[i].equals("0")) )){
				System.out.println("******************INSIDE IF");
				//if(clientForm.getSelectedItems()[i] == null || (clientForm.getSelectedItems()[i].equals("0"))){
					clientForm.getSelectedItems()[i]="0";
				//}
						

			}
			if(request.getParameter("selectedItems["+i+"]") == null){
				System.out.println("******************INSIDE IF");
				clientForm.setSelectedItems(clientForm.getSelectedItems());
				System.out.println("****************** INSIDE IF SELECETED PRM: "+clientForm.getSelectedItems()[i]);
				System.out.println("******************INSIDE IF SELECETED LENGTH: "+clientForm.getSelectedItems().length);		

			}

		}
		//clientForm.setSelectedItems(clientForm.getSelectedItems());
		return null;
	}
	*/
	/**
	 * No need to override.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customPrevious(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		EditClientStatusActionForm clientForm = (EditClientStatusActionForm)form;
		clientForm.setSelectedItems(null); 
		
		return null;
	}
	
}
