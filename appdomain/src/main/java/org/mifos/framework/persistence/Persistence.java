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
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.transform.Transformers;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.util.helpers.Param;
import org.mifos.framework.exceptions.ConnectionNotFoundException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is intended to be replaced by <b>SessionPersistence</b> which
 * intentionally has a very similar set of methods (many subclasses can be moved
 * over just by changing what they inherit from, with no further changes).
 */
public abstract class Persistence {

    private static final Logger logger = LoggerFactory.getLogger(Persistence.class);

    /**
     * @deprecated - move away from using this as starts transaction but doesn't not commit..
     */
    @Deprecated
    public Object createOrUpdate(final Object object) throws PersistenceException {
        try {
            Session session = StaticHibernateUtil.getSessionTL();
            // FIXME remove this and fix the code
            StaticHibernateUtil.startTransaction();
            session.saveOrUpdate(object);
            if (StaticHibernateUtil.getInterceptor().isAuditLogRequired()) {
                StaticHibernateUtil.getInterceptor().createChangeValueMap(object);
            }
        } catch (Exception e) { // including exceptions *not* from hibernate!
            throw new PersistenceException(e);
        }

        return object;
    }

    public Object save(final Object object) throws PersistenceException {
        try {
            Session session = StaticHibernateUtil.getSessionTL();
            session.saveOrUpdate(object);
            if (StaticHibernateUtil.getInterceptor().isAuditLogRequired()) {
                StaticHibernateUtil.getInterceptor().createChangeValueMap(object);
            }
        } catch (Exception e) { // including exceptions *not* from hibernate!
            throw new PersistenceException(e);
        }

        return object;
    }

    public Session getSession() {
        return StaticHibernateUtil.getSessionTL();
    }

    public void delete(final Object object) throws PersistenceException {
        Session session = StaticHibernateUtil.getSessionTL();
        try {
            StaticHibernateUtil.startTransaction();
            session.delete(object);
        } catch (Exception he) {
            throw new PersistenceException(he);
        }
    }

    /**
     * assumes transactionality is controlled by caller of method.
     */
    public void deleteInTransaction(final Object object) throws PersistenceException {
        Session session = StaticHibernateUtil.getSessionTL();
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
    public List executeNamedQuery(final String queryName, final Map queryParameters) throws PersistenceException {
        try {
            Query query = createdNamedQuery(queryName);
            query.setProperties(queryParameters);
            return query.list();
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    public Query createdNamedQuery(final String queryName) {
        Session session = StaticHibernateUtil.getSessionTL();
        Query query = session.getNamedQuery(queryName);
        logger.debug(
                "The query object for the query with the name  " + queryName + " has been obtained");
        return query;
    }

    public Object execUniqueResultNamedQuery(final String queryName, final Map queryParameters) throws PersistenceException {
        try {
            Query query = null;
            Session session = StaticHibernateUtil.getSessionTL();
            if (null != session) {
                query = session.getNamedQuery(queryName);
                logger.debug(
                        "The query object for the query with the name  " + queryName + " has been obtained");
            }

            query.setProperties(queryParameters);
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
            Session session = StaticHibernateUtil.getSessionTL();
            query = session.getNamedQuery(queryName).setResultTransformer(Transformers.aliasToBean(clazz));
            query.setProperties(queryParameters);
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
            Session session = StaticHibernateUtil.getSessionTL();
            Query query = session.getNamedQuery(queryName).setResultTransformer(Transformers.aliasToBean(clazz));
            query.setProperties(queryParameters);
            return query.list();
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
    }

    public Object executeUniqueHqlQuery(final String hqlQuery) {

        Session session = StaticHibernateUtil.getSessionTL();
        final Query query = session.createQuery(hqlQuery);
        return query.uniqueResult();
    }

    public List executeNonUniqueHqlQuery(final String hqlQuery) {

        Session session = StaticHibernateUtil.getSessionTL();
        final Query query = session.createQuery(hqlQuery);
        return query.list();
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
            return StaticHibernateUtil.getSessionTL().get(clazz, persistentObjectId);
        } catch (HibernateException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Deleagtes to {@link Session#load(Class, Serializable)}.
     */
    public Object loadPersistentObject(final Class clazz, final Serializable persistentObjectId) {
        return StaticHibernateUtil.getSessionTL().load(clazz, persistentObjectId);
    }

    protected Param typeNameValue(final String type, final String name, final Object value) {
        return new Param(type, name, value);
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
