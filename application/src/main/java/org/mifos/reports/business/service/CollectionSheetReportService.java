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

import static org.mifos.framework.util.helpers.FilePaths.REPORT_PRODUCT_OFFERING_CONFIG;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productdefinition.business.service.SavingsPrdBusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.reports.business.dto.CollectionSheetReportData;
import org.mifos.reports.util.helpers.ReportUtils;
import org.springframework.core.io.ClassPathResource;

/**
 * FIXME - #0001 - keithw - rename to BirtCollectionSheetReportService
 */
public class CollectionSheetReportService implements ICollectionSheetReportService {

    private final CollectionSheetService collectionSheetService;
    private final ReportProductOfferingService reportProductOfferingService;

    public CollectionSheetReportService(final CollectionSheetService collectionSheetService, final ReportProductOfferingService reportProductOfferingService) {
        super();
        this.collectionSheetService = collectionSheetService;
        this.reportProductOfferingService = reportProductOfferingService;
    }

    public CollectionSheetReportService() {
        this(new CollectionSheetService(), new ReportProductOfferingService(
                new LoanPrdBusinessService(), new SavingsPrdBusinessService(), new ClassPathResource(REPORT_PRODUCT_OFFERING_CONFIG)));
    }

//    FIXME - #0001 - keithw - write unit test for this
    // IS CALLED FROM CollectionSheetReport.rptdesign
    public List<CollectionSheetReportData> getReportData(final Integer branchId, final String meetingDate,
            final Integer personnelId, final Integer centerId) throws ServiceException {
        try {
            Date meetingDateAsDate = ReportUtils.parseReportDate(meetingDate);
            return collectionSheetService.extractReportData(branchId, meetingDateAsDate, personnelId, centerId);
        } catch (ParseException e) {
            throw new ServiceException(e);
        }
    }

    // IS CALLED FROM *.rptdesign
    public boolean displaySignatureColumn(final Integer columnNumber) throws ServiceException {
        // FIXME - #00001 - keithw - what is displaySignatureColumn and do we have to delegate to reportProductOfferingService?
        return reportProductOfferingService.displaySignatureColumn(columnNumber);
    }
}