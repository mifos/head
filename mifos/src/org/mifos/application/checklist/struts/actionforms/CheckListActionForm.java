/**

 * CheckListActionForm  version: 1.0



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

package org.mifos.application.checklist.struts.actionforms;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.application.accounts.util.valueobjects.AccountCheckList;
import org.mifos.application.checklist.util.resources.CheckListConstants;
import org.mifos.application.checklist.util.valueobjects.CheckListDetail;
import org.mifos.application.checklist.util.valueobjects.CustomerCheckList;
import org.mifos.application.customer.util.valueobjects.CustomerState;
import org.mifos.framework.business.util.helpers.MethodNameConstants;

/**
 * CheckListActionForm class contains all getter and setter methods for
 * CheckList Module
 */

public class CheckListActionForm extends ValidatorActionForm {

	/** this is the level id of corresponding category */
	private Short categoryId;

	/** this is the product type */
	private Short productTypeID;

	/** status of the corresponding category */
	private java.lang.Short statusId;

	/**
	 * previousStatusId stores previous state in edit page
	 */
	private java.lang.Short previousStatusId;

	/** checklist id */
	private String checklistId;

	/** The value of the simple checklistName property. */
	private String checklistName;

	/** The value of the simple checklistStatus property. */
	private String checklistStatus;

	/** This property holds the value of textarea */
	private String text;

	/**
	 * The value of the checklistDetailSet which takes all the currentItems of
	 * checklist
	 */
	private java.util.Set<CheckListDetail> checklistDetailSet;

	/** type represents product type */
	private String type;

	/** this is 0 when it is customerchecklist and 1 when it is productchecklist */
	private String typeId;

	/** status represents displayed when moving into status of product */
	private String status;

	/** status of checklist */
	private String statusOfCheckList;
	
	public String input;	

	/**
	 * Map is used to retrieve all the checkbox currentItems with keys as number of
	 * checkboxes and currentItems as checkbox value and this map is used to accept
	 * currentItems whenever he creates checklist
	 */
	private Map<String, String> currentItems=null;

	/**
	 * this map is used to accept currentItems whenever the user comes back from
	 * preview button
	 */
	private Map<String, String> previousItems=null;

	/** The value of the customerChecklistSet one-to-many association. */
	private CustomerCheckList customerChecklist;

	/** The value of the accountCheckList one-to-many association. */
	private AccountCheckList accountCheckList;
	
	public CheckListActionForm()
	{		
		checklistDetailSet = null;	
		currentItems=new LinkedHashMap<String, String>();
		previousItems=new LinkedHashMap<String, String>();
	}

	/**
	 * @return Returns the previousStatusId.
	 */
	public java.lang.Short getPreviousStatusId() {
		return previousStatusId;
	}

	/**
	 * @param previousStatusId
	 *            The previousStatusId to set.
	 */
	public void setPreviousStatusId(java.lang.Short previousStatusId) {
		this.previousStatusId = previousStatusId;
	}

	/**
	 * @return Returns the text.
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            The text to set.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Sets the category type
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Returns the category type
	 * 
	 * @return java.lang.String
	 */
	public String getType() {
		return this.type;
	}

	/** Sets an instance of Map */
	public void setcurrentItems(Map<String, String> currentItems) {
		this.currentItems = currentItems;
	}

	/**
	 * Returns an instance of Map
	 * 
	 * @return Map<String,String>
	 */
	public Map<String, String> getcurrentItems() {
		return this.currentItems;
	}

	/**
	 * Sets Key Value Pair of Map
	 * 
	 * @param key
	 * @param value
	 */
	public void setValue(String key, String value) {
		getcurrentItems().put(key, value);
	}

	/**
	 * Returns the value of the object based on key of Map
	 * 
	 * @return java.lang.String
	 */
	public String getValue(String key) {
		return getcurrentItems().get(key);
	}

	/**
	 * Sets the CheckListName
	 * 
	 * @param checklistName
	 */
	public String getChecklistName() {
		return checklistName;
	}

	/**
	 * Returns the CheckListName
	 * 
	 * @return java.lang.String
	 */
	public void setChecklistName(String checklistName) {		
		this.checklistName = checklistName;
	}

	/**
	 * Sets the checklistStatus
	 * 
	 * @param checklistName
	 */
	public String getChecklistStatus() {
		return checklistStatus;
	}

	/**
	 * Returns the checklistStatus
	 * 
	 * @param java.lang.String
	 */
	public void setChecklistStatus(String checklistStatus) {
		this.checklistStatus = checklistStatus;
	}

	/**
	 * This method gets the currentItems out of the map and sets in the
	 * checklistDetailSet
	 */

