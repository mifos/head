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
		personnelMock = createMock(PersonnelBO.class);

		expectedException = new ServiceException("someServiceException");

		userId = 1;
		branchId = 2;
		localeId = 3;

		reportsDataService = new ReportsDataService();
		reportsDataService.setLoanPrdBusinessService(loanPrdBusinessServiceMock);
		reportsDataService.setPersonnelBusinessService(personnelBusinessServiceMock);
		reportsDataService.setOfficeBusinessService(officeBusinessServiceMock);
		reportsDataService.setPersonnel(personnelMock);
	}

	public void testInitialize() throws Exception {
		reportsDataService.setPersonnel(null);

		expect(personnelBusinessServiceMock.getPersonnel(userId.shortValue())).andReturn(personnelMock);
		replay(personnelBusinessServiceMock);
		reportsDataService.initialize(userId.intValue());
		assertSame(personnelMock, reportsDataService.getPersonnel());
		verify(personnelBusinessServiceMock);
	}

	public void testInitializeShouldComplainIfPersonnelBusinessServiceComplains() throws Exception {
		reportsDataService.setPersonnel(null);

		expect(personnelBusinessServiceMock.getPersonnel(userId.shortValue())).andThrow(expectedException);
		replay(personnelBusinessServiceMock);
		try {
			reportsDataService.initialize(userId.intValue());
			fail("exception expected");
		} catch (ServiceException e) {
			assertSame(expectedException, e);
		}
		assertNull(reportsDataService.getPersonnel());
		verify(personnelBusinessServiceMock);
	}

	public void testGetAllLoanProductsShouldDelegateToLoanPrdBusinessService() throws Exception {
		List<LoanOfferingBO> expectedLoanProducts = new ArrayList<LoanOfferingBO>();

		expect(personnelMock.getLocaleId()).andReturn(localeId);
		expect(loanPrdBusinessServiceMock.getAllLoanOfferings(localeId)).andReturn(expectedLoanProducts);
		replay(personnelBusinessServiceMock, personnelMock, loanPrdBusinessServiceMock);
		assertSame(expectedLoanProducts, reportsDataService.getAllLoanProducts());
		verify(personnelBusinessServiceMock, personnelMock, loanPrdBusinessServiceMock);
	}

	public void testGetAllLoanProductsShouldComplainIfLoanPrdBusinessServiceComplains() throws Exception {
		expect(personnelMock.getLocaleId()).andReturn(localeId);
		expect(loanPrdBusinessServiceMock.getAllLoanOfferings(localeId)).andThrow(expectedException);
		replay(personnelBusinessServiceMock, personnelMock, loanPrdBusinessServiceMock);
		try {
			reportsDataService.getAllLoanProducts();
			fail("exception expected");
		} catch (ServiceException e) {
			assertSame(expectedException, e);
		}
		verify(personnelBusinessServiceMock, personnelMock, loanPrdBusinessServiceMock);
	}

	public void testGetAllBranchShouldReturnActiveBranchesUnderUser() throws Exception {
		List<OfficeBO> expectedBranches = new ArrayList<OfficeBO>();

		expect(officeBusinessServiceMock.getActiveBranchesUnderUser(personnelMock)).andReturn(expectedBranches);
		replay(personnelBusinessServiceMock, personnelMock, officeBusinessServiceMock);
		assertSame(expectedBranches, reportsDataService.getActiveBranchesUnderUser());
		verify(personnelBusinessServiceMock, personnelMock, officeBusinessServiceMock);
	}

	public void testGetAllBranchShouldComplainIfOfficecBusinessServiceComplains() throws Exception {
		expect(officeBusinessServiceMock.getActiveBranchesUnderUser(personnelMock)).andThrow(expectedException);
		replay(personnelBusinessServiceMock, personnelMock, officeBusinessServiceMock);
		try {
			reportsDataService.getActiveBranchesUnderUser();
			fail("exception expected");
		} catch (ServiceException e) {
			assertSame(expectedException, e);
		}
		verify(personnelBusinessServiceMock, personnelMock, officeBusinessServiceMock);
	}

	public void testGetActiveLoanOfficersShouldReturnHimselfIfUserIsALoanOfficer() throws Exception {
		List<PersonnelBO> expectedLoanOfficer = new ArrayList<PersonnelBO>();
		expectedLoanOfficer.add(personnelMock);

		expect(personnelMock.isLoanOfficer()).andReturn(true);
		replay(personnelBusinessServiceMock, personnelMock);
		assertEquals(expectedLoanOfficer, reportsDataService.getActiveLoanOfficers(branchId.intValue()));
		verify(personnelBusinessServiceMock, personnelMock);
	}

	public void testGetActiveLoanOfficersShouldReturnAListOfActiveLoanOfficersUnderTheBranchIfUserIsNotALoanOfficer()
			throws Exception {
		List<PersonnelBO> expectedLoanOfficer = new ArrayList<PersonnelBO>();

		expect(personnelMock.isLoanOfficer()).andReturn(false);
		expect(personnelBusinessServiceMock.getActiveLoanOfficersUnderOffice(branchId)).andReturn(expectedLoanOfficer);
		replay(personnelBusinessServiceMock, personnelMock);
		assertEquals(expectedLoanOfficer, reportsDataService.getActiveLoanOfficers(branchId.intValue()));
		verify(personnelBusinessServiceMock, personnelMock);
	}

	public void testGetAllLoanOfficersShouldComplainIfPersonnelBusinessServiceFailedToGetLoanOfficers() throws Exception {
		expect(personnelMock.isLoanOfficer()).andReturn(false);
		expect(personnelBusinessServiceMock.getActiveLoanOfficersUnderOffice(branchId)).andThrow(expectedException);
		replay(personnelBusinessServiceMock, personnelMock);
		try {
			reportsDataService.getActiveLoanOfficers(branchId.intValue());
			fail("exception expected");
		} catch (ServiceException e) {
			assertSame(expectedException, e);
		}
		verify(personnelBusinessServiceMock, personnelMock);
	}

}
