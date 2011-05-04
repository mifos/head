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

package org.mifos.application.master.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.mifos.framework.persistence.Upgrade;
import org.mifos.security.AddActivity;
import org.mifos.security.util.SecurityConstants;

public class Upgrade1283765911 extends Upgrade {

    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {
        new AddActivity("Permissions-CanRunBatchJobsOnDemand", SecurityConstants.CAN_RUN_BATCH_JOBS_ON_DEMAND,
                SecurityConstants.SYSTEM_INFORMATION).upgrade(connection);
        new AddActivity("Permissions-CanUpdateBatchJobsConfiguration", SecurityConstants.CAN_UPDATE_BATCH_JOBS_CONFIGURATION,
                SecurityConstants.SYSTEM_INFORMATION).upgrade(connection);
        /* removed usage of SessionFactoryUtils.getDataSource to allow upgrade to run with reconfigured dataSource
         * for MIFOS-4950. Historic batch job run info is not migrated.
         */
        connection.createStatement().executeUpdate("drop table scheduled_tasks");
    }

}
