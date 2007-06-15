package org.mifos.framework.components.batchjobs;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.framework.components.batchjobs.helpers.CollectionSheetHelperTest;
import org.mifos.framework.components.batchjobs.helpers.TestApplyCustomerFeeChangesHelper;
import org.mifos.framework.components.batchjobs.helpers.TestApplyHolidayChangesHelper;
import org.mifos.framework.components.batchjobs.helpers.TestCustomerFeeHelper;
import org.mifos.framework.components.batchjobs.helpers.TestGenerateMeetingsForCustomerAndSavingsHelper;
import org.mifos.framework.components.batchjobs.helpers.TestLoanArrearsAgingHelper;
import org.mifos.framework.components.batchjobs.helpers.TestLoanArrearsHelper;
import org.mifos.framework.components.batchjobs.helpers.TestLoanArrearsTask;
import org.mifos.framework.components.batchjobs.helpers.TestPortfolioAtRiskHelper;
import org.mifos.framework.components.batchjobs.helpers.TestProductStatusHelper;
import org.mifos.framework.components.batchjobs.helpers.TestRegenerateScheduleHelper;
import org.mifos.framework.components.batchjobs.helpers.TestSavingsIntCalcHelper;
import org.mifos.framework.components.batchjobs.helpers.TestSavingsIntPostingHelper;
import org.mifos.framework.components.batchjobs.persistence.TaskPersistenceTest;

public class BatchJobTestSuite extends TestSuite {

	public BatchJobTestSuite() {
		super();
	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new BatchJobTestSuite();
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
		testSuite.addTestSuite(TestApplyHolidayChangesHelper.class);
		return testSuite;
	}
}
