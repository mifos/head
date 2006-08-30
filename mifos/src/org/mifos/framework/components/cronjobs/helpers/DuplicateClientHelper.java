/**

 * DuplicateClientHelper.java    version: 1.0



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

package org.mifos.framework.components.cronjobs.helpers;

import java.util.Iterator;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.components.cronjobs.valueobjects.TempTable;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class DuplicateClientHelper extends TaskHelper {

	@Override
	public void execute(long timeInMillis) {
		Session session=null;
		Transaction txn=null;
		TempTable temp=null;
		int count=0;
		try{
			//When Government Id is null
			session = HibernateUtil.getSession();
			txn=session.beginTransaction();
			
			String hql="select a.customerId from Customer a, Customer b" +
					   		"  where a.customerId!=b.customerId and " +
					   		"  a.governmentId is null and " +
					   		"  b.governmentId is null and " +
					   		"  a.displayName=b.displayName and " +
					   		"  a.dateOfBirth=b.dateOfBirth";

			Iterator itr = session.createQuery(hql).iterate() ;

			while(itr.hasNext()){
				hql="update Customer A set A.statusId=6 where A.customerId=:id";
				session.createQuery(hql).setInteger("id",(Integer)itr.next()).executeUpate();
			}

			hql="select a.customerId from Customer a, Customer b" +
	   				"  where a.customerId!=b.customerId and " +
	   				"  a.governmentId is not null and " +
	   				"  b.governmentId is not null and " +
	   				"  a.governmentId=b.governmentId";

			itr = session.createQuery(hql).iterate() ;

			while(itr.hasNext()){
				hql="update Customer A set A.statusId=6 where A.customerId=:id";
				session.createQuery(hql).setInteger("id",(Integer)itr.next()).executeUpate();
			}

			txn.commit();
		}catch(HibernateProcessException e){
			e.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		finally
		{
			try{
			    HibernateUtil.closeSession(session);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean isTaskAllowedToRun() {
		return true;
	}

}
