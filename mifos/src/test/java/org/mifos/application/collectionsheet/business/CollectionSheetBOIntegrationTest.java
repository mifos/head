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

import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.collectionsheet.persistence.CollectionSheetPersistence;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;

public class CollectionSheetBOIntegrationTest extends MifosIntegrationTest {
    public CollectionSheetBOIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    public void testRetrieveCollectionSheetMeetingDateReturnsAllCollectionSheetsForSpecifiedMeeting() throws Exception {
        Session session = StaticHibernateUtil.getSessionTL();
        Transaction transaction = session.beginTransaction();
        Date year2010 = DateUtils.getSqlDate(2010, Calendar.JANUARY, 1);
        CollectionSheetBO collectionSheet = new CollectionSheetBO();
        collectionSheet.setCollSheetDate(year2010);
        collectionSheet.setRunDate(new Date(System.currentTimeMillis()));
        collectionSheet.create();
        HashMap queryParameters = new HashMap();
        queryParameters.put("MEETING_DATE", year2010);
        List matchingCollectionSheets = new CollectionSheetPersistence().executeNamedQuery(
                NamedQueryConstants.COLLECTION_SHEETS_FOR_MEETING_DATE, queryParameters);
        assertEquals(1, matchingCollectionSheets.size());
        assertEquals(collectionSheet, matchingCollectionSheets.get(0));
        transaction.rollback();
    }
}
