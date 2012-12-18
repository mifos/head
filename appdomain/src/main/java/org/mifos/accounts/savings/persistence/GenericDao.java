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

import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 */
public interface GenericDao {

    Object executeUniqueResultNamedQueryWithResultTransformer(String namedQuery, Map<String, ?> nameQueryParameters,
            Class<?> className);

    List<? extends Object> executeNamedQueryWithResultTransformer(String namedQuery,
            Map<String, ?> nameQueryParameters, Class<?> className);

    List<? extends Object> executeNamedQuery(final String queryName, final Map<String, ?> queryParameters);
    
    List<? extends Object> executeNamedQueryWithOffset(final String queryName, final Map<String, ?> queryParameters,int position,int noOfObjects);

    Object executeUniqueResultNamedQuery(final String queryName, final Map<String, ?> queryParameters);

    void createOrUpdate(Object entity);

    Query createQueryForUpdate(String hql);

    void delete(Object entity);

    // NOTE: This is temporary, should be removed once we move to spring managed transaction
    Session getSession();

    Iterator<? extends Object> executeNamedQueryIterator(String queryName, Map<String, ?> queryParameters);

    <T> T initializeAndUnproxy(T var);
    
    int executeNamedQueryDelete(final String queryName, final Map<String, ?> queryParameters);

    void update(Object entity);
}
