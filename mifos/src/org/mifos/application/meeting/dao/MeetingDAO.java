/**

 * MeetingDAO.java    version: 1.0

 

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
package org.mifos.application.meeting.dao;


import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.center.util.valueobjects.Center;
import org.mifos.application.customer.client.util.valueobjects.Client;
import org.mifos.application.customer.dao.CustomerUtilDAO;
import org.mifos.application.customer.group.util.valueobjects.Group;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerMeeting;
import org.mifos.application.master.util.valueobjects.EntityMaster;
import org.mifos.application.master.util.valueobjects.LookUpMaster;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.resources.MeetingConstants;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.meeting.util.valueobjects.MeetingDetails;
import org.mifos.application.meeting.util.valueobjects.MeetingType;
import org.mifos.application.meeting.util.valueobjects.RecurrenceType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.components.audit.util.helpers.AuditConstants;
import org.mifos.framework.components.audit.util.helpers.LogInfo;
import org.mifos.framework.components.audit.util.helpers.LogValueMap;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.components.scheduler.ScheduleDataIntf;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.dao.helpers.MasterDataRetriever;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class encapsulate all database access logic related meeting module
 */
public class MeetingDAO extends DAO {
	
	// get the logger for logging
	MifosLogger meetingLogger = MifosLogManager
			.getLogger(LoggerConstants.MEETINGLOGGER);

	/**
	 * This method would check if the meeting for a customer can be changed, the
	 * meeting should form a derivative of the loan frequencyReturns true if it
	 * can be updated , else would return false
	 * 
	 * @param customer
	 *            Customer object
	 * @return true or false
	 */
	public boolean isMeetingUpdateValid(Customer customer)
			throws ApplicationException, SystemException {

		// TODO remove
		return true;
	}

	/**
	 * This method updates the customer meeting object through hibernate calls
	 * onto the database
	 * 
	 * @param customer
	 *            Customer object
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public void update(Customer customer) throws ApplicationException,
			SystemException {

	}

	/**
	 * This method would check if the child meeting object is a derivative of
	 * the parent meeting object . Returns true if it is a derivative , else
	 * would return false
	 * 
	 * @param parentMeeting
	 * @param childMeeting
	 * @return
	 */
	boolean isMeetingTransferValid(Meeting parentMeeting, Meeting childMeeting)
			throws ApplicationException, SystemException {
		// TODO remove later
		return true;
	}

	/**
	 * This method would load all the master information required relavant to
	 * the meeting object. This internally would call other private methods in
	 * the meeting dao to load the relavant mater information required for
	 * meetings
	 * 
	 * @param context
	 */
	public void loadMeetingMaster(
			org.mifos.framework.util.valueobjects.Context context)
			throws ApplicationException, SystemException {
		meetingLogger.info("Loading the master data ...");
		context.addAttribute(getWeekDaysMaster(context));
		context.addAttribute(getWeekRankMaster(context));

	}

	/**
	 * This method would return the meeting schedule of the passed meeting
	 * object , this would interact with the scheduler component to get the
	 * relavant information.
	 * 
	 * @param meeting
	 *            meeting object
	 * @return
	 */
	ScheduleDataIntf getMeetingSchedule(Meeting meeting) {
		// TODO remove later
		return null;

	}

