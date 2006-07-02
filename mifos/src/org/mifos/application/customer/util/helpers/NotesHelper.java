/**

 * NotesHelper.java    version: 1.0



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

package org.mifos.application.customer.util.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.util.valueobjects.CustomerNote;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.QueryResultDTOImpl;
/**
 * @author navitas
 *
 */
public class NotesHelper {

	public static java.util.List  getLatestNotes(int count, Integer customerId) throws ApplicationException,SystemException
	{
		Session session = null;
		List<CustomerNote> notesList=new ArrayList<CustomerNote>();

	try
	{
    		session = HibernateUtil.getSession();
	 		Query q = session.getNamedQuery(NamedQueryConstants.GET_NOTES);
	 		q.setInteger("customerId",customerId);
	 		Iterator<CustomerNote> iterNotes = q.iterate();

			// System.out.println("in notes search ");
            CustomerNote note = null;
	 		while(iterNotes.hasNext() && count-- >0)
	 		{		note = iterNotes.next();
	 				note.getComment();
	 				note.getPersonnel().getDisplayName();
	 				note.getCommentDate();
	 				notesList.add(note);
			}
		}catch(HibernateProcessException  hpe)
		{		// System.out.println("in notes search error "+hpe.toString());
				throw new ApplicationException(hpe);
		}finally{
 			HibernateUtil.closeSession(session);
		}

	return notesList;
    }

	public QueryResult getAllLatestNotes(Integer customerId)throws ApplicationException,SystemException{
		QueryResult notesResult=null;
			try{
				Session session=null;
				 notesResult = QueryFactory.getQueryResult("NotesSearch");
				 session = notesResult.getSession();
		 		Query query= session.getNamedQuery(NamedQueryConstants.GET_NOTES);
		 		query.setInteger("customerId",customerId);

		 		notesResult.executeQuery(query);


		 	}
			catch(HibernateProcessException  hpe)
			{		// System.out.println("in notes search error "+hpe.toString());

			}
	      return notesResult;
	}
}
