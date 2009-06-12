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

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.HibernateSearchException;

/**
 * This is the class that is returned on a search operation. Search would
 * typically result in a set of search result objects , these search result
 * objects would be obtained through hibernate scroll for pagination in the
 * front end , the associate hibernate session would be held in this object , a
 * call to close from the front end on this interface would result in the
 * hibernate session object getting closed.
 */

public class QueryResultDTOImpl implements QueryResult {

    private ScrollableResults scrollResult = null;

    public QueryInputs queryInputs = null;

    protected DTOBuilder dtoBuilder = null;

    protected boolean buildDTO = false;

    private Session hibernateSession = null;

    protected int size = 0;

    /**
     * Set the query which will be used for query execution
     * 
     */
    public void executeQuery(Query query) throws HibernateSearchException {
        if (query == null)
            throw new HibernateSearchException(HibernateConstants.SEARCH_INPUTNULL);
        try {
            scrollResult = query.scroll();
        } catch (HibernateException h) {
            throw new HibernateSearchException(HibernateConstants.SEARCH_FAILED, h);
        }

    }

    /**
     * Return the Session used for query
     * 
     */
    public Session getSession() throws HibernateProcessException {
        hibernateSession = QuerySession.openSession();
        return hibernateSession;
    }

    /**
     * Set the query inputs which will be used for query execution
     * 
     */
    public void setQueryInputs(QueryInputs queryInputs) throws HibernateSearchException {
        if (queryInputs == null)
            throw new HibernateSearchException(HibernateConstants.SEARCH_INPUTNULL);
        if (queryInputs.getBuildDTO()) {
            this.queryInputs = queryInputs;
            dtoBuilder = new DTOBuilder();
            dtoBuilder.setInputs(queryInputs);
            this.buildDTO = queryInputs.getBuildDTO();
        }

    }

    /**
     * Returns the requested set of search result objects based on the
     * pagination at the front end.
     * 
     */
    public List get(int position, int noOfObjects) throws HibernateSearchException {
        List returnList = new ArrayList();
        try {
            scrollResult.setRowNumber(position);
            returnList = new ArrayList();
            if (position < size) {
                if (buildDTO)
                    returnList.add(buildDTO(scrollResult.get()));
                else {
                    Object[] obj = scrollResult.get();
                    returnList.add(obj[0]);
                }
            }
            for (int i = 0; i < noOfObjects - 1; i++) {
                if (scrollResult.next()) {
                    if (buildDTO)
                        returnList.add(buildDTO(scrollResult.get()));
                    else {
                        Object[] obj = scrollResult.get();
                        returnList.add(obj[0]);
                    }
                }
            }
        } catch (Exception e) {
            throw new HibernateSearchException(HibernateConstants.SEARCH_FAILED, e);
        }
        return returnList;
    }

    /**
     * Returns the records valid for the query
     * 
     */
    public int getSize() throws HibernateSearchException {
        int lastRowNumber = 0;
        try {
            scrollResult.last();
            lastRowNumber = scrollResult.getRowNumber();
            scrollResult.first();
        } catch (Exception e) {

            throw new HibernateSearchException(HibernateConstants.SEARCH_FAILED, e);
        }
        size = lastRowNumber + 1;
        return size;
    }

    /**
     * This is invoked on the query result to indicate the end of search result
     * view at the front end , the associated hibernate session would be closed
     */
    public void close() throws HibernateProcessException {
        if (scrollResult != null)
            scrollResult.close();
        try {
            QuerySession.closeSession(hibernateSession);
        } catch (HibernateProcessException e) {
            throw e;
        }
    }

    protected Object buildDTO(Object[] dtoData) throws HibernateSearchException {
        return dtoBuilder.buildDTO(dtoData);
    }
}