	/**
	 * This function load the master data for the week day list
	 * @param context
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	private SearchResults getWeekDaysMaster(
			org.mifos.framework.util.valueobjects.Context context)
			throws SystemException, ApplicationException {
		Session session=null;
		EntityMaster entityMaster = new EntityMaster();
		SearchResults searchResults = new SearchResults();
		try {
			session = HibernateUtil.getSession();
			List<LookUpMaster> weekdays =session.getNamedQuery(NamedQueryConstants.GETWORKINGWEEKDAYS).setShort("localeId",context.getUserContext().getLocaleId()).list();
			entityMaster.setLookUpValues(weekdays);

		} catch (HibernateProcessException e) {

			throw new SystemException(e);

		} catch (HibernateException he) {
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}	
		searchResults.setValue(entityMaster);
		searchResults.setResultName(MeetingConstants.WEEKDAYSLIST);
		return searchResults;
	}

	/**
	 * This function load the rank master data
	 * @param context
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	private SearchResults getWeekRankMaster(Context context)
			throws SystemException, ApplicationException {
		MasterDataRetriever masterDataRetriever = null;
		try {

			masterDataRetriever = getMasterDataRetriever();

			UserContext uc = context.getUserContext();
			return masterDataRetriever.retrieveMasterData(MeetingConstants.RANKDAYENTITY, uc
					.getLocaleId(), MeetingConstants.WEEKRANKLIST,
					MeetingConstants.RANKOFDAYCLASSPATH, MeetingConstants.RANKDAYID);

		} catch (HibernateProcessException hpe) {
			throw new SystemException(hpe);

		}

	}

	/**
	 * This function gets a perticular meeting from the database
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void get(Context context) throws SystemException,
			ApplicationException {
		Session session = null;
		Transaction transaction = null;
		Meeting meeting = null;

		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();
			
			Integer meetingId = ((Meeting) context.getValueObject()).getMeetingId();
			meetingLogger.info("Getting the meeting with id="+meetingId);
			meeting = (Meeting) session.get(Meeting.class,meetingId);
			
			if ( null!=meeting)
			{
				meeting.getMeetingDetails().getDetailsId();
				meeting.getMeetingDetails().getMeetingRecurrence().getDetailsId();
				meeting.getMeetingDetails().getRecurrenceType().getRecurrenceId();
				meeting.getMeetingType().getMeetingTypeId();
			}
			else
			{
				throw new MeetingException(MeetingConstants.KEYLOADFAILED);
			}
			context.setValueObject(meeting);

			transaction.commit();

		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);

		} catch (HibernateException he) {
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

	/**
	 * This function fetch the meeting with the given id
	 * @param meetingId
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public Meeting get(Integer meetingId) throws SystemException,
			ApplicationException {
		Session session = null;
		Meeting meeting = null;

		try {
			session = HibernateUtil.getSession();
			meetingLogger.info("Getting the meeting with id="+meetingId);
			
			meeting = (Meeting) session.get(Meeting.class, meetingId);

			if (null != meeting) {

				MeetingDetails md = meeting.getMeetingDetails();

				if (null != md) {

					meeting.getMeetingDetails().getDetailsId();
					meeting.getMeetingDetails().getMeetingRecurrence()
							.getDetailsId();
					meeting.getMeetingDetails().getRecurrenceType()
							.getRecurrenceId();
					meeting.getMeetingType().getMeetingTypeId();
				} else {
					throw new MeetingException(MeetingConstants.KEYLOADFAILED);
				}
			} else {
				throw new MeetingException(MeetingConstants.KEYLOADFAILED);
			}
		} catch (HibernateProcessException e) {
			throw new SystemException(e);

		} catch (HibernateException he) {
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return meeting;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mifos.framework.dao.DAO#update(org.mifos.framework.util.valueobjects.Context)
	 */
	@Override
	public void update(Context context) throws ApplicationException,
			SystemException {
		Session session = null;
		Transaction transaction = null;
		Meeting uiMeeting = (Meeting) context.getValueObject();
		Meeting meeting = null;
		
		

		try {
			
			
			Integer meetingId = uiMeeting.getMeetingId();
			session = HibernateUtil.getSessionWithInterceptor(getLogInfo(context));
			transaction = session.beginTransaction();
			
			
			if (null != meetingId) {

				meetingLogger.info("Updating the meeting with id="+meetingId);
				meeting = (Meeting) session.get(Meeting.class,meetingId);
				
				Customer customer =(Customer) context.getBusinessResults("Customer");
				updateMeetingForCustomer(customer,meeting,session);

				if (null != meeting) {

					if (!uiMeeting.getMeetingPlace().equals(
							meeting.getMeetingPlace())) {
						meeting.setMeetingPlace(uiMeeting.getMeetingPlace());
					}
					MeetingDetails md = meeting.getMeetingDetails();
					if (null != md) {

						RecurrenceType rt = md.getRecurrenceType();
						if (null != rt) {

							Short rtype = rt.getRecurrenceId();

							if (rtype.shortValue() == MeetingConstants.WEEK) {

								Short day = uiMeeting.getMeetingDetails()
										.getMeetingRecurrence().getWeekDay();
								Short dbday = meeting.getMeetingDetails()
										.getMeetingRecurrence().getWeekDay();
								if (day.shortValue() != dbday.shortValue()) {

									meeting.getMeetingDetails()
											.getMeetingRecurrence().setWeekDay(
													day);
									session.save(meeting);

								}

							} else if (rtype.shortValue() == MeetingConstants.MONTH) {
								Short dbDayNumber = meeting.getMeetingDetails()
										.getMeetingRecurrence().getDayNumber();

								// Bug 28217 changed from getWeekday
								Short uiDayNumber = uiMeeting
										.getMeetingDetails()
										.getMeetingRecurrence().getDayNumber();

								if (null != dbDayNumber) {
									if (dbDayNumber.shortValue() != uiDayNumber
											.shortValue()) {

										meeting.getMeetingDetails()
												.getMeetingRecurrence()
												.setDayNumber(uiDayNumber);
										session.save(meeting);
									}
								} else {
									boolean changed = false;
									Short dbRank = meeting.getMeetingDetails()
											.getMeetingRecurrence()
											.getRankOfDays();
									Short uiRank = uiMeeting
											.getMeetingDetails()
											.getMeetingRecurrence()
											.getRankOfDays();
									Short dbDay = meeting.getMeetingDetails()
											.getMeetingRecurrence()
											.getWeekDay();
									Short uiDay = uiMeeting.getMeetingDetails()
											.getMeetingRecurrence()
											.getWeekDay();

									if (dbRank.shortValue() != uiRank
											.shortValue()) {
										changed = true;

										meeting.getMeetingDetails()
												.getMeetingRecurrence()
												.setRankOfDays(uiRank);

									}

									if (dbDay.shortValue() != uiDay
											.shortValue()) {
										changed = true;

										meeting.getMeetingDetails()
												.getMeetingRecurrence()
												.setWeekDay(uiDay);

									}
									if (changed) {
										session.save(meeting);
									}

								}

							}

						} else {
							throw new MeetingException(MeetingConstants.KEYUPDATEFAILED);

						}
					} else {
						throw new MeetingException(MeetingConstants.KEYUPDATEFAILED);
						}
				} else {
					throw new MeetingException(MeetingConstants.KEYUPDATEFAILED);
				}
			} else {
				throw new MeetingException(MeetingConstants.KEYUPDATEFAILED);			}

			transaction.commit();
		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);

		} catch (HibernateException he) {
			throw new MeetingException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

	/**
	 * This function creates the customer meeting 
	 * @param context
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public void createCustomerMeeting(Context context)
			throws ApplicationException, SystemException {

		Session session = null;
		Transaction transaction = null;
		Meeting meeting = (Meeting) context.getValueObject();
		// set the dates
		Calendar calender = new GregorianCalendar();
		calender.setTimeInMillis(System.currentTimeMillis());
		meeting.setMeetingStartDate(calender);
		meeting.setMeetingStartTime(calender);

		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();
			Customer customer = (Customer) context.getSearchResultBasedOnName(
					"Customer").getValue();
			MeetingType meetingType = new MeetingType();
			meetingType
					.setMeetingTypeId(CustomerConstants.CUSTOMER_MEETING_TYPE);
			meeting.setMeetingType(meetingType);
			meetingLogger.info("Creating the  meeting at location"+meeting.getMeetingPlace());
			session.save(meeting);
			
			List<Customer> childList = new CustomerUtilDAO().getChildListForParent(customer.getSearchId(),customer.getOffice().getOfficeId(),session);
			createMeetingForCustomer(customer, meeting, session);
			
			for(Customer cust:childList){
				createMeetingForCustomer(cust,meeting,session);
			}
			transaction.commit();
		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);

		} catch (HibernateException he) {
			transaction.rollback();
			throw new MeetingException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}
	
	private void createMeetingForCustomer(Customer customer, Meeting meeting, Session session){
		CustomerMeeting cm = new CustomerMeeting();
		cm.setMeeting(meeting);
		cm.setCustomer(customer);
		cm.setUpdatedFlag(YesNoFlag.NO.getValue());
		session.save(cm);
	}
	/**
	 * This function returns the LogInfo object with proper values set into it 
	 * @param context
	 * @return
	 */
	private LogInfo getLogInfo(Context context)
	{
		Customer customer =(Customer) context.getBusinessResults("Customer");
		short levelId = customer.getCustomerLevel().getLevelId().shortValue();
		
		LogValueMap logValueMap = new LogValueMap();
		LogInfo logInfo=null;
		if ( levelId==CustomerConstants.GROUP_LEVEL_ID)
		{
			logValueMap.put(AuditConstants.REALOBJECT,new Group());
			logValueMap.put("customerMeeting",AuditConstants.REALOBJECT);
			logValueMap.put("meeting","customerMeeting");
			logValueMap.put("meetingDetails","meeting");
			logValueMap.put("meetingRecurrence","meetingDetails");
			logInfo =new LogInfo(customer.getCustomerId(),"Group",context,logValueMap);

		}
		else if(levelId==CustomerConstants.CLIENT_LEVEL_ID )
		{
			logValueMap.put(AuditConstants.REALOBJECT,new Client());
			logValueMap.put("customerMeeting",AuditConstants.REALOBJECT);
			logValueMap.put("meeting","customerMeeting");
			logValueMap.put("meetingDetails","meeting");
			logValueMap.put("meetingRecurrence","meetingDetails");
			logInfo =new LogInfo(customer.getCustomerId(),"Client",context,logValueMap);

		}
		else if (levelId==CustomerConstants.CENTER_LEVEL_ID)
		{
			logValueMap.put(AuditConstants.REALOBJECT,new Center());
			logValueMap.put("customerMeeting",AuditConstants.REALOBJECT);
			logValueMap.put("meeting","customerMeeting");
			logValueMap.put("meetingDetails","meeting");
			logValueMap.put("meetingRecurrence","meetingDetails");
			logInfo =new LogInfo(customer.getCustomerId(),"Center",context,logValueMap);
		}
		
		  
		return logInfo;
	}
	
	
	private void updateMeetingForCustomer(Customer customer, Meeting meeting, Session session){
		customer.getCustomerMeeting().setUpdatedFlag(YesNoFlag.YES.getValue());
		session.update(customer);
	}
	
}
