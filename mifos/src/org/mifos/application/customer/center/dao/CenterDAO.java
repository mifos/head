/**




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

package org.mifos.application.customer.center.dao;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.IDGenerator;
import org.mifos.application.accounts.util.valueobjects.Account;
import org.mifos.application.accounts.util.valueobjects.CustomerAccount;
import org.mifos.application.configuration.business.ConfigurationIntf;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.center.exception.DuplicateCustomerException;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.customer.center.util.valueobjects.Center;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.dao.CustomerNoteDAO;
import org.mifos.application.customer.dao.CustomerUtilDAO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.customer.util.helpers.IdGenerator;
import org.mifos.application.customer.util.valueobjects.CustomFieldDefinition;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerCustomField;
import org.mifos.application.customer.util.valueobjects.CustomerHierarchy;
import org.mifos.application.customer.util.valueobjects.CustomerMaster;
import org.mifos.application.office.dao.OfficeDAO;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.components.audit.util.helpers.AuditConstants;
import org.mifos.framework.components.audit.util.helpers.LogInfo;
import org.mifos.framework.components.audit.util.helpers.LogValueMap;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.dao.helpers.MasterDataRetriever;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ConcurrencyException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.HibernateSystemException;
import org.mifos.framework.exceptions.MasterDataRetrieverException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryInputs;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;


/**
 * @author sumeethaec
 *
 */
public class CenterDAO extends DAO {
	/**An insatnce of the logger which is used to log statements */
	private MifosLogger logger =MifosLogManager.getLogger(LoggerConstants.CENTERLOGGER);
	private ConfigurationIntf labelConfig=MifosConfiguration.getInstance();
	/**
	 * This method calls the methods which retireves the list of loan officers, fees and custom fields 
	 * @param context Holds the parameters necessary to retrieve the list of loan officers fees and custom fields
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void OnLoad(Context context)throws SystemException,ApplicationException{
		
		CustomerUtilDAO customerUtilDAO = new CustomerUtilDAO();
		//Retrives list of loan officers under that office and adds to the context
		context.addAttribute(customerUtilDAO.getLoanOfficersMaster(ClientConstants.LOAN_OFFICER_LEVEL, ((Center)context.getValueObject()).getOffice().getOfficeId(),context.getUserContext().getId(),context.getUserContext().getLevelId(), CustomerConstants.LOAN_OFFICER_LIST));
		//Retrives list of additional fees that can be applied to the center and adds to the context
		 List additionalFeeList =(List)customerUtilDAO.getFeesMasterWithoutLevel(ClientConstants.All_CATEGORY_ID,CenterConstants.CUSTOMER_CATEGORY_ID , CustomerConstants.CENTER_LEVEL_ID,CustomerConstants.FEES_LIST).getValue();
		 //set meetings for fees
		 new CustomerUtilDAO().setMeetingForFees(additionalFeeList);
		 /*for(int i=0;i<additionalFeeList.size();i++){
			 System.out.println("**************************Fee Meeting Periodicty in DAO: " + ((FeeMaster)additionalFeeList.get(i) ).getFeeMeeting().getFeeMeetingSchedule());
		 }*/
		 context.addAttribute(new CustomerHelper().getResultObject(CustomerConstants.FEES_LIST,additionalFeeList));
		 //Retrives list of administrative fees that has been applied to the center and adds to the context
		 List adminFeeList =(List)customerUtilDAO.getFeesMasterWithLevel(CustomerConstants.CENTER_LEVEL_ID ,CustomerConstants.ADMIN_FEES_LIST).getValue();
		 //set meetings for fees
		 new CustomerUtilDAO().setMeetingForFees(adminFeeList);
		 context.addAttribute(new CustomerHelper().getResultObject(CustomerConstants.ADMIN_FEES_LIST,adminFeeList));


