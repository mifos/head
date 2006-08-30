/**

 * AccountNotesBusinessProcesor.java    version: xxx 

 

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

package org.mifos.application.accounts.business.handler;

import java.sql.Date;

import org.mifos.application.accounts.dao.AccountNotesDAO;
import org.mifos.application.accounts.exceptions.AccountNotesException;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.valueobjects.AccountNotes;
import org.mifos.application.configuration.util.helpers.PathConstants;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ResourceNotCreatedException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This is the business processor for the AccountNotes. 
 * It takes care of handling all the business logic for the AccountNotes module
 */
public class AccountNotesBusinessProcessor extends MifosBusinessProcessor {

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
			AccountNotes personnelNote = (AccountNotes)context.getValueObject();
			personnelNote.setCommentDate(new Date(new java.util.Date().getTime()));
			Personnel p = new Personnel();
			//get the personnelId from usercontext
			p.setPersonnelId(context.getUserContext().getId());
			p.setDisplayName(context.getUserContext().getName());
			personnelNote.setOfficer(p);
			personnelNote.setOfficerId(context.getUserContext().getId());
		}catch(Exception e ){
			throw new AccountNotesException(AccountConstants.UNKNOWN_EXCEPTION);
		}
	}

	/** 
     *  This method is used to retrieve all notes for a personnel. 
     *  It calls a function on the DAO to retrieve the notes and set it in the context.
     *  @param context an instance of Context
     *  @throws ApplicationException
     *  @throws SystemException
     */
	public void getAccountNotes(Context context)throws ApplicationException, SystemException{
		try{
			AccountNotes accountNote = (AccountNotes)context.getValueObject();
			QueryResult result=getAccountNotesDAO().getAllNotes(accountNote.getAccountId());
			context.setSearchResult(result);
		}catch(ApplicationException ae ){
			throw ae;
		}catch(SystemException se ){
			throw se;
		}catch(Exception e){
			throw new AccountNotesException(AccountConstants.UNKNOWN_EXCEPTION);
		}
	
	}
	/** 
     * This method returns instance of AccountNotesDAO
     * @return AccountNotesDAO instance
     * @throws SystemException
     */  
  	private AccountNotesDAO getAccountNotesDAO()throws SystemException{
  		AccountNotesDAO accountNotesDAO=null;
  		try{
  			accountNotesDAO = (AccountNotesDAO)getDAO(PathConstants.ACCOUNT_NOTES_PATH);
  		}catch(ResourceNotCreatedException rnce){
  		}
  		return accountNotesDAO;
  	}
}
