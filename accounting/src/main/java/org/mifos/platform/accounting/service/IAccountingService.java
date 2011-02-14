/*
 * Copyright Grameen Foundation USA
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

package org.mifos.platform.accounting.service;

import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.platform.accounting.AccountingDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface IAccountingService {

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_USE_ACCOUNTING_INTEGRATION')")
    String getExportOutput(LocalDate startDate, LocalDate endDate);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_USE_ACCOUNTING_INTEGRATION')")
    List<AccountingDto> getExportDetails(LocalDate startDate, LocalDate endDate);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_USE_ACCOUNTING_INTEGRATION')")
    List<ExportFileInfo> getLastTenExports(Integer listStartDay);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_USE_ACCOUNTING_INTEGRATION')")
    Integer getNumberDaysFromStartOfFinancialTransactions();

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_USE_ACCOUNTING_INTEGRATION')")
    String getExportOutputFileName(LocalDate startDate, LocalDate endDate);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_USE_ACCOUNTING_INTEGRATION')")
    Boolean hasAlreadyRanQuery(LocalDate startDate, LocalDate endDate);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_USE_ACCOUNTING_INTEGRATION')")
    Boolean deleteCacheDir();
}
