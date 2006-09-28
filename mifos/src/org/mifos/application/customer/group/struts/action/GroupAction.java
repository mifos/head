/**

 * GroupAction.java    version: 1.0



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
package org.mifos.application.customer.group.struts.action;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.accounts.util.valueobjects.CustomerAccount;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.center.util.helpers.CenterSearchResults;
import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.customer.center.util.valueobjects.Center;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientPerformanceHistoryEntity;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.client.util.valueobjects.Client;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryEntity;
import org.mifos.application.customer.group.struts.actionforms.GroupActionForm;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.group.util.helpers.GroupTransferInput;
import org.mifos.application.customer.group.util.helpers.LinkParameters;
import org.mifos.application.customer.group.util.valueobjects.Group;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.valueobjects.CustomFieldDefinition;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerAddressDetail;
import org.mifos.application.customer.util.valueobjects.CustomerCustomField;
import org.mifos.application.customer.util.valueobjects.CustomerPosition;
import org.mifos.application.customer.util.valueobjects.CustomerSearchInput;
import org.mifos.application.fees.util.valueobjects.FeeMaster;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.application.meeting.util.helpers.MeetingConstants;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.application.personnel.util.valueobjects.PersonnelMaster;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SecurityException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.MifosSearchAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This denotes the action class for the group module. It uses the base class functions to create, update.
 * In addition includes methods to update status, update parent etc.
 * It also includes implementations for preview, cancel and validate
 */

public class GroupAction extends MifosSearchAction {

	/**
	 *  Returns the path which uniquely identifies the element in the dependency.xml.
	 *  This method implementaion is the framework requirement.
	 */
	public String getPath() {
		return GroupConstants.GROUP_ACTION;
	}

	/**
	 * This method is called before converting action form to value object on every method call.
	 * if on a particular method conversion is not required , it returns false, otherwise returns true
	 * @return Returns whether the action form should be converted or not
	 */
	protected boolean isActionFormToValueObjectConversionReq(String methodName) {
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Before converting action form to value object checking for method name " + methodName);
		if(null !=methodName && (methodName.equals(CustomerConstants.METHOD_MANAGE)||
				methodName.equals(CustomerConstants.METHOD_LOAD_STATUS)||
				methodName.equals(CustomerConstants.METHOD_UPDATE)||
				methodName.equals(CustomerConstants.METHOD_CANCEL)||
				methodName.equals(CustomerConstants.METHOD_GET_DETAILS)||
				methodName.equals(CustomerConstants.METHOD_LOAD_SEARCH)||
				methodName.equals(ClientConstants.METHOD_SET_DEFAULT_FORMEDBY)||
				methodName.equals(CustomerConstants.METHOD_SEARCH)
				)){
			return false;
		}else{
			return true;
		}
	}

	protected void handleTransaction(ActionForm actionForm,HttpServletRequest request)throws SystemException,ApplicationException{
		String method = request.getParameter("method");
		if(method.equals("create")){
			Context context = ((Context)SessionUtils.getContext(getPath(), request.getSession()));
			GroupActionForm gpActionForm = (GroupActionForm)actionForm;
			Group group = (Group)context.getValueObject();
			Short groupStatus = getGroupStatusValue(Short.valueOf(gpActionForm.getStatusId()));
			String isCenterExists=(String)context.getBusinessResults(GroupConstants.CENTER_HIERARCHY_EXIST);
			if(isCenterExists.equals(GroupConstants.YES)){
				SearchResults obj = context.getSearchResultBasedOnName(GroupConstants.GROUP_PARENT);
				Center center = (Center)obj.getValue();
				checkPermissionForCreate(groupStatus,context.getUserContext(),null,center.getOffice().getOfficeId(),center.getPersonnel().getPersonnelId());
			}else{
				if(group.getPersonnel()!=null)
					checkPermissionForCreate(groupStatus,context.getUserContext(),null,group.getOffice().getOfficeId(),group.getPersonnel().getPersonnelId());
				else
					checkPermissionForCreate(groupStatus,context.getUserContext(),null,group.getOffice().getOfficeId(),context.getUserContext().getId());
			}
		}
		super.handleTransaction(actionForm,request);
	}
	
	private boolean isPermissionAllowed(Short newState,UserContext userContext,Short flagSelected,Short recordOfficeId,Short recordLoanOfficerId,boolean saveFlag){
		if(saveFlag)return ActivityMapper.getInstance().isSavePermittedForCustomer(newState.shortValue(),userContext,recordOfficeId,recordLoanOfficerId);
		else return ActivityMapper.getInstance().isStateChangePermittedForCustomer(newState.shortValue(),null!=flagSelected?flagSelected.shortValue():0,userContext,recordOfficeId,recordLoanOfficerId);
	}
	
