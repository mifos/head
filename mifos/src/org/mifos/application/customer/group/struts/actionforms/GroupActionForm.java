/**

 * GroupActionForm.java    version: 1.0



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
package org.mifos.application.customer.group.struts.actionforms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.util.valueobjects.CustomerAccount;
import org.mifos.application.configuration.business.ConfigurationIntf;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.customer.util.valueobjects.CustomFieldDefinition;
import org.mifos.application.customer.util.valueobjects.CustomerAddressDetail;
import org.mifos.application.customer.util.valueobjects.CustomerCustomField;
import org.mifos.application.customer.util.valueobjects.CustomerNote;
import org.mifos.application.customer.util.valueobjects.CustomerPosition;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeesConstants;
import org.mifos.application.fees.util.valueobjects.FeeMaster;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.meeting.util.resources.MeetingConstants;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.actionforms.MifosSearchActionForm;
import org.mifos.framework.struts.plugin.helper.EntityMasterConstants;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This class denotes the form bean for the group.
 * It consists of the fields for which the user inputs values
 * @author navitas
 */
public class GroupActionForm extends MifosSearchActionForm{
	
	ConfigurationIntf labelConfig = MifosConfiguration.getInstance();
	
	/**Denotes the group name*/
	private String displayName;
	
	/**Denotes the id of the loan officer assigned to the group*/
	private String loanOfficerId;
	
	/**Denotes whether a group is trained or not.*/
	private String trained;
	
	/**Denotes group training date.*/
	private String trainedDate;
	
	/**Denotes the id of the group in the previous system*/
	private String externalId;
	
	/**Denotes the list of programs group is assigned to
	private String programs[];*/
	
	/**Denotes the group address details*/
	private CustomerAddressDetail customerAddressDetail;
	
	/**Denotes the group meeting details
	 * In case center hierarchy exist, it will be meeting details of the center
	 */
	private Meeting meeting;
	
	/**Denotes the collection sheet for the group*/
	private String collectionSheetId;
	
	/**Denotes the button value for group jsps*/
	private String editInfo;
	
	/**Denotes the status id for group */
	private String statusId;
	
	/**Denotes the button value for group jsps*/
	private String btn;
	
	/**Denotes the office that group belongs to*/
	private Office office;
	
	/**Denotes the custom field values of the group*/
	private List <CustomerCustomField> customFieldSet;
	
	/**Denotes the custom positions values of the group*/
	private List <CustomerPosition> customerPositions  ;
	
	/**Denotes the flagId of the group*/
	private String flagId;
	
	/**Denotes the globalCustomerId of the group*/
	private String globalCustNum;
	
	/**Denotes the version no of the group*/
	private String versionNo;
	
	/**Denotes the note object for the group.*/
	private CustomerNote customerNote;
	
	/**Denotes the parent(center) officeid the group.*/
	private String parentOfficeId;
	
	/**Denotes the parent(center) officename of the group.*/
	private String parentOfficeName;
	
	/**Denotes the parent(center) name of the group.*/
	private String centerName;
	
	/**Denotes the parent(center) systemId of the group.*/
	private String centerSystemId;
	
	/**Denotes the additional fee list selected for the group.*/	
	private List<FeeMaster> selectedFeeList = null;
	
	/**Denotes the admin fee list selected for the group.*/
	private List<FeeMaster> adminFeeList = null;
	
	/**Denotes the master fee amount list for additional fees for the group.*/
	private String selectedFeeAmntList;
	
	/**Denotes the cancelBtn property*/
	private String cancelBtn;
	
	/**Denotes the list of selected checkboxes on the status preview page*/
	private String[] selectedItems;

	/**Denotes the customer account*/
	private CustomerAccount customerAccount;
	 private String[] fieldTypeList;
	
	 private String customerFormedById;
	

	/**
	 * Constructor: All the composition objects are intialized within the contructor
	 */
	public GroupActionForm(){
		customerAddressDetail= new CustomerAddressDetail();
		office=new Office();
		customFieldSet = new ArrayList<CustomerCustomField>();
		customerPositions = new ArrayList<CustomerPosition>();
		customerNote = new CustomerNote();
		customerAccount=new CustomerAccount();
		office = new Office();
		/*programs = null;*/
		adminFeeList=new ArrayList<FeeMaster>();
		selectedFeeList=new ArrayList<FeeMaster>();
		meeting=null;
	}
	
