package org.mifos.application;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.sql.ResultSet;

import junit.framework.TestCase;
import net.sourceforge.mayfly.Database;
import net.sourceforge.mayfly.MayflyException;
import net.sourceforge.mayfly.datastore.DataStore;
import net.sourceforge.mayfly.dump.SqlDumper;

import org.mifos.framework.util.helpers.DatabaseSetup;

/**
 * This class is for various Mayfly-related tests.
 * 
 * Also see {@link MayflyTests}; no particular need to test things
 * here if they are covered in some of the tests there.
 */
public class MayflyMiscTest extends TestCase {
	
    public void testMayflySanityCheck() throws Exception {
		Database database = new Database(DatabaseSetup.getStandardStore());
        ResultSet results = database.query("select * from logmessages");
        assertFalse(results.next());
    }
    
    public void xtestStartupTime() throws Exception {
		long start = System.currentTimeMillis();
		DataStore store = DatabaseSetup.getStandardStore();
		assertEquals(4, store.table("currency").rowCount());
		long end = System.currentTimeMillis();
		System.out.println("total = " + (end - start) / 1000.0 + " s");
	}
    
    public void testMayflyDump() throws Exception {
        checkRoundTrip(DatabaseSetup.getStandardStore());
    }

    /**
     * From a datastore, dump it, then load from that dump,
     * dump again, and compare the two dumps.
     * 
     * This is a somewhat weak test in that if the dump does something wrong,
     * it quite possibly will do the same thing wrong in both dumps.  But if the
     * dump produces SQL we can't parse or something of that order, we'll
     * catch it.
     */
    
    private static void checkRoundTrip(DataStore inputStore) throws IOException {
        String dump = new SqlDumper().dump(inputStore);
        Database database2 = new Database();
        try {
            database2.executeScript(new StringReader(dump));
        }
        catch (MayflyException e) {
        	FileWriter writer = new FileWriter("checkRoundTrip.dump");
        	writer.write("-- dump follows:\n");
        	writer.write(dump);
            throw new RuntimeException(
                "failure in command: " + e.failingCommand(), e);
        }
        
        String dump2 = new SqlDumper().dump(database2.dataStore());
        assertEquals(dump, dump2);
    }

}
