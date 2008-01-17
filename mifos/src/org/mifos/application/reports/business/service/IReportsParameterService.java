package org.mifos.application.reports.business.service;

import java.util.Date;
import java.util.List;

import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.reports.ui.DateSelectionItem;
import org.mifos.application.reports.ui.SelectionItem;
import org.mifos.framework.exceptions.PersistenceException;


public interface IReportsParameterService {

	public abstract List<SelectionItem> getActiveBranchesUnderUser(
			String officeSearchId) throws PersistenceException;

	public abstract List<SelectionItem> getActiveLoanOfficersUnderOffice(
			Short branchId) throws PersistenceException;

	public abstract List<SelectionItem> getActiveCentersUnderUser(
			Short branchId, Short loanOfficerId) throws PersistenceException;

	public abstract List<DateSelectionItem> getMeetingDates(Short branchId,
			Short loanOfficerId, Integer centerId, CustomerLevel customerLevel, Date from)
			throws PersistenceException;

	public abstract List<SelectionItem> getActiveGroupsUnderUser(Short branchId,
			Short loanOfficerId) throws PersistenceException;
	
	public boolean invalidate();
}
