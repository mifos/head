/**

 * MeetingBusinessProcessor.java    version: 1.0

 

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
package org.mifos.application.meeting.business.handlers;

import org.mifos.application.meeting.dao.MeetingDAO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.resources.MeetingConstants;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This class encapsulate all the business logic related to meeting module 
 * @author rajenders
 * 
 */
public class MeetingBusinessProcessor extends MifosBusinessProcessor {

	
	/**
	 * This function is called to create the meeting .This function is kept intentionally
	 * blank as we do not wnat the framework to call the create method ,because we do not want 
	 * to create the meeting right now .It will be created by the modules who use this module
	 */
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#create(org.mifos.framework.util.valueobjects.Context)
	 */
	@Override
	public void create(Context context) throws SystemException,
			ApplicationException {
	}

	/**
	 * This method will called before loading the meeting action load to load
	 * the master data
	 */
	public void loadInitial(Context context) throws SystemException,
			ApplicationException {

		try {

			MeetingDAO meetingdao = (MeetingDAO) getDAO(context.getPath());
			meetingdao.loadMeetingMaster(context);
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new MeetingException(MeetingConstants.KEYLOADFAILED, e);
		}

	}

	/**
	 * This method will called before loading the meeting action load to load
	 * the master data
	 */
	public void getInitial(Context context) throws SystemException,
			ApplicationException {

		try {

			MeetingDAO meetingdao = (MeetingDAO) getDAO(context.getPath());
			meetingdao.loadMeetingMaster(context);
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new MeetingException(MeetingConstants.KEYLOADFAILED, e);
		}

	}


	private void createCustomerMeeting(Context context) throws SystemException,
			ApplicationException {
		try {

			MeetingDAO meetingdao = (MeetingDAO) getDAO(context.getPath());

			meetingdao.createCustomerMeeting(context);

		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new MeetingException(MeetingConstants.KEYLOADFAILED, e);
		}
	}

	/*
	 * This function is creating the customer meeting
	 */

	@Override
	public void preview(Context context) throws SystemException,
			ApplicationException {
		try {
			createCustomerMeeting(context);
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new MeetingException(MeetingConstants.KEYLOADFAILED, e);
		}
	}

	/**
	 * This function is called to load the meeting master data
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void loadMeeting(Context context) throws SystemException,
			ApplicationException {
		try {

			MeetingDAO meetingdao = (MeetingDAO) getDAO(context.getPath());
			meetingdao.loadMeetingMaster(context);
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new MeetingException(MeetingConstants.KEYLOADFAILED, e);
		}
	}

}
