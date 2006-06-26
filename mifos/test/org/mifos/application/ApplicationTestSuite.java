package org.mifos.application;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.accounts.AccountTestSuite;
import org.mifos.application.accounts.financial.FinancialTestSuite;
import org.mifos.application.accounts.loan.LoanTestSuite;
import org.mifos.application.accounts.savings.SavingsTestSuite;
import org.mifos.application.bulkentry.BulkEntryTestSuite;
import org.mifos.application.collectionsheet.CollectionSheetTestSuite;
import org.mifos.application.configuration.LabelConfigurationTestSuite;
import org.mifos.application.customer.CustomerTestSuite;
import org.mifos.application.fees.FeeTestSuite;
import org.mifos.application.office.OfficeTestSuite;
import org.mifos.application.productdefinition.ProductDefinitionTestSuite;
import org.mifos.application.reports.ReportsTestSuite;
import org.mifos.framework.MifosTestSuite;
import org.mifos.framework.components.ComponentsTestSuite;
import org.mifos.framework.components.configuration.ConfigurationTestSuite;
import org.mifos.framework.components.cronjob.CronjobTestSuite;
import org.mifos.framework.components.fieldConfiguration.FieldConfigurationTestSuite;
import org.mifos.framework.struts.plugin.InitializerPluginTest;
import org.mifos.framework.struts.plugin.TestConstPlugin;
import org.mifos.framework.util.helpers.MoneyTest;
import org.mifos.framework.util.helpers.StringToMoneyConverterTest;

public class ApplicationTestSuite extends MifosTestSuite {

	public ApplicationTestSuite() throws Exception {
		super();
	}

	public static void main(String[] args) throws Exception {
		Test testSuite = suite();

		TestRunner.run(testSuite);
	}

	public static Test suite() throws Exception {
		TestSuite suite = new ApplicationTestSuite();
		suite.addTest(CollectionSheetTestSuite.suite());
		suite.addTest(CustomerTestSuite.suite());
		suite.addTest(BulkEntryTestSuite.suite());
		suite.addTest(AccountTestSuite.suite());
		suite.addTest(FinancialTestSuite.suite());
		suite.addTestSuite(MoneyTest.class);
		suite.addTestSuite(StringToMoneyConverterTest.class);
		suite.addTestSuite(InitializerPluginTest.class);
		suite.addTest(ConfigurationTestSuite.suite());
		suite.addTest(CronjobTestSuite.suite());
		suite.addTest(LabelConfigurationTestSuite.suite());
		suite.addTest(LoanTestSuite.suite());
		suite.addTest(SavingsTestSuite.suite());
		suite.addTest(ProductDefinitionTestSuite.suite());
		suite.addTest(ReportsTestSuite.suite());
		suite.addTestSuite(TestConstPlugin.class);
		suite.addTest(FeeTestSuite.suite());
		suite.addTest(FieldConfigurationTestSuite.suite());
		suite.addTest(OfficeTestSuite.suite());
		suite.addTest(ComponentsTestSuite.suite());
		return suite;
	}

}
