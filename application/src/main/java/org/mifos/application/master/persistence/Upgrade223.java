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
        String sql = "INSERT INTO SPOUSE_FATHER_LOOKUP(SPOUSE_FATHER_ID,LOOKUP_ID) VALUES(?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1,spouseFatherId);
        statement.setInt(2, lookUpId);
        statement.execute();
    }
}
