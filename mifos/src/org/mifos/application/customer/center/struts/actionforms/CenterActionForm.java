/**

* CenterActionForm    version: 1.0



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
package org.mifos.application.customer.center.struts.actionforms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.util.valueobjects.CustomerAccount;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.customer.util.valueobjects.CustomFieldDefinition;
import org.mifos.application.customer.util.valueobjects.CustomerAddressDetail;
import org.mifos.application.customer.util.valueobjects.CustomerCustomField;
import org.mifos.application.customer.util.valueobjects.CustomerMeeting;
import org.mifos.application.customer.util.valueobjects.CustomerNote;
import org.mifos.application.customer.util.valueobjects.CustomerPosition;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeeConstants;
import org.mifos.application.fees.util.valueobjects.FeeMaster;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.struts.actionforms.MifosSearchActionForm;
import org.mifos.framework.struts.plugin.helper.EntityMasterConstants;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This class denotes the form bean for the center. It consists of the fields for which the user inputs values
 */

public class CenterActionForm extends  MifosSearchActionForm {


	/***
	 * Constructor: All the composition objects are intialized within the contructor
	 *
	 */
	public CenterActionForm(){
		customerAddressDetail = new CustomerAddressDetail();
		customFieldSet =new ArrayList<CustomerCustomField>();
		customerAccount = new CustomerAccount();
		customerMeeting = new CustomerMeeting();
		office = new Office();
		customerNote = new CustomerNote();
		customerPositions = new ArrayList<CustomerPosition>();
		selectedItems = new String[10];
		selectedFeeAmntList = new String[10];
		logger = MifosLogManager.getLogger(LoggerConstants.CENTERLOGGER);
	}

	private Office office;

	/**Denotes the center name*/
	private String displayName;

	/**Denotes the center id*/
	private String customerId;

	/**Denotes the globalCustomerId of the center*/
	private String globalCustNum;

	/**Denotes the id of the loan officer assigned to the center*/
	private String loanOfficerId;

	/**Denotes the id of the center in the previous system*/
	private String externalId;

	/**Denotes the type of collection sheet*/
	private String collectionSheetId;

	/**Denotes the type of collection sheet*/
	private String feeId;
	/**Denotes the status of the center*/
	private short statusId;
	/**Denotes the address of the center*/
	private CustomerAddressDetail customerAddressDetail;
	/**Denotes the custom field values of the center*/
	private List <CustomerCustomField> customFieldSet  ;

	/**Denotes the customer positions for the center*/
	private List <CustomerPosition> customerPositions  ;

	/**Denotes the accounts of the center*/
	private CustomerAccount customerAccount;

	/**Denotes the meeting of the center*/
	private CustomerMeeting customerMeeting;

	/**Dentoes the notes associated from the status pages*/
	private CustomerNote customerNote;

	/**Denotes the method being called in the action class*/
	private String method;

	/**Denotes the edit button on the preview pages*/
	private String editButton;

	/**Denotes the cancel button on the preview pages*/
	private String cancelButton;

	/**Denotes the list of selected checkboxes on the status preview page*/
	private String[] selectedItems;

	/**Denotes the version number*/
	private String versionNo;

	/**Denotes a list of additional fees */
	private List<FeeMaster> selectedFeeList = new ArrayList<FeeMaster>();

	/**Denotes a list of administrative fees */
	private List<FeeMaster> adminFeeList = new ArrayList<FeeMaster>();

	/**Denotes the mfi joining date property*/
	private String mfiJoiningDate;

	/**Denotes the amounts for the list of additional fees. These will be set as hidden variables*/
	private String[] selectedFeeAmntList ;

	/**An instance of the logger which is used to log statements */
	private MifosLogger logger;
	 private int listSize;
	 private String initialSearch;
	 private String[] fieldTypeList;
	

	/**
	 * Method which returns the listSize
	 * @return Returns the listSize.
	 */
	public int getListSize() {
		return listSize;
	}

