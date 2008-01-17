package org.mifos.application.reports.business.service;

import java.util.Date;
import java.util.List;

import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.reports.persistence.SelectionItemPersistence;
import org.mifos.application.reports.ui.DateSelectionItem;
import org.mifos.application.reports.ui.SelectionItem;
import org.mifos.framework.exceptions.PersistenceException;

public class ReportsParameterService implements IReportsParameterService {

	private SelectionItemPersistence selectionItemPersistence;

	public ReportsParameterService(
			SelectionItemPersistence selectionItemPersistence) {
		super();
		this.selectionItemPersistence = selectionItemPersistence;
	}

	public ReportsParameterService() {
		selectionItemPersistence = new SelectionItemPersistence();
	}

	public List<SelectionItem> getActiveBranchesUnderUser(String officeSearchId)
			throws PersistenceException {
		return selectionItemPersistence
				.getActiveBranchesUnderUser(officeSearchId);
	}

	public List<SelectionItem> getActiveLoanOfficersUnderOffice(Short branchId)
			throws PersistenceException {
		return selectionItemPersistence
				.getActiveLoanOfficersUnderOffice(branchId);
	}

	public List<SelectionItem> getActiveCentersUnderUser(Short branchId,
			Short loanOfficerId) throws PersistenceException {
		return selectionItemPersistence.getActiveCentersUnderUser(branchId,
				loanOfficerId);
	}

	public List<DateSelectionItem> getMeetingDates(Short branchId,
			Short loanOfficerId, Integer centerId, CustomerLevel customerLevel,
			Date from) throws PersistenceException {
		return selectionItemPersistence.getMeetingDates(branchId,
				loanOfficerId, centerId, customerLevel.getValue(), from);
	}

	public List<SelectionItem> getActiveGroupsUnderUser(Short branchId,
			Short loanOfficerId) throws PersistenceException {
		return selectionItemPersistence.getActiveGroupsUnderUser(branchId,
				loanOfficerId);
	}

	// dummy method called to invalidate the cache
	public boolean invalidate() {
		return true;
	}
}
