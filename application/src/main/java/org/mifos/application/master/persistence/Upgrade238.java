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

/**
 * Add new moratorium repayment rule.
 * <p>FIXME: KRP -- when it's time to commit, change name of class to "Upgrade"+<highestVersion>+1</p>
 *
 */
public class Upgrade238 extends Upgrade {

    public Upgrade238() {
        //FIXME: KRP -- change this number to latestversion+1 just prior to committing
        super(238);
    }

    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {

        int moratoriumRepaymentLookupRuleId = insertLookupValue(connection, 91, "RepaymentRule-RepaymentMoratorium");
        
        insertIntoRepaymentRuleLookup(connection,4, moratoriumRepaymentLookupRuleId);
        upgradeVersion(connection);
    }

    private void insertIntoRepaymentRuleLookup(Connection connection,int repaymentRuleId, int lookUpId ) 
                    throws SQLException { 
        String sql = "INSERT INTO REPAYMENT_RULE (REPAYMENT_RULE_ID, REPAYMENT_RULE_LOOKUP_ID) VALUES(?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1,repaymentRuleId);
        statement.setInt(2, lookUpId);
        statement.execute();        
    }

}
