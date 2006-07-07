/**

 * EditGrpMembershipDAO.java    version: xxx

 

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

package org.mifos.application.customer.client.dao;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleStateException;
import org.hibernate.Transaction;
import org.mifos.application.configuration.business.ConfigurationIntf;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.client.util.valueobjects.Client;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerHierarchy;
import org.mifos.application.customer.util.valueobjects.CustomerMeeting;
import org.mifos.application.customer.util.valueobjects.CustomerMovement;
import org.mifos.application.customer.util.valueobjects.CustomerPosition;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.components.audit.util.helpers.AuditConstants;
import org.mifos.framework.components.audit.util.helpers.LogInfo;
import org.mifos.framework.components.audit.util.helpers.LogValueMap;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class acts as a DAO for editing group membership of a client.
 * @author ashishsm
 *
 */
public class EditGrpMembershipDAO extends DAO {

/**An insatnce of the logger which is used to log statements */
	private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER);
	private ConfigurationIntf labelConfig=MifosConfiguration.getInstance();

	/**
	 * This updates the the client object based on the client id. This is called to edit the group membership 
	 * of the client
	 * The attributes to be updated are: - 
	 * branchId
	 * Loan Officer Id needs to be inherited from the group
	 * branch_id
	 * parent customer id
	 * searchId
	 * max child count
	 * updated date
	 * updated by
	 * We also need to insert a row in customer hierarchy and customer movement.
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#update(org.mifos.framework.util.valueobjects.Context)
	 */
	public void update(Context context)throws SystemException,ApplicationException{
		
		Transaction tx = null;
		Session session = null;
		CustomerHierarchy customerHierarchy = null;
		Customer parent = null;
		CustomerMovement customerFirstBranchMovement =null;
		CustomerMovement customerMovement =null;
		Client client = (Client)context.getBusinessResults(ClientConstants.CLIENT_TRANSFER);
		SearchResults sr =context.getSearchResultBasedOnName(GroupConstants.GROUP_PARENT);
		Short userId = context.getUserContext().getId();
		if(null!=sr){
			parent=(Customer)sr.getValue();
			CustomerHelper helper = new CustomerHelper();
			customerHierarchy = helper.createCustomerHierarchy(parent, client, context);
		}
		
		//updating max child count of the old group to one less
		Customer oldParent = client.getParentCustomer();
		oldParent.setMaxChildCount(oldParent.getMaxChildCount() -1 ); 
		//update parent of client to the new group 
		client.setParentCustomer(parent);
		// update the max child count of the parent
		Customer cust = client.getParentCustomer();
		if (cust != null) {
			client.setSearchId(client.getParentCustomer().getSearchId()
					+ "."
					+ String.valueOf(client.getParentCustomer()
							.getMaxChildCount() + 1));
			client.getParentCustomer().setMaxChildCount(
					client.getParentCustomer().getMaxChildCount() + 1);
			
		}
		//update loan officer
		client.setPersonnel(parent.getPersonnel());
		//update the office of the client to the parent customer office
		//client.setOffice(parent.getOffice());
		CustomerHelper helper = new CustomerHelper();
		if(client.getOffice().getOfficeId().shortValue() != parent.getOffice().getOfficeId().shortValue()){
			customerFirstBranchMovement =helper.createCustomerMovement(client , client.getCreatedDate());
			client.setOffice(parent.getOffice());
		}
		makeAssociationsNull(client);
		logger.debug("CLIENT CUSTOMER MEETING: "+ client.getCustomerMeeting()); 
		logger.debug("CLIENT PARENT CUSTOMER MEETING: "+ parent.getCustomerMeeting());
		if(parent.getCustomerMeeting() !=null){
			logger.debug("CLIENT PARENT MEETING: "+ parent.getCustomerMeeting().getMeeting());
			client.getCustomerMeeting().setMeeting(parent.getCustomerMeeting().getMeeting());
			client.getCustomerMeeting().setUpdatedFlag(YesNoFlag.YES.getValue());
		}
		try {
			  LogValueMap  objectMap = new LogValueMap();
			  objectMap.put(AuditConstants.REALOBJECT,new Customer());
			  objectMap.put("parentCustomer",AuditConstants.REALOBJECT);
			  LogInfo logInfo= new LogInfo(client.getCustomerId(),"Client",context,objectMap);
			  session=HibernateUtil.getSessionWithInterceptor(logInfo);
			  tx = session.beginTransaction();
			//updating the max child count of the old parent
			//to ensure it doesnt update the center of the group
			//oldParent.setParentCustomer(null);
			oldParent.setUpdatedBy(context.getUserContext().getId());
			oldParent.setUpdatedDate(helper.getCurrentDate());
			session.update(oldParent);
			//updating the max child count of the new parent
			cust.setUpdatedBy(context.getUserContext().getId());
			cust.setUpdatedDate(helper.getCurrentDate());
			session.update(cust);
			//updating the client details
			session.update(client);
			checkIfClientIsAssignedPosition(session ,oldParent.getCustomerId() , client.getCustomerId());
			//if client is being transferred between groups in different centers, and if center has this client assigned to a position,
			//then that is removed 
			if(oldParent.getParentCustomer()!=null && cust.getParentCustomer()!=null ){
				if(oldParent.getParentCustomer().getCustomerId().intValue() != cust.getParentCustomer().getCustomerId().intValue() ){
					checkIfClientIsAssignedPosition(session ,oldParent.getParentCustomer().getCustomerId() , client.getCustomerId());
				}
			}
			// make old hierarhcy inactive
			this.makeCurrentCustomerHierarchyInActive(session,client.getCustomerId(),userId);
			
			// create new customer hierarchy
			if(null!=customerHierarchy)
				session.save(customerHierarchy);
			if(null != customerFirstBranchMovement){
				//make the old branch association inactive
				this.makeCustomerBranchAssociationInActive(session,client,customerFirstBranchMovement,userId);
				
				customerMovement = helper.createCustomerMovement(client ,new java.sql.Date(new java.util.Date().getTime()));
				// create new customer branch association
				if(null!=customerMovement){
					session.save(customerMovement);
				}
			}
			tx.commit();
		  } catch(StaleStateException sse){
			  Object values[]=new Object[1];
			  values[0]=client.getDisplayName();
			  throw new CustomerException(CustomerConstants.CUSTOMER_INVALID_VERSION_EXCEPTION,new Object[]{labelConfig.getLabel(ConfigurationConstants.CENTER,context.getUserContext().getPereferedLocale())});
		  	}
		  	catch (HibernateProcessException hpe) {
				throw hpe;
			}catch (HibernateException hpe) {
				hpe.printStackTrace();
				throw new CustomerException(ClientConstants.UPDATE_FAILED);
			}
		  	finally {
				HibernateUtil.closeSession(session);
			}
	}
	
	public void checkIfClientIsAssignedPosition(Session session,  Integer parentCustomerId , Integer customerId)throws SystemException {
			 boolean isClientAssignedToPosition = false;
			  HashMap queryParameters = new HashMap();
			  queryParameters.put("CUSTOMER_ID",customerId);
			  queryParameters.put("PARENT_CUSTOMER_ID",parentCustomerId);
			  List queryResult = executeNamedQuery("Customer.clientPositions",queryParameters,session);
			  System.out.println("----------------query size: "+queryResult.size() +queryResult.getClass());
			  if(null!=queryResult){
				 for(int i=0;i<queryResult.size();i++){
					 if(null!=queryResult){
						 System.out.println("----------------query class: "+queryResult.get(i).getClass());
						 CustomerPosition clientPosition = (CustomerPosition)queryResult.get(i); 
						 clientPosition.setCustomerId(null);
						 session.update(clientPosition);
					 }
				 }
			  }
		
		 
	}
