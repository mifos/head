/**

 * GroupActionForm.java    version: 1.0



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

package org.mifos.application.customer.business.handlers;

import java.sql.Date;

import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ResourceNotCreatedException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.util.valueobjects.Context;

import org.mifos.application.configuration.util.helpers.PathConstants;
import org.mifos.application.customer.dao.CustomerNoteDAO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.util.helpers.LinkParameters;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.valueobjects.CustomerNote;
import org.mifos.application.personnel.util.valueobjects.Personnel;

/**
 *  This is the business processor for the Customer Note module.
 *  It takes care of handling all the business logic related to customer note
 *  @author navitas
 */
public class CustomerNoteBusinessProcessor extends MifosBusinessProcessor {

	/**
     *  This method handles notes preview.
     *  It sets the comment date as todays date and set the name of the user who is entering the note in notes object
     *  to show on preview page.
     *  @param context an instance of Context
     *  @throws ApplicationException
     *  @throws SystemException
     */
	public void previewInitial(Context context)throws ApplicationException{
		try{
			CustomerNote customerNote = (CustomerNote)context.getValueObject();
			customerNote.setCommentDate(new Date(new java.util.Date().getTime()));
			//TODO:pick personnel id and personnel name from usercontext
			Personnel p = new Personnel();
			p.setPersonnelId(context.getUserContext().getId());
			p.setDisplayName(context.getUserContext().getName());
			customerNote.setPersonnel(p);
			customerNote.setPersonnelId(context.getUserContext().getId());
		}catch(Exception e ){
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION);
		}
	}

	/**
     *  This method is used to retrieve all notes for a customer.
     *  @param context an instance of Context
     *  @throws ApplicationException
     *  @throws SystemException
     */
	public void getNotesSearch(Context context)throws ApplicationException, SystemException{
		// System.out.println("------in get notes search");
		CustomerNoteDAO notesDAO =null ;
		try{
			notesDAO = (CustomerNoteDAO)getDAO(PathConstants.CUSTOMER_NOTE_PATH);
			LinkParameters params = (LinkParameters)context.getBusinessResults(CustomerConstants.LINK_VALUES);
			QueryResult result=notesDAO.get(params.getCustomerId());
			context.setSearchResult(result);
//			SearchObject searchObject = new SearchObject();
//			searchObject.addToSearchNodeMap("result","notes");
//			context.setSearchObject(searchObject);
		}catch(ResourceNotCreatedException rnce ){
			throw rnce;
		}
	}

}
