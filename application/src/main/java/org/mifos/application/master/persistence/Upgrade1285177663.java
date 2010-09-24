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

import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.components.batchjobs.helpers.UpdateCustomerFeesHelper;
import org.mifos.framework.persistence.Upgrade;

/**
 * Run the fix for MIFOS-3712 to ensure that fee schedules are caught up.
 * This does a check to see if this fix was already run by Mifos 1.6.1
 * and does not rerun the fix if it was already run.
 *
 */
public class Upgrade1285177663 extends Upgrade {

    public Upgrade1285177663() {
        super();
    }

    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {

        String key = "Recurring fees cleanup done for MIFOS-3712";

        ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
        if (configurationPersistence.getConfigurationKeyValueInteger(key) == null) {
            long dummy = 0;
            try {
                new UpdateCustomerFeesHelper().execute(dummy);
            } catch (BatchJobException e) {
                throw new MifosRuntimeException(e);
            }
        }
    }



}
