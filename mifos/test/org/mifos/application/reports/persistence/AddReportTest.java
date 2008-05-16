package org.mifos.application.reports.persistence;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.SQLException;

import junit.framework.JUnit4TestAdapter;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.application.accounts.business.AddAccountStateFlagTest;
import org.mifos.application.reports.business.ReportsBO;
import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.reports.business.ReportsJasperMap;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.persistence.Upgrade;
import org.mifos.framework.security.activity.ActivityGenerator;
import org.mifos.framework.util.helpers.TestCaseInitializer;


public class AddReportTest {
	
	@BeforeClass
	public static void init() {
		new TestCaseInitializer();
	}
	
	@Test
	public void startFromStandardStore() throws Exception {
		TestDatabase database = TestDatabase.makeStandard();
		upgradeAndCheck(database);
	}

	private Upgrade upgradeAndCheck(TestDatabase database) 
	throws IOException, SQLException, ApplicationException {
		short newId = 17032;
		AddReport upgrade = new AddReport(
			DatabaseVersionPersistence.APPLICATION_VERSION + 1,
			newId,
			ReportsCategoryBO.ANALYSIS,
			"Detailed Aging of Portfolio at Risk",
			"aging_portfolio_at_risk",
			"DetailedAgingPortfolioAtRisk.rptdesign", (short)1
			);
		upgrade.upgrade(database.openConnection(), null);
		ReportsBO fetched = (ReportsBO) 
			database.openSession().get(ReportsBO.class, newId);
		assertEquals(newId, fetched.getReportId());
		assertEquals(ReportsBO.ACTIVE, fetched.getIsActive());
		assertEquals((short)1, fetched.getActivityId());
		assertEquals("Detailed Aging of Portfolio at Risk", 
			fetched.getReportName());
		assertEquals("aging_portfolio_at_risk", 
			fetched.getReportIdentifier());
		assertEquals(ReportsCategoryBO.ANALYSIS, 
			fetched.getReportsCategoryBO().getReportCategoryId());
		
		ReportsJasperMap map = fetched.getReportsJasperMap();
		assertEquals("DetailedAgingPortfolioAtRisk.rptdesign", 
			map.getReportJasper());
		
		return upgrade;
	}
	
	public static junit.framework.Test testSuite() {
		return new JUnit4TestAdapter(AddReportTest.class);
	}	

}
