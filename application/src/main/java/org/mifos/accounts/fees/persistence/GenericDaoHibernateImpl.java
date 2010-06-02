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

package org.mifos.accounts.fees.persistence;

import java.io.Serializable;

import org.hibernate.Query;
import org.mifos.accounts.fees.persistence.GenericDao;
import java.util.Collection;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class GenericDaoHibernateImpl<T, ID extends Serializable>
    extends HibernateDaoSupport implements GenericDao<T, ID>, QueryExecutor<T> {

    private Class<T> type;

    public GenericDaoHibernateImpl(Class<T> type) {
        this.type = type;

    }

    @Override
    public void add(T newInstance) {
        getHibernateTemplate().save(newInstance);
    }

    @Override
    public ID create(T newInstance) {
        return (ID) getHibernateTemplate().save(newInstance);
    }

    @Override
    public void delete(T persistentObject) {
        getHibernateTemplate().delete(persistentObject);
    }

    @Override
    public T getDetails(ID id) {
        return getHibernateTemplate().get(type, id);
    }

    @Override
    public List<T> getDetailsAll() {
        return getHibernateTemplate().find("from " + type.getName());
    }

    @Override
    public void saveOrUpdateAll(Collection<T> c) {
        getHibernateTemplate().saveOrUpdateAll(c);
    }

    @Override
    public void update(T transientObject) {
        getHibernateTemplate().update(transientObject);
    }

    @Override
    public List<T> execFindQuery(String qryMethodName, Object[] queryArgs) {
        //final Query namedQuery = prepareQuery(qryMethodName, queryArgs);
        //System.out.println("executing named query:" + namedQuery.getQueryString());
        //return namedQuery.list();
        return getHibernateTemplate().findByNamedQuery(type.getSimpleName() + "." + qryMethodName, queryArgs);
    }

    protected Query prepareQuery(String qryMethodName, Object[] queryArgs) {
        final String queryName = type.getSimpleName() + "." + qryMethodName;
        //getHibernateTemplate().getSessionFactory().getCurrentSession() was replacement for getSession() method.

        final Query namedQuery = getHibernateTemplate().getSessionFactory().getCurrentSession().getNamedQuery(queryName);
        String[] namedParameters = namedQuery.getNamedParameters();
        if (namedParameters.length==0) {
            if(queryArgs != null) {
                for(int i = 0; i < queryArgs.length; i++) {
                    Object arg = queryArgs[i];
                    namedQuery.setParameter(i, arg);
                }
            }
        } else {
            if(queryArgs != null) {
                for(int i = 0; i < queryArgs.length; i++) {
                    Object arg = queryArgs[i];
                    if(arg instanceof Collection) {
                        namedQuery.setParameterList(namedParameters[i], (Collection) arg);
                    } else {
                        namedQuery.setParameter(namedParameters[i], arg);
                    }
                }
            }
        }
        return namedQuery;
    }

}
