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

package org.mifos.framework.util.helpers;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.mifos.framework.hibernate.helper.QueryResultDTOImpl;

public class CacheTest extends TestCase {

    private Cache cache = null;

    @Override
    public void setUp() throws Exception {
        QueryResultDTOImpl queryResultMock = createMock(QueryResultDTOImpl.class);
        expect(queryResultMock.getSize()).andReturn(0).anyTimes();
        expect(queryResultMock.get(0, 10)).andReturn(null).anyTimes();
        replay(queryResultMock);
        verify(queryResultMock);
        cache = new Cache(queryResultMock);
    }

    @Override
    public void tearDown() {
        cache = null;
    }

    public void testGetCache() throws Exception {
        List<?> list = cache.getList(1, "newMethod");
        list = cache.getList(3, "previous");
        Assert.assertNull(list);
        list = cache.getList(4, "previous");
        Assert.assertNull(list);
       Assert.assertEquals(0, cache.getSize());
       Assert.assertEquals(1, cache.getCacheMap().size());

        list = cache.getList(4, "next");
        cache.setSize(2);
       Assert.assertEquals(2, cache.getSize());
    }
}
