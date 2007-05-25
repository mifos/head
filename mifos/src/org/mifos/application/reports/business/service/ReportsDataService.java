package org.mifos.application.reports.business.service;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;

public class ReportsDataService {

	private LoanPrdBusinessService loanPrdBusinessService;

	private PersonnelBusinessService personnelBusinessService;

	private OfficeBusinessService officeBusinessService;

	private PersonnelBO personnel;

	private LoanPersistence loanPersistence;

	public ReportsDataService() {
		this.personnelBusinessService = new PersonnelBusinessService();
		this.officeBusinessService = new OfficeBusinessService();
		this.loanPrdBusinessService = new LoanPrdBusinessService();
		this.loanPersistence = new LoanPersistence();
	}

	public void initialize(Integer userId) throws ServiceException {
		this.personnel = personnelBusinessService.getPersonnel(convertIntegerToShort(userId));
	}

	public List<OfficeBO> getActiveBranchesUnderUser() throws ServiceException {
		return officeBusinessService.getActiveBranchesUnderUser(personnel);
	}

	public List<PersonnelBO> getActiveLoanOfficers(Integer branchId) throws ServiceException {
		List<PersonnelBO> loanOfficers = new ArrayList<PersonnelBO>();
		if (personnel.isLoanOfficer()) {
			loanOfficers.add(personnel);
		} else {
			loanOfficers = personnelBusinessService.getActiveLoanOfficersUnderOffice(convertIntegerToShort(branchId));
		}
		return loanOfficers;
	}

	public List<LoanOfferingBO> getAllLoanProducts() throws ServiceException {
		return loanPrdBusinessService.getAllLoanOfferings(personnel.getLocaleId());
	}

	public List<LoanBO> getLoanAccountsInActiveBadStanding(Integer branchId, Integer loanOfficerId, Integer loanProductId) throws PersistenceException {
		return loanPersistence.getLoanAccountsInActiveBadStanding(convertIntegerToShort(branchId), convertIntegerToShort(loanOfficerId), convertIntegerToShort(loanProductId));
	}
	
	void setLoanPrdBusinessService(LoanPrdBusinessService loanPrdBusinessService) {
		this.loanPrdBusinessService = loanPrdBusinessService;
	}

	void setPersonnelBusinessService(PersonnelBusinessService personnelBusinessService) {
		this.personnelBusinessService = personnelBusinessService;
	}

	void setOfficeBusinessService(OfficeBusinessService officeBusinessService) {
		this.officeBusinessService = officeBusinessService;
	}

	void setPersonnel(PersonnelBO personnel) {
		this.personnel = personnel;
	}

	PersonnelBO getPersonnel() {
		return personnel;
	}
	void setLoanPrdBusinessService(LoanPersistence loanPersistence) {
		this.loanPersistence = loanPersistence;
	}

	private Short convertIntegerToShort(Integer intValue) {
		if (intValue == null) {
			return null;
		}
		return intValue.shortValue();
	}

}
