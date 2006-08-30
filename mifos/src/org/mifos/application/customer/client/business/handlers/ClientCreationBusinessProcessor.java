/**

 * ClientCreationBusinessProcessor.java    version: xxx



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

package org.mifos.application.customer.client.business.handlers;

import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.exception.DuplicateCustomerException;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.customer.client.dao.ClientCreationDAO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.client.util.valueobjects.Client;
import org.mifos.application.customer.client.util.valueobjects.CustomerPicture;
import org.mifos.application.customer.dao.CustomerUtilDAO;
import org.mifos.application.customer.exceptions.AssociatedObjectStaleException;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.exceptions.CustomerNotFoundException;
import org.mifos.application.customer.exceptions.DuplicateCustomerGovtIdException;
import org.mifos.application.customer.group.dao.GroupDAO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.group.util.helpers.LinkParameters;
import org.mifos.application.customer.group.util.valueobjects.Group;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerFlag;
import org.mifos.application.customer.util.valueobjects.CustomerNameDetail;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.util.valueobjects.MeetingType;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.dao.helpers.MasterDataRetriever;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ResourceNotCreatedException;
import org.mifos.framework.exceptions.SecurityException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This class acts as a BusinessProcessor to create/update a client.
 */
public class ClientCreationBusinessProcessor extends MifosBusinessProcessor {

