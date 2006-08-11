/**
 
 * DAO.java    version: 1.0
 
 
 
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

package org.mifos.framework.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


import javax.naming.InitialContext;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;


import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.hibernate.helper.HibernateConstants;
import org.mifos.framework.util.valueobjects.ValueObject;

import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.dao.helpers.DataTypeConstants;
import org.mifos.framework.dao.helpers.MasterDataRetriever;

import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ConcurrencyException;
import org.mifos.framework.exceptions.DBConnectionFailedException;

import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;


import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;



/**
 * All DAO's in the system extend from this <code>DAO</code> class.
 * It has methods with default implementations for create/update on hibernate, these methods can be overridden if required.
 */
public  class DAO
{
	
	
	/**
	 * It updates the ValueObject instance passed in the Context object in the database. This method gets the hibernate session , starts a transaction , calls update on hibernate session and then commits and closes the hibernate session , this method can be over-ridden for any other different behaviour for update
	 * @param context
	 * @throws HibernateProcessException
	 */
	public void update(org.mifos.framework.util.valueobjects.Context context) throws ApplicationException,SystemException
	{
		
		Transaction tx = null;
		Session session = null;
		try {
			session = getHibernateSession();
			tx = session.beginTransaction();
			
			session.update(context.getValueObject());
			
			tx.commit();
			
		}
		catch (StaleObjectStateException sse)
		{
			tx.rollback();
			throw new ConcurrencyException(HibernateConstants.VERSION_MISMATCH,sse);
		}
		catch (Exception e)
		{
			if(tx != null)
				tx.rollback();
			throw new ApplicationException(HibernateConstants.UPDATE_FAILED,e);
			
		} finally 
		{
			HibernateUtil.closeSession(session);
		}
	}
	
	/**
	 * It deletes the entity from the database.This should be overridden in case one requires a soft delete functionality.
	 * @param context
	 * @throws HibernateProcessException
	 */
	public void delete(org.mifos.framework.util.valueobjects.Context context) throws ApplicationException,SystemException
	{
		
		Transaction tx = null;
		Session session = null;
		try {
			session = getHibernateSession();
			tx = session.beginTransaction();
			
			session.delete(context.getValueObject());
			
			tx.commit();
			
		}
		catch (StaleObjectStateException sse)
		{
			tx.rollback();
			throw new ConcurrencyException(HibernateConstants.VERSION_MISMATCH,sse);
		}
		catch (Exception e)
		{
			if(tx != null)
				tx.rollback();
			throw new ApplicationException(HibernateConstants.DELETE_FAILED,e);
			
		}
		finally
		{
			HibernateUtil.closeSession(session);
		}
	}
	
	/**
	 * It creates the ValueObject instance passed in the Context object in the database. This method gets the hibernate session , starts a transaction , calls create on hibernate session and then commits and closes the hibernate session , this method can be over-ridden for any other different behaviour for create
	 * @param context
	 * @throws HibernateProcessException
	 */
	public void create(org.mifos.framework.util.valueobjects.Context context) throws ApplicationException,SystemException
	{
		Transaction tx = null;
		Session session = null;
		try
		{
			session = getHibernateSession();
			tx = session.beginTransaction();
			
			session.save(context.getValueObject());
			
			tx.commit();
			
		}
		catch (Exception e) 
		{
			if(tx != null)
				tx.rollback();
			throw new ApplicationException(HibernateConstants.CREATE_FAILED,e);
			
		} 
		finally
		{
			HibernateUtil.closeSession(session);
		}
		
	}
	/**
	 * This method returns a new hibernate session , this can be used by the extending class to get a hibernate session
	 * @throws HibernateProcessException 
	 *
	 * @throws HibernateProcessException
	 */
	
	public Session getHibernateSession() throws HibernateProcessException
	{
		return HibernateUtil.getSession();
	}
	
	/**
	 * This returns a connection object.
  	 * This is primarily for the logger which uses this connection
  	 * to log statements in the database.
	 * @return Connection
  	 */
	public static Connection getConnection()  throws DBConnectionFailedException{
		Connection con = null;
  		try {
			con = HibernateUtil.getSession().connection();
		} catch(Exception e){
  			throw new DBConnectionFailedException(e);
  		}
  		return con;
  	}

	public MasterDataRetriever getMasterDataRetriever()throws HibernateProcessException{
		return new MasterDataRetriever();
	}
	
	
	/**
	 * This method executes the query specified by opening a new session and later closes the session after 
	 * executing the query.
	 * @param queryName
	 * @return
	 * @throws SystemException
	 */
	public static List executeNamedQuery(String queryName,HashMap queryParameters)throws SystemException{
		Session session = HibernateUtil.getSession();
		List returnList = null;
		try{
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("The name of the query to be executed is " + queryName);
			returnList = executeNamedQuery(queryName,queryParameters,session);
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("After successfully executing the query with the name " + queryName);
		}catch(HibernateException he){
			throw new SystemException(ExceptionConstants.SYSTEMEXCEPTION,he);
		}finally{
			HibernateUtil.closeSession(session);
		}
		return returnList;
	}
	
