/**

 * PersonnelNotesDAO.java    version: xxx



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

package org.mifos.application.personnel.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mifos.framework.dao.DAO;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.personnel.util.valueobjects.PersonnelNotes;

/**
 * This class denotes the DAO layer for the PersonnelNote.
 * @author ashishsm
 *
 */
public class PersonnelNotesDAO extends DAO {


	/**
	 * This will create a new note in the personnelnotes table.
	 * @param context instance of Context
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public void create(Context context)throws ApplicationException,SystemException{
		super.create(context);
	}

	/**
	 * This will retreive n number of latest notes entered for a particular user, where n is passed in as parameter.
	 * @param count the number of notes to be retrieved
	 * @param personnelId the user for which the notes is to be retrieved
	 * @return list of notes
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public List  getLatestNotesByCount(int count, Short personnelId) throws ApplicationException,SystemException
	{
		Session session = null;
		List<PersonnelNotes> notesList=new ArrayList<PersonnelNotes>();

	try
	{
    		session = HibernateUtil.getSession();
	 		Query query = session.getNamedQuery(NamedQueryConstants.GET_PERSONNEL_NOTES);
	 		query.setInteger("personnelId",personnelId);
	 		Iterator<PersonnelNotes> iterNotes = query.iterate();

            PersonnelNotes note = null;
	 		while(iterNotes.hasNext() && count-- >0)
	 		{		note = iterNotes.next();
	 				note.getComment();
	 				note.getOfficer().getDisplayName();
	 				note.getCommentDate();
	 				notesList.add(note);
			}
		}catch(HibernateProcessException  hpe)	{
				throw new ApplicationException(hpe);
		}finally{
 			HibernateUtil.closeSession(session);
		}

	return notesList;
   	}

	/**
	 * This will retreive all notes entered for a particular personnel.
	 * @param personnelId the user for which the notes is to be retrieved
	 * @return instance of QueryResult
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public QueryResult getAllNotes(short personnelId)throws ApplicationException,SystemException{
		QueryResult notesResult=null;
			try{
				Session session=null;
				 notesResult = QueryFactory.getQueryResult("NotesSearch");
				 session = notesResult.getSession();
		 		Query query= session.getNamedQuery(NamedQueryConstants.GET_PERSONNEL_NOTES);
		 		query.setInteger("personnelId",personnelId);

		 		notesResult.executeQuery(query);


		 	}
			catch(HibernateProcessException  hpe)
			{		// System.out.println("in notes search error "+hpe.toString());

			}
	      return notesResult;
	}

}