	/**An insatnce of the logger which is used to log statements */
	private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER);
	private ConfigurationIntf labelConfig=MifosConfiguration.getInstance();
	/**
	 * This checks if the client being created is part of the group.
	 * It retrieves the group related info which is to be dispalyed on the UI. The parent customer of the client is
	 * set to the group that is retrieved. The office is set to the office of the group
	 * If the user chooses not to create the client under a group, the list of loan officers under that branch is retrieved
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void loadInitial(Context context)throws SystemException,ApplicationException{
		try{
			CustomerUtilDAO customerUtilDAO=new CustomerUtilDAO();
			Client client = (Client)context.getValueObject();
			//client belongs to a group. The group details are retrieved based on the group id of the group chosen
			//from the group search
			if(client.getIsClientUnderGrp() == ClientConstants.CLIENT_BELONGS_TO_GROUP	){
				GroupDAO groupDAO = new GroupDAO();
				Group parentGroup = groupDAO.findByGroupId(client.getParentGroupId());
				//setting the client's parent customer to the groupthat was retrieved
				client.setParentCustomer(parentGroup);
				context.addAttribute(new CustomerHelper().getResultObject("ParentGroup" ,parentGroup));
				//setting the office of the client to groups office.
				client.setOffice(parentGroup.getOffice());
				if(parentGroup.getCustomerMeeting() !=null)
				 client.getCustomerMeeting().setMeeting(parentGroup.getCustomerMeeting().getMeeting());

			}
			else{
				context.addAttribute(customerUtilDAO.getLoanOfficersMaster(ClientConstants.LOAN_OFFICER_LEVEL, client.getOffice().getOfficeId(),context.getUserContext().getId(),context.getUserContext().getLevelId(), CustomerConstants.LOAN_OFFICER_LIST));
				

			}
			logger.debug("Office Id and name : "+ client.getOffice().getOfficeId() +" "+client.getOffice().getOfficeName());
			getMasterDataForLoad(context);
			context.addAttribute(customerUtilDAO.getFormedByLoanOfficersMaster(ClientConstants.LOAN_OFFICER_LEVEL, client.getOffice().getOfficeId(), CustomerConstants.FORMEDBY_LOAN_OFFICER_LIST));
			context.addAttribute(customerUtilDAO.getCustomFieldDefnMaster(CustomerConstants.CLIENT_LEVEL_ID, ClientConstants.CLIENT_ENTITY_TYPE , CustomerConstants.CUSTOM_FIELDS_LIST));
	  	    short localeId=context.getUserContext().getLocaleId();
			//short localeId=1;
			//add administrative set fees to context
	  	    List adminFeeList =(List)customerUtilDAO.getFeesMasterWithLevel(CustomerConstants.CLIENT_LEVEL_ID ,CustomerConstants.ADMIN_FEES_LIST).getValue();
			//set meetings for fees
			customerUtilDAO.setMeetingForFees(adminFeeList);
			context.addAttribute(new CustomerHelper().getResultObject(CustomerConstants.ADMIN_FEES_LIST,adminFeeList));
			//Retrives list of additional fees that can be applied to the center and adds to the context
			List additionalFeeList =(List)customerUtilDAO.getFeesMasterWithoutLevel(ClientConstants.All_CATEGORY_ID,ClientConstants.CLIENT_CATEGORY_ID , CustomerConstants.CLIENT_LEVEL_ID,CustomerConstants.FEES_LIST).getValue();
			//set meetings for fees
			new CustomerUtilDAO().setMeetingForFees(additionalFeeList);
			//add additional fees to context
			context.addAttribute(new CustomerHelper().getResultObject(CustomerConstants.FEES_LIST,additionalFeeList));
		}

		catch(SystemException se){
			throw se;
		}
		catch(ApplicationException ae){
			throw ae;
		}
		catch(Exception e){
			e.printStackTrace();
			throw new CustomerException(CenterConstants.FATAL_ERROR_EXCEPTION , e);
		}

	}

	/**
	 * This loads the master data required for load page.
	 * The master data includes
	 * 1)Salutation
	 * 2)Gender
	 * 3)MaritalStatus
	 * 4)CitizenShip
	 * 5)Ethnicity
	 * 6)EducationLevel
	 * 7)BusinessActivities
	 * 8)Handicapped
	 * 9)CustomField Definition.
	 * The following only if the client being created is part of the group.
	 * 10)Loan officer Name
	 * 11)Meeting Location
	 * 12)Meeting Schedule
	 * 13)Group Assigned
	 * 14)Center Assigned
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	private void getMasterDataForLoad(Context context)throws SystemException,ApplicationException{

		try{
			MasterDataRetriever masterDataRetriever = new MasterDataRetriever();
			short localeId=context.getUserContext().getLocaleId();
			//short localeId=1;
			context.addAttribute(masterDataRetriever.retrieveMasterData(MasterConstants.SALUTATION ,localeId ,ClientConstants.SALUTATION_ENTITY));
			context.addAttribute(masterDataRetriever.retrieveMasterData(MasterConstants.MARITAL_STATUS ,localeId ,ClientConstants.MARITAL_STATUS_ENTITY));
			context.addAttribute(masterDataRetriever.retrieveMasterData(MasterConstants.CITIZENSHIP ,localeId ,ClientConstants.CITIZENSHIP_ENTITY));
			context.addAttribute(masterDataRetriever.retrieveMasterData(MasterConstants.BUSINESS_ACTIVITIES ,localeId ,ClientConstants.BUSINESS_ACTIVITIES_ENTITY ));
			context.addAttribute(masterDataRetriever.retrieveMasterData(MasterConstants.EDUCATION_LEVEL ,localeId ,ClientConstants.EDUCATION_LEVEL_ENTITY));
			context.addAttribute(masterDataRetriever.retrieveMasterData(MasterConstants.GENDER  ,localeId ,ClientConstants.GENDER_ENTITY));
			context.addAttribute(masterDataRetriever.retrieveMasterData(MasterConstants.SPOUSE_FATHER ,localeId ,ClientConstants.SPOUSE_FATHER_ENTITY ,"org.mifos.application.master.util.valueobjects.SpouseFatherLookup" ,"spouseFatherId"));
			context.addAttribute(masterDataRetriever.retrieveMasterData(MasterConstants.HANDICAPPED ,localeId ,ClientConstants.HANDICAPPED_ENTITY));
			context.addAttribute(masterDataRetriever.retrieveMasterData(MasterConstants.ETHINICITY ,localeId ,ClientConstants.ETHINICITY_ENTITY));

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
	 * It checks for duplicate clients based on the client name and other uniqueness criteria and the state.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void createInitial(Context context)throws SystemException,ApplicationException{
		Client client = (Client)context.getValueObject();
		String name= client.getDisplayName();
		Date dateOfBirth = client.getDateOfBirth();
		String govtId = client.getGovernmentId();
		client.setOffice(new CustomerUtilDAO().getOffice(client.getOffice().getOfficeId()));
		//setting customer id to 0 since customer being created.
		Integer customerId = Integer.valueOf("0");
		
		//security check 
		
		if(client.getPersonnelId()!=null)
			checkPermissionForCreate(client.getStatusId(),context.getUserContext(),null,client.getOffice().getOfficeId(),client.getPersonnelId());
		else
			checkPermissionForCreate(client.getStatusId(),context.getUserContext(),null,client.getOffice().getOfficeId(),context.getUserContext().getId());
			
		checkForDuplicacy(name , dateOfBirth , govtId , customerId, context.getUserContext());

		//a check is made on the status of associated entities like loan officer, branch and fees chosen.
		//If any of these are	inactive an error is thrown
		checkIFAssociatedEntitiesAreActive(context);
		
		//finally add the entries to client name 
		Iterator iterator = client.getCustomerNameDetailSet().iterator();
		while( iterator.hasNext()){
			CustomerNameDetail customerNameDetail =(CustomerNameDetail) iterator.next();
			
			if ( customerNameDetail.getNameType().shortValue()==ClientConstants.CLIENT_NAME_TYPE){
				
				client.setClientName(customerNameDetail.getFirstName(),customerNameDetail.getLastName(),customerNameDetail.getSecondLastName());
			}
		}
	}

	/**
	 * Calls the corresponding method on the DAO and checks for duplicate clients.
	 * @param name
	 * @param dob
	 * @param governmentId
	 * @param userContext TODO
	 * @return
	 */
	private void checkForDuplicacy(String name, Date dob, String governmentId , Integer customerId, UserContext userContext)throws SystemException,ApplicationException{
		try{
			ClientCreationDAO clientDAO = (ClientCreationDAO)getDAO(org.mifos.application.customer.util.helpers.PathConstants.CLIENT_CREATION);

			if(!ValidateMethods.isNullOrBlank(governmentId)){
				if(clientDAO.checkForDuplicacyOnGovtId(governmentId) == true){
					Object[] values = new Object[2];
					values[0] = governmentId;
					values[1]=labelConfig.getLabel(ConfigurationConstants.GOVERNMENT_ID,userContext.getPereferedLocale());
					throw new DuplicateCustomerGovtIdException(CustomerConstants.DUPLICATE_GOVT_ID_EXCEPTION,values);
				}
			}
			else{
				if(clientDAO.checkForDuplicacyOnName(name,dob,governmentId,customerId) == true){
					Object[] values = new Object[1];
					values[0] = name;
					throw new DuplicateCustomerException(CustomerConstants.CUSTOMER_DUPLICATE_CUSTOMERNAME_EXCEPTION,values);
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
			throw new CustomerException(CenterConstants.FATAL_ERROR_EXCEPTION , e);
		}

	}





	/**
	 * It checks if the associated entities like loan officer , branch selected or fees selected are still active..
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	private void checkIFAssociatedEntitiesAreActive(Context context)throws SystemException,ApplicationException{
		boolean loanOfficerInactive = false;
		boolean branchInactive = false;
		boolean feeStatusInactive = false;
		Client client = (Client)context.getValueObject();
		List<Short> feeIds = new ArrayList();
		Set accountFeesSet = client.getCustomerAccount().getAccountFeesSet();
		if(accountFeesSet !=null){
			if(accountFeesSet.size() != 0){
				Iterator <AccountFees>it = accountFeesSet.iterator();
				while(it.hasNext()){
					feeIds.add((it.next()).getFees().getFeeId());
				}
				feeStatusInactive = new CustomerUtilDAO().isFeesStatusInactive(feeIds);
			}
		}
		short officeId = client.getOffice().getOfficeId().shortValue();
		
		//if the client is associated with a personnel then the personnel status is checked
		if( client.getPersonnel() !=null){
			Short loanOfficerId = client.getPersonnel().getPersonnelId();
			loanOfficerInactive =new CustomerUtilDAO().isLoanOfficerInactive(loanOfficerId, officeId);
		}
		if(loanOfficerInactive ==true){
			throw new AssociatedObjectStaleException(CustomerConstants.CUSTOMER_LOAN_OFFICER_INACTIVE_EXCEPTION,new Object[]{labelConfig.getLabel(ConfigurationConstants.BRANCHOFFICE,context.getUserContext().getPereferedLocale())});

		}
		//checks the status of the branch
		branchInactive = new CustomerUtilDAO().isBranchInactive(officeId);
		if(branchInactive ==true){
			throw new AssociatedObjectStaleException(CustomerConstants.CUSTOMER_BRANCH_INACTIVE_EXCEPTION,new Object[]{labelConfig.getLabel(ConfigurationConstants.BRANCHOFFICE,context.getUserContext().getPereferedLocale())});

		}
		if(feeStatusInactive ==true){
			throw new AssociatedObjectStaleException(CustomerConstants.CUSTOMER_FEE_INACTIVE_EXCEPTION);

		}

	}



	/**
	 * Calls the findByClientId method on the DAO passing the customerId of the client
	 * It also gets the Performance History and some recent notes.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */

	public void get(Context context)throws SystemException,ApplicationException{
		String systemId =CustomerConstants.BLANK;
		int clientId = 0;
		String searchString ="";
		try{

		ClientCreationDAO clientCreationDAO=(ClientCreationDAO)getDAO(context.getPath());;
		systemId = ((Client)context.getValueObject()).getGlobalCustNum();
		Client client = clientCreationDAO.findBySystemId(systemId);
		//Client client = clientCreationDAO.findByClientId(clientId);
		if (client==null){
			Object values[]=new Object[1];
			values[0]=systemId;
			throw new CustomerNotFoundException(CustomerConstants.CUSTOMER_NOT_FOUND,values);
		}
		context.setValueObject(client);
		searchString = ((Client)context.getValueObject()).getSearchId();
		getMasterDataForLoad(context);
		setSpouseOrFatherName(context);

		CustomerUtilDAO customerUtilDAO = new CustomerUtilDAO();
		CustomerHelper customerHelper = new CustomerHelper();
		short localeId=context.getUserContext().getLocaleId();
		//short localeId=1;
		
		context.addAttribute(customerUtilDAO.getCustomFieldDefnMaster(CustomerConstants.CLIENT_LEVEL_ID, ClientConstants.CLIENT_ENTITY_TYPE , CustomerConstants.CUSTOM_FIELDS_LIST));
		context.addAttribute(customerHelper.getResultObject(CustomerConstants.CURRENT_CUSTOMER_STATUS,  customerHelper.getStatusName(localeId,client.getStatusId(),CustomerConstants.CLIENT_LEVEL_ID)));
		context.addAttribute(customerHelper.getResultObject(GroupConstants.NOTES, customerHelper.getLatestNotes(GroupConstants.NOTES_COUNT,client.getCustomerId())));
		context.addBusinessResults(GroupConstants.LINK_VALUES,getLinkValues(client));
		context.addBusinessResults(CustomerConstants.TOTAL_FEE_DUE,new ClosedAccSearchDAO().getTotalClientFeeChargesDue(client.getCustomerAccount().getAccountId()));
		CustomerBusinessService custBizService = (CustomerBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Customer);
		context.addBusinessResults("loanCycleCounter", custBizService.fetchLoanCycleCounter(client.getCustomerId()));
		logger.debug("after fetching loan cycle counter for client with id = " + client.getCustomerId() );
		//TODO display of flags for the chosen status if closed or cancelled
		checkClientFlags(client,context);
		retrievePicture(context);
		context.addBusinessResults(CustomerConstants.CUSTOMERPERFORMANCE, custBizService.numberOfMeetings(true,client.getCustomerId()));
		context.addBusinessResults(CustomerConstants.CUSTOMERPERFORMANCEHISTORY, custBizService.numberOfMeetings(false,client.getCustomerId()));
		context.addBusinessResults(CustomerConstants.CUSTOMERPERFORMANCEHISTORYVIEW, custBizService.getLastLoanAmount(client.getCustomerId()));
		
		//TO display Loan nad Savings Accounts of a client
		List<LoanBO> loanAccounts = customerUtilDAO.getActiveLoanAccountsForCustomer(client.getCustomerId(),localeId);
		List<SavingsBO> savingsAcounts = customerUtilDAO.getActiveSavingsAccountsForCustomer(client.getCustomerId(),localeId);
		context.addAttribute(customerHelper.getResultObject(CustomerConstants.CUSTOMER_ACTIVE_LOAN_ACCOUNTS,loanAccounts));
		context.addAttribute(customerHelper.getResultObject(CustomerConstants.CUSTOMER_ACTIVE_SAVINGS_ACCOUNTS,savingsAcounts));
		
		context.addAttribute(customerHelper.getResultObject(CustomerConstants.CURRENT_CLIENT_NAME,client.getDisplayName()));
		context.addAttribute(customerHelper.getResultObject(CustomerConstants.CURRENT_DOB,client.getDateOfBirth()));
		context.addAttribute(customerHelper.getResultObject(CustomerConstants.CURRENT_GOVT_ID,client.getGovernmentId()));
		context.addAttribute(customerHelper.getResultObject(CustomerConstants.CUSTOMER_ACTIVE_SAVINGS_ACCOUNTS,savingsAcounts));
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
	 * Calls the findByClientId method on the DAO passing the customerId of the client
	 * It also gets the Performance History and some recent notes.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */

	public void getByGlobalCustNum(Context context)throws SystemException,ApplicationException{
		String systemId =CustomerConstants.BLANK;
		int clientId = 0;
		String searchString ="";
		try{

		ClientCreationDAO clientCreationDAO=(ClientCreationDAO)getDAO(context.getPath());;
		systemId = ((Client)context.getValueObject()).getGlobalCustNum();
		//clientId = ((Client)context.getValueObject()).getCustomerId().intValue();
		Client client = clientCreationDAO.findBySystemId(systemId);
		//Client client = clientCreationDAO.findByClientId(clientId);
		if (client==null){
			Object values[]=new Object[1];
			values[0]=systemId;
			throw new CustomerNotFoundException(CustomerConstants.CUSTOMER_NOT_FOUND,values);
		}
		context.setValueObject(client);
		searchString = ((Client)context.getValueObject()).getSearchId();
		getMasterDataForLoad(context);
		setSpouseOrFatherName(context);

		short localeId=context.getUserContext().getLocaleId();
		//short localeId=1;
		context.addAttribute(new CustomerUtilDAO().getCustomFieldDefnMaster(CustomerConstants.CLIENT_LEVEL_ID, ClientConstants.CLIENT_ENTITY_TYPE , CustomerConstants.CUSTOM_FIELDS_LIST));
		context.addAttribute(new CustomerHelper().getResultObject(CustomerConstants.CURRENT_CUSTOMER_STATUS,  new CustomerHelper().getStatusName(localeId,client.getStatusId(),CustomerConstants.CLIENT_LEVEL_ID)));
		context.addAttribute(new CustomerHelper().getResultObject(GroupConstants.NOTES, new CustomerHelper().getLatestNotes(GroupConstants.NOTES_COUNT,client.getCustomerId())));
		context.addBusinessResults(GroupConstants.LINK_VALUES,getLinkValues(client));
		//TODO display of flags for the chosen status if closed or cancelled
		checkClientFlags(client,context);
		}catch(SystemException se){
			throw se;
		}catch(ApplicationException ae){
			throw ae;
		}catch(Exception e ){
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		}
	}
	private void  checkClientFlags(Client clientVO,Context context)throws ApplicationException,SystemException{
		Set customerFlag=clientVO.getCustomerFlag();
		boolean isBlackListed=false;
		boolean isCurrentBlack=false;
		String blacklistedName=null;
		String flagName=null;
		if(null!=customerFlag){
			Object object[]=customerFlag.toArray();
			for(int i=0;i<object.length ;i++){
				short flagId=((CustomerFlag)object[i]).getFlagId();
				isCurrentBlack = new CustomerHelper().isBlacklisted(flagId);
				//TODO: change localeId
				if(!isBlackListed && isCurrentBlack){
					blacklistedName=new CustomerHelper().getFlagName(flagId,(short)1);
					isBlackListed=isCurrentBlack;
				}
				//TODO: change localeId
				if(!isCurrentBlack)
					flagName=new CustomerHelper().getFlagName(flagId,(short)1);
			}
			// set the flag name and blacklisted in context
			CustomerHelper customerHelper = new CustomerHelper();
			context.addAttribute(customerHelper.getResultObject(GroupConstants.CURRENT_FLAG,flagName));
			context.addAttribute(customerHelper.getResultObject(GroupConstants.IS_BLACKLISTED,isBlackListed));
			context.addAttribute(customerHelper.getResultObject(GroupConstants.BLACKLISTED_FLAG_NAME,blacklistedName));
		}
	}
	/**
	 * This method returns instance of LinkParameters
	 * @return group
	 */
	LinkParameters getLinkValues(Client client){
		LinkParameters linkParams = new LinkParameters();
		linkParams.setCustomerId(client.getCustomerId());
		linkParams.setCustomerName(client.getDisplayName());
		linkParams.setGlobalCustNum(client.getGlobalCustNum());
		linkParams.setCustomerOfficeId(client.getOffice().getOfficeId());
		linkParams.setCustomerOfficeName(client.getOffice().getOfficeName());
		linkParams.setLevelId(CustomerConstants.CLIENT_LEVEL_ID);
		Customer parent = client.getParentCustomer();
		if(parent!=null){
			linkParams.setCustomerParentGCNum(parent.getGlobalCustNum());
			linkParams.setCustomerParentName(parent.getDisplayName());
			if(parent.getParentCustomer() !=null){
				linkParams.setCustomerCenterGCNum(parent.getParentCustomer().getGlobalCustNum());
				linkParams.setCustomerCenterName(parent.getParentCustomer().getDisplayName());
			}

		}
		return linkParams;
	}
	private void setSpouseOrFatherName(Context context) {
		Client clientVO =(Client)context.getValueObject();
		CustomerNameDetail spouseFatherName = null;
		Set customerNames =clientVO.getCustomerNameDetailSet();
		Iterator<CustomerNameDetail> itCustomerNames = customerNames.iterator();

		while(itCustomerNames.hasNext()){
			spouseFatherName = itCustomerNames.next();
			if(spouseFatherName.getNameType().shortValue()!=ClientConstants.CLIENT_NAME_TYPE){
				context.addBusinessResults(ClientConstants.SPOUSE_FATHER_NAME_VALUE ,  spouseFatherName.getDisplayName());
				//set the spouse/father value if chosen
				context.addBusinessResults(ClientConstants.SPOUSE_FATHER_VALUE ,  spouseFatherName.getNameType());
				break;
			}
		}


	}
	/**
	 * It calls the getMasterData for Load method to get the master data
	 * as the data required in both the pages is same.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void editPersonalInfo(Context context)throws SystemException,ApplicationException{
		CustomerUtilDAO customerUtilDAO=new CustomerUtilDAO();
		context.addAttribute(customerUtilDAO.getCustomFieldDefnMaster(CustomerConstants.CLIENT_LEVEL_ID, ClientConstants.CLIENT_ENTITY_TYPE , CustomerConstants.CUSTOM_FIELDS_LIST));
	}

	/**
	 * It calls the getMasterData for next method to get the master data
	 * as the data required in both the pages is same.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void editMFIInfo(Context context)throws SystemException,ApplicationException{
		CustomerUtilDAO customerUtilDAO=new CustomerUtilDAO();
		Client client = (Client)context.getValueObject();
		if(client.getGroupFlag() != ClientConstants.CLIENT_BELONGS_TO_GROUP	){
			context.addAttribute(customerUtilDAO.getLoanOfficersMaster(ClientConstants.LOAN_OFFICER_LEVEL, client.getOffice().getOfficeId(),context.getUserContext().getId(),context.getUserContext().getLevelId(), CustomerConstants.LOAN_OFFICER_LIST));
		}

	}
	
	public boolean isDuplicacyCheckNeeded(Context context)throws SystemException,ApplicationException{
		Client client = (Client)context.getValueObject();
		String oldDisplayName = (String)context.getSearchResultBasedOnName(CustomerConstants.CURRENT_CLIENT_NAME).getValue();
		Date oldDOB = (Date)context.getSearchResultBasedOnName(CustomerConstants.CURRENT_DOB).getValue();
		String oldGovernmentId = (String)context.getSearchResultBasedOnName(CustomerConstants.CURRENT_GOVT_ID).getValue();
		String name= client.getDisplayName();
		Date dateOfBirth = client.getDateOfBirth();
		String govtId = client.getGovernmentId();
		boolean returnValue = false;
		//if both values of govt id is null then check if either name or DOB has changed. If so retrun true
		if(ValidateMethods.isNullOrBlank(oldGovernmentId) && ValidateMethods.isNullOrBlank(govtId)){
			if(!oldDisplayName.equals(name) || !oldDOB.equals(dateOfBirth)){
				returnValue = true;
			}
		}
		//if old value of govt id is null and a value is now entered for govt id or if the values of govt id are not the same return true
		else if(
				( ValidateMethods.isNullOrBlank(oldGovernmentId) && !ValidateMethods.isNullOrBlank(govtId))
				||(!ValidateMethods.isNullOrBlank(govtId) && !ValidateMethods.isNullOrBlank(oldGovernmentId)&& !oldGovernmentId.equals(govtId))	
				||(!ValidateMethods.isNullOrBlank(oldGovernmentId) && ValidateMethods.isNullOrBlank(govtId)))
		{
				returnValue =  true;
		}
		//if the values are the same return false
		else if( !ValidateMethods.isNullOrBlank(govtId) && !ValidateMethods.isNullOrBlank(oldGovernmentId)&& oldGovernmentId.equals(govtId))	
		{
			returnValue =  false;
		}
		return returnValue ;
	}

	public void updateInitial(Context context)throws SystemException,ApplicationException{
		Client client = (Client)context.getValueObject();
		String name= client.getDisplayName();
		Date dateOfBirth = client.getDateOfBirth();
		String govtId = client.getGovernmentId();
		client.setOffice(new CustomerUtilDAO().getOffice(client.getOffice().getOfficeId()));
		boolean loanOfficerInactive = false;
		boolean branchInactive = false;
		if(isDuplicacyCheckNeeded(context)){
			checkForDuplicacy(name , dateOfBirth , govtId ,client.getCustomerId(), context.getUserContext());
		}
		//a check is made on the status of associated entities like loan officer, branch chosen.
		//If any of these are	inactive an error is thrown
		short officeId = client.getOffice().getOfficeId().shortValue();
		//if the client is associated with a personnel then the personnel status is checked
		if( client.getPersonnel() !=null){
			Short loanOfficerId = client.getPersonnel().getPersonnelId();
			loanOfficerInactive =new CustomerUtilDAO().isLoanOfficerInactive(loanOfficerId, officeId);
		}
		if(loanOfficerInactive ==true){
			throw new AssociatedObjectStaleException(CustomerConstants.CUSTOMER_LOAN_OFFICER_INACTIVE_EXCEPTION,new Object[]{labelConfig.getLabel(ConfigurationConstants.BRANCHOFFICE,context.getUserContext().getPereferedLocale())});

		}
		//checks the status of the branch
		branchInactive = new CustomerUtilDAO().isBranchInactive(officeId);
		if(branchInactive ==true){
			throw new AssociatedObjectStaleException(CustomerConstants.CUSTOMER_BRANCH_INACTIVE_EXCEPTION,new Object[]{labelConfig.getLabel(ConfigurationConstants.BRANCHOFFICE,context.getUserContext().getPereferedLocale())});
		}
	}
	/**
	 * Throws ConcurrencyException.
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#update(org.mifos.framework.util.valueobjects.Context)
	 */
	public void update(Context context)throws SystemException,ApplicationException{

		try{
			//setting the updated_date and updated_by fields to the current date and current logged in user
			Client client = (Client)context.getValueObject();
			client.setUpdatedDate(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
			client.setUpdatedBy(context.getUserContext().getId());
			//finally add the entries to client name 
			Iterator iterator = client.getCustomerNameDetailSet().iterator();
			while( iterator.hasNext()){
				CustomerNameDetail customerNameDetail =(CustomerNameDetail) iterator.next();
				
				if ( customerNameDetail.getNameType().shortValue()==ClientConstants.CLIENT_NAME_TYPE){
					
					client.setClientName(customerNameDetail.getFirstName(),customerNameDetail.getLastName(),customerNameDetail.getSecondLastName());
				}
			}
			super.update(context);
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
	 * Throws ConcurrencyException.
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#update(org.mifos.framework.util.valueobjects.Context)
	 */
	public void retrievePicture(Context context)throws SystemException,ApplicationException{
		InputStream picture = null;
		try{

			ClientCreationDAO clientCreationDAO = this.getClientCreationDAO();
			Client client = (Client)context.getValueObject();

			CustomerPicture clientPicture = clientCreationDAO.retrievePicture(client.getCustomerId());
			if(clientPicture ==null || clientPicture.getPicture().length() == 0){
				context.addBusinessResults("noPictureOnGet" , "Yes");
			}
			else {
				picture = clientPicture.getPicture().getBinaryStream();
				context.addBusinessResults("noPictureOnGet" , "No");
			}
			client.setCustomerPicture(picture);
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
	   * This method returns instance of ClientCreationDAO
	   * @return ClientCreationDAO instance
	   * @throws SystemException
	   */
	private ClientCreationDAO getClientCreationDAO()throws SystemException{
		ClientCreationDAO clientTransferDAO=null;
		try{
			clientTransferDAO = (ClientCreationDAO)getDAO(org.mifos.application.customer.util.helpers.PathConstants.CLIENT_CREATION);
		}catch(ResourceNotCreatedException rnce){
			throw rnce;
		}
		return clientTransferDAO;
	}
	/**
	 * This updates the meeting of the client.
	 * @param context This includes all the parameters passed from the action to the business processor layer.
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void updateMeeting(Context context)throws SystemException,ApplicationException{

		Client client=(Client)context.getValueObject();
		try{
			if(client.getCustomerMeeting()!=null && client.getCustomerMeeting().getMeeting()!=null){
				MeetingType meetingType = new MeetingType();
			 	meetingType.setMeetingTypeId(CustomerConstants.CUSTOMER_MEETING_TYPE);
			 	client.getCustomerMeeting().getMeeting().setMeetingType(meetingType);

			}
			ClientCreationDAO clientCreationDAO = this.getClientCreationDAO();
			//updating the meeting
			clientCreationDAO.updateCenterMeeting(client);
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
	private void checkPermissionForCreate(Short newState,UserContext userContext,Short flagSelected,Short recordOfficeId,Short recordLoanOfficerId) throws SecurityException{
		if(!isPermissionAllowed(newState,userContext,flagSelected,recordOfficeId,recordLoanOfficerId))
			  throw new SecurityException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED); 	 
	}
	private boolean isPermissionAllowed(Short newState,UserContext userContext,Short flagSelected,Short recordOfficeId,Short recordLoanOfficerId){
		
		return ActivityMapper.getInstance().isSavePermittedForCustomer(newState.shortValue(),userContext,recordOfficeId,recordLoanOfficerId);
	}
}
