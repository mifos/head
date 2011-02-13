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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.mifos.framework.persistence.SqlExecutor;
import org.mifos.platform.accounting.AccountingDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class AccountingDaoImpl implements IAccountingDao {

    private static final int BRANCH_NAME = 1;
    private static final int VOUCHER_DATE = 2;
    private static final int VOUCHER_TYPE = 3;
    private static final int GL_CODE = 4;
    private static final int GL_CODE_NAME = 5;
    private static final int DEBIT = 6;
    private static final int CREDIT = 7;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public final void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    protected final void setTestingJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public final List<AccountingDto> getAccountingDataByDate(LocalDate startDate, LocalDate endDate) {
        Object[] parameter = new Object[] { startDate.toString(), endDate.toString() };
        return jdbcTemplate.query(getAccountingDataQuery(), parameter, MAPPER);
    }

    @Override
    public final LocalDate getStartDateOfFinancialTransactions() {
        String date = jdbcTemplate.queryForObject("select min(posted_date) from financial_trxn", String.class);
        LocalDate lDate = new LocalDate();
        if(StringUtils.isNotBlank(date)) {
            lDate = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(date).toLocalDate();
        }
        return lDate;
    }

    private String getAccountingDataQuery() {
        return SqlExecutor.readFile(AccountingDaoImpl.class.getResourceAsStream("AccountingGLIntegrationQuery.sql"))[0];
    }

    public static final ParameterizedRowMapper<AccountingDto> MAPPER = new ParameterizedRowMapper<AccountingDto>() {
        @Override
        public AccountingDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new AccountingDto(rs.getString(BRANCH_NAME), rs.getString(VOUCHER_DATE), rs.getString(VOUCHER_TYPE),
                    rs.getString(GL_CODE), rs.getString(GL_CODE_NAME), rs.getString(DEBIT), rs.getString(CREDIT));
        }
    };
}
