/**
 
 * CustomerNotesDAO.java    version: xxx
 
 
 
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

package org.mifos.application.customer.dao;



import java.util.List;

import org.hibernate.Session;

import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.util.valueobjects.Context;

import org.mifos.application.customer.util.helpers.NotesHelper;
import org.mifos.application.customer.util.valueobjects.CustomerNote;

/**
 * This class denotes the DAO layer for the CustomerNote module.
 * @author navitas
 */
public class CustomerNoteDAO extends DAO {
	
	/**
	 * This method is called to create the CustomerNote.
	 * It persists the data entered for the CustomerNote in the database.
	 * @param context instance of Context
	 * @throws ApplicationException
	 * @throws SystemException
	 */	
	public void create(Context context)throws ApplicationException,SystemException{
		CustomerNote customerNote=(CustomerNote)context.getValueObject();
		super.create(context);
	}
	
	/**
	 * This method adds customerNote to the database provided the customerNote object and hibernate session.
	 * @param session the current hibernate session
	 * @param customerNote
	 * @throws SystemException
	 */
	public void addNotes(Session session,CustomerNote customerNote)throws SystemException{
		session.save(customerNote);
		MifosLogManager.getLogger(LoggerConstants.CUSTOMERNOTELOGGER).debug("After saving note in the database.");
	}

	/**
	 * This method returns count number of latest notes, when count is passed in parameter
	 * @param count is the number of notes to be retrieved 
	 * @param customerId, customerId of the customer
	 * @return List list of custoemet note
	 * @throws ApplicationException
	 * @throws SystemException
	 */	
	public List<CustomerNote> getLatestNotesByCount(int count, Integer customerId)throws ApplicationException,SystemException{
		NotesHelper notesHelper = new NotesHelper();
		return notesHelper.getLatestNotes(count,customerId);
	}
	
	/**
	 * This method to get all notes for the given customer
	 * @param customerId, customerId of the customer
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public QueryResult get(Integer customerId)throws ApplicationException,SystemException{
		return new NotesHelper().getAllLatestNotes(customerId);
	}
}
