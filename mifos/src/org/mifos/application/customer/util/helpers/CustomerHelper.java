/**

 * CustomerHelper.java    version: 1.0



 * Copyright © 2005-2006 Grameen Foundation USA

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
package org.mifos.application.customer.util.helpers;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.util.valueobjects.AccountActionDate;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.accounts.util.valueobjects.CustomerAccount;
import org.mifos.application.configuration.business.ConfigurationIntf;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.customer.center.util.valueobjects.Center;
import org.mifos.application.customer.client.util.valueobjects.Client;
import org.mifos.application.customer.dao.CustomerNoteDAO;
import org.mifos.application.customer.dao.CustomerUtilDAO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.group.util.valueobjects.Group;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerAddressDetail;
import org.mifos.application.customer.util.valueobjects.CustomerHierarchy;
import org.mifos.application.customer.util.valueobjects.CustomerMeeting;
import org.mifos.application.customer.util.valueobjects.CustomerMovement;
import org.mifos.application.customer.util.valueobjects.CustomerNote;
import org.mifos.application.customer.util.valueobjects.CustomerPosition;
import org.mifos.application.customer.util.valueobjects.CustomerPositionDisplay;
import org.mifos.application.fees.util.valueobjects.FeeFrequency;
import org.mifos.application.fees.util.valueobjects.FeeMaster;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.application.master.util.valueobjects.PositionMaster;
import org.mifos.application.master.util.valueobjects.StatusMaster;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.personnel.dao.PersonnelDAO;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.application.personnel.util.valueobjects.PersonnelMaster;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.repaymentschedule.RepaymentSchedule;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleConstansts;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleFactory;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleHelper;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleIfc;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleInputsIfc;
import org.mifos.framework.dao.helpers.MasterDataRetriever;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class is a helper class to customer (i.e. client/group/center).
 * This includes commom methods for customer that can be used by client,group or center.
 * @author navitas
 */

public class CustomerHelper {

	private ConfigurationIntf labelConfig=MifosConfiguration.getInstance();
	public Date getCurrentDate(){
		return new java.sql.Date(new java.util.Date().getTime());
	}
	/**
	 * This method creates a new customer movement object.
	 * @return instance of CustomerMovement
	 */
  	public CustomerMovement createCustomerMovement(Customer customer, Date startDate, short status, short userId)throws ApplicationException,SystemException{
  		CustomerMovement movement = new CustomerMovement();
  		movement.setCustomer(customer);
  		if(startDate!=null)
  			movement.setStartDate(startDate);
  		else
  			movement.setStartDate(new Date(new java.util.Date().getTime()));
  		movement.setStatus(status);
  		movement.setEndDate(null);
  		movement.setOffice(customer.getOffice());
  		movement.setPersonnel(new PersonnelDAO().getUser(userId));
  		return movement;
  	}