	/**
	 * Method which sets the listSize
	 * @param listSize The listSize to set.
	 */
	public void setListSize(int listSize) {
		this.listSize = listSize;
	}

	/**
	 * Method which returns the editButton
	 * @return Returns the editButton.
	 */
	public String getEditButton() {
		return editButton;
	}

	/**
	 * Method which sets the editButton
	 * @param editButton The editButton to set.
	 */
	public void setEditButton(String editButton) {
		this.editButton = editButton;
	}

	/**
	 * Method which returns the collectionSheetId
	 * @return Returns the collectionSheetId.
	 */
	public String getCollectionSheetId() {
		return collectionSheetId;
	}

	/**
	 * Method which sets the collectionSheetId
	 * @param collectionSheetId The collectionSheetId to set.
	 */
	public void setCollectionSheetId(String collectionSheetId) {
		this.collectionSheetId = collectionSheetId;
	}

	/**
	 * Method which returns the externalId
	 * @return Returns the externalId.
	 */
	public String getExternalId() {
		return externalId;
	}

	/**
	 * Method which sets the externalId
	 * @param externalId The externalId to set.
	 */
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	/**
	 * Method which returns the loanOfficerId
	 * @return Returns the loanOfficerId.
	 */
	public String getLoanOfficerId() {
		return loanOfficerId;
	}

	/**
	 * Method which sets the loanOfficerId
	 * @param loanOfficerId The loanOfficerId to set.
	 */

	public void setLoanOfficerId(String loanOfficerId) {
		this.loanOfficerId = loanOfficerId;
	}
	
	public void setCustomField(int index , short fieldId)
	{

		customFieldSet.get(index).setFieldId(fieldId);

	}
	public CustomerCustomField getCustomField(int index)
	{
		while(index>=customFieldSet.size()){
			customFieldSet.add(new CustomerCustomField());
		}
		return (CustomerCustomField)(customFieldSet.get(index));
	}

	/**
	 * Method which returns the customFieldSet
	 * @return Returns the customFieldSet.
	 */

	public Set getCustomFieldSet(){
		//conversion of list to set
		return new HashSet(this.customFieldSet);
	}


	/**
	 * Method which sets the customFieldSet
	 * @param customFieldSet The customFieldSet to set.
	 */
	public void setCustomFieldSet(List<CustomerCustomField> customFieldSet) {
		this.customFieldSet = customFieldSet;
	}
	/**
	 * Method which returns the method
	 * @return Returns the method.
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Method which sets the method
	 * @param method The method to set.
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * Method which returns the office
	 * @return Returns the office.
	 */
	public Office getOffice() {
		return office;
	}

	/**
	 * Method which sets the office
	 * @param office The office to set.
	 */
	public void setOffice(Office office) {
		this.office = office;
	}

	/**
	 * Method which returns the feeId
	 * @return Returns the feeId.
	 */
	public String getFeeId() {
		return feeId;
	}

