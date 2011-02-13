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

package org.mifos.platform.accounting.dao;

import java.sql.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.platform.accounting.AccountingDto;

public interface IAccountingDao {

    List<AccountingDto> getAccountingDataByDate(LocalDate startDate, LocalDate endDate);

    LocalDate getStartDateOfFinancialTransactions();

}
