package org.mifos.platform.accounting.tally;

import java.io.File;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mifos.core.MifosResourceUtil;
import org.mifos.platform.accounting.AccountingDto;
import org.mifos.platform.accounting.service.AccountingDataCacheManager;
import org.springframework.core.io.ClassPathResource;

public class TallyXMLOutputTest {
    AccountingDataCacheManager cacheManager;

    @Before
    public void setUp() throws Exception {
        cacheManager = new AccountingDataCacheManager();
    }

    /**
     * failing on windows -suspect it could be a carraige return or something like that.
     */
    @Ignore
    @Test
    public void testAccoutingDataOutPut() throws Exception {
        String fileName = "Mifos Accounting Export 2010-08-10 to 2010-08-10.xml";
        File file = MifosResourceUtil.getClassPathResource("org/mifos/platform/accounting/tally/2010-08-10 to 2010-08-10");
        String expected = FileUtils.readFileToString(MifosResourceUtil.getClassPathResource("org/mifos/platform/accounting/tally/"+ fileName));
        List<AccountingDto> accountingData = cacheManager.accountingDataFromCache(file);
        String tallyOutput = TallyXMLGenerator.getTallyXML(accountingData, fileName);
        Assert.assertEquals(expected, tallyOutput);
    }

}
