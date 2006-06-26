package org.mifos.application.reports.persistence.service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.mifos.application.reports.business.ReportsBO;
import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.reports.persistence.service.ReportsPersistenceService;

public class TestReportsPersistenceService extends TestCase{
	
	private ReportsPersistenceService reportsPersistenceService;	
	
	public TestReportsPersistenceService(String testName){
		super(testName);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testGetAllReportCategories(){
		reportsPersistenceService = new ReportsPersistenceService();		
		List<ReportsCategoryBO> listOfReportCategories= reportsPersistenceService.getAllReportCategories();
		
		assertEquals(7,listOfReportCategories.size());		
		assertEquals("1",((ReportsCategoryBO)listOfReportCategories.get(0)).getReportCategoryId().toString());
		assertEquals("Client Detail",((ReportsCategoryBO)listOfReportCategories.get(0)).getReportCategoryName());
		assertEquals("2",((ReportsCategoryBO)listOfReportCategories.get(1)).getReportCategoryId().toString());
		assertEquals("Performance",((ReportsCategoryBO)listOfReportCategories.get(1)).getReportCategoryName());
		assertEquals("3",((ReportsCategoryBO)listOfReportCategories.get(2)).getReportCategoryId().toString());
		assertEquals("Kendra",((ReportsCategoryBO)listOfReportCategories.get(2)).getReportCategoryName());
		assertEquals("4",((ReportsCategoryBO)listOfReportCategories.get(3)).getReportCategoryId().toString());
		assertEquals("Loan Product Detail",((ReportsCategoryBO)listOfReportCategories.get(3)).getReportCategoryName());
		assertEquals("5",((ReportsCategoryBO)listOfReportCategories.get(4)).getReportCategoryId().toString());
		assertEquals("Status",((ReportsCategoryBO)listOfReportCategories.get(4)).getReportCategoryName());
		assertEquals("6",((ReportsCategoryBO)listOfReportCategories.get(5)).getReportCategoryId().toString());
		assertEquals("Analysis",((ReportsCategoryBO)listOfReportCategories.get(5)).getReportCategoryName());
		assertEquals("7",((ReportsCategoryBO)listOfReportCategories.get(6)).getReportCategoryId().toString());
		assertEquals("Miscellaneous",((ReportsCategoryBO)listOfReportCategories.get(6)).getReportCategoryName());	
		
	}
	
	public void testGetAllReportsForACategory(){
		reportsPersistenceService = new ReportsPersistenceService();		
		List<ReportsCategoryBO> listOfReportCategories= reportsPersistenceService.getAllReportCategories();
		Set<ReportsBO> reportsSet = ((ReportsCategoryBO)listOfReportCategories.get(0)).getReportsSet();		
		
		for (Iterator iter = reportsSet.iterator(); iter.hasNext();) {
			ReportsBO reports = (ReportsBO) iter.next();
			if(reports.getReportId().equals("1"))
				assertEquals("Client Detail",reports.getReportName());
			else if(reports.getReportId().equals("2"))
				assertEquals("Performance",reports.getReportName());
			else if(reports.getReportId().equals("3"))
				assertEquals("Kendra",reports.getReportName());
			else if(reports.getReportId().equals("4"))
				assertEquals("Loan Product Detail",reports.getReportName());
			else if(reports.getReportId().equals("5"))
				assertEquals("Status",reports.getReportName());
			else if(reports.getReportId().equals("6"))
				assertEquals("Analysis",reports.getReportName());			
			else if(reports.getReportId().equals("7"))
				assertEquals("Miscellaneous",reports.getReportName());	
			
		}
	}
	
	public void testGetReportPath(){
		reportsPersistenceService = new ReportsPersistenceService();		
		List<ReportsCategoryBO> listOfReportCategories= reportsPersistenceService.getAllReportCategories();
		Set<ReportsBO> reportsSet = ((ReportsCategoryBO)listOfReportCategories.get(0)).getReportsSet();		
		
		for (Iterator iter = reportsSet.iterator(); iter.hasNext();) {
			ReportsBO reports = (ReportsBO) iter.next();
			if(reports.getReportId().equals("1"))
				assertEquals("report_designer",reports.getReportIdentifier());			
		}
	}
	
	
}
