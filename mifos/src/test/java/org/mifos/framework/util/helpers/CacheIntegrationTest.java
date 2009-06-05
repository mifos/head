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

package org.mifos.framework.util.helpers;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.QueryResultDTOImpl;

public class CacheIntegrationTest extends MifosIntegrationTest {

    public CacheIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private Cache cache = null;

    private QueryResultDTOImpl queryResult = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        cache = new Cache();
        queryResult = new QueryResultDTOImpl();
        Session session = null;
        QueryResult notesResult = null;
        notesResult = QueryFactory.getQueryResult("NotesSearch");
        session = notesResult.getSession();
        Query query = session.getNamedQuery(NamedQueryConstants.GETALLACCOUNTNOTES);
        query.setInteger("accountId", 1);
        queryResult.executeQuery(query);
        cache.setCacheMap(cache.getCacheMap());

        cache = new Cache(queryResult);
    }

    public void testGetCache() throws Exception {
        List list = cache.getList(1, "newMethod");
        list = cache.getList(3, "previous");
        assertNull(list);
        list = cache.getList(4, "previous");
        assertNull(list);
        assertEquals(0, cache.getSize());
        assertEquals(1, cache.getCacheMap().size());

        list = cache.getList(4, "next");
        cache.setSize(2);
        assertEquals(2, cache.getSize());
    }
}
