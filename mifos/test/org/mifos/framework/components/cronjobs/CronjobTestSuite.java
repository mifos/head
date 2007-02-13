package org.mifos.framework.components.cronjobs;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.framework.components.cronjobs.helpers.CollectionSheetHelperTest;
import org.mifos.framework.components.cronjobs.helpers.TestApplyCustomerFeeChangesHelper;
import org.mifos.framework.components.cronjobs.helpers.TestCustomerFeeHelper;
import org.mifos.framework.components.cronjobs.helpers.TestGenerateMeetingsForCustomerAndSavingsHelper;
import org.mifos.framework.components.cronjobs.helpers.TestLoanArrearsAgingHelper;
import org.mifos.framework.components.cronjobs.helpers.TestLoanArrearsHelper;
import org.mifos.framework.components.cronjobs.helpers.TestLoanArrearsTask;
import org.mifos.framework.components.cronjobs.helpers.TestPortfolioAtRiskHelper;
import org.mifos.framework.components.cronjobs.helpers.TestProductStatusHelper;
import org.mifos.framework.components.cronjobs.helpers.TestRegenerateScheduleHelper;
import org.mifos.framework.components.cronjobs.helpers.TestSavingsIntCalcHelper;
import org.mifos.framework.components.cronjobs.helpers.TestSavingsIntPostingHelper;
import org.mifos.framework.components.cronjobs.persistence.TaskPersistenceTest;

public class CronjobTestSuite extends TestSuite {

	public CronjobTestSuite() {
		super();
	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new CronjobTestSuite();
		testSuite.addTestSuite(TestLoanArrearsTask.class);
		testSuite.addTestSuite(TestLoanArrearsHelper.class);
		testSuite.addTestSuite(TestSavingsIntCalcHelper.class);
		testSuite.addTestSuite(TestSavingsIntPostingHelper.class);
		testSuite.addTestSuite(TestCustomerFeeHelper.class);
		testSuite.addTestSuite(TestRegenerateScheduleHelper.class);
		testSuite.addTestSuite(TestApplyCustomerFeeChangesHelper.class);
		testSuite.addTestSuite(TestProductStatusHelper.class);
		testSuite.addTestSuite(TestLoanArrearsAgingHelper.class);
		testSuite.addTestSuite(TestPortfolioAtRiskHelper.class);
		testSuite.addTestSuite(MifosSchedulerTest.class);
		testSuite.addTestSuite(TaskPersistenceTest.class);
		testSuite.addTestSuite(TestGenerateMeetingsForCustomerAndSavingsHelper.class);
		testSuite.addTestSuite(CollectionSheetHelperTest.class);
		return testSuite;
	}
}