	/**
	 * Method which sets the feeId
	 * @param feeId The feeId to set.
	 */
	public void setFeeId(String feeId) {
		this.feeId = feeId;
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
	 * Method which returns the customerAddressDetail
	 * @return Returns the customerAddressDetail.
	 */
	public CustomerAddressDetail getCustomerAddressDetail() {
		return customerAddressDetail;
	}

	/**
	 * Method which sets the customerAddressDetail
	 * @param customerAddressDetail The customerAddressDetail to set.
	 */
	public void setCustomerAddressDetail(CustomerAddressDetail customerAddressDetail) {
		this.customerAddressDetail = customerAddressDetail;
	}

	/**
	 * Method which returns the customerAccount
	 * @return Returns the customerAccount.
	 */
	public CustomerAccount getCustomerAccount() {
		return customerAccount;
	}

	/**
	 * Method which sets the customerAccount
	 * @param customerAccount The customerAccount to set.
	 */
	public void setCustomerAccount(CustomerAccount customerAccount) {
		this.customerAccount = customerAccount;
	}



	/**
	 * Method which returns the customerMeeting
	 * @return Returns the customerMeeting.
	 */
	public CustomerMeeting getCustomerMeeting() {
		return customerMeeting;
	}

	/**
	 * Method which sets the customerMeeting
	 * @param customerMeeting The customerMeeting to set.
	 */
	public void setCustomerMeeting(CustomerMeeting customerMeeting) {

		this.customerMeeting = customerMeeting;
	}



	/**
	 * Method which returns the globalCustNum
	 * @return Returns the globalCustNum.
	 */
	public String getGlobalCustNum() {
		return globalCustNum;
	}

	/**
	 * Method which sets the globalCustNum
	 * @param globalCustNum The globalCustNum to set.
	 */
	public void setGlobalCustNum(String globalCustNum) {

		this.globalCustNum = globalCustNum;

	}

	/**
	 * Method which returns the cancelButton
	 * @return Returns the cancelButton.
	 */
	public String getCancelButton() {
		return cancelButton;
	}

	/**
	 * Method which sets the cancelButton
	 * @param cancelButton The cancelButton to set.
	 */
	public void setCancelButton(String cancelButton) {
		this.cancelButton = cancelButton;
	}

	/**
	 * Method which returns the customerPositions
	 * @return Returns the customerPositions.
	 */
	public Set getCustomerPositions() {
		//conversion of list to set
		return new HashSet(this.customerPositions);
	}

	/**
	 * Method which sets the customerPositions
	 * @param customerPositions The customerPositions to set.
	 */
	public void setCustomerPositions(List<CustomerPosition> customerPositions) {
		this.customerPositions = customerPositions;
	}
	/***
	 * Method which obtains the customer position object at a particular index within the list
	 * @param index
	 * @return
	 */
	public CustomerPosition getCustomerPosition(int index) {

		while(index>=customerPositions.size()){
			customerPositions.add(new CustomerPosition());
		}
		return (CustomerPosition)customerPositions.get(index);


	}

	/**
	 * Method which returns the statusId
	 * @return Returns the statusId.
	 */
	public short getStatusId() {
		return statusId;
	}

	/**
	 * Method which sets the statusId
	 * @param statusId The statusId to set.
	 */
	public void setStatusId(short statusId) {
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
	 * Method which returns the customerId
	 * @return Returns the customerId.
	 */
	public String getCustomerId() {
		return customerId;
	}

	/**
	 * Method which sets the customerId
	 * @param customerId The customerId to set.
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
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
	 * Method which returns the selectedFeeList
	 * @return Returns the selectedFeeList.
	 */
	public List<FeeMaster> getSelectedFeeList() {
		return selectedFeeList;
	}

	/**
	 * Method which sets the selectedFeeList
	 * @param selectedFeeList The selectedFeeList to set.
	 */
	public void setSelectedFeeList(List<FeeMaster> selectedFeeList) {
		this.selectedFeeList = selectedFeeList;
	}
	public void setSelectedFee(int index , short fieldId)
	{

		selectedFeeList.get(index).setFeeId(fieldId);

	}
	public FeeMaster getSelectedFee(int index)
	{
		while(index>=selectedFeeList.size()){
			selectedFeeList.add(new FeeMaster());
		}
		return (FeeMaster)selectedFeeList.get(index);
	}

	/**
	 * Method which returns the selectedFeeAmntList
	 * @return Returns the selectedFeeAmntList.
	 */
	public String[] getSelectedFeeAmntList() {
		return selectedFeeAmntList;
	}

	/**
	 * Method which sets the selectedFeeAmntList
	 * @param selectedFeeAmntList The selectedFeeAmntList to set.
	 */
	public void setSelectedFeeAmntList(String[] selectedFeeAmntList) {
		this.selectedFeeAmntList = selectedFeeAmntList;
	}

	/**
	 * Method which returns the adminFeeList
	 * @return Returns the adminFeeList.
	 */
	public List<FeeMaster> getAdminFeeList() {
		return adminFeeList;
	}

	/**
	 * Method which sets the adminFeeList
	 * @param adminFeeList The adminFeeList to set.
	 */
	public void setAdminFeeList(List<FeeMaster> adminFeeList) {
		this.adminFeeList = adminFeeList;
	}

	/***
	 * Method to obtain the admin fees at a particular index
	 * @param index
	 * @return
	 */
	public FeeMaster getAdminFee(int index)
	{
		while(index>=adminFeeList.size()){
			adminFeeList.add(new FeeMaster());
		}
		return (FeeMaster)adminFeeList.get(index);
	}
	/**
	 * This method calls the validation only if the method called is preview.
	 * If the method called is preview from a  manage page then the loan officer required state, is checked
	 * only if the status of the center is active
	 * @param mapping
	 * @param request
	 * @return
	 * @throws ApplicationException
	 */
	public ActionErrors customValidate(ActionMapping mapping,
			HttpServletRequest request) throws ApplicationException {
		boolean duplicate = false;
		String methodCalled= request.getParameter("method");
		logger.debug("Action form method called; "+ methodCalled );
		//For all other methods other than preview, validation is skipped
		if(null !=methodCalled) {
			logger.info("In Center Action Form Custom Validate");
			if(	CustomerConstants.METHOD_CANCEL.equals(methodCalled) ||
				CustomerConstants.METHOD_CREATE.equals(methodCalled)	||
				CustomerConstants.METHOD_GET.equals(methodCalled) ||
				CustomerConstants.METHOD_LOAD_SEARCH.equals(methodCalled)||
				CustomerConstants.METHOD_LOAD_STATUS.equals(methodCalled)||
				CustomerConstants.METHOD_MANAGE.equals(methodCalled) ||
				CustomerConstants.METHOD_PREVIOUS.equals(methodCalled)||
				CustomerConstants.METHOD_SEARCH_NEXT.equals(methodCalled)||
				CustomerConstants.METHOD_SEARCH_PREV.equals(methodCalled)||
				CustomerConstants.METHOD_UPDATE.equals(methodCalled) ||
				ClientConstants.METHOD_LOAD_MEETING.equals(methodCalled)||
				ClientConstants.METHOD_UPDATE_MEETING.equals(methodCalled)||
				ClientConstants.METHOD_CHOOSE_OFFICE.equals(methodCalled)||
				CustomerConstants.METHOD_GET_DETAILS.equals(methodCalled)||
				CustomerConstants.METHOD_LOAD.equals(methodCalled) ){
				request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
			}
			else if(CustomerConstants.METHOD_PREVIEW.equals(methodCalled) && CenterConstants.INPUT_STATUS.equals(input) ){
				ActionErrors errors = new ActionErrors();
				if(request.getParameter("statusId")==null){
					errors.add(CustomerConstants.STATUS_REQUIRED_EXCEPTION,new ActionMessage(CustomerConstants.STATUS_REQUIRED_EXCEPTION));

				}
				if(ValidateMethods.isNullOrBlank(customerNote.getComment())){
					if(null==errors){
						errors = new ActionErrors();
					}
					errors.add(CustomerConstants.CUSTOMER_STATUS_NOTES_EXCEPTION ,new ActionMessage(CustomerConstants.CUSTOMER_STATUS_NOTES_EXCEPTION));
				}

				request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
				return errors;
			}
			else if(CustomerConstants.METHOD_UPDATE_STATUS.equals(methodCalled)){
				ActionErrors errors = new ActionErrors();
				Object obj=request.getParameter("listSize");
				if(request.getParameter("selectedItems")==null){
					selectedItems=null;
				}
				if(obj!=null){
					int totalItems = new Integer(obj.toString()).intValue();
					if((totalItems>0 && selectedItems==null) ||(selectedItems!=null && totalItems!=selectedItems.length)){
						errors = new ActionErrors();
						errors.add(CustomerConstants.INCOMPLETE_CHECKLIST_EXCEPTION,new ActionMessage(CustomerConstants.INCOMPLETE_CHECKLIST_EXCEPTION));
					}
				
				}

				request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
				return errors;
			}

			else if(CustomerConstants.METHOD_PREVIEW.equals(methodCalled) && CenterConstants.INPUT_CREATE.equals(input) ){

					ActionErrors errors = new ActionErrors();
					if(ValidateMethods.isNullOrBlank(displayName)){
						errors.add(CustomerConstants.NAME_REQUIRED_EXCEPTION,new ActionMessage(CustomerConstants.NAME_REQUIRED_EXCEPTION));

					}
					if(ValidateMethods.isNullOrBlank(loanOfficerId)){
						errors.add(CustomerConstants.LOAN_OFFICER_REQUIRED_EXCEPTION,new ActionMessage(CustomerConstants.LOAN_OFFICER_REQUIRED_EXCEPTION));

					}
					if(customerMeeting.getMeeting()==null){
						if(errors==null)
							errors = new ActionErrors();
						errors.add(CustomerConstants.MEETING_REQUIRED_EXCEPTION,new ActionMessage(CustomerConstants.MEETING_REQUIRED_EXCEPTION));
					}
					checkForMandatoryFields(EntityMasterConstants.Center,errors,request);
					if(new CustomerHelper().isAnyAccountFeesWithoutAmnt(adminFeeList , selectedFeeList)){
						 errors.add(CustomerConstants.INVALID_FEE_AMNT,new ActionMessage(CustomerConstants.INVALID_FEE_AMNT));

					}
					//checking for duplicacy in the list of selected additional fees
					
					checkForDuplicatePeriodicFeeExist(request,errors);
					//validating mandatory custom fields
					errors = validateCustomFields(request ,errors);
					
					return errors;
			}
			else if(CustomerConstants.METHOD_PREVIEW.equals(methodCalled) && CenterConstants.INPUT_MANAGE.equals(input)){
				ActionErrors errors = new ActionErrors();
				checkForMandatoryFields(EntityMasterConstants.Center,errors,request);
					if( statusId != CustomerConstants.INACTIVE_STATE){
						logger.debug("inside preview and manage and active status");
						if(ValidateMethods.isNullOrBlank(loanOfficerId)){
							
							errors.add(CustomerConstants.LOAN_OFFICER_BLANK_EXCEPTION,new ActionMessage(CustomerConstants.LOAN_OFFICER_BLANK_EXCEPTION));
							
						}
					}
					//validating mandatory custom fields
					errors = validateCustomFields(request ,errors);
					return errors;
				}

			else if((CustomerConstants.METHOD_SEARCH).equals(methodCalled)){
				return handleSearchValidations(request);
			}
		}

		return null;
	}
	
	private ActionErrors checkForDuplicatePeriodicFeeExist(HttpServletRequest request, ActionErrors errors) {
		Context context=(Context)SessionUtils.getAttribute(Constants.CONTEXT , request.getSession());
		List<FeeMaster> selectedFeeMaster=((List)context.getSearchResultBasedOnName(CenterConstants.FEES_LIST).getValue());
		   for(FeeMaster selectedFee : selectedFeeList){
			   List<FeeMaster> duplicateselectedFeeList = selectedFeeList;
			   int i=0;
			   for(FeeMaster duplicateFees : duplicateselectedFeeList){
				   if(selectedFee.getFeeId() != null && selectedFee.getFeeId().equals(duplicateFees.getFeeId())){
					   Short feeFrequencyType=null;
					   for(FeeMaster feeMaster : selectedFeeMaster){
						   if(selectedFee.getFeeId().equals(feeMaster.getFeeId()))
						   {
							   feeFrequencyType=feeMaster.getFeeFrequencyTypeId();
							   break;
						   }
					   }
					   if(feeFrequencyType!=null  && feeFrequencyType.equals(FeeFrequencyType.PERIODIC.getValue())){
						   i++;
						   if(i>=2) {
							   errors.add(CustomerConstants.DUPLICATE_FEE_EXCEPTION,new ActionMessage(CustomerConstants.DUPLICATE_FEE_EXCEPTION));
						   	   return errors;
						   }
					   }
				   }
			   }
		   }
		
		return errors;
	}
	
	private ActionErrors validateCustomFields(HttpServletRequest request, ActionErrors errors) {
		Context context=(Context)SessionUtils.getAttribute(Constants.CONTEXT , request.getSession());
		List<CustomFieldDefinition> customFields = (List)context.getSearchResultBasedOnName(CustomerConstants.CUSTOM_FIELDS_LIST).getValue();
		for(int i=0;i<customFieldSet.size();i++){
			for(int j=0;j<customFields.size();j++){
				if(customFieldSet.get(i).getFieldId().shortValue() == customFields.get(j).getFieldId().shortValue() 
					&& customFields.get(j).isMandatory()){
					if(ValidateMethods.isNullOrBlank(customFieldSet.get(i).getFieldValue())){
						if(errors==null){
							errors =new ActionErrors();
						}
						errors.add(CustomerConstants.ERROR_CUSTOMFIELD_REQUIRED,new ActionMessage(CustomerConstants.ERROR_CUSTOMFIELD_REQUIRED));
						
					}
					break;
				}
				
			}
		
		}
		return errors;
	}

	/**
	 * This method is helper method to do data validations before searching for groups.
	 * If user has not entered any search String, it will display error message
	 * @return ActionErrors
	 */
	private ActionErrors handleSearchValidations(HttpServletRequest request){
		ActionErrors errors = null;
		String searchString=getSearchNode("searchString");
		// System.out.println("SEARCH STRING: "+ searchString);

		if(ValidateMethods.isNullOrBlank(searchString)){
			errors = new ActionErrors();
			errors.add(GroupConstants.NO_SEARCH_STRING,new ActionMessage(GroupConstants.NO_SEARCH_STRING));
		}
		request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
		return errors;
	}
	/***
	 * Method to reset the values of checkboxes if selected
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {

		if (request.getParameter("displayName")!=null ){


			for(int i=0;i<adminFeeList.size();i++){
				//if an already checked fee is unchecked then the value set to 0
				if(request.getParameter("adminFee["+i+"].checkedFee")==null){
					adminFeeList.get(i).setCheckedFee(Short.valueOf("0"));

				}

			}
		}
		/*else{
			for(int i=0;i<selectedItems.length ;i++){
				//if an already checked fee is unchecked then the value set to 0
				if(request.getParameter("selectedItems["+i+"]") == null){
					selectedItems[i]="0";

				}

			}
		}*/
		 super.reset(mapping, request);
	}

	/**
	 * Method which returns the mfiJoiningDate
	 * @return Returns the mfiJoiningDate.
	 */
	public String getMfiJoiningDate() {
		return mfiJoiningDate;
	}

	/**
	 * Method which sets the mfiJoiningDate
	 * @param mfiJoiningDate The mfiJoiningDate to set.
	 */
	public void setMfiJoiningDate(String mfiJoiningDate) {
		this.mfiJoiningDate = mfiJoiningDate;
	}

	/**
	 * Method which returns the initialSearch
	 * @return Returns the initialSearch.
	 */
	public String getInitialSearch() {
		return initialSearch;
	}

	/**
	 * Method which sets the initialSearch
	 * @param initialSearch The initialSearch to set.
	 */
	public void setInitialSearch(String initialSearch) {
		this.initialSearch = initialSearch;
	}
	public String[] getFieldTypeList() {
		return fieldTypeList;
	}

	public void setFieldTypeList(String[] fieldTypeList) {
		this.fieldTypeList = fieldTypeList;
	}
}