	/**
	 * This method retrieves the list of programs that can be assigned to the group
	 * @return The list of programs
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	 public SearchResults getProgramMaster(short localeId)throws ApplicationException,SystemException{
			MasterDataRetriever masterDataRetriever = null;
			try{
				masterDataRetriever = getMasterDataRetriever();
			}
			catch(HibernateProcessException hpe){
				throw new ApplicationException(hpe);
			}
			masterDataRetriever.prepare(NamedQueryConstants.MASTERDATA_PROGRAM, GroupConstants.PROGRAMS_SET);
			masterDataRetriever.setParameter("localeId",localeId);
			return masterDataRetriever.retrieve();
		}

	
	/**
	 * This method return the count of customers in a office
	 * @param officeId
	 * @return customer count
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public int getCustomerCountInOffice(short officeId) throws ApplicationException,SystemException {
		Integer count ;
		Session session = null;
		try{
			session = HibernateUtil.getSession();
			Transaction trxn = session.beginTransaction();
			Query query = session.createQuery("select count(*) from org.mifos.application.customer.util.valueobjects.Customer customer where customer.office.officeId =:OFFICEID");
			query.setShort("OFFICEID", officeId);
			count = (Integer)query.uniqueResult();
			trxn.commit();
			return count;
		}catch(HibernateProcessException hpe){
			throw hpe;
		}finally{
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This method returns the count of customers based on given level
	 * @param levelId
	 * @return customer count
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public int getCustomerCount(short levelId,short officeId)throws ApplicationException,SystemException{
	  	Integer count ;
		Session session = null;

		try{
			session = HibernateUtil.getSession();
			Query query = session.createQuery("select count(*) from org.mifos.application.customer.util.valueobjects.Customer customer where  customer.customerLevel.levelId =:LEVELID and customer.office.officeId=:OFFICEID");
			query.setShort("LEVELID", levelId);
			query.setShort("OFFICEID", officeId);
			count = (Integer)query.uniqueResult();
			return count;
		}catch(HibernateProcessException hpe){
			throw new ApplicationException(hpe);
		}finally{
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This method returns the name of status configured for a level in the given locale
	 * @param localeId  user locale
	 * @param statusId status id
	 * @param levelId  customer level
	 * @return string status name
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public String getStatusName(short localeId, short statusId,short levelId)throws ApplicationException,SystemException{
		  String statusName=null;
		  List<StatusMaster> statusList =(List) new CustomerHelper().getStatusMaster(localeId,statusId,levelId).getValue();
			 if(statusList!=null){
				 StatusMaster sm = (StatusMaster)statusList.get(0);
				 statusName= sm.getStatusName();
			 }
		return statusName;
	  }

	/**
	 * This method returns the name of flag
	 * @param flagId  customer flag
	 * @param localeId  user locale
	 * @return string flag name
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public String getFlagName(short flagId,short localeId)throws ApplicationException,SystemException{
		MasterDataRetriever masterDataRetriever = null;

		try{
			masterDataRetriever = getMasterDataRetriever();
		}
		catch(HibernateProcessException hpe){
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare(NamedQueryConstants.MASTERDATA_GET_FLAGNAME, GroupConstants.CURRENT_FLAG);
		masterDataRetriever.setParameter("flagId",flagId);
		masterDataRetriever.setParameter("localeId",localeId);
		SearchResults sr = masterDataRetriever.retrieve();
		return (String)((List)sr.getValue()).get(0);
	}

	/**
	 * This method tells whether a given flag for the given status is blacklisted or not
	 * @param flagId  customer flag
	 * @return true if flag is blacklisted, otherwise false
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public boolean isBlacklisted(short flagId)throws ApplicationException,SystemException{
		MasterDataRetriever masterDataRetriever = null;

		try{
			masterDataRetriever = getMasterDataRetriever();
		}
		catch(HibernateProcessException hpe){
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare(NamedQueryConstants.MASTERDATA_IS_BLACKLISTED, GroupConstants.IS_BLACKLISTED);
		masterDataRetriever.setParameter("flagId",flagId);
		SearchResults sr = masterDataRetriever.retrieve();
		return ((Short)((List)sr.getValue()).get(0)).shortValue()==1?true:false;
	}

	/**
	 * This method returns the list of next available status
	 * to which a customer can move as per the customer state flow diagram
	 * @param localeId  user locale
	 * @param statusId status id
	 * @param levelId  customer level
	 * @return List next applicable status list
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public List getStatusList(short localeId, short status, short levelId, short branchId)throws ApplicationException,SystemException{
		List<StatusMaster> statusList = new ArrayList<StatusMaster>();
		switch (status){
			case GroupConstants.PARTIAL_APPLICATION :
				if(Configuration.getInstance().getCustomerConfig(branchId).isPendingApprovalStateDefinedForGroup())
					statusList.add(getStatusWithFlags(localeId,GroupConstants.PENDING_APPROVAL,levelId));
				else
					statusList.add(getStatusWithFlags(localeId,GroupConstants.ACTIVE, levelId));

				statusList.add(getStatusWithFlags(localeId,GroupConstants.CANCELLED, levelId));
		  		break;
			case GroupConstants.PENDING_APPROVAL:
				statusList.add(getStatusWithFlags(localeId,GroupConstants.PARTIAL_APPLICATION, levelId));
				statusList.add(getStatusWithFlags(localeId,GroupConstants.ACTIVE, levelId));
				statusList.add(getStatusWithFlags(localeId,GroupConstants.CANCELLED, levelId));
				break;
		  case GroupConstants.ACTIVE:
		  		statusList.add(getStatusWithFlags(localeId,GroupConstants.HOLD, levelId));
		  		statusList.add(getStatusWithFlags(localeId,GroupConstants.CLOSED, levelId));
		  		break;
		  case GroupConstants.HOLD:
		  		statusList.add(getStatusWithFlags(localeId,GroupConstants.ACTIVE, levelId));
		  		statusList.add(getStatusWithFlags(localeId,GroupConstants.CLOSED, levelId));
		  		break;
		  case GroupConstants.CANCELLED:
			  	statusList.add(getStatusWithFlags(localeId,GroupConstants.PARTIAL_APPLICATION, levelId));
		  default:
		  }
		  return statusList;
	  }

	/**
	 * This method returns flag list associated with passed in status Id
	 * @param localeId  user locale
	 * @param statusId status id
	 * @param levelId  customer level
	 * @return SearchResults flag list
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getStatusFlags(short localeId , short statusId , short levelId)throws ApplicationException,SystemException{
		MasterDataRetriever masterDataRetriever = null;

		try{
			masterDataRetriever = getMasterDataRetriever();
		}
		catch(HibernateProcessException hpe){
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare(NamedQueryConstants.MASTERDATA_SPECIFIC_STATUS_FLAG, GroupConstants.STATUS_FLAG);
		masterDataRetriever.setParameter("localeId" ,localeId );
		masterDataRetriever.setParameter("levelId",levelId);
		masterDataRetriever.setParameter("specificStatusId",statusId);
		return masterDataRetriever.retrieve();
	}

	/**
	 * This method returns list of all available status for a level
	 * @param localeId  user locale
	 * @param statusId status id
	 * @param levelId  customer level
	 * @return SearchResults status list
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getStatusMaster(short localeId , short statusId, short levelId )throws ApplicationException,SystemException{
		MasterDataRetriever masterDataRetriever = null;

		try{
			masterDataRetriever = getMasterDataRetriever();
		}
		catch(HibernateProcessException hpe){
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare(NamedQueryConstants.MASTERDATA_SPECIFIC_STATUS, GroupConstants.STATUS);
		masterDataRetriever.setParameter("localeId" ,localeId );
		masterDataRetriever.setParameter("levelId",levelId);
		masterDataRetriever.setParameter("specificStatusId",statusId);
		return masterDataRetriever.retrieve();
	}

	/**
	 * This method is the helper method that returns a status along with its assoicated flags
	 * @param localeId  user locale
	 * @param statusId status id
	 * @param levelId  customer level
	 * @return SearchResults status list
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private StatusMaster getStatusWithFlags(short locale, short status, short levelId) throws ApplicationException,SystemException{
	   	SearchResults sr = this.getStatusMaster(locale,status, levelId);
	  	StatusMaster statusMaster = null;
	  	Object obj = sr.getValue();
	  	if(obj!=null){
	  		statusMaster = (StatusMaster)((List)obj).get(0);
	  		sr = this.getStatusFlags(locale,status, levelId);
	  		obj=sr.getValue();
	  		if(obj!=null){
	  			statusMaster.setFlagList((List)obj);
	   		}
	  		else{
	  			statusMaster.setFlagList(null);
	   		}
	  	}
	  	return statusMaster;
	}

	
	
	/**
	 * This method returns List given number of notes for a given customer
	 * @param count number of notes to be returned
	 * @return customerId for which notes has to be retrieved
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public List<CustomerNote> getLatestNotes(int count, Integer customerId)throws ApplicationException,SystemException{
		CustomerNoteDAO notesDAO = new CustomerNoteDAO();
		return notesDAO.getLatestNotesByCount(count, customerId);
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
	 * This method returns list of all available status for a level
	 * @param localeId  user locale
	 * @param statusId status id
	 * @param levelId  customer level
	 * @return SearchResults status list
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getStatusForLevel(short localeId , short levelId )throws ApplicationException,SystemException{
		MasterDataRetriever masterDataRetriever = null;

		try{
			masterDataRetriever = getMasterDataRetriever();
		}
		catch(HibernateProcessException hpe){
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare(NamedQueryConstants.MASTERDATA_STATUS, GroupConstants.STATUS_LIST);
		masterDataRetriever.setParameter("localeId" ,localeId );
		masterDataRetriever.setParameter("levelId",levelId);
		return masterDataRetriever.retrieve();
	}
	/***
	 * This method converts the valueobject to reflect the changes being done on a edit of MFI information.
	 * @param oldClient The details retrieved from the database
	 * @param newClient The MFI details which would ahve been edited
	 * @return
	 */
	public Client convertOldClientMFIDetails(Client oldClient , Client newClient){

		oldClient.setExternalId(newClient.getExternalId());
		oldClient.setTrained(newClient.getTrained());
		oldClient.setTrainedDate(newClient.getTrainedDate());
		oldClient.setClientConfidential(newClient.getClientConfidential());
		oldClient.setCustomerFormedByPersonnel(newClient.getCustomerFormedByPersonnel());
		//oldClient.setPersonnel(newClient.getPersonnel());
		return oldClient;
	}