	public java.util.Set getChecklistDetailSet() {
		Map<String, String> map = getcurrentItems();
		Map<String, String> map2 = getpreviousItems();		
		checklistDetailSet = new LinkedHashSet<CheckListDetail>();
		if (map2 != null ) {
			Set<String> numberOfPreviousItems = map2.keySet();
			Object[] prevArray = new Object[map2.size()];
			int count=0;
			Iterator keyIter = numberOfPreviousItems.iterator();
			while (keyIter.hasNext()) {
				int key = Integer.parseInt(keyIter.next().toString());  // Get the next key.			      		       
			       prevArray[count]= key;
			       count++;
			}			
			Arrays.sort(prevArray);
			for (int i = 0; i < prevArray.length; i++) {				
			       String value = map2.get(String.valueOf(prevArray[i])).toString();  // Get the value for that key.			    
			       CheckListDetail checkListDetail = new CheckListDetail();			       
			       checkListDetail.setDetailText(value);
			       checklistDetailSet.add(checkListDetail);
			}			
			map2.clear();
		}
		if (map != null ) {
			Set<String> numberOfItems = map.keySet();
			int[] presentArray = new int[map.size()];
			int count=0;
			Iterator keyIter = numberOfItems.iterator();
			while (keyIter.hasNext()) {
			       int key = Integer.parseInt(keyIter.next().toString());  // Get the next key.			    
			       presentArray[count]= key;			    
			       count++;
			}			
			Arrays.sort(presentArray);			
			for (int i = 0; i < presentArray.length; i++) {				
			       String value = map.get(String.valueOf(presentArray[i])).toString();  // Get the value for that key.			
			       CheckListDetail checkListDetail = new CheckListDetail();				       
				   checkListDetail.setDetailText(value);
				   checklistDetailSet.add(checkListDetail);
			      }
				map.clear();		
		}		
		return checklistDetailSet;
	}

	/**
	 * @return Returns the checklistId.
	 */
	public String getChecklistId() {
		return checklistId;
	}

	/**
	 * @param checklistId
	 *            The checklistId to set.
	 */
	public void setChecklistId(String checklistId) {
		this.checklistId = checklistId;
	}

	/**
	 * @return Returns the categoryId.
	 */
	public Short getCategoryId() {

		return categoryId;

	}

