package org.mifos.framework.persistence;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public abstract class Persistence {

	protected Connection getConnection() {
		return null;
	}

	protected Session openSession() throws PersistenceException {
		try {
			return HibernateUtil.getSession();
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}

	protected void closeSession() throws PersistenceException {
		try {
			HibernateUtil.closeSession();
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}

	public Object createOrUpdate(Object object) throws PersistenceException {
		Session session = HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		try {
			session.saveOrUpdate(object);
		} catch (Exception he) {
			throw new PersistenceException(he);
		}
		return object;
	}

	public void delete(Object object) throws PersistenceException {
		Session session = HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		try {
			session.delete(object);
		} catch (Exception he) {
			throw new PersistenceException(he);
		}
	}

	/**
	 * This method takes the name of a named query to be executed as well as a
	 * list of parameters that the query uses. It assumes the session is open.
	 */
	public static List executeNamedQuery(String queryName, Map queryParameters)
			throws PersistenceException {
		try {
			Query query = null;
			Session session = HibernateUtil.getSessionTL();
			List returnList = null;
			if (null != session) {
				query = session.getNamedQuery(queryName);
				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER)
						.debug(
								"The query object for the query with the name  "
										+ queryName + " has been obtained");
			}

			setParametersInQuery(query, queryName, queryParameters);
			if (null != query) {
				returnList = query.list();
			}
			return returnList;
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}

	public static Object execUniqueResultNamedQuery(String queryName,
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
			if (null != query && null != queryParameters) {

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

	public Object getPersistentObject(Class clazz, Short persistentObjectId)
			throws PersistenceException {
		try {
			return HibernateUtil.getSessionTL().get(clazz, persistentObjectId);
		} catch (Exception he) {
			throw new PersistenceException(he);
		}
	}

	public Object getPersistentObject(Class clazz, Integer persistentObjectId)
			throws PersistenceException {
		try {
			return HibernateUtil.getSessionTL().get(clazz, persistentObjectId);
		} catch (Exception he) {
			throw new PersistenceException(he);
		}
	}

}
