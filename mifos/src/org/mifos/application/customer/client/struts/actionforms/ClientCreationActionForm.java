/**

 * ClientCreationActionForm.java    version: xxx



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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
import org.mifos.application.accounts.util.valueobjects.CustomerAccount;
import org.mifos.application.configuration.business.ConfigurationIntf;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.group.util.valueobjects.Group;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.customer.util.valueobjects.CustomFieldDefinition;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerAddressDetail;
import org.mifos.application.customer.util.valueobjects.CustomerCustomField;
import org.mifos.application.customer.util.valueobjects.CustomerDetail;
import org.mifos.application.customer.util.valueobjects.CustomerMeeting;
import org.mifos.application.customer.util.valueobjects.CustomerNameDetail;
import org.mifos.application.customer.util.valueobjects.CustomerNote;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeesConstants;
import org.mifos.application.fees.util.valueobjects.FeeMaster;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.meeting.util.resources.MeetingConstants;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationConstant;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationHelper;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.actionforms.MifosActionForm;
import org.mifos.framework.struts.plugin.helper.EntityMasterConstants;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This class acts as ActionForm for creating/updating a client.
 * @author ashishsm
 *
 */
public class ClientCreationActionForm extends MifosActionForm {
	private ConfigurationIntf labelConfig=MifosConfiguration.getInstance();
	
	/**
	 *
	 */
	public ClientCreationActionForm() {
		customerAddressDetail = new CustomerAddressDetail();
		customerDetail = new CustomerDetail();
		this.customerDetail.setCustomer(new Customer());
		customerNameDetailSet = new ArrayList<CustomerNameDetail>(2);
		customFieldSet =new ArrayList<CustomerCustomField>();
		customerAccount = new CustomerAccount();
		customerMeeting = new CustomerMeeting();
		office = new Office();
		selectedItems = new String[10];
		selectedFeeAmntList = new String[10];
		customerNote= new CustomerNote();
		logger = MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER);
	}

	/**This variable indicates if client being created is part of group or not.*/
	private short isClientUnderGrp;

	/**This variable indicates if client being created is part of group or not.*/
	private String parentGroupId;

	/**This variable indicates if client being created is part of group or not.*/
	private String displayName;

	/**Denotes the customer details like gender , marital status*/
	private List<CustomerNameDetail> customerNameDetailSet;

	/**Denotes the customer details like gender , marital status*/
	private CustomerDetail customerDetail;

	/**Denotes the accounts of the center*/
	private CustomerAccount customerAccount;

	/**Denotes the address of the client*/
	private CustomerAddressDetail customerAddressDetail;

	/**Denotes the custom field values of the client*/
	private List <CustomerCustomField> customFieldSet  ;

	/**Denotes the office of the client*/
	private Office office;

	/**Denotes the meeting of the center*/
	private CustomerMeeting customerMeeting;

	/**Dentoes the notes associated from the status pages*/
	private CustomerNote customerNote;

	/**Denotes the method being called in the action class*/
	private String method;

	/**Denotes the edit button on the preview pages*/
	private String editButton;

	/**Denotes the edit button on the preview pages*/
	private String submitButton;
/**Denotes the edit button on the preview pages*/
	private String submitButton1;

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

	/**Denotes the amounts for the list of additional fees. These will be set as hidden variables*/
	private String[] selectedFeeAmntList ;

	/**Denotes whether a group is trained or not.*/
	private String trained;

	/**Denotes group training date.*/
	private String trainedDate;

	/**Denotes group training date format.*/
	private String trainingDateFormat;

	/** Denotes  clientConfidentiality  */
    private String clientConfidential;

    /**Denotes the globalCustomerId of the group*/
	private String globalCustNum;

	/**Denotes the status id for group */
	private String statusId;

	/**Denotes the status id for group */
	private String flagId;

	/**Denotes the id of the group in the previous system*/
	private String externalId;

	/**Denotes whether client belongs to group or not*/
	private String groupFlag;

	/**Denotes the loan officer id of the client*/
	private String loanOfficerId;
	
	private String customerFormedById;
	
	/**Denotes the loan officer id of the client*/
	private String governmentId;

	/**Denotes the loan officer id of the client*/
	private String nextOrPreview;

	/**Denotes the loan officer id of the client*/
	private String dateOfBirth;

	/**Denotes the loan officer id of the client*/
	private String customerId;
	
	/**Denotes the display name of the spouse/father of the client*/
	private String spouseFatherDisplayName;
	
	/**Denotes the calculated age of the client*/
	private int age;

	/**Denotes the picture of the client*/
	private InputStream customerPicture;

	/**Denotes the picture of the client*/
	private FormFile picture;
	private int listSize;
