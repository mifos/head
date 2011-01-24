package org.mifos.platform.accounting.tally.message;

import junit.framework.Assert;

import org.junit.Test;

public class AllLedgerTest {

    @Test
    public void testAllLedgerTest() {
        AllLedger allLedger = new AllLedger("ledgerName", false, "234", "branchName");
        Assert.assertEquals("false;ledgerName;234;branchName", allLedger.toString());
    }
}