	/**
	 * @param categoryId
	 *            The categoryId to set.
	 */
	public void setCategoryId(Short categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * This function returns the status
	 * 
	 * @return Returns the status.
	 */

	public String getStatus() {
		return status;
	}

	/**
	 * This function sets the status
	 * 
	 * @param status
	 *            the status to set.
	 */

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * This function returns the typeId
	 * 
	 * @return Returns the typeId.
	 */

	public String getTypeId() {		
		return typeId;
	}

	/**
	 * This function sets the typeId
	 * 
	 * @param typeId
	 *            the typeId to set.
	 */

	public void setTypeId(String typeId) {		
		this.typeId = typeId;
	}

	/**
	 * This function sets the checklistDetailSet
	 * 
	 * @param checklistDetailSet
	 *            the checklistDetailSet to set.
	 */

	public void setChecklistDetailSet(
			java.util.Set<CheckListDetail> checklistDetailSet) {
		this.checklistDetailSet = checklistDetailSet;
	}

	/**
	 * @return Returns the customerChecklist.
	 */
	public CustomerCheckList getCustomerChecklist() {
		if (typeId != null)
			if (typeId.equals("0")) {
				customerChecklist = new CustomerCheckList();
				CustomerState customerState = new CustomerState();

				if (status!= null && (!status.equalsIgnoreCase(""))) {
					Short state = Short.valueOf(status);
					customerState.setStatusId(state);
				}
				if (categoryId != null) {
					customerChecklist.setLevelId(categoryId);
				}
				customerChecklist.setCustomerState(customerState);
			}
		return customerChecklist;
	}

	/**
	 * @param customerChecklist
	 *            The customerChecklist to set.
	 */
	public void setCustomerChecklist(CustomerCheckList customerChecklist) {
		this.customerChecklist = customerChecklist;
	}

	/**
	 * @return Returns the statusId.
	 */
	public java.lang.Short getStatusId() {
		return statusId;
	}

	/**
	 * @param statusId
	 *            The statusId to set.
	 */
	public void setStatusId(java.lang.Short statusId) {
		this.statusId = statusId;
	}

	/**
	 * @return Returns the productTypeID.
	 */
	public Short getProductTypeID() {
		return productTypeID;
	}

	/**
	 * @param productTypeID
	 *            The productTypeID to set.
	 */
	public void setProductTypeID(Short productTypeID) {
		this.productTypeID = productTypeID;
	}

	/**
	 * @return Returns the accountCheckList.
	 */
	public AccountCheckList getAccountCheckList() {
		accountCheckList = new AccountCheckList();
		if (typeId != null)
			if (typeId.equals("1")) {
				if (status!= null && (!status.equalsIgnoreCase(""))) {
					accountCheckList.setAccountStatusId(Short.valueOf(getStatus()));
				}
			}
		accountCheckList.setAccountTypeId(categoryId);
		return accountCheckList;
	}

	/**
	 * @param accountCheckList
	 *            The accountCheckList to set.
	 */
	public void setAccountCheckList(AccountCheckList accountCheckList) {
		this.accountCheckList = accountCheckList;
	}

	/**
	 * @return Returns the previousItems.
	 */
	public Map<String, String> getpreviousItems() {
		return previousItems;
	}

	/**
	 * @param previousItems
	 *            The previousItems to set.
	 */
	public void setpreviousItems(Map<String, String> previousItems) {
		this.previousItems = previousItems;
	}

	/**
	 * Sets Key Value Pair of Map
	 * 
	 * @param key
	 * @param value
	 */
	public void setMyvalue(String key, String value) {
		getpreviousItems().put(key, value);
	}

	/**
	 * Returns the value of the object based on key of Map
	 * 
	 * @return java.lang.String
	 */
	public String getMyvalue(String key) {
		return getpreviousItems().get(key);
	}

	/**
	 * @return Returns the statusOfCheckList.
	 */
	public String getStatusOfCheckList() {
		return statusOfCheckList;
	}

	/**
	 * @param statusOfCheckList
	 *            The statusOfCheckList to set.
	 */
	public void setStatusOfCheckList(String statusOfCheckList) {
		this.statusOfCheckList = statusOfCheckList;
	}
	
	public String getInput() {
		return this.input;
	}

	public void setInput(String input) {
		this.input = input;
	}
	
	public ActionErrors validate(ActionMapping mapping,HttpServletRequest request) 
	{
		ActionErrors errors = null;
		String methodCalled = request.getParameter("method");		
		if (null != methodCalled) 
		{
			if ("validate".equals(methodCalled)|| "load".equals(methodCalled)	|| "loadParent".equals(methodCalled) || "previous".equals(methodCalled) || "create".equals(methodCalled) || "loadall".equals(methodCalled) || "get".equals(methodCalled) || "manage".equals(methodCalled) || "update".equals(methodCalled)) {				
			} 
			else
			{
				errors = super.validate(mapping,request);				
				if(errors==null) errors = new ActionErrors();
				if (currentItems.size() == 0 && previousItems.size() == 0) {				
					errors.add(CheckListConstants.MANDATORY_ITEM,new ActionMessage(CheckListConstants.MANDATORY_ITEM,CheckListConstants.ITEM));				
				}
				if (currentItems.size() != 0) {
					Set<String> set = currentItems.keySet();
					for (String object : set) {
						String item = new String((String) currentItems.get(object));
						if (item.length() > 250) {
							errors.add(CheckListConstants.ITEM_LENGTH,new ActionMessage(CheckListConstants.ITEM_LENGTH,CheckListConstants.ITEM,CheckListConstants.ITEM_SIZE));
							request.setAttribute(Globals.ERROR_KEY, errors);
							return errors;
						}
					}				
				}
				if (previousItems.size() != 0) {
					Set<String> set = previousItems.keySet();
					for (String object : set) {
						String str = new String((String) previousItems.get(object));
						if (str.length() > 250) {
							errors.add(CheckListConstants.ITEM_LENGTH,new ActionMessage(CheckListConstants.ITEM_LENGTH,CheckListConstants.ITEM,CheckListConstants.ITEM_SIZE));
							request.setAttribute(Globals.ERROR_KEY, errors);
							return errors;
						}
					}
					
				}
			}					
		}
		if (null != errors && !errors.isEmpty()) {			
			request.setAttribute("methodCalled", methodCalled);
			request.setAttribute(Globals.ERROR_KEY, errors);
		}
		return errors;
	}
	
	/**
	 * The reset method is called before setting the currentItems into the actionform.In this method,
	 * the currentItems are cleared and the default currentItems are set.
	 *  
	 */
	
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		if(request.getParameter(MethodNameConstants.METHOD) !=null && request.getParameter(MethodNameConstants.METHOD).equals(MethodNameConstants.LOAD)) 
		{
			
				this.checklistName=null;
				this.categoryId=null;
				this.checklistId=null;
				this.checklistStatus=null;								
				this.previousStatusId=null;
				this.productTypeID=null;
				this.statusId=null;
				this.statusOfCheckList=null;				
				this.type=null;
				this.status=null;
		}
		else if(request.getParameter(MethodNameConstants.METHOD) !=null && request.getParameter(MethodNameConstants.METHOD).equals("loadParent"))
		{
			this.status=null;
		}
	}

}
