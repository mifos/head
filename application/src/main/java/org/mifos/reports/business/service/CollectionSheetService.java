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

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.collectionsheet.business.CollSheetCustBO;
import org.mifos.application.collectionsheet.persistence.CollectionSheetPersistence;
import org.mifos.application.collectionsheet.persistence.CollectionSheetReportPersistence;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.reports.business.dto.CollectionSheetReportData;

/**
 *
 */
public class CollectionSheetService {

    private final CollectionSheetReportPersistence collectionSheetReportPersistence;

    CollectionSheetService(final CollectionSheetReportPersistence collectionSheetReportPersistence) {
        this.collectionSheetReportPersistence = collectionSheetReportPersistence;
    }

    public CollectionSheetService() {
        this(new CollectionSheetReportPersistence());
    }

    public List<CollSheetCustBO> getCollectionSheetForCustomerOnMeetingDate(final Date meetingDate, final Integer centerId,
            final Short loanOfficerId, final CustomerLevel customerLevel) throws PersistenceException {
        Map parameters = populateQueryParams(meetingDate, centerId, customerLevel);
        parameters.put("LOAN_OFFICER_ID", loanOfficerId);
        return retrieveCollectionSheetCustomers(
                NamedQueryConstants.COLLECTION_SHEET_CUSTOMERS_ON_MEETING_DATE_FOR_LOAN_OFFICER, parameters);
    }

    public List<CollSheetCustBO> getCollectionSheetForCustomers(final Date meetingDate, final CollSheetCustBO parent,
            final Short loanOfficerId) throws PersistenceException {
        return retrieveCollectionSheetOfChildrensOnMeetingDate(meetingDate, parent, CustomerLevel.CLIENT, loanOfficerId);
    }

    public List<CollSheetCustBO> getCollectionSheetForGroups(final Date meetingDate, final CollSheetCustBO parent,
            final Short loanOfficerId) throws PersistenceException {
        return retrieveCollectionSheetOfChildrensOnMeetingDate(meetingDate, parent, CustomerLevel.GROUP, loanOfficerId);
    }

    private List<CollSheetCustBO> retrieveCollectionSheetOfChildrensOnMeetingDate(final Date meetingDate,
            final CollSheetCustBO parent, final CustomerLevel childrenLevel, final Short loanOfficerId) throws PersistenceException {
        Map parameters = new HashMap();
        parameters.put("CUSTOMER_LEVEL", childrenLevel.getValue());
        parameters.put("MEETING_DATE", meetingDate);
        parameters.put("PARENT_CUSTOMER_ID", parent.getCustId());
        parameters.put("LOAN_OFFICER_ID", loanOfficerId);
        List<CollSheetCustBO> collectionSheetOfChildrens = retrieveCollectionSheetCustomers(
                NamedQueryConstants.COLLECTION_SHEETS_OF_CHILDREN_ON_MEETING_DATE, parameters);
        return collectionSheetOfChildrens;
    }

    private Map populateQueryParams(final Date meetingDate, final Integer centerId, final CustomerLevel customerLevel) {
        Map parameters = new HashMap();
        parameters.put("CUSTOMER_LEVEL", customerLevel.getValue());
        parameters.put("MEETING_DATE", meetingDate);
        parameters.put("CUSTOMER_ID", centerId);
        return parameters;
    }

    private List<CollSheetCustBO> retrieveCollectionSheetCustomers(final String queryName, final Map parameters)
            throws PersistenceException {
        List<CollSheetCustBO> centersCollectionSheet = new CollectionSheetPersistence().executeNamedQuery(queryName,
                parameters);
        return centersCollectionSheet;
    }

    public List<CollectionSheetReportData> extractReportData(final Integer branchId, final java.util.Date meetingDate,
            final Integer personnelId, final Integer centerId) throws ServiceException {
        try {
            if (ALL_LOAN_OFFICER_SELECTION_ITEM.sameAs(personnelId)) {
                if (ALL_CENTER_SELECTION_ITEM.sameAs(centerId)) {
                    return collectionSheetReportPersistence.extractReportDataAllLoanOfficersAllCenters(branchId,
                            meetingDate);
                } else {
                    return collectionSheetReportPersistence.extractReportDataAllLoanOfficersOneCenter(branchId,
                            meetingDate, centerId);
                }
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
