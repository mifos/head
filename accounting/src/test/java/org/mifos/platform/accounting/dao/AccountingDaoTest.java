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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.accounting.AccountingDto;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

;

@RunWith(MockitoJUnitRunner.class)
public class AccountingDaoTest {

    private IAccountingDao accountingDao;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    @Ignore
    public void shouldCallQueryAndReturnData() {
        List<AccountingDto> accountData = accountingDao.getAccountingDataByDate(new LocalDate(2010, 8, 10),
                new LocalDate(2010, 8, 10));
        Assert.assertNull(accountData);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDataSourcMockWithNull() {
        accountingDao = new AccountingDaoImpl();
        ((AccountingDaoImpl) accountingDao).setTestingJdbcTemplate(jdbcTemplate);
        when(jdbcTemplate.query(any(String.class), any(Object[].class), any(ParameterizedRowMapper.class))).thenReturn(
                null);
        Assert.assertNull(accountingDao.getAccountingDataByDate(new LocalDate(2010, 8, 10), new LocalDate(2010, 8, 10)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDataSourcMockWithEmptyList() throws SQLException {
        accountingDao = new AccountingDaoImpl();
        ((AccountingDaoImpl) accountingDao).setTestingJdbcTemplate(jdbcTemplate);
        when(jdbcTemplate.query(any(String.class), any(Object[].class), any(ParameterizedRowMapper.class))).thenReturn(
                new ArrayList<Object>());
        Assert.assertTrue(accountingDao.getAccountingDataByDate(new LocalDate(2010, 8, 10), new LocalDate(2010, 8, 10))
                .isEmpty());
    }
}
