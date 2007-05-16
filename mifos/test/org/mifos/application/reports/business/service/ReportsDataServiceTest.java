package org.mifos.application.reports.business.service;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.framework.exceptions.ServiceException;

import junit.framework.TestCase;
import static org.easymock.classextension.EasyMock.*;

public class ReportsDataServiceTest extends TestCase {
	private LoanPrdBusinessService loanPrdBusinessServiceMock;

	private ReportsDataService reportsDataService;

	private Short localeId;

	private Short userId;

	private PersonnelBusinessService personnelBusinessServiceMock;

	private OfficeBusinessService officeBusinessServiceMock;

	private PersonnelBO personnelMock;

	private ServiceException expectedException;

	private Short branchId;

	@Override
	protected void setUp() throws Exception {
		loanPrdBusinessServiceMock = createMock(LoanPrdBusinessService.class);
		personnelBusinessServiceMock = createMock(PersonnelBusinessService.class);
		officeBusinessServiceMock = createMock(OfficeBusinessService.class);

		reportsDataService = ReportsDataService.getInstance();
		reportsDataService.setLoanPrdBusinessService(loanPrdBusinessServiceMock);
		reportsDataService.setPersonnelBusinessService(personnelBusinessServiceMock);
		reportsDataService.setOfficeBusinessService(officeBusinessServiceMock);

		personnelMock = createMock(PersonnelBO.class);

		expectedException = new ServiceException("someServiceException");

		userId = 1;
		localeId = 1;
		branchId = 1;
	}

	public void testGetAllLoanProductsShouldDelegateToLoanPrdBusinessService() throws Exception {
		List<LoanOfferingBO> expectedLoanProducts = new ArrayList<LoanOfferingBO>();

		expect(loanPrdBusinessServiceMock.getAllLoanOfferings(localeId)).andReturn(expectedLoanProducts);
		replay(loanPrdBusinessServiceMock);
		assertSame(expectedLoanProducts, reportsDataService.getAllLoanProducts(localeId));
		verify(loanPrdBusinessServiceMock);
	}

	public void testGetAllLoanProductsShouldComplainIfLoanPrdBusinessServiceComplains() throws Exception {
		expect(loanPrdBusinessServiceMock.getAllLoanOfferings(localeId)).andThrow(expectedException);
		replay(loanPrdBusinessServiceMock);
		try {
			reportsDataService.getAllLoanProducts(localeId);
			fail("exception expected");
		} catch (ServiceException e) {
			assertSame(expectedException, e);
		}
		verify(loanPrdBusinessServiceMock);
	}

	public void testGetAllBranchShouldReturnActiveBranchesUnderUser() throws Exception {
		List<OfficeBO> expectedBranches = new ArrayList<OfficeBO>();

		expect(personnelBusinessServiceMock.getPersonnel(userId)).andReturn(personnelMock);
		expect(officeBusinessServiceMock.getActiveBranchesUnderUser(personnelMock)).andReturn(expectedBranches);
		replay(personnelBusinessServiceMock, personnelMock, officeBusinessServiceMock);
		assertSame(expectedBranches, reportsDataService.getActiveBranchesUnderUser(userId));
		verify(personnelBusinessServiceMock, personnelMock, officeBusinessServiceMock);
	}

	public void testGetAllBranchShouldComplainIfPersonnelBusinessServiceComplains() throws Exception {
		expect(personnelBusinessServiceMock.getPersonnel(userId)).andThrow(expectedException);
		replay(personnelBusinessServiceMock, personnelMock, officeBusinessServiceMock);
		try {
			reportsDataService.getActiveBranchesUnderUser(userId);
			fail("exception expected");
		} catch (ServiceException e) {
			assertSame(expectedException, e);
		}
		verify(personnelBusinessServiceMock, personnelMock, officeBusinessServiceMock);
	}

	public void testGetAllBranchShouldComplainIfOfficecBusinessServiceComplains() throws Exception {
		expect(personnelBusinessServiceMock.getPersonnel(userId)).andReturn(personnelMock);
		expect(officeBusinessServiceMock.getActiveBranchesUnderUser(personnelMock)).andThrow(expectedException);
		replay(personnelBusinessServiceMock, personnelMock, officeBusinessServiceMock);
		try {
			reportsDataService.getActiveBranchesUnderUser(userId);
			fail("exception expected");
		} catch (ServiceException e) {
			assertSame(expectedException, e);
		}
		verify(personnelBusinessServiceMock, personnelMock, officeBusinessServiceMock);
	}

	public void testGetActiveLoanOfficersShouldReturnHimselfIfUserIsALoanOfficer() throws Exception {
		List<PersonnelBO> expectedLoanOfficer = new ArrayList<PersonnelBO>();
		expectedLoanOfficer.add(personnelMock);

		expect(personnelBusinessServiceMock.getPersonnel(userId)).andReturn(personnelMock);
		expect(personnelMock.isLoanOfficer()).andReturn(true);
		replay(personnelBusinessServiceMock, personnelMock);
		assertEquals(expectedLoanOfficer, reportsDataService.getActiveLoanOfficers(userId, branchId));
		verify(personnelBusinessServiceMock, personnelMock);
	}

	public void testGetActiveLoanOfficersShouldReturnAListOfActiveLoanOfficersUnderTheBranchIfUserIsNotALoanOfficer() throws Exception {
		List<PersonnelBO> expectedLoanOfficer = new ArrayList<PersonnelBO>();

		expect(personnelBusinessServiceMock.getPersonnel(userId)).andReturn(personnelMock);
		expect(personnelMock.isLoanOfficer()).andReturn(false);
		expect(personnelBusinessServiceMock.getActiveLoanOfficersUnderOffice(branchId)).andReturn(expectedLoanOfficer);
		replay(personnelBusinessServiceMock, personnelMock);
		assertEquals(expectedLoanOfficer, reportsDataService.getActiveLoanOfficers(userId, branchId));
		verify(personnelBusinessServiceMock, personnelMock);
	}

	public void testGetAllLoanOfficersShouldComplainIfPersonnelBusinessServiceFailedToGetPersonnel() throws Exception {
		expect(personnelBusinessServiceMock.getPersonnel(userId)).andThrow(expectedException);
		replay(personnelBusinessServiceMock, personnelMock);
		try {
			reportsDataService.getActiveLoanOfficers(userId, branchId);
			fail("exception expected");
		} catch (ServiceException e) {
			assertSame(expectedException, e);
		}
		verify(personnelBusinessServiceMock, personnelMock);
	}
	
	public void testGetAllLoanOfficersShouldComplainIfPersonnelBusinessServiceFailedToGetLoanOfficers() throws Exception {
		expect(personnelBusinessServiceMock.getPersonnel(userId)).andReturn(personnelMock);
		expect(personnelMock.isLoanOfficer()).andReturn(false);
		expect(personnelBusinessServiceMock.getActiveLoanOfficersUnderOffice(branchId)).andThrow(expectedException);
		replay(personnelBusinessServiceMock, personnelMock);
		try {
			reportsDataService.getActiveLoanOfficers(userId, branchId);
			fail("exception expected");
		} catch (ServiceException e) {
			assertSame(expectedException, e);
		}
		verify(personnelBusinessServiceMock, personnelMock);
	}

}
