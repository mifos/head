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

package org.mifos.platform.accounting.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.framework.persistence.SqlExecutor;
import org.mifos.platform.accounting.AccountingDto;
import org.mifos.platform.accounting.config.DatabaseManager;
import org.springframework.stereotype.Repository;

@Repository
public class AccountingDaoImpl implements IAccountingDao {

    @Override
    public List<AccountingDto> getAccountingData(Date startDate, Date endDate) throws SQLException {
        List<AccountingDto> dto = new ArrayList<AccountingDto>();
        Connection connection = DatabaseManager.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(getAccountingDataQuery());
        statement.setDate(1, new java.sql.Date(startDate.getTime()));
        statement.setDate(2, new java.sql.Date(endDate.getTime()));
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            dto.add(new AccountingDto(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs
                    .getString(5), rs.getString(6), rs.getString(7)));
        }
        return dto;
    }

    private String getAccountingDataQuery() {
        return SqlExecutor.readFile(AccountingDaoImpl.class.getResourceAsStream("AccountingGLIntegrationQuery.sql"))[0];
    }

    @Override
    public List<AccountingDto> getAccountingData(LocalDate startDate, LocalDate endDate) throws SQLException {
        return getAccountingData(startDate.toDateMidnight().toDate(), endDate.toDateMidnight().toDate());
    }
}
