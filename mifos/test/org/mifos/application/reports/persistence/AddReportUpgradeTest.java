package org.mifos.application.reports.persistence;

import java.sql.Connection;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.reports.business.ReportsBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class AddReportUpgradeTest extends MifosTestCase {
	private static final short ACTIVITY_ID = 1;
	private static final int HIGHER_UPGRADE_VERSION = 185;
	private static final short REPORT_CATEGORY_ID = (short) 6;
	private static final short TEST_REPORT_ID = (short) 1;
	private static final int LOWER_UPGRADE_VERSION = 184;
	private Session session;
	private Transaction transaction;
	private Connection connection;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		session = HibernateUtil.getSessionTL();
		connection = session.connection();
		transaction = session.beginTransaction();
	}

	public void testShouldNotThrowErrorWhenUpgradingForVer184WithActivityIdNull()
			throws Exception {
		AddReport addReport = createReport(LOWER_UPGRADE_VERSION);

		try {
			addReport.doUpgrade(connection);
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			fail("Should not throw error when inserting report:");
		}
	}

	private AddReport createReport(int version) {
		return new AddReport(version, TEST_REPORT_ID, REPORT_CATEGORY_ID,
				"TestReportForUpgrade", "test_report_upgrade", "design string",
				ACTIVITY_ID);
	}

	public void testShouldUpgradeForDBVersion185OrMoreWithAcivityId()
			throws Exception {
		AddReport addReport = createReport(HIGHER_UPGRADE_VERSION);
		addReport.doUpgrade(connection);
		ReportsBO report = new ReportsPersistence().getReport(TEST_REPORT_ID);
		assertNotNull(report.getActivityId());
		assertNotNull(report.getIsActive());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		transaction.rollback();
	}
}
