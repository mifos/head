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

package org.mifos.application.collectionsheet.business;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.collectionsheet.persistence.CollectionSheetPersistence;
import org.mifos.framework.util.helpers.DateUtils;


public class CollectionSheetBOTest extends TestCase {

    private CollectionSheetPersistence collectionSheetPersistenceMock;
    private CollectionSheetBO collectionSheet;
    private HashMap<String, Date> queryParameters = new HashMap<String, Date>();

    @Override
    public void setUp() throws Exception {
        collectionSheet = new CollectionSheetBO();
        collectionSheetPersistenceMock = createMock(CollectionSheetPersistence.class);
        collectionSheet.setCollectionSheetPersistence(collectionSheetPersistenceMock);
        expect(collectionSheetPersistenceMock.createOrUpdate(collectionSheet)).andReturn(new Object());
        List<CollectionSheetBO> matchingCollectionSheets = new ArrayList<CollectionSheetBO>();
        matchingCollectionSheets.add(collectionSheet);
        collectionSheetPersistenceMock.executeNamedQuery(NamedQueryConstants.COLLECTION_SHEETS_FOR_MEETING_DATE,
                queryParameters);
        expectLastCall().andReturn(matchingCollectionSheets);
        replay(collectionSheetPersistenceMock);
    }

    @Override
    public void tearDown() {
        collectionSheetPersistenceMock = null;
        collectionSheet = null;
    }

    public void testRetrieveCollectionSheetMeetingDateReturnsAllCollectionSheetsForSpecifiedMeeting() throws Exception {
        Date year2010 = DateUtils.getSqlDate(2010, Calendar.JANUARY, 1);
        collectionSheet.setCollSheetDate(year2010);
        collectionSheet.setRunDate(new Date(System.currentTimeMillis()));
        collectionSheet.create();
        queryParameters.put("MEETING_DATE", year2010);
        List<?> matchingCollectionSheets = collectionSheetPersistenceMock.executeNamedQuery(
                NamedQueryConstants.COLLECTION_SHEETS_FOR_MEETING_DATE, queryParameters);
       Assert.assertEquals(1, matchingCollectionSheets.size());
       Assert.assertEquals(collectionSheet, matchingCollectionSheets.get(0));
    }
}