	/**
	 * This method creates a new customer hierarchy object. It is called whenever a new group is created with
	 * center as its parent or group parent is changed.
	 * @return instance of CustomerHierarchy
	 */
	public CustomerHierarchy createCustomerHierarchy(Customer parent, Customer child, Context context){
		CustomerHierarchy customerHierarchy = new CustomerHierarchy();
		customerHierarchy.setCustomer(child);
		customerHierarchy.setParentCustomer(parent);
		customerHierarchy.setStatus(CustomerConstants.ACTIVE_HIERARCHY);
		customerHierarchy.setEndDate(new java.sql.Date(new java.util.Date().getTime()));
		customerHierarchy.setStartDate(new java.sql.Date(new java.util.Date().getTime()));
		customerHierarchy.setUpdatedBy(context.getUserContext().getId());
		customerHierarchy.setUpdatedDate(new java.sql.Date(new java.util.Date().getTime()));
		return customerHierarchy;
	}

	/**
	 * This method creates a new customer hierarchy object. It is called whenever a new group is created with
	 * center as its parent or group parent is changed.
	 * @return instance of CustomerHierarchy
	 */
	public CustomerMovement createCustomerMovement(Customer customer , Date startDate){
		CustomerMovement customerMovement = new CustomerMovement();
		customerMovement.setCustomer(customer);
		customerMovement.setStatus(CustomerConstants.ACTIVE_HIERARCHY);
		customerMovement.setStartDate(startDate);
		customerMovement.setOffice(customer.getOffice());
		customerMovement.setPersonnel(customer.getPersonnel());

		return customerMovement;
	}
/**
	 * This method creates a new SearchResults object with values as passed in parameters
	 * @param resultName the name with which framework will put resultvalue in request
	 * @param resultValue that need to be put in request
	 * @return SearchResults instance
	 */
	public SearchResults getResultObject(String resultName, Object resultValue){
		SearchResults result = new SearchResults();
		result.setResultName(resultName);
		result.setValue(resultValue);
		return result;
	}
	/***
	 * Concatenates all the names together
	 * @param firstName
	 * @param middleName
	 * @param secondLastName
	 * @param lastName
	 * @return
	 */
	public String setDisplayNames(String firstName, String middleName, String secondLastName, String lastName) {
		StringBuilder displayName = new StringBuilder();
		displayName.append(firstName);
		if(!ValidateMethods.isNullOrBlank(middleName)){
			displayName.append(CustomerConstants.BLANK);
			displayName.append(middleName );
		}
		if(!ValidateMethods.isNullOrBlank(secondLastName)){
			displayName.append(CustomerConstants.BLANK);
			displayName.append(secondLastName);
		}
		if(!ValidateMethods.isNullOrBlank(lastName)){
			displayName.append(CustomerConstants.BLANK);
			displayName.append(lastName);
		}
		return displayName.toString().trim();
	}
	
