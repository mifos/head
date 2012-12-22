/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.transform.Transformers;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.util.helpers.Param;
import org.mifos.framework.components.audit.util.helpers.AuditInterceptor;
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
public abstract class LegacyGenericDao {

    private static final Logger logger = LoggerFactory.getLogger(LegacyGenericDao.class);

    /**
     * @deprecated - move away from using this as starts transaction but doesn't not commit..
     */
    @Deprecated
    public Object createOrUpdate(final Object object) throws PersistenceException {
        try {
            StaticHibernateUtil.startTransaction();
            getSession().saveOrUpdate(object);
            AuditInterceptor interceptor = (AuditInterceptor) StaticHibernateUtil.getInterceptor();
            if (interceptor.isAuditLogRequired()) {
                interceptor.createChangeValueMap(object);
            }
        } catch (HibernateException e) {
            throw new PersistenceException(e);
        }
        return object;
    }

    public Object save(final Object object) throws PersistenceException {
        try {
            getSession().saveOrUpdate(object);
            AuditInterceptor interceptor = (AuditInterceptor) StaticHibernateUtil.getInterceptor();
            if (interceptor.isAuditLogRequired()) {
                interceptor.createChangeValueMap(object);
            }
        } catch (HibernateException e) {
            throw new PersistenceException(e);
        }

        return object;
    }

    public Session getSession() {
        return StaticHibernateUtil.getSessionTL();
    }

    public void delete(final Object object) throws PersistenceException {
        try {
            StaticHibernateUtil.startTransaction();
            getSession().delete(object);
        } catch (Exception he) {
            throw new PersistenceException(he);
        }
    }

    /**
     * This method takes the name of a named query to be executed as well as a
     * list of parameters that the query uses. It assumes the session is open.
     */
    @SuppressWarnings("unchecked")
    public List executeNamedQuery(final String queryName, final Map<String, ?> queryParameters) throws PersistenceException {
        try {
            Query query = createdNamedQuery(queryName);
            query.setProperties(queryParameters);
            return query.list();
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    public Query createdNamedQuery(final String queryName) {
        Query query = getSession().getNamedQuery(queryName);
        logger.debug("The query object for the query with the name  " + queryName + " has been obtained");
        return query;
    }

    public Object execUniqueResultNamedQuery(final String queryName, final Map<String, ?> queryParameters)
            throws PersistenceException {
        try {
            Query query = getSession().getNamedQuery(queryName);
            logger.debug("The query object for the query with the name  " + queryName + " has been obtained");
            query.setProperties(queryParameters);
            return query.uniqueResult();
        } catch (GenericJDBCException gje) {
            throw new ConnectionNotFoundException(gje);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    public Object execUniqueResultNamedQueryWithoutFlush(final String queryName, final Map<String, ?> queryParameters)
            throws PersistenceException {
        try {
            Session sess = getSession();
            sess.setFlushMode(FlushMode.MANUAL);
        	Query query = getSession().getNamedQuery(queryName);
            logger.debug("The query object for the query with the name  " + queryName + " has been obtained");
            query.setProperties(queryParameters);
            Object returnObject = query.uniqueResult();
            sess.setFlushMode(FlushMode.AUTO);
            return returnObject;
        } catch (GenericJDBCException gje) {
            throw new ConnectionNotFoundException(gje);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Object> T execUniqueResultNamedQueryWithResultTransformer(final String queryName,
            final Map<String, ?> queryParameters,
            final Class<T> clazz) {
        try {
            Query query = getSession().getNamedQuery(queryName).setResultTransformer(Transformers.aliasToBean(clazz));
            query.setProperties(queryParameters);
            query.setResultTransformer(Transformers.aliasToBean(clazz));
            return (T) query.uniqueResult();
        } catch (GenericJDBCException gje) {
            throw new ConnectionNotFoundException(gje);
        } catch (Exception e) {
            throw new MifosRuntimeException(e);

        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Object> List<T> executeNamedQueryWithResultTransformer(final String queryName,
            final Map<String, ?> queryParameters, final Class<T> clazz) {
        try {
            Query query = getSession().getNamedQuery(queryName);
            query.setResultTransformer(Transformers.aliasToBean(clazz));
            query.setProperties(queryParameters);
            return query.list();
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
    }

    // Hugo Technologies
    public int executeNamedQueryForUpdate(final String queryName, final Map<String, ?> queryParameters) throws PersistenceException {
        try {
		StaticHibernateUtil.startTransaction();
            Query query = createdNamedQuery(queryName);
            query.setProperties(queryParameters);
            int result = query.executeUpdate();
            StaticHibernateUtil.commitTransaction();
            return result;

        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    public Object executeUniqueHqlQuery(final String hqlQuery) {
        return getSession().createQuery(hqlQuery).uniqueResult();
    }

    public List<?> executeNonUniqueHqlQuery(final String hqlQuery) {
        return getSession().createQuery(hqlQuery).list();
    }

    @SuppressWarnings("unchecked")
    public <T extends Object> T getPersistentObject(final Class<T> clazz, final Serializable persistentObjectId) throws PersistenceException {
        if (null == persistentObjectId) {
            throw new PersistenceException("persistentObjectId is required for fetch");
        }
        try {
            return (T) getSession().get(clazz, persistentObjectId);
        } catch (HibernateException e) {
            throw new PersistenceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Object> T loadPersistentObject(final Class<T> clazz, final Serializable persistentObjectId) {
        return (T) getSession().load(clazz, persistentObjectId);
    }

    protected Param typeNameValue(final String type, final String name, final Object value) {
        return new Param(type, name, value);
    }

    protected Integer getCountFromQueryResult(final List<?> queryResult) {
        int count = 0;
        if (queryResult.size() > 0 && queryResult.get(0) != null) {
            count = ((Number) queryResult.get(0)).intValue();
        }
        return count;
    }

    protected BigDecimal getCalculateValueFromQueryResult(final List<?> queryResult) {
        return queryResult.size() > 0 && queryResult.get(0) != null ? BigDecimal.valueOf(((Number) queryResult.get(0))
                .doubleValue()) : null;
    }
}
