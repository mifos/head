package org.mifos.application.reports.business.service;

import java.util.Date;
import java.util.List;

import org.mifos.application.reports.ui.DateSelectionItem;
import org.mifos.application.reports.ui.SelectionItem;
import org.mifos.framework.exceptions.ServiceException;


public interface IReportsParameterService {

	public abstract List<SelectionItem> getActiveBranchesUnderUser(
			String officeSearchId) throws ServiceException;

	public abstract List<SelectionItem> getActiveLoanOfficersUnderOffice(
			Integer branchId) throws ServiceException;

	public abstract List<SelectionItem> getActiveCentersUnderUser(
			Integer branchId, Integer loanOfficerId) throws ServiceException;

	public abstract List<DateSelectionItem> getMeetingDates(Integer branchId,
			Integer loanOfficerId, Integer centerId, Date from)
			throws ServiceException;

	public boolean invalidate();
}
