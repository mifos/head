package org.mifos.platform.accounting.tally.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mifos.platform.accounting.VoucherType;

public class TallyMessageTest {

    @Test
    public void testTallyMessage() {
        AllLedger allLedger = new AllLedger("ledgerName", false, "234", "branchName");
        List<AllLedger> list = new ArrayList<AllLedger>();
        list.add(allLedger);
        TallyMessage tallyMessage = new TallyMessage(VoucherType.JOURNAL, new Date(), list);
        Assert.assertNotNull(tallyMessage.toString());
    }

}
