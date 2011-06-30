package org.mifos.test.acceptance.admin;

import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.testhelpers.BatchJobHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups={"acceptance","ui", "batchjobs", "no_db_unit"})
public class BatchJobTest extends UiTestCaseBase{

    private NavigationHelper navigationHelper;
    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        navigationHelper = new NavigationHelper(selenium);
        super.setUp();
    }

    @Test(enabled = false)
    //http://mifosforge.jira.com/browse/MIFOSTEST-715
    public void testRunAllBatchJobs(){
        //When
        navigationHelper.navigateToAdminPage();
        //Then
        new BatchJobHelper(selenium).runAllBatchJobs();
    }
}
