/**

 * GroupDAO.java    version: 1.0



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

package org.mifos.application.customer.group.dao;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.IDGenerator;
import org.mifos.application.accounts.util.valueobjects.Account;
import org.mifos.application.accounts.util.valueobjects.AccountActionDate;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.accounts.util.valueobjects.CustomerAccount;
import org.mifos.application.configuration.business.ConfigurationIntf;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.client.dao.ClientCreationDAO;
import org.mifos.application.customer.client.dao.EditGrpMembershipDAO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.dao.CustomerNoteDAO;
import org.mifos.application.customer.dao.CustomerUtilDAO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.exceptions.CustomerTransferException;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.group.util.helpers.GroupHelper;
import org.mifos.application.customer.group.util.helpers.GroupTransferInput;
import org.mifos.application.customer.group.util.valueobjects.Group;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.customer.util.helpers.IdGenerator;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerFlag;
import org.mifos.application.customer.util.valueobjects.CustomerHierarchy;
import org.mifos.application.customer.util.valueobjects.CustomerMaster;
import org.mifos.application.customer.util.valueobjects.CustomerMovement;
import org.mifos.application.customer.util.valueobjects.CustomerNote;
import org.mifos.application.office.dao.OfficeDAO;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.components.audit.util.helpers.AuditConstants;
import org.mifos.framework.components.audit.util.helpers.LogInfo;
import org.mifos.framework.components.audit.util.helpers.LogValueMap;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ConcurrencyException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryInputs;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;


/**
 * This class denotes the DAO layer for the group module.
 */

