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
package org.mifos.application.servicefacade;

import org.joda.time.LocalDate;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryGridDto;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetDataDto;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.security.util.UserContext;

/**
 * Service facade for Collection Sheet functionality.
 */
public interface CollectionSheetServiceFacade {

    CollectionSheetEntryFormDto loadAllActiveBranchesAndSubsequentDataIfApplicable(UserContext userContext);

    CollectionSheetEntryFormDto loadLoanOfficersForBranch(Short officeId, UserContext userContext,
            CollectionSheetEntryFormDto previousCollectionSheetEntryFormDto);

    CollectionSheetEntryFormDto loadCustomersForBranchAndLoanOfficer(Short personnelId, Short officeId,
            CollectionSheetEntryFormDto previousCollectionSheetEntryFormDto);

    CollectionSheetEntryFormDto loadMeetingDateForCustomer(Integer customerId,
            CollectionSheetEntryFormDto previousCollectionSheetEntryFormDto);

    CollectionSheetEntryGridDto generateCollectionSheetEntryGridView(
            CollectionSheetFormEnteredDataDto formEnteredDataDto, MifosCurrency currency);

    CollectionSheetEntryGridDto previewCollectionSheetEntry(CollectionSheetEntryGridDto previousCollectionSheetEntryDto,
            CollectionSheetDataDto dataView);

    CollectionSheetErrorsDto saveCollectionSheet(CollectionSheetEntryGridDto previousCollectionSheetEntryDto, Short userId);

	CollectionSheetErrorsDto saveCollectionSheet(SaveCollectionSheetDto saveCollectionSheet);

	CollectionSheetDto getCollectionSheet(Integer customerId, LocalDate meetingDate);
	
	CollectionSheetEntryFormDto loadGroupsForCustomer(final Short personnelId,
            final Short officeId,final Integer customerId,final CollectionSheetEntryFormDto formDto);
	
    CollectionSheetEntryFormDto loadMembersByGroup(final Short personnelId,
            final Short officeId,final Integer groupId,final CollectionSheetEntryFormDto formDto);

}
