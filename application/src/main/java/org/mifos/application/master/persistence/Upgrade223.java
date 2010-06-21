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
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.mifos.framework.persistence.Upgrade;

public class Upgrade223 extends Upgrade {


    public Upgrade223() {
        super(223);
    }

    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {

       int newLookupEntityId= addLookupEntity(connection,"LivingStatus", "This entity is used to track whether the family member" +
        		" is living together with the client or not");

       insertLookupValue(connection, newLookupEntityId, "Together");
       insertLookupValue(connection, newLookupEntityId, "NotTogether");

       int motherId=insertLookupValue(connection, 52, "Mother");
       int childId=insertLookupValue(connection, 52, "Child");
       //Skipping id of 3 as 3 is of name type client.
       insertIntoSpouseFatherLookup(connection,4, motherId);
       insertIntoSpouseFatherLookup(connection,5, childId);
       upgradeVersion(connection);
    }

    private void insertIntoSpouseFatherLookup(Connection connection,int spouseFatherId, int lookUpId ) throws SQLException {
        String sql = "insert into spouse_father_lookup(spouse_father_id,lookup_id) value(?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1,spouseFatherId);
        statement.setInt(2, lookUpId);
        statement.execute();
    }
}
