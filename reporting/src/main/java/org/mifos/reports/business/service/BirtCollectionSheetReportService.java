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

package org.mifos.reports.business.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.mifos.framework.exceptions.ServiceException;
import org.mifos.reports.business.dto.CollectionSheetReportData;
import org.mifos.reports.util.helpers.ReportUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * FIXME - #0001 - keithw - rename to BirtCollectionSheetReportService
 */
public class BirtCollectionSheetReportService implements ICollectionSheetReportService {

    private final BirtCollectionSheetService birtCollectionSheetService;
    private final ReportProductOfferingService reportProductOfferingService;

    @Autowired
    public BirtCollectionSheetReportService(final BirtCollectionSheetService birtCollectionSheetService, final ReportProductOfferingService reportProductOfferingService) {
        super();
        this.birtCollectionSheetService = birtCollectionSheetService;
        this.reportProductOfferingService = reportProductOfferingService;
    }

//    FIXME - #0001 - keithw - write unit test for this
    // IS CALLED FROM CollectionSheetReport.rptdesign
    @Override
	public List<CollectionSheetReportData> getReportData(final Integer branchId, final String meetingDate,
            final Integer personnelId, final Integer centerId) throws ServiceException {
        try {
            Date meetingDateAsDate = ReportUtils.parseReportDate(meetingDate);
            return birtCollectionSheetService.extractReportData(branchId, meetingDateAsDate, personnelId, centerId);
        } catch (ParseException e) {
            throw new ServiceException(e);
        }
    }

    // IS CALLED FROM *.rptdesign
    @Override
	public boolean displaySignatureColumn(final Integer columnNumber) throws ServiceException {
        // FIXME - #00001 - keithw - what is displaySignatureColumn and do we have to delegate to reportProductOfferingService?
        return reportProductOfferingService.displaySignatureColumn(columnNumber);
    }
}