package org.mifos.framework.components.cronjob;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.mifos.framework.MifosTestSuite;
import org.mifos.framework.components.cronjob.helpers.TestCustomerFeeHelper;
import org.mifos.framework.components.cronjob.helpers.TestDuplicateClientHelper;
import org.mifos.framework.components.cronjob.helpers.TestLoanArrearsHelper;
import org.mifos.framework.components.cronjob.helpers.TestLoanArrearsTask;
import org.mifos.framework.components.cronjob.helpers.TestRegenerateScheduleHelper;
import org.mifos.framework.components.cronjob.helpers.TestSavingsIntCalcHelper;
import org.mifos.framework.components.cronjob.helpers.TestSavingsIntPostingHelper;

public class CronjobTestSuite extends MifosTestSuite {

	public CronjobTestSuite() {
		super();
	}

	public static Test suite()throws Exception
	{
		TestSuite testSuite = new CronjobTestSuite();
		testSuite.addTestSuite(TestLoanArrearsTask.class);
		testSuite.addTestSuite(TestLoanArrearsHelper.class);
		testSuite.addTestSuite(TestDuplicateClientHelper.class);
		testSuite.addTestSuite(TestSavingsIntCalcHelper.class);
		testSuite.addTestSuite(TestSavingsIntPostingHelper.class);
		testSuite.addTestSuite(TestCustomerFeeHelper.class);
		testSuite.addTestSuite(TestRegenerateScheduleHelper.class);
		return testSuite;
	}
}
