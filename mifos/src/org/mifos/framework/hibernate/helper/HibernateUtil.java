/**

 * HibernateUtil.java    version: 1.0



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
package org.mifos.framework.hibernate.helper;

import org.hibernate.*;
import org.hibernate.Interceptor;

import org.mifos.framework.hibernate.factory.HibernateSessionFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.components.audit.util.helpers.AuditInterceptor; 
import org.mifos.framework.components.audit.util.helpers.LogInfo;



public class HibernateUtil {

	/**
	 *
	 */
	private static final SessionFactory sessionFactory;
	
	private static final ThreadLocal<SessionHolder> threadLocal = new ThreadLocal<SessionHolder>();


	static {
		try {

			sessionFactory = HibernateSessionFactory.getSessionFactory();
		} catch (Throwable ex) {
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).error("Initial SessionFactory creation failed.", false, null, ex);

			throw new ExceptionInInitializerError(ex);
		}
	}

	/**
	 * Method returns a hibernate session object which it obtains from SessionFactory.
	 * @return Session
	 * @throws HibernateProcessException
	 */
	public static Session getSession() throws HibernateProcessException
	{
		try
		{

				return sessionFactory.openSession();



		}catch(HibernateException e){
			throw new HibernateProcessException(HibernateConstants.FAILED_OPENINGSESSION,e);
		}
	}

	/**
	 * Method that closes the hibernate session object passed to it as parameter
	 * @param Session
	 * @throws HibernateProcessException
	 */
	public static void closeSession(Session session) throws HibernateProcessException
	{
		try
		{
			if(session != null && session.isOpen() )
			{
				session.close();
				session = null;
			}


		}
		catch(HibernateException e)
		{
			throw new HibernateProcessException(HibernateConstants.FAILED_CLOSINGSESSION,e);
		}

	}

	/**
	 * Method that returns the hibernate session factory
	 * @param Session
	 * @throws HibernateProcessException
	 */

	public static SessionFactory getSessionFactory()
	{

		return sessionFactory;
	}

	
	
	/** 
	   *  
	   *   Method returns a hibernate session object which it obtains from SessionFactory.  
	   *   It also registers the interceptor with the session and puts the interceptor in the 
	   *   threadLocal variable so that a reference to interceptor can be obtained later.
	   *   @return Session
	   *   @throws HibernateProcessException
	   *  
	   */
	public static Session getSessionWithInterceptor(LogInfo info) throws HibernateProcessException
	{  
			Session session = null;
			try
			{
				info.setInitialValueMap(); 
				Interceptor interceptor=new AuditInterceptor(info); 
				session = sessionFactory.openSession(interceptor);
				return session;
			}catch(HibernateException e)
			{
				throw new HibernateProcessException(HibernateConstants.FAILED_OPENINGSESSION,e);
			}
	  }

	/** 
	   *  
	   *   Method returns a hibernate session object which it obtains from SessionFactory.  
	   *   It also registers the interceptor with the session and puts the interceptor in the 
	   *   threadLocal variable so that a reference to interceptor can be obtained later.
	   *   @return Session
	   *   @throws HibernateProcessException
	   *  
	   */
	public static Session getSessionWithInterceptor(LogInfo[] info) throws HibernateProcessException
	{  
			Session session = null;
			try
			{
				for(int i=0;i<info.length;i++){
					info[i].setInitialValueMap();
				}
				Interceptor interceptor=new AuditInterceptor(info); 
				session = sessionFactory.openSession(interceptor);
				return session;
			}catch(HibernateException e)
			{
				throw new HibernateProcessException(HibernateConstants.FAILED_OPENINGSESSION,e);
			}
	  }

	public static Session getSessionTL() {
		if (threadLocal.get() == null) {
			threadLocal.set(new SessionHolder(sessionFactory.openSession()));
		}
		return threadLocal.get().getSession();
	}

	public static Transaction startTransaction() {
		Transaction transaction = getSessionHolder().getTransaction();
		if(transaction == null)
		{
			transaction = getSessionHolder().getSession().beginTransaction();
			getSessionHolder().setTranasction(transaction);
		}
		return transaction;
	}

	public static Transaction getTransaction() {
		if(getSessionHolder() == null)
			return null;
		
		return getSessionHolder().getTransaction();
	}

	public static void closeSession() {
		SessionHolder sessionHolder = getSessionHolder();
		if(sessionHolder != null){
			Session s = sessionHolder.getSession();
			s.close();
			s=null;
			threadLocal.set(null);
		}
	}
	public static void closeandFlushSession() {
		SessionHolder sessionHolder = getSessionHolder();
		if(sessionHolder != null){
			Session s = sessionHolder.getSession();
			s.flush();
			s.close();
			s=null;
			threadLocal.set(null);
		}
	}
	private static SessionHolder getSessionHolder() {
		if (null == threadLocal.get()) {
			// need to log to indicate that the session is being invoked when not present
			
		}
		return threadLocal.get();
	}
	
	public static boolean isSessionOpen()
	{
		if(getSessionHolder()== null)
			return false;
		
		return true;
	}

	public static void commitTransaction() {
		if(getTransaction()!=null){
			getTransaction().commit();
			getSessionHolder().setTranasction(null);
		}
	}

	public static void rollbackTransaction() {
		if(getTransaction()!=null){
			getTransaction().rollback();
			getSessionHolder().setTranasction(null);
		}
	}

}

