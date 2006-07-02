/**

 * GroupBusinessProcessor.java    version: 1.0   



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

package org.mifos.application.customer.group.business.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.dao.ClosedAccSearchDAO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.configuration.business.ConfigurationIntf;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.configuration.util.helpers.PathConstants;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.dao.CenterDAO;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.center.util.valueobjects.Center;
import org.mifos.application.customer.center.util.valueobjects.CenterSearchResults;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.dao.CustomerUtilDAO;
import org.mifos.application.customer.dao.SearchDAO;
import org.mifos.application.customer.dao.ViewClosedAccountsDAO;
import org.mifos.application.customer.exceptions.AssociatedObjectStaleException;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.exceptions.CustomerNotFoundException;
import org.mifos.application.customer.exceptions.CustomerStateChangeException;
import org.mifos.application.customer.exceptions.CustomerTransferException;
import org.mifos.application.customer.exceptions.DuplicateCustomerNameException;
import org.mifos.application.customer.group.dao.GroupDAO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.group.util.helpers.GroupHelper;
import org.mifos.application.customer.group.util.helpers.GroupTransferInput;
import org.mifos.application.customer.group.util.helpers.LinkParameters;
import org.mifos.application.customer.group.util.valueobjects.Group;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerFlag;
import org.mifos.application.customer.util.valueobjects.CustomerLevel;
import org.mifos.application.customer.util.valueobjects.CustomerMaster;
import org.mifos.application.customer.util.valueobjects.CustomerMeeting;
import org.mifos.application.customer.util.valueobjects.CustomerSearchInput;
import org.mifos.application.meeting.util.resources.MeetingConstants;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.meeting.util.valueobjects.MeetingType;
import org.mifos.application.office.dao.OfficeDAO;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.personnel.dao.PersonnelDAO;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ResourceNotCreatedException;
import org.mifos.framework.exceptions.SecurityException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/** 
 *  This is the business processor for the group module. 
 *  It takes care of handling all the business logic for the group module
 *  @author navitas
 */
public class GroupBusinessProcessor extends MifosBusinessProcessor {
	/**An instance of the customerhelper class */
	CustomerHelper helper = new CustomerHelper();
	
