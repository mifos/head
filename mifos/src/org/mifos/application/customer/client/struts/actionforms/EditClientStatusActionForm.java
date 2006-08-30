/**

 * EditClientStatusActionForm.java    version: xxx

 

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

package org.mifos.application.customer.client.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.customer.util.valueobjects.CustomerNote;
import org.mifos.framework.struts.actionforms.MifosActionForm;
import org.mifos.framework.util.helpers.Constants;

/**
 * This class acts as ActionForm for edit client status module.
 */
public class EditClientStatusActionForm extends MifosActionForm {

	/**
	 * 
	 */
	public EditClientStatusActionForm() {
		customerNote = new CustomerNote();
		selectedItems = new String[10];
		
	}
	
	private String customerId;
	
	/**The display name of the customer*/
	private String displayName;
	
	/**The version number of the customer*/
	private String versionNo;
	
	/**The status id of the customer*/
	private String statusId;
	
	/**The version number of the customer*/
	private String flagId;
	
	/**Denotes the list of selected checkboxes on the status preview page*/
	private String[] selectedItems;
	
	private String editInfo;
	 private int listSize;
	/**
	 * This would be the current date and if the status is being changed to active
	 * customerActivationdate in customer table would be set with this value.
	 */
	private String customerActivationDate;
	
	/**
	 * This field denotes the current customerState and comes as request
	 * parameter when clicked on edit status link on details page.
	 */
	private String currentStatusId;

	/**
	 * @return Returns the customerId}.
	 */
	public String getCustomerId() {
		return customerId;
	}
	/**Denotes the note object for the client.*/
	private CustomerNote customerNote;

	/**
	 * @param customerId The customerId to set.
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	/**
	 * @return Returns the customerActivationDate}.
	 */
	public String getCustomerActivationDate() {
		return customerActivationDate;
	}

	/**
	 * @param customerActivationDate The customerActivationDate to set.
	 */
	public void setCustomerActivationDate(String customerActivationDate) {
		this.customerActivationDate = customerActivationDate;
	}

	

	/**
	 * Method which returns the currentStatusId	
	 * @return Returns the currentStatusId.
	 */
	public String getCurrentStatusId() {
		return currentStatusId;
	}

	/**
	 * Method which sets the currentStatusId
	 * @param currentStatusId The currentStatusId to set.
	 */
	public void setCurrentStatusId(String currentStatusId) {
		this.currentStatusId = currentStatusId;
	}

	
	/**
	 * Method which returns the displayName	
	 * @return Returns the displayName.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Method which sets the displayName
	 * @param displayName The displayName to set.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Method which returns the versionNo	
	 * @return Returns the versionNo.
	 */
	public String getVersionNo() {
		return versionNo;
	}

	/**
	 * Method which sets the versionNo
	 * @param versionNo The versionNo to set.
	 */
	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}

	/**
	 * Method which returns the flagId	
	 * @return Returns the flagId.
	 */
	public String getFlagId() {
		return flagId;
	}

	/**
	 * Method which sets the flagId
	 * @param flagId The flagId to set.
	 */
	public void setFlagId(String flagId) {
		this.flagId = flagId;
	}

	/**
	 * Method which returns the statusId	
	 * @return Returns the statusId.
	 */
	public String getStatusId() {
		return statusId;
	}

	/**
	 * Method which sets the statusId
	 * @param statusId The statusId to set.
	 */
	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	/**
	 * Method which returns the customerNote	
	 * @return Returns the customerNote.
	 */
	public CustomerNote getCustomerNote() {
		return customerNote;
	}

	/**
	 * Method which sets the customerNote
	 * @param customerNote The customerNote to set.
	 */
	public void setCustomerNote(CustomerNote customerNote) {
		this.customerNote = customerNote;
	}

	/**
	 * Method which returns the selectedItems	
	 * @return Returns the selectedItems.
	 */
	public String[] getSelectedItems() {
		return selectedItems;
	}

	/**
	 * Method which sets the selectedItems
	 * @param selectedItems The selectedItems to set.
	 */
	public void setSelectedItems(String[] selectedItems) {
		this.selectedItems = selectedItems;
	}

	/**
	 * Method which returns the editInfo	
	 * @return Returns the editInfo.
	 */
	public String getEditInfo() {
		return editInfo;
	}

	/**
	 * Method which sets the editInfo
	 * @param editInfo The editInfo to set.
	 */
	public void setEditInfo(String editInfo) {
		this.editInfo = editInfo;
	}
/**
	 * It needs to validate certain things based on the state in which
	 * client is being created.  
	 * @param mapping
	 * @param request
	 * @return
	 */
	public final ActionErrors customValidate(ActionMapping mapping,
				HttpServletRequest request) {
		String methodCalled= request.getParameter("method");
		if(null !=methodCalled) {
			if(	CustomerConstants.METHOD_CANCEL.equals(methodCalled) || 
				CustomerConstants.METHOD_UPDATE_STATUS.equals(methodCalled) ||
				CustomerConstants.METHOD_LOAD.equals(methodCalled) ){
				request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
			}
			if(CustomerConstants.METHOD_PREVIEW.equals(methodCalled)){
				ActionErrors errors = new ActionErrors();
				if(!ValidateMethods.isNullOrBlank(statusId)){
					if((Short.valueOf(statusId).shortValue() == CustomerConstants.CLIENT_CLOSED || Short.valueOf(statusId).shortValue() == CustomerConstants.CLIENT_CANCELLED) && org.mifos.application.accounts.util.helpers.ValidateMethods.isNullOrBlank(flagId)){
					  errors.add(ClientConstants.FLAG_EXCEPTION,new ActionMessage(ClientConstants.FLAG_EXCEPTION));
					  return errors;
					}
				}
			}
			else if(CustomerConstants.METHOD_UPDATE.equals(methodCalled)){
				ActionErrors errors = new ActionErrors();
				Object obj=request.getParameter("listSize");
				if(request.getParameter("selectedItems")==null){
					selectedItems=null;
				}
				System.out.println("################## LIST SIZE"+obj);
				//System.out.println("################## SELECTED ITEMS LENGTH: "+selectedItems.length);
				if(obj!=null){
					int totalItems = new Integer(obj.toString()).intValue();
					System.out.println("################## TOTAL ITEMS SIZE:   "+ totalItems);
					System.out.println("################## SELECTED ITEMS");
					/*for(int i=0;i<selectedItems.length;i++){
						System.out.println(selectedItems[i]);
					}*/
					
					
					if((totalItems>0 && selectedItems==null) ||(selectedItems!=null && totalItems!=selectedItems.length)){
						errors.add(CustomerConstants.INCOMPLETE_CHECKLIST_EXCEPTION,new ActionMessage(CustomerConstants.INCOMPLETE_CHECKLIST_EXCEPTION));
						
					}
					
				}

				request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
				return errors;
			}
			
			
	   }
		return null;
	}

/**
 * @return Returns the listSize.
 */
public int getListSize() {
	return listSize;
}

/**
 * @param listSize The listSize to set.
 */
public void setListSize(int listSize) {
	this.listSize = listSize;
}
/***
 * Method to reset the values of checkboxes if selected
 *//*
public void reset(ActionMapping mapping, HttpServletRequest request) {

	if (request.getParameter("statusId")!=null ){
	
		for(int i=0;i<selectedItems.length ;i++){
			//if an already checked fee is unchecked then the value set to 0
			if(request.getParameter("selectedItems["+i+"]") == null){
				selectedItems[i]="0";

			}

		}
	}
	 super.reset(mapping, request);
}
	*/

}