		//Retrives list of custom fields for the center and adds to the context
		context.addAttribute(customerUtilDAO.getCustomFieldDefnMaster(CustomerConstants.CENTER_LEVEL_ID,CustomerConstants.CENTER_ENTITY_TYPE,CenterConstants.CUSTOM_FIELDS));
		
	}
	
	
	/** 
	 *  This method returns the check list associated with a center. All the items in this check list have to be ticked before the center status can be changed
	 * @throws MasterDataRetrieverException 
	 */
	
	public SearchResults getChecklists(short statusId , short levelId)throws SystemException, MasterDataRetrieverException{
		MasterDataRetriever masterDataRetriever = null;
		try{
			masterDataRetriever = getMasterDataRetriever();
		}
		catch(HibernateProcessException hpe){
			
		}
		
		masterDataRetriever.prepare(NamedQueryConstants.MASTERDATA_CHECKLIST, CenterConstants.CHECKLISTS);
		masterDataRetriever.setParameter("status",statusId);
		masterDataRetriever.setParameter("level",levelId);
		return masterDataRetriever.retrieve();
		
	}
	
	/** 
	 *  This method returns the list of MFI titles
	 * @throws MasterDataRetrieverException 
	 */
	
	public SearchResults getPositions(short localeId )throws SystemException, MasterDataRetrieverException{
		MasterDataRetriever masterDataRetriever = null;
		try{
			masterDataRetriever = getMasterDataRetriever();
		}
		catch(HibernateProcessException hpe){
			
		}
		masterDataRetriever.prepare(NamedQueryConstants.MASTERDATA_POSITIONS, CenterConstants.POSITIONS);
		masterDataRetriever.setParameter("localeId" ,localeId );
		
		return masterDataRetriever.retrieve();
		
	}
	/**
	 * This method retrieves the list of custom fields for the center
	 * @return The list of custom field types that can be assigned for the center
	 * @throws SystemException
	 * @throws MasterDataRetrieverException
	 */
	private SearchResults getCustomFieldDefnMaster()throws SystemException, MasterDataRetrieverException{
		MasterDataRetriever masterDataRetriever = null;
		try{
			masterDataRetriever = getMasterDataRetriever();
		}
		catch(HibernateProcessException hpe){
			
		}
		//prepares the named query and puts the result object under the name customFields
		masterDataRetriever.prepare(NamedQueryConstants.MASTERDATA_CUSTOMERCUSTOMFIELDDEFINITION, CenterConstants.CUSTOM_FIELDS);
		masterDataRetriever.setParameter("levelId",CustomerConstants.CENTER_LEVEL_ID);
		masterDataRetriever.setParameter("entityType",Short.valueOf("1"));
		return masterDataRetriever.retrieve();
	}
	
	/** 
	   *  This method checks if the center name already exists in the system.If yes it returns true, otherwise false.
	   *  The center name has to be unique across the MFI.This is called just before creating a new center in the 
	   *  database. 
	   *  @param centerName The name for which duplicacy is checked
	   *  @return Returns true or false as to whether the center name exists
	   *  @throws SystemException, DuplicateCustomerException
	   */
	  public boolean ifCenterNameExists(String centerName)throws SystemException, DuplicateCustomerException {
		  Session session = null;
		  Query query=null;
		  boolean centerNameExists = false;
		  logger.debug("CenterName: "+ centerName);
		  
		  try{
			  HashMap queryParameters = new HashMap();
			  queryParameters.put("centername",centerName);
			  queryParameters.put("LEVELID",CustomerConstants.CENTER_LEVEL_ID);
			  List queryResult = executeNamedQuery(NamedQueryConstants.DOES_CENTER_NAME_EXIST,queryParameters);
			  if(null!=queryResult){
				  if(queryResult.size()==0 || queryResult.get(0)==null)
					  return false;
				  else
					  return true;
			  }
			  return false;  
			 	  
			  
		  }catch(HibernateProcessException hpe){
			  throw new SystemException(hpe);
		  }finally{
			 HibernateUtil.closeSession(session);
		  }
		  
	  }
	  
	  

	  
		
	  /**
	   * This method is called to cretae the center. It persists the data entered for the center in the database
	   * @throws SystemException, ApplicationException
	   */
	  public void create(Context context) throws ApplicationException,SystemException
		{
		  int customerCount =0;
			logger.info("Inside create method");
		  	//obtains the center value object from the context
		  	Center center =(Center)context.getValueObject();
			CustomerAccount customerAccount = center.getCustomerAccount();
			CustomerHierarchy customerHierarchy = center.getCustomerHierarchy();
			if(customerHierarchy!=null)
				customerHierarchy.setCustomer(center);
			//This is to set to null as no data goes into this table 
			center.setCustomerDetail(null);
			//This is to set to null as no data goes into this table 
			center.setCustomerFormedByPersonnel(null);
			//This is to set to null as no data goes into this table during create of a center
			center.setCustomerPositions(null);
			//This is to set the custmerId in customer_address_detail same as customerId in customer table. 
			center.getCustomerAddressDetail().setCustomer(center);
			//This is to set the custmerId in customer_account same as customerId in customer table. 
			customerAccount.setCustomer(center);
			//setting the customer historical data to null as center will not have any historical data associated with it
			center.setCustomerHistoricalData(null);
//			 set the account details. prd offering name and prd offering is being
			// set to NULL
			center.getCustomerAccount().setPersonnelId(
					center.getPersonnel().getPersonnelId());
			center.getCustomerAccount().setOfficeId(center.getOffice().getOfficeId());
			center.getCustomerAccount().setAccountTypeId(
					Short.valueOf(AccountTypes.CUSTOMERACCOUNT));
			// setting the customer account state to active. To be reset when
			// cancelled, deleted, on hold or withdrawn
			center.getCustomerAccount().setAccountStateId(
					Short.valueOf(AccountStates.CUSTOMERACCOUNT_ACTIVE));
			center.getCustomerAccount().setCustomer(center);
			// TODO change this strategy
			center.getCustomerAccount().setGlobalAccountNum(IDGenerator.generateIdForCustomerAccount(new OfficeDAO().getOffice(center.getOffice().getOfficeId()).getGlobalOfficeNum()));
			center.getCustomerAccount()
					.setCreatedBy(center.getPersonnel().getPersonnelId());
			center.getCustomerAccount().setCreatedDate(
					new Date(new java.util.Date().getTime()));

			if (center.getCustomerAccount() != null
					&& center.getCustomerAccount().getAccountFeesSet() != null) {
				if (center.getCustomerAccount() != null
						&& center.getCustomerAccount().getAccountFeesSet() != null
						&& center.getCustomerAccount().getAccountFeesSet().size() == 0)
					center.getCustomerAccount().setAccountFeesSet(null);
			}
			//no children of the client
			center.setMaxChildCount(new Integer(0));
			center.convertCustomFieldDateToDbformat(context.getUserContext().getMfiLocale());
			Session session = null;
			Transaction trxn = null;
			try {
				session = HibernateUtil.getSession();
				trxn = session.beginTransaction();

				//save meeting only if user has selected any
				if(center.getCustomerMeeting()!=null && center.getCustomerMeeting().getMeeting()!=null){
					center.getCustomerMeeting().setUpdatedFlag(YesNoFlag.NO.getValue());
					session.save(center.getCustomerMeeting().getMeeting());
				}
				session.save(center);
				//the center is set with the global customer number that has been generated from
				//the office global number and the current customerId of the center 
				String gCustNum=IdGenerator.generateSystemIdForCustomer(center.getOffice().getGlobalOfficeNum(),center.getCustomerId());
				center.setGlobalCustNum(gCustNum);
				//update center
				new CustomerHelper().saveMeetingDetails(center,session, context.getUserContext());
				session.update(center);
				session.flush();
				
				trxn.commit();
			} catch (HibernateProcessException hpe) {
				trxn.rollback();
				throw new ApplicationException(hpe);
			}
			catch (HibernateException hpe) 
			{ hpe.printStackTrace();
			trxn.rollback();
				
				throw new CustomerException(CustomerConstants.CREATE_FAILED_EXCEPTION,new Object[]{labelConfig.getLabel(ConfigurationConstants.CENTER,context.getUserContext().getPereferedLocale())});
			}
			catch (Exception e) 
			{ 	trxn.rollback();
				throw new CustomerException(CustomerConstants.CREATE_FAILED_EXCEPTION,e,new Object[]{labelConfig.getLabel(ConfigurationConstants.CENTER,context.getUserContext().getPereferedLocale())});
			}
			finally {
				HibernateUtil.closeSession(session);
			}
		}
	  private Short getFieldType(Short fieldId) throws SystemException{
		  	CustomFieldDefinition customFieldDefn = null;
		  	HashMap queryParameters = new HashMap();
			//level id of the center
			queryParameters.put("FIELDID", fieldId);
			List queryResult=executeNamedQuery(NamedQueryConstants.GET_FIELD_TYPE,queryParameters);
			if(null!=queryResult && queryResult.size()>0){
				Object obj = queryResult.get(0);
				if(obj!=null){
					customFieldDefn = (CustomFieldDefinition)obj;
			
				}
			}
			return customFieldDefn.getFieldType();
			
	}


	/**
	   * This method is called to update the personnel of the children of the center, if the centers personnel was
	   * changed during an edit.
	   * @param session
	   * @param customerId
	   */
	  private void updateChildrenPersonnel(Session session, Integer customerId , Short newPersonnelId) {
		  HashMap queryParameters = new HashMap();
		  		//search id of the center
				queryParameters.put("CUSTOMER_ID", customerId);
				//office of center
				queryParameters.put("PERSONNEL_ID", newPersonnelId);

				List queryResult=executeNamedQuery(NamedQueryConstants.UPDATE_CHILD_PERSONNEL,queryParameters,session);
				
				
			
	  }


	/***
	   * This method obtains the number of centers 
	   * @return The number of centers
	   * @throws HibernateProcessException
	   * @throws HibernateSystemException
	   */
	  	public int getCenterCount(short officeId) throws SystemException {
		
			Integer count =0; ;
			HashMap queryParameters = new HashMap();
						
			try{
				//level id of the center
				queryParameters.put("LEVELID", CustomerConstants.CENTER_LEVEL_ID);
				//office of center
				queryParameters.put("OFFICEID", officeId);

				List queryResult=executeNamedQuery(NamedQueryConstants.GET_CENTER_COUNT,queryParameters);
				
				if(null!=queryResult && queryResult.size()>0){
					Object obj = queryResult.get(0);
					if(obj!=null)
						count = (Integer)obj;
				}
				return count.intValue();
			}catch(HibernateException he){
				throw new HibernateSystemException(he);
			}
		
	}
	/**
	 * This method obtains the number of customers under a particular office 
	 * @param officeId
	 * @return
	 * @throws HibernateProcessException
	 * @throws HibernateSystemException
	 */
	public int getCustomerCount(short officeId) throws SystemException {
		
		Integer count =0;
		
		HashMap queryParameters = new HashMap();
		try{
			//office of center
			queryParameters.put("OFFICEID", officeId);
			List queryResult=executeNamedQuery(NamedQueryConstants.GET_CUSTOMER_COUNT_INOFFICE,queryParameters);
			
			if(null!=queryResult && queryResult.size()>0){
				Object obj = queryResult.get(0);
				if(obj!=null)
					count = (Integer)obj;
			}		
			
			return count.intValue();
		}catch(HibernateException he){
			throw new HibernateSystemException(he);
		}
		
	}
	/**
	 * This method obtains the center details based on a particualr global customer number
	 * @param globalCustId The system od on the basis of which the details are retrieved
	 */
	public Center getCenterDetails(String globalCustId) throws HibernateSystemException{
		Center center =null;
		try{
			logger.debug("GlobalCustId on the basis of which details retrieved"+globalCustId);
			//obtains the center information based ont he global system id
			center = findBySystemId(globalCustId);
		}
		catch(HibernateProcessException he){
			throw new HibernateSystemException(he);
		}
		return center;
		
	}
	
	/**
	 * Method which return the status of the savings account held by the center
	 * @return
	 */
	public boolean getAccountStatus() {
		return true;
		
	}
	
	/**
	 * This method is used to search for a center under a particular office or with a center with a particular name.
	 * If the office id is that of an area office and the search string is not entered, then the centers for all 
	 * the branches under that area office is retrieved. If the search string is entered then all the centers with 
	 * a name like that of the search strig is retrieved
	 * @param officeId Office id of the user
	 * @param searchString The name of the center on the basis of which the search will be done
	 * @return A query result object containg the results
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	
	public QueryResult search(short officeId , String searchString , Short userId, Short userLevelId) throws SystemException, ApplicationException {
		Session session = null;
		try{
			 QueryResult queryResult = QueryFactory.getQueryResult("CenterSearch");
			 session = queryResult.getSession();

			Office office = null;
			Query query = null;
			//retrieving the office with the officeId which has been passed
			query = session.getNamedQuery(NamedQueryConstants.GET_OFFICE_SEARCHID);
			query.setShort("OFFICE_ID", officeId);
			String searchId = "";
			//System.out.println("Inside CENTER DAO ..Search ..Before fetching office");
			List l = query.list();
			//System.out.println("Inside CENTER DAO ..Search ..After fetching office");
			if(l.size() > 0)
			{
				 searchId = (String)l.get(0);

			}
			query = session.getNamedQuery(NamedQueryConstants.SEARCH_CENTERS);;			
			query.setString("SEARCH_ID", searchId+"%");
			query.setString("CENTER_NAME", searchString+"%");
			query.setShort("LEVEL_ID" , CustomerConstants.CENTER_LEVEL_ID);
			query.setShort("STATUS_ID" , CustomerConstants.CENTER_ACTIVE_STATE);
			query.setShort("USER_ID" , userId);
			query.setShort("USER_LEVEL_ID" , userLevelId);
			query.setShort("LO_LEVEL_ID" , PersonnelConstants.LOAN_OFFICER);
			
			
			//building the search results object. 
			 String[] aliasNames = {"parentOfficeId" , "parentOfficeName" , "centerSystemId" , "centerName"};
			 QueryInputs inputs = new QueryInputs();
			 inputs.setPath("org.mifos.application.customer.center.util.valueobjects.CenterSearchResults");
			 inputs.setAliasNames(aliasNames);
			 inputs.setTypes(query.getReturnTypes());

			queryResult.setQueryInputs(inputs);
			queryResult.executeQuery(query);
			//System.out.println("---------SIZE OF QUERY: "+queryResult.getSize());
			
			return queryResult;
		}catch(HibernateProcessException he){
			throw new SystemException(he);
		}
				
		
	}
	
	
	/**
	 * Returns the center object which has the system id being passed
	 * It also tries to initialize the associations which are set to be lazy loaded in the mapping file.
	 * @param systemId
	 * @return
	 * @throws HibernateProcessException
	 * @throws HibernateSystemException
	 */
	public Center findBySystemId(String systemId) throws HibernateProcessException,HibernateSystemException{
		Center center = new Center();
		Session session = null;
		
		try{
			session = HibernateUtil.getSession();
			//obtaining the center details for the specified global customer number
			logger.debug("Gloabal cust number: "+systemId);
			HashMap queryParameters = new HashMap();
			queryParameters.put("globalCustNum", systemId);
			List queryResult=executeNamedQuery(NamedQueryConstants.FIND_CUSTOMER_BY_SYSTEM_ID,queryParameters,session);
			if(null!=queryResult && queryResult.size()>0){
				Object obj = queryResult.get(0);
				if(obj!=null)
					center = (Center)obj;
			}
			
			if(center==null)
				return null;
			//initializing the following associations
			//lazily loading certain results
			logger.debug("Lazy loading ");
			center.getCustomerAddressDetail();
			center.getCustomFieldSet();
			center.getCustomerAccounts();
			center.getOffice().getGlobalOfficeNum();
			//center.getOffice();
			if(center.getPersonnel() != null){
				center.getPersonnel().getDisplayName();
			}
			if(center.getCustomerMeeting() != null){
				center.getCustomerMeeting().getMeeting().getMeetingPlace();
			}
			
			//obtains the customer positions
			center.getCustomerPositions();
			//obtains the level id
			center.getCustomerLevel().getLevelId();
			center.getCustomerLevel().getMaxChildCount();
			if(center.getCustomerAccounts()!=null){
				Iterator accountsIterator  = center.getCustomerAccounts().iterator();
				while(accountsIterator.hasNext()){
					Account account = (Account)accountsIterator.next();
					if(account.getAccountTypeId().shortValue()== new Short(AccountTypes.CUSTOMERACCOUNT).shortValue()){
						center.setCustomerAccount((CustomerAccount)account);
						break;
					}
				}
			}
			logger.debug("Lazy loading done");
				
		}catch(HibernateException he){
			throw new HibernateSystemException(he);
		}finally{
			HibernateUtil.closeSession(session);
		}
		return center;
	}
	
	/**
	 * This method obtains all the customers which have a particular seaqrch Id within a particualr office
	 * @param searchString The search string of the center
	 * @param officeId Office under which the children are being searched
	 */
	/* TODO THIS CODE WILL BE REFACTORED TO OBTAIN THE LIST OF GROUPS AND CLIENTS SEPERATELY BY PASSING THE LEVEL ID
	 * CURRENTLY THE FUNCTION RETRIEVES ALL THE CHILDREN IRRESPECTIVE OF THE STATUS.  
	 * THE FUNCTION WILL ALSO BE MODIFIED TO RETRIEVE ONLY THOSE CUSTOMERS THAT ARE NOT CLOSED OR CANCELLED, 
	 * 
	 */
	private  List<Customer> getChildListForParent(String searchString , short officeId)throws HibernateProcessException,HibernateSystemException{
		Center center = null;
		Session session = null;
		Iterator<Customer> customerIterator =null;
		List<Customer> customerChildren = new ArrayList<Customer>();
		try{
			session = HibernateUtil.getSession();
			
			Query query = session.createQuery("from Customer customer where customer.searchId like :SEARCH_STRING and customer.office.officeId = :OFFICE_ID and customer.statusId in (1,2,3,4,7,8,9,10)");
			//Bug id 27252. Changed the searchString to get child of the Center 
			query.setString("SEARCH_STRING", searchString+".%");
			query.setShort("OFFICE_ID", officeId);
			customerIterator = query.iterate();
			//adding all the list of customers to a list. customer level is also loaded
			while(customerIterator.hasNext()){
				Customer customer = customerIterator.next();
				customer.getCustomerLevel();
				customerChildren.add(customer); 
			}
			return customerChildren;
		}catch(HibernateException he){
			throw new HibernateSystemException(he);
		}finally{
			HibernateUtil.closeSession(session);
		}
	}
	/**
	 * Updates the valueobject which it obtains from the context.
	 * The customerAccount is set null because it should never be updated from the center object
	 * but only created.Because if we update the customeraccount object it will increase the version number although none of the fields would be changed.
	 * Hence we break the association by setting it to null so that no update is called on the customer account.
	 * Then we reattch the fees set and update it seperately but as part of the same session.
	 * @see org.mifos.framework.dao.DAO#update(org.mifos.framework.util.valueobjects.Context)
	 */
	public void update(Context context)throws SystemException, ApplicationException{
		Session session =null;
		 LogValueMap namedValues = new LogValueMap();

		 Transaction trxn=null;
		try{
			
			Set accountFeesSet = null;
			Center center = (Center)context.getValueObject();
			
			logger.info("Setting associations");
			center.setCustomerDetail(null);
			//If there are no clients under that center then the customer position object in customer is set to null
			List<CustomerMaster> clients =null;
			SearchResults listOfClients =context.getSearchResultBasedOnName(CenterConstants.CLIENT_LIST);
			clients= (List)listOfClients.getValue();	
			
			if(clients.size() == 0){
				center.setCustomerPositions(null);
			}
			center.convertCustomFieldDateToDbformat(context.getUserContext().getMfiLocale());
			//session = HibernateUtil.getSession();
			namedValues.put(AuditConstants.REALOBJECT,new Customer());
			namedValues.put("personnel",AuditConstants.REALOBJECT);
			namedValues.put("customerAddressDetail",AuditConstants.REALOBJECT);
			namedValues.put("customerPositions",AuditConstants.REALOBJECT);
			namedValues.put("customFieldSet",AuditConstants.REALOBJECT);
			namedValues.put("customerPosition","customerPositions");
			LogInfo logInfo= new LogInfo(center.getCustomerId(),"Center",context,namedValues);
			session = HibernateUtil.getSessionWithInterceptor(logInfo) ;
			trxn = session.beginTransaction();
			accountFeesSet = center.getCustomerAccount().getAccountFeesSet();
			//account object set to null as no changes being made
			center.setCustomerAccount(null);
			center.setCustomerHistoricalData(null);
			center.setCustomerMeeting(null);
			center.setCustomerFormedByPersonnel(null);
			
			session.update(center);
			//List<Customer> allCenterChildren = getCenterChildDetails(center.getSearchId() ,center.getOffice().getOfficeId().shortValue());
			Short personnelId =null;
			if(center.getPersonnel() != null){
				personnelId = center.getPersonnel().getPersonnelId();
			}
			
			String hql="update Customer customer set customer.personnelId="+personnelId+" where customer.searchId like '"+center.getSearchId()+".%' and customer.office.officeId="+center.getOffice().getOfficeId();
			session.createQuery(hql).executeUpate();
			trxn.commit();
		}
		catch ( StaleObjectStateException sose )
		{
			throw new ConcurrencyException(ExceptionConstants.CONCURRENCYEXCEPTION,sose);
		}
		catch (HibernateException he) {
			trxn.rollback();
			throw new CustomerException(CustomerConstants.UPDATE_FAILED_EXCEPTION,he,new Object[]{labelConfig.getLabel(ConfigurationConstants.CENTER,context.getUserContext().getPereferedLocale())});
		 }
		finally{
			HibernateUtil.closeSession(session);
		}
		
	}
	/**
	 * This method obtains the list of child details for a particular customer
	 * @param searchString The search string of the center
	 * @return
	 */
	public List<Customer> getCenterChildDetails(String searchString , short officeId) {
		List centerChildren = null;
		try{
			centerChildren = getChildListForParent(searchString , officeId);
		}
		catch(HibernateProcessException hpe){
			logger.error("getCenterChildDetails has not obtained the center children",false,null,hpe);
		}
		catch(HibernateSystemException hse){
			logger.error("Hibernate System Exception thrown in getCenterChildDetails",false,null,hse);
		}
		//the lsit of children
		return centerChildren;
		
		
		
	}
	/**
	 * This method loads the custom fields definition , so that name and type of custom fields will be available on the details page
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void onGetLoad(Context context) throws SystemException,ApplicationException{
		//obtains the custom fields definition
		context.addAttribute(new CustomerUtilDAO().getCustomFieldDefnMaster(CustomerConstants.CENTER_LEVEL_ID,CustomerConstants.CENTER_ENTITY_TYPE,CenterConstants.CUSTOM_FIELDS));

		
	}
	
	
	
	/**
	 * This method checks if there is any child under this center which is active.
	 * @param searchString
	 * @throws HibernateSystemException
	 */
	public int countOfActiveChildren(String searchString , short officeId)throws HibernateProcessException,HibernateSystemException{
		Query query = null;
		Session session = null;
		Integer numberOfActiveChildren = null;
		try{
			
			session = HibernateUtil.getSession();
			//obtains the list of active children
			//query = session.createQuery("select count(*) from Customer customer where customer.searchId like :SEARCH_STRING and customer.statusId = :statusId and customer.office.officeId = :officeId");
			//Bug id 27881 & 27980. Change the statusId from active to partial,pending,active,hold.
			query = session.createQuery("select count(*) from Customer customer where customer.searchId like :SEARCH_STRING and customer.statusId in (7,8,9,10) and customer.office.officeId = :officeId");
			query.setString("SEARCH_STRING", searchString+".%");
			//query.setShort("statusId", GroupConstants.ACTIVE);
			query.setShort("officeId", officeId);
			
			logger.debug("Executing the query to figure out the number of active children under center with searchString ." + searchString);
			numberOfActiveChildren = (Integer)query.uniqueResult();
			logger.debug("The number of active children is ." + numberOfActiveChildren );
			
		}catch(HibernateException he){
			throw new HibernateSystemException(he);
		}catch(HibernateProcessException hpe){
			hpe.printStackTrace();
		}finally{
			HibernateUtil.closeSession(session);
		}
		
		return numberOfActiveChildren;
	}
	
	
	
	/**
	 * This updates the status for the center .It also calls addNote method on the customerdao to add notes in the database.
	 * It passes the session object to that method so that both of them work on the same transaction and if any one of them fails
	 * everything is rolled back.
	 * @param context
	 * @throws HibernateProcessException
	 */
	public void updateCenterStatus(Center centerVO , Customer oldCustomer, Context context)throws SystemException,ApplicationException{
			
		logger.debug("-------------in update center status");
		Transaction tx = null;
		Session session = null;
		Map namedValues = new HashMap();
		logger.debug("------------old Customer Id: "+ oldCustomer.getCustomerId());
		logger.debug("-----------center  Customer Id: "+ centerVO.getCustomerId());
		
		oldCustomer.setStatusId(centerVO.getStatusId());
		//setting the version number
		oldCustomer.setVersionNo(centerVO.getVersionNo());
		oldCustomer.setCustomerHistoricalData(null);
		  try {
			    namedValues.put(AuditConstants.REALOBJECT,new Customer());
				LogInfo logInfo= new LogInfo(oldCustomer.getCustomerId(),"Center",context,namedValues);
				session = HibernateUtil.getSessionWithInterceptor(logInfo) ;
			    tx = session.beginTransaction();
			
				session.update(oldCustomer);
				CustomerNoteDAO customerNoteDao = new CustomerNoteDAO();
				customerNoteDao.addNotes(session , centerVO.getCustomerNote());
				tx.commit();
		  } 
		  catch ( StaleObjectStateException sse )
			{
			  throw new ConcurrencyException(ExceptionConstants.CONCURRENCYEXCEPTION,sse);
			} 
		  catch (HibernateException he) {
			  tx.rollback();
			  throw new CustomerException(CustomerConstants.UPDATE_FAILED_EXCEPTION,new Object[]{labelConfig.getLabel(ConfigurationConstants.CENTER,context.getUserContext().getPereferedLocale())});
			 }
		  catch (Exception e) {
				throw new HibernateProcessException(e);
			} 
		  finally {
				HibernateUtil.closeSession(session);
			}
	}
	
}



