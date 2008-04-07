package org.mifos.application.reports.persistence;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;
import org.mifos.application.reports.business.ReportsBO;
import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.reports.business.ReportsJasperMap;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.persistence.Upgrade;


public class AddReportTest {

	@Test
	public void startFromStandardStore() throws Exception {
		TestDatabase database = TestDatabase.makeStandard();
		String start = database.dumpForComparison();

		Upgrade upgrade = upgradeAndCheck(database);
		upgrade.downgrade(database.openConnection(), null);
		String afterUpAndDownGrade = database.dumpForComparison();

		assertEquals(start, afterUpAndDownGrade);
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
			"DetailedAgingPortfolioAtRisk.rptdesign"
			);
		upgrade.upgrade(database.openConnection(), null);
		ReportsBO fetched = (ReportsBO) 
			database.openSession().get(ReportsBO.class, newId);
		assertEquals(newId, fetched.getReportId());
		assertEquals(ReportsBO.ACTIVE, fetched.getIsActive());
		assertEquals(null, fetched.getActivityId());
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

}
