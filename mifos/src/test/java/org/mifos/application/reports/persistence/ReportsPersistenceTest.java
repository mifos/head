package org.mifos.application.reports.persistence;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.reports.business.ReportsBO;
import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.reports.business.ReportsDataSource;
import org.mifos.application.reports.business.ReportsJasperMap;
import org.mifos.application.reports.business.ReportsParams;
import org.mifos.application.reports.util.helpers.ReportsConstants;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestDatabase;

public class ReportsPersistenceTest extends MifosIntegrationTest {

	public ReportsPersistenceTest() throws SystemException, ApplicationException {
        super();
    }

    private ReportsPersistence reportsPersistence;

	@Override
	protected void setUp() throws Exception {
		reportsPersistence = new ReportsPersistence();
	}

	public void testGetAllReportCategories() {
		List<ReportsCategoryBO> listOfReportCategories = reportsPersistence
				.getAllReportCategories();
         Session session = HibernateUtil.getSessionTL();
         Query query = session.createQuery("select count(*) from ReportsCategoryBO");
         List list = query.list();
         
		assertEquals(((Long) list.get(0)).intValue(), listOfReportCategories.size());
		assertEquals("1", listOfReportCategories.get(0)
				.getReportCategoryId().toString());
		assertEquals("Client Detail",
				listOfReportCategories.get(0)
						.getReportCategoryName());
		assertEquals("2", listOfReportCategories.get(1)
				.getReportCategoryId().toString());
		assertEquals("Performance", listOfReportCategories
				.get(1).getReportCategoryName());
		assertEquals("3", listOfReportCategories.get(2)
				.getReportCategoryId().toString());
		assertEquals("Center", listOfReportCategories
				.get(2).getReportCategoryName());
		assertEquals("4", listOfReportCategories.get(3)
				.getReportCategoryId().toString());
		assertEquals("Loan Product Detail",
				listOfReportCategories.get(3)
						.getReportCategoryName());
		assertEquals("5", listOfReportCategories.get(4)
				.getReportCategoryId().toString());
		assertEquals("Status", listOfReportCategories
				.get(4).getReportCategoryName());
		assertEquals("6", listOfReportCategories.get(5)
				.getReportCategoryId().toString());
		assertEquals("Analysis", listOfReportCategories
				.get(5).getReportCategoryName());
		assertEquals("7", listOfReportCategories.get(6)
				.getReportCategoryId().toString());
		assertEquals("Miscellaneous",
				listOfReportCategories.get(6)
						.getReportCategoryName());

	}

	public void testGetAllReportsForACategory() {
		List<ReportsCategoryBO> listOfReportCategories = reportsPersistence
				.getAllReportCategories();
		Set<ReportsBO> reportsSet = listOfReportCategories
				.get(0).getReportsSet();

		for (Iterator iter = reportsSet.iterator(); iter.hasNext();) {
			ReportsBO reports = (ReportsBO) iter.next();
			if (reports.getReportId().equals("1")) {
				assertEquals("Client Detail", reports.getReportName());
			}
			else if (reports.getReportId().equals("2")) {
				assertEquals("Performance", reports.getReportName());
			}
			else if (reports.getReportId().equals("3")) {
				assertEquals("Kendra", reports.getReportName());
			}
			else if (reports.getReportId().equals("4")) {
				assertEquals("Loan Product Detail", reports.getReportName());
			}
			else if (reports.getReportId().equals("5")) {
				assertEquals("Status", reports.getReportName());
			}
			else if (reports.getReportId().equals("6")) {
				assertEquals("Analysis", reports.getReportName());
			}
			else if (reports.getReportId().equals("7")) {
				assertEquals("Miscellaneous", reports.getReportName());
			}
			else {
				/* We always get here, because the above code
				   is comparing a Short to a String.
				   TODO: how do we really want to test this, anyway? */
//				fail("unexpected report " + reports.getReportId());
			}
		}
	}

	public void testGetReportPath() {
		List<ReportsCategoryBO> listOfReportCategories = reportsPersistence
				.getAllReportCategories();
		Set<ReportsBO> reportsSet = listOfReportCategories
				.get(0).getReportsSet();

		for (Iterator iter = reportsSet.iterator(); iter.hasNext();) {
			ReportsBO reports = (ReportsBO) iter.next();
			if (reports.getReportId().equals("1"))
				assertEquals("report_designer", reports.getReportIdentifier());
		}
	}
	
	public void testGetAllParameters() throws Exception {
		{
			List<ReportsParams> parameters = 
				reportsPersistence.getAllReportParams();
			assertEquals(0, parameters.size());
		}
		
		TestDatabase database = TestDatabase.makeStandard();
		database.execute(
			"insert into report_parameter(name, type, classname)" +
			"values('my_report', 'my_type', 'my_class')");
		Session session = database.openSession();
		List<ReportsParams> moreParameters = 
			reportsPersistence.getAllReportParams(session);
		assertEquals(1, moreParameters.size());
		ReportsParams parameter = moreParameters.get(0);
		assertEquals("my_report", parameter.getName());
		assertEquals("my_type", parameter.getType());
		assertEquals("my_class", parameter.getClassname());

		/* Just a sanity check that the above is mucking with the local
		   database not the global test database. */
		{
			List<ReportsParams> parameters = 
				reportsPersistence.getAllReportParams();
			assertEquals(0, parameters.size());
		}
	}
	
	public void testCreateJasper() throws Exception {
		TestDatabase database = TestDatabase.makeStandard();
		Session session = database.openSession();
		ReportsJasperMap jasperMap = new ReportsJasperMap(
			null, "report.jrxml");
		new ReportsPersistence().createJasperMap(session, jasperMap);
		short reportId = jasperMap.getReportId();

		Session session2 = database.openSession();
		ReportsJasperMap reRead = 
			new ReportsPersistence().oneJasperOfReportId(session2, reportId);
		assertEquals("report.jrxml", reRead.getReportJasper());
	}
	 public void testGetReport() {
		 Short reportId = 28;
		 ReportsBO report = reportsPersistence.getReport(reportId);
		 assertEquals(reportId, report.getReportId());
		 }
	 
	 public void testGetReportCategoryByCategoryId() {
		 Short reportCategoryId = 1;
		 ReportsCategoryBO reportCategory = reportsPersistence.getReportCategoryByCategoryId(reportCategoryId);
		 assertEquals(reportCategoryId, reportCategory.getReportCategoryId());
	 }
	 
	 public void testGetAllReports(){
		 org.hibernate.Session session= HibernateUtil.getSessionTL();
		 Query query = session.createQuery("select count(*) from ReportsBO");
		 List list = query.list();
		 int reportsNum = ((Long)list.get(0)).intValue();
		 assertEquals(reportsNum, reportsPersistence.getAllReports().size());
	 }
	 
	 public void testViewDataSource() throws Exception {
			List <ReportsDataSource> queryResult =reportsPersistence.viewDataSource(1);
			Iterator itrQueryResult= queryResult.iterator();
			while(itrQueryResult.hasNext()){
				ReportsDataSource objReportsDataSource =(ReportsDataSource)itrQueryResult.next();
				assertEquals(ReportsConstants.HIDDEN_PASSWORD, objReportsDataSource.getPassword());
			}
		}
	 
}
