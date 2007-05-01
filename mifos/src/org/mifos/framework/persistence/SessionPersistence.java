package org.mifos.framework.persistence;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.GenericJDBCException;
import org.mifos.application.customer.util.helpers.Param;
import org.mifos.framework.components.audit.util.helpers.AuditInterceptor;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ConnectionNotFoundException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.SessionHolder;

public class SessionPersistence {
	
	private SessionHolder sessionHolder;
	
	public SessionPersistence(SessionHolder sessionHolder) {
		this.sessionHolder = sessionHolder;
		if (sessionHolder == null) {
			throw new NullPointerException("session holder is required");
		}
	}
	
	public SessionPersistence() {
		this(HibernateUtil.getOrCreateSessionHolder());
	}
	
	protected Session getSession() {
		return sessionHolder.getSession();
	}
	
	protected AuditInterceptor getInterceptor() {
		return sessionHolder.getInterceptor();
	}
	
	protected SessionHolder getSessionHolder() {
		return sessionHolder;
	}
	

	public Connection getConnection() {
		return getSession().connection();
	}

	public Object createOrUpdate(Object object) throws PersistenceException {
		return createOrUpdate(getSession(), getInterceptor(), object);
	}

	public Object createOrUpdate(Session session, 
			AuditInterceptor interceptor, Object object) 
	throws PersistenceException {
		Transaction tx = session.beginTransaction();
		try {
			session.saveOrUpdate(object);
			if (interceptor.isAuditLogRequired()) {
				interceptor.createChangeValueMap(object);
			}
			tx.commit();
		} catch (Exception hibernateException) {
			tx.rollback();
			throw new PersistenceException(hibernateException);
		}
		return object;
	}

	public void delete(Object object) throws PersistenceException {
		try {
			sessionHolder.startTransaction();
			getSession().delete(object);
		} catch (Exception he) {
			throw new PersistenceException(he);
		}
	}

	/**
	 * This method takes the name of a named query to be executed as well as a
	 * list of parameters that the query uses. It assumes the session is open.
	 * 
	 * It is preferred to just call getSession().getNamedQuery(queryName)
	 * and go from there.  This method doesn't really buy us much.
	 */
	public List executeNamedQuery(String queryName, Map queryParameters)
			throws PersistenceException {
		try {
			Session session = getSession();
			Query query = session.getNamedQuery(queryName);
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER)
					.debug(
							"The query object for the query with the name  "
									+ queryName + " has been obtained");

			Persistence.setParametersInQuery(query, queryName, queryParameters);
			return query.list();
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}

	/**
	 * Wrapper around Hibernate's uniqueResult() method.
	 * 
	 * It is preferred to just call getSession().getNamedQuery(queryName)
	 * and go from there.  This method doesn't really buy us much.
	 */
	public Object execUniqueResultNamedQuery(String queryName,
			Map queryParameters) throws PersistenceException {
		try {
			Query query = null;
			Session session = getSession();
			if (null != session) {
				query = session.getNamedQuery(queryName);
				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER)
						.debug(
								"The query object for the query with the name  "
										+ queryName + " has been obtained");
			}

			Persistence.setParametersInQuery(query, queryName, queryParameters);
			if (null != query) {
				return query.uniqueResult();
			}
			return null;
		} catch (GenericJDBCException gje) {
			throw new ConnectionNotFoundException(gje);
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}

	public Object getPersistentObject(Class clazz, Short persistentObjectId)
			throws PersistenceException {
		try {
			return getSession().get(clazz, persistentObjectId);
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}

	public Object getPersistentObject(Class clazz, Integer persistentObjectId)
			throws PersistenceException {
		try {
			return getSession().get(clazz, persistentObjectId);
		} catch (Exception he) {
			throw new PersistenceException(he);
		}
	}

	protected Param typeNameValue(String type, String name, Object value) {
		return new Param(type, name, value);
	}
	
	public void initialize(Object object) {
		Hibernate.initialize(object);
	}


}