	/**
	 * Return the value of the selectedItems attribute.	
	 * @return Returns the selectedItems.
	 */
	public String[] getSelectedItems() {
		return selectedItems;
	}

	/**
	 * Sets the value of the selectedItems attribute.
	 * @param selectedItems The selectedItems to set.
	 */
	public void setSelectedItems(String[] selectedItems) {
		this.selectedItems = selectedItems;
	}
	/**
     * Return the value of the one of the custom field at index i in customFieldSet attribute.
     * @param i index of the custom field
     * @return CustomerCustomField
     */
	public CustomerCustomField getCustomField(int i){
		while(i>=customFieldSet.size()){
			customFieldSet.add(new CustomerCustomField());
		}
		return (CustomerCustomField)(customFieldSet.get(i));
	}
	
	/**
     * Return the value of the customFieldSet attribute.
     * @return Set
     */
	public Set getCustomFieldSet(){
		return new HashSet(this.customFieldSet);
	}
	
	/**
     * Sets the value of customFieldSet
     * @param customFieldSet
     */
	public void setCustomFieldSet(List<CustomerCustomField> customFieldSet) {
		this.customFieldSet = customFieldSet;
	}
	
	/**
     * Return the value of the CustomerPrograms, that user has selected
     * @return Set
     
	public Set getCustomerProgram(){
		Set custPrg = new HashSet();
		if(programs!=null){
			CustomerProgram cp = null;
			for(int i=0;i<programs.length;i++){
				cp = new CustomerProgram();
				cp.setProgramId(Short.valueOf(programs[i]));
				custPrg.add(cp);
			}
		}
		return custPrg;
	}
*/
	/**
     * Return the value of the collectionSheetId attribute.
     * @return String
     */
	public String getCollectionSheetId() {
		return collectionSheetId;
	}
	
	/**
     * Sets the value of collectionSheetId
     * @param collectionSheetId
     */
 	public void setCollectionSheetId(String collectionSheetId) {
		this.collectionSheetId = collectionSheetId;
	}

	/**
     * Return the value of the customerAddressDetail attribute.
     * @return CustomerAddressDetail
     */
	public CustomerAddressDetail getCustomerAddressDetail() {
		return customerAddressDetail;
	}

	/**
     * Sets the value of customerAddressDetail
     * @param customerAddressDetail
     */
	public void setCustomerAddressDetail(CustomerAddressDetail customerAddressDetail) {
		this.customerAddressDetail = customerAddressDetail;
	}

	/**
     * Return the value of the displayName attribute.
     * @return String
     */
	public String getDisplayName() {
		return displayName;
	}

	/**
     * Sets the value of displayName
     * @param displayName
     */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
     * Return the value of the editInfo attribute.
     * @return String
     */
	public String getEditInfo() {
		return editInfo;
	}
	
	/**
     * Sets the value of editInfo
     * @param editInfo
     */
	public void setEditInfo(String editInfo) {
		this.editInfo = editInfo;
	}

	/**
     * Return the value of the externalId attribute.
     * @return String
     */
	public String getExternalId() {
		return externalId;
	}

	/**
     * Sets the value of externalId
     * @param externalId
     */
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	/**
     * Return the value of the loanOfficerId attribute.
     * @return String
     */
	public String getLoanOfficerId() {
		return loanOfficerId;
	}

	/**
     * Sets the value of loanOfficerId
     * @param loanOfficerId
     */
	public void setLoanOfficerId(String loanOfficerId) {
		this.loanOfficerId = loanOfficerId;
	}

	/**
     * Return the value of the programs attribute.
     * @return String[]
     
	public String[] getPrograms() {
		return programs;
	}

	/**
     * Sets the value of programs
     * @param programs array
     
	public void setPrograms(String[] programs) {
		this.programs = programs;
	}
*/
	/**
     * Return the value of the btn attribute.
     * @return String
     */
	public String getStatusBtn() {
		return btn;
	}

