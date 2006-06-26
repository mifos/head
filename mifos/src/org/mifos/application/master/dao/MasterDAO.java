/**

 * MasterDAO.java    version: 1.0



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

package org.mifos.application.master.dao;

import java.util.List;
import java.util.Locale;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.master.util.valueobjects.EntityMaster;
import org.mifos.application.master.util.valueobjects.LookUpMaster;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class MasterDAO {

	public MasterDAO() {
		super();
	}

	public EntityMaster getLookUpEntity(String entityName,Short localeId) throws ApplicationException,SystemException {
		Session session=null;
		try {
			session = HibernateUtil.getSession();

			Query queryEntity = session.getNamedQuery("masterdata.entityvalue");
			queryEntity.setString("entityType",entityName);
			queryEntity.setShort("localeId",localeId);

			EntityMaster entity =  (EntityMaster)queryEntity.uniqueResult();

			entity.setLookUpValues(lookUpValue(entityName,localeId,session));

		    return entity;
		}catch(HibernateProcessException hbe) {
			throw new SystemException();
		}catch(Exception e) {
			throw new ApplicationException(e);
		}finally {
			HibernateUtil.closeSession(session);
		}
	}

	private List<LookUpMaster> lookUpValue(String entityName,Short localeId,Session session)  {
		Query queryEntity = session.getNamedQuery("masterdata.entitylookupvalue");
	  	queryEntity.setString("entityType",entityName);
	  	queryEntity.setShort("localeId",localeId);
	  	List<LookUpMaster> entityList =  queryEntity.list();
	  	return entityList;
	}

	public short getLocaleId(Locale locale){
		/*Session session=null;
		try {
			session = HibernateUtil.getSession();
			Query queryEntity = session.getNamedQuery("masterdata.localevalue");
			/*queryEntity.setShort("localeId",localeId);
			EntityMaster entity =  (EntityMaster)queryEntity.uniqueResult();
			entity.setLookUpValues(lookUpValue(entityName,localeId,session));
		    return entity;
		}catch(HibernateProcessException hbe) {
			throw new SystemException();
		}catch(Exception e) {
			throw new ApplicationException(e);
		}finally {
			HibernateUtil.closeSession(session);
		}*/

		return Short.valueOf("1");


	}

	public EntityMaster getLookUpEntity(String entityName,Short localeId,String classPath , String column)
			throws ApplicationException,SystemException {
		Session session=null;
		try {
			session = HibernateUtil.getSession();

			Query queryEntity = session.getNamedQuery("masterdata.entityvalue");
		  	queryEntity.setString("entityType",entityName);
		  	queryEntity.setShort("localeId",localeId);

		  	EntityMaster entity =  (EntityMaster)queryEntity.uniqueResult();
			entity.setLookUpValues(lookUpValue(entityName,localeId,classPath,column,session));

			return entity;
		}catch(HibernateProcessException hbe) {
			throw new SystemException();
		}catch(Exception e) {
			throw new ApplicationException(e);
		}finally {
			HibernateUtil.closeSession(session);
		}
	}



	private List<LookUpMaster> lookUpValue(String entityName,Short localeId,String classPath,String column,
				Session session){

		String q="select new org.mifos.application.master.util.valueobjects.LookUpMaster(mainTable.";
		String q2=" ,lookup.lookUpId,lookupvalue.lookUpValue) from org.mifos.application.master.util.valueobjects.LookUpValue lookup,org.mifos.application.master.util.valueobjects.LookUpValueLocale lookupvalue,";
		String q3=" mainTable where mainTable.lookUpId =lookup.lookUpId and lookup.lookUpEntity.entityType =? and lookup.lookUpId=lookupvalue.lookUpId and lookupvalue.localeId=?";

		q=q+column+q2+classPath+q3;

		Query queryEntity = session.createQuery(q);
		queryEntity.setString(0,entityName);
		queryEntity.setShort(1,localeId);
		 List<LookUpMaster> entityList =  queryEntity.list();
		return entityList;
	}
}
