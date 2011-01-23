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
import java.util.List;

import javax.sql.DataSource;

import org.joda.time.LocalDate;
import org.mifos.framework.persistence.SqlExecutor;
import org.mifos.platform.accounting.AccountingDto;
import org.mifos.platform.accounting.AccountingRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AccountingDaoImpl implements IAccountingDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountingDaoImpl.class);

    public static final int BRANCH_NAME = 1;
    public static final int VOUCHER_DATE = 2;
    public static final int VOUCHER_TYPE = 3;
    public static final int GL_CODE = 4;
    public static final int GL_CODE_NAME = 5;
    public static final int DEBIT = 6;
    public static final int CREDIT = 7;

    private final DataSource dataSource;

    @Autowired
    public AccountingDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public final List<AccountingDto> getAccountingDataByDate(LocalDate startDate, LocalDate endDate) {
        List<AccountingDto> dto = new ArrayList<AccountingDto>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(getAccountingDataQuery());
            statement.setString(1, startDate.toString());
            statement.setString(2, endDate.toString());
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                dto.add(new AccountingDto(rs.getString(BRANCH_NAME), rs.getString(VOUCHER_DATE), rs
                        .getString(VOUCHER_TYPE), rs.getString(GL_CODE), rs.getString(GL_CODE_NAME), rs
                        .getString(DEBIT), rs.getString(CREDIT)));
            }

        } catch (SQLException e) {
            LOGGER.error("Making accounting query :" + getAccountingDataQuery(), e);
            throw new AccountingRuntimeException(getAccountingDataQuery(), e);
        } finally {
            try {
                if(connection != null) { connection.close(); }
            } catch (SQLException e) {
                LOGGER.error("Closing connection :" + getAccountingDataQuery(), e);
                throw new AccountingRuntimeException(getAccountingDataQuery(), e);
            }
        }
        return dto;
    }

    private String getAccountingDataQuery() {
        return SqlExecutor.readFile(AccountingDaoImpl.class.getResourceAsStream("AccountingGLIntegrationQuery.sql"))[0];
    }
}
