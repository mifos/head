/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.framework.persistence;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.transform.Transformers;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.util.helpers.Param;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ConnectionNotFoundException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

/**
 * This class is intended to be replaced by <b>SessionPersistence</b> which
 * intentionally has a very similar set of methods (many subclasses can be moved
 * over just by changing what they inherit from, with no further changes).
 */
public abstract class Persistence {
    private HibernateUtil hibernateUtil;

    public HibernateUtil getHibernateUtil() {
        if (null == hibernateUtil) {
            hibernateUtil = new HibernateUtil();
        }
        return hibernateUtil;
    }

    public Object createOrUpdate(final Object object) throws PersistenceException {
        try {
            Session session = getHibernateUtil().getSessionTL();
            getHibernateUtil().startTransaction();
            session.saveOrUpdate(object);
            if (getHibernateUtil().getInterceptor().isAuditLogRequired()) {
                getHibernateUtil().getInterceptor().createChangeValueMap(object);
            }
        } catch (Exception e) { // including exceptions *not* from hibernate!
            throw new PersistenceException(e);
        }

        return object;
    }

    public Session getSession() {
        return getHibernateUtil().getSessionTL();
    }

    public void delete(final Object object) throws PersistenceException {
        Session session = getHibernateUtil().getSessionTL();
        try {
            getHibernateUtil().startTransaction();
            session.delete(object);
        } catch (Exception he) {
            throw new PersistenceException(he);
        }
    }

    /**
     * This method takes the name of a named query to be executed as well as a
     * list of parameters that the query uses. It assumes the session is open.
     */
    public List executeNamedQuery(final String queryName, final Map queryParameters) throws PersistenceException {
        try {
            Query query = createdNamedQuery(queryName);

            setParametersInQuery(query, queryName, queryParameters);
            return runQuery(query);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    public List runQuery(final Query query) {
        return query.list();
    }

    public Query createdNamedQuery(final String queryName) {
        Session session = getHibernateUtil().getSessionTL();
        Query query = session.getNamedQuery(queryName);
        MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
                "The query object for the query with the name  " + queryName + " has been obtained");
        return query;
    }

    public Object execUniqueResultNamedQuery(final String queryName, final Map queryParameters) throws PersistenceException {
        try {
            Query query = null;
            Session session = getHibernateUtil().getSessionTL();
            if (null != session) {
                query = session.getNamedQuery(queryName);
                MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
                        "The query object for the query with the name  " + queryName + " has been obtained");
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

    public Object execUniqueResultNamedQueryWithResultTransformer(final String queryName,
            final Map<String, ?> queryParameters,
            final Class<?> clazz) {
        try {
            Query query = null;
            Session session = getHibernateUtil().getSessionTL();
            query = session.getNamedQuery(queryName).setResultTransformer(Transformers.aliasToBean(clazz));
            setParametersInQuery(query, queryName, queryParameters);
            query.setResultTransformer(Transformers.aliasToBean(clazz));
            return query.uniqueResult();
        } catch (GenericJDBCException gje) {
            throw new ConnectionNotFoundException(gje);
        } catch (Exception e) {
            throw new MifosRuntimeException(e);

        }
    }

    @SuppressWarnings("unchecked")
    public List executeNamedQueryWithResultTransformer(final String queryName,
            final Map<String, ?> queryParameters, final Class<?> clazz) {
        try {
            Session session = getHibernateUtil().getSessionTL();
            Query query = session.getNamedQuery(queryName).setResultTransformer(Transformers.aliasToBean(clazz));
            setParametersInQuery(query, queryName, queryParameters);
            return query.list();
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
    }

    public Object executeUniqueHqlQuery(final String hqlQuery) {

        Session session = getHibernateUtil().getSessionTL();
        final Query query = session.createQuery(hqlQuery);
        return query.uniqueResult();
    }

    public List executeNonUniqueHqlQuery(final String hqlQuery) {

        Session session = getHibernateUtil().getSessionTL();
        final Query query = session.createQuery(hqlQuery);
        return query.list();
    }

    public static void setParametersInQuery(final Query query, final String queryName, final Map queryParameters)
            throws PersistenceException {

        MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
                "Check if query object and queryParameters are not null for query with name  " + queryName);
        try {
            if (null != queryParameters) {

                MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
                        "Obtaining keys for the parameter map query with name  " + queryName);
                Set queryParamKeys = queryParameters.keySet();
                Iterator queryParamIterator = queryParamKeys.iterator();

                MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
                        "Iterating over the keys for query with name  " + queryName);
                while (queryParamIterator.hasNext()) {
                    String key = queryParamIterator.next().toString();
                    MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
                            "The parameter key  for query with name  " + queryName + " is " + key);
                    Object value = queryParameters.get(key);
                    query.setParameter(key, value);
                }
            }
        } catch (Exception e) {
            throw new PersistenceException(e);
        }

    }

    /**
     * Delegates to {@link Session#get(Class, Serializable)}.
     */
    public Object getPersistentObject(final Class clazz, final Serializable persistentObjectId) throws PersistenceException {
        // keep current unit tests happy, they get confused with an
        // IllegalArgumentException (thrown if get() is given a null ID, below)
        // TODO: get rid of this. The default handling for null IDs of
        // get() (throwing that IllegalArgumentException) should be fine.
        if (null == persistentObjectId) {
            throw new PersistenceException("persistentObjectId is required for fetch");
        }

        // TODO: it is probably cleaner to NOT catch the HibernateException
        // since it is a RuntimeException. Let's eventually make this method
        // more like loadPersistentObject(), below.
        try {
            return getHibernateUtil().getSessionTL().get(clazz, persistentObjectId);
        } catch (HibernateException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Deleagtes to {@link Session#load(Class, Serializable)}.
     */
    public Object loadPersistentObject(final Class clazz, final Serializable persistentObjectId) {
        return getHibernateUtil().getSessionTL().load(clazz, persistentObjectId);
    }

    protected Param typeNameValue(final String type, final String name, final Object value) {
        return new Param(type, name, value);
    }

    public void initialize(final Object object) {
        Hibernate.initialize(object);
    }

    protected Integer getCountFromQueryResult(final List queryResult) {
        int count = 0;
        if (queryResult.size() > 0 && queryResult.get(0) != null) {
            count = ((Number) queryResult.get(0)).intValue();
        }
        return count;
    }

    protected BigDecimal getCalculateValueFromQueryResult(final List queryResult) {
        return queryResult.size() > 0 && queryResult.get(0) != null ? BigDecimal.valueOf(((Number) queryResult.get(0))
                .doubleValue()) : null;
    }
}