	/**
     * Sets the value of btn
     * @param btn
     */
	public void setStatusBtn(String btn) {
		this.btn = btn;
	}

	/**
     * Return the value of the statusId attribute.
     * @return String
     */
	public String getStatusId() {
		return statusId;
	}
	
	/**
     * Sets the value of statusId
     * @param statusId
     */
	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	/**
     * Return the value of the trained attribute.
     * @return String
     */
	public String getTrained() {
		return trained;
	}

	/**
     * Sets the value of trained
     * @param trained
     */	
	public void setTrained(String trained) {
		this.trained = trained;
	}

	/**
     * Return the value of the trainedDate attribute.
     * @return String
     */
	public String getTrainedDate() {
		return trainedDate;
	}

	/**
     * Sets the value of trainedDate
     * @param trainedDate
     */	
	public void setTrainedDate(String trainedDate) {
		this.trainedDate = trainedDate;
	}

	/**
     * Return the value of the meeting attribute.
     * @return String
     */
	public Meeting getMeeting() {
		return meeting;
	}

	/**
     * Sets the value of meeting
     * @param meeting object
     */
	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}

	/**
     * Return the value of the office attribute.
     * @return Office
     */
	public Office getOffice() {
		return office;
	}

	/**
     * Sets the value of office
     * @param office object
     */
	public void setOffice(Office office) {
		this.office = office;
	}

	/**
     * Return the value of the flagId attribute.
     * @return String
     */
	public String getFlagId() {
		return flagId;
	}

	/**
     * Sets the value of flagId
     * @param flagId 
     */	
	public void setFlagId(String flagId) {
		this.flagId = flagId;
	}

	/**
     * Return the value of the globalCustNum attribute.
     * @return String
     */
	public String getGlobalCustNum() {
		return globalCustNum;
	}

	/**
     * Sets the value of globalCustNum
     * @param globalCustNum 
     */		
	public void setGlobalCustNum(String globalCustNum) {
		this.globalCustNum = globalCustNum;
	}

	/**
	 * Return the value of the customerPositions attribute.	
	 * @return Set
	 */
	public Set getCustomerPositions() {
		return new HashSet(this.customerPositions);
	}

	/**
     * Return the value of the CustomerPosition at passed in index 
     * @return CustomerPosition
     */
	public CustomerPosition getCustomerPosition(int index) {
		
		while(index>=customerPositions.size()){
			customerPositions.add(new CustomerPosition());
		}
		return (CustomerPosition)customerPositions.get(index);
	}
	
	/**
     * Sets the value of customerPositions
     * @param customerPositions list 
     */
	public void setCustomerPositions(List<CustomerPosition> customerPositions) {
		this.customerPositions = customerPositions;
	}

	/**
     * Return the value of the versionNo attribute.
     * @return String
     */
	public String getVersionNo() {
		return versionNo;
	}

	/**
     * Sets the value of versionNo
     * @param versionNo  
     */
	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}

	/**
     * Return the value of the customerNote attribute.
     * @return CustomerNote
     */
	public CustomerNote getCustomerNote() {
		return customerNote;
	}

	/**
     * Sets the value of customerNote
     * @param customerNote object  
     */	
	public void setCustomerNote(CustomerNote customerNote) {
		this.customerNote = customerNote;
	}

	/**
     * Return the value of the centerSystemId attribute.
     * @return String
     */
	public String getCenterSystemId() {
		return centerSystemId;
	}

	/**
     * Sets the value of centerSystemId
     * @param centerSystemId object  
     */	
	public void setCenterSystemId(String centerSystemId) {
		this.centerSystemId = centerSystemId;
	}

	/**
     * Return the value of the centerName attribute.
     * @return String
     */
	public String getCenterName() {
		return centerName;
	}

	/**
     * Sets the value of centerName
     * @param centerName object  
     */	
	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}

	/**
     * Return the value of the parentOfficeId attribute.
     * @return String
     */
	public String getParentOfficeId() {
		return parentOfficeId;
	}

	/**
     * Sets the value of parentOfficeId
     * @param parentOfficeId object  
     */	
	public void setParentOfficeId(String parentOfficeId) {
		this.parentOfficeId = parentOfficeId;
	}

	/**
     * Return the value of the parentOfficeName attribute.
     * @return String
     */
	public String getParentOfficeName() {
		return parentOfficeName;
	}

	/**
     * Sets the value of parentOfficeName
     * @param parentOfficeName object  
     */
	public void setParentOfficeName(String parentOfficeName) {
		this.parentOfficeName = parentOfficeName;
	}

	/**
     * Sets the value of adminFeeList
     * @param adminFeeList list  
     */
	public void setAdminFeeList(List<FeeMaster> adminFeeList) {
		this.adminFeeList = adminFeeList;
	}

	/**
     * Sets the value of selectedFeeList
     * @param selectedFeeList list  
     */
	public void setSelectedFeeList(List<FeeMaster> selectedFeeList) {
		this.selectedFeeList = selectedFeeList;
	}

	/**
     * Return the value of the adminFeeList attribute at index.
     * @return String
     */
	public FeeMaster getAdminFee(int index)
	{
		while(index>=adminFeeList.size()){
			adminFeeList.add(new FeeMaster());
		}
		return (FeeMaster)adminFeeList.get(index);
	}

	/**
     * Return the value of the selectedFeeList attribute.
     * @return List
     */
	public List<FeeMaster> 	getSelectedFeeList(){
		return this.selectedFeeList;
	}
	
	/**
     * Return the value of the adminFeeList attribute.
     * @return List
     */
	public List<FeeMaster> 	getAdminFeeList(){
		return this.adminFeeList;
	}
	
	/**
     * Return the value of the adminFeeList attribute at index.
     * @return FeeMaster
     */
	public FeeMaster getSelectedFee(int index)
	{
		while(index>=selectedFeeList.size()){
			selectedFeeList.add(new FeeMaster());
		}
		return (FeeMaster)selectedFeeList.get(index);
	}

	/**
     * Return the value of the selectedFeeAmntList attribute.
     * @return String
     */
	public String getSelectedFeeAmntList() {
		return selectedFeeAmntList;
	}
	
	/**
     * Sets the value of selectedFeeAmntList
     * @param selectedFeeAmntList  
     */
	public void setSelectedFeeAmntList(String selectedFeeAmntList) {
		this.selectedFeeAmntList = selectedFeeAmntList;
	}

	/**
	 * This method is used in addition to validation framework to do input data validations before proceeding.  
	 * @param mapping
	 * @param request
	 * @return ActionErrors
	 * @throws ApplicationException
	 */
	public ActionErrors customValidate(ActionMapping mapping, HttpServletRequest request) throws ApplicationException {
		String methodCalled= request.getParameter("method");
		
		if(null !=methodCalled) {
			if( (CustomerConstants.METHOD_CANCEL).equals(methodCalled) || 
			    (CustomerConstants.METHOD_GET).equals(methodCalled)	||
			    (CustomerConstants.METHOD_LOAD).equals(methodCalled) || 
			    (CustomerConstants.METHOD_SEARCH_NEXT).equals(methodCalled)||
			    (CustomerConstants.METHOD_SEARCH_PREV).equals(methodCalled)||
			    (CustomerConstants.METHOD_MANAGE).equals(methodCalled) ||
			    (CustomerConstants.METHOD_PREVIOUS).equals(methodCalled)||
			    (GroupConstants.LOAD_STATUS_METHOD).equals(methodCalled)||
			    (CustomerConstants.METHOD_UPDATE).equals(methodCalled)||
				(GroupConstants.HIERARCHY_CHECK_METHOD).equals(methodCalled)||
				(GroupConstants.LOAD_PARENT_TRANSFER_METHOD).equals(methodCalled)||
				(GroupConstants.CONFIRM_PARENT_TRANSFER_METHOD).equals(methodCalled)||
				(GroupConstants.UPDATE_PARENT_METHOD).equals(methodCalled)||
				(GroupConstants.LOAD_TRANSFER_METHOD).equals(methodCalled)||
				(GroupConstants.CONFIRM_BRANCH_TRANSFER_METHOD).equals(methodCalled)||
				(GroupConstants.LOAD_SEARCH_METHOD).equals(methodCalled)||
				(GroupConstants.METHOD_CHOOSE_OFFICE).equals(methodCalled)||
				(GroupConstants.LOAD_MEETING_METHOD).equals(methodCalled)||
				(CustomerConstants.METHOD_GET_DETAILS).equals(methodCalled)||
				(ClientConstants.METHOD_SET_DEFAULT_FORMEDBY).equals(methodCalled)||
				(CustomerConstants.METHOD_PREVIEW.equals(methodCalled) && input.equals(GroupConstants.LOAD_CENTERS))||
				(GroupConstants.UPDATE_BRANCH_METHOD).equals(methodCalled))
				{
					request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
			}else if(CustomerConstants.METHOD_PREVIEW.equals(methodCalled)&& input.equals(GroupConstants.CREATE_NEW_GROUP)){
				return handleCreatePreviewValidations(request);
			}else if(CustomerConstants.METHOD_PREVIEW.equals(methodCalled)&& input.equals(GroupConstants.MANAGE_GROUP)){
				return handleManagePreviewValidations(request);
			}else if(CustomerConstants.METHOD_PREVIEW.equals(methodCalled)&& input.equals(GroupConstants.CHANGE_GROUP_STATUS)){
				return handleStatusPreviewValidations(request);
			}else if(CustomerConstants.METHOD_CREATE.equals(methodCalled)){
				return handleCreateValidations();
			}else if((CustomerConstants.METHOD_SEARCH).equals(methodCalled)){
			    return handleSearchValidations(request);
			}else if((CustomerConstants.METHOD_UPDATE_STATUS).equals(methodCalled)){
			    return handleUpdateStatus(request);
			}
		}
		return null;	
	}

	/**
	 * This method is helper method to do data validations before searching for groups.
	 * If user has not entered any search String, it will display error message  
	 * @param request  
	 * @return ActionErrors
	 */
	private ActionErrors handleUpdateStatus(HttpServletRequest request){
		ActionErrors errors = null;
		Object obj=request.getParameter("chklistSize");
		if(request.getParameter("selectedItems")==null){
			selectedItems=null;
		}
		if(obj!=null){
			int totalItems = new Integer(obj.toString()).intValue();
			if((totalItems>0 && selectedItems==null) ||(selectedItems!=null && totalItems!=selectedItems.length)){
				errors = new ActionErrors();
				errors.add(GroupConstants.INCOMPLETE_CHECKLIST,new ActionMessage(GroupConstants.INCOMPLETE_CHECKLIST));
			}
		
		}
		request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
		return errors;
	}
	/**
	 * This method is helper method to do data validations before searching for groups.
	 * If user has not entered any search String, it will display error message
	 * @param request     
	 * @return ActionErrors
	 */
	private ActionErrors handleSearchValidations(HttpServletRequest request){
		ActionErrors errors = null;
		String searchString=getSearchNode("searchString");
		if(CustomerHelper.isNullOrBlank(searchString)){
			errors = new ActionErrors();
			errors.add(GroupConstants.NO_SEARCH_STRING,new ActionMessage(GroupConstants.NO_SEARCH_STRING));
		}
		request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
		return errors;
	}
	/**
	 * This method is helper method to do data validations before creating a new group
	 * @param request    
	 * @return ActionErrors
	 */
	private ActionErrors handleStatusPreviewValidations(HttpServletRequest request){
		ActionErrors errors = null;
		
		if(statusId==null){
			errors = new ActionErrors();
			errors.add(GroupConstants.MANDATORY_SELECT,new ActionMessage(GroupConstants.MANDATORY_SELECT,GroupConstants.STATUS));
		}else{
			if(Short.valueOf(statusId).shortValue()==GroupConstants.CLOSED || Short.valueOf(statusId).shortValue()==GroupConstants.CANCELLED){
				//chekck if flag ha been selected
				if(CustomerHelper.isNullOrBlank(flagId)){
					if(null==errors){
						errors = new ActionErrors();
					}
					errors.add(GroupConstants.MANDATORY_SELECT,new ActionMessage(GroupConstants.MANDATORY_SELECT,GroupConstants.FLAG));
				}
			}
		}
		if(CustomerHelper.isNullOrBlank(customerNote.getComment())){
			if(null==errors){
				errors = new ActionErrors();
			}
			errors.add(GroupConstants.MANDATORY,new ActionMessage(GroupConstants.MANDATORY,GroupConstants.NOTES));
		}else if(customerNote.getComment().length()>CustomerConstants.COMMENT_LENGTH){
			//status length is more than 500, throw an exception
			if(null==errors){
				errors = new ActionErrors();
			}
			errors.add(GroupConstants.MAX_LENGTH,new ActionMessage(GroupConstants.MAX_LENGTH,GroupConstants.NOTES,CustomerConstants.COMMENT_LENGTH));
		}
		request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
		return errors;
	}
	
	/**
	 * This method is helper method to do data validations before creating a new group   
	 * @return ActionErrors
	 */
	private ActionErrors handleCreateValidations(){
		ActionErrors errors = null;
		//if pending approval state is defined and user directly saving in active state, 
		//check for the loan officer and meetings
		if(Short.valueOf(statusId).shortValue()==3){
			//loan office is mandatory
			if(CustomerHelper.isNullOrBlank(loanOfficerId)){
				errors = new ActionErrors();
				errors.add(GroupConstants.LOAN_OFFICER_REQUIRED,new ActionMessage(GroupConstants.LOAN_OFFICER_REQUIRED));
			}
			if(meeting==null){
				if(errors==null)
					errors = new ActionErrors();
				errors.add(GroupConstants.MEETING_REQUIRED,new ActionMessage(GroupConstants.MEETING_REQUIRED));
			}
		}
		return errors;
	}

	/**
	 * This method is helper method to do data validations before previewing a new group   
	 * @return ActionErrors
	 */	
	private ActionErrors handleManagePreviewValidations(HttpServletRequest request){
		ActionErrors errors = null;
		if(ValidateMethods.isNullOrBlank(displayName)){
			if(errors==null)
				errors =new ActionErrors();
			errors.add(GroupConstants.MANDATORY,new ActionMessage(GroupConstants.MANDATORY,GroupConstants.NAME));
		}
		Short isTrained =(Short)SessionUtils.getAttribute(CustomerConstants.IS_TRAINED,request.getSession());
		if(isTrained!=null && isTrained.shortValue()==Constants.NO){
			//validate trained date
			errors =validateTrainedDate(errors,request);
		}
		if(errors==null)
			errors =new ActionErrors();
		checkAddress(request,errors);
		checkForMandatoryFields(EntityMasterConstants.Group,errors,request);
		String isCenterHierarchy=(String)SessionUtils.getAttribute(GroupConstants.CENTER_HIERARCHY_EXIST,request.getSession());
		if(isCenterHierarchy!=null && isCenterHierarchy.equals(GroupConstants.NO)){
			if(statusId!=null &&(Short.valueOf(statusId).shortValue()==GroupConstants.ACTIVE ||Short.valueOf(statusId).shortValue()==GroupConstants.HOLD)){
				if(CustomerHelper.isNullOrBlank(loanOfficerId)){
					if(errors==null)
						errors = new ActionErrors();
					errors.add(GroupConstants.LOAN_OFFICER_REQUIRED_FOR_ACTIVE_GROUP,new ActionMessage(GroupConstants.LOAN_OFFICER_REQUIRED_FOR_ACTIVE_GROUP));
					return errors;
				}
			}
		}
		errors = validateCustomFields(request ,errors);
		return errors;
	}
	/**
	 * This method is helper method to do data validations before previewing a new group   
	 * @return ActionErrors
	 */	
	private ActionErrors validateTrainedDate(ActionErrors errors,HttpServletRequest request){
		//check for training date, if trained is selected
		if(request.getParameter("trained")==null)
			trained=null;
		if(trained!=null && trained.equals("1")){
			if(CustomerHelper.isNullOrBlank(trainedDate)){
				if(errors==null)
					errors = new ActionErrors();
				errors.add(GroupConstants.TRAINED_DATE,new ActionMessage(GroupConstants.TRAINED_DATE));
			}	
		}
		//if traing date is entered and trained is not selected, throw an error
		if(!CustomerHelper.isNullOrBlank(trainedDate)&& CustomerHelper.isNullOrBlank(trained)){
			if(errors==null)
				errors = new ActionErrors();
			errors.add(GroupConstants.TRAINED_DATE,new ActionMessage(GroupConstants.TRAINED_DATE));
		}
		return errors;
	}
	
	/**
	 * This method is helper method to do data validations before previewing a new group
	 * @param request   
	 * @return ActionErrors
	 */	
	private ActionErrors handleCreatePreviewValidations(HttpServletRequest request){
		ActionErrors errors = null;

		if(ValidateMethods.isNullOrBlank(displayName)){
			if(errors==null)
				errors =new ActionErrors();
			errors.add(GroupConstants.MANDATORY,new ActionMessage(GroupConstants.MANDATORY,GroupConstants.NAME));
		}

		if(ValidateMethods.isNullOrBlank(customerFormedById)){
			if(errors==null)
				errors =new ActionErrors();
			errors.add(CustomerConstants.FORMEDBY_LOANOFFICER_BLANK_EXCEPTION,new ActionMessage(CustomerConstants.FORMEDBY_LOANOFFICER_BLANK_EXCEPTION));
		}
		
		if(errors==null)
			errors =new ActionErrors();
		
		checkAddress(request,errors);
		checkForMandatoryFields(EntityMasterConstants.Group,errors,request);
		//custom field validations
		errors = validateCustomFields(request ,errors);
		
		if(isAnyAccountFeesWithoutAmnt()){
			 errors =new ActionErrors();
			 errors.add(GroupConstants.INVALID_FEE_AMNT,new ActionMessage(GroupConstants.INVALID_FEE_AMNT));
			 return errors;
		}
		//check if fee is assigned without meeting
		if(isFeeAssignedWithoutMeeting(request)){
			 errors =new ActionErrors();
			 errors.add(GroupConstants.FEE_WITHOUT_MEETING,new ActionMessage(GroupConstants.FEE_WITHOUT_MEETING));
			 return errors;
		}
		//check for duplicate fees
		boolean duplicate = false;
		//check if user has selected same additional fee multiple times
		
		errors =checkForDuplicatePeriodicFeeExist(request,errors);
		//validate trained date
		errors =validateTrainedDate(errors,request);
		return errors;
	}
	
	public void checkAddress(HttpServletRequest request, ActionErrors errors)
			 {
		if (customerAddressDetail != null) {
			UserContext userContext = (UserContext) request.getSession()
					.getAttribute(LoginConstants.USERCONTEXT);
			if (userContext != null) {
				Locale locale = userContext.getPereferedLocale();
				checkField(customerAddressDetail.getLine1(),ConfigurationConstants.ADDRESS1,locale,GroupConstants.MAX_ADDRESS_LINE_LENGTH,errors);
				checkField(customerAddressDetail.getLine2(),ConfigurationConstants.ADDRESS2,locale,GroupConstants.MAX_ADDRESS_LINE_LENGTH,errors);
				checkField(customerAddressDetail.getLine3(),ConfigurationConstants.ADDRESS2,locale,GroupConstants.MAX_ADDRESS_LINE_LENGTH,errors);
				checkField(customerAddressDetail.getCity(),ConfigurationConstants.CITY,locale,GroupConstants.MAX_FIELD_LENGTH,errors);
				checkField(customerAddressDetail.getState(),ConfigurationConstants.STATE,locale,GroupConstants.MAX_FIELD_LENGTH,errors);
				checkField(customerAddressDetail.getZip(),ConfigurationConstants.POSTAL_CODE,locale,GroupConstants.MAX_FIELD_LENGTH,errors);
			}
		}

	}
	
	public void checkField(String field ,String key,Locale locale,int length,ActionErrors errors){
		
		try{
		if(field!=null&&field.length()>length){
			errors.add(GroupConstants.MAX_LENGTH, new ActionMessage(
					GroupConstants.MAX_LENGTH, labelConfig.getLabel(ConfigurationConstants.ADDRESS1,locale),length));
		}
		}
		catch(ConfigurationException ce){
			
			//TODO log this as error
			//let the user see wrong message 
			errors.add(GroupConstants.MAX_LENGTH, new ActionMessage(
					GroupConstants.MAX_LENGTH));
			
		}
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
							   if (errors==null)
									errors =new ActionErrors();
							   errors.add(GroupConstants.DUPLICATE_FEE,new ActionMessage(GroupConstants.DUPLICATE_FEE));
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
						if (errors==null)
							errors =new ActionErrors();
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
		
		String isCenterExist=(String)SessionUtils.getAttribute(GroupConstants.CENTER_HIERARCHY_EXIST,request.getSession());
		Context context = (Context)SessionUtils.getAttribute(Constants.CONTEXT,request.getSession());
		if(isCenterExist.equals(CustomerConstants.NO)){
			
			Object obj=context.getBusinessResults(MeetingConstants.MEETING);
			if(obj==null && isAnyFeeAssinged())
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
				if(adminFeeList.get(index).getCheckedFee()!=null && adminFeeList.get(index).getCheckedFee().shortValue()!=1){
					if (adminFeeList.get(index).getFeeId()!=null && adminFeeList.get(index).getFeeId()!=0){
						return true;
					}
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
	/**
	 * This method returns true if there is any accountFee with null or zero amnt.
	 * it checks if the fees id is not null , then amount should not be null. 
	 * @return true or false
	 */
	private boolean isAnyAccountFeesWithoutAmnt() {
		//check if any administrative fee amount is null
		if(null!=adminFeeList && adminFeeList.size()>0){
			for(int index=0;index<adminFeeList.size();index++ ){
				if(adminFeeList.get(index).getCheckedFee()!=null){
					if(adminFeeList.get(index).getCheckedFee().shortValue()!=1 ){
						if (adminFeeList.get(index).getRateOrAmount()==null ||adminFeeList.get(index).getRateOrAmount()==0.0){
							return true;
						}
					}
				}
			}
		}
		//check if any additional fee amount is null
		if(null!=selectedFeeList && selectedFeeList.size()>0){
			for(int index=0;index<selectedFeeList.size();index++ ){
				if(selectedFeeList.get(index).getFeeId()!=null && selectedFeeList.get(index).getFeeId().shortValue()!=0 ){
						if (selectedFeeList.get(index).getRateOrAmount()==null ||selectedFeeList.get(index).getRateOrAmount()==0.0){
							return true;
						}
				}
			}
		}
		return false;
	}
	
	/**
     * Return the value of the btn attribute.
     * @return String
     */
	public String getBtn() {
		return btn;
	}

	/**
     * Sets the value of the btn attribute.
     * @param btn
     */
	public void setBtn(String btn) {
		this.btn = btn;
	}
	
	/**
     * Return the value of the cancelBtn attribute.
     * @return String
     */
	public String getCancelBtn() {
		return cancelBtn;
	}
	
	/**
     * Sets the value of canclBtn
     * @param cancelBtn
     */
	public void setCancelBtn(String cancelBtn) {
		this.cancelBtn = cancelBtn;
	}
	/**
	 * This method is used to clear check boxes if has deselected them in the last request   
	 * @param mapping
	 * @param request
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		if (request.getParameter("displayName")!=null){
		
			for(int i=0;i<adminFeeList.size();i++){
				if(request.getParameter("adminFee["+i+"].checkedFee")==null){
					adminFeeList.get(i).setCheckedFee(Short.valueOf("0"));
				}
			}
		}
		
		super.reset(mapping, request);
	}
	
	/**
     * Return the value of the customerAccount attribute.
     * @return String
     */
	public CustomerAccount getCustomerAccount() {
		return customerAccount;
	}
	
	/**
	 * Sets the value of the customerAccount attribute.
	 * @param customerAccount The customerAccount to set.
	 */
	public void setCustomerAccount(CustomerAccount customerAccount) {
		this.customerAccount = customerAccount;
	}
	public String[] getFieldTypeList() {
		return fieldTypeList;
	}

	public void setFieldTypeList(String[] fieldTypeList) {
		this.fieldTypeList = fieldTypeList;
	}

	public String getCustomerFormedById() {
		return customerFormedById;
	}

	public void setCustomerFormedById(String customerFormedById) {
		this.customerFormedById = customerFormedById;
	}
	
}
