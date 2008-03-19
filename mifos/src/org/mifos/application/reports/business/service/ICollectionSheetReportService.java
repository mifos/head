package org.mifos.application.reports.business.service;

import java.util.Date;
import java.util.List;

import org.mifos.application.reports.business.dto.CollectionSheetReportDTO;
import org.mifos.application.reports.ui.DateSelectionItem;
import org.mifos.application.reports.ui.SelectionItem;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;


public interface ICollectionSheetReportService {

	public List<SelectionItem> getBranchOffices(Integer userId)
			throws ServiceException, PersistenceException;

	public List<SelectionItem> getActiveLoanOfficers(Integer userId,
			Integer branchIdIntValue) throws ServiceException,
			PersistenceException;

	public List<SelectionItem> getActiveCentersForLoanOfficer(
			Integer loanOfficerIdIntValue, Integer branchIdIntValue)
			throws ServiceException, PersistenceException;

	public List<CollectionSheetReportDTO> getCollectionSheets(
			Integer branchId, Integer officerId, Integer centerId,
			Date meetingDate) throws Exception;

	public List<DateSelectionItem> getMeetingDatesForCenter(Integer branchId,
			Integer groupId, Integer officerId) throws PersistenceException,
			ServiceException;

	public void invalidateCachedReportParameters();

}