	private void checkPermissionForCreate(Short newState,UserContext userContext,Short flagSelected,Short recordOfficeId,Short recordLoanOfficerId) throws SecurityException{
		if(!isPermissionAllowed(newState,userContext,flagSelected,recordOfficeId,recordLoanOfficerId,true))
			  throw new SecurityException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED); 	 
	}
	
	 /**
	  *	Method which is called to decide the pages on which the errors on failure of validation will be displayed
	  * this method forwards as per the respective input page
	  * @param mapping indicates action mapping defined in struts-config.xml
	  * @param form The form bean associated with this action
	  * @param request Contains the request parameters
	  * @param response
	  * @return The mapping to the next page
	  */
	public ActionForward customValidate(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws SystemException{
		String forward = null;
		String methodCalled= (String)request.getAttribute("methodCalled");
		GroupActionForm gpActionForm = (GroupActionForm)form;
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		String fromPage =gpActionForm.getInput();
			 //deciding forward page
			if(null !=methodCalled) {
				if((CustomerConstants.METHOD_PREVIEW).equals(methodCalled)){
					if(GroupConstants.CREATE_NEW_GROUP.equals(fromPage))
						forward =GroupConstants.CREATE_NEW_GROUP_FAILURE;
					else if(GroupConstants.MANAGE_GROUP.equals(fromPage )){
						((Group)context.getValueObject()).convertCustomFieldDateToDbformat(context.getUserContext().getMfiLocale());
						forward = GroupConstants.MANAGE_GROUP_FAILURE;
					}
					else if(GroupConstants.CHANGE_GROUP_STATUS.equals(fromPage ))
						forward = GroupConstants.LOAD_STATUS_METHOD;
				}else if((CustomerConstants.METHOD_CREATE).equals(methodCalled)){
					forward = GroupConstants.PREVIEW_CREATE_NEW_GROUP_FAILURE;
				}else if(fromPage.equals(GroupConstants.GROUP_SEARCH_CREATE_CLIENT)){
					forward = CustomerConstants.SEARCH_FAILURE;
				}else if(fromPage.equals(GroupConstants.GROUP_SEARCH_CLIENT_TRANSFER)){
					forward = CustomerConstants.SEARCH_FAILURE_TRANSFER;
				}
			}
			return mapping.findForward(forward);
	}

	/**
	 * This method is called when create new group link is clicked.
	 * If Center Hierarchy exists it forwards to center search page, otherwise it directly shows create new group page.
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	public ActionForward hierarchyCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		  Context context=(Context)request.getAttribute(Constants.CONTEXT);
		  context.setBusinessAction(GroupConstants.HIERARCHY_CHECK_METHOD);
		  delegate(context,request);

		  String forward=(String)context.getBusinessResults().get(GroupConstants.BP_RESULT);
		  if (forward.equals(GroupConstants.CREATE_NEW_GROUP)){
			  SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST, GroupConstants.NO,request.getSession());
		  }
		  else{
			  SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST, GroupConstants.YES,request.getSession());
			  SessionUtils.setAttribute(GroupConstants.CENTER_SEARCH_INPUT,context.getBusinessResults(GroupConstants.CENTER_SEARCH_INPUT),request.getSession());
		  }

		  //clear action form
		  this.clearActionForm(form);
		  return mapping.findForward(forward);
	}

	/**
	 * This method is called when group status is updated.
	 * It calls updateStatus in business processor that does all validations before updating the group status.
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	@TransactionDemarcate(validateAndResetToken=true)
	public ActionForward updateStatus(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction(GroupConstants.UPDATE_STATUS_METHOD);
		delegate(context,request);
		return mapping.findForward(GroupConstants.UPDATE_STATUS_SUCCESS);
	}

	/**
	 * This method is called to retrieve group details.
	 * It calls get in business processor to get all group all details
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	@TransactionDemarcate(saveToken = true)
	public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		ActionForward forward =super.get(mapping,form,request,response);
	    Context context=(Context)request.getAttribute(Constants.CONTEXT);
	    Group group = (Group)context.getValueObject();
	    SessionUtils.setAttribute(GroupConstants.IS_GROUP_LOAN_ALLOWED,Configuration.getInstance().getCustomerConfig(group.getOffice().getOfficeId()).canGroupApplyForLoan(),request.getSession());
	    SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST,(String)context.getBusinessResults().get(GroupConstants.CENTER_HIERARCHY_EXIST),request.getSession());
	    SessionUtils.setAttribute(GroupConstants.LINK_VALUES,(LinkParameters)context.getBusinessResults(GroupConstants.LINK_VALUES),request.getSession());
	    
	    Integer customerId = group.getCustomerId();
	    GroupBO groupBO = (GroupBO) new CustomerBusinessService().getCustomer(customerId);
	    SessionUtils.setAttribute(Constants.BUSINESS_KEY, groupBO, request.getSession());
	    
	    GroupPerformanceHistoryEntity groupPerfHistoryEntity = groupBO.getPerformanceHistory();
	    SessionUtils.setAttribute(GroupConstants.GROUP_PERFORMANCE_VO,groupPerfHistoryEntity,request.getSession());
	    return forward;
	}

	/**
	 * This method is called to retrieve group details.
	 * It calls get in business processor to get all group all details
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	@TransactionDemarcate(saveToken = true)
	public ActionForward getDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		String globalCustNum=request.getParameter(CustomerConstants.GLOBAL_CUST_NUM);
		Group group=(Group)getValueObject(getPath());
		group.setGlobalCustNum(globalCustNum);

		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		context.setValueObject(group);
		return get(mapping,form,request,response);
	}
	/**
	 * This is called when one clicks to edit users link in the ui to navigate to a search page.
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 * @throws Exception
	 */
	public ActionForward chooseOffice(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)throws Exception{
		return mapping.findForward(GroupConstants.CHOOSE_OFFICE_SUCCESS);
	}
	/**
	 * This method is called before every preview
	 * It prepares data to show on preview.
	 * e.g. for loan officers, it finds the name of the lo and stores it in valueobject.
	 * It also chooses which preview page is to be shown based on input.
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	public ActionForward customPreview(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		Context context=(Context)request.getAttribute(Constants.CONTEXT);

		Group group=(Group)context.getValueObject();
		GroupActionForm gpActionForm=(GroupActionForm)form;
		String fromPage= gpActionForm.getInput();

		//setting input page to context. this is used by previewInitial in buisness processor
		context.addBusinessResults(GroupConstants.INPUT_PAGE,fromPage);
		if (fromPage.equals(GroupConstants.CREATE_NEW_GROUP)){
			this.handleCreateNewGroupPreview(request, gpActionForm);
		}else if (fromPage.equals(GroupConstants.MANAGE_GROUP)){
			this.handleManageGroupPreview(request, gpActionForm);
		}else if (fromPage.equals(GroupConstants.CHANGE_GROUP_STATUS)){
			this.handleChangeStatusPreview(group, gpActionForm);
		}
		//set display address for the customer
		group.setDisplayAddress(new CustomerHelper().getDisplayAddress(group.getCustomerAddressDetail()));

		// choose preview page to be shown
		return mapping.findForward(this.ChoosePreviewForward(fromPage));
	}

	/**
	 * This is the helper method that handles preview of status change. It makes flag null if status is not cancelled or closed
	 */
	private void handleChangeStatusPreview(Group group,GroupActionForm gpActionForm){
		if(group.getStatusId().shortValue()!=GroupConstants.CANCELLED && group.getStatusId().shortValue()!=GroupConstants.CLOSED && (group.getFlagId()!=null||group.getFlagId().shortValue()==0)){
			group.setFlagId(null);
			gpActionForm.setFlagId(null);
		}
		gpActionForm.setSelectedItems(null);
	}
	/**
	 * This method is called to preview pages
	 * It calls preview on buisness processor and sets if pending approval state is defined or not in session for jsp
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	@TransactionDemarcate(joinToken = true)
	public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		ActionForward forward = super.preview(mapping,form, request, response);
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		String fromPage= ((GroupActionForm)form).getInput();
	    if (fromPage.equals(GroupConstants.CREATE_NEW_GROUP)){
		    //HttpSession session = request.getSession();
	    	SessionUtils.setAttribute(GroupConstants.IS_PENDING_APPROVAL_DEFINED,(String)context.getBusinessResults().get(GroupConstants.IS_PENDING_APPROVAL_DEFINED),request.getSession());
	    }
	    return forward;
	}

	/**
	 * This method is helper method that is called to handle preview for create new group.
	 * @param request Contains the request parameters
	 * @param gpActionForm The form bean associated with this action
	 */
	private void handleCreateNewGroupPreview(HttpServletRequest request, GroupActionForm gpActionForm){
		//String isCenterExist=(String)request.getSession().getAttribute(GroupConstants.CENTER_HIERARCHY_EXIST);
		String isCenterExist=(String)SessionUtils.getAttribute(GroupConstants.CENTER_HIERARCHY_EXIST,request.getSession());
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		Group group=(Group)context.getValueObject();
		if(!ValidateMethods.isNullOrBlank(gpActionForm.getCustomerFormedById()) ){
			Personnel loanOfficer = new CustomerHelper().getFormedByLO(context,Short.parseShort(gpActionForm.getCustomerFormedById()));
			group.setCustomerFormedByPersonnel(loanOfficer);
		}
		else{
			group.setCustomerFormedByPersonnel(null);

		}
		if(isCenterExist.equals(CustomerConstants.NO)){
			if(! ValidateMethods.isNullOrBlank(gpActionForm.getLoanOfficerId())){
				group.setPersonnel(this.getPersonnel(context,Integer.parseInt(gpActionForm.getLoanOfficerId())));
			}
			else
				group.setPersonnel(null);
			//set the meeting in action form if selected
			Meeting meeting =(Meeting) context.getBusinessResults(MeetingConstants.MEETING);
			gpActionForm.setMeeting(meeting);
		}else{
			SearchResults obj = context.getSearchResultBasedOnName(GroupConstants.GROUP_PARENT);
			if(null != obj && null!=obj.getValue()){
				Customer parent = (Customer)obj.getValue();
				gpActionForm.setMeeting(parent.getCustomerMeeting().getMeeting());
			}
		}

		//set null to programs in action form if user has not selected any
		/*this.checkForPrograms(request,gpActionForm);*/
		List<FeeMaster> fees = gpActionForm.getSelectedFeeList();
		context.addAttribute(this.getResultObject(GroupConstants.ADDITIONAL_FEES,getAdditionalFeeListForPreview(context , fees)));
	}

	/**
	 * This method is helper method that is called to handle preview for manage group
	 * @param request Contains the request parameters
	 * @param gpActionForm The form bean associated with this action
	 */
	private void handleManageGroupPreview(HttpServletRequest request, GroupActionForm gpActionForm){
		String isCenterExist=(String)request.getSession().getAttribute(GroupConstants.CENTER_HIERARCHY_EXIST);
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		Group group=(Group)context.getValueObject();

		if(isCenterExist.equals(CustomerConstants.NO)){
			if(! ValidateMethods.isNullOrBlank(gpActionForm.getLoanOfficerId())){
				group.setPersonnel(this.getPersonnel(context,Integer.parseInt(gpActionForm.getLoanOfficerId())));
			}
			else
				group.setPersonnel(null);
		}
	}
	/**
	 * This method is helper that prepares fees to be shown on preview page
	 * @param context instance of Context stoerd in request
	 * @param Additional Fees that user has selected on create new group page
	 * @return Fee list that is to be shown on preview
	 */
	private List<FeeMaster> getAdditionalFeeListForPreview(Context context , List<FeeMaster> fees){
		List<FeeMaster> additionalFees = new ArrayList<FeeMaster>();
		List<FeeMaster> AdditionalFeesMaster =(List)context.getSearchResultBasedOnName(CenterConstants.FEES_LIST).getValue();
		//The list of selected fee ids is obtained and those fee objects are added to an other array to display on
		//the preview page for the create page information
		for(int i=0;i<fees.size();i++){
			FeeMaster selectedFee = fees.get(i);
			for(int j=0;j<AdditionalFeesMaster.size();j++){

				FeeMaster additionalFee =AdditionalFeesMaster.get(j);
				if(fees.get(i).getFeeId() !=null){
					if(fees.get(i).getFeeId().shortValue() == additionalFee.getFeeId().shortValue())
					{
						selectedFee.setFeeName(additionalFee.getFeeName());
						selectedFee.setFeeFrequencyTypeId(additionalFee.getFeeFrequencyTypeId());
						selectedFee.setFeeMeeting(additionalFee.getFeeMeeting());
						additionalFees.add(selectedFee);
						break;
					}
				}
			}
		}
		return additionalFees;
	}

	/**
	 * This method is helper method that removes customer programs from the action form and valueobject,
	 * in case user in last request has not selected any program
	 * @param request Contains the request parameters
	 * @param gpActionForm The form bean associated with this action

	private void checkForPrograms(HttpServletRequest request, GroupActionForm gpActionForm){
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		Object obj=request.getParameter("programs");
		if (obj==null){
			gpActionForm.setPrograms(null);
			Group group = (Group)context.getValueObject();
			group.setCustomerProgram(gpActionForm.getCustomerProgram());
		}
	}
*/
	/**
	 * This method is helper method that chooses forwards for preview method based on input page
	 * @param request Contains the request parameters
	 * @param gpActionForm The form bean associated with this action
	 */
	private String ChoosePreviewForward(String fromPage){
		String forward=null;
		if (fromPage.equals(GroupConstants.CREATE_NEW_GROUP)){
			forward=GroupConstants.PREVIEW_CREATE_NEW_GROUP;
		}else if (fromPage.equals(GroupConstants.MANAGE_GROUP)){
			forward=GroupConstants.PREVIEW_MANAGE_GROUP;
		}else if (fromPage.equals(GroupConstants.CHANGE_GROUP_STATUS)){
			forward=GroupConstants.PREVIEW_CHANGE_GROUP_STATUS;
		}else if (fromPage.equals(GroupConstants.LOAD_CENTERS)){
			forward=GroupConstants.CONFIRM_TRANSFER_SUCCESS;
		}
		return forward;
	}

	/**
	 * This method is called on previous method
	 * It chooses forwards for previous page based on input page
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	public ActionForward customPrevious(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		String fromPage=((GroupActionForm)form).getInput();
		String forward=null;
		//setting input page to context. this is used by previewInitial in buisness processor
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		context.addBusinessResults(GroupConstants.INPUT_PAGE,fromPage);

		if (fromPage.equals(GroupConstants.PREVIEW_CREATE_NEW_GROUP)){
				forward=CustomerConstants.LOAD_SUCCESS;
		}else if(fromPage.equals(GroupConstants.PREVIEW_MANAGE_GROUP)){
			((Group)context.getValueObject()).convertCustomFieldDateToDbformat(context.getUserContext().getMfiLocale());
				forward=GroupConstants.MANAGE_SUCCESS;
		} else if(fromPage.equals(GroupConstants.PREVIEW_CHANGE_GROUP_STATUS)){
			forward=GroupConstants.LOAD_STATUS_METHOD;
		}else if(fromPage.equals(GroupConstants.CREATE_MEETING)){
			forward=CustomerConstants.LOAD_SUCCESS;
		}
		return mapping.findForward(forward);
	}
	/**
	 * This method is called on create method
	 * It chooses forwards for previous page based on input page
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	public ActionForward customCreate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		//set version number of fees
		GroupActionForm actionForm=(GroupActionForm)form;
		List<FeeMaster> adminFeeList =actionForm.getAdminFeeList();
		List<FeeMaster> selectedFeeList = actionForm.getSelectedFeeList();

		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		Group group=(Group)context.getValueObject();
		group.setSelectedFeeSet(formFeeSet(adminFeeList,selectedFeeList,context));

		//set status Id
		group.setStatusId(getGroupStatusValue(group.getStatusId()));
		return null;
	}

	/**
	 * This method is called on create method
	 * It does the explicit cleanup required after gorup creation
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	@TransactionDemarcate(validateAndResetToken=true)
	public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		ActionForward forward = super.create(mapping,form,request,response);
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		//set meeetings to null, after group is created
		context.addBusinessResults(MeetingConstants.MEETING,null);
		Group group = (Group)context.getValueObject();
		SessionUtils.setAttribute(GroupConstants.IS_GROUP_LOAN_ALLOWED,Configuration.getInstance().getCustomerConfig(group.getOffice().getOfficeId()).canGroupApplyForLoan(),request.getSession());
		return forward;
	}

	/**
	 * This is the helper method to set the actual group status.
	 * Based on constant received from jsp in statusId, it sets the approriate group status from group constants.
	 * @param group the value object.
	 */
	private Short getGroupStatusValue(short statusConst){
		switch (statusConst){
			case 1:
				return GroupConstants.PARTIAL_APPLICATION;
			case 2:
				return GroupConstants.PENDING_APPROVAL;
			case 3:
				return GroupConstants.ACTIVE;
		}
		return null;
	}

	private Set formFeeSet(List<FeeMaster> adminFeeList,List<FeeMaster> selectedFeeList,Context context){
		Set<AccountFees> accountFeeSet = new HashSet<AccountFees>();
		List<FeeMaster> adminFeeMaster=((List)context.getSearchResultBasedOnName(GroupConstants.ADMIN_FEES_LIST).getValue());
		List<FeeMaster> selectedFeeMaster=((List)context.getSearchResultBasedOnName(GroupConstants.FEES_LIST).getValue());

		//put administrative fees in set if any
		for(int index=0;index<adminFeeList.size();index++ ){
			if(adminFeeList.get(index).getCheckedFee()!=null){
				if(adminFeeList.get(index).getCheckedFee().shortValue()!=1 ){
					for(int i=0 ;i<adminFeeMaster.size();i++){
						if(adminFeeMaster.get(i).getFeeId().shortValue()== adminFeeList.get(index).getFeeId()){
							AccountFees accountFees = new AccountFees();
							Fees fee = new Fees();
							fee.setFeeId(adminFeeList.get(index).getFeeId());
							fee.setVersionNo(adminFeeMaster.get(i).getVersionNo());
							accountFees.setFees(fee);
							accountFees.setAccountFeeAmount(new Money(Configuration.getInstance().getSystemConfig().getCurrency(),adminFeeList.get(index).getRateOrAmount()));
							accountFees.setFeeAmount(adminFeeList.get(index).getRateOrAmount());
							accountFeeSet.add(accountFees);
							break;
						}
					}
				}
			}
		}
		//put additional fees in set if any
		for(int index=0;index<selectedFeeList.size();index++ ){
			if(selectedFeeList.get(index).getFeeId()!=null){
				if(selectedFeeList.get(index).getFeeId().shortValue()!=0 ){
					for(int i=0 ;i<selectedFeeMaster.size();i++){
						if(selectedFeeMaster.get(i).getFeeId().shortValue()== selectedFeeList.get(index).getFeeId()){
							AccountFees accountFees = new AccountFees();
							Fees fee = new Fees();
							fee.setFeeId(selectedFeeList.get(index).getFeeId());
							fee.setVersionNo(selectedFeeMaster.get(i).getVersionNo());
							accountFees.setFees(fee);
							accountFees.setAccountFeeAmount(new Money(Configuration.getInstance().getSystemConfig().getCurrency(),selectedFeeList.get(index).getRateOrAmount()));
							accountFees.setFeeAmount(selectedFeeList.get(index).getRateOrAmount());
							accountFeeSet.add(accountFees);
							break;
						}
					}
				}
			}
		}

		return accountFeeSet;
	}

	/**
	 * This method is called to load the change status page
	 * It calls the loadStatus method on businessprocessor, that in turn loads list of next status that a group
	 * can changed to
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	public ActionForward loadStatus(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		GroupActionForm groupActionForm =(GroupActionForm)form;
		groupActionForm.setStatusId(null);
		groupActionForm.setFlagId(null);
		groupActionForm.getCustomerNote().setComment(null);
		groupActionForm.setSelectedItems(null);
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		request.getSession().setAttribute(GroupConstants.OLD_STATUS,((Group)context.getValueObject()).getStatusId());
		context.setBusinessAction(GroupConstants.LOAD_STATUS_METHOD);
		delegate(context,request);
		return mapping.findForward(GroupConstants.LOAD_STATUS_METHOD);
	}

	/**
	 * This method is called to load the page for office list,
	 * from where user can select the office to which group is to be transferred
	 * It calls the loadTransfer method on businessprocessor, that in turn loads list of all offices
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	public ActionForward loadTransfer(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		//get the link parameter object from session
		LinkParameters linkParams = (LinkParameters)SessionUtils.getAttribute(CustomerConstants.LINK_VALUES,request.getSession());

		GroupTransferInput gpTransferInput = new GroupTransferInput();
		gpTransferInput.setGlobalCustNum(linkParams.getGlobalCustNum());
		gpTransferInput.setGroupName(linkParams.getCustomerName());

		//put the gpTransferInput object in session
		SessionUtils.setAttribute(GroupConstants.GROUP_TRANSFER_INPUT,gpTransferInput,request.getSession());

		//return loadTransfer success forward
		return mapping.findForward(GroupConstants.LOAD_TRANSFER_SUCCESS);
	}

	/**
	 * This method is called to confirm branch transfer for group
	 * It calls loadCenters on business processor.
	 * In case center hierarchy does not exists it directly shows the group branch transfer confirmation page.
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	public ActionForward confirmBranchTransfer(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		GroupActionForm groupActionForm =(GroupActionForm)form;

		//	get the gpTransferInput object from  session and set the office id and office name for the center
		GroupTransferInput gpTransferInput = (GroupTransferInput)SessionUtils.getAttribute(GroupConstants.GROUP_TRANSFER_INPUT,request.getSession());
		gpTransferInput.setOfficeId(groupActionForm.getOffice().getOfficeId());
		gpTransferInput.setOfficeName(groupActionForm.getOffice().getOfficeName());

		return mapping.findForward(GroupConstants.CONFIRM_TRANSFER_SUCCESS);
	}
	/**
	 * This method is called on cancel to choose cancel forward.
	 * Based on input page on which cancel has been clicked it forwards to appropriate page
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	public ActionForward customCancel(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
		GroupActionForm groupActionForm = (GroupActionForm)form;
		String fromPage =groupActionForm.getInput();
		return mapping.findForward(chooseCancelForward(fromPage));
	}
	/**
	 * This method is called on cancel to choose cancel forward.
	 * Based on input page on which cancel has been clicked it forwards to appropriate page
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	public ActionForward customManage(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		GroupActionForm gpActionForm = (GroupActionForm)form;
		Group group=(Group)context.getValueObject();
		if(group.getTrainedDate()==null){
			SessionUtils.setAttribute(CustomerConstants.IS_TRAINED,Constants.NO,request.getSession());
			gpActionForm.setTrained(String.valueOf(Constants.NO));
			gpActionForm.setTrainedDate(null);
		}
		else{
			SessionUtils.setAttribute(CustomerConstants.IS_TRAINED,Constants.YES,request.getSession());
			gpActionForm.setTrained(String.valueOf(Constants.YES));
			SessionUtils.setAttribute(CustomerConstants.TRAINED_DATE,group.getTrainedDate(),request.getSession());
		}
		gpActionForm.setAdminFeeList(new ArrayList<FeeMaster>());
		gpActionForm.setSelectedFeeList(new ArrayList<FeeMaster>());
		clearActionForm(gpActionForm);
		return null;
	}

	/**
	 * This method is called on cancel to choose cancel forward.
	 * Based on input page on which cancel has been clicked it forwards to appropriate page
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	public ActionForward customUpdate(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		Group group=(Group)context.getValueObject();
		Short isTrained=(Short)SessionUtils.getAttribute(CustomerConstants.IS_TRAINED,request.getSession());
		if(isTrained!=null && isTrained.shortValue()==Constants.YES){
			group.setTrainedDate((Date)SessionUtils.getAttribute(CustomerConstants.TRAINED_DATE,request.getSession()));
			group.setTrained(Constants.YES);
		}
		return null;
	}
	/**
	 * This method is called on cancel to choose cancel forward.
	 * Based on input page on which cancel has been clicked it forwards to appropriate page
	 * @param fromPage
	 * @return page to which it has to forward
	 */
	private String chooseCancelForward(String fromPage){
		String forward=null;
		if(null!=fromPage){
			if (fromPage.equals(GroupConstants.CREATE_NEW_GROUP)||fromPage.equals(GroupConstants.PREVIEW_CREATE_NEW_GROUP)){
				forward=GroupConstants.CANCEL_CREATE;
			}else if (fromPage.equals(GroupConstants.MANAGE_GROUP) || fromPage.equals(GroupConstants.PREVIEW_MANAGE_GROUP)){
				forward=GroupConstants.CANCEL_MANAGE;
			}else if (fromPage.equals(GroupConstants.CHANGE_GROUP_STATUS)||fromPage.equals(GroupConstants.PREVIEW_CHANGE_GROUP_STATUS)){
				forward=GroupConstants.GROUP_DETAILS_PAGE;
			}else if (fromPage.equals(GroupConstants.LOAD_CENTERS)){
				forward=GroupConstants.CONFIRM_TRANSFER_SUCCESS;
			}else if (fromPage.equals(GroupConstants.GROUP_SEARCH_CREATE_CLIENT)){
				forward=GroupConstants.CANCEL_CREATE;
			}else if (fromPage.equals(GroupConstants.GROUP_SEARCH_CLIENT_TRANSFER)){
					forward=GroupConstants.CLIENT_DETAILS_PAGE;
			}else if (fromPage.equals(GroupConstants.CONFIRM_PARENT_TRANSFER_METHOD)){
				forward=GroupConstants.GROUP_DETAILS_PAGE;
			}
		}
		return forward;
	}

	/**
	 * This method is called when group is transferred in different branch.
	 * It calls updateBranchRSM method in business processor
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	@TransactionDemarcate(validateAndResetToken=true)
	public ActionForward updateBranch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		Context context=(Context)request.getAttribute(Constants.CONTEXT);

		//get the gpTransferInput object from  session and set the office id and office name for the center

		GroupTransferInput gpTransferInput = (GroupTransferInput)SessionUtils.getAttribute(GroupConstants.GROUP_TRANSFER_INPUT,request.getSession());

		//put the transfer input object in the context
		context.addBusinessResults(GroupConstants.GROUP_TRANSFER_INPUT,gpTransferInput);

		//call business processor method to transfer group in another branch
		String centerExist=(String)SessionUtils.getAttribute(GroupConstants.CENTER_HIERARCHY_EXIST,request.getSession());
		context.addBusinessResults(GroupConstants.CENTER_HIERARCHY_EXIST, centerExist);
		context.setBusinessAction(GroupConstants.UPDATE_BRANCH_RSM);
		delegate(context,request);

		//remove transfer input object from session
		request.getSession().removeAttribute(GroupConstants.GROUP_TRANSFER_INPUT);
		return mapping.findForward(GroupConstants.UPDATE_BRANCH_SUCCESS);
	}

	/**
	 * This method is called to show the confirmation page for transferring group across different office.
	 * It forwards to confirm transfer page, from where user can update branch office of the group.
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	public ActionForward confirmParentTransfer(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		GroupActionForm gpActionForm =(GroupActionForm)form;


		GroupTransferInput gpTransferInput = new GroupTransferInput();
		gpTransferInput.setCenterId(gpActionForm.getCenterSystemId());
		gpTransferInput.setOfficeId(Short.valueOf(gpActionForm.getParentOfficeId()));

		SessionUtils.setAttribute(GroupConstants.GROUP_TRANSFER_INPUT,gpTransferInput,request.getSession());
		return mapping.findForward(GroupConstants.CONFIRM_PARENT_TRANSFER_SUCCESS);
	}

	/**
	 * This method is called to show center search page for group tranfer across center in same office.
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	public ActionForward loadParentTransfer(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction(GroupConstants.LOAD_PARENT_TRANSFER_METHOD);
		delegate(context,request);
		request.getSession().setAttribute(GroupConstants.CENTER_SEARCH_INPUT,context.getBusinessResults(GroupConstants.CENTER_SEARCH_INPUT));
		return mapping.findForward(GroupConstants.LOAD_CENTER_SEARCH_SUCCESS);
	}

	/**
	 * This method is called to transfer group across center in same office
	 * It calls updateParent method in business processor
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	@TransactionDemarcate(validateAndResetToken=true)
	public ActionForward updateParent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		GroupActionForm gpActionForm =(GroupActionForm)form;

		//get the gpTransferInput object from  session and set the office id and office name for the center

		LinkParameters linkParams = (LinkParameters)SessionUtils.getAttribute(CustomerConstants.LINK_VALUES,request.getSession());

		GroupTransferInput gpTransferInput = new GroupTransferInput();
		gpTransferInput.setCenterId(gpActionForm.getCenterSystemId());
		gpTransferInput.setOfficeId(Short.valueOf(gpActionForm.getParentOfficeId()));

		//put the transfer input object in the context
		context.addBusinessResults(GroupConstants.GROUP_TRANSFER_INPUT,gpTransferInput);


		// put the link values object in context
		context.addBusinessResults(CustomerConstants.LINK_VALUES,linkParams);

		//call method in business processor
		context.setBusinessAction(GroupConstants.UPDATE_PARENT_METHOD);
		delegate(context,request);

		return mapping.findForward(GroupConstants.UPDATE_PARENT_SUCCESS);
	}

	/**
	 * This method is called by framework to add extra methods needed for group module.
	 * It calls updateParent method in business processor
	 * @return Map of extra methods needed for group module
	 */
	public Map<String,String> appendToMap(){

		Map<String,String> map = new HashMap<String,String>();
		map.putAll(super.appendToMap());
		map.put(CustomerConstants.METHOD_GET_DETAILS,CustomerConstants.METHOD_GET_DETAILS);
		map.put(GroupConstants.HIERARCHY_CHECK_METHOD,GroupConstants.HIERARCHY_CHECK_METHOD);
		map.put(GroupConstants.UPDATE_BRANCH_METHOD,GroupConstants.UPDATE_BRANCH_METHOD);
		map.put(GroupConstants.UPDATE_STATUS_METHOD,GroupConstants.UPDATE_STATUS_METHOD);
		map.put(GroupConstants.UPDATE_PARENT_METHOD,GroupConstants.UPDATE_PARENT_METHOD);
		map.put(GroupConstants.LOAD_TRANSFER_METHOD,GroupConstants.LOAD_TRANSFER_METHOD);
		map.put(GroupConstants.CONFIRM_BRANCH_TRANSFER_METHOD,GroupConstants.CONFIRM_BRANCH_TRANSFER_METHOD);
		map.put(GroupConstants.LOAD_SEARCH_METHOD,GroupConstants.LOAD_SEARCH_METHOD);
		map.put(GroupConstants.LOAD_STATUS_METHOD,GroupConstants.LOAD_STATUS_METHOD);
		map.put(GroupConstants.METHOD_CHOOSE_OFFICE,GroupConstants.METHOD_CHOOSE_OFFICE);
		map.put(GroupConstants.LOAD_MEETING_METHOD,GroupConstants.LOAD_MEETING_METHOD);
		map.put(GroupConstants.CONFIRM_PARENT_TRANSFER_METHOD,GroupConstants.CONFIRM_PARENT_TRANSFER_METHOD);
		map.put(ClientConstants.METHOD_SET_DEFAULT_FORMEDBY ,ClientConstants.METHOD_SET_DEFAULT_FORMEDBY);
		map.put(GroupConstants.LOAD_PARENT_TRANSFER_METHOD,GroupConstants.LOAD_PARENT_TRANSFER_METHOD);
		return map;
	}

	/**
	 * This is the helper method that finds out the name of loan officer based on its personnelId.
	 * Master list of loan officers is stored in context
	 * @param context instance of Context
	 * @param personnelId is the id of loan officer
	 * @return Map of extra methods needed for group module
	 */
	private Personnel getPersonnel(Context context, int personnelId){
		Iterator iteratorLO=((List)context.getSearchResultBasedOnName(GroupConstants.LOANOFFICERS).getValue()).iterator();
		Personnel personnel=null;
		while (iteratorLO.hasNext()){
			PersonnelMaster lo=(PersonnelMaster)iteratorLO.next();
			if(lo.getPersonnelId()==personnelId){
				personnel = new Personnel();
				personnel.setPersonnelId(lo.getPersonnelId());
				personnel.setDisplayName(lo.getDisplayName());
				personnel.setVersionNo(lo.getVersionNo());
				return personnel;
			}
		}
		return null;
	}

	/**
	 * This method creates a new SearchResults object with values as passed in parameters
	 * @param resultName the name with which framework will put resultvalue in request
	 * @param resultValue that need to be put in request
	 * @return SearchResults instance
	 */
	 private SearchResults getResultObject(String resultName, Object resultValue){
		  SearchResults result = new SearchResults();
		  result.setResultName(resultName);
		  result.setValue(resultValue);
		  return result;
	  }

	/**
	 * This method is called when group details are to be retrieved
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	 public ActionForward customGet(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		 Context context=(Context)request.getAttribute(Constants.CONTEXT);
		  if(Configuration.getInstance().getCustomerConfig(context.getUserContext().getBranchId()).isCenterHierarchyExists())
			  SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST, GroupConstants.YES,request.getSession());
		  else
			  SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST, GroupConstants.NO,request.getSession());
		 return null;
	 }

	/**
	 * This method is called to load the create new group page when center hierarchy exists.
	 * It sets the group parent id and group parent office id
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	 public ActionForward customLoad(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		 GroupActionForm groupActionForm =(GroupActionForm)form;

		 Context context=(Context)request.getAttribute(Constants.CONTEXT);
		 if(Configuration.getInstance().getCustomerConfig(context.getUserContext().getBranchId()).isCenterHierarchyExists()){
			 CenterSearchResults centerSearchResults= new CenterSearchResults();
			 centerSearchResults.setCenterSystemId(groupActionForm.getCenterSystemId());
			 centerSearchResults.setParentOfficeId(Short.valueOf(groupActionForm.getParentOfficeId()));
			 context.addBusinessResults(GroupConstants.CENTER_SEARCH_RESULT,centerSearchResults);
			 context.addBusinessResults(GroupConstants.CENTER_HIERARCHY_EXIST,GroupConstants.YES);
			 SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST, GroupConstants.YES,request.getSession());
		 }
		 else {
			 context.addBusinessResults(GroupConstants.CENTER_HIERARCHY_EXIST,GroupConstants.NO);
			 SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST, GroupConstants.NO,request.getSession());
		 }
		 //clear action form
		  this.clearActionForm(form);
		 return null;
	 }
	 /**
		 * This method is called to retrieve center details.
		 * It calls get in business processor to get all center details
		 * @param mapping indicates action mapping defined in struts-config.xml
		 * @param form The form bean associated with this action
		 * @param request Contains the request parameters
		 * @param response
		 * @throws Exception
		 */
		@TransactionDemarcate(saveToken =true)
		public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		  	ActionForward forward =super.load(mapping,form,request,response);
		  	Context context=(Context)request.getAttribute(Constants.CONTEXT);
		    GroupActionForm groupForm = (GroupActionForm)form;
		    String isCenterPresent =(String)context.getBusinessResults(GroupConstants.CENTER_HIERARCHY_EXIST);
			if(isCenterPresent.equals(GroupConstants.YES)){
				Short formedById= ((Customer)context.getSearchResultBasedOnName(GroupConstants.GROUP_PARENT).getValue()).getPersonnelId();
				groupForm.setCustomerFormedById(String.valueOf(formedById));
			}
		    List<CustomFieldDefinition> customFields = (List)context.getSearchResultBasedOnName(CustomerConstants.CUSTOM_FIELDS_LIST).getValue();
			for(int i=0;i<customFields.size();i++){
				String defaultValue = customFields.get(i).getDefaultValue();
				if(defaultValue == null || defaultValue.equals(CustomerConstants.BLANK)){
					groupForm.getCustomField(i).setFieldValue("");	
				}
				else{
					if(customFields.get(i).getFieldType() == CustomerConstants.DATE_FIELD_TYPE 
							&&! ValidateMethods.isNullOrBlank(defaultValue)){
							//SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.SHORT, context.getUserContext().getMfiLocale());
							//String userfmt = DateHelper.convertToCurrentDateFormat(((SimpleDateFormat) sdf).toPattern());
						groupForm.getCustomField(i).setFieldValue(DateHelper.getUserLocaleDate(
									context.getUserContext().getMfiLocale(), defaultValue));
							
					}
					else
						groupForm.getCustomField(i).setFieldValue(customFields.get(i).getDefaultValue());
				}
			}
		    return forward;
		}
		public ActionForward setDefaultFormedByPersonnel(ActionMapping mapping, ActionForm form,
				HttpServletRequest request, HttpServletResponse response) {
			
			String forward = GroupConstants.CREATE_NEW_GROUP_PAGE;
			GroupActionForm groupForm = (GroupActionForm)form;
			groupForm.setCustomerFormedById(groupForm.getLoanOfficerId());
			
			return mapping.findForward(forward);
		}

	 /**
	  *	Method which is called to load the  search page which searchs for all the groups based on entered name and as per data scope
	  * @param mapping The page to which the control passes to. This is specified in the struts-config.xml
	  * @param form The form bean associated with this action
	  * @param request Contains the request parameters
	  * @param response
	  * @return The mapping to the next page
	  */
	public ActionForward customSearch(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
		//obtains a search input object which contains parameters like the office id under which the group should be
		//searched. Also contains parameters which helps to decide if a group is being searched to create a new
		//client or to edit the client membership of an already existing client

		CustomerSearchInput customerSearchInput = (CustomerSearchInput)SessionUtils.getAttribute(CustomerConstants.CUSTOMER_SEARCH_INPUT,request.getSession());
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		context.addBusinessResults(CustomerConstants.CUSTOMER_SEARCH_INPUT , customerSearchInput);
		return null;
	}

	/**
	  *	Method which is called to load the  search page which searchs for all the groups based on entered name and as per data scope
	  * @param mapping The page to which the control passes to. This is specified in the struts-config.xml
	  * @param form The form bean associated with this action
	  * @param request Contains the request parameters
	  * @param response
	  * @return The mapping to the next page
	  */
	public ActionForward loadSearch(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
		GroupActionForm groupForm = (GroupActionForm)form;
		groupForm.setSearchNode("searchString",null);
		return mapping.findForward(GroupConstants.LOAD_SEARCH_SUCCESS);
	}

	/**
	  *	Method which is called to load the  create meeting page for the group, when center hierarchy does not exists
	  * @param mapping The page to which the control passes to. This is specified in the struts-config.xml
	  * @param form The form bean associated with this action
	  * @param request Contains the request parameters
	  * @param response
	  * @return The mapping to the next page
	  */
	public ActionForward loadMeeting(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
//		GroupActionForm groupForm = (GroupActionForm)form;
//
//		String fromPage = groupForm.getInput();
//		if (fromPage!=null){
//			if(fromPage.equals(GroupConstants.CREATE_NEW_GROUP))
//				forward=GroupConstants.LOAD_MEETING_SUCCESS;
//		}
		return mapping.findForward(GroupConstants.LOAD_MEETING_SUCCESS);
	}
	 /**
	  * This method is called to clear action form values, whenever a fresh request to create a group comes in
	  * This is necessary because action form is stored in session
	  * @param mapping indicates action mapping defined in struts-config.xml
	  * @param form The form bean associated with this action
	  */
	private void clearActionForm(ActionForm form){
		GroupActionForm groupForm = (GroupActionForm)form;
		groupForm.setDisplayName("");
		groupForm.setCustomerAddressDetail(new CustomerAddressDetail());
		groupForm.setGlobalCustNum("");
		groupForm.setExternalId("");
		groupForm.setLoanOfficerId("");
		groupForm.setCustomerFormedById("");
		groupForm.setMeeting(null);
		//groupForm.setPrograms(null);
		groupForm.setAdminFeeList(new ArrayList<FeeMaster>());
		groupForm.setSelectedFeeList(new ArrayList<FeeMaster>());
		groupForm.setCustomFieldSet(new ArrayList<CustomerCustomField>());
		groupForm.setCustomerPositions(new ArrayList<CustomerPosition>());
		groupForm.setCustomerAccount(new CustomerAccount());
		groupForm.setTrainedDate(null);
		groupForm.setTrained(null);
	}

}
