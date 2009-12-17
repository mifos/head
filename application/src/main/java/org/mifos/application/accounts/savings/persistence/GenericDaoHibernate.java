/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
package org.mifos.application.accounts.savings.persistence;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

/**
 *
 */
public class GenericDaoHibernate implements GenericDao {

    private HibernateUtil hibernateUtil;

    @SuppressWarnings("unchecked")
    @Override
    public List<? extends Object> executeNamedQueryWithResultTransformer(final String queryName,
            final Map<String, ?> nameQueryParameters, final Class<?> className) {

        try {
            Session session = getHibernateUtil().getSessionTL();
            Query query = session.getNamedQuery(queryName).setResultTransformer(Transformers.aliasToBean(className));
            setParametersInQuery(query, nameQueryParameters);
            return query.list();
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public Object executeUniqueResultNamedQueryWithResultTransformer(final String queryName,
            final Map<String, ?> nameQueryParameters, final Class<?> className) {

        try {
            Session session = getHibernateUtil().getSessionTL();
            Query query = session.getNamedQuery(queryName).setResultTransformer(Transformers.aliasToBean(className));
            setParametersInQuery(query, nameQueryParameters);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<? extends Object> executeNamedQuery(final String queryName, final Map<String, ?> queryParameters) {

        try {
            Session session = getHibernateUtil().getSessionTL();
            Query query = session.getNamedQuery(queryName);

            setParametersInQuery(query, queryParameters);
            return query.list();
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public Object executeUniqueResultNamedQuery(final String queryName,
            final Map<String, ?> queryParameters) {

        try {
            Session session = getHibernateUtil().getSessionTL();
            Query query = session.getNamedQuery(queryName);

            setParametersInQuery(query, queryParameters);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
    }

    private void setParametersInQuery(final Query query, final Map<String, ?> queryParameters) {

        final Set<String> queryParamKeys = queryParameters.keySet();
        for (String queryParamKey : queryParamKeys) {
            query.setParameter(queryParamKey, queryParameters.get(queryParamKey));
        }
    }

    private HibernateUtil getHibernateUtil() {
        if (null == hibernateUtil) {
            hibernateUtil = new HibernateUtil();
        }
        return hibernateUtil;
    }
}