public class GroupDAO extends DAO {
	/**An instance of the logger which is used to log statements */
	private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.GROUP_LOGGER);
	private ConfigurationIntf labelConfig=MifosConfiguration.getInstance();
	/**
	 * This method is called to create the group. It persists the data entered for the group in the database.
	 * @param context instance of Context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void create(Context context)throws ApplicationException,SystemException {
	  Group groupVO =(Group)context.getValueObject();
	  logger.debug("in method create() of group with customerId:"+groupVO.getCustomerId());
	  CustomerHelper helper = new CustomerHelper();
	  
	  groupVO.setCustomerDetail(null);
	  groupVO.setCustomerId(null);
	  groupVO.setCustomerPositions(null);
	  groupVO.getCustomerAddressDetail().setCustomer(groupVO);
	  groupVO.getCustomerAddressDetail().setCustomerAddressId(null);
	  
	  //groupVO.getCustomerNameDetail().setCustomer(groupVO);
	  
	  /*//set customer program
	  this.setCustomerProgram(groupVO.getCustomerProgram());*/
	  
	  //set customer account
	  setCustomerAccountAssociations(groupVO,context.getUserContext().getId());
	  
	  //set customer historical data to null
	  groupVO.setCustomerHistoricalData(null);
	  Transaction tx = null;
	  Session session = null;
	  CustomerHierarchy customerHierarchy = null;
	  
	  //check for parent  
	  Customer parent=null;
	  SearchResults sr =context.getSearchResultBasedOnName(GroupConstants.GROUP_PARENT);
	  if(null!=sr && sr.getValue()!=null){
		  parent=(Customer)sr.getValue();
		  logger.debug("*** Parent found with id: "+parent.getCustomerId());
		  customerHierarchy = helper.createCustomerHierarchy(parent, groupVO, context);
	  }
	  groupVO.convertCustomFieldDateToDbformat(context.getUserContext().getMfiLocale());
  
	  try {
		session = HibernateUtil.getSession();
		tx = session.beginTransaction();
		//set searchid for the group
		groupVO.setSearchId(new GroupHelper().getSearchId(parent,groupVO.getOffice().getOfficeId()));
		  
		if(groupVO.getCustomerMeeting()!=null && groupVO.getCustomerMeeting().getMeeting()!=null)
			groupVO.getCustomerMeeting().setUpdatedFlag(YesNoFlag.NO.getValue());
		
		if (null!=parent && null != customerHierarchy){
			//parent.setCustomerHistoricalData(null);
			logger.debug("updating parent with id: "+parent.getCustomerId()+" & version no: "+parent.getVersionNo());
			makeCustomerAssociationsNullForUpdate(parent);
			session.update(parent);
			session.save(customerHierarchy);
		}
		else{
			//	save meeting only if user has selected any
			if(groupVO.getCustomerMeeting()!=null && groupVO.getCustomerMeeting().getMeeting()!=null)
				session.save(groupVO.getCustomerMeeting().getMeeting());
		}
		logger.debug("--------------------- creating  group with id: "+groupVO.getCustomerId());
		logger.debug("----------------------dao: "+ this.toString());
		logger.debug("----------------------valueobject: "+ groupVO.toString());
		//to be changed after creating group
		groupVO.setGlobalCustNum("xx");
		//save the group value object
		session.save(groupVO);
		
		//set the global cust num for the customer
		//String gCustNum = helper.generateSystemId(groupVO.getOffice().getOfficeId() ,groupVO.getOffice().getGlobalOfficeNum());
		logger.debug("-----group created with----------------------groupVO.getCustomerId(): "+ groupVO.getCustomerId());
		String gCustNum=IdGenerator.generateSystemIdForCustomer(groupVO.getOffice().getGlobalOfficeNum(),groupVO.getCustomerId());
		groupVO.setGlobalCustNum(gCustNum);
		
		//helper.saveMeetingDetails(groupVO,session, context.getUserContext());
		//update group
		session.update(groupVO);
		

		tx.commit();
	  } catch(StaleStateException sse){
		  tx.rollback();
		  throw new CustomerException(GroupConstants.GROUP_INVALID_VERSION,sse,new Object[]{groupVO.getDisplayAddress(),labelConfig.getLabel(ConfigurationConstants.GROUP,context.getUserContext().getPereferedLocale())});
	  }catch(org.hibernate.exception.LockAcquisitionException lae){
		  throw new CustomerException(GroupConstants.CREATE_FAILED,lae,new Object[]{labelConfig.getLabel(ConfigurationConstants.GROUP,context.getUserContext().getPereferedLocale())});
	  }
	  	catch (HibernateProcessException hpe){
	  		tx.rollback();
			throw hpe;
		}catch (HibernateException hpe)	{ 
			tx.rollback();
			throw new CustomerException(GroupConstants.CREATE_FAILED,hpe,new Object[]{labelConfig.getLabel(ConfigurationConstants.GROUP,context.getUserContext().getPereferedLocale())});
		}catch (Exception e)	{ 
			tx.rollback();
			throw new CustomerException(GroupConstants.CREATE_FAILED,e,new Object[]{labelConfig.getLabel(ConfigurationConstants.GROUP,context.getUserContext().getPereferedLocale())});
		}
		 finally {
			HibernateUtil.closeSession(session);
		}
  }
	
	/**
	 * This is the helper method that sets customer account associations
	 * @param groupVO instance of Group for which account associations need to be set
	 * @param loggedInUserId is of the logged in user
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	private void setCustomerAccountAssociations(Group groupVO,short loggedInUserId)throws ApplicationException, SystemException{
		CustomerAccount customerAccount = groupVO.getCustomerAccount();
		if(groupVO.getPersonnel()!=null && groupVO.getPersonnel().getPersonnelId()!=null)
			customerAccount.setPersonnelId(groupVO.getPersonnel().getPersonnelId());
		
		customerAccount.setOfficeId(groupVO.getOffice().getOfficeId());
		customerAccount.setAccountTypeId(
				AccountTypes.CUSTOMERACCOUNT.getValue());
		// setting the customer account state to active. To be reset when
		// cancelled, deleted, on hold or withdrawn
		customerAccount.setAccountStateId(Short.valueOf(AccountStates.CUSTOMERACCOUNT_ACTIVE));
		
		//set customer to account
		customerAccount.setCustomer(groupVO);
		
		customerAccount.setGlobalAccountNum(IDGenerator.generateIdForCustomerAccount(groupVO.getOffice().getGlobalOfficeNum()));
		customerAccount.setCreatedBy(loggedInUserId);
		customerAccount.setCreatedDate(new CustomerHelper().getCurrentDate());
		if (groupVO.getCustomerAccount() != null 
				&& groupVO.getCustomerAccount().getAccountFeesSet() != null
					&& groupVO.getCustomerAccount().getAccountFeesSet().size() == 0){
				groupVO.getCustomerAccount().setAccountFeesSet(null);
		}
		logger.debug("account associations set for group with id: "+groupVO.getCustomerId());
	}
 
	/** 
	 *  This method checks if the group name already exists in the system.If yes it returns true, otherwise false.
	 *  The group name has to be unique across the branch.
	 *  This is called just before creating a new group in the database.
	 *  @param groupname The name for which duplicacy is checked
	 *  @param officeid office in which group name should be unique
	 *  @return returns true or false as to whether the group name exists
	 *  @throws SystemException
	 */	
  public boolean isGroupNameExist(String groupname, int officeid) throws SystemException {
	  	  logger.debug("checking group name duplicay for groupname:"+groupname + "  in office: "+ officeid);
		  HashMap queryParameters = new HashMap();
		  queryParameters.put("gpname",groupname);
		  queryParameters.put("offid",officeid);
		  queryParameters.put("levelId",CustomerConstants.GROUP_LEVEL_ID);
		  List queryResult = executeNamedQuery(NamedQueryConstants.IS_GROUP_NAME_EXIST,queryParameters);
		  if(null!=queryResult){
			  if(queryResult.size()==0 || queryResult.get(0)==null)
				  return false;
			  else
				  return true;
		  }
		  return false;  
  }
  
  /** 
	 *  This method is called when group is transferred across center. Group's parent is updated in the database.
	 *  It makes old entry in customer hierarchy historical by changing hierarhcy state and 
	 *  creates a new customer hierarhcy for the group in the database.
	 *  If the selected center belogns to differrent branch, a record in customer movement table is also inserted.
	 *  @param oldCustomer The group who has transferred
	 *  @param context instance of Context
	 *  @throws ApplicationException
	 *  @throws SystemException
	 */	
  	public void updateParent(Group oldCustomer, List clientList,Context context)throws ApplicationException ,SystemException{
  		logger.debug("updating parent for customer: "+oldCustomer.getCustomerId());
		Transaction tx = null;
		Session session = null;
		CustomerHierarchy customerHierarchy = null;
		Customer parent = null;
		SearchResults sr =context.getSearchResultBasedOnName(GroupConstants.GROUP_PARENT);
		//GroupTransferInput gpTransferInput = (GroupTransferInput)context.getBusinessResults(GroupConstants.GROUP_TRANSFER_INPUT);
		if(null!=sr && null!=sr.getValue()){
			parent=(Customer)sr.getValue();
			CustomerHelper helper = new CustomerHelper();
			customerHierarchy = helper.createCustomerHierarchy(parent, oldCustomer, context);
		}
		
		//if change in office insert record in customer movement
		CustomerMovement firstCustomerMovement = null;
		CustomerMovement nextCustomerMovement = null;
		
		//if new center belons to different branch, create a customer movement entry, 
		//since group is being transferred to differrent branch
		if(oldCustomer.getOffice().getOfficeId().shortValue()!=parent.getOffice().getOfficeId().shortValue()){
			logger.debug("group is being transferred in diferrent office, therefore creating customer movement entries");
			//customerMovement = new CustomerHelper().createCustomerMovement(oldCustomer);
			firstCustomerMovement = new CustomerHelper().createCustomerMovement(oldCustomer,oldCustomer.getCreatedDate(),CustomerConstants.INACTIVE_HIERARCHY,context.getUserContext().getId());
			
			//update office for the customer
			oldCustomer.setOffice(parent.getOffice());
			
			//create next customer movement
			nextCustomerMovement = new CustomerHelper().createCustomerMovement(oldCustomer,null,CustomerConstants.ACTIVE_HIERARCHY,context.getUserContext().getId());
		}
		
		//get oldParent, and set its max child count less by 1
		Customer oldParent = oldCustomer.getParentCustomer();
		oldParent.setMaxChildCount(oldParent.getMaxChildCount()-1);
		logger.debug("old parent for group is :"+oldParent.getCustomerId());
		//update parent for group 
		oldCustomer.setParentCustomer(parent);
		logger.debug("new parent for group is :"+parent.getCustomerId());
		//update Meeting for group
		oldCustomer.getCustomerMeeting().setMeeting(parent.getCustomerMeeting().getMeeting());
		oldCustomer.getCustomerMeeting().setUpdatedFlag(YesNoFlag.YES.getValue());
		
		//update searchId
		oldCustomer.setSearchId(new GroupHelper().getSearchId(parent,oldCustomer.getOffice().getOfficeId()));

		//update loan officer
		oldCustomer.setPersonnel(parent.getPersonnel());

		//make associations null which are not required to update
		makeCustomerAssociationsNullForUpdate(oldCustomer);
		
		try {
			//session = getHibernateSession();
			//for change log
			 LogValueMap  objectMap = new LogValueMap();
			  objectMap.put(AuditConstants.REALOBJECT,new Customer());
			  objectMap.put("parentCustomer",AuditConstants.REALOBJECT);
			  LogInfo logInfo= new LogInfo(oldCustomer.getCustomerId(),GroupConstants.GROUP,context,objectMap);
			  session=HibernateUtil.getSessionWithInterceptor(logInfo);

			tx = session.beginTransaction();
			
			//update max child count of old parent
			session.update(oldParent);

			//update maxchild count of the new parent
			session.update(parent);
			
			//update group to inherit from new parent
			session.update(oldCustomer);
			
			// make old hierarhcy inactive
			this.makeCurrentCustomerHierarchyInActive(session,oldCustomer.getGlobalCustNum(),context);
			
			//make the old branch association inactive			
			if(null!=firstCustomerMovement)
				this.makeCurrentBranchAssociationInActive(session,oldCustomer.getCustomerId(),firstCustomerMovement,context.getUserContext().getId());
			
			// create new customer hierarchy
			if(null!=customerHierarchy)
				session.save(customerHierarchy);

			// insert record in customer movement
			if(null!=nextCustomerMovement){
				session.save(nextCustomerMovement);
			}
			//transfer clients also if group is transferred to center in a differrent branch
			transferClients(oldCustomer,clientList,tx,session,context.getUserContext());
			tx.commit();
		  } catch(StaleObjectStateException sose){
		   		tx.rollback();
				throw new ConcurrencyException(ExceptionConstants.CONCURRENCYEXCEPTION,sose);
		   	}
		  	catch (HibernateProcessException hpe) {
		  		tx.rollback();
				throw hpe;
			}catch (HibernateException hpe) {
				tx.rollback();
				throw new CustomerException(GroupConstants.UPDATE_FAILED,hpe,new Object[]{labelConfig.getLabel(ConfigurationConstants.GROUP,context.getUserContext().getPereferedLocale())});
			}
		  	finally {
				HibernateUtil.closeSession(session);
			}
}
  	
 	/** 
	 *  This method is called when current client is being moved to another branch.
	 *  It accepts the current session and does an update on customer movement across the different branches.
	 *  @param session it holds the current hibernate session
	 *  @param customerId the id of the customer to be moved
	 *  @param firstCustomerMovement instance of customermovement
	 *  @param userId id of the logged in user  
	 *  @throws SystemException
	 */	  	
  	private void makeCurrentBranchAssociationInActive(Session session , Integer customerId,CustomerMovement firstCustomerMovement,Short userId)throws SystemException{
  		CustomerHelper helper = new CustomerHelper();
  		CustomerMovement customerMovement = null;
  		HashMap queryParameters = new HashMap();
		queryParameters.put("customerId", customerId);
		queryParameters.put("status",CustomerConstants.ACTIVE_HIERARCHY);
		//makes the current customer branch association inactive
		List queryResult=executeNamedQuery(NamedQueryConstants.GET_CUSTOMER_MOVEMENT,queryParameters,session);
		
		if(null!=queryResult && queryResult.size()>0){
			Object obj = queryResult.get(0);
			if(obj!=null)
				customerMovement = (CustomerMovement)obj;
		}
		if(null!=customerMovement){
			customerMovement.setStatus(CustomerConstants.INACTIVE_HIERARCHY);
			customerMovement.setEndDate(helper.getCurrentDate());
			customerMovement.setUpdatedBy(userId);
			customerMovement.setUpdatedDate(helper.getCurrentDate());
			session.update(customerMovement);
		}
		else{
			firstCustomerMovement.setEndDate(helper.getCurrentDate());
			firstCustomerMovement.setUpdatedBy(userId);
			firstCustomerMovement.setUpdatedDate(helper.getCurrentDate());
			session.save(firstCustomerMovement);
		}
		
		  
  	}
	/** 
	 *  This method is called when group is transferred in different branch and center hierarchy does not exists.
	 *  Group's office is updated in the database and an entry is also made to customer movement table
	 *  @param oldCustomer The group who has transferred
	 *  @param context instance of Context
	 *  @throws ApplicationException
	 *  @throws SystemException
	 */	
  	public void updateBranchRSM(Group oldCustomer, List clientList, Context context)throws ApplicationException,SystemException{
		Transaction tx = null;
		Session session = null;
		GroupTransferInput gpTransferInput = (GroupTransferInput)context.getBusinessResults(GroupConstants.GROUP_TRANSFER_INPUT);
		
		//customerMovement = new CustomerHelper().createCustomerMovement(oldCustomer);
		CustomerMovement firstCustomerMovement = new CustomerHelper().createCustomerMovement(oldCustomer,oldCustomer.getCreatedDate(),CustomerConstants.INACTIVE_HIERARCHY,context.getUserContext().getId());
		
		//update office for the customer
		oldCustomer.setOffice(new CustomerUtilDAO().getOffice(gpTransferInput.getOfficeId()));
		
		//create next customer movement
		CustomerMovement nextCustomerMovement = new CustomerHelper().createCustomerMovement(oldCustomer,null,CustomerConstants.ACTIVE_HIERARCHY,context.getUserContext().getId());
		
		//set searchid for the group
		oldCustomer.setSearchId(new GroupHelper().getSearchId(null,oldCustomer.getOffice().getOfficeId()));
		try {
			//session = getHibernateSession();
			//for change log
			 LogValueMap  objectMap = new LogValueMap();
			 objectMap.put(AuditConstants.REALOBJECT,new Customer());
			 LogInfo logInfo= new LogInfo(oldCustomer.getCustomerId(),GroupConstants.GROUP,context,objectMap);
			  
			 session=HibernateUtil.getSessionWithInterceptor(logInfo);
			  
			tx = session.beginTransaction();
			OfficeDAO officeDAO = new OfficeDAO();
			oldCustomer.setOffice(officeDAO.getOffice(gpTransferInput.getOfficeId()));
			
			makeCustomerAssociationsNullForUpdate(oldCustomer);
			//set meeting to null
			oldCustomer.setCustomerMeeting(null);
			session.update(oldCustomer);
			
			//make current customer movement to null
			this.makeCurrentBranchAssociationInActive(session,oldCustomer.getCustomerId(),firstCustomerMovement,context.getUserContext().getId());
			
			//insert a record in the customer movement table
			session.save(nextCustomerMovement);
			
			//transfer every client under group also
			transferClients(oldCustomer,clientList,tx,session,context.getUserContext());
			
			//commit the transaction
			tx.commit();
		  }catch(StaleObjectStateException sose){
		   		tx.rollback();
				throw new ConcurrencyException(ExceptionConstants.CONCURRENCYEXCEPTION,sose);
		   	}catch (HibernateProcessException hpe) {
			  	tx.rollback();
				throw hpe;
			}catch (HibernateException hpe) {
				tx.rollback();
				throw new CustomerException(GroupConstants.UPDATE_FAILED,hpe,new Object[]{labelConfig.getLabel(ConfigurationConstants.GROUP,context.getUserContext().getPereferedLocale())});
			}  finally {
				HibernateUtil.closeSession(session);
			}
  	}
  	
  	private void transferClients(Customer group,List clientList,Transaction tx,Session session, UserContext userContext)throws SystemException,ApplicationException{
		if(null!=clientList && clientList.size()>0){
			EditGrpMembershipDAO grpMembershipDAO = new EditGrpMembershipDAO();
			try{
				for(int i=0;i<clientList.size();i++){
					CustomerMaster client = (CustomerMaster)clientList.get(i);
					String clientSearchId=group.getSearchId()+"."+(i+1);
					logger.debug("----- clientSearchId : " + clientSearchId);
					//transfer client
					grpMembershipDAO.updateClientForGroup(session,client.getCustomerId(),clientSearchId,group.getOffice(),group.getCustomerMeeting(),group.getPersonnel(),userContext);
					logger.debug("----- successfully transferred client with id: "+client.getCustomerId());
				}
			}catch(Exception e){
				e.printStackTrace();
				tx.rollback();
				throw new CustomerTransferException(GroupConstants.TRANSFER_EX_CLIENT_TRANSFER_FAILED);
			}
		}
  	}
  	
  	/** 
	 *  This method is called to update the group status and respective flag (if any).
	 *  If the group was assigned with blacklisted flag in past, it donot remove blacklisted flag 
	 *  @param context instance of Context
	 *  @param oldCustomer The group whose status is to be updated
	 *  @throws ApplicationException
	 *  @throws SystemException
	 */	
  	public void updateStatus(Context context,Customer oldCustomer)throws ApplicationException,SystemException{
		Transaction tx = null;
		Session session = null;
		Group groupVO =(Group)context.getValueObject();
		short oldStatus=oldCustomer.getStatusId();
		short newStatus=groupVO.getStatusId();
		
		
		CustomerNoteDAO customerNoteDAO = new CustomerNoteDAO();
		CustomerNote customerNote = groupVO.getCustomerNote();
		//make associations null that need not be updated
		makeCustomerAssociationsNullForUpdate(oldCustomer);
		  try {
			//session = getHibernateSession();
			//for change log
			  LogValueMap  objectMap = new LogValueMap();
			objectMap.put(AuditConstants.REALOBJECT,new Customer());
			objectMap.put("customerFlag",AuditConstants.REALOBJECT);
			
			LogInfo logInfo= new LogInfo(oldCustomer.getCustomerId(),GroupConstants.GROUP,context,objectMap);
			
			session=HibernateUtil.getSessionWithInterceptor(logInfo);
			tx = session.beginTransaction();
			
			oldCustomer.setStatusId(newStatus);
			
			CustomerHelper helper=helper=new CustomerHelper();
			/*if(!Configuration.getInstance().getCustomerConfig(oldCustomer.getOffice().getOfficeId()).isCenterHierarchyExists() && groupVO.getStatusId().shortValue()==GroupConstants.ACTIVE && oldCustomer.getCustomerActivationDate()==null)
					helper.saveMeetingDetails(oldCustomer,session, context.getUserContext());*/
			
			
			//check if group is being active for the first time
			if(groupVO.getStatusId().shortValue()==GroupConstants.ACTIVE && oldCustomer.getCustomerActivationDate()==null){
				oldCustomer.setCustomerActivationDate(helper.getCurrentDate());
				CustomerUtilDAO.applyFees(oldCustomer,session);
			}
			
//			set meeting to null
			oldCustomer.setCustomerMeeting(null);
	
			session.update(oldCustomer);
			customerNoteDAO.addNotes(session,customerNote);
			
			// handle group flags
			handleGroupFlags(session,groupVO,oldCustomer, context.getUserContext().getId());
			
			//if group status is changed from pending approval to cancel, change all of its clients to partial application
			
			if(oldStatus==GroupConstants.PENDING_APPROVAL && newStatus==GroupConstants.PARTIAL_APPLICATION)
				updateAllGroupClientsToPartialApplication(session,groupVO.getSearchId(),oldCustomer.getOffice().getOfficeId());
				

			
			
			tx.commit();
		  }catch(StaleObjectStateException sose){
		   		tx.rollback();
				throw new ConcurrencyException(ExceptionConstants.CONCURRENCYEXCEPTION,sose);
		   	}
		  catch (HibernateProcessException hpe) {
			  tx.rollback();
				throw hpe;
			}catch (HibernateException hpe) {
				tx.rollback();
				throw new CustomerException(GroupConstants.UPDATE_FAILED,hpe,new Object[]{labelConfig.getLabel(ConfigurationConstants.GROUP,context.getUserContext().getPereferedLocale())});
			}  finally {
				HibernateUtil.closeSession(session);
			}
  	}
  	
	/** 
	 *  This method is helper method that handles group flags
	 *  If the group was assigned with blacklisted flag in past, it won't remove blacklisted flag
	 *  @param session instance of hibernate session 
	 *  @param groupVO hold the new status and new flag for the customer
	 *  @param oldCustomer The old customer before updating flag ans status
	 *  @param createdBy logged in user id
	 *  @throws ApplicationException
	 *  @throws SystemException
	 */	
	private void handleGroupFlags(Session session, Group groupVO, Customer oldCustomer, short createdBy)throws ApplicationException,SystemException{
		boolean isBlacklisted=false;
		
		Set customerFlag = oldCustomer.getCustomerFlag();
		if(customerFlag!=null){
			Object flagObjects[]=customerFlag.toArray();
			for(int i=0;i<flagObjects.length;i++){
				CustomerFlag flag = (CustomerFlag)flagObjects[i];
				if(new CustomerHelper().isBlacklisted(flag.getFlagId())){
					isBlacklisted = true;
					logger.debug("blacklisted flag exist in database for customer: "+ oldCustomer.getCustomerId());
				}else{
					session.delete(flag);
				}
			}
		}
		//save the flag in the database if group flag is not blacklisted, or group is made blacklisted for the first time.
		if(groupVO.getFlagId()!=null && groupVO.getFlagId()!=0){
			if(groupVO.getFlagId()!=GroupConstants.BLACKLISTED || !isBlacklisted){
				CustomerFlag cf = new CustomerFlag();
				cf.setCustomer(oldCustomer);
				cf.setFlagId((short)groupVO.getFlagId().intValue());
				cf.setCreatedBy(createdBy);
				cf.setCreatedDate(new CustomerHelper().getCurrentDate());
				cf.setFlagStatus(Short.valueOf("1"));
				session.save(cf);
			}	
		}
	
	}
		
    /** 
	 *  This method is called when current group hierarchy state is to be changed to historical.
	 *  It accepts the current session and does an update on customerhierarchy.
	 *  If the group was assigned with blacklisted flag in past, it donot remove blacklisted flag 
	 *  @param session it holds the current hibernate session
	 *  @param globalCustNum The group system id
	 *  @param context instance of Context  
	 *  @throws SystemException
	 */	  	
  	private void makeCurrentCustomerHierarchyInActive(Session session , String globalCustNum,Context context)throws SystemException{
  		HashMap queryParameters = new HashMap();
		queryParameters.put("globalCustNum", globalCustNum);
		queryParameters.put("status",CustomerConstants.ACTIVE_HIERARCHY);
		
		List queryResult=executeNamedQuery(NamedQueryConstants.GET_CUSTOMER_HIERARCHY,queryParameters,session);
		CustomerHierarchy customerHierarchy = null;
		if(null!=queryResult && queryResult.size()>0){
			Object obj = queryResult.get(0);
			if(obj!=null)
				customerHierarchy = (CustomerHierarchy)obj;
		}
		
		if(null!=customerHierarchy){
			//set current customer hierarchy state to inactive
			customerHierarchy.setStatus(CustomerConstants.INACTIVE_HIERARCHY);
			customerHierarchy.setEndDate(new CustomerHelper().getCurrentDate());
			customerHierarchy.setUpdatedBy(context.getUserContext().getId());
			customerHierarchy.setUpdatedDate(new CustomerHelper().getCurrentDate());
			session.update(customerHierarchy);
		}
		  
  	}

