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
import java.sql.SQLException;
import java.util.Iterator;
import org.hibernate.Query;
import org.hibernate.Session;
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

    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {
        SqlUpgrade upgrade = SqlUpgradeScriptFinder.findUpgradeScript(
                "upgrade1286195484.sql");
        upgrade.runScript(connection);

        Session session = StaticHibernateUtil.getSessionTL();
        Query query = session.createQuery("from CustomerAddressDetailEntity");
        Iterator it = query.iterate();
        while (it.hasNext()) {
            CustomerAddressDetailEntity address = (CustomerAddressDetailEntity)it.next();
			if (address.getAddress().getPhoneNumber() != null && !address.getAddress().getPhoneNumber().isEmpty()) {
				Query update = session.createQuery("update CustomerAddressDetailEntity set address.phoneNumberStripped = :phoneNumberStripped where " +
						"customerAddressId = :id");
				update.setString("phoneNumberStripped", address.getAddress().getPhoneNumberStripped());
				update.setInteger("id", address.getCustomerAddressId());
				update.executeUpdate();
			}
        }
    }

}
