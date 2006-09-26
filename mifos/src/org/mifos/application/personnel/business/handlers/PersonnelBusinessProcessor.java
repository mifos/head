/**
 
 * PersonnelBusinessProcessor.java    version: xxx
 
 
 
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

package org.mifos.application.personnel.business.handlers;

import java.sql.Date;
import java.util.List;

import org.mifos.application.configuration.util.helpers.PathConstants;
import org.mifos.application.customer.dao.SearchDAO;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.master.util.valueobjects.EntityMaster;
import org.mifos.application.master.util.valueobjects.LookUpMaster;
import org.mifos.application.master.util.valueobjects.SupportedLocales;
import org.mifos.application.office.dao.OfficeDAO;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.office.util.valueobjects.OfficeMaster;
import org.mifos.application.personnel.dao.PersonnelDAO;
import org.mifos.application.personnel.dao.PersonnelNotesDAO;
import org.mifos.application.personnel.exceptions.DuplicateUserException;
import org.mifos.application.personnel.exceptions.HierarchyChangeException;
import org.mifos.application.personnel.exceptions.PersonnelException;
import org.mifos.application.personnel.exceptions.StatusChangeException;
import org.mifos.application.personnel.exceptions.TransferNotPossibleException;
import org.mifos.application.personnel.exceptions.UserNotFoundException;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelHelper;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.application.personnel.util.valueobjects.PersonnelDetails;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.dao.helpers.MasterDataRetriever;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.EncryptionException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.ResourceNotCreatedException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.authentication.EncryptionService;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;


/**
 * This is the business processor for the Personnel module. 
 * It takes care of handling all the business logic for the Personnel module
 */
