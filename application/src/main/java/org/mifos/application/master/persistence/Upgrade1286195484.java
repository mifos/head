/*
 *  Copyright 2010 Grameen Foundation USA
 *  All rights reserved.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.mifos.application.master.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.customers.business.CustomerAddressDetailEntity;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.SqlUpgrade;
import org.mifos.framework.persistence.Upgrade;
import org.mifos.framework.util.SqlUpgradeScriptFinder;

/**
 * Loads all customer_address_detail records and forces Hibernate to do update
 * to compute phoneNumberStripped column value.
 */
public class Upgrade1286195484 extends Upgrade {

	/**
	 * Add new column "phone_number_stripped" only if the column doesn't exist yet.
	 */
	public static void conditionalAlter(Connection connection) throws SQLException, IOException {
		ResultSet rs = connection.createStatement().executeQuery(
				"SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE()" +
				" AND COLUMN_NAME='phone_number_stripped' AND TABLE_NAME='customer_address_detail'");
		try {
			if (!rs.first()) {
				SqlUpgrade upgrade = SqlUpgradeScriptFinder.findUpgradeScript(
						"upgrade1286195484.sql");
				upgrade.runScript(connection);
			}
		} finally {
			rs.close();
		}
	}

	public static int COMMIT_EVERY = 100;
    public static int FLUSH_EVERY = 1000;
    public static int PRINT_EVERY = 10000;

    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {
		conditionalAlter(connection);

        Session session = StaticHibernateUtil.getSessionTL();
        ScrollableResults results = session.createQuery("from CustomerAddressDetailEntity").setCacheMode(CacheMode.IGNORE).scroll(ScrollMode.FORWARD_ONLY);
        Query update = session.createQuery("update CustomerAddressDetailEntity set address.phoneNumberStripped = :phoneNumberStripped where " +
						"customerAddressId = :id");
		Transaction t = session.beginTransaction();
		int modifyCount = 0;
        int resultCount = 0;
        while (results.next()) {
            ++resultCount;
            CustomerAddressDetailEntity address = (CustomerAddressDetailEntity)results.get(0);
			if (address.getAddress().getPhoneNumber() != null && !address.getAddress().getPhoneNumber().isEmpty()) {
				update.setString("phoneNumberStripped", address.getAddress().getPhoneNumberStripped());
				update.setInteger("id", address.getCustomerAddressId());
				update.executeUpdate();
				++modifyCount;
				if (modifyCount % COMMIT_EVERY == 0) {
					t.commit();
					t = session.beginTransaction();
				}
			}
            if (resultCount % FLUSH_EVERY == 0) {
                session.flush();
                session.clear();
            }
            if (resultCount % PRINT_EVERY == 0) {
                System.out.print(".");
                System.out.flush();
            }
        }
		session.flush();
		t.commit();
    }

}