	/**
	 * This method is helper method that sets the personnel age in request
	 * @param request Contains the request parameters
	 */
	public int calculateAge(Date date){
		int age = DateHelper.DateDiffInYears(date);
		return age;
	}
	/**
	 * Generates the ID  for the client using ID Generator.
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public String generateSystemId(Short officeId , String officeGlobalNum)throws SystemException,ApplicationException{
		int maxCustomerId = 0;
		maxCustomerId = new CustomerUtilDAO().getMaxCustomerId(officeId);
		String systemId = IdGenerator.generateSystemId(officeGlobalNum , maxCustomerId);
		return systemId;
	}

	/**
	 * This is the helper method to check for extra date validations needed at the time of create preview
	 * @param personnelStatus
	 */
	public boolean isValidDOB(String dob ,Locale mfiLocale){
		java.sql.Date sqlDOB=null;
		boolean isValidDate = true;

		if(!ValidateMethods.isNullOrBlank(dob)) {
			sqlDOB=DateHelper.getLocaleDate(mfiLocale,dob);
			Calendar currentCalendar = new GregorianCalendar();
			int year=currentCalendar.get(Calendar.YEAR);
			int month=currentCalendar.get(Calendar.MONTH);
			int day=currentCalendar.get(Calendar.DAY_OF_MONTH);
			currentCalendar = new GregorianCalendar(year,month,day);
			java.sql.Date currentDate=new java.sql.Date(currentCalendar.getTimeInMillis());
			if(currentDate.compareTo(sqlDOB) < 0 ) {
				isValidDate= false;


			}
		}
		return isValidDate;
	}
	/**
	 * This method concatenates differnt address lines and forms one line of address
	 * @param addressDetails  CustomerAddressDetail
	 * @return string single line address
	 */
	public String getDisplayAddress(CustomerAddressDetail addressDetails){
		String displayAddress="";
		if(!isNullOrBlank(addressDetails.getLine1())){			
			displayAddress+=addressDetails.getLine1();
		}
			
		if(!isNullOrBlank(addressDetails.getLine2())&&!isNullOrBlank(addressDetails.getLine1())){
			displayAddress+=", "+ addressDetails.getLine2();
		}
		else if(!isNullOrBlank(addressDetails.getLine2())){
			displayAddress+=addressDetails.getLine2();
		}
		if(!isNullOrBlank(addressDetails.getLine3())&&!isNullOrBlank(addressDetails.getLine2())||(!isNullOrBlank(addressDetails.getLine3())&&!isNullOrBlank(addressDetails.getLine1()))){			
			displayAddress+=", "+ addressDetails.getLine3();			
		}
		else if(!isNullOrBlank(addressDetails.getLine3())){			
			displayAddress+=addressDetails.getLine3();
		}
		return displayAddress;
	}
	/***
	 * This method checks if a particuar value is either null or blank
	 * @param value the value that has to be checked as to whether it is null or blank
	 * @return true or false as to whether the value passed was null or blank
	 */
	public static boolean isNullOrBlank(String value){
		boolean isValueNull = false;

		if(value == null || value.trim().equals(CenterConstants.BLANK)){
			isValueNull = true;
		}
		return isValueNull;
	}

