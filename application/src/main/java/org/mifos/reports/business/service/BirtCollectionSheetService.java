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

package org.mifos.reports.business.service;

import static org.mifos.reports.ui.SelectionItem.ALL_CENTER_SELECTION_ITEM;
import static org.mifos.reports.ui.SelectionItem.ALL_LOAN_OFFICER_SELECTION_ITEM;

import java.util.List;

import org.mifos.application.collectionsheet.persistence.BirtCollectionSheetReportPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.reports.business.dto.CollectionSheetReportData;

/**
 * FIXME - #00001 - keithw - wire up service and dao using spring and test using new integration tests
 */
public class BirtCollectionSheetService {

    private final BirtCollectionSheetReportPersistence collectionSheetReportPersistence;

    public BirtCollectionSheetService(final BirtCollectionSheetReportPersistence collectionSheetReportPersistence) {
        this.collectionSheetReportPersistence = collectionSheetReportPersistence;
    }

    public BirtCollectionSheetService() {
        this(new BirtCollectionSheetReportPersistence());
    }

    // FIXME - #00001 - keithw - write mockito unit tests around this to replace easymock tests that existed in CollectionSheetServiceIntegrationTest
    public List<CollectionSheetReportData> extractReportData(final Integer branchId, final java.util.Date meetingDate,
            final Integer personnelId, final Integer centerId) throws ServiceException {
        try {
            if (ALL_LOAN_OFFICER_SELECTION_ITEM.sameAs(personnelId)) {
                if (ALL_CENTER_SELECTION_ITEM.sameAs(centerId)) {
                    return collectionSheetReportPersistence.extractReportDataAllLoanOfficersAllCenters(branchId,
                            meetingDate);
                }

                return collectionSheetReportPersistence.extractReportDataAllLoanOfficersOneCenter(branchId,
                            meetingDate, centerId);

            } else if (ALL_CENTER_SELECTION_ITEM.sameAs(centerId)) {
                return collectionSheetReportPersistence.extractReportDataAllCentersUnderLoanOfficer(branchId,
                        meetingDate, personnelId);
            }

            return collectionSheetReportPersistence.extractReportData(branchId, meetingDate, personnelId, centerId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }
}
