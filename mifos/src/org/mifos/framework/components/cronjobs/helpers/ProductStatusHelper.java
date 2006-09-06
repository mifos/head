/**

 * ProductStatusHelper.java    version: 1.0

 

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

import java.sql.Timestamp;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.components.cronjobs.valueobjects.Task;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class ProductStatusHelper extends TaskHelper {

	@Override
	public void execute(long timeInMillis) {
		Session session=null;
		Transaction txn=null;
		String hqlUpdate=null;
		Query query=null;
		try{
			session = HibernateUtil.getSession();
			txn=session.beginTransaction();
			
			hqlUpdate = "update PrdOffering p set p.prdStatus =:activeLoanStatus where p.prdType=:loan and p.startDate=:currentDate";
			query= session.createQuery(hqlUpdate);
			query.setShort("activeLoanStatus", PrdStatus.LOANACTIVE.getValue());
			query.setShort("loan",ProductType.LOAN.getValue());
			query.setDate("currentDate", new Date(timeInMillis));
			query.executeUpate();
			
		    hqlUpdate = "update PrdOffering p set p.prdStatus =:inActiveLoanStatus where p.prdType=:loan and p.endDate=:currentDate";
			query= session.createQuery(hqlUpdate);
			query.setShort("inActiveLoanStatus", PrdStatus.LOANINACTIVE.getValue());
			query.setShort("loan",ProductType.LOAN.getValue());
			query.setDate("currentDate", new Date(timeInMillis));
			query.executeUpate();
			
			hqlUpdate = "update PrdOffering p set p.prdStatus =:activeSavingStatus where p.prdType=:saving and p.startDate=:currentDate";
			query= session.createQuery(hqlUpdate);
			query.setShort("activeSavingStatus", PrdStatus.SAVINGSACTIVE.getValue());
			query.setShort("saving",ProductType.SAVINGS.getValue());
			query.setDate("currentDate", new Date(timeInMillis));
			query.executeUpate();
			
			hqlUpdate = "update PrdOffering p set p.prdStatus =:inActiveSavingStatus where p.prdType=:saving and p.endDate=:currentDate";
			query= session.createQuery(hqlUpdate);
			query.setShort("inActiveSavingStatus", PrdStatus.SAVINGSINACTIVE.getValue());
			query.setShort("saving",ProductType.SAVINGS.getValue());
			query.setDate("currentDate", new Date(timeInMillis));
			query.executeUpate();
	
			txn.commit(); 
		
		}
		catch(HibernateProcessException hpe){
			hpe.printStackTrace();
			txn.rollback();
		}catch(HibernateException he){
			he.printStackTrace();
			txn.rollback();
		}catch(IllegalArgumentException iae){
			iae.printStackTrace();
			txn.rollback();
		}catch(Exception e){
			e.printStackTrace();
			txn.rollback(); 
		}
		finally
		{
			try{
			    HibernateUtil.closeSession(session);
			}catch(HibernateProcessException hpe){
				hpe.printStackTrace();
			}catch(Exception e){
				e.printStackTrace(); 
			}
		}
	}

	
	
	
}
