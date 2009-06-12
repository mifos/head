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

package org.mifos.framework.hibernate.helper;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.HibernateSearchException;

/**
 * This is the interface that is returned on a search operation. Search would
 * typically result in a set of search result objects , these search result
 * objects would be obtained through hibernate scroll for pagination in the
 * front end , the associate hibernate session would be held in this object , a
 * call to close from the front end on this interface would result in the
 * hibernate session object getting closed.
 */
public interface QueryResult {
    /**
     * Return the Session used for query
     * 
     * @return Session
     */
    public Session getSession() throws HibernateProcessException;

    /**
     * Set the query which will be used for query execution
     * 
     * @param Query
     */
    public void executeQuery(Query query) throws HibernateSearchException;

    /**
     * Returns the requested set of search result objects based on the
     * pagination at the front end.
     * 
     * @return List
     */
    public List get(int position, int noOfObjects) throws HibernateSearchException;

    /**
     * Set the query inputs which will be used for query execution
     * 
     * @param queryInputs
     */
    public void setQueryInputs(QueryInputs queryInputs) throws HibernateSearchException;

    /**
     * This is invoked on the query result to indicate the end of search result
     * view at the front end , the associated hibernate session would be closed
     */
    public void close() throws HibernateProcessException;

    /**
     * Returns the size
     * 
     * @return int
     */
    public int getSize() throws HibernateSearchException;

}