/**Denotes no meeting for parent group*/
	private String parentGroupMeetingPresent;
/**An insatnce of the logger which is used to log statements */
	private  MifosLogger logger;
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
	 * It needs to validate certain things based on the state in which
	 * client is being created.
	 * @param mapping
	 * @param request
	 * @return
	 */
	public final ActionErrors customValidate(ActionMapping mapping,
				HttpServletRequest request) {
		String methodCalled= request.getParameter("method");
		UserContext userContext=null;
		if(request.getSession()!=null){
			 userContext=(UserContext) request.getSession().getAttribute(LoginConstants.USERCONTEXT);
		}
		boolean duplicate = false;
		boolean isNotValidCreation = false;
		if(null !=methodCalled) {
			if(	CustomerConstants.METHOD_CANCEL.equals(methodCalled) ||
					CustomerConstants.METHOD_GET.equals(methodCalled) ||
					ClientConstants.METHOD_PREVIOUS_MFI_INFO.equals(methodCalled)||
					ClientConstants.METHOD_EDIT_PERSONAL_INFO .equals(methodCalled)||
					ClientConstants.METHOD_EDIT_MFI_INFO.equals(methodCalled)||
					ClientConstants.METHOD_PREVIOUS_PERSONAL_INFO.equals(methodCalled)||
					CustomerConstants.METHOD_UPDATE.equals(methodCalled) ||
					ClientConstants.METHOD_UPDATE_MFI.equals(methodCalled) ||
					ClientConstants.METHOD_SET_DEFAULT_FORMEDBY.equals(methodCalled) ||
					ClientConstants.METHOD_RETRIEVE_PICTURE.equals(methodCalled) ||
					ClientConstants.METHOD_RETRIEVE_PICTURE_PREVIEW.equals(methodCalled) ||
					ClientConstants.METHOD_SHOW_PICTURE.equals(methodCalled) ||
					CustomerConstants.METHOD_LOAD_STATUS.equals(methodCalled) ||
					ClientConstants.METHOD_PRELOAD.equals(methodCalled) ||
					ClientConstants.METHOD_LOAD_TRANSFER.equals(methodCalled) ||
					ClientConstants.METHOD_LOAD_BRANCH_TRANSFER.equals(methodCalled) ||
					ClientConstants.METHOD_LOAD_MEETING.equals(methodCalled)||
					ClientConstants.METHOD_UPDATE_MEETING.equals(methodCalled)||
					ClientConstants.METHOD_CHOOSE_OFFICE.equals(methodCalled)||
					ClientConstants.METHOD_LOAD_HISTORICAL_DATA.equals(methodCalled)||
					CustomerConstants.METHOD_GET_DETAILS.equals(methodCalled)||
					CustomerConstants.METHOD_LOAD.equals(methodCalled) ){
					request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
			}
			if(( CustomerConstants.METHOD_NEXT.equals(methodCalled)|| CustomerConstants.METHOD_PREVIEW.equals(methodCalled)) && (ClientConstants.INPUT_PERSONAL_INFO.equals(input)||ClientConstants.INPUT_EDIT_PERSONAL_INFO.equals(input)) ){

				ActionErrors errors = new ActionErrors();
				checkForMandatoryFields(EntityMasterConstants.Client,errors,request);
				if(( new CustomerHelper().isValidDOB(dateOfBirth , getUserLocale(request)) ) == false ){
					errors.add(ClientConstants.INVALID_DOB_EXCEPTION,new ActionMessage(ClientConstants.INVALID_DOB_EXCEPTION));
				}
				//custom field validations
				errors = validateCustomFields(request ,errors);
				if(picture !=null){
					String fileName = picture.getFileName();
					if(picture.getFileSize() > ClientConstants.PICTURE_ALLOWED_SIZE){
						errors.add(ClientConstants.PICTURE_SIZE_EXCEPTION,new ActionMessage(ClientConstants.PICTURE_SIZE_EXCEPTION));
					}
					if(!ValidateMethods.isNullOrBlank(fileName)){
						String fileExtension =fileName.substring(fileName.lastIndexOf(".")+1 , fileName.length());
						logger.debug("----file extension: "+fileExtension);
						if(!(fileExtension.equalsIgnoreCase("jpeg") || fileExtension.equalsIgnoreCase("jpg")) )
							errors.add(ClientConstants.PICTURE_EXCEPTION,new ActionMessage(ClientConstants.PICTURE_EXCEPTION));

					}
					logger.debug("Picture Size: "+picture.getFileSize());
					if(picture.getFileSize() == 0||picture.getFileSize() < 0){

						SessionUtils.setAttribute("noPicture" , "Yes" ,request.getSession());
					}
					else{
						SessionUtils.setAttribute("noPicture" , "No" ,request.getSession());
					}
				}
				return errors;
			}

			if(CustomerConstants.METHOD_PREVIEW.equals(methodCalled) && ClientConstants.INPUT_MFI_INFO.equals(input) ){
				ActionErrors errors = new ActionErrors();
				checkForMandatoryFields(EntityMasterConstants.Client,errors,request);
				if(ValidateMethods.isNullOrBlank(customerFormedById)){
					 errors.add(CustomerConstants.FORMEDBY_LOANOFFICER_BLANK_EXCEPTION,new ActionMessage(CustomerConstants.FORMEDBY_LOANOFFICER_BLANK_EXCEPTION));
				}

				if(new CustomerHelper().isAnyAccountFeesWithoutAmnt(adminFeeList , selectedFeeList)){
					 errors.add(CustomerConstants.INVALID_FEE_AMNT,new ActionMessage(CustomerConstants.INVALID_FEE_AMNT));
				}
				//	check if fee is assigned without meeting
				if(isFeeAssignedWithoutMeeting(request)){
					errors.add(GroupConstants.FEE_WITHOUT_MEETING,new ActionMessage(GroupConstants.FEE_WITHOUT_MEETING));

				}
//				checking for duplicacy in the list of selected additional fees
				checkForDuplicatePeriodicFeeExist(request,errors);
				checkForTrainedValidations(request,errors);
				//request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
				return errors;
			}
			else if(CustomerConstants.METHOD_PREVIEW.equals(methodCalled) && ClientConstants.INPUT_EDIT_MFI_INFO.equals(input) ){

				ActionErrors errors = new ActionErrors();
				checkForMandatoryFields(EntityMasterConstants.Client,errors,request);
				
				if( Short.valueOf(statusId).shortValue()== CustomerConstants.CLIENT_APPROVED) {
					if(ValidateMethods.isNullOrBlank(loanOfficerId)){
						errors.add(CustomerConstants.LOAN_OFFICER_BLANK_EXCEPTION,new ActionMessage(CustomerConstants.LOAN_OFFICER_BLANK_EXCEPTION));
						
					}
				}
				Short isTrained =(Short)SessionUtils.getAttribute(CustomerConstants.IS_TRAINED,request.getSession());
				if(isTrained!=null && isTrained.shortValue()==Constants.NO){
					errors = checkForTrainedValidations(request,errors);
					
				}
				
			request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
			return errors;

			}
			else if(CustomerConstants.METHOD_CREATE.equals(methodCalled)){
				ActionErrors errors = new ActionErrors();
				if(Short.valueOf(statusId).shortValue() == ClientConstants.STATUS_ACTIVE && isClientUnderGrp == Constants.NO && (customerMeeting ==null || customerMeeting.getMeeting()==null)){
					if(errors==null)
						errors = new ActionErrors();
					errors.add(CustomerConstants.MEETING_REQUIRED_EXCEPTION,new ActionMessage(CustomerConstants.MEETING_REQUIRED_EXCEPTION));
				}
				if(customerMeeting !=null && customerMeeting.getMeeting()==null){
					setCustomerMeeting(null);

				}
				if( Short.valueOf(statusId).shortValue() == ClientConstants.STATUS_ACTIVE && isClientUnderGrp != 1 ) {

					if(ValidateMethods.isNullOrBlank(loanOfficerId)){
						errors.add(CustomerConstants.LOAN_OFFICER_REQUIRED_EXCEPTION,new ActionMessage(CustomerConstants.LOAN_OFFICER_REQUIRED_EXCEPTION));

					}

				}
				else if( (Short.valueOf(statusId).shortValue() == ClientConstants.STATUS_ACTIVE
						  ||Short.valueOf(statusId).shortValue() == ClientConstants.STATUS_PENDING )
						  && isClientUnderGrp != 0 ) {
					isNotValidCreation = checkGroupStatus(Short.valueOf(statusId).shortValue() ,request);
					logger.debug("In isNotValidCreation-------------------------------------------"+isNotValidCreation);
					if(isNotValidCreation){
						try{
						errors.add(ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION,new ActionMessage(ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION,new Object[]{labelConfig.getLabel(ConfigurationConstants.GROUP,userContext.getPereferedLocale()),labelConfig.getLabel(ConfigurationConstants.CLIENT,userContext.getPereferedLocale())}));
						}
						catch(ConfigurationException ce){
							logger.error("Could not retrive the configured labels from database ...");
							//ignore it and let the user see
							errors.add(ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION,new ActionMessage(ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION));
						}
					}

				}
				else
					errors=null;
				request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
				return errors;

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
	
	private ActionErrors checkForTrainedValidations(HttpServletRequest request ,ActionErrors errors) {
		//	Bug id 28221. Added if else if condition to avoid the null pointer Exception
		//check for training date, if trained is selected
		
		if(request.getParameter("trained")==null) {
			trained=null;
		}
		else if( trained.equals("1")){
			if(ValidateMethods.isNullOrBlank(trainedDate)){
				if(errors == null){
					errors = new ActionErrors();
				}
				errors.add(ClientConstants.TRAINED_DATE_MANDATORY,new ActionMessage(ClientConstants.TRAINED_DATE_MANDATORY));
			}
	
		}
		logger.debug("--------------------trained Date: "+ trainedDate);
		logger.debug("--------------------------trained : "+ trained);
		//if training date is entered and trained is not selected, throw an error
		if(!ValidateMethods.isNullOrBlank(trainedDate)&&ValidateMethods.isNullOrBlank(trained)){
			if(errors == null){
				errors = new ActionErrors();
			}
			errors.add(ClientConstants.TRAINED_CHECKED,new ActionMessage(ClientConstants.TRAINED_CHECKED));
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
	 * This method returns true if any fee(administrative/additional) is assgined to the customer without
	 * attaching meeting, otherwise false
	 * @param request
	 * @return true or false
	 */
	private boolean isFeeAssignedWithoutMeeting(HttpServletRequest request){

		for(int i=0;i<adminFeeList.size();i++){
			if(request.getParameter("adminFee["+i+"].checkedFee")==null){
				adminFeeList.get(i).setCheckedFee(Short.valueOf("0"));
			}
		}

		Context context = (Context)SessionUtils.getAttribute(Constants.CONTEXT,request.getSession());
		if(isClientUnderGrp != Constants.YES ){
			Object obj=context.getBusinessResults(MeetingConstants.MEETING);
			if(obj==null && isAnyFeeAssinged()){
				return true;
			}
			else{
				return false;
			}
		}
		else if(isClientUnderGrp == Constants.YES){
			Group parentGroup = (Group)(context.getSearchResultBasedOnName("ParentGroup").getValue());
			if( parentGroup.getCustomerMeeting() ==null && isAnyFeeAssinged())
				return true;
			else
				return false;

		}
		return false;
	}
	/**
	 * This method returns true if any fee(administrative/additional) is assgined to the customer otherwise false.
	 * @return true or false
	 */
	private boolean isAnyFeeAssinged(){
		//check if any  administrative fee is assigned

		if(null!=adminFeeList && adminFeeList.size()>0){
			for(int index=0;index<adminFeeList.size();index++ ){
				if( ( adminFeeList.get(index).getCheckedFee()==null || adminFeeList.get(index).getCheckedFee().shortValue()!=1 ) && adminFeeList.get(index).getFeeId()!=null && adminFeeList.get(index).getFeeId()!=0){
					return true;
				}
			}
		}

		//check if any additional fee is assigned
		if(null!=selectedFeeList && selectedFeeList.size()>0){
			for(int index=0;index<selectedFeeList.size();index++ ){
				if(selectedFeeList.get(index).getFeeId()!=null && selectedFeeList.get(index).getFeeId().shortValue()!=0 ){
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkGroupStatus(short status , HttpServletRequest request) {
		//Bug Id 27501. Changed the logic to get the parent status id from Context.
		Context context=(Context) SessionUtils.getAttribute("ClientCreationAction_Context",request.getSession());
		Group parentGroup = (Group)(context.getSearchResultBasedOnName("ParentGroup").getValue());
		logger.debug("parentStatus-----------------------"+parentGroup.getStatusId());
		short parentStatus = (parentGroup.getStatusId()).shortValue();
		//short parentStatus = ((Short)request.getAttribute("parentStatus")).shortValue();
		boolean isNotValid = false ;
		if(status == 2){
			if(parentStatus == 7){
				isNotValid = true;
			}
		}
		else if(status == 3){
			if(parentStatus == 7 || parentStatus == 8){
				isNotValid = true;
			}
		}
		return isNotValid;
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
	 * Method which returns the customerDetail
	 * @return Returns the customerDetail.
	 */
	public CustomerDetail getCustomerDetail() {
		return customerDetail;
	}

	/**
	 * Method which sets the customerDetail
	 * @param customerDetail The customerDetail to set.
	 */
	public void setCustomerDetail(CustomerDetail customerDetail) {
		this.customerDetail = customerDetail;
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
	 * Method which returns the customerNameDetail
	 * @return Returns the customerNameDetail.
	 */
	public Set getCustomerNameDetailSet() {
		//conversion of list to set
		return new HashSet(this.customerNameDetailSet);
	}

	/**
	 * Method which sets the customerNameDetail
	 * @param customerNameDetail The customerNameDetail to set.
	 */
	public void setCustomerNameDetailSet(List<CustomerNameDetail> customerNameDetailSet) {
		this.customerNameDetailSet = customerNameDetailSet;
	}
	public CustomerNameDetail getCustomerNameDetail(int index)
	{
			while(index>=customerNameDetailSet.size()){
				customerNameDetailSet.add(new CustomerNameDetail());
			}
			return (CustomerNameDetail)(customerNameDetailSet.get(index));
	}
	/**
	 * Method which returns the isClientUnderGrp
	 * @return Returns the isClientUnderGrp.
	 */
	public short getIsClientUnderGrp() {
		return isClientUnderGrp;
	}

	/**
	 * Method which sets the isClientUnderGrp
	 * @param isClientUnderGrp The isClientUnderGrp to set.
	 */
	public void setIsClientUnderGrp(short isClientUnderGrp) {
		this.isClientUnderGrp = isClientUnderGrp;
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
	 * Method which returns the customFieldSet
	 * @return Returns the customFieldSet.
	 */
	public Set getCustomFieldSet() {
		//conversion of list to set
		return new HashSet(this.customFieldSet);

	}
	public CustomerCustomField getCustomField(int index)
	{
			while(index>=customFieldSet.size()){
				customFieldSet.add(new CustomerCustomField());
			}
			return (CustomerCustomField)(customFieldSet.get(index));
	}
	/**
	 * Method which sets the customFieldSet
	 * @param customFieldSet The customFieldSet to set.
	 */
	public void setCustomFieldSet(List<CustomerCustomField> customFieldSet) {
		this.customFieldSet = customFieldSet;
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
	public FeeMaster getSelectedFee(int index)
	{
		while(index>=selectedFeeList.size()){
			selectedFeeList.add(new FeeMaster());
		}
		return (FeeMaster)selectedFeeList.get(index);
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
	 * Method which returns the submitButton
	 * @return Returns the submitButton.
	 */
	public String getSubmitButton() {
		return submitButton;
	}

	/**
	 * Method which sets the submitButton
	 * @param submitButton The submitButton to set.
	 */
	public void setSubmitButton(String submitButton) {
		this.submitButton = submitButton;
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
	 * Method which returns the trained
	 * @return Returns the trained.
	 */
	public String getTrained() {
		return trained;
	}

	/**
	 * Method which sets the trained
	 * @param trained The trained to set.
	 */
	public void setTrained(String trained) {
		this.trained = trained;
	}

	/**
	 * Method which returns the trainingDate
	 * @return Returns the trainingDate.
	 */
	public String getTrainedDate() {
		return trainedDate;
	}

	/**
	 * Method which sets the trainingDate
	 * @param trainingDate The trainingDate to set.
	 */
	public void setTrainedDate(String trainingDate) {
		this.trainedDate = trainingDate;
	}

	/**
	 * Method which returns the trainingDateFormat
	 * @return Returns the trainingDateFormat.
	 */
	public String getTrainingDateFormat() {
		return trainingDateFormat;
	}

	/**
	 * Method which sets the trainingDateFormat
	 * @param trainingDateFormat The trainingDateFormat to set.
	 */
	public void setTrainingDateFormat(String trainingDateFormat) {
		this.trainingDateFormat = trainingDateFormat;
	}

	/**
	 * Method which returns the clientConfidential
	 * @return Returns the clientConfidential.
	 */
	public String getClientConfidential() {
		return clientConfidential;
	}

	/**
	 * Method which sets the clientConfidential
	 * @param clientConfidential The clientConfidential to set.
	 */
	public void setClientConfidential(String clientConfidential) {
		this.clientConfidential = clientConfidential;
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
	 * Method which returns the groupFlag
	 * @return Returns the groupFlag.
	 */
	public String getGroupFlag() {
		return groupFlag;
	}

	/**
	 * Method which sets the groupFlag
	 * @param groupFlag The groupFlag to set.
	 */
	public void setGroupFlag(String groupFlag) {
		this.groupFlag = groupFlag;
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

	/**
	 * Method which returns the governmentId
	 * @return Returns the governmentId.
	 */
	public String getGovernmentId() {
		return governmentId;
	}

	/**
	 * Method which sets the governmentId
	 * @param governmentId The governmentId to set.
	 */
	public void setGovernmentId(String governmentId) {
		this.governmentId = governmentId;
	}

	/**
	 * Method which returns the nextOrPreview
	 * @return Returns the nextOrPreview.
	 */
	public String getNextOrPreview() {
		return nextOrPreview;
	}

	/**
	 * Method which sets the nextOrPreview
	 * @param nextOrPreview The nextOrPreview to set.
	 */
	public void setNextOrPreview(String nextOrPreview) {
		this.nextOrPreview = nextOrPreview;
	}

	/**
	 * Method which returns the dateOfBirth
	 * @return Returns the dateOfBirth.
	 */
	public String getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * Method which sets the dateOfBirth
	 * @param dateOfBirth The dateOfBirth to set.
	 */
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	/**
	 * This method is used to clear check boxes if has deselected them in the last request
	 * @param mapping
	 * @param request
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		if (request.getParameter("customerNameDetail[0].firstName")!=null){
			if(request.getParameter("trained")==null)
					this.trained=null;

			for(int i=0;i<adminFeeList.size();i++){
				if(request.getParameter("adminFee["+i+"].checkedFee")==null){
					adminFeeList.get(i).setCheckedFee(Short.valueOf("0"));
				}
			}
		}

		//super.reset(mapping, request);
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
	 * Method which returns the parentGroupId
	 * @return Returns the parentGroupId.
	 */
	public String getParentGroupId() {
		return parentGroupId;
	}

	/**
	 * Method which sets the parentGroupId
	 * @param parentGroupId The parentGroupId to set.
	 */
	public void setParentGroupId(String parentGroupId) {
		this.parentGroupId = parentGroupId;
	}

	/**
	 * Method which returns the customerPicture
	 * @return Returns the customerPicture.
	 */
	public InputStream getCustomerPicture() {
		return customerPicture;
	}

	/**
	 * Method which returns the picture
	 * @return Returns the picture.
	 */
	public FormFile getPicture() {
		return picture;
	}

	/**
	 * Method which sets the picture
	 * @param picture The picture to set.
	 */
	public void setPicture(FormFile picture) {
		this.picture = picture;
		try{
			customerPicture = picture.getInputStream();
		}
		catch(IOException ioe){

		}
	}

	/**
	 * Method which returns the submitButton1
	 * @return Returns the submitButton1.
	 */
	public String getSubmitButton1() {
		return submitButton1;
	}

	/**
	 * Method which sets the submitButton1
	 * @param submitButton1 The submitButton1 to set.
	 */
	public void setSubmitButton1(String submitButton1) {
		this.submitButton1 = submitButton1;
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
	public String[] getFieldTypeList() {
		return fieldTypeList;
	}

	public void setFieldTypeList(String[] fieldTypeList) {
		this.fieldTypeList = fieldTypeList;
	}
/**
	 * @return Returns the age.
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @param age The age to set.
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * @return Returns the spouseFatherDisplayName.
	 */
	public String getSpouseFatherDisplayName() {
		return spouseFatherDisplayName;
	}

	/**
	 * @param spouseFatherDisplayName The spouseFatherDisplayName to set.
	 */
	public void setSpouseFatherDisplayName(String spouseFatherDisplayName) {
		this.spouseFatherDisplayName = spouseFatherDisplayName;
	}

	/**
	 * @return Returns the parentGroupMeetingPresent.
	 */
	public String getParentGroupMeetingPresent() {
		return parentGroupMeetingPresent;
	}

	/**
	 * @param parentGroupMeetingPresent The parentGroupMeetingPresent to set.
	 */
	public void setParentGroupMeetingPresent(String parentGroupMeetingPresent) {
		this.parentGroupMeetingPresent = parentGroupMeetingPresent;
	}

	public String getCustomerFormedById() {
		return customerFormedById;
	}

	public void setCustomerFormedById(String customerFormedById) {
		this.customerFormedById = customerFormedById;
	}

	@Override
	public void checkForMandatoryFields(Short entityId, ActionErrors errors, HttpServletRequest request) {
		Map<Short,List<FieldConfigurationEntity>> entityMandatoryFieldMap=(Map<Short,List<FieldConfigurationEntity>>)request.getSession().getServletContext().getAttribute(Constants.FIELD_CONFIGURATION);
		List<FieldConfigurationEntity> mandatoryfieldList=entityMandatoryFieldMap.get(entityId);
		for(FieldConfigurationEntity fieldConfigurationEntity : mandatoryfieldList){
			String propertyName=request.getParameter(fieldConfigurationEntity.getLabel());
			Locale locale=((UserContext)request.getSession().getAttribute(LoginConstants.USERCONTEXT)).getPereferedLocale();
			if(propertyName!=null && !propertyName.equals("") && !propertyName.equalsIgnoreCase("picture")){
				String propertyValue=request.getParameter(propertyName);
				if(propertyValue==null || propertyValue.equals(""))
					errors.add(fieldConfigurationEntity.getLabel(),
							new ActionMessage(FieldConfigurationConstant.EXCEPTION_MANDATORY,
									FieldConfigurationHelper.getLocalSpecificFieldNames(fieldConfigurationEntity.getLabel(),locale)));
			}else if(propertyName!=null && !propertyName.equals("") && propertyName.equalsIgnoreCase("picture")){
					try {
						if(getCustomerPicture()==null || getCustomerPicture().read()==-1 ){
							errors.add(fieldConfigurationEntity.getLabel(),
									new ActionMessage(FieldConfigurationConstant.EXCEPTION_MANDATORY,
											FieldConfigurationHelper.getLocalSpecificFieldNames(fieldConfigurationEntity.getLabel(),locale)));
						
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
	}

	

}