	/**
	 * This method takes a session a query name and executes the same without closing the session after executing the query.
	 * @param queryName
	 * @param session
	 * @return
	 * @throws HibernateException
	 */
	public static List executeNamedQuery(String queryName,HashMap queryParameters,Session session)throws HibernateException{
		Query query = null;
		List returnList = null;
		if(null != session){
			query = session.getNamedQuery(queryName);
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("The query object for the query with the name  " + queryName + " has been obtained");
		}
		
		setParametersInQuery(query,queryName, queryParameters);
		if(null != query){
			returnList = query.list();
		}
		return returnList;
	}
	
	
	/**
	 * This methods sets the parameters to the query passed.
	 * @param query
	 * @param queryName
	 */
	public  static void setParametersInQuery(Query query,String queryName,HashMap queryParameters) {
		
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Check if query object and queryParameters are not null for query with name  " + queryName );
		if(null != query && null != queryParameters){
			
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Obtaining keys for the parameter map query with name  " + queryName );
			Set queryParamKeys = queryParameters.keySet();
			Iterator queryParamIterator = queryParamKeys.iterator();  
			
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Iterating over the keys for query with name  " + queryName );
			while(queryParamIterator.hasNext()){
				String key = queryParamIterator.next().toString();
				
				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("The parameter key  for query with name  " + queryName  + " is " + key);
				query.setParameter(key, queryParameters.get(key));
			}
			
			
		}
		
	}
	
	
	
	/**
	 * This returns entity corresponding to the name and primary key passed.It uses session.get provided by Hibernate to load the object and hence
	 * will not initialize the associated entities.If that behaviour is desired use the other overloaded method.
	 * @param entityName- Fully Qualified class name of the entity which is to be retireved.
	 * @param primaryKey - An object representing the primary key.
	 * @param dataType - An enum which indicates the data type of the pk.
	 * @return
	 * @throws SystemException
	 */
	public static ValueObject getEntity(String entityName ,Object primaryKey,DataTypeConstants dataType)throws SystemException,ApplicationException{
		Session session =null;
		ValueObject entity = null;
		
		if(null == entityName || null == primaryKey){
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Entity name or primary key is null hence returning null");
			return entity;
		}
		try{
			session = HibernateUtil.getSession();
			entity =getEntity(entityName,  primaryKey, dataType,session);
		}catch(SystemException se){
			throw se;
		}finally{
			HibernateUtil.closeSession(session);
		}
		return entity;
	}
	
	/**
	 * This returns entity corresponding to the name and primary key passed.It uses session.get provided by Hibernate to load the object and hence
	 * will not initialize the associated entities but since the session is open that can be done by the calling method.
	 * This method will not close the session neither the database connection.
	 * @param entityName- Fully Qualified class name of the entity which is to be retireved.
	 * @param primaryKey - An object representing the primary key.
	 * @param dataType - An enum which indicates the data type of the pk.
	 * @param session  - Hibernate session which has open database connection.
	 * @return
	 * @throws SystemException
	 */
	public static ValueObject getEntity(String entityName ,Object primaryKey,DataTypeConstants dataType,Session session)throws ApplicationException{
		
		ValueObject entity = null;
		if(null != session && null != entityName && null != primaryKey){
			try{
				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Before obtaining the class object for entity with name " + entityName);
				Class clazz = Class.forName(entityName);
				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("The data type of the pk is  " + dataType);
				switch(dataType){
				
				case Integer:
					entity = (ValueObject)session.get(clazz, new Integer(primaryKey.toString()));
					MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("After successfully retrieving the value object " );
					break;
				case Short:
					entity = (ValueObject)session.get(clazz, new Short(primaryKey.toString()));
					MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("After successfully retrieving the value object " );
					break;
				}
		  }catch(HibernateException he){
				he.printStackTrace();
				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).error(" class object for the given entity name could not be created   " ,false,null,he);
				throw new ApplicationException(ExceptionConstants.SYSTEMEXCEPTION,he);
			} catch (ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).error(" class object for the given entity name could not be created   " ,false,null,cnfe);
				throw new ApplicationException(ExceptionConstants.SYSTEMEXCEPTION,cnfe);
			}catch(ClassCastException cce){
				//this could arise if version no is not an integer
				cce.printStackTrace();
				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).error(" Version no column in not an integer   " ,false,null,cce);
				throw new ApplicationException(ExceptionConstants.SYSTEMEXCEPTION,cce);
			}finally{
				
			}
			
		}
		return entity;
	}
	
	
}