	/**An instance of the logger which is used to log statements */
	private  MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.GROUP_LOGGER);
	private ConfigurationIntf labelConfig=MifosConfiguration.getInstance();
	
	/** 
	* This method loads all the master information needed to be displayed on the create group page
	* e.g list of fees and custom fields etc.
	* This method is called irrespective of center hierarchy exists or not, therefore it loads only 
	* common master data that is required in both the cases
	* @param context an instance of Context
	* @throws ApplicationException
	* @throws SystemException
	*/
	public void loadInitial(Context context)throws ApplicationException, SystemException {
	  try{
		  logger.debug("in loadInitial method of Group Module");
		  CustomerUtilDAO customerUtilDAO=new CustomerUtilDAO();
		 
		  //load custom fields
		  context.addAttribute(customerUtilDAO.getCustomFieldDefnMaster(CustomerConstants.GROUP_LEVEL_ID, GroupConstants.ENTITY_TYPE,CustomerConstants.CUSTOM_FIELDS_LIST));
		 
		  //add administrative set fees to context
		  
		  List adminFeeList = (List)customerUtilDAO.getFeesMasterWithLevel(CustomerConstants.GROUP_LEVEL_ID ,CustomerConstants.ADMIN_FEES_LIST).getValue();
		  //set meetings for fees
		  customerUtilDAO.setMeetingForFees(adminFeeList);
		  
		  context.addAttribute(this.getResultObject(CustomerConstants.ADMIN_FEES_LIST,adminFeeList));
		  
		  //retrieve additional fee list
		  List additionalFeeList=(List)customerUtilDAO.getFeesMasterWithoutLevel(CustomerConstants.All_CATEGORY_ID, GroupConstants.GROUP_CATEGORY_ID, CustomerConstants.GROUP_LEVEL_ID,CustomerConstants.FEES_LIST).getValue();
		  //set meetings for fees
		  customerUtilDAO.setMeetingForFees(additionalFeeList);
		  //add additional fees to context		  
		  context.addAttribute(this.getResultObject(CustomerConstants.FEES_LIST,additionalFeeList));
		  
		  String isCenterExists=(String)context.getBusinessResults(GroupConstants.CENTER_HIERARCHY_EXIST);
		  logger.debug("isCenterExists: -"+ isCenterExists);
		  Group group = (Group)context.getValueObject();
		  if(isCenterExists.equals(GroupConstants.NO)){
			  context.addAttribute(customerUtilDAO.getLoanOfficersMaster(PersonnelConstants.LOAN_OFFICER,group.getOffice().getOfficeId(),context.getUserContext().getId(),context.getUserContext().getLevelId(),GroupConstants.LOANOFFICERS));
		  }
		  /*  add programs to context (feature removed)
		   SearchResults prg = custHelper.getProgramMaster(localeId);
		   List prgList = (List)prg.getValue();
		   context.addAttribute(getResultObject(GroupConstants.PROGRAMS_MAP,new GroupHelper().getProgramsMap(prgList)));
		   */
		  logger.debug("masterdata successfully loaded for create group page.");
	  }catch(SystemException se){
			throw se;
		}catch(ApplicationException ae){
			throw ae;
		}catch(Exception e ){
			e.printStackTrace();
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		}
	}
  
 /** 
   *  This method loads all the master information needed to be displayed on the edit/manage group page
   *  e.g list of programs,loan officers (in case center hierarchy does not exists)
   *  This method is called irrespective of center hierarchy exists or not, therefore it loads only 
   *  common master data that is required in both the cases
   *  @param context an instance of Context
   *  @throws ApplicationException
   *  @throws SystemException
   */ 
	public void manageInitial(Context context)throws ApplicationException, SystemException {
	  try{
		  Group group = (Group)context.getValueObject();
		  logger.debug("in manageInitial method of Group Module for group with id: "+group.getCustomerId());
		  if(!Configuration.getInstance().getCustomerConfig(context.getUserContext().getBranchId()).isCenterHierarchyExists()){
	    	context.addAttribute(new CustomerUtilDAO().getLoanOfficersMaster(PersonnelConstants.LOAN_OFFICER,group.getOffice().getOfficeId(),context.getUserContext().getId(),context.getUserContext().getLevelId(),GroupConstants.LOANOFFICERS));
	    	if(group.getPersonnel()!=null)
	    		context.addBusinessResults(GroupConstants.GROUP_LO,group.getPersonnel().getPersonnelId());
		  }
		  
		 //feature removed) 
		 //context.addAttribute(getResultObject(GroupConstants.PROGRAMS_MAP,new GroupHelper().getProgramsMap((List)new CustomerHelper().getProgramMaster(localeId).getValue())));
		 // context.addAttribute(this.getResultObject(GroupConstants.CUSTOMER_PROGRAMS_MAP,new GroupHelper().getProgramsMap(new GroupHelper().loadCustomerPrograms(group,localeId))));
		  context.addAttribute(this.getResultObject(GroupConstants.OLD_GROUP_NAME,group.getDisplayName()));
	  }catch(SystemException se){
		  throw se;
	  }catch(ApplicationException ae){
		  throw ae;
	  }catch(Exception e ){
		throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
	  }
  }
  
	/** 
	* This is overriden method from base class. loadInitial method gets called before this method.
	* This method is called to show create new group page.
	* It retrieves center under which group is to be created and put that into context. If center hierarchy does 
	* not exists it puts null in the context
	* @param context an instance of Context
	* @throws ApplicationException
	* @throws SystemException
	*/
	public void load(Context context)throws ApplicationException, SystemException {
		String isCenterExists=(String)context.getBusinessResults(GroupConstants.CENTER_HIERARCHY_EXIST);
		Short officeId = null;
		if(isCenterExists.equals(GroupConstants.YES)){
			CenterDAO centerDAO = getCenterDAO();
			CenterSearchResults centerSearchResult=(CenterSearchResults)context.getBusinessResults(GroupConstants.CENTER_SEARCH_RESULT);
			//retreive center by center system id obtained from centerSearchResult object
			String centerSystemId = centerSearchResult.getCenterSystemId();
			Center parent = centerDAO.findBySystemId(centerSystemId);
			officeId = parent.getOffice().getOfficeId();
			context.addAttribute(this.getResultObject(GroupConstants.GROUP_PARENT, parent));
		}
		else/*make group parent null*/{
			context.addAttribute(this.getResultObject(GroupConstants.GROUP_PARENT, null));
			officeId = ((Group)context.getValueObject()).getOffice().getOfficeId();
		}
		context.addAttribute(new CustomerUtilDAO().getFormedByLoanOfficersMaster(ClientConstants.LOAN_OFFICER_LEVEL, officeId, CustomerConstants.FORMEDBY_LOAN_OFFICER_LIST));
					  
	}

	/**
	 * This method is called to create the group. 
	 * validation for duplicate group name has already been done till this point. 
	 * Before a group is created a search Id and global customer number is generated and assigned to the group.
	 * @param context an instance of Context
	 * @throws ApplicationException
	 * @throws SystemException
	 */
    public void create(Context context)throws ApplicationException, SystemException {
	  try{
		  Customer parent= null;
		  Group groupVO=(Group)context.getValueObject();
		 
		  SearchResults obj = context.getSearchResultBasedOnName(GroupConstants.GROUP_PARENT);
		  if(null != obj && null!=obj.getValue()){
			 parent = (Customer)obj.getValue();
			 groupVO.setParentCustomer(parent);
			 groupVO.setPersonnel(parent.getPersonnel());
			 //inherit meeting from center
			 //create an instane of customerMeeting and save in group value object
			 CustomerMeeting customerMeeting = new CustomerMeeting();
			 customerMeeting.setMeeting(parent.getCustomerMeeting().getMeeting());
			 groupVO.setCustomerMeeting(customerMeeting);
			 
		 }else{//center hierarchy does not exists
			 //check for inactive loan officer
			 checkForInactiveLoanOfficer(groupVO.getPersonnel(),groupVO.getOffice().getOfficeId(), context.getUserContext());
			 
			 //check if branch has been made inactive
			 checkForBranchInActive(groupVO.getOffice().getOfficeId(), context.getUserContext());
			 
			 //set parent customer to null, in case center hierarchy does not exists.
			 groupVO.setParentCustomer(null);
			 
			//check for new meeting and add to value object, if created
			 Meeting meeting = (Meeting)context.getBusinessResults(MeetingConstants.MEETING);
			 if(meeting!=null){
				 //create meeting type as customer meeting
				 MeetingType meetingType = new MeetingType();
				 meetingType.setMeetingTypeId(CustomerConstants.CUSTOMER_MEETING_TYPE);
				 meeting.setMeetingType(meetingType);
				 //create an instane of customerMeeting and save in group value object
				 CustomerMeeting customerMeeting = new CustomerMeeting();
				 customerMeeting.setMeeting(meeting);
				 groupVO.setCustomerMeeting(customerMeeting);
			 }
		 }
			  
		 
		  //set customer level
		  CustomerLevel groupLevel = new CustomerLevel();
		  groupLevel.setLevelId(CustomerConstants.GROUP_LEVEL_ID);
		  groupVO.setCustomerLevel(groupLevel);
		  
		  //set created date
		  groupVO.setCreatedDate(helper.getCurrentDate());
		  
		  //set created by
		  groupVO.setCreatedBy(context.getUserContext().getId());
		  
		  if(groupVO.getTrained().shortValue()==Constants.NO)
			  groupVO.setTrained(CustomerConstants.TRAINED_NO);
		  
		  //set address name
		  groupVO.getCustomerAddressDetail().setAddressName("Address");
		  
		  //set approval date if group is being saved in approved state
		  if(groupVO.getStatusId().shortValue()==GroupConstants.ACTIVE){
			  groupVO.setCustomerActivationDate(new java.sql.Date(new Date().getTime()));
		  }
		  else
			  groupVO.setCustomerActivationDate(null);
		  
		  //set updated by and updated date
		  if(parent!=null){	
			parent.setUpdatedBy(context.getUserContext().getId());
			parent.setUpdatedDate(helper.getCurrentDate());
		  }
		  //set maxchildcount for the group
		  groupVO.setMaxChildCount(0);
		  
		  //check permission
//		  if(groupVO.getPersonnelId()!=null)
//				checkPermissionForCreate(groupVO.getStatusId(),context.getUserContext(),null,groupVO.getOffice().getOfficeId(),groupVO.getPersonnelId());
//			else
//				checkPermissionForCreate(groupVO.getStatusId(),context.getUserContext(),null,groupVO.getOffice().getOfficeId(),context.getUserContext().getId());
//		  
		  super.create(context);	
		  }
		  catch(SystemException se){
				throw se;
			}catch(ApplicationException ae){
				throw ae;
			}catch(Exception e ){
				throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
			}
  }

	/**
	 * This method is called to check whether a group can be cancelled or not
	 * It checks for various validations needed for changing the group status to cancelled and throws appropriate exceptions
	 * Group cannot be closed if it has active clients 
	 * @param groupId id of the group getting closed
	 * @param userContext TODO
	 * @throws ApplicationException
	 */    
    private void checkIfGroupCanBeCancelled(int groupId, UserContext userContext) throws ApplicationException{
    	try{
			GroupDAO groupDAO=getGroupDAO();
			// check if any client has active status
			if (groupDAO.isGroupClientsAreActive(groupId))
				throw new CustomerStateChangeException(GroupConstants.GROUP_CLIENTS_ARE_ACTIVE,new Object[]{labelConfig.getLabel(ConfigurationConstants.GROUP,userContext.getPereferedLocale())});
		}catch(ApplicationException ae){
			throw ae;
		}catch(Exception e ){
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		}
    }
    
	/**
	 * This method is called to check whether a group can be closed or not
	 * Group can not be closed if it has active clients or it or its children have active loan account.
	 * It checks for various validations needed for changing the group status to closed and throws appropriate exceptions 
	 * @param groupId the group getting closed
	 * @param userContext TODO
	 * @throws ApplicationException
	 */    
    private void checkIfGroupCanBeClosed(int groupId, UserContext userContext) throws ApplicationException{
    	try{
			GroupDAO groupDAO=getGroupDAO();
			// check  if group has any active loan account
			if (new ViewClosedAccountsDAO().isCustomerWithActiveAccounts(groupId))
				throw new CustomerStateChangeException(GroupConstants.GROUP_HAS_ACTIVE_ACCOUNTS);
			// check if any client has active status
			if (groupDAO.isGroupClientsAreActive(groupId))
				throw new CustomerStateChangeException(GroupConstants.GROUP_CLIENTS_ARE_ACTIVE,new Object[]{labelConfig.getLabel(ConfigurationConstants.GROUP,userContext.getPereferedLocale())});
		}catch(ApplicationException ae){
			throw ae;
		}catch(Exception e ){
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		}
    }

    /** 
     *  This method handles preview for most of the pages/methods. Based on inputpage parameter it handles 
     *  preview of various pages.  
     *  @param context an instance of Context
     *  @throws ApplicationException
     *  @throws SystemException
     */     
    public void previewInitial(Context context) throws ApplicationException,SystemException{
    	try{
			String fromPage=(String)context.getBusinessResults().get(GroupConstants.INPUT_PAGE);
			if(fromPage.equals(GroupConstants.MANAGE_GROUP)){
				handleManagePreview(context);
			}else if (fromPage.equals(GroupConstants.CHANGE_GROUP_STATUS)){
				handleChangeGroupStatusPreview(context);
			}else if (fromPage.equals(GroupConstants.CREATE_NEW_GROUP)){
				handleCreatePreview(context);
			}
		 }catch(SystemException se){
			throw se;
		 }catch(ApplicationException ae){
			throw ae;
		 }catch(Exception e ){
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		}
    }

    /** 
     *  This method is the helper method that handles preview for manage(at the time of edit group information).
     *  It prepares customer positions and customer programs to show on preview page.  
     *  @param context an instance of Context
     *  @throws ApplicationException
     *  @throws SystemException
     */
    private void handleManagePreview(Context context)throws ApplicationException, SystemException{
    	try{
    		Group group = (Group)context.getValueObject();

  		  	short localeId=context.getUserContext().getLocaleId();
    		
    		//List custPrgList=new GroupHelper().loadCustomerPrograms(group,localeId);
    		//context.addAttribute(this.getResultObject(GroupConstants.CUSTOMER_PROGRAMS,custPrgList));
    		//context.addAttribute(this.getResultObject(GroupConstants.CUSTOMER_PROGRAMS_MAP,new GroupHelper().getProgramsMap(custPrgList)));
    		context.addAttribute(this.getResultObject(GroupConstants.CUSTOMER_POSITIONS,new GroupHelper().loadCustomerPositions(group,localeId,(List)(context.getSearchResultBasedOnName(GroupConstants.POSITIONS).getValue()))));
		 }catch(SystemException se){
				throw se;
		 }catch(ApplicationException ae){
				throw ae;
		 }catch(Exception e ){
				throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		 }
    }
    
   /** 
     *  This method is the helper method that handles preview for change group status.
     *  It retrieves check list for the newly selected state and saves that to context.
     *  @param context an instance of Context
     *  @throws ApplicationException
     *  @throws SystemException
     */
    private void handleChangeGroupStatusPreview(Context context)throws ApplicationException, SystemException{
		try{
			Group group = (Group)context.getValueObject();
		
			short localeId=context.getUserContext().getLocaleId();
			
			context.addAttribute(new CustomerUtilDAO().getCheckList(group.getStatusId(), CustomerConstants.GROUP_LEVEL_ID,GroupConstants.CHECKLIST));
			
			//build Customer Note object
			group.getCustomerNote().setCommentDate(helper.getCurrentDate());
			Personnel p = new Personnel();
			p.setPersonnelId(context.getUserContext().getId());
			p.setDisplayName(context.getUserContext().getName());
			group.getCustomerNote().setPersonnel(p);
			group.getCustomerNote().setPersonnelId(context.getUserContext().getId());
			
			group.getCustomerNote().setCustomerId(group.getCustomerId());
			context.removeAttribute(GroupConstants.NEW_STATUS);
			context.removeAttribute(GroupConstants.NEW_FLAG);			
			context.addAttribute(this.getResultObject(GroupConstants.NEW_STATUS, helper.getStatusName(localeId, group.getStatusId(), CustomerConstants.GROUP_LEVEL_ID)));
			
			if(group.getFlagId()!=null && group.getFlagId()!=0)
				context.addAttribute(this.getResultObject(GroupConstants.NEW_FLAG, helper.getFlagName(group.getFlagId(),localeId)));
			
		}catch(SystemException se){
			throw se;
		}catch(ApplicationException ae){
			throw ae;
		}catch(Exception e ){
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		}
    }

  /** 
     *  This method is the helper method that handles preview for create  
     *  @param context an instance of Context
     *  @throws ApplicationException
     *  @throws SystemException
     */
    private void handleCreatePreview(Context context)throws ApplicationException,SystemException{
    	try{
			 Group group=(Group)context.getValueObject();
		//	 List custPrgList=new GroupHelper().loadCustomerPrograms(group,localeId);
		//	 context.addAttribute(this.getResultObject(GroupConstants.CUSTOMER_PROGRAMS,custPrgList));
		//	 context.addAttribute(this.getResultObject(GroupConstants.CUSTOMER_PROGRAMS_MAP,new GroupHelper().getProgramsMap(custPrgList)));
			 if(Configuration.getInstance().getCustomerConfig(context.getUserContext().getBranchId()).isPendingApprovalStateDefinedForGroup())
				 context.addBusinessResults(GroupConstants.IS_PENDING_APPROVAL_DEFINED,GroupConstants.YES);
			 else
				 context.addBusinessResults(GroupConstants.IS_PENDING_APPROVAL_DEFINED,GroupConstants.NO);
			 //add meetings to valueobject
			 Meeting meeting = (Meeting)context.getBusinessResults(MeetingConstants.MEETING);
			 if(meeting!=null){
				 CustomerMeeting customerMeeting = new CustomerMeeting();
				 customerMeeting.setMeeting(meeting);
				 group.setCustomerMeeting(customerMeeting);
			 }else
				 group.setCustomerMeeting(null);
		 }catch(Exception e ){
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		 }
	    }

    /** 
     *  This method is called before updating group. It does validations required before updating 
     *  e.g duplicate name check
     *  @param context an instance of Context
     *  @throws ApplicationException
     *  @throws SystemException
     */    
    public void updateInitial(Context context)throws ApplicationException,SystemException {
    	try{
		 // if the group name has been changed check for duplicate group
		 SearchResults obj=context.getSearchResultBasedOnName(GroupConstants.OLD_GROUP_NAME);
		 String oldName=null;
		 if(obj!=null)
			 oldName = (String)obj.getValue();
		 
		 Group groupVO=(Group)context.getValueObject();
		 if(!oldName.equals(groupVO.getDisplayName()))
			 checkForDuplicateGroup(groupVO,groupVO.getOffice().getOfficeId());

		 if(groupVO.getTrained().shortValue()==Constants.NO)
			  groupVO.setTrained(CustomerConstants.TRAINED_NO);
		 
		 //set updated by and updated date
		 groupVO.setUpdatedBy(context.getUserContext().getId());
		 groupVO.setUpdatedDate(helper.getCurrentDate());
		 
    	}catch(ApplicationException ae){
			throw ae;
		 }catch(Exception e ){
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		 }
    }
 
	/** 
	 *  This method gets called just before creating a new group in database.
	 *  It calls a helper method that throws the exception if groupname duplicate groupname exists.
     *  @param context an instance of Context
     *  @throws ApplicationException
     *  @throws SystemException
	 */
    public void createInitial(Context context) throws ApplicationException, SystemException {
    	try{
    		setOffice(context);
    		Group groupVO=(Group)context.getValueObject();
    		checkForDuplicateGroup(groupVO,groupVO.getOffice().getOfficeId());
    		//check if fee has been made inactive
  		    checkForInActiveFee(groupVO.getCustomerAccount().getAccountFeesSet());
    	}catch(ApplicationException ae){
			throw ae;
		 }catch(Exception e ){
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		 }
	}
    private void checkForInactiveLoanOfficer(Personnel personnel,short officeId, UserContext userContext) throws ApplicationException, SystemException{
    	if(null!=personnel && null!=personnel.getPersonnelId()){
    		if(new CustomerUtilDAO().isLoanOfficerInactive(personnel.getPersonnelId(),officeId))
    			throw new AssociatedObjectStaleException(GroupConstants.LOANOFFICER_INACTIVE,new Object[]{labelConfig.getLabel(ConfigurationConstants.BRANCHOFFICE,userContext.getPereferedLocale())});
    	}
    }
    private void checkForInActiveFee(Set accountFeesSet)throws ApplicationException, SystemException{
    	List<Short> feeIds = new ArrayList();
    	if(accountFeesSet !=null && accountFeesSet.size()>0){
			Iterator <AccountFees>it = accountFeesSet.iterator();
			while(it.hasNext()){
				feeIds.add((it.next()).getFees().getFeeId());
			}
			if(new CustomerUtilDAO().isFeesStatusInactive(feeIds)){
				throw new AssociatedObjectStaleException(GroupConstants.FEE_INACTIVE);
			}
		}
    }
    private void checkForBranchInActive(Short officeId, UserContext userContext)throws ApplicationException, SystemException{
    	if(new CustomerUtilDAO().isBranchInactive(officeId)){
    		throw new AssociatedObjectStaleException(GroupConstants.BRANCH_INACTIVE,new Object[]{labelConfig.getLabel(ConfigurationConstants.BRANCHOFFICE,userContext.getPereferedLocale())});
    	}
    }
	/** 
	 *  This method the helper method that sets office in group value object, based on center hierarchy.
	 *  If center hierarchy exits , it sets group office as centers office, otherwise it sets the office
	 *  that was selected from officelist before creating group.
	 *  @param context an instance of Context
	 *  @throws ApplicationException
     *  @throws SystemException  
	 */
    public void setOffice(Context context) throws ApplicationException, SystemException{
    	SearchResults obj = context.getSearchResultBasedOnName(GroupConstants.GROUP_PARENT);
    	Group groupVO=(Group)context.getValueObject();
		if(null != obj && null!=obj.getValue()){
			 Customer parent = (Customer)obj.getValue();
			 groupVO.setOffice(parent.getOffice());
		}
		else{
			groupVO.setOffice(new CustomerUtilDAO().getOffice(groupVO.getOffice().getOfficeId()));
		}
	}

	/** 
	 *  This method is called to check for duplicate group name
	 *  It calls isGroupNameExists on GroupDAO to check for groupname duplicacy.
	 *  If duplicate groupname is found, it throws DuplicateCustomerNameException
     *  @param context an instance of Context
     *  @throws ApplicationException
     *  @throws SystemException  
	 */
    private void checkForDuplicateGroup(Group groupVO,short officeId)throws ApplicationException{
		try{
			boolean isDuplicate=this.getGroupDAO().isGroupNameExist(groupVO.getDisplayName(),officeId);
						
			if (isDuplicate){
				Object values[]=new Object[2];
				values[0]=GroupConstants.GROUP;
				values[1]=groupVO.getDisplayName();
				throw new DuplicateCustomerNameException(GroupConstants.DUPLICATE_GROUP,values);
			}
		}catch(ApplicationException ae){
			throw ae;
		}catch(Exception e ){
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		}
    }

    /** 
     * This method is called whenever a request for create new group page comes.
     * This method calls isCenterHierarchyExist on ConfigurationHelper, to check if center hierarchy exists
     * It shows center search page if center hierarchy exists, otherwise it shows create new group page.
     * @param context an instance of Context
     * @throws ApplicationException
     * @throws SystemException    
     */
    public void hierarchyCheck(Context context) throws SystemException,ApplicationException {
	  try{
		  GroupHelper groupHelper = new GroupHelper();
		  if(Configuration.getInstance().getCustomerConfig(context.getUserContext().getBranchId()).isCenterHierarchyExists()){
			   context.addBusinessResults(GroupConstants.BP_RESULT,GroupConstants.LOAD_CENTER_SEARCH_SUCCESS);
			   context.addBusinessResults(GroupConstants.CENTER_HIERARCHY_EXIST,GroupConstants.YES);
			   //office of the logged in user
			   Office userOffice=getUserOffice(context.getUserContext().getId());
			   context.addBusinessResults(GroupConstants.CENTER_SEARCH_INPUT,groupHelper.getCenterSearchInput(userOffice.getOfficeId(),GroupConstants.CREATE_NEW_GROUP ));
		  }
		  else{
			  context.addBusinessResults(GroupConstants.BP_RESULT,GroupConstants.CREATE_NEW_GROUP);
			  context.addBusinessResults(GroupConstants.CENTER_HIERARCHY_EXIST,GroupConstants.NO);
		  }
		  }catch(SystemException se){
				throw se;
		  }catch(ApplicationException ae){
				throw ae;
		  }catch(Exception e ){
				throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		  }
  }

	/** 
	 * This method is called to load the status page.
	 * It finds out next applicable status that a group can have by passing current group status
	 * and puts applicable status list that in context
	 * @param context an instance of Context
	 * @throws ApplicationException
	 * @throws SystemException  
	 */ 
  	public void loadStatus(Context context) throws SystemException,ApplicationException {
		try{
			short localeId=context.getUserContext().getLocaleId();
			List statusList= helper.getStatusList(localeId,((Group)context.getValueObject()).getStatusId(),CustomerConstants.GROUP_LEVEL_ID,context.getUserContext().getBranchId());
			context.addAttribute(getResultObject(GroupConstants.STATUS_LIST, statusList));
		}catch(SystemException se){
			throw se;
		}catch(ApplicationException ae){
			throw ae;
		}catch(Exception e){
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		}
  	}
 
	/** 
	 * This method is called whenever edit center membership link (group transfer in differrent center) 
	 * is requested. It loads center search page. From center results page new center for the group is selected.
	 * @param context an instance of Context
	 * @throws ApplicationException
	 */ 
  	public void loadParentTransfer(Context context)throws ApplicationException  {
  		try{
			context.addBusinessResults(GroupConstants.CENTER_SEARCH_INPUT,new GroupHelper().getCenterSearchInput(context.getUserContext().getBranchId(),GroupConstants.TRANSFER ));
  		}catch(Exception e){
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		}
  	}
 
 	/** 
	 *  This method is called whenever a user chooses a branch in which the group is to be transferred. 
	 *  This method is called only if center hierarchy does not exits and application is functioning in RSM.
	 *  @param context an instance of Context
	 *  @throws ApplicationException
	 *  @throws SystemException
	 */   	

 public void updateBranchRSM(Context context) throws ApplicationException,SystemException {
	  try{
		  GroupDAO groupDAO=this.getGroupDAO();
		  GroupTransferInput gpTransferInput = (GroupTransferInput)context.getBusinessResults(GroupConstants.GROUP_TRANSFER_INPUT);
		  
		  Group oldCustomer = groupDAO.findBySystemId(gpTransferInput.getGlobalCustNum());
		  
		  List clientList=new CustomerUtilDAO().getCustomerChildren(oldCustomer.getSearchId(),oldCustomer.getOffice().getOfficeId(),null);
		  
		  //validate if group can be transferred to another branch
		  validateUpdateBranch(context,oldCustomer, gpTransferInput.getOfficeId(), clientList);
		  
		  //set updated by and updated date
		  oldCustomer.setUpdatedBy(context.getUserContext().getId());
		  oldCustomer.setUpdatedDate(helper.getCurrentDate());
		  
		  //set customer status on hold if customer is in active state and make loan office association inactive
		  oldCustomer.setPersonnel(null);
		  if(oldCustomer.getStatusId().shortValue()==GroupConstants.ACTIVE)
			  oldCustomer.setStatusId(GroupConstants.HOLD);
		  
		  //call method in DAO to update branch for group 
		  groupDAO.updateBranchRSM(oldCustomer , clientList,context);
	  }catch(SystemException se){
			throw se;
	  }catch(ApplicationException ae){
			throw ae;
	  }catch(Exception e ){
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
	  }
 }
 
	/** 
	 *  This method is called whenever a parent of the gorup is need to be updated.
	 *  @param context an instance of Context
	 *  @throws ApplicationException
	 *  @throws SystemException
	 */   	
 	public void updateParent(Context context) throws ApplicationException,SystemException {
		try{
			CenterDAO centerDAO = this.getCenterDAO();
			GroupDAO groupDAO=this.getGroupDAO();
			LinkParameters linkParams = (LinkParameters)context.getBusinessResults(CustomerConstants.LINK_VALUES);
			GroupTransferInput transferInput = (GroupTransferInput)context.getBusinessResults(GroupConstants.GROUP_TRANSFER_INPUT);
			if(linkParams==null ||transferInput==null )
				throw new CustomerException();
			//get old customer
			Group oldCustomer = groupDAO.findBySystemId(linkParams.getGlobalCustNum());
			
			Customer parent=centerDAO.findBySystemId(transferInput.getCenterId());
			
			//retrieve clients for group
			List<CustomerMaster> clientList=new CustomerUtilDAO().getCustomerChildren(oldCustomer.getSearchId(),oldCustomer.getOffice().getOfficeId(),null);
			
			//call if group can be transfer to given center
			this.validateCenterTransfer(context,oldCustomer,parent,clientList);
			
			context.addAttribute(this.getResultObject(GroupConstants.GROUP_PARENT, parent));
			
			//set old customer updated date and updated by
			oldCustomer.setUpdatedBy(context.getUserContext().getId());
			oldCustomer.setUpdatedDate(helper.getCurrentDate());
			
			//set parent updated date and updated by and updated date
			oldCustomer.getParentCustomer().setUpdatedBy(context.getUserContext().getId());
			oldCustomer.getParentCustomer().setUpdatedDate(helper.getCurrentDate());
			
			//if group is being transferred to center of a differrent branch
			if(oldCustomer.getOffice().getOfficeId().shortValue()!=parent.getOffice().getOfficeId().shortValue()){
				 //set customer status on hold if customer is in active state
				if(oldCustomer.getStatusId().shortValue()==GroupConstants.ACTIVE)
					oldCustomer.setStatusId(GroupConstants.HOLD);
			}
			
			groupDAO.updateParent(oldCustomer,clientList,context);
		}catch(SystemException se){
			throw se;
		}catch(ApplicationException ae){
			throw ae;
		}catch(Exception e ){
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		}
 	}
 	
	/** 
	 *  This method is called to validate, whether a group can be assigned to given parent or not
	 *  If group cannot be assigned to the passed in parent , it throws CustomerTransferException
	 *  @param context an instance of Context
	 *  @param group 
	 *  @param parent  
	 *  @param clientList list of clients under the group
	 *  @throws ApplicationException
	 *  @throws SystemException
	 */   	
	public void validateCenterTransfer(Context context,Group group, Customer parent, List<CustomerMaster> clientList)throws ApplicationException,SystemException{
		if(group.getParentCustomer().getCustomerId().intValue()==parent.getCustomerId().intValue()){
			Object[]values = new Object[1];
			values[0]=group.getDisplayName();
			throw new CustomerTransferException(GroupConstants.SAME_PARENT,values);	
		}
		if(group.getOffice().getOfficeId().shortValue()!=parent.getOffice().getOfficeId().shortValue()){
			validateUpdateBranch(context,group,parent.getOffice().getOfficeId(),clientList);
		}
	}

	
	/** 
	 *  This method is called to check whether group can be transferred to another branch or not 
	 *  If group cannot be transferred in new branch, throws CustomerTransferException
	 *  @param group 
	 *  @throws ApplicationException
	 */   
	private void validateUpdateBranch(Context context,Group group, short officeIdToUpdate, List<CustomerMaster> clientList)throws ApplicationException,SystemException{
		//transferring in same office, should result in an exception
		if(group.getOffice().getOfficeId().shortValue()==officeIdToUpdate){
			Object[]values = new Object[1];
			values[0]=group.getDisplayName();
			throw new CustomerTransferException(GroupConstants.SAME_BRANCH,values);	
		}

		//check for name duplicay
		checkForDuplicateGroup(group,officeIdToUpdate);
		
		//check if group has active loans
		if(new ViewClosedAccountsDAO().isCustomerWithActiveAccounts(group.getCustomerId())){
			throw new CustomerTransferException(GroupConstants.TRANSFER_EX_ACTIVE_LOAN_ACCOUNTS);
		}
		
		if(null!=clientList && clientList.size()>0){
			for(int i=0;i<clientList.size();i++){
				CustomerMaster client = (CustomerMaster)clientList.get(i);
				//check if client has active loans
				if(new ViewClosedAccountsDAO().isCustomerWithActiveAccounts(client.getCustomerId())){
					throw new CustomerTransferException(GroupConstants.TRANSFER_EX_CLIENT_HAS_ACTIVE_LOAN_ACCOUNTS);
				}
			}
		}
	}

	/** 
	 *  This method is called to update the group status with the new status
	 *  It validates for whether grop status can be changed. If yes, it updates group status by calling 
	 *  updateStatus on GroupDAO.
	 *  @param context an instance of Context 
	 *  @throws ApplicationException
	 *  @throws SystemException
	 */	
	public void updateStatus(Context context)throws ApplicationException,SystemException {
		try{
			Group groupVO=(Group)context.getValueObject();
			GroupDAO groupDAO=this.getGroupDAO();
			Customer oldCustomer = groupDAO.findBySystemId(groupVO.getGlobalCustNum());
			if(oldCustomer.getVersionNo().intValue()!=groupVO.getVersionNo().intValue()){
				Object values[]=new Object[1];
				values[0]=oldCustomer.getDisplayName();
				values[0]=labelConfig.getLabel(ConfigurationConstants.GROUP,context.getUserContext().getPereferedLocale());
				throw new CustomerException(GroupConstants.GROUP_INVALID_VERSION, values);
			}
			
			
			//validate group status
			if(oldCustomer.getStatusId().shortValue()!=groupVO.getStatusId().shortValue())
				validateStatus(oldCustomer, groupVO, context.getUserContext());
		
			//set updated by and updated date
			groupVO.setUpdatedBy(context.getUserContext().getId());
			groupVO.setUpdatedDate(helper.getCurrentDate());
			checkPermissionForStatusChange(groupVO.getStatusId(),context.getUserContext(),groupVO.getFlagId(),oldCustomer.getOffice().getOfficeId(),oldCustomer.getPersonnel().getPersonnelId());
			
			//update group status
			groupDAO.updateStatus(context, oldCustomer);
		}catch(SystemException se){
			throw se;
		}catch(ApplicationException ae){
			throw ae;
		}catch(Exception e ){
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		}
	}

	/** 
	 *  This method is called before updating the group status. 
	 *  It validates whether this group be changed to new status
	 *  If no it throws customer state change exception with respective key
	 *  @param oldCustomer group with old status 
	 * @param groupVO group with new staus id
	 * @param userContext TODO
	 *  @throws ApplicationException
	 *  @throws SystemException
	 */	
	private void validateStatus(Customer oldCustomer, Group groupVO, UserContext userContext)throws ApplicationException,SystemException{
		short newStatus=groupVO.getStatusId();
		if(newStatus==GroupConstants.CLOSED)
			checkIfGroupCanBeClosed(groupVO.getCustomerId(), userContext);
		if(newStatus==GroupConstants.ACTIVE)
			checkIfGroupCanBeActive(oldCustomer);
		if(oldCustomer.getStatusId().shortValue()==GroupConstants.CANCELLED && newStatus==GroupConstants.PARTIAL_APPLICATION){
			handleValidationsForCancelToPartial(oldCustomer, userContext);
		}
		if(newStatus==GroupConstants.CANCELLED)
			checkIfGroupCanBeCancelled(groupVO.getCustomerId(), userContext);
	}
	
	/** 
	 * This method is called before updating the group status to parital application from cancelled state. 
	 * It checks for if center is active, incase group is assigned to center
	 * It checks for if branch and loan officer assigned is active, incase group is not assigned to center
	 * If not it throws customer state change exception with respective key
	 * @param oldCustomer group retrieved from the database to update the status
	 * @param userContext TODO
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private void handleValidationsForCancelToPartial(Customer oldCustomer, UserContext userContext)throws ApplicationException,SystemException{
		//group status is being changed from cancelled to partial application
		if(oldCustomer.getParentCustomer()!=null && oldCustomer.getParentCustomer().getCustomerId()!=null){
			//if center hierarchy exists, check for center state
			//if center assigned is inactive throw an exception
			if (!getGroupDAO().isCenterActive(oldCustomer.getParentCustomer().getCustomerId())){
				throw new CustomerStateChangeException(GroupConstants.CENTER_INACTIVE,new Object[]{labelConfig.getLabel(ConfigurationConstants.GROUP,userContext.getPereferedLocale())});
			}
		}else{
			if (new CustomerUtilDAO().isBranchInactive(oldCustomer.getOffice().getOfficeId())){
				throw new CustomerStateChangeException(GroupConstants.BRANCH_INACTIVE,new Object[]{labelConfig.getLabel(ConfigurationConstants.GROUP,userContext.getPereferedLocale())});
			}
			if(oldCustomer.getPersonnel()!=null && oldCustomer.getPersonnel().getPersonnelId()!=null){
				if (new CustomerUtilDAO().isLoanOfficerInactive(oldCustomer.getPersonnel().getPersonnelId(),oldCustomer.getOffice().getOfficeId())){
					throw new CustomerStateChangeException(GroupConstants.LOANOFFICER_INACTIVE,new Object[]{labelConfig.getLabel(ConfigurationConstants.BRANCHOFFICE,userContext.getPereferedLocale())});
				}
			}
		}
	}
	
	/** 
	 * This method is called before updating the group status to active. 
	 * It checks whether a group can be made active. If group does not have a meeting and loan officer it can
	 * not be made active
	 * If not it throws customer state change exception with respective key
	 * @param group with new staus id
	 * @throws ApplicationException
	 */
	public void checkIfGroupCanBeActive(Customer group) throws ApplicationException{
		//check for loan officer and meeting if center is not assigned to the group.
		//in case center is assigned, group will inherit meeting and lo from center
		
		if(group.getParentCustomer()==null || group.getParentCustomer().getCustomerId()==null){
			
			// check if loan office is assigned to group
			if(group.getPersonnel()==null || group.getPersonnel().getPersonnelId()==null){
				throw new CustomerStateChangeException(GroupConstants.GROUP_LOANOFFICER_NOT_ASSIGNED);
			}
			//check if meeting is assinged to the group
			if(group.getCustomerMeeting()==null||group.getCustomerMeeting().getMeeting()==null){
				throw new CustomerStateChangeException(GroupConstants.MEETING_NOT_ASSIGNED);
			}
		}
	}
 
	/** 
	 * This method is called to obtain all group details. It retreives group details based on 
	 * systemId(global customer number). If group is not found it throws group not found exception
	 * @param group with new staus id
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public void get(Context context) throws ApplicationException,SystemException{
		try{
		List<CustomerMaster> clients =null;
		GroupDAO groupDAO=this.getGroupDAO();
		
		String systemId = ((Group)context.getValueObject()).getGlobalCustNum();
		logger.debug("In Group Module retrieving group  by systemid: "+systemId);
		Group group = groupDAO.findBySystemId(systemId);
		if (group==null){
			Object values[]=new Object[2];
			values[0]=systemId;
			values[1]=labelConfig.getLabel(ConfigurationConstants.GROUP,context.getUserContext().getPereferedLocale());
			
			throw new CustomerNotFoundException(GroupConstants.GROUP_NOT_FOUND,values);
		}
		logger.debug("Group found and setting in valueobject. GroupId: "+group.getCustomerId()+" Group SystemId : "+group.getGlobalCustNum());
		context.setValueObject(group);
		if(Configuration.getInstance().getCustomerConfig(group.getOffice().getOfficeId()).isCenterHierarchyExists()){
			//set group parent. In case center hierarhcy does not exists, group parent will be null
			 context.addBusinessResults(GroupConstants.CENTER_HIERARCHY_EXIST, GroupConstants.YES);
			 context.addAttribute(this.getResultObject(GroupConstants.GROUP_PARENT, group.getParentCustomer()));
		}
		else{
			 context.addBusinessResults(GroupConstants.CENTER_HIERARCHY_EXIST, GroupConstants.NO);
			 context.addAttribute(this.getResultObject(GroupConstants.GROUP_PARENT, null));
		}
		
		CustomerUtilDAO customerUtilDAO = new CustomerUtilDAO();
		clients = customerUtilDAO.getCustomerChildren(group.getSearchId(),group.getOffice().getOfficeId(),null);
		context.addAttribute(this.getResultObject(GroupConstants.CLIENT_LIST,clients));
		
		
		CustomerHelper customerHelper = new CustomerHelper();
		short localeId=context.getUserContext().getLocaleId();
		
		int clientSize=0;
		if(clients!=null)
			clientSize = clients.size();
		logger.debug("no of clients under group are: "+clientSize);
		context.addAttribute(getResultObject(GroupConstants.GROUP_PERFORMANCE_VO,groupDAO.getPerformanceHistory(clientSize)));
	//	context.addAttribute(this.getResultObject(GroupConstants.CUSTOMER_PROGRAMS, new GroupHelper().loadCustomerPrograms(group,localeId)));
		context.addAttribute(new GroupHelper().getPositionsMaster(localeId));
		context.addAttribute(this.getResultObject(GroupConstants.CUSTOMER_POSITIONS, new GroupHelper().loadCustomerPositions(group,localeId,(List)(context.getSearchResultBasedOnName(GroupConstants.POSITIONS).getValue()))));
		context.addAttribute(new CustomerUtilDAO().getCustomFieldDefnMaster(CustomerConstants.GROUP_LEVEL_ID, GroupConstants.ENTITY_TYPE,CustomerConstants.CUSTOM_FIELDS_LIST));
		context.addAttribute(this.getResultObject(GroupConstants.CURRENT_STATUS,  helper.getStatusName(localeId,group.getStatusId(),CustomerConstants.GROUP_LEVEL_ID)));
		context.addAttribute(this.getResultObject(GroupConstants.NOTES, helper.getLatestNotes(GroupConstants.NOTES_COUNT,group.getCustomerId())));
		context.addBusinessResults(GroupConstants.LINK_VALUES,getLinkValues(group));
		context.addAttribute(this.getResultObject(GroupConstants.TOTAL_AMOUNT_DUE,	new ClosedAccSearchDAO().getTotalClientFeeChargesDue(group.getCustomerAccount().getAccountId())));
		CustomerBusinessService custBizService = (CustomerBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Customer);
		context.addBusinessResults("loanCycleCounter", custBizService.fetchLoanCycleCounter(group.getCustomerId()));
		logger.debug("after fetching loan cycle counter for group with id = " + group.getCustomerId() );
		checkGroupFlags(group,context);
		

		//TO display Loan nad Savings Accounts of a client
		List<LoanBO> loanAccounts = customerUtilDAO.getActiveLoanAccountsForCustomer(group.getCustomerId(),localeId);
		List<SavingsBO> savingsAcounts = customerUtilDAO.getActiveSavingsAccountsForCustomer(group.getCustomerId(),localeId);
		context.addAttribute(customerHelper.getResultObject(CustomerConstants.CUSTOMER_ACTIVE_LOAN_ACCOUNTS,loanAccounts));
		context.addAttribute(customerHelper.getResultObject(CustomerConstants.CUSTOMER_ACTIVE_SAVINGS_ACCOUNTS,savingsAcounts));
		
		}catch(SystemException se){
			throw se;
		}catch(ApplicationException ae){
			throw ae;
		}catch(Exception e ){
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		}
	}

	/**
	 * This method searches for the list of groups under a particualr office and its child offices(as per the data scope) 
	 * @param context This includes all the parameters passed from the action to the business processor layer.
	 * This search done base on groupName
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void getGroupList(Context context)throws SystemException,ApplicationException {
//		The search string which is entered byt he user 
		String searchString = context.getSearchObject().getFromSearchNodeMap("searchString");
		
		CustomerSearchInput customerSearchInput = (CustomerSearchInput)context.getBusinessResults(CustomerConstants.CUSTOMER_SEARCH_INPUT);
		
		//office id under which the list of centers should be displayed
		short officeId = customerSearchInput.getOfficeId();
		String searchId = new CustomerUtilDAO().getOffice(officeId).getSearchId();
		String searchType="GroupList";
		SearchDAO searchDAO=new SearchDAO();
		try{
			//puts the results obtained after the search into the context
			context.setSearchResult(searchDAO.search(searchType,searchString,context.getUserContext().getLevelId(),searchId ,context.getUserContext().getId(),context.getUserContext().getBranchId()));			
		}
		catch(SystemException se){
			throw se;
		}
		catch(ApplicationException ae){
			throw ae;
		}
		catch(Exception e){
			throw new CustomerException(CenterConstants.FATAL_ERROR_EXCEPTION , e);
		}
		
	}
	
	/** 
	 * This method is the helper method that returns instance of LinkParameters to show on top of jsps.
	 * @return group 
	 */ 
	private LinkParameters getLinkValues(Group group){
		LinkParameters linkParams = new LinkParameters();
		linkParams.setCustomerId(group.getCustomerId());
		linkParams.setCustomerName(group.getDisplayName());
		linkParams.setGlobalCustNum(group.getGlobalCustNum());
		linkParams.setCustomerOfficeId(group.getOffice().getOfficeId());
		linkParams.setCustomerOfficeName(group.getOffice().getOfficeName());
		linkParams.setLevelId(CustomerConstants.GROUP_LEVEL_ID);
		Customer parent = group.getParentCustomer();
		if(parent!=null){
			linkParams.setCustomerParentGCNum(parent.getGlobalCustNum());
			linkParams.setCustomerParentName(parent.getDisplayName());
		}
		return linkParams;
	}
 /** 
   * This method returns instance of GroupDAO
   * @return GroupDAO instance
   * @throws SystemException
   */  
	private GroupDAO getGroupDAO()throws SystemException{
		GroupDAO groupDAO=null;
		try{
			groupDAO = (GroupDAO)getDAO(PathConstants.GROUP_PATH);
		}catch(ResourceNotCreatedException rnce){
		}
	return groupDAO;
	}

	 /** 
	   * This method returns instance of CenterDAO
	   * @return CenterDAO instance
	   * @throws SystemException
	   */  
	private CenterDAO getCenterDAO()throws SystemException{
		CenterDAO centerDAO=null;
		try{
			centerDAO = (CenterDAO)getDAO(PathConstants.CENTER_PATH);
		}catch(ResourceNotCreatedException rnce){
			throw rnce;
		}
		return centerDAO;
	}

