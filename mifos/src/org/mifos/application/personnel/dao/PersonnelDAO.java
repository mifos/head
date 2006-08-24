/**

 * PersonnelDAO.java    version: xxx

 

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

package org.mifos.application.personnel.dao;

import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.valueobjects.CustomFieldDefinition;
import org.mifos.application.master.util.valueobjects.SupportedLocales;
import org.mifos.application.office.dao.OfficeDAO;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.personnel.exceptions.PersonnelException;
import org.mifos.application.personnel.util.helpers.IDGenerator;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelHelper;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.application.personnel.util.valueobjects.PersonnelMovement;
import org.mifos.application.personnel.util.valueobjects.PersonnelRole;

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
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryInputs;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class denotes the DAO layer for the Personnel module.
 * @author ashishsm
 */
public class PersonnelDAO extends DAO {
	/**An insatnce of the logger which is used to log statements */
	private  MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.PERSONNEL_LOGGER);
	
  	/**
	 * This method is called to create the Personnel.
	 * It persists the data entered for the Personnel in the database.
	 * @param context instance of Context
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public void create(Context context)throws ApplicationException,SystemException {
	  Personnel personnel = (Personnel)context.getValueObject();
	  
	  //initialize personnel associations before creating
	  initializePersonnel(personnel);
	  Transaction tx = null;
	  Session session = null;
	  try {
		  session = HibernateUtil.getSession();
		  tx = session.beginTransaction();
		  personnel.setGlobalPersonnelNum("xx");
		  session.save(personnel);
		  //generate global personnel num for the saved personnel
		  personnel.setGlobalPersonnelNum(IDGenerator.generateIdForUser(personnel.getOffice().getGlobalOfficeNum(),personnel.getPersonnelId()));
		  session.update(personnel);
		  tx.commit();
	  }catch (HibernateProcessException hpe){
	  		tx.rollback();
	  		throw new PersonnelException(PersonnelConstants.CREATE_FAILED,hpe);
		}catch (HibernateException he){
			tx.rollback();
			throw new PersonnelException(PersonnelConstants.CREATE_FAILED,he);
		}
		 finally {
			HibernateUtil.closeSession(session);
		}
  }
	
 	/**
	 * This method is the helper method that initializes the associations related to personnel before 
	 * saving in the database.
	 * @param personnel instance of personnel to be created/updated
	 */
	private void  initializePersonnel(Personnel personnel){
		logger.debug("in method initializePersonnel of personnel with personnelId: "+personnel.getPersonnelId());
		//set personnel and personnel roles association
		Set personnelRoles = personnel.getPersonnelRolesSet();
		if(personnelRoles!=null){
			logger.debug("Size of personnel Roles :"+personnelRoles.size());
			Iterator rolesIterator = personnelRoles.iterator();
			while(rolesIterator.hasNext()){
			  PersonnelRole role = (PersonnelRole)rolesIterator.next();
			  role.setPersonnel(personnel);
			}
		}
		else
			personnel.setPersonnelRolesSet(null);
		
		//set personnel and personnel details association
		logger.debug("In initializePersonnel personnel.getPersonnelDetails().getPersonnelId(): "+ personnel.getPersonnelDetails().getPersonnelId());
		personnel.getPersonnelDetails().setPersonnel(personnel);
  }

	/**
	 * This method is called to update personnel/user to unlocking his account. 
	 * @param context instance of Context
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public void updateUser(Context context)throws ApplicationException,SystemException {
		Personnel personnel = (Personnel)context.getValueObject();
		
		
		//since only user account has been unlocked, there are no changes in roles. 
		//following line won't update roles in the database.
		  personnel.setPersonnelRolesSet(null);
		  
		  Transaction tx = null;
		  Session session = null;
		  try {
			  //for change log
			  LogValueMap  objectMap = new LogValueMap();
			  objectMap.put(AuditConstants.REALOBJECT,new Personnel());
			  LogInfo logInfo= new LogInfo(personnel.getPersonnelId(),PersonnelConstants.PERSONNEL,context,objectMap);
			  
			  session=HibernateUtil.getSessionWithInterceptor(logInfo);
			  
			  tx = session.beginTransaction();
			  //update personnel
			  session.update(personnel);
			  tx.commit();
		  }catch(StaleObjectStateException sose){
			  tx.rollback();
			  throw new PersonnelException(PersonnelConstants.INVALID_VERSION,sose);
		  }
		  catch (HibernateProcessException hpe){
		  	  tx.rollback();
		  	  throw new PersonnelException(PersonnelConstants.UPDATE_FAILED,hpe);
		  }catch (HibernateException he){
			  tx.rollback();
			  throw new PersonnelException(PersonnelConstants.UPDATE_FAILED,he);
		  }
		   finally {
				HibernateUtil.closeSession(session);
		   }
	}
	
	/**
	 * This method is called to update personnel object in the database
	 * @param context instance of Context
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public void update(Context context)throws ApplicationException,SystemException {
		  Personnel personnel = (Personnel)context.getValueObject(); 
		  logger.info("Updating personnel with id: "+ personnel.getPersonnelId());
		  //initialize personnel associations before creating
		  initializePersonnel(personnel);
	
		  //check if user is to be transferred across different offices
		  PersonnelMovement personnelMovement = handleTransfer(context);
		  Transaction tx = null;
		  Session session = null;
		  try {
			  LogValueMap  objectMap = new LogValueMap();
			  objectMap.put(AuditConstants.REALOBJECT,new Personnel());
			  objectMap.put("personnelDetails",AuditConstants.REALOBJECT);
			  objectMap.put("level",AuditConstants.REALOBJECT);
			  objectMap.put("preferredLocale",AuditConstants.REALOBJECT);
			  objectMap.put("personnelRolesSet",AuditConstants.REALOBJECT);
			  objectMap.put("office",AuditConstants.REALOBJECT);
			  objectMap.put("role","personnelRolesSet");
			  LogInfo logInfo= new LogInfo(personnel.getPersonnelId(),PersonnelConstants.PERSONNEL,context,objectMap);
			
			  session=HibernateUtil.getSessionWithInterceptor(logInfo);
			 
			  tx = session.beginTransaction();
			  
			  //delete roles, if they are removed for personnel in this update
			  deleteExistingRoles(session,(List<PersonnelRole>)context.getBusinessResults(PersonnelConstants.ROLES_TO_DELETE));
			  
			  //update personnel
			  session.update(personnel);
			  
			  //if personnel is transferred in different branch, insert record in personnel movement
			  if(personnelMovement!=null)
				  session.save(personnelMovement);
			  
			  //commit the transaction
			  tx.commit();
			  logger.info("Personnel with id: "+ personnel.getPersonnelId()+" successfully updated");
		  }catch(StaleObjectStateException sose){
			  tx.rollback();
			  throw new ConcurrencyException(PersonnelConstants.INVALID_VERSION,sose);
		  }catch (HibernateProcessException hpe){
		  		tx.rollback();
		  		throw new PersonnelException(PersonnelConstants.UPDATE_FAILED,hpe);
			}catch (HibernateException he){
				tx.rollback();
				throw new PersonnelException(PersonnelConstants.UPDATE_FAILED,he);
			}
			 finally {
				HibernateUtil.closeSession(session);
			}
  }
	
	/**
	 * This method deleted roles for personnel, if they are removed in this update.
	 * @param session is the current hiberante session
	 * @param rolesToDelete list of roles to be deleted
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private void deleteExistingRoles(Session session,List<PersonnelRole> rolesToDelete)throws ApplicationException,SystemException {
		  if(rolesToDelete!=null){
			  logger.info("Deleting "+rolesToDelete.size()+" for personnel, while updating personnel");
			  for(int i=0;i<rolesToDelete.size();i++)
				  session.delete(rolesToDelete.get(i));
		  }
	}
  
	/**
	 * This method handles work related to transfers, if user is to be transferred across branch.
	 * It creates and returns a PersonnelMovement instance
	 * @param context instance of Context
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private PersonnelMovement handleTransfer(Context context)throws ApplicationException,SystemException{
		  Object obj = context.getBusinessResults(PersonnelConstants.TRANSFER);
		  PersonnelMovement personnelMovement = null;
		  if(obj!=null && ((Short)obj).shortValue()==(Constants.YES)){
			  Personnel personnel=(Personnel)context.getValueObject();
			  //set the office to which personnel is to be transferred
			  personnel.setOffice(new OfficeDAO().getOffice(personnel.getOffice().getOfficeId()));
			  personnel.getPersonnelDetails().setDateOfLeavingBranch(new PersonnelHelper().getCurrentDate());
			  personnelMovement = new PersonnelMovement();
			  personnelMovement.setCreatedBy(context.getUserContext().getId());
			  personnelMovement.setCreatedDate(new PersonnelHelper().getCurrentDate());
			  personnelMovement.setPersonnelId(personnel.getPersonnelId());
			  personnelMovement.setEndDate(personnel.getPersonnelDetails().getDateOfLeavingBranch());
			  personnelMovement.setStartDate(personnel.getPersonnelDetails().getDateOfJoiningBranch());
			  personnelMovement.setOfficeId(personnel.getOffice().getOfficeId());
			  personnel.getPersonnelDetails().setDateOfJoiningBranch(new PersonnelHelper().getCurrentDate());
			  personnelMovement.setVersionNo((short)0);
			  logger.info("transferring personnel with id: "+ personnel.getPersonnelId()+" in office with id: "+ personnel.getOffice().getOfficeId());
		  }
		  return personnelMovement;
	}
  
  /**
   * This method retrieves the list of locales that this application supports
   * @return The list of list of SupportedLocales instances
   * @throws ApplicationException
   * @throws SystemException
   */	
   public List<SupportedLocales> getSupportedLocales()throws SystemException{
	   Session session = null;
	   List<SupportedLocales> locales=null;
	   try{
		   session=HibernateUtil.getSession();
		  // Query query=executeNamedQuery()
		   locales =  (List<SupportedLocales>)executeNamedQuery(NamedQueryConstants.GET_SUPPORTED_LOCALES,null,session);
		   if(null!=locales){
			   for(int i=0 ;i<locales.size();i++){
				   SupportedLocales sl = locales.get(i);
				   sl.getLanguage().getLanguageName();
			   }
		   }
	   }catch(HibernateProcessException hpe){
		   throw hpe;
	   }
	   finally{
		   HibernateUtil.closeSession(session);
	   }
	   return locales;
   }

   /**
    * This method finds user based on user systemId.
    * @param systemId is the global personnel number
    * @return instance of Personnel if found, otherwise null
    * @throws ApplicationException
    * @throws SystemException
    */
	public Personnel getUser(String systemId) throws ApplicationException,SystemException {
	   Personnel personnel = null;
		Session session = null;
		try{
			session = HibernateUtil.getSession();
			
			HashMap queryParameters = new HashMap();
			queryParameters.put("systemId", systemId);
			
			List personnelList=executeNamedQuery(NamedQueryConstants.GET_PERSONNEL_BY_SYSTEM_ID,queryParameters,session);
			if(personnelList!=null && personnelList.size()>0)
				personnel = (Personnel)personnelList.get(0);
			//personnel = (Personnel)query.uniqueResult();
			
			//if personnel not found return null;
			if(personnel == null)
				return null;
			
			//call specific getters to load lazy associations
			
			//get roles
			Set personnelRolesSet=personnel.getPersonnelRolesSet();
			if(personnelRolesSet!=null){
				PersonnelRole personnelRole = null;
				Iterator personnelRolesIterator = personnelRolesSet.iterator();
				while(personnelRolesIterator.hasNext()){
					personnelRole=(PersonnelRole)personnelRolesIterator.next();
					personnelRole.getRole().getName();
				}
			}
			//get language
			if(personnel.getPreferredLocale()!=null){
				personnel.getPreferredLocale().getLanguage().getLanguageName();
			}
			//get office
				personnel.getOffice().getGlobalOfficeNum();
			logger.info("personnel retrieved with systemid: "+systemId);
			}catch(HibernateProcessException hpe){
				throw hpe;
			}finally{
				HibernateUtil.closeSession(session);
			}
			return personnel;
	   }

   /**
    * This method finds user based on user id (auto-generated running number).
    * @param userId 
    * @return instance of Personnel if found, otherwise null
    * @throws ApplicationException
    * @throws SystemException
    */
    public Personnel getUser(short userId) throws ApplicationException,SystemException {
	 	Personnel personnel = null;
	 	Session session = null;
	 	try{
	 		session = HibernateUtil.getSession();
	 		personnel=(Personnel)session.get(org.mifos.application.personnel.util.valueobjects.Personnel.class,userId);
	 		if(personnel==null)
	 			return null;
	 		//get office
	 		personnel.getOffice().getGlobalOfficeNum();
	 		//get language
			if(personnel.getPreferredLocale()!=null){
				personnel.getPreferredLocale().getLanguage().getLanguageName();
			}
	 		logger.info("personnel retrieved with userId: "+userId);
		}catch(HibernateProcessException hpe){
			throw hpe;
		}finally{
			HibernateUtil.closeSession(session);
		}
		return personnel;
    }

	/**
	 * This method to obtain maximum number of userId that has been assigned to personnel till now in the given office.
	 * @param officeId, the office in which max personnelId is to be find
	 * @session current hiberante session
	 * @return max personnelId
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public short getMaxPersonnelId(Short officeId, Session session) throws ApplicationException,SystemException {
		Short count =0 ;
		try{
			HashMap queryParameters = new HashMap();
			queryParameters.put("officeId", officeId);
			List queryResult=executeNamedQuery(NamedQueryConstants.GET_MAX_PERSONNEL_ID,queryParameters);
			if(null!=queryResult && queryResult.size()>0){
				Object obj = queryResult.get(0);
				if(obj!=null)
					count = (Short)obj;
			} 
			return count;
		}catch(HibernateException he){
			throw he;
		}
	}
	
   /**
	* This method retrieves the list of custom fields for the Personnel
	* @param entityType entity type of the personnel 
	* @return The list of custom field types that can be assigned for the personnel
	* @throws ApplicationException
	* @throws SystemException
	*/	
   public SearchResults getCustomFieldDefinitions(short entityType)throws ApplicationException,SystemException{
	   Session session = null;
	   List queryResult = null;
	   try{
		  session = HibernateUtil.getSession();
		  HashMap queryParameters = new HashMap();
		  queryParameters.put("entityType",entityType);
		  queryResult = executeNamedQuery(NamedQueryConstants.MASTERDATA_CUSTOMFIELDDEFINITION,queryParameters,session);
		  if(null!=queryResult && queryResult.size()>0){
			  for(int i=0;i<queryResult.size();i++){
				 ((CustomFieldDefinition)queryResult.get(i)).getLookUpEntity().getEntityType();
			  }
		  }
	   }catch(HibernateException he){
			throw new HibernateSystemException(he);
		}finally{
			HibernateUtil.closeSession(session);
		}
		SearchResults searchResults = new SearchResults();
 		searchResults.setResultName(PersonnelConstants.CUSTOM_FIELDS);
 		searchResults.setValue(queryResult);
 		return searchResults;
	}  
   
   /**
	* This method retrieves the list of branch offices under the office with given searchId
	* @param statusId status of the offices to be retrieved
	* @param searchId tells search id of the office, for which offices need to be searched
	* @return SearchResult that contains list of branch offices
	* @throws ApplicationException
	* @throws SystemException
	*/	
   public SearchResults getOffices(short statusId,String searchId)throws ApplicationException,SystemException{
	   MasterDataRetriever masterDataRetriever = null;
		try{
			masterDataRetriever = getMasterDataRetriever();
		}
		catch(HibernateProcessException hpe){
			throw new ApplicationException(hpe);
		}

		masterDataRetriever.prepare(NamedQueryConstants.OFFICE_MASTER, PersonnelConstants.OFFICE_LIST);
		masterDataRetriever.setParameter("statusId",statusId);
		masterDataRetriever.setParameter("searchId",searchId+"%");
		return masterDataRetriever.retrieve();
   }
   /** 
   *  This method checks if the user name already exists in the system.If yes it returns true, otherwise false.
   *  The user name has to be unique across the MFI.This is called just before creating a new user in the 
   *  database. 
   *  @param userName The name for which duplicacy is checked
   *  @return Returns true or false as to whether the user name exists
   *  @throws SystemException
   */
  public boolean isUserNameExists(String userName)throws SystemException {
	  try{
		  HashMap queryParameters = new HashMap();
		  queryParameters.put("UserName", userName);
		  List queryResult=executeNamedQuery(NamedQueryConstants.GET_PERSONNEL_BY_USERNAME,queryParameters);
		  if(null!=queryResult){
			  if(queryResult.size()==0 || queryResult.get(0)==null)
				  return false;
			  else
				  return true;
		  }
		  return false;
	  }catch(HibernateProcessException hpe){
		  throw new SystemException(hpe);
	  }
	  
  }
    /**
	 * This method returns a status based on passed in statusId
	 * @param localeId  user locale
	 * @param statusId status id
	 * @return SearchResults status 
	 * @throws ApplicationException
	 * @throws SystemException
	 */	
	public SearchResults getStatusMaster(short localeId , short statusId)throws ApplicationException,SystemException{
		MasterDataRetriever masterDataRetriever = null;
	
		try{
			masterDataRetriever = getMasterDataRetriever();
		}
		catch(HibernateProcessException hpe){
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare(NamedQueryConstants.MASTERDATA_PERSONNEL_STATUS, GroupConstants.STATUS);
		masterDataRetriever.setParameter("localeId" ,localeId );
		masterDataRetriever.setParameter("statusId",statusId);
		return masterDataRetriever.retrieve();
	}

    /**
	 * This method counts active centers whose loan officer is the user, with passed in userId
	 * @param userId  id of the loan officer
	 * @param officeId loan officer office id
	 * @return Integer count of active centers under this personnel(Loan Officer). 
	 * @throws ApplicationException
	 * @throws SystemException
	 */	
	public Integer getActiveCenterCount(short userId,short officeId) throws SystemException{
		Integer count=0 ;
		try{
			HashMap queryParameters = new HashMap();
			queryParameters.put("levelId", CustomerConstants.CENTER_LEVEL_ID);
			queryParameters.put("userId", userId);
			queryParameters.put("officeId", officeId);
			queryParameters.put("statusId", CustomerConstants.INACTIVE_STATE);
			List queryResult=executeNamedQuery(NamedQueryConstants.GET_ACTIVE_CENTER_COUNT,queryParameters);
			if(null!=queryResult && queryResult.size()>0){
				Object obj = queryResult.get(0);
				if(obj!=null)
					count = (Integer)obj;
			} 
			return count;
			
		}catch(HibernateException he){
			throw he;
		}
	}
	
	/**
	 * This method counts active groups whose loan officer is the user, with passed in userId
	 * @param userId  id of the loan officer
	 * @param officeId loan officer office id
	 * @return Integer count of active groups under this personnel(Loan Officer). 
	 * @throws ApplicationException
	 * @throws SystemException
	 */	
	public Integer getActiveGroupCount(short userId,short officeId) throws ApplicationException,SystemException{
		Integer count =0;
		try{
			HashMap queryParameters = new HashMap();
			queryParameters.put("levelId", CustomerConstants.GROUP_LEVEL_ID);
			queryParameters.put("userId", userId);
			queryParameters.put("officeId", officeId);
			queryParameters.put("statusId1",  GroupConstants.CANCELLED);
			queryParameters.put("statusId2",  GroupConstants.CLOSED);
			List queryResult=executeNamedQuery(NamedQueryConstants.GET_ACTIVE_CUSTOMER_COUNT,queryParameters);
			if(null!=queryResult && queryResult.size()>0){
				Object obj = queryResult.get(0);
				if(obj!=null)
					count = (Integer)obj;
			}
			return count;
		}catch(HibernateException he){
			throw he;
		}
	}

	/**
	 * This method counts active clients whose loan officer is the user, with passed in userId
	 * @param userId  id of the loan officer
	 * @param officeId loan officer office id
	 * @return Integer count of active clients under this personnel(Loan Officer). 
	 * @throws ApplicationException
	 * @throws SystemException
	 */	
	public Integer getActiveClientCount(short userId,short officeId) throws ApplicationException,SystemException{
		Integer count =0;
		
		try{
			HashMap queryParameters = new HashMap();
			queryParameters.put("levelId", CustomerConstants.CLIENT_LEVEL_ID);
			queryParameters.put("userId", userId);
			queryParameters.put("officeId", officeId);
			queryParameters.put("statusId1",  ClientConstants.STATUS_CANCELLED);
			queryParameters.put("statusId2",  ClientConstants.STATUS_CLOSED);
			List queryResult=executeNamedQuery(NamedQueryConstants.GET_ACTIVE_CUSTOMER_COUNT,queryParameters);
			if(null!=queryResult && queryResult.size()>0){
				Object obj = queryResult.get(0);
				if(obj!=null)
					count = (Integer)obj;
			}
			return count;
			
		}catch(HibernateException he){
			throw he;
		}
	}
	
	
	/**
	 * This method counts the total number of customers for whome loan officer is the user, with passed in userId
	 * @param userId  id of the loan officer
	 * @param officeId loan officer office id
	 * @return Integer count of customers under this personnel(Loan Officer). 
	 * @throws ApplicationException
	 * @throws SystemException
	 */	
	public Integer getCustomerCountWithLO(short userId,short officeId) throws ApplicationException,SystemException{
		Integer count=0 ;
		Session session = null;
		try{
			HashMap queryParameters = new HashMap();
			queryParameters.put("userId", userId);
			queryParameters.put("officeId", officeId);
			List queryResult=executeNamedQuery(NamedQueryConstants.GET_CUSTOMER_COUNT,queryParameters);
			if(null!=queryResult && queryResult.size()>0){
				Object obj = queryResult.get(0);
				if(obj!=null)
					count = (Integer)obj;
			}
			return count;
		}catch(HibernateException he){
			throw new HibernateSystemException(he);
		}finally{
			HibernateUtil.closeSession(session);
		}
	}
	
	/**
	 * This method is used to search for users as per data scope of the logged in user.
	 * If the logged in user is of area office than it returns all personnels under that area office 
	 * and its child offices where name starts with passed in searchString.
	 * If the logged in user is of that of branch, it return personnels under that branch only,
	 * where name starts with passed in searchString.
	 * @param officeSearchId searchId of the office (It is the office of logged in user)
	 * @param searchString The string entered by user to search for personnels
	 * @return A query result object containg the results
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	
	public QueryResult search(String officeSearchId , String searchString) throws SystemException, ApplicationException {
		Session session = null;
		try{
			QueryResult queryResult = QueryFactory.getQueryResult(PersonnelConstants.USER_LIST);
			session = queryResult.getSession();
			Query query = null;

			//if the search string is not present then all the users belonging to either the office or the child offices under that office are retrieved
			//should be retrieved
			if(PersonnelHelper.isNullOrBlank(searchString)){
				searchString="";
			}
			//if the search string is present, then all those users that have a name like that of the search string and belonging to either the office or the child offices under that office are retrieved
			else{
				query=session.getNamedQuery(NamedQueryConstants.SEARCH_PERSONNEL);
				query.setString("SEARCH_ID", officeSearchId);
				query.setString("SEARCH_ALL", officeSearchId+".%");
				query.setString("USER_NAME", searchString+"%");
			}
			
			//building the search results object. 
			 String[] aliasNames = {"officeId" , "officeName" , "personnelId" , "globalPersonnelNum","personnelName"};
			 QueryInputs inputs = new QueryInputs();
			 inputs.setPath("org.mifos.application.personnel.util.valueobjects.UserSearchResults");
			 inputs.setAliasNames(aliasNames);
			 inputs.setTypes(query.getReturnTypes());

			 queryResult.setQueryInputs(inputs);
			 queryResult.executeQuery(query);
				return queryResult;
			}catch(HibernateProcessException he){
				throw new SystemException(he);
			}
		}
	
	/**
	 * Returns true if personnel already exists in the database.
	 * The uniqueness check is done based on the uniqueness criteria of the
	 * personnel. 
	 * @param governmentId
	 * @return true with user with govt id is present in the database otherwise false
	 */
	public boolean isUserExist(String governmentId) throws SystemException, ApplicationException {
		
		  try{
			  //Checks if the client government id exists for any other customer with the same level as that of client
			  HashMap queryParameters = new HashMap();
			  queryParameters.put("GOVT_ID", governmentId);
			  List queryResult=executeNamedQuery(NamedQueryConstants.GET_PERSONNEL_BY_GOVTID,queryParameters);
			  if(null!=queryResult){
				  if(queryResult.size()==0 || queryResult.get(0)==null)
					  return false;
				  else
					  return true;
			  }
			  return false;  
		  }catch(HibernateProcessException hpe){
			  throw new SystemException(hpe);
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
	public boolean isUserExist(String name, Date dob) throws SystemException, ApplicationException {
		Session session = null;
		  Query query=null;
		  try{
			  HashMap queryParameters = new HashMap();
			  queryParameters.put("userName",name);
			  queryParameters.put("dob", dob);
			  List queryResult=executeNamedQuery(NamedQueryConstants.GET_PERSONNEL_BY_NAME_AND_DOB,queryParameters);
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
	   *  This method checks if the branch selected by the user during creation of the personnel is still active.
	   *  because in multiuser scenario between the time the user selects a branch on the UI and by the time 
	   *  it submits the record for creation this might have been updated by some other user.
	   *  @param officeId The branch whose status is checked
	   *  @return Returns true or false as to whether the branch is inactive
	   *  @throws SystemException
	   */
		public  boolean isBranchActive(short officeId)throws SystemException{
			try{
				Integer count=0;
				HashMap queryParameters = new HashMap();
				queryParameters.put("officeId", officeId);
				queryParameters.put("statusId", OfficeConstants.INACTIVE);
				List queryResult=executeNamedQuery(NamedQueryConstants.ISOFFICEACTIVE,queryParameters);
				if(null!=queryResult && queryResult.size()>0){
					Object obj = queryResult.get(0);
					if(obj!=null)
						count = (Integer)obj;
				}
				return(count.intValue()==0)?true:false; 
			}catch(HibernateProcessException hpe){
				  throw new SystemException(hpe);
			  }
		}
}
