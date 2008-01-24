package org.mifos.framework.persistence;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.exception.GenericJDBCException;
import org.mifos.application.customer.util.helpers.Param;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ConnectionNotFoundException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

/**
 * This class is intended to be replaced by {@link SessionPersistence}
 * which intentionally has a very similar set of methods (many subclasses
 * can be moved over just by changing what they inherit from, with
 * no further changes).
 */
public abstract class Persistence {

	public Connection getConnection() {
		return HibernateUtil.getSessionTL().connection();
	}

	public Object createOrUpdate(Object object) throws PersistenceException {
        try {
            Session session = HibernateUtil.getSessionTL();
            HibernateUtil.startTransaction();
            session.saveOrUpdate(object);
            if (HibernateUtil.getInterceptor().isAuditLogRequired()) {
                HibernateUtil.getInterceptor().createChangeValueMap(object);
            }
        } catch (Exception hibernateException) {
            throw new PersistenceException(hibernateException);
        }

        return object;
	}
	
	public Session getSession() {
		return HibernateUtil.getSessionTL();
	}

	public void delete(Object object) throws PersistenceException {
		Session session = HibernateUtil.getSessionTL();
		try {
			HibernateUtil.startTransaction();
			session.delete(object);
		} catch (Exception he) {
			throw new PersistenceException(he);
		}
	}	

	/**
	 * This method takes the name of a named query to be executed as well as a
	 * list of parameters that the query uses. It assumes the session is open.
	 */
	public List executeNamedQuery(String queryName, Map queryParameters)
			throws PersistenceException {
		try {
			Session session = HibernateUtil.getSessionTL();
			Query query = session.getNamedQuery(queryName);
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER)
					.debug(
							"The query object for the query with the name  "
									+ queryName + " has been obtained");

			setParametersInQuery(query, queryName, queryParameters);
			return query.list();
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}

	public Object execUniqueResultNamedQuery(String queryName,
			Map queryParameters) throws PersistenceException {
		try {
			Query query = null;
			Session session = HibernateUtil.getSessionTL();
			if (null != session) {
				query = session.getNamedQuery(queryName);
				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER)
						.debug(
								"The query object for the query with the name  "
										+ queryName + " has been obtained");
			}

			setParametersInQuery(query, queryName, queryParameters);
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

	public static void setParametersInQuery(Query query, String queryName,
			Map queryParameters) throws PersistenceException {

		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
				"Check if query object and queryParameters are not null for query with name  "
						+ queryName);
		try {
			if (null != queryParameters) {

				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER)
						.debug(
								"Obtaining keys for the parameter map query with name  "
										+ queryName);
				Set queryParamKeys = queryParameters.keySet();
				Iterator queryParamIterator = queryParamKeys.iterator();

				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER)
						.debug(
								"Iterating over the keys for query with name  "
										+ queryName);
				while (queryParamIterator.hasNext()) {
					String key = queryParamIterator.next().toString();
					MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER)
							.debug(
									"The parameter key  for query with name  "
											+ queryName + " is " + key);
					query.setParameter(key, queryParameters.get(key));
				}
			}
		} catch (Exception e) {
			throw new PersistenceException(e);
		}

	}

	/**
	 * Deleagtes to {@link Session#get(Class, Serializable)}.
	 */
	public Object getPersistentObject(Class clazz,
			Serializable persistentObjectId) throws PersistenceException {
		// keep current unit tests happy, they get confused with an
		// IllegalArgumentException (thrown if get() is given a null ID, below)
		// TODO: get rid of this. The default handling for null IDs of
		// get() (throwing that IllegalArgumentException) should be fine.
		if (null == persistentObjectId)
			throw new PersistenceException("persistentObjectId is required for fetch");
		
		// TODO: it is probably cleaner to NOT catch the HibernateException
		// since it is a RuntimeException. Let's eventually make this method
		// more like loadPersistentObject(), below.
		try {
			return HibernateUtil.getSessionTL().get(clazz, persistentObjectId);
		}
		catch (HibernateException e) {
			throw new PersistenceException(e);
		}
	}

	/**
	 * Deleagtes to {@link Session#load(Class, Serializable)}.
	 */
	public Object loadPersistentObject(Class clazz,
			Serializable persistentObjectId) {
		return HibernateUtil.getSessionTL().load(clazz, persistentObjectId);
	}

	protected Param typeNameValue(String type, String name, Object value) {
		return new Param(type, name, value);
	}
	
	public void initialize(Object object) {
		Hibernate.initialize(object);
	}
}
