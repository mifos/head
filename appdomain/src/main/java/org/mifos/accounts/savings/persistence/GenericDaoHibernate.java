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
package org.mifos.accounts.savings.persistence;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.transform.Transformers;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.components.audit.util.helpers.AuditInterceptor;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

/**
 *
 */
public class GenericDaoHibernate implements GenericDao {

    @SuppressWarnings("unchecked")
    @Override
    public final List<? extends Object> executeNamedQueryWithResultTransformer(final String queryName,
            final Map<String, ?> nameQueryParameters, final Class<?> className) {

        try {
            Session session = getSession();
            Query query = session.getNamedQuery(queryName).setResultTransformer(Transformers.aliasToBean(className));
            query.setProperties(nameQueryParameters);
            return query.list();
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public final Object executeUniqueResultNamedQueryWithResultTransformer(final String queryName,
            final Map<String, ?> nameQueryParameters, final Class<?> className) {

        try {
            Session session = getSession();
            Query query = session.getNamedQuery(queryName).setResultTransformer(Transformers.aliasToBean(className));
            query.setProperties(nameQueryParameters);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public final List<? extends Object> executeNamedQuery(final String queryName, final Map<String, ?> queryParameters) {

        try {
            Session session = getSession();
            Query query = session.getNamedQuery(queryName);
            query.setProperties(queryParameters);
            return query.list();
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Iterator<? extends Object> executeNamedQueryIterator(final String queryName, final Map<String, ?> queryParameters) {

        try {
            Session session = getSession();
            Query query = session.getNamedQuery(queryName);
            query.setProperties(queryParameters);
            return query.iterate();
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public final Object executeUniqueResultNamedQuery(final String queryName,
            final Map<String, ?> queryParameters) {

        try {
            Session session = getSession();
            Query query = session.getNamedQuery(queryName);
            query.setProperties(queryParameters);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public void delete(final Object entity) {
        Session session = getSession();
        try {
            session.delete(entity);
        } catch (Exception he) {
            throw new MifosRuntimeException(he);
        }
    }

    @Override
    public final void createOrUpdate(final Object entity) {
        try {
            Session session = getSession();
            session.saveOrUpdate(entity);
            AuditInterceptor interceptor = (AuditInterceptor) StaticHibernateUtil.getInterceptor();
            if (interceptor.isAuditLogRequired()) {
                interceptor.createChangeValueMap(entity);
            }
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public final Query createQueryForUpdate(String hql) {
        Session session = StaticHibernateUtil.getSessionTL();
        return session.createQuery(hql);
    }

    @Override
    // NOTE: This is temporary, should be removed once we move to spring managed transaction
    public Session getSession() {
        return StaticHibernateUtil.getSessionTL();
    }
    
    // useful if casting is required to make sure we don't try to cast a proxied object.
    @Override
    public <T> T initializeAndUnproxy(T var) {
        if (var == null) {
            return null;
        }

        Hibernate.initialize(var);
        if (var instanceof HibernateProxy) {
            var = (T) ((HibernateProxy) var).getHibernateLazyInitializer().getImplementation();
        }
        return var;
    }    
}