	//Added by mohammedn
	public void saveMeetingDetails(Customer customer,Session session, UserContext userContext) throws ApplicationException,SystemException {

		//Customer customer = (Customer)context.getValueObject();
		Meeting meeting =null ;
		Set<AccountFees> accountFeesSet=new HashSet();
		CustomerMeeting customerMeeting =null;
		customerMeeting = customer.getCustomerMeeting();
		// only if customer meeting is not null get the meeting from customer meeting
		// this could be null if customer does not have a customer meeting.
		if(null != customerMeeting){
			meeting = customerMeeting.getMeeting();
		//	Session session = null;
			//Transaction trxn = null;
			try {
				//session = HibernateUtil.getSession();
				//trxn = session.beginTransaction();
				CustomerAccount customerAccount=customer.getCustomerAccount();
				if(null != customerAccount) {
				accountFeesSet=customerAccount.getAccountFeesSet();
				if(accountFeesSet !=null&& accountFeesSet.size()>0){
					for(AccountFees accountFees:accountFeesSet) {
						Fees fees=(Fees)session.get(Fees.class,accountFees.getFees().getFeeId());
						FeeFrequency feeFrequency=fees.getFeeFrequency();
						if(null !=feeFrequency) {
							feeFrequency.getFeeFrequencyId();
							feeFrequency.getFeeFrequencyTypeId();
						 }

						accountFees.setFeeAmount(accountFees.getAccountFeeAmount().getAmountDoubleValue());
						accountFees.getFees().setRateFlatFalg(fees.getRateFlatFalg());
						accountFees.getFees().setFeeFrequency(feeFrequency);
					 }
				}
			}
			//get the repayment schedule input object which would be passed to repayment schedule generator
			RepaymentScheduleInputsIfc repaymntScheduleInputs = RepaymentScheduleFactory.getRepaymentScheduleInputs();
			RepaymentScheduleIfc repaymentScheduler = RepaymentScheduleFactory.getRepaymentScheduler();

			//set the customer'sMeeting , this is required to check if the disbursement date is valid
			// this would be null if customer does not have a meeting.
			repaymntScheduleInputs.setMeeting(meeting);
			repaymntScheduleInputs.setMeetingToConsider(RepaymentScheduleConstansts.MEETING_CUSTOMER);
			// set the loan disburesment date onto the repayment frequency
			repaymntScheduleInputs.setRepaymentFrequency(meeting);
			if(accountFeesSet!=null)
				repaymntScheduleInputs.setAccountFee(accountFeesSet);
			else
				repaymntScheduleInputs.setAccountFee(new HashSet());
			// this method invokes the  repayment schedule generator.
			repaymentScheduler.setRepaymentScheduleInputs(repaymntScheduleInputs);
			RepaymentSchedule repaymentSchedule = repaymentScheduler.getRepaymentSchedule();
			Set<AccountActionDate> accntActionDateSet = RepaymentScheduleHelper.getActionDateValueObject(repaymentSchedule);
			//context.addAttribute(new SearchResults("AccountActionDate",accntActionDateSet));
	        //this will insert records in account action date which is noting but installments.
			if(null != accntActionDateSet && ! accntActionDateSet.isEmpty()){
				// iterate over account action date set and set the relation ship.
				for(AccountActionDate accountActionDate : accntActionDateSet){
					accountActionDate.setAccount(customer.getCustomerAccount());
					accountActionDate.setCustomerId(customer.getCustomerId());
					accountActionDate.setCurrencyId(Short.valueOf("1"));
					session.save(accountActionDate);
				}
			}
			//trxn.commit();

			}catch (Exception hpe) {
				String messageArgumentKey =null;
				if(customer instanceof Client)messageArgumentKey=ConfigurationConstants.CLIENT;
				else if (customer instanceof Group )messageArgumentKey=ConfigurationConstants.GROUP;
					else if (customer instanceof Center )messageArgumentKey=ConfigurationConstants.CENTER;	
				
				throw new CustomerException(CustomerConstants.CREATE_FAILED_EXCEPTION ,hpe,new Object[]{labelConfig.getLabel(messageArgumentKey,userContext.getPereferedLocale())});
			}
		}
		}

