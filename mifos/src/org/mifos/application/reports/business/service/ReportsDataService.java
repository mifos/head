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

public class ReportsDataService {

	private LoanPrdBusinessService loanPrdBusinessService;

	private PersonnelBusinessService personnelBusinessService;

	private OfficeBusinessService officeBusinessService;

	private ReportsDataService() {
	}

	public static ReportsDataService getInstance() {
		ReportsDataService reportsDataService = new ReportsDataService();
		reportsDataService.setPersonnelBusinessService(new PersonnelBusinessService());
		reportsDataService.setOfficeBusinessService(new OfficeBusinessService());
		reportsDataService.setLoanPrdBusinessService(new LoanPrdBusinessService());
		return reportsDataService;
	}

	public List<LoanOfferingBO> getAllLoanProducts(Short localeId) throws ServiceException {
		return loanPrdBusinessService.getAllLoanOfferings(localeId);
	}

	public List<OfficeBO> getActiveBranchesUnderUser(Short userId) throws ServiceException {
		PersonnelBO personnel = personnelBusinessService.getPersonnel(userId);
		return officeBusinessService.getActiveBranchesUnderUser(personnel);
	}

	public void setLoanPrdBusinessService(LoanPrdBusinessService loanPrdBusinessService) {
		this.loanPrdBusinessService = loanPrdBusinessService;
	}

	public void setPersonnelBusinessService(PersonnelBusinessService personnelBusinessService) {
		this.personnelBusinessService = personnelBusinessService;
	}

	public void setOfficeBusinessService(OfficeBusinessService officeBusinessService) {
		this.officeBusinessService = officeBusinessService;
	}

	public List<PersonnelBO> getActiveLoanOfficers(Short userId, Short branchId) throws ServiceException {
		List<PersonnelBO> loanOfficers = new ArrayList<PersonnelBO>();
		PersonnelBO personnel = personnelBusinessService.getPersonnel(userId);
		if (personnel.isLoanOfficer()) {
			loanOfficers.add(personnel);
		} else {
			loanOfficers = personnelBusinessService.getActiveLoanOfficersUnderOffice(branchId);
		}
		return loanOfficers;
	}
	public Short switchIntegerIntoShort(Integer intValue) {
		if (intValue == null) {
			return null;
		}
		return intValue.shortValue();
	}
}
