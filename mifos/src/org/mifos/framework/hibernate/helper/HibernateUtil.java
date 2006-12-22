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

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.mifos.framework.components.audit.util.helpers.AuditInterceptor;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ConnectionNotFoundException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.hibernate.factory.HibernateSessionFactory;

public class HibernateUtil {

	private static final SessionFactory sessionFactory;

	private static final ThreadLocal<SessionHolder> threadLocal = new ThreadLocal<SessionHolder>();

	static {
		try {
			sessionFactory = HibernateSessionFactory.getSessionFactory();
		} catch (Throwable ex) {
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).error(
					"Initial SessionFactory creation failed.", false, null, ex);

			throw new ExceptionInInitializerError(ex);
		}
	}

	/**
	 * Open a new hibernate session.
	 */
	public static Session openSession() throws HibernateProcessException {
		try {
			return sessionFactory.openSession();
		} catch (HibernateException e) {
			throw new HibernateProcessException(
					HibernateConstants.FAILED_OPENINGSESSION, e);
		}
	}

	/**
	 * Close a session.  Do nothing if the session is null or already closed.
	 */
	public static void closeSession(Session session)
			throws HibernateProcessException {
		try {
			if (session != null && session.isOpen()) {
				session.close();
			}

		} catch (HibernateException e) {
			throw new HibernateProcessException(
					HibernateConstants.FAILED_CLOSINGSESSION, e);
		}

	}

	/**
	 * Return the hibernate session factory
	 */
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * Return the current hibernate session for this thread.  If this
	 * thread doesn't have one, create a new one.
	 */
	public static Session getSessionTL() {
		try {
			if (threadLocal.get() == null) {
				AuditInterceptor auditInterceptor = new AuditInterceptor();
				SessionHolder sessionHolder = new SessionHolder(sessionFactory
						.openSession(auditInterceptor));
				sessionHolder.setInterceptor(auditInterceptor);
				threadLocal.set(sessionHolder);
			}
		} catch (HibernateException he) {
			throw new ConnectionNotFoundException(he);
		}
		return threadLocal.get().getSession();

	}

	public static AuditInterceptor getInterceptor() {
		return getSessionHolder().getInterceptor();
	}

	/**
	 * Begin a transaction and store it in a thread-local variable,
	 * or return the currently open transaction if there is one.
	 * The result is that the transaction will remain open until
	 * a call to {@link #commitTransaction()} or {@link #rollbackTransaction()}.
	 * Calling this method instead of just creating a hibernate
	 * Transaction via the session is probably not a good idea (see
	 * an example of the latter at 
	 * TestObjectPersistence#update(Session, org.mifos.framework.business.PersistentObject)
	 */
	public static Transaction startTransaction() {
		Transaction transaction = getSessionHolder().getTransaction();
		if (transaction == null) {
			transaction = getSessionHolder().getSession().beginTransaction();
			getSessionHolder().setTranasction(transaction);
		}
		return transaction;
	}

	public static Transaction getTransaction() {
		if (getSessionHolder() == null)
			return null;

		return getSessionHolder().getTransaction();
	}

	public static void closeSession() {
		SessionHolder sessionHolder = getSessionHolder();
		if (sessionHolder != null) {
			Session session = sessionHolder.getSession();
			session.close();
			session = null;
			getSessionHolder().setInterceptor(null);
			threadLocal.set(null);
		}

	}

	public static void closeandFlushSession() {
		SessionHolder sessionHolder = getSessionHolder();
		if (sessionHolder != null) {
			Session session = sessionHolder.getSession();
			session.flush();
			session.close();
			session = null;
			getSessionHolder().setInterceptor(null);
			threadLocal.set(null);
		}

	}

	private static SessionHolder getSessionHolder() {
		if (null == threadLocal.get()) {
			// need to log to indicate that the session is being invoked when
			// not present

		}
		return threadLocal.get();
	}

	public static boolean isSessionOpen() {
		if (getSessionHolder() == null)
			return false;

		return true;
	}

	public static void commitTransaction() {
		if (getTransaction() != null) {
			getTransaction().commit();
			getSessionHolder().setTranasction(null);
		}

	}

	public static void rollbackTransaction() {
		if (getTransaction() != null) {
			getTransaction().rollback();
			getSessionHolder().setTranasction(null);
		}

	}

}