//	/** 
//	 *  This method is called when group status is changed to closed.
//	 *  For all clients belonging to this group, it updates client's customer hierarchy to historical.
//	 *  It accepts the current session and does an update on customerhierarchy.
//	 *  @param session it holds the current hibernate session
//	 *  @param group The group whose client's hierarchy is to be made inactive. 
//	 *  @throws SystemException
//	 */	  	
//  	private void updateParentHierarchyForClients(Session session , Group group)throws SystemException{
////		String hqlUpdate = "update CustomerHierarchy  customerHierarchy set customerHierarchy.status = :status where customerHierarchy.parentCustomer.customerId = :parentId";
////	    int updatedEntities = session.createQuery(hqlUpdate)
////	                        .setShort( "status", CustomerConstants.INACTIVE_HIERARCHY)
////	                        .setInteger( "parentId", group.getCustomerId())
////	                        .executeUpate();
//  	}
  	
  	/** 
	 *  This method is called when group status validations are being done.
	 *  It checks if any of the group client is in active state. if yes it returns true, otherwise false
	 *  It accepts the current session and does an update on customerhierarchy.
	 *  @param groupId 
	 *  @throws SystemException
	 */	  	
  	public boolean isGroupClientsAreActive(int groupId)throws SystemException{
  		HashMap queryParameters = new HashMap();
  		Integer count=0;
		queryParameters.put("levelId", CustomerConstants.CLIENT_LEVEL_ID);
		queryParameters.put("parentId", groupId);
		queryParameters.put("statusId", ClientConstants.STATUS_CANCELLED);
		queryParameters.put("otherStatusId", ClientConstants.STATUS_CLOSED);
		List queryResult = executeNamedQuery(NamedQueryConstants.COUNT_ACTIVE_CUSTOMERS_FOR_PARENT,queryParameters);
		if(null!=queryResult && queryResult.size()>0){
			Object obj = queryResult.get(0);
			if(obj!=null)
				count = (Integer)obj;
		}
		return (count.intValue())>0 ? true : false;	
  	}

  	
 	
	/**
	 * This method is called to update group in the database.
	 * It obtains valueobject from the context.
	 * The customerAccount is set null since along with group we don't want to update them but they are
	 * created when group is created for the first time.
	 * @param context instance of Context   
	 * @throws ApplicationException
	 * @throws SystemException
	 */	
  	public void update(Context context) throws ApplicationException,SystemException{
		Group groupVO =(Group)context.getValueObject();
		logger.debug("updating group with id : "+ groupVO.getCustomerId());
		Session session = null;
		Transaction tx=null;
		
		/*//setting the customer program association :Feature removed
		this.setCustomerProgram(groupVO.getCustomerProgram());*/
		//groupVO.setCustomerPositions(new GroupHelper().checkForNullCustomerPositions(groupVO.getCustomerPositions()));
		SearchResults listOfClients =context.getSearchResultBasedOnName(GroupConstants.CLIENT_LIST);
		List clients= (List)listOfClients.getValue();	
		if(clients==null||clients.size() == 0){
			groupVO.setCustomerPositions(null);
		}
		groupVO.convertCustomFieldDateToDbformat(context.getUserContext().getMfiLocale());
		try{
			  //session = HibernateUtil.getSession();
			  //for change log
			  LogValueMap  objectMap = new LogValueMap();
			  objectMap.put(AuditConstants.REALOBJECT,new Customer());
			  objectMap.put("personnel",AuditConstants.REALOBJECT);
			  objectMap.put("customerAddressDetail",AuditConstants.REALOBJECT);
			  objectMap.put("customerPositions",AuditConstants.REALOBJECT);
			  objectMap.put("customFieldSet",AuditConstants.REALOBJECT);
			  objectMap.put("customerPosition","customerPositions");
			  LogInfo logInfo= new LogInfo(groupVO.getCustomerId(),GroupConstants.GROUP,context,objectMap);
			  
			  session=HibernateUtil.getSessionWithInterceptor(logInfo);
			  tx = session.beginTransaction();
			  //customer accounts are not updated while updating group
		   	  groupVO.setCustomerAccount(null);
		   	  //update group in the database
			  session.update(groupVO);
			  Object obj  = context.getBusinessResults(GroupConstants.GROUP_LO);

			  //update loan officer for clients when center hierarchy does not exists
			  if(!Configuration.getInstance().getCustomerConfig(groupVO.getOffice().getOfficeId()).isCenterHierarchyExists()){
				  if(groupVO.getPersonnel()!=null && groupVO.getPersonnel().getPersonnelId()!=null){
					  if(obj==null || !((Short)obj).equals(groupVO.getPersonnel().getPersonnelId())){
						  String hql="update Customer customer set customer.personnelId="+groupVO.getPersonnel().getPersonnelId()+" where customer.searchId like '"+groupVO.getSearchId()+".%' and customer.office.officeId="+groupVO.getOffice().getOfficeId();
						  session.createQuery(hql).executeUpate();
					  }
				  }else{
					  if(obj!=null){
						  String hql="update Customer customer set customer.personnelId=null where customer.searchId like '"+groupVO.getSearchId()+".%' and customer.office.officeId="+groupVO.getOffice().getOfficeId();
						  session.createQuery(hql).executeUpate();
					  }
				  }
			  }
			  tx.commit();
		}catch(StaleObjectStateException sose){
	   		tx.rollback();
			throw new ConcurrencyException(ExceptionConstants.CONCURRENCYEXCEPTION,sose);
	   	}
		 catch (HibernateProcessException hpe) {
			 tx.rollback();
			throw hpe;
		 }catch (HibernateException he) {
			 tx.rollback();
			throw new CustomerException(GroupConstants.UPDATE_FAILED,he,new Object[]{labelConfig.getLabel(ConfigurationConstants.GROUP,context.getUserContext().getPereferedLocale())});
		 }
		 finally{
			HibernateUtil.closeSession(session);
		}
	  }
  	
	/**
	 * This method obtains the group details based on global customer number(systemId).
	 * If group not found it returns null
	 * @param systemId The system id for which group is to be retrieved
	 * return an instance of Group
 	 * @throws SystemException
	 */
	public Group findBySystemId(String systemId)throws SystemException {
		Group group = null;
		Session session = null;
		try{
			session = HibernateUtil.getSession();
			HashMap queryParameters = new HashMap();
			queryParameters.put("globalCustNum", systemId);
			List queryResult=executeNamedQuery(NamedQueryConstants.FIND_CUSTOMER_BY_SYSTEM_ID,queryParameters,session);
			if(null!=queryResult && queryResult.size()>0){
				Object obj = queryResult.get(0);
				if(obj!=null)
					group = (Group)obj;
			}
			
			if(group==null)
				return null;
			logger.debug("group retrieved successfully with systemid: "+systemId);
			//call specific getters to load lazy associations
			
			//get group loan officer
			if(group.getPersonnel()!=null)
				group.getPersonnel().getDisplayName();
			if(group.getCustomerFormedByPersonnel()!=null)
				group.getCustomerFormedByPersonnel().getDisplayName();
			
			//get group parent
			Customer parent =group.getParentCustomer();
			if(parent!=null)
				parent.getDisplayName();
			/*	
			//get group programs
			group.getCustomerProgram();*/
			
			//get client positions for group 
			group.getCustomerPositions();
			
			//get custom fied values for group
			group.getCustomFieldSet();
			
			//get group address details
			group.getCustomerAddressDetail().getPhoneNumber();
			
			//get office of the group
			group.getOffice().getGlobalOfficeNum();
			
			//retrieve customer meeting
			if(group.getCustomerMeeting()!=null){
				group.getCustomerMeeting().getMeeting().getMeetingPlace();
			}
			
			if(group.getCustomerAccounts()!=null){
				Iterator accountsIterator  = group.getCustomerAccounts().iterator();
				while(accountsIterator.hasNext()){
					Account account = (Account)accountsIterator.next();
					if(account.getAccountTypeId().equals(
				AccountTypes.CUSTOMERACCOUNT.getValue())){
						group.setCustomerAccount((CustomerAccount)account);
						break;
					}
				}
			}
			
			Account custAccount = group.getCustomerAccount();
			if(custAccount!=null){
				
				Set<AccountActionDate> accntActDates=custAccount.getAccountActionDateSet();
				if(accntActDates!=null){
					Hibernate.initialize(accntActDates);
					for(AccountActionDate accountActionDate :  accntActDates){
						accountActionDate.getActionDate();
					}
				}
				
				Set<AccountFees> accntFees=custAccount.getAccountFeesSet();
				if(accntFees!=null){
					Hibernate.initialize(accntFees);
					for(AccountFees accountFees :  accntFees){
						accountFees.getAccountId();
						accountFees.getAccountFeeAmount();
					}
				}
			}
		}catch(HibernateProcessException hpe){
			throw hpe;
		}finally{
			HibernateUtil.closeSession(session);
		}
		return group;
	}
	
	/**
	 * This method obtains the group details based on group id (customer id)
	 * @param groupId The group id for which group is to be retrieved
	 * return an instance of Group
	 * @throws ApplicationException
 	 * @throws SystemException
	 */
	public Group findByGroupId(Integer groupId)throws ApplicationException,SystemException {
		Group group = new Group();
		Session session = null;
		try{
			session = HibernateUtil.getSession();
			group=(Group)session.get(org.mifos.application.customer.group.util.valueobjects.Group.class,groupId);
			
			//call specific getters to load lazy associations
			
			//get group loan officer
			if(group.getPersonnel()!=null)
				group.getPersonnel().getDisplayName();
			if(group.getCustomerFormedByPersonnel()!=null)
				group.getCustomerFormedByPersonnel().getDisplayName();
			//get group parent
			Customer parent =group.getParentCustomer();
			if(parent!=null)
				parent.getDisplayName();
			
			//get office of the group
			group.getOffice().getGlobalOfficeNum();
			
			if(group.getCustomerMeeting() !=null)
				group.getCustomerMeeting().getMeeting().getMeetingPlace();
		}catch(HibernateProcessException hpe){
			throw hpe;
		}finally{
			HibernateUtil.closeSession(session);
		}
		return group;
	}
	
	/**
	 * This method is used to search for a groups under a particular office and its child offices with a 
	 * given starting name.
	 * @param userId it is userId of the loggedInUser
	 * @param searchString The string from which name starts
	 * @return A query result object containg the results
	 * @throws SystemException
	 * @throws ApplicationException
	 */	
	public QueryResult search(short officeId , String searchString, Short userId, Short userLevelId) throws SystemException, ApplicationException {
		Session session = null;
		try{
			QueryResult queryResult = QueryFactory.getQueryResult("GroupList");
			session = queryResult.getSession();
			Query query = null;
			//retrieving the searchId of the office of loggedIn User
			String searchId = new CustomerUtilDAO().getOffice(officeId).getSearchId();
			if(Configuration.getInstance().getCustomerConfig(officeId).isCenterHierarchyExists()){
				query=session.getNamedQuery(NamedQueryConstants.GROUP_SEARCH_WITH_CENTER);
				//query = session.createQuery("select distinct customer.office.officeName,customer.displayName,customer.parentCustomer.displayName, customer.customerId from Customer customer where customer.office.searchId like :SEARCH_ID and customer.displayName like :SEARCH_STRING and customer.customerLevel.levelId =:LEVEL_ID and customer.statusId!=:STATUS1 and customer.statusId!=:STATUS2");			
				query.setString("SEARCH_ID", searchId+"%");
				query.setString("SEARCH_STRING", searchString+"%");
				query.setShort("LEVEL_ID" , CustomerConstants.GROUP_LEVEL_ID);
				query.setShort("STATUS1" , GroupConstants.CANCELLED);
				query.setShort("STATUS2" , GroupConstants.CLOSED);
				query.setShort("USER_ID" , userId);
				query.setShort("USER_LEVEL_ID" , userLevelId);
				query.setShort("LO_LEVEL_ID" , PersonnelConstants.LOAN_OFFICER);
				
				
				//building the search results object. 
				String[] aliasNames = {"officeName" , "groupName" , "centerName","groupId" };
				 QueryInputs inputs = new QueryInputs();
				 inputs.setPath("org.mifos.application.group.util.valueobjects.GroupSearchResults");
				 inputs.setAliasNames(aliasNames);
				 inputs.setTypes(query.getReturnTypes());
	
				queryResult.setQueryInputs(inputs);
				queryResult.executeQuery(query);
			}
			else{
				//query = session.createQuery("select distinct customer.office.officeName,customer.displayName,customer.customerId from Customer customer where ((customer.personnel.personnelId=:USER_ID and :USER_LEVEL_ID=:LO_LEVEL_ID)or(:USER_LEVEL_ID!=:LO_LEVEL_ID)) and customer.office.searchId like :SEARCH_ID and customer.displayName like :SEARCH_STRING and customer.customerLevel.levelId =:LEVEL_ID and customer.statusId!=:STATUS1 and customer.statusId!=:STATUS2");
				query=session.getNamedQuery(NamedQueryConstants.GROUP_SEARCH_WITHOUT_CENTER);
				query.setString("SEARCH_ID", searchId+"%");
				query.setString("SEARCH_STRING", searchString+"%");
				query.setShort("LEVEL_ID" , CustomerConstants.GROUP_LEVEL_ID);
				query.setShort("STATUS1" , GroupConstants.CANCELLED);
				query.setShort("STATUS2" , GroupConstants.CLOSED);
				query.setShort("USER_ID" , userId);
				query.setShort("USER_LEVEL_ID" , userLevelId);
				query.setShort("LO_LEVEL_ID" , PersonnelConstants.LOAN_OFFICER);
				
				//building the search results object. 
				String[] aliasNames = {"officeName" , "groupName" , "groupId" };
				QueryInputs inputs = new QueryInputs();
				inputs.setPath("org.mifos.application.group.util.valueobjects.GroupSearchResults");
				inputs.setAliasNames(aliasNames);
				inputs.setTypes(query.getReturnTypes());
	
				queryResult.setQueryInputs(inputs);
				queryResult.executeQuery(query);
			}
			return queryResult;
		}catch(HibernateProcessException he){
			throw new SystemException(he);
		}
	}
	
	/** 
	   *  This is the helper method to change all clients belonging to that group to partial application.
	   *  @param session hibernate session instance
	   *  @param groupSearchId The branch whose status is checked
	   *  @param officeId The branch whose status is checked
	   *  @throws SystemException
	   */
	private void updateAllGroupClientsToPartialApplication(Session session,String groupSearchId,Short officeId)throws SystemException{
		//retrieve all clients in pending approval status those are assigned to the group
		List clients = new CustomerUtilDAO().getCustomerChildren(groupSearchId, officeId,session);
		if(null!=clients && clients.size()>0){
			for(int i=0;i<clients.size();i++){
				Object obj=clients.get(i);
				CustomerMaster client = (CustomerMaster)obj;
				if(client.getStatusId().shortValue()==ClientConstants.STATUS_PENDING){
					logger.debug("updating client status to partial application for client id: "+client.getCustomerId());
					new ClientCreationDAO().updateClientToPartialApplication(session,client.getCustomerId());
				}
			}
		}
	}
	/** 
	   *  This method checks if center with passed in center id is active or not
	   *  @param centerId center to be checked for active state
	   *  @return Returns true or false as to whether the branch is inactive
	   *  @throws SystemException
	   */
		public  boolean isCenterActive(Integer centerId)throws SystemException{
			HashMap queryParameters = new HashMap();
			Integer count=0;
			queryParameters.put("customerId", centerId);
			//inactive state of center
			queryParameters.put("statusId", CustomerConstants.CENTER_INACTIVE_STATE);
			
			List queryResult=executeNamedQuery(NamedQueryConstants.IS_CENTER_ACTIVE,queryParameters);

			if(null!=queryResult && queryResult.size()>0){
				Object obj = queryResult.get(0);
				if(obj!=null)
					count = (Integer)obj;
			}
			return(count.intValue()==0)?true:false;
		}
		
	/**
	 * This is the helper method that makes associations null, which are not required to update along with the customer.
	 * It is called at the time of updating group's parent
	 * @param customer whose associations are supposed to make null
	 */
	private void makeCustomerAssociationsNullForUpdate(Customer customer){
	  customer.setCustomerAddressDetail(null);
	  customer.setCustomerHistoricalData(null);
	  customer.setCustomerPositions(null);
	  customer.setCustomerHistoricalData(null);
	}
	
		
}
