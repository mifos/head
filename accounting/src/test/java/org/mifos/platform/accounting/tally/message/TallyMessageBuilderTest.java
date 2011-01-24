package org.mifos.platform.accounting.tally.message;

import java.util.Date;

import org.junit.Test;
import org.mifos.platform.accounting.AccountingDto;
import org.mifos.platform.accounting.VoucherType;

public class TallyMessageBuilderTest {

    @Test(expected = TallyMessageBuilderException.class)
    public void testTallyMessageBuilderWithoutDate() throws TallyMessageBuilderException {
        new TallyMessageBuilder(VoucherType.JOURNAL, "branch").build();
    }

    @Test(expected = TallyMessageBuilderException.class)
    public void testTallyMessageBuilderWithNullDate() throws TallyMessageBuilderException {
        new TallyMessageBuilder(VoucherType.JOURNAL, "branch").withVoucherDate(null).build();
    }

    @Test(expected = TallyMessageBuilderException.class)
    public void testTallyMessageBuilderWithoutVoucherType() throws TallyMessageBuilderException {
        new TallyMessageBuilder(null, "branch").withVoucherDate(new Date()).build();
    }

    @Test(expected = TallyMessageBuilderException.class)
    public void testTallyMessageBuilderWithNullBranch() throws TallyMessageBuilderException {
        new TallyMessageBuilder(VoucherType.JOURNAL, null).withVoucherDate(new Date()).build();
    }

    @Test(expected = TallyMessageBuilderException.class)
    public void testTallyMessageBuilderWithEmptyBranch() throws TallyMessageBuilderException {
        new TallyMessageBuilder(VoucherType.JOURNAL, "").withVoucherDate(new Date()).build();
    }

    @Test(expected = TallyMessageBuilderException.class)
    public void testTallyMessageBuilderNegativeAmountCredit() throws TallyMessageBuilderException {
        AccountingDto voucherEntry = new AccountingDto("branch", "2010-04-20", "Payment", "4365", "GL CODE NAME", "-4",
                "-6");
        new TallyMessageBuilder(VoucherType.JOURNAL, "branch").withVoucherDate(new Date()).addCreditEntry(voucherEntry)
                .build();
    }

    @Test(expected = TallyMessageBuilderException.class)
    public void testTallyMessageBuilderNegativeAmountDebit() throws TallyMessageBuilderException {
        AccountingDto voucherEntry = new AccountingDto("branch", "2010-04-20", "Payment", "4365", "GL CODE NAME", "-4",
                "-6");
        new TallyMessageBuilder(VoucherType.JOURNAL, "branch").withVoucherDate(new Date()).addDebitEntry(voucherEntry)
                .build();
    }

    @Test
    public void testTallyMessageBuilder() throws TallyMessageBuilderException {
        AccountingDto voucherEntry = new AccountingDto("branch", "2010-04-20", "Payment", "4365", "GL CODE NAME", "4",
                "6");
        new TallyMessageBuilder(VoucherType.JOURNAL, "branch").withVoucherDate(new Date()).addDebitEntry(voucherEntry)
                .addCreditEntry(voucherEntry).build();
    }
}
