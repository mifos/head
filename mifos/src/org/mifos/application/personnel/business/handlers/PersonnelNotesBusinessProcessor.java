/**

 * PersonnelNotesBusinessProcesor.java    version: xxx

 

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

import org.mifos.application.configuration.util.helpers.PathConstants;
import org.mifos.application.personnel.dao.PersonnelNotesDAO;
import org.mifos.application.personnel.exceptions.PersonnelException;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.application.personnel.util.valueobjects.PersonnelNotes;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ResourceNotCreatedException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This is the business processor for the PersonnelNotes. 
 * It takes care of handling all the business logic for the PersonnelNotes module
 */
public class PersonnelNotesBusinessProcessor extends MifosBusinessProcessor {
	/**An insatnce of the logger which is used to log statements */
	private  MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.PERSONNEL_LOGGER);
	/** 
     *  This method handles notes preview.
     *  It sets the comment date as todays date and set the name of the user who is entering the note in notes object
     *  to show on preview page.  
     *  @param context an instance of Context
     *  @throws ApplicationException
     *  @throws SystemException
     */     
	public void previewInitial(Context context)throws ApplicationException,SystemException{
		try{
			logger.info("in previewInitial() method of PersonnelNotesBusinessProcessor");
			PersonnelNotes personnelNote = (PersonnelNotes)context.getValueObject();
			personnelNote.setCommentDate(new Date(new java.util.Date().getTime()));
			Personnel p = new Personnel();
			//get the personnelId from usercontext
			p.setPersonnelId(context.getUserContext().getId());
			p.setDisplayName(context.getUserContext().getName());
			personnelNote.setOfficer(p);
			personnelNote.setOfficerId(context.getUserContext().getId());
		}catch(Exception e ){
			throw new PersonnelException(PersonnelConstants.UNKNOWN_EXCEPTION,e);
		}
	}

	/** 
     *  This method is used to retrieve all notes for a personnel. 
     *  It calls a function on the DAO to retrieve the notes and set it in the context.
     *  @param context an instance of Context
     *  @throws ApplicationException
     *  @throws SystemException
     */
	public void getPersonnelNotes(Context context)throws ApplicationException, SystemException{
		try{
			
			PersonnelNotes personnelNote = (PersonnelNotes)context.getValueObject();
			QueryResult result=getPersonnelNotesDAO().getAllNotes(personnelNote.getPersonnelId());
			context.setSearchResult(result);
		}catch(ApplicationException ae ){
			throw ae;
		}catch(SystemException se ){
			throw se;
		}catch(Exception e){
			throw new PersonnelException(PersonnelConstants.UNKNOWN_EXCEPTION,e);
		}
	
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
}
