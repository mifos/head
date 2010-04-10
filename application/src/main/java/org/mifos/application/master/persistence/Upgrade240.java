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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.persistence.GroupPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Upgrade;

/**
 * Clean up search ids as part of fix for MIFOS-2737
 *
 */
public class Upgrade240 extends Upgrade {

    public Upgrade240() {
        super(240);
    }

    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {
        fixDuplicateGroupSearchIds(connection);
        upgradeVersion(connection);
    }


    private void fixDuplicateGroupSearchIds(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        String query = "select customer_id, customer_level_id, branch_id, search_id " +
        "from customer c " +
        "where c.customer_level_id = 2 " +
        "and 1 < " +
        "(select count(*) " +
        "FROM customer customerProbs " +
        "where c.branch_id = customerProbs.branch_id " +
        "and c.search_id = customerProbs.search_id " +
        "and customerProbs.customer_level_id = 2)";

        List<Integer> customerIdList = new ArrayList<Integer>();
        try {
            ResultSet results = statement.executeQuery(query);
            if (results != null) {
                while (results.next()) {
                    int customerId = results.getInt(1);
                    customerIdList.add(customerId);
                }
            }
        } finally {
            statement.close();
        }
        for (int customerId: customerIdList) {
            try {
                updateGroupSearchId(customerId);
            } catch (PersistenceException e) {
                throw new MifosRuntimeException("Unable to update group searchId", e);
            } catch (CustomerException e) {
                throw new MifosRuntimeException("Unable to update group searchId", e);
            }
        }
    }

    private void updateGroupSearchId(int customerId) throws PersistenceException, CustomerException {
        GroupBO group = (GroupBO)new GroupPersistence().loadPersistentObject(GroupBO.class, new Integer(customerId));
        group.updateSearchId();
    }
}
