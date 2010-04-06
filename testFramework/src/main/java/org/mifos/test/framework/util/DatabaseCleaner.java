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

package org.mifos.test.framework.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * I am responsible for tearing down the data in the database in a given order.
 */
public class DatabaseCleaner {

    private final JdbcTemplate template;

    public DatabaseCleaner(final DriverManagerDataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }

    public void clean() {

        template.execute("delete from change_log_detail");
        template.execute("delete from change_log");

        template.execute("delete from customer_fee_schedule");
        template.execute("delete from customer_schedule");
        template.execute("delete from account_fees");
        template.execute("delete from customer_account");

        template.execute("delete from account_status_change_history");
        template.execute("delete from loan_summary");
        template.execute("delete from loan_account");

        template.execute("delete from saving_schedule");
        template.execute("delete from savings_account");
        template.execute("delete from savings_performance");

        template.execute("delete from account");

        template.execute("delete from customer_address_detail");
        template.execute("delete from customer_meeting");

        template.execute("delete from loan_amount_from_last_loan");
        template.execute("delete from loan_amount_from_loan_cycle");
        template.execute("delete from loan_amount_same_for_all_loan");

        template.execute("delete from no_of_install_from_last_loan");
        template.execute("delete from no_of_install_from_loan_cycle");
        template.execute("delete from no_of_install_same_for_all_loan");

        template.execute("delete from loan_offering_fund");
        template.execute("delete from loan_offering");
        template.execute("delete from savings_offering");

        template.execute("delete from prd_offering_meeting");
        template.execute("delete from prd_offering");

        template.execute("delete from group_perf_history");
        template.execute("delete from client_perf_history");

        template.execute("delete from customer_picture");

        template.execute("update customer set parent_customer_id = null");
        template.execute("delete from customer");
    }
}
