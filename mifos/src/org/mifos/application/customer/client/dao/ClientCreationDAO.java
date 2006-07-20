/**

 * ClientCreationDAO.java    version: xxx



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

import java.io.IOException;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;
import org.mifos.application.accounts.savings.business.SavingsBO;
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
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.customer.client.util.valueobjects.Client;
import org.mifos.application.customer.client.util.valueobjects.ClientPerformanceHistory;
import org.mifos.application.customer.client.util.valueobjects.CustomerPicture;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.group.util.valueobjects.GroupPerformanceHistory;
import org.mifos.application.customer.persistence.service.CustomerPersistenceService;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.customer.util.helpers.IdGenerator;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerHierarchy;
import org.mifos.application.meeting.util.valueobjects.MeetingType;
import org.mifos.application.office.dao.OfficeDAO;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.audit.util.helpers.AuditConstants;
import org.mifos.framework.components.audit.util.helpers.LogInfo;
import org.mifos.framework.components.audit.util.helpers.LogValueMap;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ConcurrencyException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.IllegalStateException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This class acts as DAO for creating/updating a client.
 *
 * @author ashishsm
 *
 */
public class ClientCreationDAO extends DAO {

	/**An insatnce of the logger which is used to log statements */
	private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER);
	private ConfigurationIntf labelConfig=MifosConfiguration.getInstance();

	/**
	 *
	 */
	public ClientCreationDAO() {
		super();

	
	}

	public void create(Context context) throws ApplicationException,
			SystemException {
		Client vo = (Client) context.getValueObject();
		CustomerHierarchy customerHierarchy = null;
		// set the customer level
		vo.getCustomerLevel().setLevelId(CustomerConstants.CLIENT_LEVEL_ID);
		vo.setCreatedDate(new Date(new java.util.Date().getTime()));
		 if(vo.getTrained().shortValue()==Constants.NO)
			 vo.setTrained(CustomerConstants.TRAINED_NO);
		// set address name
		vo.getCustomerAddressDetail().setAddressName(
				CustomerConstants.DEFAULT_ADDRESS_NAME);

		// update the max child count of the parent
		Customer cust = vo.getParentCustomer();
		if (cust != null) {
			vo.setSearchId(vo.getParentCustomer().getSearchId()+ "."+ String.valueOf(vo.getParentCustomer().getMaxChildCount() + 1));
			vo.getParentCustomer().setMaxChildCount(vo.getParentCustomer().getMaxChildCount() + 1);
			/*GroupPerformanceHistory groupPerformanceHistory= ((GroupPerformanceHistory)vo.getParentCustomer(). getCustomerPerformanceHistory());
			if(groupPerformanceHistory!=null)
				groupPerformanceHistory.setClientCount(groupPerformanceHistory.getClientCount()+1);*/
			
		}
		else{
			//group hierarchy does not exists
			  int customerCount=new CustomerHelper().getCustomerCount(CustomerConstants.CLIENT_LEVEL_ID , vo.getOffice()
						.getOfficeId())+1;
			  String searchId=GroupConstants.PREFIX_SEARCH_STRING + String.valueOf(customerCount);
			  vo.setSearchId(searchId);
		  }
		vo.setHoUpdated(new Short(Constants.NO));
		vo.setGroupFlag(vo.getIsClientUnderGrp());
		//no children of the client
		vo.setMaxChildCount(new Integer(0));

		// joining date
		vo.setMfiJoiningDate(new Date(new java.util.Date().getTime()));
		// activation date
		if (vo.getStatusId() == CustomerConstants.CLIENT_APPROVED)
			vo.setCustomerActivationDate(new Date(new java.util.Date().getTime()));

		// set the account details. prd offering name and prd offering is being
		// set to NULL
		vo.getCustomerAccount().setPersonnelId(context.getUserContext().getId());
		vo.getCustomerAccount().setOfficeId(vo.getOffice().getOfficeId());
		vo.getCustomerAccount().setAccountTypeId(
				Short.valueOf(AccountTypes.CUSTOMERACCOUNT));
		// setting the customer account state to active. To be reset when
		// cancelled, deleted, on hold or withdrawn
		vo.getCustomerAccount().setAccountStateId(
				Short.valueOf(AccountStates.CUSTOMERACCOUNT_ACTIVE));
		vo.getCustomerAccount().setCustomer(vo);
		// TODO change this strategy
		vo.getCustomerAccount().setGlobalAccountNum(IDGenerator.generateIdForCustomerAccount(new OfficeDAO().getOffice(vo.getOffice().getOfficeId()).getGlobalOfficeNum()));
		vo.getCustomerAccount()
				.setCreatedBy(context.getUserContext().getId());
		vo.getCustomerAccount().setCreatedDate(
				new Date(new java.util.Date().getTime()));

		if (vo.getCustomerAccount() != null
				&& vo.getCustomerAccount().getAccountFeesSet() != null) {
			if (vo.getCustomerAccount() != null
					&& vo.getCustomerAccount().getAccountFeesSet() != null
					&& vo.getCustomerAccount().getAccountFeesSet().size() == 0)
				vo.getCustomerAccount().setAccountFeesSet(null);
		}

		if(vo.getIsClientUnderGrp()== Constants.NO){
			vo.setCustomerHierarchy(null);
		}
		else{
			logger.debug("*******************Customer hier in client creation DAO: "+vo.getCustomerHierarchy());
			CustomerHelper helper = new CustomerHelper();
			customerHierarchy = helper.createCustomerHierarchy(cust, vo, context);
			/*vo.getCustomerHierarchy().setCustomer(vo);
			vo.getCustomerHierarchy().setParentCustomer(vo.getParentCustomer());*/
		}

		vo.setCustomerHistoricalData(null);

		vo.getCustomerDetail().setCustomer(vo);
		/*CustomerMeeting meeting = vo.getCustomerMeeting();
		if(meeting!=null){
			meeting.setCustomer(vo);

		}*/

		vo.setCustomerPositions(null);
		vo.convertCustomFieldDateToDbformat(context.getUserContext().getMfiLocale());

		Session session = null;
		Transaction trxn = null;
		try {
			session = HibernateUtil.getSession();
			trxn = session.beginTransaction();
			
			if(vo.getCustomerMeeting()!=null && vo.getCustomerMeeting().getMeeting()!=null){
				vo.getCustomerMeeting().setUpdatedFlag(YesNoFlag.NO.getValue());
			}
			//save meeting only if user has selected any
			logger.debug("GET CUSTMER MEETING WITH VALUE IN DAO " + vo.getCustomerMeeting());
			if(vo.getCustomerMeeting()!=null && vo.getCustomerMeeting().getMeeting()!=null && vo.getGroupFlag()!=Constants.YES){
				MeetingType meetingType = new MeetingType();
			 	meetingType.setMeetingTypeId(CustomerConstants.CUSTOMER_MEETING_TYPE);
			 	vo.getCustomerMeeting().getMeeting().setMeetingType(meetingType);
			 	session.save(vo.getCustomerMeeting().getMeeting());
			}
			
			ClientPerformanceHistory clientPerfHistory = new ClientPerformanceHistory(0,0,new Money(),new Money(),new Money());
			vo.setPerformanceHistory(clientPerfHistory);

			session.save(vo);
			//the client is set with the global customer number that has been generated from
			//the office global number and the current customerId of the client
			String gCustNum=IdGenerator.generateSystemIdForCustomer(vo.getOffice().getGlobalOfficeNum(),vo.getCustomerId());
			vo.setGlobalCustNum(gCustNum);
			
			new CustomerHelper().saveMeetingDetails(vo,session, context.getUserContext());
			//update client
			session.update(vo);
			session.flush();
			if (null!=cust && null != customerHierarchy){
				//parent.setCustomerHistoricalData(null);
				makeAssociationsNull(cust);
				session.update(cust);
				session.save(customerHierarchy);
			}
			logger.debug("The customer id currently is - " + vo.getCustomerId());
			//create a new CustomerPicture object
			if(vo.getCustomerPicture() != null){
				CustomerPicture pict = new CustomerPicture();
				pict.setCustomerId(vo.getCustomerId());
				Blob picture = Hibernate.createBlob(vo.getCustomerPicture());
				try{
				// System.out.println("Picture size in create: "+ picture.length());
				}
				catch(Exception e){

				}
				pict.setPicture(picture);
				//save the customer picture
				session.save(pict);
			}
			trxn.commit();

		} catch (HibernateProcessException hpe) {
			trxn.rollback();
			throw new ApplicationException(hpe);
		}
		catch (HibernateException he) {
			trxn.rollback();
			throw new CustomerException(CustomerConstants.CREATE_FAILED_EXCEPTION ,he,new Object[]{labelConfig.getLabel(ConfigurationConstants.CLIENT,context.getUserContext().getPereferedLocale())});
		 }
		catch(IOException ioe){
			trxn.rollback();
			throw new ApplicationException(ioe);

		}
		catch (Exception e) {
			trxn.rollback();
			throw new CustomerException(CustomerConstants.CREATE_FAILED_EXCEPTION ,e,new Object[]{labelConfig.getLabel(ConfigurationConstants.CLIENT,context.getUserContext().getPereferedLocale())});
		 }
		finally {
			HibernateUtil.closeSession(session);
		}
		
		saveDepositSchedule(vo, context.getUserContext());
	}
	
	private void saveDepositSchedule(Customer client, UserContext userContext)throws ApplicationException, SystemException{
		if(client.getCustomerMeeting()!=null && client.getCustomerMeeting().getMeeting()!=null){
			CustomerPersistenceService customerPersistenceService = (CustomerPersistenceService) ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.Customer);
			CustomerBO customer = customerPersistenceService.getCustomer(client.getCustomerId());
			if(customer.getParentCustomer()!=null){
				List<SavingsBO> savingsList = customerPersistenceService.retrieveSavingsAccountForCustomer(customer.getParentCustomer().getCustomerId());
				if(customer.getParentCustomer().getParentCustomer()!=null)
					savingsList.addAll(customerPersistenceService.retrieveSavingsAccountForCustomer(customer.getParentCustomer().getParentCustomer().getCustomerId()));
				for(SavingsBO savings : savingsList){
					savings.setUserContext(userContext);
					savings.generateAndUpdateDepositActionsForClient(customer);
				}
				//TODO: Code to be removed when moved to M2
				try {
					if(HibernateUtil.getTransaction()!=null)
						HibernateUtil.getTransaction().commit();
				}catch(HibernateException he) {
					HibernateUtil.getTransaction().rollback();
					throw new ApplicationException(he);
				}catch(IllegalStateException ise) {
					HibernateUtil.getTransaction().rollback();
					throw new ApplicationException(ise);
				}
			}
		}
	}
	
	/**
	 * This is the helper method that makes associations null, which are not required to update along with the customer.
	 * It is called at the time of updating group's parent
	 * @param customer whose associations are supposed to make null
	 */
	private void makeAssociationsNull(Customer customer){
	  customer.setCustomerAddressDetail(null);
	  customer.setCustomerHistoricalData(null);
	  customer.setCustomerPositions(null);
	  customer.setCustomerAccount(null);
	  customer.setCustomerMeeting(null);
	}
	/**
	 * Returns true if there already exists a client in the database.The
	 * uniqueness check is done based on the uniqueness criteria of the
	 * client.No check is made on clients with status closed.
	 *
	 * @param name
	 * @param dob
	 * @param governmentId
	 * @return
	 */
	public boolean checkForDuplicacyOnName(String name, Date dob, String governmentId, Integer customerId) throws SystemException, ApplicationException {
		Session session = null;
		  Query query=null;
		  boolean centerNameExists = false;
		  try{
			  session = HibernateUtil.getSession();
			  //Checks if the client name and date of birth exists for any other customer with the same level as that of client
			 	  query= session.createQuery("from Customer as client where (client.displayName = :clientName and client.customerLevel.levelId =:LEVELID and client.dateOfBirth =:DATE_OFBIRTH and :customerId = 0) or (:customerId != 0 and client.displayName = :clientName and client.customerLevel.levelId =:LEVELID and client.dateOfBirth =:DATE_OFBIRTH and client.customerId !=:customerId)");
				  query.setString("clientName",name);
				  query.setShort("LEVELID", CustomerConstants.CLIENT_LEVEL_ID);
				  query.setDate("DATE_OFBIRTH", dob);
				  query.setInteger("customerId", customerId);
			  return(query.uniqueResult()!=null)?true:false;
		  }catch(HibernateProcessException hpe){
			  throw new SystemException(hpe);
		  }finally{
			 HibernateUtil.closeSession(session);
		  }
	}

	/**
	 * Returns true if there already exists a client in the database.The
	 * uniqueness check is done based on the uniqueness criteria of the
	 * client.No check is made on clients with status closed.
	 *
	 * @param name
	 * @param dob
	 * @param governmentId
	 * @return
	 */
	public boolean checkForDuplicacyOnGovtId(String governmentId) throws SystemException, ApplicationException {
		Session session = null;
		Query query=null;
		  try{
			  session = HibernateUtil.getSession();
			  //Checks if the client government id exists for any other customer with the same level as that of client
			  if(!ValidateMethods.isNullOrBlank(governmentId)){
				  query= session.createQuery("from Customer as client where client.governmentId = :GOVT_ID and client.customerLevel.levelId =:LEVELID");
				  query.setString("GOVT_ID",governmentId);
				  query.setShort("LEVELID", CustomerConstants.CLIENT_LEVEL_ID);
			  }
			  return(query.uniqueResult()!=null)?true:false;
		  }catch(HibernateProcessException hpe){
			  throw new SystemException(hpe);
		  }finally{
			 HibernateUtil.closeSession(session);
		  }
	}

	/**
	 * Returns the client object based on the systemId passed as parameter.
	 *
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public Client findBySystemId(String systemId) throws SystemException,
			ApplicationException {
		Client client = new Client();
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			Query query = session
					.createQuery("from org.mifos.application.customer.util.valueobjects.Customer customer where customer.globalCustNum =:globalCustNum");
			query.setString("globalCustNum", systemId);
			client = (Client) query.uniqueResult();

			//call specific getters to load lazy associations

			//get group loan officer
			if (client.getPersonnel() != null)

				client.getPersonnel().getDisplayName();
			
			if (client.getCustomerFormedByPersonnel() != null)
				client.getCustomerFormedByPersonnel().getDisplayName();

			//get group parent
			Customer parent = client.getParentCustomer();
			if (parent != null){
				parent.getDisplayName();
				//get parent for group if it exists
				Customer parentCenter = parent.getParentCustomer();
				if (parentCenter != null)
						parentCenter.getDisplayName();
				Personnel parentloanOfficer = parent.getPersonnel();
				if (parentloanOfficer != null)
					parentloanOfficer.getDisplayName();
			
			}

			//get office of the group
			client.getOffice().getGlobalOfficeNum();
			//get the level of the client
			client.getCustomerLevel().getLookUpEntity();
			//get address details
			client.getCustomerAddressDetail();

			//get customer details
			client.getCustomerDetail();

			//get name details
			client.getCustomerNameDetailSet();

			//get meeting details

			if(client.getCustomerMeeting() != null)
				client.getCustomerMeeting().getMeeting().getMeetingPlace() ;

			if(client.getCustomerAccounts()!=null){
				Iterator accountsIterator  = client.getCustomerAccounts().iterator();
				while(accountsIterator.hasNext()){
					Account account = (Account)accountsIterator.next();
					if(account.getAccountTypeId().shortValue()== new Short(AccountTypes.CUSTOMERACCOUNT).shortValue()){
						client.setCustomerAccount((CustomerAccount)account);
						break;
					}
				}
			}
			
			Account custAccount = client.getCustomerAccount();
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
			//get the account details
			//client.getCustomerAccount();
			//getaccount fees
			/*Set accountFees = client.getCustomerAccount().getAccountFeesSet();
			if(accountFees != null){
				Iterator it = accountFees.iterator();
				while(it.hasNext()){
					((AccountFees)it.next()).getFees();
				}
			}*/

		} catch (HibernateProcessException hpe) {
			throw new SystemException(hpe);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return client;
	}
	/**
	 * Returns the client object based on the systemId passed as parameter.
	 *
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public Client findByClientId(int clientId) throws SystemException,
			ApplicationException {
		Client client = new Client();
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			client = (Client) session.get(Client.class, new Integer(clientId));

			//call specific getters to load lazy associations
			// System.out.println("Client in DAO: "+ client);
			//get group loan officer
			if (client.getPersonnel() != null)
				client.getPersonnel().getDisplayName();
			if (client.getCustomerFormedByPersonnel() != null)
				client.getCustomerFormedByPersonnel().getDisplayName();
			//get group parent
			Customer parent = client.getParentCustomer();
			if (parent != null){
				parent.getDisplayName();
				//get parent for group if it exists
				Customer parentCenter = parent.getParentCustomer();
				if (parentCenter != null)
					parentCenter.getDisplayName();
				Personnel parentloanOfficer = parent.getPersonnel();
				if (parentloanOfficer != null)
					parentloanOfficer.getDisplayName();
			}

			//get office of the group
			client.getOffice().getGlobalOfficeNum();
			//get the level of the client
			client.getCustomerLevel().getLookUpEntity();
			//get address details
			client.getCustomerAddressDetail();

			//get customer details
			client.getCustomerDetail();

			//get name details
			client.getCustomerNameDetailSet();

			//get meeting details

			if(client.getCustomerMeeting() != null)
				client.getCustomerMeeting().getMeeting().getMeetingPlace() ;

			if(client.getCustomerAccounts()!=null){
				Iterator accountsIterator  = client.getCustomerAccounts().iterator();
				while(accountsIterator.hasNext()){
					Account account = (Account)accountsIterator.next();
					if(account.getAccountTypeId().shortValue()== new Short(AccountTypes.CUSTOMERACCOUNT).shortValue()){
						client.setCustomerAccount((CustomerAccount)account);
						break;
					}
				}
			}
			
			Account custAccount = client.getCustomerAccount();
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


		} catch (HibernateProcessException hpe) {
			throw new SystemException(hpe);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return client;
	}

	/**
	 * This updates the client record.
	 *
	 * @see org.mifos.framework.dao.DAO#update(org.mifos.framework.util.valueobjects.Context)
	 */
	public void update(Context context) throws SystemException,
			ApplicationException {
		Client vo = (Client) context.getValueObject();
		LogValueMap namedValues = new LogValueMap();
		vo.setCustomerAccount(null);
		vo.setCustomerPositions(null);
		vo.setCustomerHierarchy(null);
		vo.setCustomerMeeting(null);
		if(vo.getHistoricalData()!= null)
			vo.getHistoricalData().setCustomer(vo);
		if(vo.getTrained().shortValue()==Constants.NO)
			 vo.setTrained(CustomerConstants.TRAINED_NO);

		if(vo.getCustomerDetail()!=null) {
			vo.getCustomerDetail().setCustomerId(vo.getCustomerId());
			vo.getCustomerDetail().setCustomer(vo);
		}
		vo.convertCustomFieldDateToDbformat(context.getUserContext().getMfiLocale());
		Session session = null;
		Transaction trxn = null;
		try {
			namedValues.put(AuditConstants.REALOBJECT,new Client());
			namedValues.put("customerAddressDetail",AuditConstants.REALOBJECT);
			namedValues.put("customerDetail",AuditConstants.REALOBJECT);
			//namedValues.put("personnel",AuditConstants.REALOBJECT);
			namedValues.put("customFieldSet",AuditConstants.REALOBJECT);
			namedValues.put("customerNameDetailSet",AuditConstants.REALOBJECT);
			LogInfo logInfo= new LogInfo(vo.getCustomerId(),"Client",context,namedValues);
			session = HibernateUtil.getSessionWithInterceptor(logInfo) ;
			trxn = session.beginTransaction();
			logger.debug("CLIENT CREATION dao:  ");
			logger.debug("CUSTOMER ID:  "+ vo.getCustomerId());
			logger.debug("CUSTOMER version: " + vo.getVersionNo());
			session.update(vo);
			//update a new CustomerPicture object
			if(vo.getCustomerPicture() != null){

				CustomerPicture pict = retrievePicture(vo.getCustomerId());;
				//pict.setCustomerId(vo.getCustomerId());
				Blob picture = Hibernate.createBlob(vo.getCustomerPicture());
				pict.setPicture(picture);
				//save the customer picture
				try{
					if(picture.length() >0){
						session.saveOrUpdate(pict);
					}
				}
				catch(SQLException sqle){
					
				}
			}
			trxn.commit();
		}
		catch ( StaleObjectStateException sose )
		{
			trxn.rollback();
			throw new ConcurrencyException(ExceptionConstants.CONCURRENCYEXCEPTION,sose);
		}
		catch (HibernateProcessException hpe) {
			trxn.rollback();
			throw new ApplicationException(hpe);
		}
		catch (HibernateException he) {
			trxn.rollback();
			throw new CustomerException(CustomerConstants.UPDATE_FAILED_EXCEPTION , he,new Object[]{labelConfig.getLabel(ConfigurationConstants.CLIENT,context.getUserContext().getPereferedLocale())});
		 }
		catch(IOException ioe){
			trxn.rollback();
			throw new ApplicationException(ioe);

		}
		finally {
			HibernateUtil.closeSession(session);
		}


	}

	/**
	 * This updates the client status to partial if the group was transferred to a different branch
	 *
	 * @see org.mifos.framework.dao.DAO#update(org.mifos.framework.util.valueobjects.Context)
	 */
	public void updateClientToPartialApplication(Session session , Integer customerId) throws HibernateException {
		Client vo = null;
		//get the office object
		vo = (Client) session.get(Client.class, customerId);
		vo.setStatusId(CustomerConstants.CLIENT_PARTIAL);
		try {
			logger.debug("CLIENT CREATION dao:  ");
			logger.debug("CUSTOMER ID:  "+ vo.getCustomerId());
			logger.debug("CUSTOMER version: " + vo.getVersionNo());
			session.update(vo);
		}
		catch (HibernateException he) {
			throw he;
		 }
	}


	public CustomerPicture retrievePicture(Integer customerId) throws SystemException,ApplicationException {

		CustomerPicture clientPicture  = null;
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			// System.out.println("CustomerId :" +customerId.intValue());
			Query query = session.createQuery("from org.mifos.application.customer.client.util.valueobjects.CustomerPicture customerPicture where customerPicture.customerId =:customerId");
			query.setInteger("customerId", customerId);
			clientPicture = (CustomerPicture) query.uniqueResult();
			/*try{
			System.out.println("-------**************CustomerPicture blob length: "+clientPicture.getPicture().length());
			}
			catch(Exception e){}
*/
		}
		catch (HibernateProcessException hpe) {
			throw new SystemException(hpe);
		}
		 finally {
			HibernateUtil.closeSession(session);
		}
		return clientPicture;

	}
	/**
	 * This updates the status for the center .It also calls addNote method on the customerdao to add notes in the database.
	 * It passes the session object to that method so that both of them work on the same transaction and if any one of them fails
	 * everything is rolled back.
	 * @param context
	 * @throws HibernateProcessException
	 */
	public void updateCenterMeeting(Client clientVO)throws HibernateProcessException,ConcurrencyException{


		Transaction tx = null;
		Session session = null;


		 try {
			session = HibernateUtil.getSession();;
			tx = session.beginTransaction();
			if(clientVO.getCustomerMeeting()!=null && clientVO.getCustomerMeeting().getMeeting()!=null){
				logger.debug("centerVO.getCustomerMeeting().getMeeting(): "+clientVO.getCustomerMeeting().getMeeting().getMeetingPlace());
				session.update(clientVO.getCustomerMeeting().getMeeting());
			}

			tx.commit();
		  }
		  catch ( StaleObjectStateException sose )
			{
			  throw new ConcurrencyException(ExceptionConstants.CONCURRENCYEXCEPTION,sose);
			}
		  catch (Exception e) {
				throw new HibernateProcessException(e);
			}
		  finally {
				HibernateUtil.closeSession(session);
			}
	}
}