//	 /** 
//	   * This method returns instance of ViewClosedAccountsDAO
//	   * @return ViewClosedAccountsDAO instance
//	   * @throws SystemException
//	   */  
//	private ViewClosedAccountsDAO getViewClosedAccountsDAO()throws SystemException{
//		ViewClosedAccountsDAO viewClosedAccountsDAO=null;
//		try{
//			viewClosedAccountsDAO = (ViewClosedAccountsDAO)getDAO();
//		}catch(ResourceNotCreatedException rnce){
//			throw rnce;
//		}
//		return viewClosedAccountsDAO;
//	}

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
	
	private void  checkGroupFlags(Group groupVO,Context context)throws ApplicationException,SystemException{
		Set customerFlag=groupVO.getCustomerFlag();
		boolean isBlackListed=false;
		boolean isCurrentBlack=false;
		String blacklistedName=null;
		String flagName=null;
		if(null!=customerFlag){
			Object object[]=customerFlag.toArray();
			for(int i=0;i<object.length ;i++){
				short flagId=((CustomerFlag)object[i]).getFlagId();
				isCurrentBlack = helper.isBlacklisted(flagId);
				
				if(!isBlackListed && isCurrentBlack){
					blacklistedName=helper.getFlagName(flagId,context.getUserContext().getLocaleId());
					isBlackListed=isCurrentBlack;
				}

				if(!isCurrentBlack)
					flagName=helper.getFlagName(flagId,context.getUserContext().getLocaleId());
			}
			// set the flag name and blacklisted in context
			
			context.addAttribute(getResultObject(GroupConstants.CURRENT_FLAG,flagName));
			context.addAttribute(getResultObject(GroupConstants.IS_BLACKLISTED,isBlackListed));
			context.addAttribute(getResultObject(GroupConstants.BLACKLISTED_FLAG_NAME,blacklistedName));
		}
	}
	
	/**
	 * This is the helper method that returns the office of the user whose id has been passed
	 * @param userId id of the user
	 * @return office of the user, whose id is passed in as parameter 
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private Office getUserOffice(short userId)throws ApplicationException,SystemException{
		Personnel loggedInUSer = new PersonnelDAO().getUser(userId);
		return new OfficeDAO().getOffice(loggedInUSer.getOffice().getOfficeId());
	}
	
	private void checkPermissionForStatusChange(Short newState,UserContext userContext,Short flagSelected,Short recordOfficeId,Short recordLoanOfficerId) throws SecurityException{
		if(!isPermissionAllowed(newState,userContext,flagSelected,recordOfficeId,recordLoanOfficerId,false))
			  throw new SecurityException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED); 	 
	}
	
	private boolean isPermissionAllowed(Short newState,UserContext userContext,Short flagSelected,Short recordOfficeId,Short recordLoanOfficerId,boolean saveFlag){
		if(saveFlag)return ActivityMapper.getInstance().isSavePermittedForCustomer(newState.shortValue(),userContext,recordOfficeId,recordLoanOfficerId);
		else return ActivityMapper.getInstance().isStateChangePermittedForCustomer(newState.shortValue(),null!=flagSelected?flagSelected.shortValue():0,userContext,recordOfficeId,recordLoanOfficerId);
	}
}