public class PersonnelBusinessProcessor extends MifosBusinessProcessor {
	/**An insatnce of the logger which is used to log statements */
	private  MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.PERSONNEL_LOGGER);
	
	/**
	 * This method is called before loading create user page. It loads master data required to create a personnel
	 * and set that into context
	 * @param context instance of Context
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	/*public void loadInitial(Context context) throws ApplicationException,SystemException{
		try{
			logger.debug("in method loadInitial() of Personnel Module");
			//load master data for personnel page
			loadMasterData(context);
			
			//retrieve roles and set in the context
			List<Role> roleList=new RolesandPermissionDAO().getRoles();
			context.addAttribute(new PersonnelHelper().getResultObject(PersonnelConstants.ROLES_LIST,roleList));
			
			Personnel p =(Personnel)context.getValueObject();
			Office office = new OfficeDAO().getOffice(p.getOffice().getOfficeId());
			context.addAttribute(new PersonnelHelper().getResultObject(PersonnelConstants.PERSONNEL_OFFICE,office));
			
			// remove loan officer personnel level, when office opted is not a branch
			if(office.getLevel().getLevelId().shortValue()!=OfficeConstants.BRANCHOFFICE){
				EntityMaster em = (EntityMaster)context.getSearchResultBasedOnName(PersonnelConstants.PERSONNEL_LEVEL_LIST).getValue();
				List<LookUpMaster> lookUpMaster = em.getLookUpMaster();
				if(lookUpMaster!=null){
					for(int i=0;i<lookUpMaster.size();i++){
						LookUpMaster master =lookUpMaster.get(i);
						if(master.getId().shortValue()==PersonnelConstants.LOAN_OFFICER)
							lookUpMaster.remove(i);
					}
				}
			}
			logger.info("Successfully loaded master data to load the user creation page");
		}catch(SystemException se){
			throw se;
		}catch(ApplicationException ae){
			throw ae;
		}catch(Exception e ){
			throw new PersonnelException(PersonnelConstants.UNKNOWN_EXCEPTION,e);
		}
	}*/
	
	/**
	 * Retrieves master data using the MasterDataRetriever by passing the entity name, locale and the result name.
	 * It then adds the SearchResults retrieved to the context. These can be referred on UI using result name.
	 * The master data required for this module are: -
	 * SupportedLocales,Gender,Language,offices,usertitle,userhierarchy,roles.
	 * @param context
	 * @throws ApplicationException
	 * @throws SystemException
	 */	
	private void loadMasterData(Context context) throws ApplicationException,SystemException{
		try{
			logger.debug("in method loadMasterData() of Personnel Module");
			loadMasterDataForYourSettings(context);
			//get the locale id of the logged in user
			short localeId=context.getUserContext().getLocaleId();
			Object values[]= new Object[2];
			values[0]="";
			values[1]=PersonnelConstants.PERSONNEL;
			
			SearchResults obj=getMasterDataRetriever().retrieveMasterData(MasterConstants.PERSONNEL_TITLE ,localeId ,PersonnelConstants.TITLE_LIST);
			if(obj!=null)
				context.addAttribute(obj);
			else
				logger.info("MasterData:PERSONNEL_TITLE could not be loaded");
			
			obj=this.getPersonnelDAO().getCustomFieldDefinitions(PersonnelConstants.PERSONNEL_CUSTOM_FIELD_ENTITY_TYPE);
			if(obj!=null)
				context.addAttribute(obj);
			else
				logger.info("MasterData:PERSONNEL_CUSTOMFIELDS could not be loaded");
			
			obj=getMasterDataRetriever().retrieveMasterData(MasterConstants.PERSONNEL_LEVELS ,localeId ,PersonnelConstants.PERSONNEL_LEVEL_LIST,"org.mifos.application.personnel.util.valueobjects.PersonnelLevel","levelId");
			if(obj!=null)
				context.addAttribute(obj);
			else{
				values[0]=PersonnelConstants.USERLEVLELS;
				throw new PersonnelException(PersonnelConstants.ERROR_MASTERDATA,values);
			}
			
		}catch(SystemException se){
			throw se;
		}
		catch(ApplicationException ae){
			throw ae;
		}
	}
	
	/**
	 * Retrieves master data using the MasterDataRetriever by passing the entity name, locale and the result name.
	 * It then adds the SearchResults retrieved to the context. These can be referred on UI using result name.
	 * This method loads master data for your settings i.e Gender,Language,Marital Status
	 * @param context
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private void loadMasterDataForYourSettings(Context context) throws ApplicationException,SystemException{
		short localeId=context.getUserContext().getLocaleId();
		Object values[]= new Object[2];
		values[0]="";
		values[1]=PersonnelConstants.PERSONNEL;
		
		SearchResults obj=getMasterDataRetriever().retrieveMasterData(MasterConstants.GENDER ,localeId ,PersonnelConstants.GENDER_LIST);
		if(obj!=null)
			context.addAttribute(obj);
		else{
			values[0]=PersonnelConstants.GENDER;
			throw new PersonnelException(PersonnelConstants.ERROR_MASTERDATA,values);
		}
			
		
		obj=getMasterDataRetriever().retrieveMasterData(MasterConstants.MARITAL_STATUS ,localeId ,PersonnelConstants.MARITAL_STATUS_LIST);
		if(obj!=null)
			context.addAttribute(obj);
		else{
			values[0]=PersonnelConstants.MARITAL_STATUS;
			throw new PersonnelException(PersonnelConstants.ERROR_MASTERDATA,values);
		}
		
		List<SupportedLocales> supportedLocales=this.getPersonnelDAO().getSupportedLocales();
		obj=getMasterDataRetriever().retrieveMasterData(MasterConstants.LANGUAGE ,localeId ,PersonnelConstants.LANGUAGE_LIST,"org.mifos.application.master.util.valueobjects.Language","languageId");
		if(obj!=null && obj.getValue()!=null && supportedLocales!=null && supportedLocales.size()>0){
			EntityMaster em = (EntityMaster)obj.getValue();
			List<LookUpMaster> lookUpMaster = em.getLookUpMaster();
			if(lookUpMaster!=null){
				for(int i=0;i<lookUpMaster.size();i++){
					LookUpMaster master =lookUpMaster.get(i);
					for(int j=0;j<supportedLocales.size();j++){
						if(master.getId().shortValue()==supportedLocales.get(j).getLanguage().getLanguageId().shortValue()){
							supportedLocales.get(j).getLanguage().setLanguageName(master.getLookUpValue());
						}
					}
				}
			}
			context.addAttribute(new PersonnelHelper().getResultObject(PersonnelConstants.LANGUAGE_LIST,supportedLocales));			
		}
		else{
			values[0]=PersonnelConstants.LANGUAGE;
			throw new PersonnelException(PersonnelConstants.ERROR_MASTERDATA,values);
		}
	}
	
	/**
	 * This method loads the master data required for edit user page to display.
	 * It retrieves the master data using MasterDataRetriever and puts them into the context object.
	 * @param context instance of Context
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	/*public void manageInitial(Context context) throws ApplicationException,SystemException{
		try{
			logger.debug("in method manageInitial() of Personnel Module");
			Personnel personnel = (Personnel)context.getValueObject();
			
			//load Status list
			short localeId=context.getUserContext().getLocaleId();
			context.addAttribute(getMasterDataRetriever().retrieveMasterData(MasterConstants.PERSONNEL_STATUS ,localeId ,PersonnelConstants.STATUS_LIST,"org.mifos.application.personnel.util.valueobjects.PersonnelStatus","personnelStatusId"));

			//set dojmfi  and dob in personnel
			personnel.setDateOfJoiningMFI(personnel.getPersonnelDetails().getDateOfJoiningMFI());
			personnel.setDob(personnel.getPersonnelDetails().getDob());
			
			//load roles
			List<Role> roleList=new RolesandPermissionDAO().getRoles();
			context.addAttribute(new PersonnelHelper().getResultObject(PersonnelConstants.ROLES_LIST,roleList));
			List<Role> personnelRoleList=new PersonnelHelper().getPersonnelRolesList(roleList,personnel.getPersonnelRolesSet());
			context.addAttribute(new PersonnelHelper().getResultObject(PersonnelConstants.PERSONNEL_ROLES_LIST,personnelRoleList));
			
			//getOfficeList
			context.addAttribute(getOfficeList(context.getUserContext().getBranchId()));
			
		}catch(SystemException se){
			throw se;
		}
		catch(ApplicationException ae){
			throw ae;
		}catch(Exception e ){
			throw new PersonnelException(PersonnelConstants.UNKNOWN_EXCEPTION,e);
		}
		
	}*/
	
	/**
	 * This method loads the the list of offices to which current personnel can be transferred.
	 * It loads offices based on datascope of the user.
	 * @param officeId, office id of the logged in user
	 * @return search result object containing offices list as per the datascope of the logged in user
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private SearchResults getOfficeList(short officeId)throws ApplicationException,SystemException{
		//load branch offices
		Office loggedInUserOffice=getOffice(officeId);
		return getPersonnelDAO().getOffices(OfficeConstants.ACTIVE,loggedInUserOffice.getSearchId());

	}
	
	/**
	 * This method returns a list of changelogs for personnel
	 * @param context instance of Context
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public void getUserChangeLog(Context context)throws ApplicationException,SystemException{
		/*try{
			logger.debug("in method getUserChangeLog() of Personnel Module");
			Personnel personnel = (Personnel)context.getValueObject();
			//List changeLogList= new ClosedAccSearchDAO().getClientChangeLog(new Integer(personnel.getPersonnelId()),PersonnelConstants.PERSONNEL_ENTITY_TYPE);
			//context.addAttribute(new PersonnelHelper().getResultObject(PersonnelConstants.USER_CHANGE_LOG_LIST,changeLogList));
		}catch(SystemException se){
			throw se;
		}catch(ApplicationException ae){
			throw new PersonnelException(PersonnelConstants.USER_CHANGE_LOG_ERROR,ae);
		}catch(Exception e ){
			throw new PersonnelException(PersonnelConstants.USER_CHANGE_LOG_ERROR,e);
		}*/
	}
	/**
	 * This is the helper method that returns the office of passed in office id
	 * @param officeId id of the office to be returned 
	 * @return instance of Office  
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private Office getOffice(short officeId)throws ApplicationException,SystemException{
		return getOfficeDAO().getOffice(officeId);
	}
	
	/**
	 * This method sets the data required for the preview page.
	 * @param context instance of Context
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	/*public void previewInitial(Context context) throws ApplicationException,SystemException{
		try{
			logger.debug("in method previewInitial() of Personnel Module");
			String fromPage=(String)context.getBusinessResults().get(PersonnelConstants.INPUT_PAGE);
			Personnel personnel = (Personnel)context.getValueObject();
			if(fromPage.equals(PersonnelConstants.CREATE_USER)){
				personnel = (Personnel)context.getValueObject();
				//set date of joining branch
				personnel.getPersonnelDetails().setDateOfJoiningBranch(new Date(new java.util.Date().getTime()));
			}
			String[] personnelRolesIds = (String[])context.getBusinessResults(PersonnelConstants.PERSONNEL_ROLES_LIST);
			SearchResults sr = context.getSearchResultBasedOnName(PersonnelConstants.ROLES_LIST);
			if(sr!=null){
				//add roles to context
				List<Role> rolesList= new PersonnelHelper().getSelectedRoles((List<Role>)sr.getValue(),personnelRolesIds);
				context.addAttribute(new PersonnelHelper().getResultObject(PersonnelConstants.PERSONNEL_ROLES_LIST,rolesList));
				//add personnel roles to valueobject
				Set<PersonnelRole> personnelRoles = new PersonnelHelper().getPersonnelRoles((List<Role>)sr.getValue(),personnelRolesIds);
				personnel.setPersonnelRolesSet(personnelRoles);
			}
		}catch(Exception e ){
			throw new PersonnelException(PersonnelConstants.UNKNOWN_EXCEPTION,e);
		}
	}*/
	
	/**
	 * This is called just before the create method in the business processor.
	 * It calls several business logic methods like
	 * checkforduplicateuser,generateId,check if the office is still active 
	 * because in multiuser scenario that might have been de-activated by some other user 
	 * same is the case with roles.It might also need to check if the branch at which the loan officer is created 
	 * is still a branch office because hierarchy might be changed by some other concurrent user.
	 * It also has to set certain values in the valueobject like 
	 * systemid,status etc.
	 * @param context instance of context
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public void createInitial(Context context) throws ApplicationException,SystemException{
		try{
			logger.debug("in method createInitial() of Personnel Module");
			Personnel personnel =(Personnel)context.getValueObject();
			//check if user already exists in the system
			checkForDuplicacy(personnel);
			//Bug#26502  fixed : exception added for if branch is inactive while creating personnel.
			//check if branch is still active
			if(!getPersonnelDAO().isBranchActive(((Personnel)context.getValueObject()).getOffice().getOfficeId())){
				Object values[]=new Object[1];
				values[0]=personnel.getOffice().getOfficeId();
				throw new ApplicationException(PersonnelConstants.INACTIVE_BRANCH,values);
			}
			//fill data for create in valueobject
			fillDataForCreate(context);
		}catch(SystemException se ){
			throw se;
		}catch(ApplicationException ae ){
			throw ae;
		}catch(Exception e ){
			throw new PersonnelException(PersonnelConstants.UNKNOWN_EXCEPTION,e);
		}
	}
	
	/**
	 * This method check if user record already exist. It first checks for the login name.
	 * If login Name already exists it throws DuplicateUserException.
	 * If login name is not duplicate, it checks for govtid if present, otherwise name and dob.
	 * @param personnel
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private void checkForDuplicacy(Personnel personnel)throws SystemException,ApplicationException{
		try{
			logger.debug("in method checkForDuplicacy() of Personnel Module");
			PersonnelDAO personnelDAO = this.getPersonnelDAO();
			//check if login name exists
			checkForDuplicateUser(personnel.getUserName());
			
			//check for duplicate user if government id is present
			if(!PersonnelHelper.isNullOrBlank(personnel.getPersonnelDetails().getGovernmentIdNumber())){
				if(personnelDAO.isUserExist(personnel.getPersonnelDetails().getGovernmentIdNumber())){
					Object[] values = new Object[1];
					values[0] = personnel.getPersonnelDetails().getGovernmentIdNumber();
					throw new DuplicateUserException(PersonnelConstants.DUPLICATE_GOVT_ID,values);
				}
			}
			else{
				//set personnel display name. It is concatenatation of first, middle, second last and last name
				personnel.setDisplayName(new PersonnelHelper().getDisplayName(personnel.getPersonnelDetails()));
				if(personnelDAO.isUserExist(personnel.getDisplayName(),personnel.getDob()) == true){
					Object[] values = new Object[1];
					values[0] = personnel.getDisplayName();
					throw new DuplicateUserException(PersonnelConstants.DUPLICATE_USER_NAME_OR_DOB,values);
				}
			}
		}
		catch(SystemException se){
			throw se;
		}
		catch(ApplicationException ae){
			throw ae;
		}
		catch(Exception e){
			throw new PersonnelException(PersonnelConstants.UNKNOWN_EXCEPTION , e);
		}
		
	}
	
	/**
	 * This method is helper method that fills data in personnel before saving it to database.
	 * e.g. date of joining branch,personnel status etc. 
	 * @param context instance of Context
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private void fillDataForCreate(Context context)throws ApplicationException,SystemException{
		logger.debug("in method fillDataForCreate() of Personnel Module");
		Personnel personnel=(Personnel)context.getValueObject();
		
		//set created by and created date in personnel object
		personnel.setCreatedBy(context.getUserContext().getId());
		personnel.setCreatedDate(new Date(new java.util.Date().getTime()));
		
		//set personnel display name. It is concatenatation of first, middle, second last and last name
		personnel.setDisplayName(new PersonnelHelper().getDisplayName(personnel.getPersonnelDetails()));
		
		Office office = (Office)context.getSearchResultBasedOnName(PersonnelConstants.PERSONNEL_OFFICE).getValue();
		personnel.setOffice(office);
		
		personnel.setPersonnelStatus(PersonnelConstants.ACTIVE);
		
		PersonnelDetails personnelDetails = personnel.getPersonnelDetails();
		
		//if title is not set make it null, since it is foreign key reference
		if(personnel.getTitle()!=null && personnel.getTitle().intValue()==0)
			personnel.setTitle(null);

		//set encripted password
		personnel.setEncriptedPassword(getEncryptedPassword(personnel.getPassword()));
		
		//set passowrd changed to No
		personnel.setPasswordChanged(Constants.NO);
		
		//set locked to No
		personnel.setLocked(Constants.NO);
		
		//set no of tries to 0
		personnel.setNoOfTries(Constants.NO);
		
		//set date of birth to personnel details object
		personnelDetails.setDob(personnel.getDob());
		
		//set date of joining branch
		personnelDetails.setDateOfJoiningMFI(personnel.getDateOfJoiningMFI());
		
		//check for prefeered locale
		SetPreferredLocale(personnel);
	}
	
	
	/**
	 * This method checks if the login name for the user already exists in the system.
	 * This is done by calling a isUserNameExists() method on the Personnel DAO
	 * @param userName user login name
	 * @throws ApplicationException
	 * @throws SystemException
	 */
    private void checkForDuplicateUser(String userName)throws ApplicationException,SystemException{
		try{
			logger.debug("in method checkForDuplicateUser() of Personnel Module");
			boolean isDuplicate=getPersonnelDAO().isUserNameExists(userName);
			logger.debug("isDuplicate: "+ isDuplicate);
			if (isDuplicate){
				Object values[]=new Object[1];
				values[0]=userName;
				throw new DuplicateUserException(PersonnelConstants.DUPLICATE_USER,values);
			}
		}catch(SystemException se){
			throw se;
		}catch(ApplicationException ae){
			throw ae;
		}catch(Exception e ){
			throw new PersonnelException(PersonnelConstants.UNKNOWN_EXCEPTION , e);
		}
    }
    
	/**
	 * This method gets encrypted password of the user to store in the database.
	 * It passed the password that user has entered in UI.
	 * @param password to be encrypted
	 * @return Encrypted New Password.
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private byte[] getEncryptedPassword(String password) throws ApplicationException, SystemException {
		byte[] encryptedPassword = null;
		try {
			logger.debug("in method getEncryptedPassword() of Personnel Module");
			encryptedPassword = EncryptionService.getInstance().createEncryptedPassword(password);
			logger.debug("got Encripted Password from EncryptionService");
			
		} catch (EncryptionException e) {
			throw e;
		} catch(Exception e) {
			throw new PersonnelException(PersonnelConstants.UNKNOWN_EXCEPTION , e);
		}
		
		return encryptedPassword;
	}
	
	
	/**
	 * This method is called to handle the business logic to unlock a user account.
	 * @param context instance of context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void unLockUserAccount(Context context)throws SystemException,ApplicationException{
		try{
			logger.debug("in method unLockUserAccount() of Personnel Module");
			Personnel personnel = (Personnel)context.getValueObject();
			
			//set user to unlocked
			personnel.setLocked(Constants.NO);
			
			//reset number of tries to zero
			personnel.setNoOfTries(new Short("0"));
			
			//set updated by and updated date in personnel object
			personnel.setUpdatedBy(context.getUserContext().getId());
			personnel.setUpdatedDate(new Date(new java.util.Date().getTime()));
			
			//update user
			logger.info("Ublocking account of the personnel with id: "+ personnel.getPersonnelId());
			getPersonnelDAO().updateUser(context);
			logger.info("personnel with id: "+ personnel.getPersonnelId()+" successfully unlocked");
		}catch(SystemException se){
			throw se;
		}catch(ApplicationException ae){
			throw ae;
		}catch(Exception e){
			throw new PersonnelException(PersonnelConstants.UNKNOWN_EXCEPTION , e);
		}
	}
	
	
	/**
	 * This will retrieve the personnel object based on the systemId. 
	 * @param context instance of context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	/*public void get(Context context)throws SystemException,ApplicationException {
		try{
			String systemId =((Personnel)context.getValueObject()).getGlobalPersonnelNum();
			logger.info("retrieving Personnel details with system id: "+systemId);
			Personnel personnel = getPersonnelDAO().getUser(systemId);
			if (personnel==null){
				Object values[]=new Object[1];
				values[0]=systemId;
				throw new UserNotFoundException(PersonnelConstants.USER_NOT_FOUND,values);
			}
			context.setValueObject(personnel);
			
			//retrieve master data to show on details page
			loadMasterData(context);
			
			//sets the language name 
			setLanguageName(context);
			
			//load notes
			context.addAttribute(new PersonnelHelper().getResultObject(PersonnelConstants.NOTES, getPersonnelNotesDAO().getLatestNotesByCount(PersonnelConstants.NOTES_COUNT,personnel.getPersonnelId())));
			
			//loan name for the current status
			context.addAttribute(new PersonnelHelper().getResultObject(PersonnelConstants.CURRENT_STATUS,  new PersonnelHelper().getStatusName(context.getUserContext().getLocaleId(),personnel.getPersonnelStatus())));
		}catch(SystemException se){
			throw se;
		}catch(ApplicationException ae){
			throw ae;
		}catch(Exception e ){
			throw new PersonnelException(PersonnelConstants.UNKNOWN_EXCEPTION,e);
		}
	}*/
	
	/**
	 * This will set the language name as per locale in the personnel value object stored in the context
	 * @param context instance of context
	 */
	private void setLanguageName(Context context){
		Personnel personnel =(Personnel)context.getValueObject();
		if(null!=personnel.getPreferredLocale()&& null!=personnel.getPreferredLocale().getLanguage()&& null!=personnel.getPreferredLocale().getLanguage().getLanguageId()){
			List<SupportedLocales> languageList=(List)context.getSearchResultBasedOnName(PersonnelConstants.LANGUAGE_LIST).getValue();
			for(int i=0;i<languageList.size();i++){
				if(languageList.get(i).getLanguage().getLanguageId().shortValue()==personnel.getPreferredLocale().getLanguage().getLanguageId().shortValue()){
					personnel.getPreferredLocale().getLanguage().setLanguageName(languageList.get(i).getLanguage().getLanguageName());
				}
			}
		}
	}
	
	/**
	 * This will retrieve the personnel object based on logged in user id.
	 * @param context instance of context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void getDetails(Context context)throws SystemException,ApplicationException {
		try{
			
			logger.info("retriving Personnel details for logged in user with id: "+ context.getUserContext().getId());
			Personnel personnel = getPersonnelDAO().getUser(context.getUserContext().getId());
			if (personnel==null){
				Object values[]=new Object[1];
				values[0]=context.getUserContext().getId();
				throw new UserNotFoundException(PersonnelConstants.USER_NOT_FOUND,values);
			}
			//set dob in personnel 
			personnel.setDob(personnel.getPersonnelDetails().getDob());
			context.setValueObject(personnel);

			//retrieve master data to show on details page
			loadMasterDataForYourSettings(context);
			setLanguageName(context);
		}catch(SystemException se){
			throw se;
		}catch(ApplicationException ae){
			throw ae;
		}catch(Exception e ){
			throw new PersonnelException(PersonnelConstants.UNKNOWN_EXCEPTION,e);
		}
	}
	
	/**
	 * This is called just before the update method in the business processor 
	 * and performs business logic validation which are required before a personnel 
	 * can be updated.The validations that it needs to perform are
	 * isUserHierarchyChangeIsAcceptable
	 * isStatusChangeAcceptable
	 * isTransferAllowed
	 * isBranchActive
	 * @param context an instance of the context
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	/*public void updateInitial(Context context)throws ApplicationException,SystemException{
		logger.debug("in method updateInitial() of Personnel Module");
		Personnel oldPersonnel = getPersonnelDAO().getUser(((Personnel)context.getValueObject()).getPersonnelId());
		validateForUpdate(oldPersonnel, context);
		fillDataForUpdate(oldPersonnel, context);
		setNewRolesSet(oldPersonnel,context);
	}*/
	
	/**
	 * This method is called when logged in user wants to update his Settings.
	 * @param context an instance of the context
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public void updateSettings(Context context)throws ApplicationException,SystemException{
		logger.debug("in method updateSettings() of Personnel Module");
		Personnel oldPersonnel = getPersonnelDAO().getUser(((Personnel)context.getValueObject()).getPersonnelId());
		Personnel personnel=(Personnel)context.getValueObject();
		
		fillDataForUpdateSettings(oldPersonnel, personnel,context.getUserContext().getId());
		
		//update user
		logger.info("updating settings for the personnel with id: "+ oldPersonnel.getPersonnelId());
		context.setValueObject(oldPersonnel);
		getPersonnelDAO().updateUser(context);
		logger.info("personnel with id: "+ oldPersonnel.getPersonnelId()+" successfully updated");
	}
	
	/**
	 * This method is helper method that fills data in personnel before updating it to database.
	 * @param oldPersonnel instace of the oldPersonnel
	 * @param context instance of Context
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private void fillDataForUpdateSettings(Personnel oldPersonnel, Personnel personnel,Short userId){
		PersonnelDetails oldPersonnelDetails=oldPersonnel.getPersonnelDetails();
		PersonnelDetails personnelDetails=personnel.getPersonnelDetails();
		//user name
		oldPersonnelDetails.setFirstName(personnelDetails.getFirstName());
		oldPersonnelDetails.setMiddleName(personnelDetails.getMiddleName());
		oldPersonnelDetails.setSecondLastName(personnelDetails.getSecondLastName());
		oldPersonnelDetails.setLastName(personnelDetails.getLastName());
		oldPersonnelDetails.setMaritalStatus(personnelDetails.getMaritalStatus());
		oldPersonnelDetails.setGender(personnelDetails.getGender());
		
		//set personnel display name. It is concatenatation of first, middle, second last and last name
		oldPersonnel.setDisplayName(new PersonnelHelper().getDisplayName(personnelDetails));
		
		//user address details
		oldPersonnelDetails.setAddress1(personnelDetails.getAddress1());
		oldPersonnelDetails.setAddress2(personnelDetails.getAddress2());
		oldPersonnelDetails.setAddress3(personnelDetails.getAddress3());
		oldPersonnelDetails.setCity(personnelDetails.getCity());
		oldPersonnelDetails.setState(personnelDetails.getState());
		oldPersonnelDetails.setCountry(personnelDetails.getCountry());
		oldPersonnelDetails.setPostalCode(personnelDetails.getPostalCode());
		oldPersonnelDetails.setTelephone(personnelDetails.getTelephone());
		oldPersonnel.setPreferredLocale(personnel.getPreferredLocale());
		oldPersonnel.setEmailId(personnel.getEmailId());
		SetPreferredLocale(oldPersonnel);
		//set updated by and updated date in personnel object
		oldPersonnel.setUpdatedBy(userId);
		oldPersonnel.setUpdatedDate(new PersonnelHelper().getCurrentDate());
		
		//set version of personnel
		oldPersonnel.setVersionNo(personnel.getVersionNo());
	}
	
	/**
	 * This method is helper method that fills data in personnel before updating it to database.
	 * @param oldPersonnel instace of the oldPersonnel
	 * @param context instance of Context
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private void fillDataForUpdate(Personnel oldPersonnel,Context context)throws ApplicationException,SystemException{
		logger.debug("in method fillDataForUpdate() of Personnel Module");
		//get personnel object from context
		Personnel personnel=(Personnel)context.getValueObject();
		
		//if title is not set make it null, since it is foreign key reference
		if(personnel.getTitle()!=null && personnel.getTitle().intValue()==0)
			personnel.setTitle(null);
		
		//set updated by and updated date in personnel object
		personnel.setUpdatedBy(context.getUserContext().getId());
		personnel.setUpdatedDate(new PersonnelHelper().getCurrentDate());
		
		//if user password has been changed , encrypt it to store in the database, also reset locked and no of tries
		if(!PersonnelHelper.isNullOrBlank(personnel.getPassword())){
			//set encripted password
			personnel.setEncriptedPassword(getEncryptedPassword(personnel.getPassword()));
		}else
			personnel.setEncriptedPassword(oldPersonnel.getEncriptedPassword());
		
		//set personnel display name. It is concatenatation of first, middle, second last and last name
		personnel.setDisplayName(new PersonnelHelper().getDisplayName(personnel.getPersonnelDetails()));
		PersonnelDetails personnelDetails = personnel.getPersonnelDetails();
		PersonnelDetails oldPersonnelDetails = oldPersonnel.getPersonnelDetails();
		personnelDetails.setDob(oldPersonnelDetails.getDob());
		personnelDetails.setDateOfJoiningBranch(oldPersonnelDetails.getDateOfJoiningBranch());
		personnelDetails.setDateOfLeavingBranch(oldPersonnelDetails.getDateOfLeavingBranch());
		personnelDetails.setPersonnelId(oldPersonnelDetails.getPersonnelId());
		personnelDetails.setDateOfJoiningMFI(personnel.getDateOfJoiningMFI());
		
		//	check for preferred locale
		SetPreferredLocale(personnel);
	}
	
	private void SetPreferredLocale(Personnel personnel){
		if(personnel.getPreferredLocale()==null || personnel.getPreferredLocale().getLocaleId()==null){
			SupportedLocales lc = new SupportedLocales();
			lc.setLocaleId(Configuration.getInstance().getSystemConfig().getMFILocaleId());
			personnel.setPreferredLocale(lc);
		}
	}
	/**
	 * This method is helper method that gets called before updating personnel to check for roles.
	 * It check personnel old roles and new roles, if old roles are not present in new role list, those are 
	 * added to separate list preapared for roles to be removed while updating personnel. 
	 * @param oldPersonnel personnel object before updating
 	 * @param context instance of Context
	 */
	/*private void setNewRolesSet(Personnel oldPersonnel,Context context){
		logger.debug("in method setNewRolesSet() of Personnel Module");
		Personnel personnel=(Personnel)context.getValueObject();
		List<PersonnelRole> rolesToDelete=null;
		if(oldPersonnel.getPersonnelRolesSet()!=null){
			Iterator oldRolesSet=oldPersonnel.getPersonnelRolesSet().iterator();
			if(personnel.getPersonnelRolesSet()!=null){
				PersonnelRole oldRole=null;
				PersonnelRole newRole=null;
				while(oldRolesSet.hasNext()){
					oldRole=(PersonnelRole)oldRolesSet.next();
					Iterator newRolesSet=personnel.getPersonnelRolesSet().iterator();
					boolean roleFound=false;
					while(newRolesSet.hasNext()){
						newRole=(PersonnelRole)newRolesSet.next();
						if(newRole.getRole().getId().shortValue()==oldRole.getRole().getId().shortValue()){
							newRole.setPersonnelRoleId(oldRole.getPersonnelRoleId());
							roleFound=true;
							break;
						}
					}
					if(!roleFound){
						if(rolesToDelete==null)
							rolesToDelete=new ArrayList<PersonnelRole>();
						rolesToDelete.add(oldRole);
					}
				}
			}else
				rolesToDelete=new ArrayList<PersonnelRole>(oldPersonnel.getPersonnelRolesSet());
		}
		context.addBusinessResults(PersonnelConstants.ROLES_TO_DELETE,rolesToDelete);
	}*/
	
	/**
	 * This is called to check whehter personnel can be updated or not.
	 * Itperforms business logic validation which are required before a personnel can be updated.
	 * @param oldPersonnel personnel object before updating
 	 * @param context instance of Context
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private void validateForUpdate(Personnel oldPersonnel,Context context)throws ApplicationException,SystemException{
		Personnel personnel=(Personnel)context.getValueObject();
		logger.debug("in method validateForUpdate() of Personnel Module");
		//	validate if user hierarchy is changed
		if(!isUserHierarchyChangeIsAcceptable(context,oldPersonnel.getLevel().getLevelId())){
			Object values[]=new Object[1];
			values[0]=personnel.getGlobalPersonnelNum();
			throw new HierarchyChangeException(PersonnelConstants.HIERARCHY_CHANGE_EXCEPTION,values);
		}
		
		boolean isLoanOfficer=false;
		if (personnel.getLevel().getLevelId().shortValue()==PersonnelConstants.LOAN_OFFICER.shortValue())
			isLoanOfficer=true;
		
		//if user office has not been changed
		if(personnel.getOffice().getOfficeId().shortValue()==oldPersonnel.getOffice().getOfficeId().shortValue()){
			personnel.setOffice(oldPersonnel.getOffice());
			context.addBusinessResults(PersonnelConstants.TRANSFER,Constants.NO);
		}
		else{
			//validate if user office is changed
			if (!isTransferAllowed(context,oldPersonnel.getOffice().getOfficeId(),isLoanOfficer)){
				Object values[]=new Object[1];
				values[0]=personnel.getGlobalPersonnelNum();
				throw new TransferNotPossibleException(PersonnelConstants.TRANSFER_NOT_POSSIBLE_EXCEPTION,values);
			}else{ //if user transfer is allowed, set a flag
				context.addBusinessResults(PersonnelConstants.TRANSFER,Constants.YES);
			}
		}
		
		//validate if user status is changed
		if(!isStatusChangeAcceptable(oldPersonnel.getPersonnelStatus(),personnel.getPersonnelStatus(),personnel.getPersonnelId(),personnel.getOffice().getOfficeId(),isLoanOfficer)){
			Object values[]=new Object[1];
			values[0]=personnel.getGlobalPersonnelNum();
			throw new StatusChangeException(PersonnelConstants.STATUS_CHANGE_EXCEPTION,values);
		}
		//validate if user branch is still active
		if(!isBranchActive(oldPersonnel.getPersonnelStatus(),personnel.getPersonnelStatus(),personnel.getOffice().getOfficeId())){
			Object values[]=new Object[1];
			values[0]=personnel.getOffice().getOfficeId();
			throw new ApplicationException(PersonnelConstants.INACTIVE_BRANCH,values);
		}
				
	}
	
	/**
	 * This is called to check if personnel hierararchy can be changed or not
	 * @param context instance of context
 	 * @param oldLevel personnel old level
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private boolean isUserHierarchyChangeIsAcceptable(Context context,Short oldLevel)throws ApplicationException,SystemException{
		Personnel personnel =(Personnel)context.getValueObject();
		
		if(oldLevel.equals(PersonnelConstants.LOAN_OFFICER.shortValue()) && personnel.getLevel().getLevelId().equals(PersonnelConstants.NON_LOAN_OFFICER)){
			if(getPersonnelDAO().getCustomerCountWithLO(personnel.getPersonnelId(),personnel.getOffice().getOfficeId())>0)
				return false;
		}else if(oldLevel.equals(PersonnelConstants.NON_LOAN_OFFICER.shortValue())&& personnel.getLevel().getLevelId().equals(PersonnelConstants.LOAN_OFFICER)){
			//throw an error if office assigned is not branch office
			if(chkForBranchOffice(context)){
				Object values[]=new Object[1];
				values[0]=personnel.getGlobalPersonnelNum();
				throw new HierarchyChangeException(PersonnelConstants.LO_ONLY_IN_BRANCHES,values);
			}
		}
		return true;
	}
	
	/**
	 * This is called to check if personnel status can be changed or not
	 * @param oldStatus personnel old status
 	 * @param newStatus personnel new status
 	 * @param userId id of the personnel to be updated
 	 * @param officeId office of the personnel
 	 * @param isLoanOfficer tells whether user to be updated is loan officer or not.
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private boolean isStatusChangeAcceptable(short oldStatus, short newStatus, short userId,short officeId,boolean isLoanOfficer)throws ApplicationException,SystemException {
		if(oldStatus==PersonnelConstants.ACTIVE && newStatus==PersonnelConstants.INACTIVE && isLoanOfficer)
			return checkLOCustomers(userId,officeId);
		return true;
	}
	
	/**
	 * This is called to check whether branch of the personnel is active or not
	 * @param oldStatus personnel old status
 	 * @param newStatus personnel new status
 	 * @param officeId offce id of ther personnel
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private boolean isBranchActive(short oldStatus, short newStatus,short officeId)throws ApplicationException,SystemException {
		 if(oldStatus==PersonnelConstants.INACTIVE && newStatus==PersonnelConstants.ACTIVE)
			return getPersonnelDAO().isBranchActive(officeId);
		return true;
	}
	
	/**
	 * This is called to check if personnel can be transferred across different branch
	 * @param context instance of context
 	 * @param oldBranchId personnel old branch
 	 * @param isLoanOfficer tells whether user to be updated is loan officer or not.
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private boolean isTransferAllowed(Context context,Short oldBranchId, boolean isLoanOfficer)throws ApplicationException,SystemException {
		Personnel personnel = (Personnel)context.getValueObject();
		if(isLoanOfficer){
			if(chkForBranchOffice(context)){
				Object values[]=new Object[1];
				values[0]=personnel.getGlobalPersonnelNum();
				throw new TransferNotPossibleException(PersonnelConstants.LO_ONLY_IN_BRANCHES,values);
			}
			return checkLOCustomers(personnel.getPersonnelId(),oldBranchId);
		}
		
		return true;
	}
	
	/**
	 * This is called to check if personnel can be transferred across different branch
	 * @param context instance of context
 	 * @return  boolean returns true if branch office otherwise false
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private boolean chkForBranchOffice(Context context)throws ApplicationException,SystemException{
		Personnel personnel = (Personnel)context.getValueObject();
		List<OfficeMaster> officeList =(List) context.getSearchResultBasedOnName(PersonnelConstants.OFFICE_LIST).getValue();
		//if the personnel is of type loan officer, its office can only be branch office. If not throw an exception
		OfficeMaster office=null;
		if(officeList!=null){
			for(int i=0;i<officeList.size();i++){
				if(officeList.get(i).getOfficeId()==personnel.getOffice().getOfficeId().shortValue()){
					office =officeList.get(i);
					break;
				}
			}
		}
		
		//if personnel is LO and office is not branch office, throw an exception
		if(office!=null && office.getLevelId()!=OfficeConstants.BRANCHOFFICE){
			return true;
		}
			return false;
	}
	
	/**
	 * This is the helper method that check if LO has any active customer assigned.
 	 * @param userId id of the personnel to be updated
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private boolean checkLOCustomers(short userId,short officeId)throws ApplicationException,SystemException{
		if(getPersonnelDAO().getActiveCenterCount(userId,officeId)>0)
			return false;
		if(getPersonnelDAO().getActiveGroupCount(userId,officeId)>0)
			return false;
		if(getPersonnelDAO().getActiveClientCount(userId,officeId)>0)
			return false;
		return true;
	}
	
	/**
	 * This method searches for the list of users based on the data scope of logged in user. 
	 * @param context instance of context
	 * A search can also be done on the center name
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void getUserList(Context context)throws SystemException,ApplicationException {
				
		SearchDAO searchDAO=new SearchDAO();	 
		String searchString = context.getSearchObject().getFromSearchNodeMap("searchString");
		String searchType=PersonnelConstants.USER_LIST;		
		String officeSearchId=getOffice(context.getUserContext().getBranchId()).getSearchId();
		context.setSearchResult(searchDAO.search(searchType,searchString,context.getUserContext().getLevelId(),officeSearchId,context.getUserContext().getId(),null));
	}
	
	/**
	 * This method is used to retrieve MasterDataRetriver instance
	 * @return instance of MasterDataRetriever
	 * @throws HibernateProcessException
	 */	
	public MasterDataRetriever getMasterDataRetriever()throws HibernateProcessException{
		return new MasterDataRetriever();
	}
	
	/** 
    * This method returns instance of PersonnelDAO
    * @return PersonnelDAO instance
    * @throws SystemException
    */  
 	private PersonnelDAO getPersonnelDAO()throws SystemException{
 		PersonnelDAO personnelDAO=null;
 		try{
 			personnelDAO = (PersonnelDAO)getDAO(PathConstants.PERSONNEL_PATH);
 		}catch(ResourceNotCreatedException rnce){
 			throw rnce;
 		}
 		return personnelDAO;
 	}
 	/** 
     * This method returns instance of PersonnelNotesDAO
     * @return PersonnelNotesDAO instance
     * @throws SystemException
     */  
  	private PersonnelNotesDAO getPersonnelNotesDAO()throws SystemException{
  		PersonnelNotesDAO personnelNotesDAO=null;
  		try{
  			personnelNotesDAO = (PersonnelNotesDAO)getDAO(PathConstants.PERSONNEL_NOTES_PATH);
  		}catch(ResourceNotCreatedException rnce){
  			throw rnce;
  		}
  		return personnelNotesDAO;
  	}
  	/** 
     * This method returns instance of OfficeDAO
     * @return OfficeDAO instance
     * @throws SystemException
     */  
  	private OfficeDAO getOfficeDAO()throws SystemException{
  		return new OfficeDAO();
  	}
}