/**
	 * This updates the the client object based on the client id. This is called to if the parent group chnages its membership 
	 * of the client
	 * The attributes to be updated are: - 
	 * branchId
	 * Loan Officer Id needs to be inherited from the group
	 * branch_id
	 * parent customer id
	 * searchId
	 * max child count
	 * updated date
	 * updated by
	 * We also need to insert a row in customer hierarchy and customer movement.
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#update(org.mifos.framework.util.valueobjects.Context)
	 */
	public void updateClientForGroup(Session session , Integer customerId , String searchId ,Office parentOffice , CustomerMeeting customerMeeting, Personnel personnel,UserContext userContext)throws SystemException,ApplicationException{
		Customer client = null;
		//get the office object
		client = (Client) session.get(Client.class, customerId);
		client.setSearchId(searchId);
		CustomerMovement customerFirstBranchMovement =null;
		CustomerMovement customerMovement =null;
		CustomerHelper helper = new CustomerHelper();
		//create customer movement objects if customer is being transferred in different office 
		if(client.getOffice().getOfficeId().shortValue()!=parentOffice.getOfficeId().shortValue()){
			if(client.getStatusId().shortValue() == CustomerConstants.CLIENT_APPROVED){
				client.setStatusId(CustomerConstants.CLIENT_ONHOLD); 
			}
			customerFirstBranchMovement =helper.createCustomerMovement(client , client.getCreatedDate());
			client.setOffice(parentOffice);
		}		
		try {
			//updating the max child count of the old parent
			//to ensure it doesnt update the center of the group
			//updating the client details
			client.setPersonnel(personnel);
			if(customerMeeting!=null && customerMeeting.getMeeting()!=null)
				client.getCustomerMeeting().setMeeting(customerMeeting.getMeeting());
			session.update(client);
			
			if(null != customerFirstBranchMovement){
				//make the old branch association inactive
				this.makeCustomerBranchAssociationInActive(session,client,customerFirstBranchMovement,userContext.getId());
				
				customerMovement = helper.createCustomerMovement(client ,new java.sql.Date(new java.util.Date().getTime()));
				// create new customer branch association
				if(null!=customerMovement){
					session.save(customerMovement);
				}
			}
			
		  } catch(StaleStateException sse){
			  Object values[]=new Object[1];
			  values[0]=client.getDisplayName();
			  throw new CustomerException(CustomerConstants.CUSTOMER_INVALID_VERSION_EXCEPTION,new Object[]{labelConfig.getLabel(ConfigurationConstants.CENTER,userContext.getPereferedLocale())});
		  	}
		  	catch (HibernateProcessException hpe) {
				throw hpe;
			}catch (HibernateException hpe) {
				hpe.printStackTrace();
				throw new CustomerException(ClientConstants.UPDATE_FAILED);
			}
	}
	
	/** 
	 *  This method is called when client is transferred in different branch 
	 *  Clients office is updated in the database. 
	 *  @param oldCustomer The client who is being transferred
	 * @param userContext TODO
	 * @param context instance of Context
	 *  @throws ApplicationException
	 *  @throws SystemException
	 */	
  	public void updateBranchRSM(Client oldCustomer , Short officeId ,UserContext userContext)throws ApplicationException,SystemException{
		Transaction tx = null;
		Session session = null;
		/*OfficeDAO officeDAO = new OfficeDAO();
		oldCustomer.setOffice(officeDAO.getOffice(officeId));*/
		CustomerHelper helper = new CustomerHelper();
		CustomerMovement customerMovement = null;
		CustomerMovement customerFirstBranchMovement =helper.createCustomerMovement(oldCustomer , oldCustomer.getCreatedDate());
		

		makeAssociationsNull(oldCustomer);
		try {
			session = getHibernateSession();
			tx = session.beginTransaction();
			Office office = (Office) session.get(Office.class, officeId);
			oldCustomer.setOffice(office);
			logger.debug("CUSTOMER ID IN TRANSFER dao:" +oldCustomer.getCustomerId());
			logger.debug("CUSTOMER VERSION IN TRANSFER dao:" +oldCustomer.getVersionNo());
			logger.debug("CUSTOMER STATUS IN TRANSFER dao:" +oldCustomer.getStatusId());
			logger.debug("CUSTOMER LEVEL IN TRANSFER dao:" +oldCustomer.getCustomerLevel().getLevelId());
			logger.debug("CUSTOMER OFFICE ID IN TRANSFER dao:" +oldCustomer.getOffice().getOfficeId());
			logger.debug("CUSTOMER OFFICE VERSION IN TRANSFER dao:" +oldCustomer.getOffice().getVersionNo());
			oldCustomer.setPersonnel(null);
			session.update(oldCustomer);
			
			//make the old branch association inactive
			this.makeCustomerBranchAssociationInActive(session,oldCustomer,customerFirstBranchMovement,userContext.getId());
			
			customerMovement = helper.createCustomerMovement(oldCustomer ,new java.sql.Date(new java.util.Date().getTime()));
			// create new customer branch association
			if(null!=customerMovement)
				session.save(customerMovement);
			tx.commit();			
		  } 
			catch(StaleStateException sse){
			  Object values[]=new Object[1];
			  values[0]=oldCustomer.getDisplayName();
			  throw new CustomerException(CustomerConstants.CUSTOMER_INVALID_VERSION_EXCEPTION,new Object[]{labelConfig.getLabel(ConfigurationConstants.CENTER,userContext.getPereferedLocale())});
		  	}catch (HibernateProcessException hpe) {
				throw hpe;
			}catch (HibernateException hpe) {
				throw new CustomerException(ClientConstants.UPDATE_FAILED , hpe);
			}  finally {
				HibernateUtil.closeSession(session);
			}
  	}
	/**
	 * This is the helper method that makes associations null, which are not required to update along with the customer.
	 * It is called at the time of updating group's parent
	 * @param customer whose associations are supposed to make null
	 */
	private void makeAssociationsNull(Client customer){
	  customer.setCustomerAddressDetail(null);
	 
	 // customer.setCustomerNameDetailSet(null);
	  customer.setCustomerDetail(null);
	  customer.setCustomerHistoricalData(null);
	  customer.setCustomerPositions(null);
	  customer.setCustomerFlag(null);
	  customer.setCustomerAccount(null);
		
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
  	private void makeCurrentCustomerHierarchyInActive(Session session , Integer customerId, Short userId)throws SystemException{
		Query query = session.createQuery("from org.mifos.application.customer.util.valueobjects.CustomerHierarchy customerHierarchy where customerHierarchy.customer.customerId =:customerId and customerHierarchy.status =:status");
		query.setInteger("customerId", customerId);
		query.setShort("status",CustomerConstants.ACTIVE_HIERARCHY);
		//makes the current customer hierarchy inactive
		CustomerHierarchy customerHierarchy = (CustomerHierarchy)query.uniqueResult();
		if(null!=customerHierarchy){
			customerHierarchy.setStatus(CustomerConstants.INACTIVE_HIERARCHY);
			customerHierarchy.setEndDate(new Date(new java.util.Date().getTime()));
			customerHierarchy.setUpdatedBy(userId);
			customerHierarchy.setUpdatedDate(new Date(new java.util.Date().getTime()));
			session.update(customerHierarchy);
		}
		
		  
  	}
  	/** 
	 *  This method is called when current client is being moved to another branch.
	 *  It accepts the current session and does an update on customer movement across the different branches.
	 *  
	 *  @param session it holds the current hibernate session
	 *  @param globalCustNum The group system id
	 *  @param context instance of Context  
	 *  @throws SystemException
	 */	  	
  	private void makeCustomerBranchAssociationInActive(Session session , Customer customer,CustomerMovement customerMovement1 , Short userId)throws SystemException{
  		
  		Integer customerId = customer.getCustomerId();
  		Query query = session.createQuery("from org.mifos.application.customer.util.valueobjects.CustomerMovement customerMovement where customerMovement.customer.customerId =:customerId and customerMovement.status =:status");
		query.setInteger("customerId", customerId);
		query.setShort("status",CustomerConstants.ACTIVE_HIERARCHY);
		//makes the current customer branch association inactive
		CustomerMovement customerMovement = (CustomerMovement)query.uniqueResult();
		if(null!=customerMovement){
			customerMovement.setStatus(CustomerConstants.INACTIVE_HIERARCHY);
			customerMovement.setEndDate(new Date(new java.util.Date().getTime()));
			customerMovement.setUpdatedBy(userId);
			customerMovement.setUpdatedDate(new Date(new java.util.Date().getTime()));
			session.update(customerMovement);
		}
		else{
			
			customerMovement1.setStatus(CustomerConstants.INACTIVE_HIERARCHY);
			customerMovement1.setEndDate(new Date(new java.util.Date().getTime()));
			customerMovement1.setUpdatedBy(userId);
			customerMovement1.setUpdatedDate(new Date(new java.util.Date().getTime()));
			session.save(customerMovement1);
		}
		
		  
  	}
}
