package org.mifos.application;

import java.sql.ResultSet;

import junit.framework.TestCase;
import net.sourceforge.mayfly.Database;

import org.mifos.application.fees.persistence.FeePersistenceTest;
import org.mifos.framework.util.helpers.DatabaseSetup;

/**
 * This class might be an interesting demonstration/check of how
 * we have hibernate set up to talk to different test databases.
 * 
 * But now that {@link FeePersistenceTest} is passing with Mayfly,
 * the only thing left here is some Mayfly dump tests (currently
 * commented out).
 */
public class HibernateTest extends TestCase {
	
    public void testMayflyNoHibernate() throws Exception {
		Database database = new Database(DatabaseSetup.getStandardStore());
        ResultSet results = database.query("select * from logmessages");
        assertFalse(results.next());
    }
    
    /*
    public void testMayflyDump() throws Exception {
        checkRoundTrip(DatabaseSetup.getStandardStore());
    }
    */

    /**
     * From a datastore, dump it, then load from that dump,
     * dump again, and compare the two dumps.
     * 
     * This is a somewhat weak test in that if the dump does something wrong,
     * it quite possibly will do the same thing wrong in both dumps.  But if the
     * dump produces SQL we can't parse or something of that order, we'll
     * catch it.
     */
    /*
    private static void checkRoundTrip(DataStore inputStore) {
        String dump = new SqlDumper().dump(inputStore);
        Database database2 = new Database();
        try {
            database2.executeScript(new StringReader(dump));
        }
        catch (MayflyException e) {
            throw new RuntimeException(
                "failure in command: " + e.failingCommand(), e);
        }
        
        String dump2 = new SqlDumper().dump(database2.dataStore());
        assertEquals(dump, dump2);
    }
    */

}