		public void attachMeetingDetails(Customer customer,Session session,CustomerMeeting customerMeeting) throws ApplicationException,SystemException {

			//Customer customer = (Customer)context.getValueObject();
			Meeting meeting =null ;
			// only if customer meeting is not null get the meeting from customer meeting
			// this could be null if customer does not have a customer meeting.
			if(null != customerMeeting){
				meeting = customerMeeting.getMeeting();
			//	Session session = null;
				//Transaction trxn = null;
				try {
					//session = HibernateUtil.getSession();
					//trxn = session.beginTransaction();
				/*	Customer vo = (Customer)session.get(Customer.class,customer.getCustomerId());

					if(vo.getCustomerAccounts()!=null){
						Iterator accountsIterator  = vo.getCustomerAccounts().iterator();
						while(accountsIterator.hasNext()){
							Account account = (Account)accountsIterator.next();
							if(account.getAccountTypeId().shortValue()== new Short(AccountTypes.CUSTOMERACCOUNT).shortValue()){
								vo.setCustomerAccount((CustomerAccount)account);
								break;
							}
						}
					}
					CustomerAccount customerAccount=vo.getCustomerAccount();

					Set<AccountFees> accountFeesSet=null;
					if(null != customerAccount) {
					accountFeesSet=customerAccount.getAccountFeesSet();
					// System.out.println("In Account Fees-----@@@@^^^^^^^^^&&&&&&&&&&**************" +
							"**********#################!!!!!!!!!!------"+accountFeesSet.size());
					for(AccountFees accountFees:accountFeesSet) {
						Fees fees=(Fees)session.get(Fees.class,accountFees.getFees().getFeeId());
						FeeFrequency feeFrequency=fees.getFeeFrequency();
						if(null !=feeFrequency) {
							feeFrequency.getFeeFrequencyId();
							feeFrequency.getFeeFrequencyTypeId();
						 }

						accountFees.setFeeAmount(accountFees.getAccountFeeAmount());
						accountFees.getFees().setRateFlatFalg(fees.getRateFlatFalg());
						accountFees.getFees().setFeeFrequency(feeFrequency);
					 }
				}*/
				//get the repayment schedule input object which would be passed to repayment schedule generator
				RepaymentScheduleInputsIfc repaymntScheduleInputs = RepaymentScheduleFactory.getRepaymentScheduleInputs();
				RepaymentScheduleIfc repaymentScheduler = RepaymentScheduleFactory.getRepaymentScheduler();

				//set the customer'sMeeting , this is required to check if the disbursement date is valid
				// this would be null if customer does not have a meeting.
				repaymntScheduleInputs.setMeeting(meeting);
				repaymntScheduleInputs.setMeetingToConsider(RepaymentScheduleConstansts.MEETING_CUSTOMER);
				// set the loan disburesment date onto the repayment frequency
				repaymntScheduleInputs.setRepaymentFrequency(meeting);
				//repaymntScheduleInputs.setAccountFee(accountFeesSet);
				// this method invokes the  repayment schedule generator.
				repaymentScheduler.setRepaymentScheduleInputs(repaymntScheduleInputs);
				RepaymentSchedule repaymentSchedule = repaymentScheduler.getRepaymentSchedule();
				Set<AccountActionDate> accntActionDateSet = RepaymentScheduleHelper.getActionDateValueObject(repaymentSchedule);
				//context.addAttribute(new SearchResults("AccountActionDate",accntActionDateSet));
		        //this will insert records in account action date which is noting but installments.
				if(null != accntActionDateSet && ! accntActionDateSet.isEmpty()){
					// iterate over account action date set and set the relation ship.
					for(AccountActionDate accountActionDate : accntActionDateSet){
						accountActionDate.setAccount(customer.getCustomerAccount());
						accountActionDate.setCustomerId(customer.getCustomerId());
						accountActionDate.setCurrencyId(Short.valueOf("1"));
						session.save(accountActionDate);
					}
				}
				//trxn.commit();

				}catch (Exception hpe) {
					throw new CustomerException(CustomerConstants.CREATE_FAILED_EXCEPTION ,hpe);
				}
			}

	}
		/**
		 * This method returns true if there is any accountFee with null or zero amnt.
		 * it checks if the fees id is not null , then amount should not be null.
		 * @return true or false
		 */
		public boolean isAnyAccountFeesWithoutAmnt(List<FeeMaster> adminFeeList , List<FeeMaster> selectedFeeList ) {
			//check if any administrative fee amount is null
			if(null!=adminFeeList && adminFeeList.size()>0){
				for(int index=0;index<adminFeeList.size();index++ ){
					if(adminFeeList.get(index).getCheckedFee()==null ||(adminFeeList.get(index).getCheckedFee()!=null &&adminFeeList.get(index).getCheckedFee().shortValue()!=1)){
						if (adminFeeList.get(index).getRateOrAmount()==null ||adminFeeList.get(index).getRateOrAmount()==0.0){
							return true;
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
		 * This method prepares a list of CustomerPositionDisplay that tells which position is assigned to which customer.
		 * @param group
		 * @return List of CustomerPositionDisplay
		 * @throws ApplicationExcpetion
		 * @throws SystemExcpetion
		 */
		public List loadCustomerPositions(Customer customer, short localeId,List positionMaster)throws ApplicationException,SystemException{
			List<CustomerPositionDisplay> customerPositions = new ArrayList<CustomerPositionDisplay>();
			//SearchResults sr =  new GroupHelper().getPositionsMaster(localeId);
			
				//List positionMaster =(List) sr.getValue();
				Set positionsSet = customer.getCustomerPositions();

				if(positionMaster!=null && positionsSet!=null){
					PositionMaster pm=null;
					CustomerPosition cp=null;
					CustomerPositionDisplay cpdisplay=null;
					Iterator posMaster=positionMaster.iterator();
					Iterator custPos=null;
					while(posMaster.hasNext()){
						pm=(PositionMaster)posMaster.next();
						cpdisplay = new CustomerPositionDisplay();
						cpdisplay.setPositionId(pm.getPositionId());
						cpdisplay.setPositionName(pm.getPositionName());
						custPos=positionsSet.iterator();
						while(custPos.hasNext()){
							cp=(CustomerPosition)custPos.next();
							if(cp.getPositionId().intValue()==pm.getPositionId().intValue()){
								cpdisplay.setCustomerName(cp.getCustomerName());
								cpdisplay.setCustomerId(cp.getCustomerId());
							}
						}
						customerPositions.add(cpdisplay);
					}
				}
			
			return  customerPositions;
		  }
		public Personnel getFormedByLO(Context context, short personnelId) {
			Personnel loanOfficer = new Personnel();
			Iterator iteratorLO=((List)context.getSearchResultBasedOnName(CustomerConstants.FORMEDBY_LOAN_OFFICER_LIST).getValue()).iterator();
			//Obtaining the name of the selected loan officer from the master list of loan officers
			while (iteratorLO.hasNext()){
				PersonnelMaster lo=(PersonnelMaster)iteratorLO.next();
				if(lo.getPersonnelId().shortValue()==personnelId){
					loanOfficer.setPersonnelId(lo.getPersonnelId());
					loanOfficer.setDisplayName(lo.getDisplayName());
					loanOfficer.setVersionNo(lo.getVersionNo());

				}
			}
			return loanOfficer;
		}



}