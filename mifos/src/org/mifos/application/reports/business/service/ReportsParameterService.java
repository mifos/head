package org.mifos.application.reports.business.service;

import java.util.Date;
import java.util.List;

import org.mifos.application.reports.persistence.SelectionItemPersistence;
import org.mifos.application.reports.ui.DateSelectionItem;
import org.mifos.application.reports.ui.SelectionItem;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;

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
			throws ServiceException {
		try {
			return selectionItemPersistence
					.getActiveBranchesUnderUser(officeSearchId);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<SelectionItem> getActiveLoanOfficersUnderOffice(Short branchId)
			throws ServiceException {
		try {
			return selectionItemPersistence
					.getActiveLoanOfficersUnderOffice(branchId);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<SelectionItem> getActiveCentersUnderUser(Short branchId,
			Short loanOfficerId) throws ServiceException {
		try {
			return selectionItemPersistence.getActiveCentersUnderUser(branchId,
					loanOfficerId);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<DateSelectionItem> getMeetingDates(Short branchId,
			Short loanOfficerId, Integer centerId, Date from) throws ServiceException {
		try {
			return selectionItemPersistence.getMeetingDates(branchId,
					loanOfficerId, centerId, from);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	// dummy method called to invalidate the cache
	public boolean invalidate() {
		return true;
	}
}
